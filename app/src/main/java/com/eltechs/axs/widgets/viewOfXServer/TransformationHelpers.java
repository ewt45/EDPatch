package com.eltechs.axs.widgets.viewOfXServer;

import android.graphics.Matrix;

import com.eltechs.axs.configuration.XServerViewConfiguration;
import com.eltechs.axs.helpers.Assert;

/* loaded from: classes.dex */
public class TransformationHelpers {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static TransformationDescription makeTransformationDescription(float viewW, float viewH, float visibleRectX, float visibleRectY, float visibleRectW, float visibleRectH, XServerViewConfiguration.FitStyleHorizontal fitStyleHorizontal, XServerViewConfiguration.FitStyleVertical fitStyleVertical) {
        float viewTransX;
        float scaleX = viewW / visibleRectW;
        float scaleY = viewH / visibleRectH;
        float minScale = Math.min(scaleX, scaleY);
        //如果不是拉伸全屏，那么就要等比全屏，给宽高统一缩放比例
        if (fitStyleHorizontal != XServerViewConfiguration.FitStyleHorizontal.STRETCH)
            scaleX = minScale;
        if (fitStyleVertical != XServerViewConfiguration.FitStyleVertical.STRETCH)
            scaleY = minScale;

        //viewW和viewH，因为viewofxserver安卓视图一直是填充布局的，所以是个定值，
        //就是安卓屏幕宽/高 与 按比例等比（或拉伸）全屏后的宽高之差 。 如果不拉伸，乘以的scale能保证至少宽高有一个是铺满。缩放的话就是直接铺满
        float widthDiff = viewW - (visibleRectW * scaleX);
        float heightDiff = viewH - (visibleRectH * scaleY);
        float viewTransY = 0.0f;
        switch (fitStyleHorizontal) {
            case LEFT:
            case STRETCH:
                viewTransX = 0.0f;
                break;
            case CENTER:
                viewTransX = widthDiff / 2.0f;
                break;
            case RIGHT:
                viewTransX = widthDiff;
                break;
            default:
                Assert.unreachable();
                viewTransX = 0.0f;
                break;
        }
        switch (fitStyleVertical) {
            case TOP:
            case STRETCH:
                break;
            case CENTER:
                heightDiff /= 2.0f;
            case BOTTOM:
                viewTransY = heightDiff;
                break;
            default:
                Assert.unreachable();
                break;
        }
        return new TransformationDescription(scaleX, scaleY, 0 - visibleRectX, 0 - visibleRectY, viewTransX, viewTransY);
    }

    public static Matrix makeTransformationMatrix(float viewW, float viewH, float visibleRectX, float visibleRectY, float visibleRectW, float visibleRectH, XServerViewConfiguration.FitStyleHorizontal fitStyleHorizontal, XServerViewConfiguration.FitStyleVertical fitStyleVertical) {
        TransformationDescription makeTransformationDescription = makeTransformationDescription(viewW, viewH, visibleRectX, visibleRectY, visibleRectW, visibleRectH, fitStyleHorizontal, fitStyleVertical);
        Matrix matrix = new Matrix();
        matrix.postTranslate(makeTransformationDescription.xServerTranslateX, makeTransformationDescription.xServerTranslateY);
        matrix.postScale(makeTransformationDescription.scaleX, makeTransformationDescription.scaleY);
        matrix.postTranslate(makeTransformationDescription.viewTranslateX, makeTransformationDescription.viewTranslateY);
        return matrix;
    }

    public static void mapPoints(Matrix matrix, float[] points) {
        Assert.state(points.length == 2);
        float[] fArr2 = new float[9];
        matrix.getValues(fArr2);
        float[] fArr3 = {0.0f, 0.0f, 0.0f};
        float[] fArr4 = {points[0], points[1], 1.0f};
        for (int i = 0; i < 3; i++) {
            for (int i2 = 0; i2 < 3; i2++) {
                fArr3[i] = fArr3[i] + (fArr2[(i * 3) + i2] * fArr4[i2]);
            }
        }
        points[0] = fArr3[0];
        points[1] = fArr3[1];
    }

    public static float getScaleX(Matrix matrix) {
        float[] fArr = new float[9];
        matrix.getValues(fArr);
        return fArr[0];
    }

    public static float getScaleY(Matrix matrix) {
        float[] fArr = new float[9];
        matrix.getValues(fArr);
        return fArr[4];
    }
}