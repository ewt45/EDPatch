package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.Pixmap;

/* loaded from: classes.dex */
public class PixmapImpl implements Pixmap {
    private final Drawable backingStore;

    public PixmapImpl(Drawable drawable) {
        this.backingStore = drawable;
    }

    @Override // com.eltechs.axs.xserver.Pixmap
    public Drawable getBackingStore() {
        return this.backingStore;
    }
}
