package com.eltechs.axs.helpers;

import org.apache.commons.io.IOUtils;

/* loaded from: classes.dex */
public class StringHelpers {
    public static String removeTrailingSlashes(String str) {
        int length = str.length();
        while (true) {
            length--;
            if (str.charAt(length) != '/' || length <= 0) {
                break;
            }
        }
        return str.substring(0, length + 1);
    }

    public static String appendTrailingSlash(String str) {
        if (str.charAt(str.length() - 1) != '/') {
            return str + IOUtils.DIR_SEPARATOR_UNIX;
        }
        return str;
    }
}