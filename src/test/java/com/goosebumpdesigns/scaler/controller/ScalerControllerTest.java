// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.controller;

import static org.assertj.core.api.Assertions.assertThat;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import com.goosebumpdesigns.scaler.Scaler;
import com.goosebumpdesigns.scaler.controller.error.ErrorDetails;
import com.goosebumpdesigns.scaler.model.Dimensions;
import com.goosebumpdesigns.scaler.model.Measurement;
import com.goosebumpdesigns.scaler.model.Scale;
import com.goosebumpdesigns.scaler.model.ScalerData;

/**
 * 
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {Scaler.class})
@ActiveProfiles("test")
class ScalerControllerTest extends ControllerTestSupport {

  @Autowired
  private TestRestTemplate restTemplate;

  /**
   * 
   */
  @Test
  void assertThatMissingScaleThrowsException() {
    // Given: a request body
    URI uri = buildUri();

    Dimensions modelDimensions = buildDimensions("144.00", "INCH", "5.00", "FOOT", "4.50", "FOOT");
    Dimensions fullsizeDimensions =
        buildDimensions("400.43", "INCH", "139.05", "MM", "12.3", "CM");
    String body = buildBody(null, Measurement.FOOT, modelDimensions, fullsizeDimensions);
    HttpHeaders headers = buildJsonHeaders();
    RequestEntity<String> request = new RequestEntity<>(body, headers, HttpMethod.POST, uri);

    // When: the request is made
    ResponseEntity<?> response = restTemplate.exchange(request, ErrorDetails.class);

    // Then: the result status is 400
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  /**
   * 
   */
  @Test
  void assertThatMissingOutputMeasurementThrowsException() {
    // Given: a request body
    URI uri = buildUri();

    Dimensions modelDimensions = buildDimensions("144.00", "INCH", "5.00", "FOOT", "4.50", "FOOT");
    Dimensions fullsizeDimensions =
        buildDimensions("400.43", "INCH", "139.05", "MM", "12.3", "CM");
    String body = buildBody(Scale.HO, null, modelDimensions, fullsizeDimensions);
    HttpHeaders headers = buildJsonHeaders();
    RequestEntity<String> request = new RequestEntity<>(body, headers, HttpMethod.POST, uri);

    // When: the request is made
    ResponseEntity<?> response = restTemplate.exchange(request, ErrorDetails.class);

    // Then: the result status is 400
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  /**
   * 
   */
  @Test
  void assertThatSupplyingBothRealAndScaleMeasurementsThrowsException() {
    // Given: a request body
    URI uri = buildUri();

    Dimensions modelDimensions = buildDimensions("144.00", "INCH", "5.00", "FOOT", "4.50", "FOOT");
    Dimensions fullsizeDimensions =
        buildDimensions("400.43", "INCH", "139.05", "MM", "12.3", "CM");
    String body = buildBody(Scale.HO, Measurement.CM, modelDimensions, fullsizeDimensions);
    HttpHeaders headers = buildJsonHeaders();
    RequestEntity<String> request = new RequestEntity<>(body, headers, HttpMethod.POST, uri);

    // When: the request is made
    ResponseEntity<?> response = restTemplate.exchange(request, ErrorDetails.class);

    // Then: the result status is 400
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  /**
   * 
   */
  @Test
  void assertThatSupplyingNeitherRealOrScaleMeasurementsThrowsException() {
    // Given: a request body
    URI uri = buildUri();
    String body = buildBody(Scale.HO, Measurement.CM, null, null);
    HttpHeaders headers = buildJsonHeaders();
    RequestEntity<String> request = new RequestEntity<>(body, headers, HttpMethod.POST, uri);

    // When: the request is made
    ResponseEntity<?> response = restTemplate.exchange(request, ErrorDetails.class);

    // Then: the result status is 400
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  /**
   * 
   */
  @Test
  void assertThatModelMeasurementIsCalculatedCorrectlyGivenFullSizeMeasurement() {
    // Given: a scaler data object with real measurements
    Scale scale = Scale.HO;
    Measurement outputMeasurement = Measurement.CM;
    Dimensions fullsizeDimensions =
        buildDimensions("40.00", "FOOT", "12.50", "FOOT", "147.00", "INCH");
    Dimensions modelDimensions = buildDimensions("14.00", "CM", "4.37", "CM", "4.29", "CM");

    ScalerData expected = ScalerData
        .builder() // @formatter:off
        .scale(scale)
        .outputMeasurement(outputMeasurement)
        .fullsizeDimensions(fullsizeDimensions)
        .modelDimensions(modelDimensions)
        .build(); // @formatter:on

    URI uri = buildUri();
    String body = buildBody(scale, outputMeasurement, null, fullsizeDimensions);
    HttpHeaders headers = buildJsonHeaders();
    RequestEntity<String> request = new RequestEntity<>(body, headers, HttpMethod.POST, uri);
    
    // When: the request is made
    ResponseEntity<?> response = restTemplate.exchange(request, ScalerData.class);

    // Then: the result status is 200
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // And: the actual result is what is expected
    assertThat(response.getBody()).isEqualTo(expected);
  }

  /**
   * 
   */
  @Test
  void assertThatFullSizeMeasurementIsCalculatedCorrectlyGivenModelMeasurement() {
    // Given: a scaler data object with scale measurements
    Scale scale = Scale.HO;
    Measurement outputMeasurement = Measurement.FOOT;
    Dimensions modelDimensions = buildDimensions("18.75", "CM", "4.23", "CM", "27.50", "MM");
    Dimensions fullsizeDimensions =
        buildDimensions("53.58", "FOOT", "12.09", "FOOT", "7.86", "FOOT");

    ScalerData expected = ScalerData
        .builder() // @formatter:off
        .scale(scale)
        .outputMeasurement(outputMeasurement)
        .fullsizeDimensions(fullsizeDimensions)
        .modelDimensions(modelDimensions)
        .build(); // @formatter:on

    URI uri = buildUri();
    String body = buildBody(scale, outputMeasurement, modelDimensions, null);
    HttpHeaders headers = buildJsonHeaders();
    RequestEntity<String> request = new RequestEntity<>(body, headers, HttpMethod.POST, uri);

    System.out.println(body);

    // When: the request is made
    ResponseEntity<?> response = restTemplate.exchange(request, ScalerData.class);

    // Then: the result status is 200
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // And: the actual result is what is expected
    assertThat(response.getBody()).isEqualTo(expected);
  }
}
