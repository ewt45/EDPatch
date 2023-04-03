package com.eltechs.axs;

import android.graphics.PointF;

/* loaded from: classes.dex */
public class GeometryHelpers {
    public static float distance(float x1, float y1, float x2, float y2) {
        float distanceAxis = distanceAxis(x1, x2);
        float distanceAxis2 = distanceAxis(y1, y2);
        return (float) Math.sqrt((distanceAxis * distanceAxis) + (distanceAxis2 * distanceAxis2));
    }

    public static float distance(PointF pointF, PointF pointF2) {
        return distance(pointF.x, pointF.y, pointF2.x, pointF2.y);
    }

    public static float distanceAxis(float f, float f2) {
        return Math.abs(f - f2);
    }

    public static float[] center(float f, float f2, float f3, float f4) {
        return new float[]{(f + f3) / 2.0f, (f2 + f4) / 2.0f};
    }

    public static PointF center(PointF pointF, PointF pointF2) {
        float[] center = center(pointF.x, pointF.y, pointF2.x, pointF2.y);
        return new PointF(center[0], center[1]);
    }
}