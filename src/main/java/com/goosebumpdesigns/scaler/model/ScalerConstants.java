// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.model;

/**
 * This class defines constants used in the scaler package.
 */
public class ScalerConstants {
  /** This is the scale (number of decimal places) in the data returned by the application. */
  public static final int OUTPUT_SCALE = 2;

  /** This is the scale of intermediate operations (like division). */
  public static final int INTERMEDIATE_SCALE = 6;

  private ScalerConstants() {};
}
