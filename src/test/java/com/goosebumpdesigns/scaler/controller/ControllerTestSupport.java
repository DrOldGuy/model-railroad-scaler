// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.controller;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goosebumpdesigns.scaler.model.Dimension;
import com.goosebumpdesigns.scaler.model.Dimensions;
import com.goosebumpdesigns.scaler.model.Measurement;
import com.goosebumpdesigns.scaler.model.Scale;
import com.goosebumpdesigns.scaler.model.ScalerData;

/**
 * 
 */
public class ControllerTestSupport {

  @LocalServerPort
  private int serverPort;

  /**
   * @param scale
   * @param outputMeasurement
   * @param modelDimensions
   * @param fullsizeDimensions
   * @return
   */
  protected String buildBody(Scale scale, Measurement outputMeasurement, Dimensions modelDimensions,
      Dimensions fullsizeDimensions) {
    // @formatter:off
    ScalerData data = ScalerData.builder()
        .scale(scale)
        .outputMeasurement(outputMeasurement)
        .modelDimensions(modelDimensions)
        .fullsizeDimensions(fullsizeDimensions)
        .build();
    // @formatter:on

    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);

    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
    }
    catch(JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * 
   * @param length
   * @param lengthType
   * @param width
   * @param widthType
   * @param height
   * @param heightType
   * @return
   */
  protected Dimensions buildDimensions(String length, String lengthType, String width,
      String widthType, String height, String heightType) {
    Dimension lengthDim = null;
    Dimension widthDim = null;
    Dimension heightDim = null;

    if(Objects.nonNull(length) && Objects.nonNull(lengthType)) {
      lengthDim = buildDimension(length, lengthType);
    }

    if(Objects.nonNull(width) && Objects.nonNull(widthType)) {
      widthDim = buildDimension(width, widthType);
    }

    if(Objects.nonNull(height) && Objects.nonNull(heightType)) {
      heightDim = buildDimension(height, heightType);
    }

    return new Dimensions(lengthDim, widthDim, heightDim);
  }

  /**
   * 
   * @param value
   * @param measure
   * @return
   */
  protected Dimension buildDimension(String value, String measure) {
    return new Dimension(new BigDecimal(value), Measurement.value(measure));
  }

  /**
   * @return
   */
  protected URI buildUri() {
    try {
      return new URI(String.format("http://localhost:%d/scale", serverPort));
    }
    catch(URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 
   * @return
   */
  protected HttpHeaders buildJsonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

}
