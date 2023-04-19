package com.eltechs.axs.xserver.impl.drawables.gl;

import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.GraphicsContext;
import com.eltechs.axs.xserver.Painter;
import com.eltechs.axs.xserver.graphicsContext.PixelCompositionRule;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class PainterOnPersistentGLDrawable implements Painter {
    public static final int ALPHA_PIXEL = 0;
    public static final int BLACK_PIXEL = -16777216;
    public static final int WHITE_PIXEL = -1;
    private final PersistentGLDrawable drawable;
    private Drawable.ModificationListener modificationListener;

    private native void copyPixmapArea(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void copyPixmapAreaAnd(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void copyPixmapAreaAndReverse(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void copyPixmapAreaOr(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void copyPixmapAreaOrReverse(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void drawAlphaMaskedBitmapImpl(long j, long j2, long j3, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11);

    private native void drawBitmapImpl(ByteBuffer byteBuffer, long j, int i, int i2, int i3, int i4, int i5);

    private native void drawFilledRectangles(ByteBuffer byteBuffer, int i, int i2, long j, int i3, int i4, int i5);

    private native void drawLines(ByteBuffer byteBuffer, int i, int i2, long j, int i3, int i4, int i5);

    private native void drawZPixmapS16D16(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, long j, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void drawZPixmapS16D16AND(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, long j, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void drawZPixmapS16D16XOR(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, long j, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void drawZPixmapS32D32(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, long j, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void drawZPixmapS32D32AND(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, long j, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void drawZPixmapS32D32XOR(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, long j, int i5, int i6, int i7, int i8, int i9, int i10);

    private native void getZPixmap16(byte[] bArr, int i, int i2, int i3, int i4, long j, int i5, int i6);

    private native void getZPixmap32(byte[] bArr, int i, int i2, int i3, int i4, long j, int i5, int i6);

    private native void setPixmapArea(long j, int i, int i2, int i3, int i4, int i5, int i6, int i7);

    @Override // com.eltechs.axs.xserver.Painter
    public void fillWithColor(int i) {
    }

    static {
        System.loadLibrary("axs-helpers");
    }

    public PainterOnPersistentGLDrawable(PersistentGLDrawable persistentGLDrawable) {
        this.drawable = persistentGLDrawable;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.eltechs.axs.xserver.Painter
    public void copyArea(GraphicsContext graphicsContext, Drawable drawable, int i, int i2, int i3, int i4, int i5, int i6) {
        int i7;
        int i8;
        Rectangle rectangle;
        PixelCompositionRule function = graphicsContext.getFunction();
        if (function == PixelCompositionRule.NO_OP) {
            return;
        }
        if (i < 0 || i2 < 0 || i + i5 > drawable.getWidth() || i2 + i6 > drawable.getHeight()) {
            Assert.notImplementedYet();
        }
        Rectangle intersection = Rectangle.getIntersection(new Rectangle(i3, i4, i5, i6), new Rectangle(0, 0, this.drawable.getWidth(), this.drawable.getHeight()));
        if (intersection == null) {
            return;
        }
        int i9 = i + (intersection.x - i3);
        int i10 = i2 + (intersection.y - i4);
        int i11 = intersection.width;
        int i12 = intersection.height;
        PersistentGLDrawable persistentGLDrawable = (PersistentGLDrawable) drawable;
        switch (function) {
            case COPY:
                i7 = i12;
                i8 = i11;
                rectangle = intersection;
                copyPixmapArea(persistentGLDrawable.getContent(), this.drawable.getContent(), persistentGLDrawable.getWidth(), persistentGLDrawable.getHeight(), this.drawable.getWidth(), this.drawable.getHeight(), i8, i7, i9, i10, intersection.x, intersection.y);
                break;
            case AND_REVERSE:
                i7 = i12;
                i8 = i11;
                copyPixmapAreaAndReverse(persistentGLDrawable.getContent(), this.drawable.getContent(), persistentGLDrawable.getWidth(), persistentGLDrawable.getHeight(), this.drawable.getWidth(), this.drawable.getHeight(), i8, i7, i9, i10, intersection.x, intersection.y);
                rectangle = intersection;
                break;
            case OR_REVERSE:
                i7 = i12;
                i8 = i11;
                copyPixmapAreaOrReverse(persistentGLDrawable.getContent(), this.drawable.getContent(), persistentGLDrawable.getWidth(), persistentGLDrawable.getHeight(), this.drawable.getWidth(), this.drawable.getHeight(), i8, i7, i9, i10, intersection.x, intersection.y);
                rectangle = intersection;
                break;
            case AND:
                i7 = i12;
                i8 = i11;
                copyPixmapAreaAnd(persistentGLDrawable.getContent(), this.drawable.getContent(), persistentGLDrawable.getWidth(), persistentGLDrawable.getHeight(), this.drawable.getWidth(), this.drawable.getHeight(), i8, i7, i9, i10, intersection.x, intersection.y);
                rectangle = intersection;
                break;
            case OR:
                i7 = i12;
                i8 = i11;
                copyPixmapAreaOr(persistentGLDrawable.getContent(), this.drawable.getContent(), persistentGLDrawable.getWidth(), persistentGLDrawable.getHeight(), this.drawable.getWidth(), this.drawable.getHeight(), i11, i12, i9, i10, intersection.x, intersection.y);
                rectangle = intersection;
                break;
            case CLEAR:
                setPixmapArea(this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight(), i11, i12, intersection.x, intersection.y, -16777216);
                i7 = i12;
                i8 = i11;
                rectangle = intersection;
                break;
            case SET:
                setPixmapArea(this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight(), i11, i12, intersection.x, intersection.y, -1);
                i7 = i12;
                i8 = i11;
                rectangle = intersection;
                break;
            default:
                i7 = i12;
                i8 = i11;
                rectangle = intersection;
                break;
        }
        Rectangle rectangle2 = rectangle;
        this.modificationListener.changed(rectangle2.x, rectangle2.y, i8, i7);
    }

    @Override // com.eltechs.axs.xserver.Painter
    public void drawBitmap(int i, int i2, int i3, int i4, ByteBuffer byteBuffer) {
        if (i3 != this.drawable.getWidth() || i4 != this.drawable.getHeight() || i != 0 || i2 != 0) {
            Assert.notImplementedYet();
        }
        drawBitmapImpl(byteBuffer, this.drawable.getContent(), i3, i4, -1, -16777216, 4);
    }

    @Override // com.eltechs.axs.xserver.Painter
    public void drawZPixmap(PixelCompositionRule pixelCompositionRule, byte b, int i, int i2, int i3, int i4, int i5, int i6, ByteBuffer byteBuffer, int i7, int i8) {
        Rectangle intersection = Rectangle.getIntersection(new Rectangle(i, i2, i5, i6), new Rectangle(0, 0, this.drawable.getWidth(), this.drawable.getHeight()));
        if (intersection == null) {
            return;
        }
        int i9 = i3 + (intersection.x - i);
        int i10 = i4 + (intersection.y - i2);
        if (b == 1) {
            if (pixelCompositionRule != PixelCompositionRule.COPY) {
                Assert.notImplementedYet("drawZPixmap bitmap::Function values other than COPY is not supported yet.");
            }
            drawBitmapImpl(byteBuffer, this.drawable.getContent(), i7, i8, -1, -16777216, 4);
        } else if (b != 32) {
            switch (b) {
                case 15:
                case 16:
                    if (pixelCompositionRule == PixelCompositionRule.COPY) {
                        drawZPixmap16(byteBuffer, i7, i8, i9, i10, intersection.x, intersection.y, intersection.width, intersection.height);
                        break;
                    } else if (pixelCompositionRule == PixelCompositionRule.AND) {
                        drawZPixmap16AND(byteBuffer, i7, i8, i9, i10, intersection.x, intersection.y, intersection.width, intersection.height);
                        break;
                    } else if (pixelCompositionRule == PixelCompositionRule.XOR) {
                        drawZPixmap16XOR(byteBuffer, i7, i8, i9, i10, intersection.x, intersection.y, intersection.width, intersection.height);
                        break;
                    } else {
                        Assert.notImplementedYet("drawZPixmap 15/16 bit depth::Function values other than COPY/AND/XOR is not supported yet.");
                        break;
                    }
                default:
                    Assert.state(false, "Sorting out unsupported pixmap depths must be done in protocol handlers.");
                    break;
            }
        } else if (pixelCompositionRule == PixelCompositionRule.COPY) {
            drawZPixmap32(byteBuffer, i7, i8, i9, i10, intersection.x, intersection.y, intersection.width, intersection.height);
        } else if (pixelCompositionRule == PixelCompositionRule.AND) {
            drawZPixmap32AND(byteBuffer, i7, i8, i9, i10, intersection.x, intersection.y, intersection.width, intersection.height);
        } else if (pixelCompositionRule == PixelCompositionRule.XOR) {
            drawZPixmap32XOR(byteBuffer, i7, i8, i9, i10, intersection.x, intersection.y, intersection.width, intersection.height);
        } else {
            Assert.notImplementedYet("drawZPixmap 32 bit depth::Function values other than COPY/AND/XOR is not supported yet.");
        }
        this.modificationListener.changed(intersection.x, intersection.y, intersection.width, intersection.height);
    }

    @Override // com.eltechs.axs.xserver.Painter
    public void drawAlphaMaskedBitmap(Drawable drawable, Drawable drawable2, int i, int i2, int i3, int i4, int i5, int i6) {
        drawAlphaMaskedBitmapImpl(this.drawable.getContent(), ((PersistentGLDrawable) drawable).getContent(), drawable2 != null ? ((PersistentGLDrawable) drawable2).getContent() : 0L, this.drawable.getWidth(), this.drawable.getHeight(), i, i2, i3, i4, i5, i6, -1, -16777216, 0);
    }

    @Override // com.eltechs.axs.xserver.Painter
    public byte[] getZPixmap(int i, int i2, int i3, int i4) {
        int depth = this.drawable.getVisual().getDepth();
        if (depth == 16) {
            byte[] bArr = new byte[i3 * i4 * 2];
            getZPixmap16(bArr, i3, i4, i, i2, this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight());
            return bArr;
        } else if (depth == 32) {
            byte[] bArr2 = new byte[i3 * i4 * 4];
            getZPixmap32(bArr2, i3, i4, i, i2, this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight());
            return bArr2;
        } else {
            Assert.notImplementedYet();
            return null;
        }
    }

    @Override // com.eltechs.axs.xserver.Painter
    public void drawLines(ByteBuffer byteBuffer, int i, int i2) {
        if (i2 != 1) {
            return;
        }
        int depth = this.drawable.getVisual().getDepth();
        if (depth != 32) {
            switch (depth) {
                case 15:
                case 16:
                    break;
                default:
                    return;
            }
        }
        drawLines(byteBuffer, (byteBuffer.limit() / 4) - 1, i, this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight(), depth);
    }

    @Override // com.eltechs.axs.xserver.Painter
    public void drawFilledRectangles(ByteBuffer byteBuffer, int i) {
        int depth = this.drawable.getVisual().getDepth();
        if (depth != 32) {
            switch (depth) {
                case 15:
                case 16:
                    break;
                default:
                    return;
            }
        }
        drawFilledRectangles(byteBuffer, byteBuffer.limit() / 8, i, this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight(), depth);
    }

    public void setModificationListener(Drawable.ModificationListener modificationListener) {
        this.modificationListener = modificationListener;
    }

    private void drawZPixmap16(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        switch (this.drawable.getVisual().getDepth()) {
            case 15:
            case 16:
                drawZPixmapS16D16(byteBuffer, i, i2, i3, i4, this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight(), i5, i6, i7, i8);
                return;
            default:
                Assert.notImplementedYet();
                return;
        }
    }

    private void drawZPixmap16XOR(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        switch (this.drawable.getVisual().getDepth()) {
            case 15:
            case 16:
                drawZPixmapS16D16XOR(byteBuffer, i, i2, i3, i4, this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight(), i5, i6, i7, i8);
                return;
            default:
                Assert.notImplementedYet();
                return;
        }
    }

    private void drawZPixmap16AND(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        switch (this.drawable.getVisual().getDepth()) {
            case 15:
            case 16:
                drawZPixmapS16D16AND(byteBuffer, i, i2, i3, i4, this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight(), i5, i6, i7, i8);
                return;
            default:
                Assert.notImplementedYet();
                return;
        }
    }

    private void drawZPixmap32(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (this.drawable.getVisual().getDepth() == 32) {
            drawZPixmapS32D32(byteBuffer, i, i2, i3, i4, this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight(), i5, i6, i7, i8);
        } else {
            Assert.notImplementedYet();
        }
    }

    private void drawZPixmap32XOR(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (this.drawable.getVisual().getDepth() == 32) {
            drawZPixmapS32D32XOR(byteBuffer, i, i2, i3, i4, this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight(), i5, i6, i7, i8);
        } else {
            Assert.notImplementedYet();
        }
    }

    private void drawZPixmap32AND(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (this.drawable.getVisual().getDepth() == 32) {
            drawZPixmapS32D32AND(byteBuffer, i, i2, i3, i4, this.drawable.getContent(), this.drawable.getWidth(), this.drawable.getHeight(), i5, i6, i7, i8);
        } else {
            Assert.notImplementedYet();
        }
    }
}