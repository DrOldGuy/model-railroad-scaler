// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.goosebumpdesigns.scaler.model.ScalerData;
import com.goosebumpdesigns.scaler.service.ScalerService;
import lombok.extern.slf4j.Slf4j;

/**
 * This controller manages the scale operation. Either full size or model measurements are passed to
 * the service. The service fills in the other fields. For example, if full size measurements are
 * passed to the service, it will fill in the model measurement fields.
 */
@RestController
@RequestMapping("/scale")
@Slf4j
public class ScalerController {
  @Autowired
  private ScalerService scalerService;

  /**
   * Fill in either the full size or model fields based on the values that are passed. If full size
   * fields are passed in the model fields are populated and vice versa.
   * 
   * @param scalerData The input data to convert.
   * @return The input data with missing fields filled in.
   */
  @PostMapping
  @ResponseStatus(code = HttpStatus.OK)
  public ScalerData processScalerData(@RequestBody ScalerData scalerData) {
    log.debug("scalerData={}", scalerData);
    return scalerService.supplyMissingFields(scalerData);
  }
}
