// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.goosebumpdesigns.scaler.model.Dimension;
import com.goosebumpdesigns.scaler.model.Measurement;

/**
 * 
 */
class DimensionTest {

  /**
   * 
   */
  @Test
  void assertThatNullValueThrowsException() {
    // Given: a null value
    BigDecimal value = null;
    Measurement measurement = Measurement.CM;

    // When: a dimension is created
    // Then: an exception is thrown
    assertThatThrownBy(() -> new Dimension(value, measurement))
        .isInstanceOf(NullPointerException.class);
  }

  /**
   * 
   */
  @Test
  void assertThatNullMeasurementThrowsException() {
    // Given: a null measurement
    BigDecimal value = new BigDecimal("123.45");
    Measurement measurement = null;

    // When: a dimension is created
    // Then: an exception is thrown
    assertThatThrownBy(() -> new Dimension(value, measurement))
        .isInstanceOf(NullPointerException.class);
  }
}
