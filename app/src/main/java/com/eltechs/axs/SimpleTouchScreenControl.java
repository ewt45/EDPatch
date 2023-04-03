package com.eltechs.axs;

import com.eltechs.axs.graphicsScene.SceneOfRectangles;

/* loaded from: classes.dex */
public class SimpleTouchScreenControl implements TouchScreenControl {
    private final TouchArea[] touchAreas;
    private final TouchScreenControlVisualizer[] visualizers;

    public SimpleTouchScreenControl(TouchArea[] touchAreaArr, TouchScreenControlVisualizer[] touchScreenControlVisualizerArr) {
        this.touchAreas = touchAreaArr;
        this.visualizers = touchScreenControlVisualizerArr == null ? new PointerVisualizer[0] : touchScreenControlVisualizerArr;
    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void attachedToGLContext(SceneOfRectangles sceneOfRectangles) {
        for (TouchScreenControlVisualizer touchScreenControlVisualizer : this.visualizers) {
            touchScreenControlVisualizer.attachedToGLContext(sceneOfRectangles);
        }
    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void detachedFromGLContext() {
        for (TouchScreenControlVisualizer touchScreenControlVisualizer : this.visualizers) {
            touchScreenControlVisualizer.detachedFromGLContext();
        }
    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void handleFingerDown(Finger finger) {
        for (TouchArea touchArea : this.touchAreas) {
            touchArea.handleFingerDown(finger);
        }
    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void handleFingerUp(Finger finger) {
        for (TouchArea touchArea : this.touchAreas) {
            touchArea.handleFingerUp(finger);
        }
    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void handleFingerMove(Finger finger) {
        for (TouchArea touchArea : this.touchAreas) {
            touchArea.handleFingerMove(finger);
        }
    }
}