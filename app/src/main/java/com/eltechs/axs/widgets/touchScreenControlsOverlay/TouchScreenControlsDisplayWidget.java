package com.eltechs.axs.widgets.touchScreenControlsOverlay;

import android.opengl.GLSurfaceView;
import com.eltechs.axs.TouchScreenControls;
import com.eltechs.axs.activities.XServerDisplayActivity;

/* loaded from: classes.dex */
public class TouchScreenControlsDisplayWidget extends GLSurfaceView {
    private final TouchScreenControlsRenderer renderer;

    public TouchScreenControlsDisplayWidget(XServerDisplayActivity xServerDisplayActivity) {
        super(xServerDisplayActivity);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(1);
        this.renderer = new TouchScreenControlsRenderer();
        setRenderer(this.renderer);
        setRenderMode(1);
    }

    public void setTouchScreenControls(TouchScreenControls touchScreenControls) {
        this.renderer.setTouchScreenControls(touchScreenControls);
    }

    @Override // android.opengl.GLSurfaceView
    public void onResume() {
        super.onResume();
    }

    @Override // android.opengl.GLSurfaceView
    public void onPause() {
        this.renderer.onPause();
        super.onPause();
    }
}