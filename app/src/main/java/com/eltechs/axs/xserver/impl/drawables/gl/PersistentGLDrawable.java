package com.eltechs.axs.xserver.impl.drawables.gl;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.Painter;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.impl.drawables.Visual;

/* loaded from: classes.dex */
public class PersistentGLDrawable implements Drawable {
    private final long content;
    private final int height;
    private final int id;
    private final PainterOnPersistentGLDrawable painter;
    private final Window rootWindow;
    private final Visual visual;
    private final int width;

    private static native long allocateNativeStorage(int i, int i2);

    static {
        System.loadLibrary("axs-helpers");
    }

    public PersistentGLDrawable(int i, Window window, int i2, int i3, Visual visual) {
        Assert.isTrue(i2 >= 0 && i3 >= 0, "Dimensions of a Drawable must be non-negative.");
        this.id = i;
        this.rootWindow = window;
        this.visual = visual;
        this.content = allocateNativeStorage(i2, i3);
        this.width = i2;
        this.height = i3;
        this.painter = new PainterOnPersistentGLDrawable(this);
        this.painter.setModificationListener(new Drawable.ModificationListener() { // from class: com.eltechs.axs.xserver.impl.drawables.gl.PersistentGLDrawable.1
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

    public long getContent() {
        return this.content;
    }
}