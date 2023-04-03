package com.eltechs.axs.widgets.viewOfXServer;

import android.graphics.Matrix;
import com.eltechs.axs.configuration.XServerViewConfiguration;
import com.eltechs.axs.helpers.Assert;

/* loaded from: classes.dex */
public class TransformationHelpers {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static TransformationDescription makeTransformationDescription(float f, float f2, float f3, float f4, float f5, float f6, XServerViewConfiguration.FitStyleHorizontal fitStyleHorizontal, XServerViewConfiguration.FitStyleVertical fitStyleVertical) {
        float f7;
        float f8 = f / f5;
        float f9 = f2 / f6;
        float min = Math.min(f8, f9);
        switch (fitStyleHorizontal) {
            case LEFT:
            case CENTER:
            case RIGHT:
                f8 = min;
                break;
        }
        switch (fitStyleVertical) {
            case TOP:
            case CENTER:
            case BOTTOM:
                f9 = min;
                break;
        }
        float f10 = f - (f5 * f8);
        float f11 = f2 - (f6 * f9);
        float f12 = 0.0f;
        switch (fitStyleHorizontal.ordinal()) {
            case 1:
            case 4:
                f7 = 0.0f;
                break;
            case 2:
                f10 /= 2.0f;
                f7 = f10;
                break;
            case 3:
                f7 = f10;
                break;
            default:
                Assert.unreachable();
                f7 = 0.0f;
                break;
        }
        switch (fitStyleVertical) {
            case TOP:
            case STRETCH:
                break;
            case CENTER:
                f11 /= 2.0f;
            case BOTTOM:
                f12 = f11;
                break;
            default:
                Assert.unreachable();
                break;
        }
        return new TransformationDescription(f8, f9, -f3, -f4, f7, f12);
    }

    public static Matrix makeTransformationMatrix(float f, float f2, float f3, float f4, float f5, float f6, XServerViewConfiguration.FitStyleHorizontal fitStyleHorizontal, XServerViewConfiguration.FitStyleVertical fitStyleVertical) {
        TransformationDescription makeTransformationDescription = makeTransformationDescription(f, f2, f3, f4, f5, f6, fitStyleHorizontal, fitStyleVertical);
        Matrix matrix = new Matrix();
        matrix.postTranslate(makeTransformationDescription.xServerTranslateX, makeTransformationDescription.xServerTranslateY);
        matrix.postScale(makeTransformationDescription.scaleX, makeTransformationDescription.scaleY);
        matrix.postTranslate(makeTransformationDescription.viewTranslateX, makeTransformationDescription.viewTranslateY);
        return matrix;
    }

    public static void mapPoints(Matrix matrix, float[] fArr) {
        Assert.state(fArr.length == 2);
        float[] fArr2 = new float[9];
        matrix.getValues(fArr2);
        float[] fArr3 = {0.0f, 0.0f, 0.0f};
        float[] fArr4 = {fArr[0], fArr[1], 1.0f};
        for (int i = 0; i < 3; i++) {
            for (int i2 = 0; i2 < 3; i2++) {
                fArr3[i] = fArr3[i] + (fArr2[(i * 3) + i2] * fArr4[i2]);
            }
        }
        fArr[0] = fArr3[0];
        fArr[1] = fArr3[1];
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