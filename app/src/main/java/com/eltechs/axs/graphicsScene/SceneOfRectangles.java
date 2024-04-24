package com.eltechs.axs.graphicsScene;

import android.graphics.Bitmap;
import android.opengl.Matrix;
import android.util.Log;

import com.eltechs.axs.annotations.UsedByNativeCode;
import com.eltechs.axs.geom.RectangleF;
import com.eltechs.axs.graphicsScene.impl.TextureManagersFactory;
import com.eltechs.axs.graphicsScene.impl.TexturesManager;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.GLHelpers;
import com.eltechs.axs.helpers.MathHelpers;
import com.eltechs.axs.helpers.ShaderHelpers;
import com.eltechs.axs.xserver.impl.drawables.gl.PersistentGLDrawable;

/* loaded from: classes.dex */
public class SceneOfRectangles {
    private static final float DEPTH_OF_SCENE = 1024.0f;

    static {
        System.loadLibrary("axs-helpers");
        Assert.state(initialiseNativeParts(), "Managed and native parts of SceneOfRectangles do not match one another.");
    }

    private final int nRectangles;
    private final int nTextures;
    private final float[] imageTransformationMatrix = new float[16];
    private final float[] viewportAdjustmentMatrix = new float[16];
    private final boolean have_GL_OES_texture_npot = GLHelpers.have_GL_OES_texture_npot();
    private final TexturesManager texturesManager = TextureManagersFactory.createTexturesManager();
    @UsedByNativeCode
    private long sceneData;
    @UsedByNativeCode
    private int texturerStaticAlpha = createTexturer(true);
    @UsedByNativeCode
    private int texturerDynamicAlpha = createTexturer(false);

    public SceneOfRectangles(int nRectangles, int nTextures) {
        this.nRectangles = nRectangles;
        this.nTextures = nTextures;
        Matrix.setIdentityM(this.imageTransformationMatrix, 0);
        Matrix.setIdentityM(this.viewportAdjustmentMatrix, 0);
        this.texturesManager.allocateTextures(nTextures);
        allocateNativeSceneData(nRectangles);
    }

    private static native boolean initialiseNativeParts();

    private static int createTexturer(boolean z) {
        return ShaderHelpers.createAndLinkProgram(
                ShaderHelpers.compileShader(ShaderHelpers.ShaderType.VERTEX,
                        "uniform mat4 u_MVP;                                        \n" +
                                "attribute vec4 a_Position;                                 \n" +
                                "attribute vec2 a_TexCoordinate;                            \n" +
                                "varying vec2 v_TexCoordinate;                              \n" +
                                "void main()                                                \n" +
                                "{                                                          \n" +
                                "   v_TexCoordinate = a_TexCoordinate;                      \n" +
                                "   gl_Position = u_MVP * a_Position;                       \n" +
                                "}                                                          \n"),
                ShaderHelpers.compileShader(ShaderHelpers.ShaderType.FRAGMENT, z
                        ? "precision mediump float;                                   \n" +
                        "uniform sampler2D u_Texture;                               \n" +
                        "uniform float u_Alpha;varying vec2 v_TexCoordinate;                              \n" +
                        "void main()                                                \n" +
                        "{                                                          \n" +
                        "   vec4 tc = texture2D(u_Texture, v_TexCoordinate);        \n" +
                        "   gl_FragColor = vec4(tc.rgb, u_Alpha);}                                                          \n"

                        : "precision mediump float;                                   \n" +
                        "uniform sampler2D u_Texture;                               \n" +
                        "uniform float u_Alpha;varying vec2 v_TexCoordinate;                              \n" +
                        "void main()                                                \n" +
                        "{                                                          \n" +
                        "   vec4 tc = texture2D(u_Texture, v_TexCoordinate);        \n" +
                        "   gl_FragColor = vec4(tc.rgb, u_Alpha * tc.a);            \n" +
                        "}                                                          \n"),
                new String[]{"a_Position", "a_TexCoordinate"});
    }

    private native void allocateNativeSceneData(int i);

    private native void freeNativeSceneData();

    private native void moveRectangleImpl(int i, float f, float f2, float f3, float f4, float f5);

    private native void placeRectangleImpl(int i, float f, float f2, float f3, float f4, float f5, int i2, float f6, float f7, float f8, boolean z);

    private native void setMVPMatrix(float[] fArr);

    public native synchronized void draw();

    public void destroy() {
        this.texturesManager.freeTextures();
        freeNativeSceneData();
    }

    public synchronized void setViewport(float f, float f2, float scaleFactorX, float scaleFactorY) {
        Matrix.setIdentityM(this.viewportAdjustmentMatrix, 0);
        Matrix.translateM(this.viewportAdjustmentMatrix, 0, this.viewportAdjustmentMatrix, 0, (2.0f * f) - 1.0f, ((-2.0f) * f2) + 1.0f, 0.0f);
        Matrix.scaleM(this.viewportAdjustmentMatrix, 0, this.viewportAdjustmentMatrix, 0, scaleFactorX, scaleFactorY, 1.0f);
        Matrix.translateM(this.viewportAdjustmentMatrix, 0, this.viewportAdjustmentMatrix, 0, 1.0f, -1.0f, 0.0f);
        updateMVPMatrix();
    }

    /**
     * 设置可见区域。x向右为正，y貌似比较特殊是向上为正
     */
    public synchronized void setSceneViewport(float x, float y, float width, float height) {
        Matrix.orthoM(this.imageTransformationMatrix, 0, x, x + width, y - height, y, DEPTH_OF_SCENE, -DEPTH_OF_SCENE);
        updateMVPMatrix();
    }

    public synchronized void setSceneViewport(RectangleF rectangleF) {
        setSceneViewport(rectangleF.x, rectangleF.y, rectangleF.width, rectangleF.height);
    }

    private void updateMVPMatrix() {
        float[] fArr = new float[16];
        System.arraycopy(this.imageTransformationMatrix, 0, fArr, 0, 16);
        Matrix.multiplyMM(fArr, 0, this.viewportAdjustmentMatrix, 0, fArr, 0);
        setMVPMatrix(fArr);
    }

    public synchronized void setTextureSize(int i, int width, int height) {
        Assert.isTrue(i < this.nTextures, "Invalid texture number.");
        this.texturesManager.setTextureSize(i, width, height);
    }

    /**
     * 注意y是向上为正
     */
    public synchronized void placeRectangle(int rect, float x, float y, float width, float height, float f5, int textureIdx, float f6, boolean z) {
        Assert.isTrue(rect < this.nRectangles, "Invalid rectangle number");
        Assert.isTrue(textureIdx < this.nTextures, "Invalid texture number");
        float widthScale;
        float heightScale;
        if (this.have_GL_OES_texture_npot) {
            widthScale = 1.0f;
            heightScale = 1.0f;
        } else {
            //如果不支持OES_texture_npot，就要变为2次幂
            int[] textureWH = new int[2];
            this.texturesManager.getTextureSize(textureIdx, textureWH);
            widthScale = (float) textureWH[0] / MathHelpers.upperPOT(textureWH[0]);
            heightScale = (float) textureWH[1] / MathHelpers.upperPOT(textureWH[1]);
        }
        placeRectangleImpl(rect, x, y, width, height, f5, this.texturesManager.getTextureId(textureIdx), widthScale, heightScale, f6, z);
    }
    /**
     * 注意y是向上为正
     */
    public synchronized void moveRectangle(int rect, float x, float y, float width, float height, float drawable) {
        Assert.isTrue(rect < this.nRectangles, "Invalid rectangle number");
        moveRectangleImpl(rect, x, y, width, height, drawable);
    }

    public synchronized void updateTextureFromDrawable(int i, PersistentGLDrawable persistentGLDrawable) {
        Assert.isTrue(i < this.nTextures, "Invalid texture number");
        this.texturesManager.updateTextureFromDrawable(i, persistentGLDrawable);
    }

    public synchronized void updateTextureFromBitmap(int i, Bitmap bitmap) {
        Assert.isTrue(i < this.nTextures, "Invalid texture number");
        this.texturesManager.updateTextureFromBitmap(i, bitmap);
    }

    public synchronized void setTextureFromBitmap(int i, Bitmap bitmap) {
        Assert.isTrue(i < this.nTextures, "Invalid texture number");
        this.texturesManager.setTextureSize(i, bitmap.getWidth(), bitmap.getHeight());
        this.texturesManager.updateTextureFromBitmap(i, bitmap);
    }
}