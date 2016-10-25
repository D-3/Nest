package ml.that.pigeon.util;

/**
 * Operations on primitive integers (like {@code int}).
 * <p>
 * This class tries to handle {@code null} input gracefully. An exception will not be thrown for a
 * {@code null} array input. Each method documents its behavior.
 *
 * @author That Mr.L (thatmr.l@gmail.com)
 */
public class IntegerUtils {

  /**
   * Converts a primitive byte to an array of bytes
   *
   * @param num a byte
   * @return a byte array
   */
  public static byte[] asBytes(byte num) {
    byte[] result = new byte[1];

    result[0] = num;

    return result;
  }

  /**
   * Converts a primitive short to an array of bytes
   *
   * @param num a short
   * @return a byte array
   */
  public static byte[] asBytes(short num) {
    byte[] result = new byte[2];

    result[0] = (byte) ((num >> 8) & 0xff);
    result[1] = (byte) (num & 0xff);

    return result;
  }

  /**
   * Converts a primitive int to an array of bytes
   *
   * @param num an int
   * @return a byte array
   */
  public static byte[] asBytes(int num) {
    byte[] result = new byte[4];

    result[0] = (byte) ((num >> 24) & 0xff);
    result[1] = (byte) ((num >> 16) & 0xff);
    result[2] = (byte) ((num >> 8) & 0xff);
    result[3] = (byte) (num & 0xff);

    return result;
  }

  /**
   * Converts a primitive long to an array of bytes
   *
   * @param num a long
   * @return a byte array
   */
  public static byte[] asBytes(long num) {
    byte[] result = new byte[8];

    result[0] = (byte) ((num >> 56) & 0xff);
    result[1] = (byte) ((num >> 48) & 0xff);
    result[2] = (byte) ((num >> 40) & 0xff);
    result[3] = (byte) ((num >> 32) & 0xff);
    result[4] = (byte) ((num >> 24) & 0xff);
    result[5] = (byte) ((num >> 16) & 0xff);
    result[6] = (byte) ((num >> 8) & 0xff);
    result[7] = (byte) (num & 0xff);

    return result;
  }

  /**
   * Converts an array of primitive bytes to a primitive byte.
   *
   * @param bytes a byte array, may be {@code null}
   * @return a byte, {@code 0} if null or empty array input
   */
  public static byte parseByte(byte[] bytes) {
    if (ArrayUtils.isEmpty(bytes)) {
      return 0;
    }

    return bytes[bytes.length - 1];
  }

  /**
   * Converts an array of primitive bytes to a primitive short.
   *
   * @param bytes a byte array, may be {@code null}
   * @return a short, {@code 0} if null or empty array input
   */
  public static short parseShort(byte[] bytes) {
    if (ArrayUtils.isEmpty(bytes)) {
      return 0;
    }

    bytes = ArrayUtils.ensureLength(bytes, 2);

    return (short) ((bytes[bytes.length - 2] & 0xff) << 8 | (bytes[bytes.length - 1] & 0xff));
  }

  /**
   * Converts an array of primitive bytes to a primitive int.
   *
   * @param bytes a byte array, may be {@code null}
   * @return an int, {@code 0} if null or empty array input
   */
  public static int parseInt(byte[] bytes) {
    if (ArrayUtils.isEmpty(bytes)) {
      return 0;
    }

    bytes = ArrayUtils.ensureLength(bytes, 4);

    return (bytes[bytes.length - 4] & 0xff) << 24
           | (bytes[bytes.length - 3] & 0xff) << 16
           | (bytes[bytes.length - 2] & 0xff) << 8
           | (bytes[bytes.length - 1] & 0xff);
  }

  /**
   * Converts an array of primitive bytes to a primitive long.
   *
   * @param bytes a byte array, may be {@code null}
   * @return a long, {@code 0} if null or empty array input
   */
  public static long parseLong(byte[] bytes) {
    if (ArrayUtils.isEmpty(bytes)) {
      return 0;
    }

    bytes = ArrayUtils.ensureLength(bytes, 8);

    return (bytes[bytes.length - 8] & 0xffL) << 56
           | (bytes[bytes.length - 7] & 0xffL) << 48
           | (bytes[bytes.length - 6] & 0xffL) << 40
           | (bytes[bytes.length - 5] & 0xffL) << 32
           | (bytes[bytes.length - 4] & 0xffL) << 24
           | (bytes[bytes.length - 3] & 0xffL) << 16
           | (bytes[bytes.length - 2] & 0xffL) << 8
           | (bytes[bytes.length - 1] & 0xffL);
  }

  @SuppressWarnings("unchecked")
  public static byte[] toBcd(long num) {
    int digits = 0;

    long quot = num;
    while (quot != 0) {
      quot /= 10;
      digits++;
    }

    int len = digits % 2 == 0 ? digits / 2 : digits / 2 + 1;

    byte[] bcd = new byte[len];

    byte digit;
    for (int i = digits; i > 0; i--) {
      digit = (byte) (num % 10);
      if (i % 2 == 0) {
        bcd[i / 2 - 1] = digit;
      } else {
        bcd[i / 2] |= digit << 4;
      }
      num /= 10;
    }

    return bcd;
  }

}
