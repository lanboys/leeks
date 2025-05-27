package utils;

/**
 * Created by oopcoder at 2025/5/27 23:33 .
 */

public class NumberUtil {
   public static double toDouble(String str) {
      return toDouble(str, 0.0);
   }

   public static double toDouble(String str, double defaultValue) {
      if (str == null) {
         return defaultValue;
      } else {
         try {
            return Double.parseDouble(str);
         } catch (NumberFormatException var4) {
            return defaultValue;
         }
      }
   }
}
