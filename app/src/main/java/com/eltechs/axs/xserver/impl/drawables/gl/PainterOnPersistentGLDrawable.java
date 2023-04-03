package com.eltechs.axs.xserver.impl.drawables.gl;

import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.GraphicsContext;
import com.eltechs.axs.xserver.Painter;
import com.eltechs.axs.xserver.graphicsContext.PixelCompositionRule;

import java.nio.ByteBuffer;

public class PainterOnPersistentGLDrawable implements Painter {
    public static final int ALPHA_PIXEL = 0;
    public static final int BLACK_PIXEL = -16777216;
    public static final int WHITE_PIXEL = -1;
    private final PersistentGLDrawable drawable;
    private Drawable.ModificationListener modificationListener;

    public PainterOnPersistentGLDrawable(PersistentGLDrawable persistentGLDrawable) {
        this.drawable = persistentGLDrawable;
    }
    public void setModificationListener(Drawable.ModificationListener modificationListener) {
        this.modificationListener = modificationListener;
    }

    @Override
    public void copyArea(GraphicsContext graphicsContext, Drawable drawable, int i, int i2, int i3, int i4, int i5, int i6) {

    }

    @Override
    public void drawAlphaMaskedBitmap(Drawable drawable, Drawable drawable2, int i, int i2, int i3, int i4, int i5, int i6) {

    }

    @Override
    public void drawBitmap(int i, int i2, int i3, int i4, ByteBuffer byteBuffer) {

    }

    @Override
    public void drawFilledRectangles(ByteBuffer byteBuffer, int i) {

    }

    @Override
    public void drawLines(ByteBuffer byteBuffer, int i, int i2) {

    }

    @Override
    public void drawZPixmap(PixelCompositionRule pixelCompositionRule, byte b, int i, int i2, int i3, int i4, int i5, int i6, ByteBuffer byteBuffer, int i7, int i8) {

    }

    @Override
    public void fillWithColor(int i) {

    }

    @Override
    public byte[] getZPixmap(int i, int i2, int i3, int i4) {
        return new byte[0];
    }
}
