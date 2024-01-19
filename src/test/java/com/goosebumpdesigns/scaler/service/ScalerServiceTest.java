// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.goosebumpdesigns.scaler.model.Dimension;
import com.goosebumpdesigns.scaler.model.Dimensions;
import com.goosebumpdesigns.scaler.model.Measurement;
import com.goosebumpdesigns.scaler.model.Scale;
import com.goosebumpdesigns.scaler.model.ScalerData;

/**
 * 
 */
class ScalerServiceTest {
  private ScalerService scalerService = new ScalerService();

  /**
   * 
   */
  @Test
  void assertThatNullScalarDataThrowsException() {
    // Given: a null scaler data object
    ScalerData data = null;

    // When: the missing fields are calculated
    // Then: an exception is thrown
    assertThatThrownBy(() -> scalerService.supplyMissingFields(data))
        .isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * 
   */
  @Test
  void assertThatMissingScaleThrowsException() {
    // Given: a scaler data object without scale
    Dimension fullsizeWidth = new Dimension(new BigDecimal("5.0"), Measurement.FOOT);

    ScalerData data = ScalerData
        .builder() // @formatter:off
        .scale(null)
        .outputMeasurement(Measurement.CM)
        .fullsizeDimensions(new Dimensions(fullsizeWidth, null, null))
        .build(); // @formatter:on

    // When: the missing fields are calculated
    // Then: an exception is thrown
    assertThatThrownBy(() -> scalerService.supplyMissingFields(data))
        .isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * 
   */
  @Test
  void assertThatMissingOutputMeasurementThrowsException() {
    // Given: a scaler data object without measurement
    Dimension fullsizeWidth = new Dimension(new BigDecimal("5.0"), Measurement.FOOT);

    ScalerData data = ScalerData
        .builder() // @formatter:off
        .scale(Scale.HO)
        .outputMeasurement(null)
        .fullsizeDimensions(new Dimensions(fullsizeWidth, null, null))
        .build(); // @formatter:on

    // When: the missing fields are calculated
    // Then: an exception is thrown
    assertThatThrownBy(() -> scalerService.supplyMissingFields(data))
        .isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * 
   */
  @Test
  void assertThatSupplyingBothRealAndScaleMeasurementsThrowsException() {
    // Given: a scaler data object with real and scale measurements
    Dimension fullsizeWidth = new Dimension(new BigDecimal("5.0"), Measurement.FOOT);
    Dimension modelWidth = new Dimension(new BigDecimal("50.5"), Measurement.INCH);

    ScalerData data = ScalerData
        .builder() // @formatter:off
        .scale(Scale.HO)
        .outputMeasurement(Measurement.CM)
        .fullsizeDimensions(new Dimensions(fullsizeWidth, null, null))
        .modelDimensions(new Dimensions(modelWidth, null, null))
        .build(); // @formatter:on

    // When: the missing fields are calculated
    // Then: an exception is thrown
    assertThatThrownBy(() -> scalerService.supplyMissingFields(data))
        .isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * 
   */
  @Test
  void assertThatSupplyingNeitherRealOrScaleMeasurementsThrowsException() {
    // Given: a scaler data object with real and scale measurements
    ScalerData data = ScalerData
        .builder() // @formatter:off
        .scale(Scale.HO)
        .outputMeasurement(Measurement.CM)
        .build(); // @formatter:on

    // When: the missing fields are calculated
    // Then: an exception is thrown
    assertThatThrownBy(() -> scalerService.supplyMissingFields(data))
        .isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * 
   */
  @Test
  void assertThatScaleMeasurementIsCalculatedCorrectlyGivenRealMeasurement() {
    // Given: a scaler data object with real measurements
    Scale scale = Scale.HO;
    Measurement outputMeasurement = Measurement.CM;

    Dimension fullsizeWidth = new Dimension(new BigDecimal("12.50"), Measurement.FOOT);
    Dimension fullsizeLength = new Dimension(new BigDecimal("40.00"), Measurement.FOOT);
    Dimension fullsizeHeight = new Dimension(new BigDecimal("147.00"), Measurement.INCH);
    Dimensions fullsizeDimensions = new Dimensions(fullsizeLength, fullsizeWidth, fullsizeHeight);

    Dimension modelWidth = new Dimension(new BigDecimal("4.37"), Measurement.CM);
    Dimension modelLength = new Dimension(new BigDecimal("14.00"), Measurement.CM);
    Dimension modelHeight = new Dimension(new BigDecimal("4.29"), Measurement.CM);
    Dimensions modelDimensions = new Dimensions(modelLength, modelWidth, modelHeight);

    ScalerData expected = ScalerData
        .builder() // @formatter:off
        .scale(scale)
        .outputMeasurement(outputMeasurement)
        .fullsizeDimensions(fullsizeDimensions)
        .modelDimensions(modelDimensions)
        .build(); // @formatter:on

    ScalerData data = ScalerData
        .builder() // @formatter:off
        .scale(scale)
        .outputMeasurement(outputMeasurement)
        .fullsizeDimensions(fullsizeDimensions)
        .build(); // @formatter:on

    // When: the missing fields are calculated
    ScalerData actual = scalerService.supplyMissingFields(data);

    // Then: the fields are calculated correctly
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * 
   */
  @Test
  void assertThatRealMeasurementIsCalculatedCorrectlyGivenScaleMeasurement() {
    // Given: a scaler data object with scale measurements
    Scale scale = Scale.HO;
    Measurement outputMeasurement = Measurement.FOOT;

    Dimension modelWidth = new Dimension(new BigDecimal("4.23"), Measurement.CM);
    Dimension modelLength = new Dimension(new BigDecimal("18.75"), Measurement.CM);
    Dimension modelHeight = new Dimension(new BigDecimal("27.50"), Measurement.MM);
    Dimensions modelDimensions = new Dimensions(modelLength, modelWidth, modelHeight);

    Dimension fullsizeWidth = new Dimension(new BigDecimal("12.09"), Measurement.FOOT);
    Dimension fullsizeLength = new Dimension(new BigDecimal("53.58"), Measurement.FOOT);
    Dimension fullsizeHeight = new Dimension(new BigDecimal("7.86"), Measurement.FOOT);
    Dimensions fullsizeDimensions =
        new Dimensions(fullsizeLength, fullsizeWidth, fullsizeHeight);

    ScalerData expected = ScalerData
        .builder() // @formatter:off
        .scale(scale)
        .outputMeasurement(outputMeasurement)
        .fullsizeDimensions(fullsizeDimensions)
        .modelDimensions(modelDimensions)
        .build(); // @formatter:on

    ScalerData data = ScalerData
        .builder() // @formatter:off
        .scale(scale)
        .outputMeasurement(outputMeasurement)
        .modelDimensions(modelDimensions)
        .build(); // @formatter:on

    // When: the missing fields are calculated
    ScalerData actual = scalerService.supplyMissingFields(data);

    // Then: the fields are calculated correctly
    assertThat(actual).isEqualTo(expected);
  }
}
