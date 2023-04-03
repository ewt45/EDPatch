package com.eltechs.axs.xserver.impl.drawables.bitmapBacked;

import android.graphics.Bitmap;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.Painter;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.impl.drawables.Visual;

/* loaded from: classes.dex */
public class BitmapBackedDrawable implements Drawable {
    private final Bitmap content;
    private final int height;
    private final int id;
    private final PainterOnBitmap painter;
    private final Window rootWindow;
    private final Visual visual;
    private final int width;

    public BitmapBackedDrawable(int i, Window window, Bitmap bitmap, int i2, int i3, Visual visual) {
        Assert.isTrue(i2 <= bitmap.getWidth() && i3 <= bitmap.getHeight(), "Bitmap smaller than the Drawable");
        this.id = i;
        this.rootWindow = window;
        this.visual = visual;
        this.content = bitmap;
        this.width = i2;
        this.height = i3;
        this.painter = new PainterOnBitmap(this.content, this, i2, i3);
        this.painter.setModificationListener(new Drawable.ModificationListener() { // from class: com.eltechs.axs.xserver.impl.drawables.bitmapBacked.BitmapBackedDrawable.1
            @Override // com.eltechs.axs.xserver.Drawable.ModificationListener
            public void changed(int i4, int i5, int i6, int i7) {
            }
        });
    }

    @Override // com.eltechs.axs.xserver.Drawable
    public int getId() {
        return this.id;
    }

    @Override // com.eltechs.axs.xserver.Drawable
    public Window getRoot() {
        return this.rootWindow;
    }

    @Override // com.eltechs.axs.xserver.Drawable
    public Visual getVisual() {
        return this.visual;
    }

    @Override // com.eltechs.axs.xserver.Drawable
    public int getWidth() {
        return this.width;
    }

    @Override // com.eltechs.axs.xserver.Drawable
    public int getHeight() {
        return this.height;
    }

    @Override // com.eltechs.axs.xserver.Drawable
    public Painter getPainter() {
        return this.painter;
    }

    @Override // com.eltechs.axs.xserver.Drawable
    public void installModificationListener(Drawable.ModificationListener modificationListener) {
        this.painter.setModificationListener(modificationListener);
    }

    public Bitmap getContent() {
        return this.content;
    }
}
