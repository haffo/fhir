//    Copyright 2018 Google Inc.
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        https://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.

package com.google.fhir.stu3;

import com.google.fhir.stu3.proto.Decimal;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/** A wrapper around the Decimal FHIR primitive type. */
public class DecimalWrapper extends NumericTypeWrapper<Decimal> {

  private static final Pattern DECIMAL_PATTERN =
      Pattern.compile("[-\\+]?(0|[1-9][0-9]*)(\\.[0-9]+)?");
  private static final Decimal NULL_DECIMAL =
      Decimal.newBuilder().addExtension(getNoValueExtension()).build();

  /** Create an DecimalWrapper from a Decimal. */
  public DecimalWrapper(Decimal decimal) {
    super(decimal);
  }

  /** Create an DecimalWrapper from a java String. */
  public DecimalWrapper(String input) {
    super(input == null ? NULL_DECIMAL : parseAndValidate(input));
  }

  private static void validate(String input) {
    try {
      // We don't use Double.parseDouble() here because that function simply
      // accepts all values. Here we parse the value into a BigDecimal and do
      // explicit range check on it.
      double doubleValue = new BigDecimal(input).doubleValue();
      if (Double.isInfinite(doubleValue)) {
        throw new IllegalArgumentException("Out of range decimal value: " + input);
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Not a decimal value", e);
    }
  }

  private static Decimal parseAndValidate(String input) {
    validateUsingPattern(DECIMAL_PATTERN, input);
    validate(input);
    return Decimal.newBuilder().setValue(input).build();
  }

  @Override
  protected String printValue() {
    return getWrapped().getValue();
  }
}
