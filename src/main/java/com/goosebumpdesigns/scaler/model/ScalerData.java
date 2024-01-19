// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.goosebumpdesigns.scaler.service.ScalerService;
import lombok.Builder;
import lombok.Data;

/**
 * This class is a Data Transfer Object (DTO) that shuttles data back and forth between the layers.
 * It determines the shape of the JSON that is passed into and returned from the Scaler application.
 * A client sends a POST request with either the model or prototype dimensions filled in. The
 * {@link ScalerService} then fills in the rest of the data and returns the completed
 * {@link ScalerData} object. For example, if a client passes in the prototype dimensions, the
 * service will fill in the model dimensions.
 */
@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class ScalerData {
  private Scale scale;
  private Measurement outputMeasurement;
  private Dimensions modelDimensions;
  private Dimensions fullsizeDimensions;

  /**
   * This toString method separates the fields with linefeeds for a more readable result.
   */
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();

    b.append(getClass().getSimpleName()).append(":\n");
    b.append("   Scale=").append(scale).append("\n");
    b.append("   Output=").append(outputMeasurement).append("\n");
    b.append("   Model ").append(modelDimensions).append("\n");
    b.append("   Full Size ").append(fullsizeDimensions).append("\n");

    return b.toString();
  }
}
