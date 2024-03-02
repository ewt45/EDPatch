package com.example.datainsert.exagear.controlsV2;

import static com.eltechs.axs.configuration.XServerViewConfiguration.FitStyleHorizontal.STRETCH;

import android.graphics.Matrix;
import android.support.annotation.Nullable;

import com.eltechs.axs.configuration.XServerViewConfiguration;
import com.eltechs.axs.geom.Point;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.widgets.viewOfXServer.XZoomController;
import com.eltechs.axs.xserver.ViewFacade;

import java.lang.ref.WeakReference;

//TODO 等接口函数全部确定下来之后，把xserver为null的情况都移到XServerViewHolderStub里，这里就不判断null了
//TODO 还要判断一下当前是否是编辑模式，别编辑到一半后面游戏按键触发了
//TODO releaseKeyOrPointer(List<Integer> keycodes) 改为一次性调用xserverfacade的对应方法，防止lock影响时间？
public class XServerViewHolderImpl implements XServerViewHolder {
    private WeakReference<ViewOfXServer> mRenderViewRef = null;
    private Matrix mMatrix = new Matrix();
    private int scaleStyle = SCALE_FULL_WITH_RATIO;

    /**
     * @param view null则说明没运行xserver，但是外部调用此类的方法时不应报错，只是不执行
     */
    public XServerViewHolderImpl(@Nullable ViewOfXServer view) {
        if (view != null) {
            mRenderViewRef = new WeakReference<>(view);
            scaleStyle = view.getConfiguration().getFitStyleHorizontal() == STRETCH ? SCALE_FULL_IGNORE_RATIO : SCALE_FULL_WITH_RATIO;
        }

    }


    @Override
    public void injectPointerMove(float x, float y) {
        if (getXServerFacade() != null && !Const.isEditing())
            getXServerFacade().injectPointerMove((int) x, (int) y);
    }

    @Override
    public void injectPointerDelta(float x, float y, int times) {
        if (getXServerFacade() != null && !Const.isEditing())
            getXServerFacade().injectPointerDelta((int) x, (int) y, times);
    }

    @Override
    public void injectPointerButtonPress(int button) {
        if (getXServerFacade() != null && !Const.isEditing())
            getXServerFacade().injectPointerButtonPress(button);
    }

    @Override
    public void injectPointerButtonRelease(int button) {
        if (getXServerFacade() != null && !Const.isEditing())
            getXServerFacade().injectPointerButtonRelease(button);
    }

    @Override
    public void injectPointerWheelUp(int times) {
        if (getXServerFacade() != null && !Const.isEditing())
            getXServerFacade().injectPointerWheelUp(times);

    }

    @Override
    public void injectPointerWheelDown(int times) {
        if (getXServerFacade() != null && !Const.isEditing())
            getXServerFacade().injectPointerWheelDown(times);
    }

    @Override
    public void injectKeyPress(int keycode) {
        ViewFacade facade = getXServerFacade();
        if (facade != null && !Const.isEditing())
            facade.injectKeyPress((byte) (keycode + 8));
    }

    @Override
    public void injectKeyRelease(int keycode) {
        ViewFacade facade = getXServerFacade();
        if (facade != null && !Const.isEditing())
            facade.injectKeyRelease((byte) (keycode + 8));
    }

    private ViewFacade getXServerFacade() {
        return mRenderViewRef == null ? null : mRenderViewRef.get().getXServerFacade();
    }

    @Override
    public Matrix getXServerToViewTransformationMatrix() {
        return mRenderViewRef == null ? mMatrix : mRenderViewRef.get().getXServerToViewTransformationMatrix();
    }

    @Override
    public Matrix getViewToXServerTransformationMatrix() {
        return mRenderViewRef == null ? mMatrix : mRenderViewRef.get().getViewToXServerTransformationMatrix();
    }


    @Deprecated
    @Override
    public XZoomController getZoomController() {
        return mRenderViewRef == null ? null : mRenderViewRef.get().getZoomController();
    }

    @Override
    public int[] getXScreenPixels() {
        return getXServerFacade() == null ? null : new int[]{getXServerFacade().getScreenInfo().widthInPixels, getXServerFacade().getScreenInfo().heightInPixels};
    }

    @Override
    public Point getPointerLocation() {
        ViewFacade viewFacade = getXServerFacade();
        return viewFacade == null ? new Point(0, 0) : viewFacade.getPointerLocation();
    }

    @Override
    public boolean isShowCursor() {
        return mRenderViewRef != null && mRenderViewRef.get().getConfiguration().isCursorShowNeeded();
    }

    @Override
    public void setShowCursor(boolean showCursor) {
        if (mRenderViewRef != null)
            mRenderViewRef.get().getConfiguration().setShowCursor(showCursor);
    }

    @Override
    public int getScaleStyle() {
        return scaleStyle;
    }

    @Override
    public void setScaleStyle(int scaleStyle) {
        this.scaleStyle = scaleStyle;
        //TODO 这个好像原x11和xegw不一样？
        if (mRenderViewRef == null)
            return;
        XServerViewConfiguration configuration = mRenderViewRef.get().getConfiguration();
        if (scaleStyle == SCALE_FULL_WITH_RATIO) {
            configuration.setFitStyleHorizontal(XServerViewConfiguration.FitStyleHorizontal.CENTER);
            configuration.setFitStyleVertical(XServerViewConfiguration.FitStyleVertical.CENTER);
        } else if (scaleStyle == SCALE_FULL_IGNORE_RATIO) {
            configuration.setFitStyleHorizontal(STRETCH);
            configuration.setFitStyleVertical(XServerViewConfiguration.FitStyleVertical.STRETCH);
        }
    }
}
