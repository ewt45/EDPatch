package com.example.datainsert.exagear.controlsV2;

import android.graphics.Matrix;

import com.eltechs.axs.widgets.viewOfXServer.XZoomController;
import com.eltechs.axs.xserver.ViewFacade;

import java.util.List;

/**
 * 当目前没有xserver时新建的xserverholder，大部分返回null，部分返回默认值
 */
public abstract class XServerViewHolderStub implements XServerViewHolder {
    Matrix mMatrix = new Matrix();
    private ViewFacade getXServerFacade() {
        return null;
    }

    @Override
    public Matrix getXServerToViewTransformationMatrix() {
        return mMatrix;
    }

    @Override
    public XZoomController getZoomController() {
        return null;
    }

    @Override
    public Matrix getViewToXServerTransformationMatrix() {
        return mMatrix;
    }

    @Override
    public void pressKeyOrPointer(int keycode) {

    }

    @Override
    public void pressKeyOrPointer(List<Integer> keycodes) {

    }

    @Override
    public void releaseKeyOrPointer(int keycode) {

    }

    @Override
    public void releaseKeyOrPointer(List<Integer> keycodes) {

    }
}
