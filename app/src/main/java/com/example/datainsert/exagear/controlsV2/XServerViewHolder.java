package com.example.datainsert.exagear.controlsV2;

import android.graphics.Matrix;
import android.support.annotation.IntDef;

import com.eltechs.axs.geom.Point;

import java.util.List;

/**
 * 有关xserver相关的视图，输入事件
 * <br/>内部存储的xserver可能是null，但是外部调用此类方法时，不应报错，只是不会运行。
 * <br/> 也应该注意，此类方法如果返回了xserver相关的类对象，那么后续在外面对相关对象的调用可能还会报错。
 */
public interface XServerViewHolder {

    public final static int SCALE_FULL_WITH_RATIO = 1;
    public final static int SCALE_FULL_IGNORE_RATIO = 2;

    Matrix getXServerToViewTransformationMatrix();

    Matrix getViewToXServerTransformationMatrix();

    void setViewToXServerTransformationMatrix(Matrix matrix);

    /**
     * 用于控制屏幕缩放。如果没有实现，可以传入XZoomHandler.EMPTY
     */
    XZoomHandler getZoomController();

    /**
     * 设置x屏幕上可见的范围的起始位置和宽高（相对完整可见区域）,用于屏幕缩放时
     */
    void setXViewport(float l, float t, float r, float b);

    /**
     * pressKeyOrPointer 这几个改到接口作为default，因为这个pointer还是key的区分是我自己定义的，不属于通用规则，子类不应该管这些的实现
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

    default void releaseKeyOrPointer(List<Integer> keycodes) {
        for (int keycode : keycodes)
            releaseKeyOrPointer(keycode);
    }

    /**同 {@link #injectKeyPress(int, int)}*/
    default void injectKeyPress(int keycode){
        injectKeyPress(keycode,0);
    }

    /**
     * 输入（键盘）按键按下事件。
     * @param keycode <a href="https://elixir.bootlin.com/linux/v6.0.2/source/include/uapi/linux/input-event-codes.h">linux的keycode</a>,不像exa中那样+8
     * @param keySym 用于表示同键位不同字符，或unicode字符  <a href="https://github.com/D-Programming-Deimos/libX11/blob/master/c/X11/keysymdef.h">参考此代码</a>
     */
    void injectKeyPress(int keycode, int keySym);

    /**同 {@link  #injectKeyRelease(int, int)}*/
    default void injectKeyRelease(int keycode){
        injectKeyRelease(keycode,0);
    }

    /** 参考 {@link #injectKeyPress(int, int)}*/
    void injectKeyRelease(int keycode, int keySym);

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

    int[] getAndroidViewPixels();

    Point getPointerLocation();

    boolean isShowCursor();

    void setShowCursor(boolean showCursor);

    @ScaleStyle
    int getScaleStyle();

    void setScaleStyle(@ScaleStyle int scaleStyle);

    @IntDef(value = {SCALE_FULL_WITH_RATIO, SCALE_FULL_IGNORE_RATIO})
    @interface ScaleStyle {
    }

}

