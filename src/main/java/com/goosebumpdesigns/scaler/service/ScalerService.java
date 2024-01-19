// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.service;

import static com.goosebumpdesigns.scaler.model.ScalerConstants.INTERMEDIATE_SCALE;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import com.goosebumpdesigns.scaler.model.Dimension;
import com.goosebumpdesigns.scaler.model.Dimensions;
import com.goosebumpdesigns.scaler.model.Measurement;
import com.goosebumpdesigns.scaler.model.ScalerData;

/**
 * This service takes input dimensions and generates scaled output dimensions. Since each dimension
 * may have a different {@link Measurement measurement} type, the input dimensions are converted to
 * millimeters prior to scaling them. Once scaled they are converted to the output measurement.
 */
@Service
public class ScalerService {
  /** Shortcut for BiFunction that takes a BigDecimal type. */
  private interface MathOp extends BiFunction<BigDecimal, BigDecimal, BigDecimal> {
  }

  /** Shortcut for Function that takes a BigDecimal type. */
  private interface ConversionOp extends Function<BigDecimal, BigDecimal> {
  }

  private static final BigDecimal MILLIMETERS_PER_CENTIMETER = BigDecimal.TEN;
  private static final BigDecimal MILLIMETERS_PER_INCH = new BigDecimal("25.40");
  private static final BigDecimal MILLIMETERS_PER_FOOT = new BigDecimal("304.80");

  /** These functions convert from the input measurement to millimeters. */
  private ConversionOp cmToMm = cm -> cm.multiply(MILLIMETERS_PER_CENTIMETER);
  private ConversionOp inToMm = in -> in.multiply(MILLIMETERS_PER_INCH);
  private ConversionOp ftToMm = ft -> ft.multiply(MILLIMETERS_PER_FOOT);

  /** These functions convert from millimeters to the output measurement. */
  private ConversionOp mmToCm = mm -> divide(mm, MILLIMETERS_PER_CENTIMETER);
  private ConversionOp mmToIn = mm -> divide(mm, MILLIMETERS_PER_INCH);
  private ConversionOp mmToFt = mm -> divide(mm, MILLIMETERS_PER_FOOT);

  /** These functions are used in the scale() method to perform the scaling operation. */
  private MathOp multByScale = (value, scale) -> value.multiply(scale);
  private MathOp divByScale = (value, scale) -> divide(value, scale);

  /**
   * This method validates the input {@link ScalerData} object. It then calculates the missing
   * measurement fields. If full size dimensions are supplied, model dimensions are calculated. If
   * model dimensions are supplied, full size dimensions are calculated.
   * 
   * @param data The input dimensions.
   * @return The completely populated object.
   * @throws IllegalArgumentException Thrown if neither model nor full size dimensions are provided,
   *         or if both are provided. This is also thrown if other fields in the input data are
   *         {@code null}.
   */
  public ScalerData supplyMissingFields(ScalerData data) {
    validateInputData(data);

    Dimensions fullsizeDimensions;
    Dimensions modelDimensions;

    if(hasFullsizeDimensions(data)) {
      fullsizeDimensions = data.getFullsizeDimensions();

      modelDimensions = scaleDimensions(data.getOutputMeasurement(), data.getScale().getFactor(),
          fullsizeDimensions, divByScale);
    }
    else {
      modelDimensions = data.getModelDimensions();

      fullsizeDimensions = scaleDimensions(data.getOutputMeasurement(), data.getScale().getFactor(),
          modelDimensions, multByScale);
    }

    // @formatter:off
    return ScalerData.builder()
        .scale(data.getScale())
        .outputMeasurement(data.getOutputMeasurement())
        .modelDimensions(modelDimensions)
        .fullsizeDimensions(fullsizeDimensions)
        .build();
    // @formatter:on
  }

  /**
   * @param data The input data.
   * @return {@code true} if the input data has full size dimensions.
   */
  private boolean hasFullsizeDimensions(ScalerData data) {
    return hasValue(data.getFullsizeDimensions());
  }

  /**
   * @param data The input data.
   * @return {@code true} if the input data has model dimensions.
   */
  private boolean hasModelDimensions(ScalerData data) {
    return hasValue(data.getModelDimensions());
  }

  /**
   * Validate the input data. There must be one and only one set of dimensions.
   * 
   * @param data The data to validate.
   * @throws IllegalArgumentException Thrown if a data element is missing or is invalid.
   */
  private void validateInputData(ScalerData data) {
    requireNonNull(data, "The input object must not be null.");
    requireNonNull(data.getScale(), "Scale must not be null.");
    requireNonNull(data.getOutputMeasurement(), "Output measurement must not be null.");

    boolean hasPrototype = hasFullsizeDimensions(data);
    boolean hasModel = hasModelDimensions(data);

    if(hasPrototype && hasModel) {
      throw new IllegalArgumentException(
          "Value to calculate has both full size and model dimensions.");
    }

    if(!hasPrototype && !hasModel) {
      throw new IllegalArgumentException("Must supply either full size or model dimensions.");
    }
  }

  /**
   * If the given object is {@code null}, this method throws an exception.
   * 
   * @param obj The object to test.
   * @param message The message to supply to the exception, if thrown.
   * @throws IllegalArgumentException Thrown if the required object is {@code null}.
   */
  private void requireNonNull(Object obj, String message) {
    if(Objects.isNull(obj)) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Multiply the length, width, and height of the input dimensions by the scale factor and return
   * the scaled results.
   * 
   * @param outputMeasurement The output measurement.
   * @param factor The scale factor (i.e., HO => 1:87.1).
   * @param inputDimensions The given dimensions to scale.
   * @param mathOp The math operation to perform.
   * @return The scaled dimensions.
   */
  private Dimensions scaleDimensions(Measurement outputMeasurement, BigDecimal factor,
      Dimensions inputDimensions, MathOp mathOp) {
    Dimension length = scaleDimension(factor, outputMeasurement, inputDimensions.length(), mathOp);
    Dimension width = scaleDimension(factor, outputMeasurement, inputDimensions.width(), mathOp);
    Dimension height = scaleDimension(factor, outputMeasurement, inputDimensions.height(), mathOp);

    return new Dimensions(length, width, height);
  }

  /**
   * Calculate the scaled dimension.
   * 
   * @param scaleFactor The scale factor (i.e., N scale => 1:160).
   * @param outputMeasurement The measurement system (inches, cm, etc.) to which to convert the
   *        output dimension.
   * @param dimension The dimension to scale.
   * @return The scaled dimension.
   */
  private Dimension scaleDimension(BigDecimal scaleFactor, Measurement outputMeasurement,
      Dimension dimension, BiFunction<BigDecimal, BigDecimal, BigDecimal> op) {
    if(Objects.nonNull(dimension)) {
      BigDecimal inputMillis = toMillis(dimension);
      BigDecimal scaledMillis = op.apply(inputMillis, scaleFactor);

      return toOutputDimension(scaledMillis, outputMeasurement);
    }

    return null;
  }

  /**
   * Convert a millimeter length to the correct output measurement (i.e., mm => ft, mm => in, etc.).
   * 
   * @param scaledMillis The scaled length in millimeters.
   * @param outputMeasurement The measurement type to which to convert the input length.
   * @return The length in the output measurement.
   */
  private Dimension toOutputDimension(BigDecimal scaledMillis, Measurement outputMeasurement) {
    BigDecimal length = switch(outputMeasurement) {
      case CM -> mmToCm.apply(scaledMillis);
      case FOOT -> mmToFt.apply(scaledMillis);
      case INCH -> mmToIn.apply(scaledMillis);
      case MM -> scaledMillis;
    };

    return new Dimension(length, outputMeasurement);
  }

  /**
   * Convert the given length to millimeters.
   * 
   * @param dimension The dimension to convert.
   * @return The length in millimeters.
   */
  private BigDecimal toMillis(Dimension dimension) {
    BigDecimal value = dimension.value();

    return switch(dimension.measurement()) {
      case CM -> cmToMm.apply(value);
      case FOOT -> ftToMm.apply(value);
      case INCH -> inToMm.apply(value);
      case MM -> value;
    };
  }

  /**
   * Divide the dividend by the divisor and return the result. The result will be scaled to the
   * required number of decimal places, rounding if necessary.
   * 
   * @param dividend The number to divide.
   * @param divisor The number to divide by.
   * @return The division result.
   */
  private BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
    return dividend.divide(divisor, INTERMEDIATE_SCALE, RoundingMode.HALF_UP);
  }

  /**
   * @param dimensions The dimensions (length, width, and height) to examine.
   * @return {@code true} if there is a length, width or height.
   */
  private boolean hasValue(Dimensions dimensions) {
    if(Objects.isNull(dimensions)) {
      return false;
    }

    return Objects.nonNull(dimensions.length()) || Objects.nonNull(dimensions.width())
        || Objects.nonNull(dimensions.height());
  }
}
