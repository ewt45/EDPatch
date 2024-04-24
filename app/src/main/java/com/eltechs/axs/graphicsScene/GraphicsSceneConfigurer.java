package com.eltechs.axs.graphicsScene;

import com.eltechs.axs.geom.RectangleF;

/* loaded from: classes.dex */
public class GraphicsSceneConfigurer {
    private int nRectangles;
    private int nTextures;
    private RectangleF sceneViewport = new RectangleF(0.0f, 0.0f, 0.0f, 0.0f);

    public void setSceneViewport(RectangleF sceneViewport) {
        this.sceneViewport = sceneViewport;
    }

    public void setSceneViewport(float x, float y, float width, float height) {
        this.sceneViewport = new RectangleF(x, y, width, height);
    }

    public int addRectangle() {
        int old = this.nRectangles;
        this.nRectangles = old + 1;
        return old;
    }

    public int addTexture() {
        int old = this.nTextures;
        this.nTextures = old + 1;
        return old;
    }

    public SceneOfRectangles createScene() {
        SceneOfRectangles scene = new SceneOfRectangles(this.nRectangles, this.nTextures);
        scene.setSceneViewport(sceneViewport.x, sceneViewport.y, sceneViewport.width, sceneViewport.height);
        return scene;
    }
}