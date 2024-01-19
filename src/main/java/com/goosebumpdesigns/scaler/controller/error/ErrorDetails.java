// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.controller.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An object of this class is returned in the POST payload (as JSON) if an error occurs in the
 * application. The returned object contains details about the error.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
  private String message;
  private int errorCode;
  private String errorReason;
  private String timestamp;
  private String uri;

  @Override
  public String toString() {
    return """
        %s:
           message=%s
           error code=%d
           errorReason=%s
           timestamp=%s
           uri=%s
        """.formatted(getClass().getSimpleName(), message, errorCode, errorReason, timestamp, uri);
  }
}
