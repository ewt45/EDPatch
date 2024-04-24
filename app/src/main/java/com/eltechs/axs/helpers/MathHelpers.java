package com.eltechs.axs.helpers;

/* loaded from: classes.dex */
public abstract class MathHelpers {
    static final double log2 = Math.log(2.0d);

    private MathHelpers() {
    }

    /**
     * 获取最接近（小于等于）value的 2的幂次方 (POT是power of two的缩写？）
     * @param value 一个正数
     * @return 最接近该正数的2的幂次方
     */
    public static int lowerPOT(int value) {
        Assert.isTrue(value > 0, "Non positive number");
        return (int) Math.floor(Math.pow(2.0d, Math.floor(Math.log(value) / log2)));
    }

    /**
     * 获取最接近（大于等于）value的 2的幂次方(POT是power of two的缩写？）
     * @param value 一个正数
     * @return 最接近该正数的2的幂次方
     */
    public static int upperPOT(int value) {
        int lowerPOT = lowerPOT(value);
        return lowerPOT < value ? lowerPOT * 2 : lowerPOT;
    }
}