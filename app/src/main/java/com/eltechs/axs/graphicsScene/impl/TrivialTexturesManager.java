package com.eltechs.axs.graphicsScene.impl;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.GLHelpers;
import com.eltechs.axs.helpers.MathHelpers;
import com.eltechs.axs.xserver.impl.drawables.gl.PersistentGLDrawable;
import org.apache.commons.compress.archivers.tar.TarConstants;

/* loaded from: classes.dex */
public class TrivialTexturesManager implements TexturesManager {
    /** 记录每个texture的宽高（1个texture会连着占用两个位置，所以长度是textures数组的二倍） */
    private int[] textureSizes;
    /** 存储每个texture的id. 下标很迷，好像传进来的要么是0,1这种，要么是一个列表的长度。*/
    private int[] textures;

    private native void setTextureFromDrawableImpl15(int textureId, int width, int height, long content);

    private native void setTextureFromDrawableImpl16(int textureId, int width, int height, long content);

    private native void setTextureFromDrawableImpl32(int textureId, int width, int height, long content);

    static {
        System.loadLibrary("axs-helpers");
    }

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public boolean allocateTextures(int n) {
        Log.d("", "allocateTextures: 到底分配了几个texture"+n);
        freeTextures();
        this.textures = new int[n];
        this.textureSizes = new int[n * 2];
        if (n != 0) {
            GLES20.glGenTextures(n, this.textures, 0);
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
    public boolean setTextureSize(int i, int width, int height) {
        boolean haveNPot = GLHelpers.have_GL_OES_texture_npot();
        int widthUpperPOT = haveNPot ? width : MathHelpers.upperPOT(width);
        int heightUpperPOT = haveNPot ? height : MathHelpers.upperPOT(height);
        GLES20.glBindTexture(GL_TEXTURE_2D, this.textures[i]);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, widthUpperPOT, heightUpperPOT, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
        this.textureSizes[2 * i] = width;
        this.textureSizes[2 * i + 1] = height;
        return true;
    }

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public void getTextureSize(int i, int[] size) {
        int i2 = 2 * i;
        size[0] = this.textureSizes[i2];
        size[1] = this.textureSizes[i2 + 1];
    }

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public int getTextureId(int i) {
        return this.textures[i];
    }

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public void updateTextureFromDrawable(int i, PersistentGLDrawable persistentGLDrawable) {
        switch (persistentGLDrawable.getVisual().getDepth()) {
            case 1:
            case 32:
                setTextureFromDrawableImpl32(this.textures[i], persistentGLDrawable.getWidth(), persistentGLDrawable.getHeight(), persistentGLDrawable.getContent());
                return;
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

    @Override // com.eltechs.axs.graphicsScene.impl.TexturesManager
    public void updateTextureFromBitmap(int i, Bitmap bitmap) {
        GLES20.glBindTexture(GL_TEXTURE_2D, this.textures[i]);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
    }
}