// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.model;

import com.goosebumpdesigns.scaler.service.ScalerService;

/**
 * This enum defines the valid measurement types for input and output dimensions. Note that
 * dimensions are scaled based on these enum values, so you can't simply add a new value here (like
 * "YARD" for example) without changing the {@link ScalerService service}.
 */
public enum Measurement {
  INCH("in"), CM("cm"), FOOT("ft"), MM("mm");
  
  private String name;
  
  /**
   * 
   * @param name
   */
  private Measurement(String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return name;
  }

  /**
   * This method returns a {@link Measurement} enum value given the value name. I can't overload the
   * static {@link #valueOf(String) valueOf} method to allow for case insensitive lookups, so I
   * needed to name the method something else.
   * 
   * @param name The enum value name (case insensitive)
   * @return The {@link Measurement} enum value with the given name if found.
   * @throws IllegalArgumentException Thrown if the name doesn't correspond to a {@link Measurement}
   *         value.
   */
  public static Measurement value(String value) {
    for(Measurement m : Measurement.values()) {
      if(m.name().equalsIgnoreCase(value)) {
        return m;
      }
    }

    throw new IllegalArgumentException(value + " is not a valid measurement.");
  }
}
