package com.eltechs.axs.widgets.viewOfXServer;

/* loaded from: classes.dex */
public class TransformationDescription {
    final float scaleX;
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