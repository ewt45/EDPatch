package org.ewt45.customcontrols.toucharea;

import android.graphics.Canvas;

/**
 * 负责一块区域的图像绘制以及手势操作。
 */
public interface TouchArea {
    /**
     * 手指新进入该区域
     */
     int HANDLED_ADD = 2;
    /**
     * 手指仍然停留在该区域
     */
    int HANDLED_KEEP=2<<1;
    /**
     * 手指移出该区域
     */
     int HANDLED_REMOVE = 2<<2;
    void onDraw( Canvas canvas);

    int handleFingerDown(Finger finger);

    int handleFingerMove(Finger finger);

    int handleFingerUp(Finger finger);
}
