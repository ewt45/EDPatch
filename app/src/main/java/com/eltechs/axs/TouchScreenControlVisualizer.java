package com.eltechs.axs;

import com.eltechs.axs.graphicsScene.SceneOfRectangles;

/* loaded from: classes.dex */
public interface TouchScreenControlVisualizer {
    void attachedToGLContext(SceneOfRectangles sceneOfRectangles);

    void detachedFromGLContext();
}