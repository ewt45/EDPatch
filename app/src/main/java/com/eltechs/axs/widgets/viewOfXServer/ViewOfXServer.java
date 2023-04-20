package com.eltechs.axs.widgets.viewOfXServer;

import android.content.Context;
import android.graphics.Matrix;
import android.view.SurfaceView;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import com.eltechs.axs.configuration.XServerViewConfiguration;
import com.eltechs.axs.geom.RectangleF;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.PointerListener;
import com.eltechs.axs.xserver.RealXServer;
import com.eltechs.axs.xserver.ScreenInfo;
import com.eltechs.axs.xserver.ViewFacade;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowAttributeNames;
import com.eltechs.axs.xserver.WindowChangeListener;
import com.eltechs.axs.xserver.WindowContentModificationListener;
import com.eltechs.axs.xserver.WindowLifecycleListener;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public class ViewOfXServer extends SurfaceView {
    private final XServerViewConfiguration configuration;
//    private final WindowContentModificationListener contentModificationListener;
//    private final PointerListener pointerListener;
//    private final AXSRendererGL renderer;
    private Matrix transformationViewToXServer;
//    private final WindowChangeListener windowChangeListener;
//    private final WindowLifecycleListener windowLifecycleListener;
    private final ViewFacade xServerFacade;
    private XZoomController zoomController;


    public ViewOfXServer(Context context, XServer xServer, ViewFacade viewFacade, XServerViewConfiguration xServerViewConfiguration) {
        super(context);
        getHolder().setFormat(1);
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
        RealXServer.addCallback(this);
        RealXServer.start();
    }

    public Matrix getViewToXServerTransformationMatrix() {
        Assert.notNull(this.transformationViewToXServer, "transformation matrix is not set");
        return this.transformationViewToXServer;
    }

    public Matrix getXServerToViewTransformationMatrix() {
        Matrix matrix = new Matrix();
        getViewToXServerTransformationMatrix().invert(matrix);
        return matrix;
    }

    public void setViewToXServerTransformationMatrix(Matrix matrix) {
        Assert.notNull(this.transformationViewToXServer, "transformation matrix is not set");
        this.transformationViewToXServer = matrix;
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        BaseInputConnection baseInputConnection = new BaseInputConnection(this, false);
        editorInfo.actionLabel = null;
        editorInfo.inputType = 0;
        editorInfo.imeOptions = 6;
        return baseInputConnection;
    }

//    @Override // android.opengl.GLSurfaceView
    public void onResume() {
//        super.onResume();
    }

//    @Override // android.opengl.GLSurfaceView
    public void onPause() {
//        this.renderer.onPause();
//        super.onPause();
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
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        reinitRenderTransformation();
    }

    public ViewFacade getXServerFacade() {
        return this.xServerFacade;
    }

    public void setXViewport(RectangleF rectangleF) {
//        this.renderer.setXViewport(rectangleF);
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
//        if (this.renderer != null) {
//            this.renderer.freeze();
//        }
    }

    public void unfreezeRenderer() {
//        if (this.renderer != null) {
//            this.renderer.unFreeze();
//        }
    }

//
//    DrawThread mDrawThread;
//    //surfaceholder的callback
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        mDrawThread = new DrawThread(holder.getSurface());
//        mDrawThread.start();
//
//        //
//        X11ServerHelpers.start();
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        X11ServerHelpers.windowChanged(holder.getSurface(),width,height);
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        mDrawThread.stopDraw();
//        try {
//            mDrawThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 渲染画面的线程，
//     */
//    private static class DrawThread extends Thread {
//        private final Surface mSurface;
//        private boolean mRunning = true;
//        private final Paint mPaint = new Paint();
//
//        DrawThread(Surface surface) {
//            mSurface = surface;
//            mPaint.setColor(Color.RED);
//        }
//
//        void stopDraw() {
//            mRunning = false;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//
//            int left = 0;
//            int top = 0;
//            int width = 100;
//            int height = 100;
//            int verticalFlag = 5;
//            int horizontalFlag = 5;
//            while (mRunning) {
//                Canvas canvas = mSurface.lockCanvas(null);
//                canvas.drawColor(Color.WHITE);
//                canvas.drawRect(left, top, left + width, top + height, mPaint);
//                mSurface.unlockCanvasAndPost(canvas);
////                Log.d(TAG, String.format("run: canvas.width=%d, height=%d",canvas.getWidth(),canvas.getHeight()));
//
//
//                if(((top + height) >= 400) || top<0)
//                    verticalFlag = - verticalFlag;
//
//                if(((left + width) >= 800) || left<0)
//                    horizontalFlag = - horizontalFlag;
//
//                left += horizontalFlag;
//                top += verticalFlag;
////                try {
////                    Thread.sleep(50);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//            }
//        }
//    }
}