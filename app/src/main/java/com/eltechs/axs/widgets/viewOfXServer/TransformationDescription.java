package com.eltechs.axs.widgets.viewOfXServer;

/* loaded from: classes.dex */
public class TransformationDescription {
    /**
     * android单位宽除以xserver单位宽
     * <br/> （等比全屏时，最终值选择scaleXY中较小的一个）
     */
    final float scaleX;
    /**
     * android单位高除以xserver单位高
     * <br/> （等比全屏时，最终值选择scaleXY中较小的一个）
     */
    final float scaleY;
    final float viewTranslateX;
    final float viewTranslateY;
    final float xServerTranslateX;
    final float xServerTranslateY;

    public TransformationDescription(float scaleX, float scaleY, float xServerTranslateX, float xServerTranslateY, float viewTranslateX, float viewTranslateY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.xServerTranslateX = xServerTranslateX;
        this.xServerTranslateY = xServerTranslateY;
        this.viewTranslateX = viewTranslateX;
        this.viewTranslateY = viewTranslateY;
    }
}