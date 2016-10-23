package ml.that.pigeon.util;

/**
 * Utility class for intent.
 *
 * @author ThatMrL (thatmr.l@gmail.com)
 */
public class IntentUtils {

  @SuppressWarnings("unchecked")
  public static String makeAction(Class cls, String act) {
    return cls.getCanonicalName() + "." + act.toUpperCase();
  }

}
