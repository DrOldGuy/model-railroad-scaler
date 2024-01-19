// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.model;

import static com.goosebumpdesigns.scaler.model.ScalerConstants.OUTPUT_SCALE;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This class associates measurement type with a value. Each dimension (length, width or height) can
 * have a separate measurement type.
 */
@JsonInclude(Include.NON_NULL)
public record Dimension(BigDecimal value, Measurement measurement) {
  /**
   * Custom constructor that validates and rounds the value.
   * 
   * @param value The value
   * @param measurement The measurement type associated with the value.
   * @throws NullPointerException Thrown if either the value or the measurement is {@code null}.
   */
  public Dimension(BigDecimal value, Measurement measurement) {
    Objects.requireNonNull(value, "Size must not be null.");
    Objects.requireNonNull(measurement, "Measurement must not be null.");

    this.value = value.setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);
    this.measurement = measurement;
  }

  /**
   * 
   */
  @Override
  public String toString() {
    return value.toString() + " " + measurement;
  }
}
