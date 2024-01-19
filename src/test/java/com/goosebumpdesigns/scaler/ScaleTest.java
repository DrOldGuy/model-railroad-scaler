// Copyright (c) 2024 Goosebump Designs LLC

package com.goosebumpdesigns.scaler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.MethodSource;
import com.goosebumpdesigns.scaler.model.Scale;

/**
 * 
 */
class ScaleTest {

  /**
   * 
   */
  @ParameterizedTest
  @MethodSource("com.goosebumpdesigns.scaler.ScaleTest#scaleValues")
  void assertThatAScaleCanBeCreatedFromAStringValue(String value, Scale expected,
      boolean throwsException) {
    // Given: a string value

    if(throwsException) {
      // When: the scale is converted from the value
      // Then: an illegal argument exception is thrown
      assertThatThrownBy(() -> Scale.value(value)).isInstanceOf(IllegalArgumentException.class);
    }
    else {
      // When: the scale is converted from the value
      Scale actual = Scale.value(value);
      
      // Then: the expected scale matches the actual scale
      assertThat(actual).isEqualTo(expected);
    }
  }

  static Stream<Arguments> scaleValues() {
    // @formatter:off
    return Stream.of(
        arguments("O", Scale.O, false),
        arguments("o", Scale.O, false),
        arguments("S", Scale.S, false),
        arguments("s", Scale.S, false),
        arguments("OO", Scale.OO, false),
        arguments("oO", Scale.OO, false),
        arguments("Oo", Scale.OO, false),
        arguments("oo", Scale.OO, false),
        arguments("HO", Scale.HO, false),
        arguments("hO", Scale.HO, false),
        arguments("Ho", Scale.HO, false),
        arguments("ho", Scale.HO, false),
        arguments("TT", Scale.TT, false),
        arguments("tT", Scale.TT, false),
        arguments("Tt", Scale.TT, false),
        arguments("tt", Scale.TT, false),
        arguments("N", Scale.N, false),
        arguments("n", Scale.N, false),
        arguments("Z", Scale.Z, false),
        arguments("z", Scale.Z, false),
        arguments("Bogus", null, true),
        arguments(null, null, true)
    );
    // @formatter:on
  }
}
