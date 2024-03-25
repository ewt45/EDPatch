package com.eltechs.axs.widgets.viewOfXServer;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.eltechs.axs.configuration.XServerViewConfiguration;
import com.eltechs.axs.geom.RectangleF;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.UiThread;
import com.termux.x11.CmdEntryPoint;
import com.termux.x11.LorieView;
import com.termux.x11.ViewForRendering;
import com.eltechs.axs.xserver.ScreenInfo;
import com.eltechs.axs.xserver.ViewFacade;
import com.eltechs.axs.xserver.XServer;

/* loaded from: classes.dex */
public class ViewOfXServer extends FrameLayout {
    private final static String TAG = "ViewOfXServer";
    private static boolean isFirstStart = true;
    private final XServerViewConfiguration configuration;

    private final ViewFacade xServerFacade;

    //    private final WindowChangeListener windowChangeListener;
//    private final WindowLifecycleListener windowLifecycleListener;
//    private final WindowContentModificationListener contentModificationListener;
//    private final PointerListener pointerListener;
//    private final AXSRendererGL renderer = null;
    private final ViewForRendering viewForRendering;

    private Matrix transformationViewToXServer;
    private XZoomController zoomController;

    public ViewOfXServer(Context context, XServer xServer, ViewFacade viewFacade, XServerViewConfiguration xServerViewConfiguration) {
        super(context);
//        getHolder().setFormat(PixelFormat.RGBA_8888);
        this.configuration = xServerViewConfiguration;
        if (viewFacade == null) {
            this.xServerFacade = new ViewFacade(xServer);
        } else {
            this.xServerFacade = viewFacade;
        }
//        this.renderer = new AXSRendererGL(this, this.xServerFacade);
//        setRenderer(this.renderer);
//        setRenderMode(RENDERMODE_WHEN_DIRTY);
        this.transformationViewToXServer = new Matrix();
        this.zoomController = new XZoomController(this, xServer.getScreenInfo());
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置surfaceholder的callback
        viewForRendering = new ViewForRendering(context);

        addView(viewForRendering);

        //按照termux x11的方式启动xserver
//        Log.d(TAG, "ViewOfXServer: 与termux-x11 xserver连接");


        //试试换成TextureView
//        RealXServer.addCallback(viewForRendering);
    }

    /**
     * 获取surfaceview。用于activity将surface传给xserver
     *
     * @return surfaceview
     */
    public ViewForRendering getViewForRendering() {
        return viewForRendering;
    }


    public Matrix getViewToXServerTransformationMatrix() {
        Assert.notNull(this.transformationViewToXServer, "transformation matrix is not set");
        return this.transformationViewToXServer;
    }

    public void setViewToXServerTransformationMatrix(Matrix matrix) {
        Assert.notNull(this.transformationViewToXServer, "transformation matrix is not set");
        this.transformationViewToXServer = matrix;
    }

    public Matrix getXServerToViewTransformationMatrix() {
        Matrix matrix = new Matrix();
        getViewToXServerTransformationMatrix().invert(matrix);
        return matrix;
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        BaseInputConnection baseInputConnection = new BaseInputConnection(this, false);
        editorInfo.actionLabel = null;
        editorInfo.inputType = 0;
        editorInfo.imeOptions = 6;
        return baseInputConnection;
    }

    //        @Override // android.opengl.GLSurfaceView
    public void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume: 调用了invalidate");

        //重建时重新设置ViewForRendering
//        xegwConnection.setLorieView(viewForRendering);
//        xegwConnection.onConnect();

    }


    //        @Override // android.opengl.GLSurfaceView
    public void onPause() {
//        this.renderer.onPause();
//        super.onPause();
//        RealXServer.stop();
    }

    @Override // android.opengl.GLSurfaceView, android.view.SurfaceView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        this.xServerFacade.addWindowLifecycleListener(this.windowLifecycleListener);
//        this.xServerFacade.addWindowContentModificationListner(this.contentModificationListener);
//        this.xServerFacade.addWindowChangeListener(this.windowChangeListener);
//        this.xServerFacade.addPointerListener(this.pointerListener);
    }

    @Override // android.opengl.GLSurfaceView, android.view.SurfaceView, android.view.View
    protected void onDetachedFromWindow() {
//        this.xServerFacade.removeWindowLifecycleListener(this.windowLifecycleListener);
//        this.xServerFacade.removeWindowContentModificationListner(this.contentModificationListener);
//        this.xServerFacade.removeWindowChangeListener(this.windowChangeListener);
//        this.xServerFacade.removePointerListener(this.pointerListener);
        super.onDetachedFromWindow();
    }

    private void reinitRenderTransformation() {
        ScreenInfo screenInfo = this.xServerFacade.getScreenInfo();
        TransformationHelpers.makeTransformationMatrix(getWidth(), getHeight(), 0.0f, 0.0f, screenInfo.widthInPixels, screenInfo.heightInPixels, this.configuration.getFitStyleHorizontal(), this.configuration.getFitStyleVertical()).invert(this.transformationViewToXServer);
        this.zoomController = new XZoomController(this, this.xServerFacade.getScreenInfo());
//        this.zoomController.reInit(screenInfo);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //现在因为要通过调整安卓布局大小来实现缩放，所以要一直调用onSizeChanged，如果半道new一个的话，会出现成员变量还没初始化的情况。
        //只应该在初始时调用一次吧
//        if (oldw == 0 && oldh == 0) {
        Log.d(TAG, String.format("onSizeChanged: 布局变化，新建缩放控制器.w=%d,h=%d,getWidth=%d,getHeight=%d", w, h, getWidth(), getHeight()));
        //保证在CmdEntryPoint.connected()之后再设置缩放，看看还会不会出现拉伸问题？
        // 不行，切小窗时直接是connected，但还是会拉伸。必须无条件延迟0.5秒再执行
        // 切小窗时的具体情况是：切成小窗会比例全屏，点一下小窗放大一圈 此时错误拉伸。解决办法：LorieView的onMeasure删掉
        UiThread.postDelayed(500,()->{
            reinitRenderTransformation();
            zoomController.revertZoom();
        });
    }

    public ViewFacade getXServerFacade() {
        return this.xServerFacade;
    }

    /**
     * 这里可以设置缩放
     *
     * @param newVisibleRectF
     */
    public void setXViewport(RectangleF newVisibleRectF) {

        //获取ViewForRendering应该摆放的位置
        TransformationDescription trDesc = TransformationHelpers.makeTransformationDescription(
                getWidth(), getHeight(),
                newVisibleRectF.x, newVisibleRectF.y, newVisibleRectF.width, newVisibleRectF.height,
                configuration.getFitStyleHorizontal(), configuration.getFitStyleVertical());

//        if (viewForRendering.getLayoutParams() == null) {
//            return;
//        }
        FrameLayout.LayoutParams layoutParams = (LayoutParams) viewForRendering.getLayoutParams();


        /*
        margin计算：
        距离左侧偏移，就是显示根据viewTranslateX，偏移到正中央或者左右对齐或者拉伸，
        然后再放大scaleX倍，
        然后再根据xserverTranslateX偏移,因为已经放大了，倍率正好是view/visibleRect，所以再偏移xserverTranslateX*scaleX就行了

        宽高计算：
        宽度，首先通过裁切矩形宽 * scaleX（安卓宽除以裁切宽），达到全屏，然后再 * xserver完整宽/裁切矩形宽
         */

        int marginLeft = (int) (trDesc.viewTranslateX + trDesc.xServerTranslateX * trDesc.scaleX);
        int marginTop = (int) (trDesc.viewTranslateY + trDesc.xServerTranslateY * trDesc.scaleY);
//        layoutParams.width = (int) (newVisibleRectF.width * trDesc.scaleX * (xServerFacade.getScreenInfo().widthInPixels / newVisibleRectF.width));
//        layoutParams.height = (int) (newVisibleRectF.height * trDesc.scaleY * (xServerFacade.getScreenInfo().heightInPixels / newVisibleRectF.height));


//        //如果通过scale，width和height会变吗
//        viewForRendering.setScaleX((newVisibleRectF.width * trDesc.scaleX * (xServerFacade.getScreenInfo().widthInPixels / newVisibleRectF.width))/layoutParams.width);
//        viewForRendering.setScaleY((newVisibleRectF.height * trDesc.scaleY * (xServerFacade.getScreenInfo().heightInPixels / newVisibleRectF.height))/layoutParams.height);

//        layoutParams.setMargins(marginLeft, marginTop, 0, 0);
//        viewForRendering.setLayoutParams(layoutParams);

//        float stretchX = trDesc.scaleX * newVisibleRectF.width / getWidth();
//        float stretchY = trDesc.scaleY * newVisibleRectF.height / getHeight();
//        Log.d(TAG, "setXViewport: 拉伸系数=" + stretchX);

        Log.d(TAG, String.format("setXViewport: 设置缩放，修改layoutparams为：topMargin=%d, leftMargin=%d, width=%d, height =%d, 裁切矩形=%s, trDesc=%s", layoutParams.topMargin, layoutParams.leftMargin, layoutParams.width, layoutParams.height, newVisibleRectF, trDesc));

        //用动画，貌似缩放不会抖动了（在某次Xegw更新 限制帧率之后 又或者用了gles之后，缩放就会抖动）
        //xserver全宽 除以 可见矩形宽，是缩放x，不过还要注意一下拉伸系数


        viewForRendering.setPivotX(0);
        viewForRendering.setPivotY(0);

        viewForRendering.animate()

                .scaleX(trDesc.scaleX * xServerFacade.getScreenInfo().widthInPixels / getWidth())
                .scaleY(trDesc.scaleY * xServerFacade.getScreenInfo().heightInPixels / getHeight())
//                .scaleX(xServerFacade.)
                .x(marginLeft)
                .y(marginTop)
                .setDuration(0).start();

        Log.d("缩放", "setXViewport: scaleXY="+
                (trDesc.scaleX * xServerFacade.getScreenInfo().widthInPixels / getWidth())+", "
                +(trDesc.scaleY * xServerFacade.getScreenInfo().heightInPixels / getHeight())+", rectFrame="+viewForRendering.getHolder().getSurfaceFrame());

//        viewForRendering.getHolder().setFixedSize(xServerFacade.getScreenInfo().widthInPixels,xServerFacade.getScreenInfo().heightInPixels);
//        viewForRendering.regenerate();
//        this.renderer.setXViewport(newVisibleRectF);
//        requestRender();

    }


    public XZoomController getZoomController() {
        Assert.state(this.zoomController != null, "Zoom controller is not initialized");
        return this.zoomController;
    }

    public XServerViewConfiguration getConfiguration() {
        return this.configuration;
    }

    public final boolean isHorizontalStretchEnabled() {
        return this.configuration.getFitStyleHorizontal() == XServerViewConfiguration.FitStyleHorizontal.STRETCH;
    }

    public void setHorizontalStretchEnabled(boolean z) {
        if (z) {
            this.configuration.setFitStyleHorizontal(XServerViewConfiguration.FitStyleHorizontal.STRETCH);
        } else {
            this.configuration.setFitStyleHorizontal(XServerViewConfiguration.FitStyleHorizontal.CENTER);
        }
        if (isDegenerate()) {
            return;
        }
        reinitRenderTransformation();
        this.zoomController.revertZoom();
    }

    private boolean isDegenerate() {
        return getWidth() == 0 || getHeight() == 0;
    }

    public void freezeRenderer() {
        Log.d(TAG, "freezeRenderer: stub!");
//        if (this.renderer != null) {
//            this.renderer.freeze();
//        }
    }

    public void unfreezeRenderer() {
        Log.d(TAG, "unfreezeRenderer: stub!");
//        if (this.renderer != null) {
//            this.renderer.unFreeze();
//        }
    }


    public void test() {
        SurfaceTexture texture = new SurfaceTexture(1);
        SurfaceView surfaceView = new SurfaceView(getContext());
        Surface surface = new Surface(texture);
        TextureView textureView = new TextureView(getContext());
//        textureView.tra
        SurfaceTexture.OnFrameAvailableListener listener = new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                Log.d(TAG, "onFrameAvailable: ");
            }
        };

        texture.setOnFrameAvailableListener(listener);

        texture.attachToGLContext(2);
        texture.release();
        surface.release();

    }

}