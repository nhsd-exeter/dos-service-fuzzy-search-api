package uk.nhs.digital.uec.api.auth.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * Assertions for checking arguments passed to a method. Any assertions that fail<br>
 * will result in an {@link IllegalArgumentException} being thrown.
 */
public class CheckArgument {

  private static final Map<Class<? extends Number>, Number> ZERO_VALUES = new HashMap<>();

  static {
    ZERO_VALUES.put(BigDecimal.class, BigDecimal.ZERO);
    ZERO_VALUES.put(BigInteger.class, BigInteger.ZERO);
    ZERO_VALUES.put(Double.class, 0d);
    ZERO_VALUES.put(Float.class, 0f);
    ZERO_VALUES.put(Long.class, 0L);
    ZERO_VALUES.put(Integer.class, 0);
    ZERO_VALUES.put(Short.class, (short) 0);
    ZERO_VALUES.put(Byte.class, (byte) 0);
  }

  /**
   * @param argument Argument we want to check is not {@code null}
   * @param exceptionMessage Message explaining the argument must not be {@code null}, must have
   *     text
   * @throws IllegalArgumentException Thrown if the argument is {@code null}
   */
  public static void isNotNull(Object argument, String exceptionMessage)
      throws IllegalArgumentException {
    checkExceptionMessageHasText(exceptionMessage);

    if (argument == null) {
      throw new IllegalArgumentException(exceptionMessage);
    }
  }

  /**
   * @param argument {@link Map} argument we want to check is not empty or {@code null}
   * @param exceptionMessage Message explaining the argument must not be empty or {@code null}
   * @throws IllegalArgumentException Thrown if the {@link Map} argument is empty or {@code null}
   */
  public static void hasEntries(Map<?, ?> argument, String exceptionMessage)
      throws IllegalArgumentException {
    isNotNull(argument, exceptionMessage);

    if (argument.isEmpty()) {
      throw new IllegalArgumentException(exceptionMessage);
    }
  }

  /**
   * @param argument {@link String} argument we want to check has text and is not {@code null}
   * @param exceptionMessage Message explaining the argument must have text and must not be {@code
   *     null}, must have text
   * @throws IllegalArgumentException Thrown if the {@link String} argument does not have text or is
   *     {@code null}
   */
  public static void hasText(String argument, String exceptionMessage)
      throws IllegalArgumentException {
    isNotNull(argument, exceptionMessage);

    if (StringUtils.isBlank(argument)) {
      throw new IllegalArgumentException(exceptionMessage);
    }
  }

  /**
   * Tests the validity of an object based on an array of valid values.
   *
   * @param object The object to validate.
   * @param exceptionMessage Exception message to use if the object is valid.
   * @param validValues An array of values to test the object against.
   * @throws IllegalArgumentException if the the array of valid values does not contain the object.
   */
  public static void isOneOf(Object object, String exceptionMessage, Object... validValues)
      throws IllegalArgumentException {
    isNotNull(object, exceptionMessage);

    if (Arrays.stream(validValues).noneMatch(object::equals)) {
      throw new IllegalArgumentException(exceptionMessage);
    }
  }

  /**
   * @param argument {@link Number} we want to check is greater than or equal to {@code minimum},
   *     must not be {@code null}
   * @param minimum {@link Number} we want to check {@code argument} is greater than or equal to,
   *     must not be {@code null}
   * @param exceptionMessage Message explaining the argument must be greater than or equal to the
   *     minimum, must have text
   * @param <T> a number type
   * @throws IllegalArgumentException Thrown if the argument is less than the minimum
   */
  public static <T extends Number & Comparable<T>> void isGreaterThanOrEqualTo(
      T argument, T minimum, String exceptionMessage) throws IllegalArgumentException {
    isNotNull(argument, exceptionMessage);
    isNotNull(minimum, "minimum must not be null");

    if (argument.compareTo(minimum) < 0) {
      throw new IllegalArgumentException(exceptionMessage);
    }
  }

  /**
   * @param argument {@link Number} we want to check is less than or equal to {@code maximum}, must
   *     not be {@code null}
   * @param maximum {@link Number} we want to check {@code argument} is less than or equal to, must
   *     not be {@code null}
   * @param exceptionMessage Message explaining the argument must be less than or equal to the
   *     maximum, must have text
   * @param <T> a number type
   * @throws IllegalArgumentException Thrown if the argument is greater than the maximum
   */
  public static <T extends Number & Comparable<T>> void isLessThanOrEqualTo(
      T argument, T maximum, String exceptionMessage) throws IllegalArgumentException {
    isNotNull(argument, exceptionMessage);
    isNotNull(maximum, "maximum must not be null");

    if (argument.compareTo(maximum) > 0) {
      throw new IllegalArgumentException(exceptionMessage);
    }
  }

  /**
   * @param argument {@code String} we want to check matches the supplied regular expression
   * @param regex the regular expression to test
   * @param exceptionMessage Message explaining the argument must be zero or positive, must have
   *     text
   * @throws IllegalArgumentException Thrown if the argument is negative
   */
  public static void matches(String argument, String regex, String exceptionMessage)
      throws IllegalArgumentException {
    isNotNull(argument, exceptionMessage);
    hasText(regex, "regex must have text");

    if (!argument.matches(regex)) {
      throw new IllegalArgumentException(exceptionMessage);
    }
  }

  private static void checkExceptionMessageHasText(String exceptionMessage) {
    if (StringUtils.isBlank(exceptionMessage)) {
      throw new IllegalArgumentException("exceptionMessage must have text");
    }
  }
}
