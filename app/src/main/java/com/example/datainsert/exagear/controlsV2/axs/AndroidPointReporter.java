package com.example.datainsert.exagear.controlsV2.axs;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.XServerViewHolder;

/**
 * 来自exa的PointerEventReporter。
 * <br/>函数改为静态调用。
 * <br/> 相对移动鼠标的函数中需要用到当前屏幕宽高，所以在Const.init中初始化（是否足够？xserver改变宽高时一定调用Const.init吗）
 */
public class AndroidPointReporter {
    private static final String TAG = "PointerEventReporter";

    public AndroidPointReporter() {

    }

    private static void sendCoordinates(float f, float f2) {
        XServerViewHolder holder = Const.getXServerHolder();
        float[] fArr = {f, f2};
        TransformationHelpers.mapPoints(holder.getViewToXServerTransformationMatrix(), fArr);
//        Log.d(TAG, "sendCoordinates: 设置坐标："+fArr[0]+","+fArr[1]);
        holder.injectPointerMove((int) fArr[0], (int) fArr[1]);
    }

    public static void pointerEntered(float f, float f2) {
        sendCoordinates(f, f2);
    }

    public static void pointerExited(float f, float f2) {
        sendCoordinates(f, f2);
    }

    public static void pointerMove(float f, float f2) {
        sendCoordinates(f, f2);
    }

    public static void clickAtPoint(float f, float f2, int i, int i2) {
        pointerMove(f, f2);
        click(i, i2);
    }

    public static void click(int i, int i2) {
        try {
            Thread.sleep(i2, 0);
        } catch (InterruptedException ignored) {
        }
        buttonPressed(i);
        try {
            Thread.sleep(i2, 0);
        } catch (InterruptedException ignored) {
        }
        buttonReleased(i);
    }

    public static void pointerMoveDelta(float dx, float dy) {
        XServerViewHolder holder = Const.getXServerHolder();
        float[] p = {0,0,dx,dy}; //与map单个点不同，如果要转换距离，需要map两个点然后计算两者map之后之差
        Matrix matrix = holder.getViewToXServerTransformationMatrix();
        matrix.mapPoints(p);
        float xDx = p[2]-p[0], xDy = p[3]-p[1];
        Log.d("MouseMoveAdapter", "moveTo: view偏移量："+dx+","+dy+", xserver偏移量："+xDx+","+xDy );
        holder.injectPointerDelta(xDx,xDy);
//        int min = Math.min((int) Math.max(Math.abs(dx / Const.maxPointerDeltaDistInOneEvent), Math.abs(dy / Const.maxPointerDeltaDistInOneEvent)), Const.maxDivisor);
//        float f4 = dx / (float) min;
//        float f5 = dy / (float) min;
//        Const.getXServerHolder().injectPointerDelta((int) f4, (int) f5, min);
//        Const.getXServerHolder().injectPointerDelta((int) (dx - (f4 * (float) min)), (int) (dy - (f5 * (float) min)));
    }

    public static void buttonPressed(int i) {
        Const.getXServerHolder().injectPointerButtonPress(i);
    }

    public static void buttonReleased(int i) {
        Const.getXServerHolder().injectPointerButtonRelease(i);
    }

    public static void wheelScrolledUp() {
        Const.getXServerHolder().injectPointerWheelUp(1);
    }

    public static void wheelScrolledDown() {
        Const.getXServerHolder().injectPointerWheelDown(1);
    }
}
