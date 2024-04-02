package com.eltechs.axs.helpers;


public abstract class ArithHelpers {
    public static int extendAsUnsigned(byte b) {
        return b & 255;
    }

    public static int extendAsUnsigned(short s) {
        return s & 65535;
    }

    public static long extendAsUnsigned(int i) {
        return i & 4294967295L;
    }

    public static int max(int i, int i2) {
        return Math.max(i, i2);
    }

    public static int min(int i, int i2) {
        return Math.min(i, i2);
    }

    public static float saturateInRange(float f, float f2, float f3) {
        return f < f2 ? f2 : Math.min(f, f3);
    }

    public static int saturateInRange(int i, int i2, int i3) {
        return i < i2 ? i2 : Math.min(i, i3);
    }

    private ArithHelpers() {
    }

    /**
     * 将给定的值 限制在0~max之间
     * @param value 未限制的值
     * @param max 大于等于0
     * @return 限制范围后的值
     */
    public static int unsignedSaturate(int value, int max) {
        Assert.isTrue(max >= 0);
        return value < 0 ? 0 : Math.min(value, max);
    }

    /**
     * 将给定的值 限制在0~max之间
     * @param value 未限制的值
     * @param max 大于等于0
     * @return 限制范围后的值
     */
    public static float unsignedSaturate(float value, float max) {
        Assert.isTrue(max >= 0.0f);
        return value < 0f ? 0f : Math.min(value, max);
    }
}
