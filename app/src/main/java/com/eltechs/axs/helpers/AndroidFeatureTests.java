package com.eltechs.axs.helpers;

import android.os.Build;

/* loaded from: classes.dex */
public abstract class AndroidFeatureTests {
    private AndroidFeatureTests() {
    }

    public static boolean haveAndroidApi(ApiLevel apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel.numericLevel;
    }

    /* loaded from: classes.dex */
    public enum ApiLevel {
        ANDROID_4_4(19),
        ANDROID_4_0(14),
        ANDROID_3_0(11),
        ANDROID_2_3_3(10);

        private final int numericLevel;

        ApiLevel(int i) {
            this.numericLevel = i;
        }
    }
}