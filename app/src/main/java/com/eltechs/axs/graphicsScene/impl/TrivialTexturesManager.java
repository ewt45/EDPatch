package com.eltechs.axs.graphicsScene.impl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.GLHelpers;
import com.eltechs.axs.helpers.MathHelpers;
import com.eltechs.axs.xserver.impl.drawables.gl.PersistentGLDrawable;
import org.apache.commons.compress.archivers.tar.TarConstants;

/* loaded from: classes.dex */
public class TrivialTexturesManager implements TexturesManager {
    private int[] textureSizes;
    private int[] textures;

    private native void setTextureFromDrawableImpl15(int i, int i2, int i3, long j);

    private native void setTextureFromDrawableImpl16(int i, int i2, int i3, long j);

    private native void setTextureFromDrawableImpl32(int i, int i2, int i3, long j);

    static {
        System.loadLibrary("axs-helpers");
    }

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public boolean allocateTextures(int i) {
        freeTextures();
        this.textures = new int[i];
        this.textureSizes = new int[i * 2];
        if (i != 0) {
            GLES20.glGenTextures(i, this.textures, 0);
            return true;
        }
        return true;
    }

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public void freeTextures() {
        if (this.textures != null) {
            GLES20.glDeleteTextures(this.textures.length, this.textures, 0);
            this.textures = null;
        }
    }

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public boolean setTextureSize(int i, int i2, int i3) {
        int upperPOT;
        int upperPOT2;
        if (GLHelpers.have_GL_OES_texture_npot()) {
            upperPOT = i2;
            upperPOT2 = i3;
        } else {
            upperPOT = MathHelpers.upperPOT(i2);
            upperPOT2 = MathHelpers.upperPOT(i3);
        }
        GLES20.glBindTexture(3553, this.textures[i]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, TarConstants.DEFAULT_BLKSIZE, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6408, upperPOT, upperPOT2, 0, 6408, 5121, null);
        int i4 = 2 * i;
        this.textureSizes[i4 + 0] = i2;
        this.textureSizes[i4 + 1] = i3;
        return true;
    }

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public void getTextureSize(int i, int[] iArr) {
        int i2 = 2 * i;
        iArr[0] = this.textureSizes[i2];
        iArr[1] = this.textureSizes[i2 + 1];
    }

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public int getTextureId(int i) {
        return this.textures[i];
    }

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public void updateTextureFromDrawable(int i, PersistentGLDrawable persistentGLDrawable) {
        int depth = persistentGLDrawable.getVisual().getDepth();
        if (depth != 1 && depth != 32) {
            switch (depth) {
                case 15:
                    setTextureFromDrawableImpl15(this.textures[i], persistentGLDrawable.getWidth(), persistentGLDrawable.getHeight(), persistentGLDrawable.getContent());
                    return;
                case 16:
                    setTextureFromDrawableImpl16(this.textures[i], persistentGLDrawable.getWidth(), persistentGLDrawable.getHeight(), persistentGLDrawable.getContent());
                    return;
                default:
                    Assert.notImplementedYet(String.format("Unsupported depth %s.", persistentGLDrawable.getVisual().getDepth()));
                    return;
            }
        }
        setTextureFromDrawableImpl32(this.textures[i], persistentGLDrawable.getWidth(), persistentGLDrawable.getHeight(), persistentGLDrawable.getContent());
    }

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public void updateTextureFromBitmap(int i, Bitmap bitmap) {
        GLES20.glBindTexture(3553, this.textures[i]);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
    }
}