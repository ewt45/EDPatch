package com.example.datainsert.exagear.controlsV2;

import static com.example.datainsert.exagear.controlsV2.XServerViewHolder.SCALE_FULL_WITH_RATIO;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;


/**
 * 内部存一个viewOfXServer。
 * <br/> 用于负责画面缩放的具体实现，因此内部会调用一些XServerViewHolder的方法，如果实现了这些方法则会缩放，没实现也就是没反应而已。
 * <br/> xserver内 sizeChange的时候还会重建ZoomController，会不会有冲突？（还有matrix）
 * <br/> x和安卓的完整宽高 实时获取，holder没有单独数据类记录宽高的，如果不实时，更新了的话这里可能不是最新的
 * <br/> 应该实现的方法：start(float[] f1, float[] f2); update(float[] f1, float[] f2); setZoomFactor(double);
 * <br/> viewOfXServer的那几个函数要不要改成Controller的接口，比如getScaleStyle，setMatrix，setViewport之类的
 */
public class XZoomHandlerImpl implements XZoomHandler {
    private static final String TAG = "XZoomController";
    private static final float MAX_ZOOM_FACTOR = 5.0f;
    private static final float ZOOM_SENSETIVITY_THRESHOLD = 1.005f;
    private final XServerViewHolder viewOfXServer;
    /**
     * 实际显示的部分，对xserver的裁切矩形
     */
    private final RectF visibleRectangle;
    /**
     * 实际显示的部分，对xserver的裁切矩形
     */
    private double zoomFactorCurr = 1.0d;
    private boolean isZoomed;
    /**
     * 用于确定位置的手指（锚点）x单位坐标。缩放过程中实时更新。(原anchorHost)
     */
    private final PointF anchorLatestInX = new PointF();
    /**
     * 用于确定位置的手指（锚点）x单位坐标。缩放矩阵开始改变之前，此位置确定下来，缩放过程中不再改变。(原anchorXServer)
     */
    private final PointF anchorWhenZoomStartInX = new PointF();
    private float lastDistance;

    public XZoomHandlerImpl(XServerViewHolder viewOfXServer) {
        this.viewOfXServer = viewOfXServer;
        int[] wh = viewOfXServer.getXScreenPixels();
        this.visibleRectangle = new RectF(0.0f, 0.0f, wh[0], wh[1]);

    }

    /**
     * 缩放开始。传入此时的两根手指的坐标(event中获取）
     * <br/> 函数内部会记录此时的缩放倍率，之后的update()都在此基础上调整
     */
    @Override
    public void start(float x1, float y1, float x2, float y2) {
        lastDistance = TestHelper.distance(x1, y1, x2, y2);
        //TODO 为什么原代码用的getXWhenFingerCountLastChanged？
        setAnchorWhenStart(x1,y1);
        setAnchorLatest(x1,y1);
    }

    /**
     * 缩放更新，传入传入此时的两根手指的坐标(event中获取）。函数内部会重新计算缩放倍率和当前应显示的画面区域，并将此区域提交给viewOfXServer
     */
    @Override
    public void update(float x1, float y1, float x2, float y2) {
        float newDistance = TestHelper.distance(x1, y1, x2, y2);
        boolean isEarlierZoomed = isZoomed;

        //更新锚点手指的位置（安卓单位）
        setAnchorLatest(x1, y1);

        //更新缩放倍率和缩放状态
        zoomFactorCurr = zoomFactorCurr * (newDistance / this.lastDistance);
        //限制缩放倍率在1到5之间
        zoomFactorCurr = Math.max(zoomFactorCurr,1);
        zoomFactorCurr = Math.min(zoomFactorCurr,MAX_ZOOM_FACTOR);
        //如果非常接近1，则认作没有缩放(isZoomed=false），但zoomFactor记录的数值不重置为1
        isZoomed = !(this.zoomFactorCurr < ZOOM_SENSETIVITY_THRESHOLD);

        //只有在缩放过程以外的时候，才更新锚点手指的位置（x单位），因为缩放过程中view到x的矩阵一直在变化，导致锚点的x单位坐标也会一直变化
        if(!isEarlierZoomed)
            setAnchorWhenStart(x1, y1);

        //修改缩放矩阵
        if (!isZoomed) {
            resetZoom();
        } else {
            int[] xserverWH = viewOfXServer.getXScreenPixels();
            //这个是缩放之后可见的宽高，比如放大了那就比完整宽高要小(x单位）
            float zoomedW = (float) (xserverWH[0] / this.zoomFactorCurr);
            float zoomedH = (float) (xserverWH[1] / this.zoomFactorCurr);
            //应用新的裁切矩形，原点根据第一根手指的坐标调整一下，宽高根据两根手指的距离调整一下

            visibleRectangle.left = visibleRectangle.left + anchorWhenZoomStartInX.x - anchorLatestInX.x;
            visibleRectangle.top = visibleRectangle.top + anchorWhenZoomStartInX.y -anchorLatestInX.y;
            visibleRectangle.right = visibleRectangle.left + zoomedW;
            visibleRectangle.bottom = visibleRectangle.top + zoomedH;
            applyZoomRect(visibleRectangle);
        }

        //最后更新两指间距离
        this.lastDistance = newDistance;
    }

    /**
     * 缩放结束。这个函数好像没什么用。
     */
    @Override
    public void stop() {

    }

    /**
     * 直接设置缩放倍率。默认以屏幕中心为原点吧
     */
    public void setZoomFactor(double factor) {
        //TODO 模拟调用start()和update()，第一次传左上角和右下角，第二次传左上角*factor和右下角*factor？
    }

    public boolean isZoomed() {
        return isZoomed;
    }

    private void setAnchorWhenStart(float x, float y){
        float[] xPos = {x, y};
        viewOfXServer.getViewToXServerTransformationMatrix().mapPoints(xPos);
        this.anchorWhenZoomStartInX.set(xPos[0],xPos[1]);
    }

    private void setAnchorLatest(float x, float y) {
//        this.anchorLatestInX.set(x,y);
        float[] xPos = {x, y};
        viewOfXServer.getViewToXServerTransformationMatrix().mapPoints(xPos);
        this.anchorLatestInX.set(xPos[0],xPos[1]);
    }

    /**
     * 传入的时候可见区域的起始坐标和宽高（相对完整区域）
     * 接收一个可见区域，将其设置给view，
     * <br/>重新生成矩阵并设置给view
     */
    private void applyZoomRect(RectF newVisibleRect) {
        int[] viewWH = viewOfXServer.getAndroidViewPixels();
        Matrix TransMatrix = makeTransformationMatrix(viewWH[0], viewWH[1], newVisibleRect.left, newVisibleRect.top, newVisibleRect.width(), newVisibleRect.height(), viewOfXServer.getScaleStyle());
        TestHelper.assertTrue(TransMatrix.invert(TransMatrix), "xScreenRect is degenerate");
        this.viewOfXServer.setViewToXServerTransformationMatrix(TransMatrix);
        this.viewOfXServer.setXViewport(newVisibleRect.left, newVisibleRect.top, newVisibleRect.right, newVisibleRect.bottom);
    }

    /**
     * 清除当前缩放
     */
    public void resetZoom() {
        int[] xWH = viewOfXServer.getXScreenPixels();
        visibleRectangle.set(0, 0, xWH[0], xWH[1]);
        applyZoomRect(visibleRectangle);
    }


    private static Matrix makeTransformationMatrix(float viewW, float viewH, float visibleLeft, float visibleTop, float visibleWidth, float visibleHeight, @XServerViewHolder.ScaleStyle int scaleStyle) {
        //原makeTransformationDescription 函数内容

        float scaleX = viewW / visibleWidth;
        float scaleY = viewH / visibleHeight;
        float viewTranslateX = 0;
        float viewTranslateY = 0;
        float xServerTranslateX = 0 - visibleLeft;
        float xServerTranslateY = 0 - visibleTop;

        //如果不是拉伸全屏，那么就要等比全屏，给宽高统一缩放比例
        if (scaleStyle == SCALE_FULL_WITH_RATIO) {
            float minScale = Math.min(scaleX, scaleY);
            scaleX = minScale;
            scaleY = minScale;
        }

        //viewW和viewH，因为viewofxserver安卓视图一直是填充布局的，所以是个定值，
        //就是安卓屏幕宽/高 与 按比例等比（或拉伸）全屏后的宽高之差 。 如果不拉伸，乘以的scale能保证至少宽高有一个是铺满。缩放的话就是直接铺满
        if (scaleStyle == SCALE_FULL_WITH_RATIO) {
            viewTranslateX = (viewW - (visibleWidth * scaleX)) / 2.0f;
            viewTranslateY = (viewH - (visibleHeight * scaleY)) / 2.0f;
        }

        Log.d(TAG, String.format("makeTransformationMatrix: 可见区域：%f,%f,%f,%f. 矩阵变化：%f,%f,%f,%f,%f,%f"
                , visibleLeft, visibleTop, visibleWidth, visibleHeight,
                xServerTranslateX, xServerTranslateY, scaleX, scaleY, viewTranslateX, viewTranslateY));
        //makeTransformationMatrix原本内容
        Matrix matrix = new Matrix();
        matrix.postTranslate(xServerTranslateX, xServerTranslateY);
        matrix.postScale(scaleX, scaleY);
        matrix.postTranslate(viewTranslateX, viewTranslateY);
        return matrix;
    }

    public interface ZoomTarget {
        void setXViewport(float l, float t, float r, float b);

        Matrix getXServerToViewTransformationMatrix();

        Matrix getViewToXServerTransformationMatrix();

        void setViewToXServerTransformationMatrix(Matrix matrix);

        int[] getXScreenPixels();

        int[] getAndroidViewPixels();
    }

}
