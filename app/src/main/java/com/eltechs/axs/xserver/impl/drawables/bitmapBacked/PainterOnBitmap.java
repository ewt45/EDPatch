package com.eltechs.axs.xserver.impl.drawables.bitmapBacked;

import android.graphics.Bitmap;
import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.ReluctantlyGarbageCollectedArrays;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.GraphicsContext;
import com.eltechs.axs.xserver.Painter;
import com.eltechs.axs.xserver.graphicsContext.PixelCompositionRule;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class PainterOnBitmap implements Painter {
    public static final int ALPHA_PIXEL = 0;
    public static final int BLACK_PIXEL = -16777216;
    public static final int WHITE_PIXEL = -1;
    private final ReluctantlyGarbageCollectedArrays arrays = new ReluctantlyGarbageCollectedArrays();
    private final Bitmap bitmap;
    private final BitmapBackedDrawable drawable;
    private final int height;
    private Drawable.ModificationListener modificationListener;
    private final int width;

    private int makeVisibleRGBPixel(int i, int i2, int i3) {
        return (i << 16) | (-16777216) | (i2 << 8) | i3;
    }

    private native void readBitmap(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, int i5, int[] iArr);

    private native void readZPixmap24(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, int i5, int[] iArr);

    @Override // com.eltechs.axs.xserver.Painter
    public void drawFilledRectangles(ByteBuffer byteBuffer, int i) {
    }

    @Override // com.eltechs.axs.xserver.Painter
    public void drawLines(ByteBuffer byteBuffer, int i, int i2) {
    }

    @Override // com.eltechs.axs.xserver.Painter
    public void fillWithColor(int i) {
    }

    static {
        System.loadLibrary("axs-helpers");
    }

    public PainterOnBitmap(Bitmap bitmap, BitmapBackedDrawable bitmapBackedDrawable, int i, int i2) {
        this.drawable = bitmapBackedDrawable;
        this.bitmap = bitmap;
        this.width = i;
        this.height = i2;
    }

    @Override // com.eltechs.axs.xserver.Painter
    public void drawBitmap(int i, int i2, int i3, int i4, ByteBuffer byteBuffer) {
        drawBitmapImpl(i, i2, i3, i4, byteBuffer);
    }

    @Override // com.eltechs.axs.xserver.Painter
    public void drawZPixmap(PixelCompositionRule pixelCompositionRule, byte b, int i, int i2, int i3, int i4, int i5, int i6, ByteBuffer byteBuffer, int i7, int i8) {
        PainterOnBitmap painterOnBitmap;
        if (b == 1) {
            painterOnBitmap = this;
            painterOnBitmap.drawBitmapImpl(i, i2, i5, i6, byteBuffer);
        } else if (b == 24) {
            painterOnBitmap = this;
            painterOnBitmap.drawZPixmap24(i, i2, i5, i6, byteBuffer);
        } else {
            Assert.state(false, "Sorting out unsupported pixmap depths must be done in protocol handlers.");
            painterOnBitmap = this;
        }
        painterOnBitmap.modificationListener.changed(i, i2, i5, i6);
    }

    @Override // com.eltechs.axs.xserver.Painter
    public byte[] getZPixmap(int i, int i2, int i3, int i4) {
        if (this.drawable.getVisual().getDepth() == 24) {
            return getZPixmap24(i, i2, i3, i4);
        }
        Assert.state(false, "Sorting out unsupported pixmap depths must be done in protocol handlers.");
        return null;
    }

    @Override // com.eltechs.axs.xserver.Painter
    public void copyArea(GraphicsContext graphicsContext, Drawable drawable, int i, int i2, int i3, int i4, int i5, int i6) {
        PixelCompositionRule function = graphicsContext.getFunction();
        if (function == PixelCompositionRule.NO_OP) {
            return;
        }
        Bitmap content = ((BitmapBackedDrawable) drawable).getContent();
        if (i < 0 || i2 < 0 || i + i5 > content.getWidth() || i2 + i6 > content.getHeight()) {
            Assert.notImplementedYet();
        }
        Rectangle intersection = Rectangle.getIntersection(new Rectangle(i3, i4, i5, i6), new Rectangle(0, 0, this.bitmap.getWidth(), this.bitmap.getHeight()));
        if (intersection == null) {
            return;
        }
        int i7 = i + (intersection.x - i3);
        int i8 = i2 + (intersection.y - i4);
        int i9 = intersection.width;
        int i10 = intersection.height;
        int i11 = i9 * i10;
        int[] intArray = this.arrays.getIntArray(i11);
        if (function == PixelCompositionRule.COPY) {
            content.getPixels(intArray, 0, i5, i7, i8, i9, i10);
        } else if (function == PixelCompositionRule.CLEAR || function == PixelCompositionRule.SET) {
            int i12 = function == PixelCompositionRule.CLEAR ? -16777216 : -1;
            for (int i13 = 0; i13 < i11; i13++) {
                intArray[i13] = i12;
            }
        }
        this.bitmap.setPixels(intArray, 0, i5, intersection.x, intersection.y, i9, i10);
        this.modificationListener.changed(intersection.x, intersection.y, i9, i10);
    }

    @Override // com.eltechs.axs.xserver.Painter
    public void drawAlphaMaskedBitmap(Drawable drawable, Drawable drawable2, int i, int i2, int i3, int i4, int i5, int i6) {
        Bitmap content = ((BitmapBackedDrawable) drawable).getContent();
        Bitmap content2 = drawable2 != null ? ((BitmapBackedDrawable) drawable2).getContent() : null;
        int makeVisibleRGBPixel = makeVisibleRGBPixel(i, i2, i3);
        int makeVisibleRGBPixel2 = makeVisibleRGBPixel(i4, i5, i6);
        int width = this.bitmap.getWidth();
        int height = this.bitmap.getHeight();
        int[] iArr = new int[width * height];
        int i7 = 0;
        int i8 = 0;
        while (i7 < height) {
            int i9 = i8;
            int i10 = 0;
            while (i10 < width) {
                int i11 = content.getPixel(i10, i7) == -16777216 ? makeVisibleRGBPixel : makeVisibleRGBPixel2;
                if (drawable2 != null && content2.getPixel(i10, i7) == -1) {
                    i11 = 0;
                }
                iArr[i9] = i11;
                i10++;
                i9++;
            }
            i7++;
            i8 = i9;
        }
        this.bitmap.setPixels(iArr, 0, width, 0, 0, width, height);
    }

    private void drawZPixmap24(int i, int i2, int i3, int i4, ByteBuffer byteBuffer) {
        Rectangle intersection = Rectangle.getIntersection(new Rectangle(i, i2, i3, i4), new Rectangle(0, 0, this.bitmap.getWidth(), this.bitmap.getHeight()));
        if (intersection == null) {
            return;
        }
        int i5 = intersection.width;
        int i6 = intersection.height;
        int i7 = intersection.x - i;
        int i8 = ((i + i3) - 1) - ((intersection.x + intersection.width) - 1);
        int i9 = intersection.y - i2;
        int[] intArray = this.arrays.getIntArray(i5 * i6);
        readZPixmap24(byteBuffer, i3, i4, i7, i8, i9, intArray);
        this.bitmap.setPixels(intArray, 0, i3, intersection.x, intersection.y, i5, i6);
    }

    private void drawBitmapImpl(int i, int i2, int i3, int i4, ByteBuffer byteBuffer) {
        int[] intArray = this.arrays.getIntArray(i3 * i4);
        readBitmap(byteBuffer, i3, i4, 4, -16777216, -1, intArray);
        this.bitmap.setPixels(intArray, 0, i3, i, i2, i3, i4);
    }

    private byte[] getZPixmap24(int i, int i2, int i3, int i4) {
        int i5 = i3 * i4;
        int i6 = i5 * 4;
        byte[] bArr = new byte[i6];
        int[] iArr = new int[i5];
        this.bitmap.getPixels(iArr, 0, i3, i, i2, i3, i4);
        int i7 = 0;
        int i8 = 0;
        while (i7 < i6) {
            bArr[i7 + 0] = (byte) iArr[i8];
            bArr[i7 + 1] = (byte) (iArr[i8] >> 8);
            bArr[i7 + 2] = (byte) (iArr[i8] >> 16);
            bArr[i7 + 3] = 0;
            i7 += 4;
            i8++;
        }
        return bArr;
    }

    public void setModificationListener(Drawable.ModificationListener modificationListener) {
        this.modificationListener = modificationListener;
    }
}
