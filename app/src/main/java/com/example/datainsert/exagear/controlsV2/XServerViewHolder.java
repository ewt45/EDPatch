package com.example.datainsert.exagear.controlsV2;

import android.graphics.Matrix;
import android.support.annotation.IntDef;

import com.eltechs.axs.geom.Point;
import com.eltechs.axs.widgets.viewOfXServer.XZoomController;

import java.util.List;

/**
 * 有关xserver相关的视图，输入事件
 * <br/>内部存储的xserver可能是null，但是外部调用此类方法时，不应报错，只是不会运行。
 * <br/> 也应该注意，此类方法如果返回了xserver相关的类对象，那么后续在外面对相关对象的调用可能还会报错。
 */
public interface XServerViewHolder {

    Matrix getXServerToViewTransformationMatrix();

    @Deprecated
    XZoomController getZoomController();

    Matrix getViewToXServerTransformationMatrix();

    /**
     * // pressKeyOrPointer 这几个改到接口作为default，因为这个pointer还是key的区分是我自己定义的，不属于通用规则，子类不应该管这些的实现
     */
    default void pressKeyOrPointer(int keycode) {
        if ((keycode & Const.keycodePointerMask) == 0)
            injectKeyPress(keycode); //+8在impl里加吧
        else {
            int buttonCode = keycode - Const.keycodePointerMask;
            if (buttonCode == 4 || buttonCode == 5)
                XServerViewHolder_MouseWheelInjector.getByCode(buttonCode).start();
            else
                injectPointerButtonPress(buttonCode);
        }
    }

    default void releaseKeyOrPointer(int keycode) {
        if ((keycode & Const.keycodePointerMask) == 0)
            injectKeyRelease(keycode); //+8在impl里加吧
        else {
            int buttonCode = keycode - Const.keycodePointerMask;
            if (buttonCode == 4 || buttonCode == 5)
                XServerViewHolder_MouseWheelInjector.getByCode(buttonCode).stop();
            else
                injectPointerButtonRelease(buttonCode);
        }
    }

    default void pressKeyOrPointer(List<Integer> keycodes) {
        for (int keycode : keycodes)
            pressKeyOrPointer(keycode);
    }

    ;

    default void releaseKeyOrPointer(List<Integer> keycodes) {
        for (int keycode : keycodes)
            releaseKeyOrPointer(keycode);
    }

    void injectKeyPress(int keycode);

    void injectKeyRelease(int keycode);


    void injectPointerMove(float x, float y);

    default void injectPointerDelta(float x, float y) {
        injectPointerDelta(x, y, 1);
    }


    void injectPointerDelta(float x, float y, int times);

    void injectPointerButtonPress(int button);

    void injectPointerButtonRelease(int button);

    void injectPointerWheelUp(int times);

    void injectPointerWheelDown(int times);

    int[] getXScreenPixels();

    Point getPointerLocation();
    boolean isShowCursor();

    void setShowCursor(boolean showCursor);
    public final static int SCALE_FULL_WITH_RATIO = 1;
    public final static int SCALE_FULL_IGNORE_RATIO = 2;
    @IntDef(value = {SCALE_FULL_WITH_RATIO,SCALE_FULL_IGNORE_RATIO})
    @interface ScaleStyle{}

    void setScaleStyle(@ScaleStyle int scaleStyle);

    @ScaleStyle int getScaleStyle();
}

