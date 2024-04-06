package com.eltechs.axs.widgets.viewOfXServer;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

import com.eltechs.axs.geom.RectangleF;
import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.ScreenInfo;

/* loaded from: classes.dex */
public class XZoomController {
    private static final String TAG = "XZoomController";
    /**
     *   实时更新的，移动位置的那根手指对应安卓的坐标
     */
    private PointF anchorHost;
    /**
     *   应该是只在进入或退出缩放状态时设置一次，移动位置的那根手指对应xserver的坐标
     */
    private PointF anchorXServer;
    private boolean isZoomed;
    private final ViewOfXServer viewOfXServer;
    /**
     * 实际显示的部分，对xserver的裁切矩形
     */
    private RectangleF visibleRectangle;
    private final ScreenInfo xServerScreenInfo;
    private final float MAX_ZOOM_FACTOR = 5.0f;
    private final float ZOOM_SENSETIVITY_THRESHOLD = 1.005f;
    private double zoomFactor = 1.0d;

    public XZoomController(ViewOfXServer viewOfXServer, ScreenInfo screenInfo) {
        this.viewOfXServer = viewOfXServer;
        this.xServerScreenInfo = screenInfo;
        this.visibleRectangle = new RectangleF(0.0f, 0.0f, screenInfo.widthInPixels, screenInfo.heightInPixels);
    }

    public void setAnchorBoth(float x, float y) {
        setAnchorHost(x, y);
        float[] fArr = {x, y};
        TransformationHelpers.mapPoints(this.viewOfXServer.getViewToXServerTransformationMatrix(), fArr);
        this.anchorXServer = new PointF(fArr[0], fArr[1]);
    }

    public void setAnchorHost(float x, float y) {
        this.anchorHost = new PointF(x, y);
    }

    public void insertZoomFactorChange(double d) {
        this.zoomFactor *= d;
    }


    public void refreshZoom() {
        if (this.zoomFactor < ZOOM_SENSETIVITY_THRESHOLD) {
            if (this.zoomFactor < 1.0d) {
                this.zoomFactor = 1.0d;
            }
            if (this.isZoomed) {
                revertZoom();
                this.isZoomed = false;
                return;
            }
            return;
        }
        if (this.zoomFactor > MAX_ZOOM_FACTOR) {
            this.zoomFactor = MAX_ZOOM_FACTOR;
        }
        this.isZoomed = true;
        setZoom();
    }

    public boolean isZoomed() {
        return this.isZoomed;
    }

    public void revertZoom() {
        applyZoomRect(new RectangleF(0.0f, 0.0f, this.xServerScreenInfo.widthInPixels, this.xServerScreenInfo.heightInPixels));
    }

    private void setZoom() {
//        Log.d(TAG, "开始放大：");
        Assert.isTrue(this.isZoomed);
        int xserverW = this.xServerScreenInfo.widthInPixels;
        int xserverH = this.xServerScreenInfo.heightInPixels;
        //这个是缩放之后可见的宽高，比如放大了那就比完整宽高要小
        float zoomedW = ArithHelpers.unsignedSaturate((float) (xserverW / this.zoomFactor), xserverW);
        float zoomedH = ArithHelpers.unsignedSaturate((float) (xserverH / this.zoomFactor), xserverH);
        //用于移动位置的那根手指，会将安卓坐标同步到anchorHost，这里将其转换为xserver坐标
        float[] newAnchorXServer = {this.anchorHost.x, this.anchorHost.y};
        TransformationHelpers.mapPoints(this.viewOfXServer.getViewToXServerTransformationMatrix(), newAnchorXServer);
        //应用新的裁切矩形，原点根据第一根手指的坐标调整一下，宽高根据两根手指的距离调整一下
        applyZoomRect(new RectangleF(visibleRectangle.x + anchorXServer.x - newAnchorXServer[0], visibleRectangle.y + anchorXServer.y - newAnchorXServer[1], zoomedW, zoomedH));
    }

    /**
     * @param newVisibleRect 注意这里Rect y向上为正
     */
    private void applyZoomRect(RectangleF newVisibleRect) {
        Matrix TransMatrix = TransformationHelpers.makeTransformationMatrix(this.viewOfXServer.getWidth(), this.viewOfXServer.getHeight(), newVisibleRect.x, newVisibleRect.y, newVisibleRect.width, newVisibleRect.height, this.viewOfXServer.getConfiguration().getFitStyleHorizontal(), this.viewOfXServer.getConfiguration().getFitStyleVertical());
        Assert.state(TransMatrix.invert(TransMatrix), "xScreenRect is degenerate");
        this.viewOfXServer.setViewToXServerTransformationMatrix(TransMatrix);
        this.viewOfXServer.setXViewport(newVisibleRect);
        this.visibleRectangle = newVisibleRect;
    }
}