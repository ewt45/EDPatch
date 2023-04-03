package com.eltechs.axs;

import com.eltechs.axs.graphicsScene.SceneOfRectangles;

/* loaded from: classes.dex */
public class PointerVisualizer implements PointerEventListener, TouchScreenControlVisualizer {
    private final int color;
    private float x;
    private float y;

    @Override // com.eltechs.axs.TouchScreenControlVisualizer
    public void attachedToGLContext(SceneOfRectangles sceneOfRectangles) {
    }

    @Override // com.eltechs.axs.TouchScreenControlVisualizer
    public void detachedFromGLContext() {
    }

    public PointerVisualizer(int i) {
        this.color = i;
    }

    private void updatePointer(float f, float f2) {
        this.x = f;
        this.y = f2;
    }

    @Override // com.eltechs.axs.PointerEventListener
    public void pointerEntered(float f, float f2) {
        updatePointer(f, f2);
    }

    @Override // com.eltechs.axs.PointerEventListener
    public void pointerExited(float f, float f2) {
        updatePointer(f, f2);
    }

    @Override // com.eltechs.axs.PointerEventListener
    public void pointerMove(float f, float f2) {
        updatePointer(f, f2);
    }
}