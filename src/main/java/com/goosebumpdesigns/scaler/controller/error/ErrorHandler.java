// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.controller.error;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import com.goosebumpdesigns.scaler.controller.ScalerController;
import com.goosebumpdesigns.scaler.model.ScalerData;
import lombok.extern.slf4j.Slf4j;

/**
 * This class handles application errors, returning an error object along with an appropriate HTTP
 * status code.
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {
  private enum LogError {
    MESSAGE, STACK_TRACE
  }

  /**
   * Handle an {@link IllegalArgumentException}. This exception is thrown if both full size and
   * model dimensions are supplied to the service, or when an input field is missing.
   * 
   * @param e The exception that was thrown.
   * @param webRequest This object is supplied by Spring Boot. It describes the HTTP request.
   * @return A populated {@link ErrorDetails} object.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public ErrorDetails handleIllegalArgumentException(IllegalArgumentException e,
      WebRequest webRequest) {
    return buildErrorMessage(e, HttpStatus.BAD_REQUEST, webRequest);
  }

  /**
   * Handle the {@link HttpMessageNotReadableException}. This exception is thrown by Spring if it
   * cannot populate the {@link ScalerData} object used in the {@link ScalerController controller}.
   * 
   * @param e The exception that was thrown.
   * @param webRequest This object is supplied by Spring Boot. It describes the HTTP request.
   * @return A populated {@link ErrorDetails} object.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public ErrorDetails handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
      WebRequest webRequest) {
    return buildErrorMessage(e, HttpStatus.BAD_REQUEST, webRequest);
  }

  /**
   * This handler method is called when an exception is thrown that isn't handled by any of the
   * other methods in this class. When called, this method causes the exception stack trace to be
   * logged.
   * 
   * @param e The exception that was thrown.
   * @param webRequest This object is supplied by Spring Boot. It describes the HTTP request.
   * @return A populated {@link ErrorDetails} object.
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDetails handleException(Exception e, WebRequest webRequest) {
    return buildErrorMessage(e, HttpStatus.INTERNAL_SERVER_ERROR, webRequest, LogError.STACK_TRACE);
  }

  /**
   * Populate and return an {@link ErrorDetails} object that describes the error. This method is
   * used when the stack trace does not need to be logged.
   * 
   * @param e The exception that was thrown.
   * @param status The status code to document in the error object.
   * @param webRequest This object is supplied by Spring Boot. It describes the HTTP request.
   * @return A populated {@link ErrorDetails} object.
   */
  private ErrorDetails buildErrorMessage(Exception e, HttpStatus status, WebRequest webRequest) {
    return buildErrorMessage(e, status, webRequest, LogError.MESSAGE);
  }

  /**
   * Populate and return an {@link ErrorDetails} object that describes the error.
   * 
   * @param e The exception that was thrown.
   * @param status The status code to document in the error object.
   * @param webRequest This object is supplied by Spring Boot. It describes the HTTP request.
   * @param logError This indicates if the entire stack trace or just the exception message is
   *        logged.
   * @return A populated {@link ErrorDetails} object.
   */
  private ErrorDetails buildErrorMessage(Exception e, HttpStatus status, WebRequest webRequest,
      LogError logError) {
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, dd-MMM-yyyy HH:mm:ss");
    DateTimeFormatter zone = DateTimeFormatter.ofPattern("ZZZ");
    ZonedDateTime time = ZonedDateTime.now();
    String timestamp = time.format(fmt) + " GMT" + time.format(zone);
    String uri = "unavailable";

    if(webRequest instanceof ServletWebRequest swr) {
      uri = swr.getRequest().getRequestURI();
    }

    if(logError == LogError.STACK_TRACE) {
      log.error("Exception:", e);
    }
    else {
      log.error("Exception: {}", e.toString());
    }

    // @formatter:off
    return ErrorDetails.builder()
        .errorCode(status.value())
        .errorReason(status.getReasonPhrase())
        .message(e.getMessage())
        .timestamp(timestamp)
        .uri(uri)
        .build();
    // @formatter:on
  }
}
