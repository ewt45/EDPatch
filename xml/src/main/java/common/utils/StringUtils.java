package common.utils;

/**
 * Created by Sens on 2021/8/27.
 */
public class StringUtils {
    public static boolean isEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }
}
