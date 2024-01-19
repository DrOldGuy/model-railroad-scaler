// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler.model;

import java.math.BigDecimal;
import lombok.Getter;

/**
 * This enum defines the various scales recognized by the application.
 */
public enum Scale {
  // @formatter:off
  O(new BigDecimal(48)), 
  S(new BigDecimal(64)), 
  OO(new BigDecimal(76)), 
  HO(new BigDecimal("87.1")), 
  TT(new BigDecimal(120)), 
  N(new BigDecimal(160)), 
  Z(new BigDecimal(220));
  // @formatter:on

  @Getter
  private BigDecimal factor;

  private Scale(BigDecimal factor) {
    this.factor = factor;
  }

  /**
   * This method returns a {@link Scale} enum value given the value name. I can't overload the
   * static {@link #valueOf(String) valueOf} method to allow for case insensitive lookups, so I
   * needed to name the method something else.
   * 
   * @param name The enum value name (case insensitive)
   * @return The {@link Scale} enum value with the given name if found.
   * @throws IllegalArgumentException Thrown if the name doesn't correspond to a {@link Scale}
   *         value.
   */
  public static Scale value(String name) {
    for(Scale scale : Scale.values()) {
      if(scale.name().equalsIgnoreCase(name)) {
        return scale;
      }
    }

    throw new IllegalArgumentException(name + " is not a valid Scale name.");
  }
}
