// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.goosebumpdesigns.scaler.model.Dimension;
import com.goosebumpdesigns.scaler.model.Dimensions;
import com.goosebumpdesigns.scaler.model.Measurement;
import com.goosebumpdesigns.scaler.model.Scale;
import com.goosebumpdesigns.scaler.model.ScalerData;
import com.goosebumpdesigns.scaler.service.ScalerService;

/**
 * You can use this test to manually calculate scaled dimensions. Set the dimensions to whatever you
 * want. Then set either full size dimensions or model dimensions in the ScalerData input object.
 * Then run the test.
 */
class ManualScale {
  private ScalerService scalerService = new ScalerService();

  /**
   * Set the variables as needed and run the test to manually calculate the scaled dimensions.
   */
  @Test
  void test() {
    Scale scale = Scale.HO;
    Measurement outputMeasurement = Measurement.INCH;
    Dimension length = new Dimension(new BigDecimal("48.5"), Measurement.FOOT);
    Dimension width = new Dimension(new BigDecimal("18.5"), Measurement.FOOT);
    Dimension height = new Dimension(new BigDecimal("15.55"), Measurement.FOOT);
    Dimensions dimensions = new Dimensions(length, width, height);

    // @formatter:off
    ScalerData data = ScalerData.builder()
        .scale(scale)
        .outputMeasurement(outputMeasurement)
        .fullsizeDimensions(dimensions) // flip with model as desired
        .modelDimensions(null)
        .build();
    // @formatter:on

    ScalerData scaled = scalerService.supplyMissingFields(data);

    System.out.println(scaled);
  }

}
