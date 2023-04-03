package com.eltechs.axs.graphicsScene.impl;

/* loaded from: classes.dex */
public abstract class TextureManagersFactory {
    private TextureManagersFactory() {
    }

    public static TexturesManager createTexturesManager() {
        return new TrivialTexturesManager();
    }
}