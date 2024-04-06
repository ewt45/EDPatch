package com.eltechs.axs.graphicsScene.impl;

import android.graphics.Bitmap;
import com.eltechs.axs.xserver.impl.drawables.gl.PersistentGLDrawable;

/* loaded from: classes.dex */
public interface TexturesManager {
    boolean allocateTextures(int i);

    void freeTextures();

    int getTextureId(int i);

    void getTextureSize(int i, int[] size);

    boolean setTextureSize(int i, int width, int height);

    void updateTextureFromBitmap(int i, Bitmap bitmap);

    void updateTextureFromDrawable(int i, PersistentGLDrawable persistentGLDrawable);
}