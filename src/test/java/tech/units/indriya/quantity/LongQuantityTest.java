/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2018, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-385, Indriya nor the names of their contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tech.units.indriya.quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.LongQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public class LongQuantityTest {

  private static final Unit<?> SQUARE_OHM = Units.OHM.multiply(Units.OHM);
  private final LongQuantity<ElectricResistance> ONE_OHM = createQuantity(1L, Units.OHM);
  private final LongQuantity<ElectricResistance> TWO_OHM = createQuantity(2L, Units.OHM);
  private final LongQuantity<ElectricResistance> MAX_VALUE_OHM = createQuantity(Long.MAX_VALUE, Units.OHM);
  private final LongQuantity<ElectricResistance> ONE_MILLIOHM = createQuantity(1L, MetricPrefix.MILLI(Units.OHM));
  private final LongQuantity<ElectricResistance> ONE_KILOOHM = createQuantity(1L, MetricPrefix.KILO(Units.OHM));
  private final LongQuantity<ElectricResistance> ONE_YOTTAOHM = createQuantity(1L, MetricPrefix.YOTTA(Units.OHM));

  private <Q extends Quantity<Q>> LongQuantity<Q> createQuantity(long l, Unit<Q> unit) {
    return new LongQuantity<Q>(Long.valueOf(l).longValue(), unit);
  }

  /**
   * Verifies that the addition of two quantities with the same multiples results in a new quantity with the same multiple and the value holding the
   * sum.
   */
  @Test
  public void additionWithSameMultipleKeepsMultiple() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(TWO_OHM);
    LongQuantity<ElectricResistance> expected = createQuantity(3, Units.OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the addition of two quantities with the same multiples resulting in an overflow throws an exception.
   */
  @Test
  public void additionWithSameMultipleResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      ONE_OHM.add(MAX_VALUE_OHM);
    });
  }

  /**
   * Verifies that adding a quantity with a larger multiple keeps the result to the smaller multiple.
   */
  @Test
  public void additionWithLargerMultipleKeepsSmallerMultiple() {
    Quantity<ElectricResistance> actual = ONE_MILLIOHM.add(ONE_OHM);
    LongQuantity<ElectricResistance> expected = createQuantity(1001, MetricPrefix.MILLI(Units.OHM));
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a smaller multiple casts the result to the smaller multiple.
   */
  @Test
  public void additionWithSmallerMultipleCastsToSmallerMultipleIfNeeded() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(ONE_MILLIOHM);
    LongQuantity<ElectricResistance> expected = createQuantity(1001, MetricPrefix.MILLI(Units.OHM));
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a larger overflowing multiple casts the result to the larger multiple.
   */
  @Test
  public void additionWithLargerOverflowingMultipleCastsToLargerMultiple() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(ONE_YOTTAOHM);
    assertEquals(ONE_YOTTAOHM, actual);
  }

  /**
   * Verifies that adding a quantity with a larger multiple resulting in an overflowing sum casts the result to the larger multiple.
   */
  @Test
  public void additionWithLargerMultipleAndOverflowingResultCastsToLargerMultiple() {
    LongQuantity<ElectricResistance> almost_max_value_ohm = createQuantity(Long.MAX_VALUE - 999, Units.OHM);
    Quantity<ElectricResistance> actual = almost_max_value_ohm.add(ONE_KILOOHM);
    LongQuantity<ElectricResistance> expected = createQuantity(Long.MAX_VALUE / 1000, MetricPrefix.KILO(Units.OHM));
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a larger multiple resulting in an overflowing sum casts the result to the larger multiple.
   */
  @Test
  public void additionWithLargerMultipleButNotOverflowingResultKeepsSmallerMultiple() {
    LongQuantity<ElectricResistance> almost_max_value_ohm = createQuantity(Long.MAX_VALUE - 1000L, Units.OHM);
    Quantity<ElectricResistance> actual = almost_max_value_ohm.add(ONE_KILOOHM);
    assertEquals(MAX_VALUE_OHM, actual);
  }

  /**
   * Verifies that adding a quantity with a smaller underflowing multiple keeps the result at the larger multiple.
   */
  @Test
  public void additionWithSmallerUnderflowingMultipleKeepsAtLargerMultiple() {
    Quantity<ElectricResistance> actual = ONE_YOTTAOHM.add(ONE_OHM);
    assertEquals(ONE_YOTTAOHM, actual);
  }

  /**
   * Verifies that subtraction subtracts the argument from the target object.
   */
  @Test
  public void subtractionSubtractsArgumentFromTargetObject() {
    Quantity<ElectricResistance> actual = TWO_OHM.subtract(ONE_OHM);
    assertEquals(ONE_OHM, actual);
  }

  /**
   * Verifies that the multiplication of two quantities multiplies correctly.
   */
  @Test
  public void quantityMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = TWO_OHM.multiply(TWO_OHM);
    LongQuantity<?> expected = createQuantity(4L, SQUARE_OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication of two quantities resulting in an overflow throws an exception.
   */
  @Test
  public void quantityMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      Quantity<ElectricResistance> halfMaxValuePlusOne = createQuantity(1L + Long.MAX_VALUE / 2L, Units.OHM);
      halfMaxValuePlusOne.multiply(TWO_OHM);
    });
  }

  /**
   * Verifies that the multiplication with a number multiplies correctly.
   */
  @Test
  public void numberMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = TWO_OHM.multiply(2L);
    LongQuantity<ElectricResistance> expected = createQuantity(4L, Units.OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication with a number resulting in an overflow throws an exception.
   */
  @Test
  public void numberMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      Quantity<ElectricResistance> halfMaxValuePlusOne = createQuantity(1L + Long.MAX_VALUE / 2L, Units.OHM);
      halfMaxValuePlusOne.multiply(2L);
    });
  }

  /**
   * Verifies that the division of two quantities divides correctly.
   */
  @Test
  public void quantityDivisionDividesCorrectly() {
    Quantity<?> actual = TWO_OHM.divide(TWO_OHM);
    LongQuantity<Dimensionless> expected = createQuantity(1L, AbstractUnit.ONE);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the division with a number divides correctly.
   */
  @Test
  public void numberDivisionDividesCorrectly() {
    Quantity<?> actual = TWO_OHM.divide(2L);
    assertEquals(ONE_OHM, actual);
  }
  
  /**
   * Verifies that the inverse returns the correct reciprocal for a unit quantity.
   */
  @Test
  public void inverseReturnsUnitQuantityForUnitQuantity() {
    Quantity<?> actual = ONE_OHM.inverse();
    LongQuantity<?> expected = createQuantity(1L, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the inverse returns the correct reciprocal for a quantity larger than a unit quantity.
   */
  @Test
  public void inverseReturnsZeroQuantityForLargerThanUnitQuantity() {
    Quantity<?> actual = TWO_OHM.inverse();
    LongQuantity<?> expected = createQuantity(0L, Units.OHM.inverse());
    assertEquals(expected, actual);
  }
  
  /**
   * Verifies that the inverse throws an exception for a zero quantity.
   */
  @Test
  public void inverseThrowsExceptionForZeroQuantity() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity(0L, Units.OHM).inverse();
    });
  } 
  
  /**
   * Verifies that a LongQuantity isn't big.
   */
  @Test
  public void longQuantityIsNotBig() {
    assertFalse(ONE_OHM.isBig());
  }
  
  /**
   * Verifies that a quantity isn't equal to null.
   */
  @Test
  public void longQuantityIsNotEqualToNull() {
    assertFalse(ONE_OHM.equals(null));
  }
  
  /**
   * Verifies that a quantity is equal to itself.
   */
  @Test
  public void longQuantityIsEqualToItself() {
    assertTrue(ONE_OHM.equals(ONE_OHM));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different value.
   */
  @Test
  public void longQuantityIsNotEqualToQuantityWithDifferentValue() {
    assertFalse(ONE_OHM.equals(TWO_OHM));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different unit.
   */
  @Test
  public void longQuantityIsNotEqualToQuantityWithDifferentUnit() {
    assertFalse(ONE_OHM.equals(ONE_KILOOHM));
  }

  /**
   * Verifies that a quantity is not equal to an object of a different class.
   */
  @Test
  public void longQuantityIsNotEqualToObjectOfDifferentClass() {
    assertFalse(ONE_OHM.equals(SQUARE_OHM));
  }

  @Test
  public void longValueTest() {
    LongQuantity<Time> day = new LongQuantity<Time>(Double.valueOf(3).longValue(), Units.DAY);
    long hours = day.longValue(Units.HOUR);
    assertEquals(72L, hours);
  }

  @Test
  public void toTest() {
    Quantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    Quantity<Time> hour = day.to(Units.HOUR);
    assertEquals(hour.getValue().intValue(), 24);
    assertEquals(hour.getUnit(), Units.HOUR);

    Quantity<Time> dayResult = hour.to(Units.DAY);
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
  }
}
