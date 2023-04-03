package com.eltechs.axs;

import com.eltechs.axs.graphicsScene.GraphicsSceneConfigurer;
import com.eltechs.axs.graphicsScene.SceneOfRectangles;
import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class TouchScreenControls {
    private SceneOfRectangles graphicsScene;
    private GraphicsSceneConfigurer graphicsSceneConfigurer;
    private final Collection<TouchScreenControl> touchScreenControls = new ArrayList<>();

    public TouchScreenControls(GraphicsSceneConfigurer graphicsSceneConfigurer) {
        this.graphicsSceneConfigurer = graphicsSceneConfigurer;
    }

    public void add(TouchScreenControl touchScreenControl) {
        this.touchScreenControls.add(touchScreenControl);
    }

    public void attachedToGLContext() {
        this.graphicsScene = this.graphicsSceneConfigurer.createScene();
        for (TouchScreenControl touchScreenControl : this.touchScreenControls) {
            touchScreenControl.attachedToGLContext(this.graphicsScene);
        }
    }

    public void detachedFromGLContext() {
        if (this.graphicsScene != null) {
            for (TouchScreenControl touchScreenControl : this.touchScreenControls) {
                touchScreenControl.detachedFromGLContext();
            }
            this.graphicsScene.destroy();
            this.graphicsScene = null;
        }
    }

    public void draw() {
        if (this.graphicsScene != null) {
            this.graphicsScene.draw();
        }
    }

    public void handleFingerDown(Finger finger) {
        for (TouchScreenControl touchScreenControl : this.touchScreenControls) {
            touchScreenControl.handleFingerDown(finger);
        }
    }

    public void handleFingerUp(Finger finger) {
        for (TouchScreenControl touchScreenControl : this.touchScreenControls) {
            touchScreenControl.handleFingerUp(finger);
        }
    }

    public void handleFingerMove(Finger finger) {
        for (TouchScreenControl touchScreenControl : this.touchScreenControls) {
            touchScreenControl.handleFingerMove(finger);
        }
    }
}