//package com.example.datainsert.exagear.controlsV2;
//
//import static com.example.datainsert.exagear.controlsV2.XServerViewHolder.SCALE_FULL_WITH_RATIO;
//
//import android.graphics.Matrix;
//import android.graphics.PointF;
//import android.util.Log;
//
//import com.eltechs.axs.GeometryHelpers;
//
///**
// * 内部存一个viewOfXServer。
// * <br/> 用于负责画面缩放的具体实现，因此内部会调用一些XServerViewHolder的方法，如果实现了这些方法则会缩放，没实现也就是没反应而已。
// * <br/> xserver内 sizeChange的时候还会重建ZoomController，会不会有冲突？（还有matrix）
// * <br/> x和安卓的完整宽高 实时获取，holder没有单独数据类记录宽高的，如果不实时，更新了的话这里可能不是最新的
// * <br/> 应该实现的方法：start(float[] f1, float[] f2); update(float[] f1, float[] f2); setZoomFactor(double);
// * <br/> viewOfXServer的那几个函数要不要改成Controller的接口，比如getScaleStyle，setMatrix，setViewport之类的
// */
//public class XZoomController2 {
//    private static final String TAG = "XZoomController";
//    private static final float MAX_ZOOM_FACTOR = 5.0f;
//    private static final float ZOOM_SENSETIVITY_THRESHOLD = 1.005f;
//    private final XServerViewHolder viewOfXServer;
//    private final PointF point1 = new PointF();
//    private final PointF point2 = new PointF();
//    private final Matrix matrixV2XWhenStart = new Matrix();
//    private final PointF pointCenterWhenStart = new PointF();
//    /**
//     * 实际显示的部分，对xserver的裁切矩形
//     */
//    private double zoomFactorCurr = 1.0d;
//    private double zoomFactorWhenStart = zoomFactorCurr; //调用start()时的缩放比例，之后update时依据这个来调整
//    private float distanceStart; //start()时，两根手指的距离
//
//    public XZoomController2(XServerViewHolder viewOfXServer) {
//        this.viewOfXServer = viewOfXServer;
//    }
//
//    /**
//     * 缩放开始。传入此时的两根手指的坐标(event中获取）
//     * <br/> 函数内部会记录此时的缩放倍率，之后的update()都在此基础上调整
//     */
//    public void start(float x1, float y1, float x2, float y2) {
//        //TODO factor是自己内部维护，还是从matrix中获取？（内部维护，因为matrix中缩放的含义不同（有可能拉伸）
//        //记录起始时两根手指坐标
//        point1.set(x1, y1);
//        point2.set(x2, y2);
//
//        //记录起始时两根手指中心位置
//        distanceStart = GeometryHelpers.distance(x1, y1, x2, y2);
//        matrixV2XWhenStart.set(viewOfXServer.getViewToXServerTransformationMatrix());
//
//        float[] pointNowCenter = new float[]{(x1 + x2) / 2, (y1 + y2) / 2};
//        viewOfXServer.getViewToXServerTransformationMatrix().mapPoints(pointNowCenter);
//        pointCenterWhenStart.x = pointNowCenter[0];
//        pointCenterWhenStart.y = pointNowCenter[1];
//
//        //记录起始时缩放倍率
//        zoomFactorWhenStart = zoomFactorCurr;
//    }
//
//    /**
//     * 缩放更新，传入传入此时的两根手指的坐标(event中获取）。函数内部会重新计算缩放倍率和当前应显示的画面区域，并将此区域提交给viewOfXServer
//     */
//    public void update(float nowX1, float nowY1, float nowX2, float nowY2) {
//        //0. 定位中心点：两指的中心（如果用绝对位置，要换算到x的坐标，否则没有意义）
//        //TODO 如果重复进入这里，即使手指位置没变，也会因为矩阵变了而导致中心变了，然后后面全都跟着变了（试试用起始时的矩阵？）
//        // 不行啊这个概念搞不明白，要不还是用原来的吧
//        float[] pointNowCenter = new float[]{(nowX1 + nowX2) / 2, (nowY1 + nowY2) / 2};
//        viewOfXServer.getViewToXServerTransformationMatrix().mapPoints(pointNowCenter);
//
//        //1. 计算新的zoomfactor（以按下时的为基准）
//        zoomFactorCurr = zoomFactorWhenStart * GeometryHelpers.distance(nowX1, nowY1, nowX2, nowY2) / distanceStart;
//
//        //2. 调整缩放倍率，并应用缩放
//        if (zoomFactorCurr < ZOOM_SENSETIVITY_THRESHOLD)
//            zoomFactorCurr = 1d;
//        else if (zoomFactorCurr > MAX_ZOOM_FACTOR)
//            zoomFactorCurr = MAX_ZOOM_FACTOR;
//
//        //新的可见窗口，左边到 新的中心点-新的可见宽度/2 ，上面到 新的中心点-新的可见高度/2
//        int[] xFullWH = viewOfXServer.getXScreenPixels();
//        float zoomedWidth = (float) (xFullWH[0] / zoomFactorCurr);
//        float zoomedHeight = (float) (xFullWH[1] / zoomFactorCurr);
//
//
//        applyZoomRect(pointCenterWhenStart.x + pointCenterWhenStart.x - pointNowCenter[0] - zoomedWidth / 2,
//                pointCenterWhenStart.y + pointCenterWhenStart.y - pointNowCenter[1] - zoomedHeight / 2,
//                zoomedWidth, zoomedHeight);
//    }
//
//    /**
//     * 缩放结束。这个函数好像没什么用。
//     */
//    public void stop() {
//
//    }
//
//    /**
//     * 直接设置缩放倍率。默认以屏幕中心为原点吧
//     */
//    public void setZoomFactor(double factor) {
//        //TODO 模拟调用start()和update()，第一次传左上角和右下角，第二次传左上角*factor和右下角*factor？
//    }
//
//    public boolean isZoomed() {
//        return zoomFactorCurr != 1;
//    }
//
//
//    /**
//     * 清除当前缩放
//     */
//    public void resetZoom() {
//        int[] xWH = viewOfXServer.getXScreenPixels();
//        applyZoomRect(0.0f, 0.0f, xWH[0], xWH[1]);
//    }
//
//    /**
//     * 传入的时候可见区域的起始坐标和宽高（相对完整区域）
//     * 接收一个可见区域，将其设置给view，
//     * <br/>重新生成矩阵并设置给view
//     */
//    private void applyZoomRect(float x, float y, float w, float h) {
//        int[] viewWH = viewOfXServer.getAndroidViewPixels();
//        Matrix TransMatrix = makeTransformationMatrix(viewWH[0], viewWH[1], x, y, w, h, viewOfXServer.getScaleStyle());
//        TransMatrix.invert(TransMatrix);
//        this.viewOfXServer.setViewToXServerTransformationMatrix(TransMatrix);
//        this.viewOfXServer.setXViewport(x, y, w, h);
//    }
//
//    private Matrix makeTransformationMatrix(float viewW, float viewH, float visibleLeft, float visibleTop, float visibleWidth, float visibleHeight, @XServerViewHolder.ScaleStyle int scaleStyle) {
//        //原makeTransformationDescription 函数内容
//
//        float scaleX = viewW / visibleWidth;
//        float scaleY = viewH / visibleHeight;
//        float viewTranslateX = 0;
//        float viewTranslateY = 0;
//        float xServerTranslateX = 0 - visibleLeft;
//        float xServerTranslateY = 0 - visibleTop;
//
//        //如果不是拉伸全屏，那么就要等比全屏，给宽高统一缩放比例
//        if (scaleStyle == SCALE_FULL_WITH_RATIO) {
//            float minScale = Math.min(scaleX, scaleY);
//            scaleX = minScale;
//            scaleY = minScale;
//        }
//
//        //viewW和viewH，因为viewofxserver安卓视图一直是填充布局的，所以是个定值，
//        //就是安卓屏幕宽/高 与 按比例等比（或拉伸）全屏后的宽高之差 。 如果不拉伸，乘以的scale能保证至少宽高有一个是铺满。缩放的话就是直接铺满
//        float widthDiff = viewW - (visibleWidth * scaleX);
//        float heightDiff = viewH - (visibleHeight * scaleY);
//        if (scaleStyle == SCALE_FULL_WITH_RATIO) {
//            viewTranslateX = widthDiff / 2.0f;
//            viewTranslateY = heightDiff / 2.0f;
//        }
//
//        Log.d(TAG, String.format("makeTransformationMatrix: 可见区域：%f,%f,%f,%f. 矩阵变化：%f,%f,%f,%f,%f,%f"
//                , visibleLeft, visibleTop, visibleWidth, visibleHeight,
//                xServerTranslateX, xServerTranslateY, scaleX, scaleY, viewTranslateX, viewTranslateY));
//        //makeTransformationMatrix原本内容
//        Matrix matrix = new Matrix();
//        matrix.postTranslate(xServerTranslateX, xServerTranslateY);
//        matrix.postScale(scaleX, scaleY);
//        matrix.postTranslate(viewTranslateX, viewTranslateY);
//        return matrix;
//
//
////        TransformationDescription makeTransformationDescription = makeTransformationDescription(
////                viewW, viewH, visibleLeft, visibleTop, visibleRight-visibleLeft, visibleBottom-visibleTop, fitStyleHorizontal, fitStyleVertical);
////        Matrix matrix = new Matrix();
////        matrix.postTranslate(makeTransformationDescription.xServerTranslateX, makeTransformationDescription.xServerTranslateY);
////        matrix.postScale(makeTransformationDescription.scaleX, makeTransformationDescription.scaleY);
////        matrix.postTranslate(makeTransformationDescription.viewTranslateX, makeTransformationDescription.viewTranslateY);
////        return matrix;
//    }
//
//    public interface ZoomTarget {
//        void setXViewport(float l, float t, float r, float b);
//
//        Matrix getXServerToViewTransformationMatrix();
//
//        Matrix getViewToXServerTransformationMatrix();
//
//        void setViewToXServerTransformationMatrix(Matrix matrix);
//
//        int[] getXScreenPixels();
//
//        int[] getAndroidViewPixels();
//    }
//
//}
