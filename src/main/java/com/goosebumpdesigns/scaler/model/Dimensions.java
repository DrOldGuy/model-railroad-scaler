// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This record allows the application to work with length, width and height measurements.
 */
@JsonInclude(Include.NON_NULL)
public record Dimensions(Dimension length, Dimension width, Dimension height) {
}
