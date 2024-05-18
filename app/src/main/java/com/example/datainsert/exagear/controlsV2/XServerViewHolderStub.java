package com.example.datainsert.exagear.controlsV2;

import android.graphics.Matrix;

import com.eltechs.axs.geom.Point;

/**
 * 当目前没有xserver时新建的xserverholder，大部分返回null，部分返回默认值
 */
public class XServerViewHolderStub implements XServerViewHolder {
    private Matrix mV2XMatrix = new Matrix();
    private final Matrix mTmpX2VMatrix = new Matrix();
    private final int[] fakeScreenPixels = {800, 600};
    private final Point fakePointPos = new Point(0,0);
    private boolean fakeIsShowCursor = false;
    @ScaleStyle private int fakeScaleStyle = SCALE_FULL_WITH_RATIO;

    public XServerViewHolderStub() {
    }

    @Override
    public Matrix getXServerToViewTransformationMatrix() {
        mV2XMatrix.invert(mTmpX2VMatrix);
        return mTmpX2VMatrix;
    }

    @Override
    public Matrix getViewToXServerTransformationMatrix() {
        return mV2XMatrix;
    }

    @Override
    public void setViewToXServerTransformationMatrix(Matrix matrix) {
        mV2XMatrix = matrix;
    }

    @Override
    public XZoomHandler getZoomHandler() {
        return XZoomHandler.EMPTY;
    }

    @Override
    public void setXViewport(float l, float t, float r, float b) {

    }

    @Override
    public void injectKeyPress(int keycode, int keySym) {

    }

    @Override
    public void injectKeyRelease(int keycode, int keySym) {

    }

    @Override
    public void injectPointerMove(float x, float y) {

    }

    @Override
    public void injectPointerDelta(float x, float y, int times) {

    }

    @Override
    public void injectPointerButtonPress(int button) {

    }

    @Override
    public void injectPointerButtonRelease(int button) {

    }

    @Override
    public void injectPointerWheelUp(int times) {

    }

    @Override
    public void injectPointerWheelDown(int times) {

    }

    @Override
    public int[] getXScreenPixels() {
        return fakeScreenPixels;
    }

    @Override
    public int[] getAndroidViewPixels() {
        return fakeScreenPixels;
    }

    @Override
    public Point getPointerLocation() {
        return fakePointPos;
    }

    @Override
    public boolean isShowCursor() {
        return fakeIsShowCursor;
    }

    @Override
    public void setShowCursor(boolean showCursor) {
        fakeIsShowCursor = showCursor;
    }

    @Override
    public int getScaleStyle() {
        return fakeScaleStyle;
    }

    @Override
    public void setScaleStyle(int scaleStyle) {
        fakeScaleStyle = scaleStyle;
    }
}
