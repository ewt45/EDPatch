package com.eltechs.axs.graphicsScene.impl;

import android.graphics.Bitmap;

import com.eltechs.axs.xserver.impl.drawables.gl.PersistentGLDrawable;

public class TrivialTexturesManager implements TexturesManager {
    @Override
    public boolean allocateTextures(int i) {
        return false;
    }

    @Override
    public void freeTextures() {

    }

    @Override
    public int getTextureId(int i) {
        return 0;
    }

    @Override
    public void getTextureSize(int i, int[] iArr) {

    }

    @Override
    public boolean setTextureSize(int i, int i2, int i3) {
        return false;
    }

    @Override
    public void updateTextureFromBitmap(int i, Bitmap bitmap) {

    }

    @Override
    public void updateTextureFromDrawable(int i, PersistentGLDrawable persistentGLDrawable) {

    }
}
