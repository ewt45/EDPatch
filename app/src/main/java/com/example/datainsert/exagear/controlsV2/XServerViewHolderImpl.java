package com.example.datainsert.exagear.controlsV2;

import static com.eltechs.axs.configuration.XServerViewConfiguration.FitStyleHorizontal.STRETCH;

import android.graphics.Matrix;
import android.support.annotation.NonNull;

import com.eltechs.axs.configuration.XServerViewConfiguration;
import com.eltechs.axs.geom.Point;
import com.eltechs.axs.geom.RectangleF;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.widgets.viewOfXServer.XZoomController;
import com.eltechs.axs.xserver.ViewFacade;

import java.lang.ref.WeakReference;

/**
 * 等接口函数全部确定下来之后，把xserver为null的情况都移到XServerViewHolderStub里，这里就不判断null了
 * 还要判断一下当前是否是编辑模式，别编辑到一半后面游戏按键触发了
 */
//TODO releaseKeyOrPointer(List<Integer> keycodes) 改为一次性调用xserverfacade的对应方法，防止lock影响时间？
public class XServerViewHolderImpl implements XServerViewHolder {
    @NonNull
    private final WeakReference<ViewOfXServer> mRenderViewRef;
    private final XZoomHandlerImpl mZoomController;
    @ScaleStyle
    private int scaleStyle;
    //用于对比当前原始controller，尽量及时发现是否已经被重置
    private XZoomController mOriZoomController = null;

    /**
     * @param view 不能为null。若xserver未启动，则应该是用XServerViewHolderStub类
     */
    public XServerViewHolderImpl(@NonNull ViewOfXServer view) {
        mRenderViewRef = new WeakReference<>(view);
        scaleStyle = view.getConfiguration().getFitStyleHorizontal() == STRETCH ? SCALE_FULL_IGNORE_RATIO : SCALE_FULL_WITH_RATIO;
        mOriZoomController = view.getZoomController();
        mZoomController = new XZoomHandlerImpl(this);
    }


    @Override
    public void injectPointerMove(float x, float y) {
        if (!Const.isEditing())
            getXServerFacade().injectPointerMove((int) x, (int) y);
    }

    @Override
    public void injectPointerDelta(float x, float y, int times) {
        if (!Const.isEditing())
            getXServerFacade().injectPointerDelta((int) x, (int) y, times);
    }

    @Override
    public void injectPointerButtonPress(int button) {
        if (!Const.isEditing())
            getXServerFacade().injectPointerButtonPress(button);
    }

    @Override
    public void injectPointerButtonRelease(int button) {
        if (!Const.isEditing())
            getXServerFacade().injectPointerButtonRelease(button);
    }

    @Override
    public void injectPointerWheelUp(int times) {
        if (!Const.isEditing())
            getXServerFacade().injectPointerWheelUp(times);

    }

    @Override
    public void injectPointerWheelDown(int times) {
        if (!Const.isEditing())
            getXServerFacade().injectPointerWheelDown(times);
    }

    @Override
    public void injectKeyPress(int keycode, int keySym) {
        if (!Const.isEditing())
            getXServerFacade().injectKeyPressWithSym((byte) (keycode + 8),keySym);
    }

    @Override
    public void injectKeyRelease(int keycode, int keySym) {
        if (!Const.isEditing())
            getXServerFacade().injectKeyReleaseWithSym((byte) (keycode + 8),keySym);
    }

    private @NonNull ViewFacade getXServerFacade() {
        return mRenderViewRef.get().getXServerFacade();
    }

    @Override
    public Matrix getXServerToViewTransformationMatrix() {
        return mRenderViewRef.get().getXServerToViewTransformationMatrix();
    }

    @Override
    public Matrix getViewToXServerTransformationMatrix() {
        return mRenderViewRef.get().getViewToXServerTransformationMatrix();
    }

    @Override
    public void setViewToXServerTransformationMatrix(Matrix matrix) {
        mRenderViewRef.get().setViewToXServerTransformationMatrix(matrix);
    }


    @Override
    public XZoomHandler getZoomHandler() {
        // viewOfXServer在setHorizontalStretchEnabled和onSizeChanged函数中会主动新建zoomcontroller，会缩放重置. 自己如果单独维护XZoomHandler则不会收到更新
        // 劫持函数又不现实，在不改动原有代码的基础上，只能尽量及时手动同步了。比如调用holder.getController的时候，手动同步一次factor
        // (只需要判断是否为1就可以，因为设置横向拉伸的那个如果是自己的操作模式下不会调用此函数，只剩一个大小变化时重置，重置肯定就是1了）不行，因为压根没调用过原始的，导致一直为1，不可能是别的值
        if(mOriZoomController!= mRenderViewRef.get().getZoomController()){
            mOriZoomController = mRenderViewRef.get().getZoomController();
            mZoomController.resetZoom();
        }
        return mZoomController;
    }

    @Override
    public void setXViewport(float l, float t, float r, float b) {
        mRenderViewRef.get().setXViewport(new RectangleF(l,t,r-l,b-t));
    }

    /**
     * 获取x11屏幕的宽高像素值
     */
    @Override
    public int[] getXScreenPixels() {
        ViewFacade viewFacade = getXServerFacade();
        return new int[]{viewFacade.getScreenInfo().widthInPixels, viewFacade.getScreenInfo().heightInPixels};
    }

    /**
     * 获取用于显示图形的安卓View视图的宽高像素值
     */
    @Override
    public int[] getAndroidViewPixels(){
        return new int[]{mRenderViewRef.get().getWidth(),mRenderViewRef.get().getHeight()};
    }

    @Override
    public Point getPointerLocation() {
        return getXServerFacade().getPointerLocation();
    }

    @Override
    public boolean isShowCursor() {
        return mRenderViewRef.get().getConfiguration().isCursorShowNeeded();
    }

    @Override
    public void setShowCursor(boolean showCursor) {
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
        XServerViewConfiguration configuration = mRenderViewRef.get().getConfiguration();
        if (scaleStyle == SCALE_FULL_WITH_RATIO) {
            configuration.setFitStyleHorizontal(XServerViewConfiguration.FitStyleHorizontal.CENTER);
            configuration.setFitStyleVertical(XServerViewConfiguration.FitStyleVertical.CENTER);
            mRenderViewRef.get().setHorizontalStretchEnabled(false);
        } else if (scaleStyle == SCALE_FULL_IGNORE_RATIO) {
            configuration.setFitStyleHorizontal(STRETCH);
            configuration.setFitStyleVertical(XServerViewConfiguration.FitStyleVertical.STRETCH);
            mRenderViewRef.get().setHorizontalStretchEnabled(true);
        }
    }
}
