package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.xserver.Colormap;

/* loaded from: classes.dex */
public class ColormapImpl implements Colormap {
    private final int id;

    public ColormapImpl(int i) {
        this.id = i;
    }

    @Override // com.eltechs.axs.xserver.Colormap
    public int getId() {
        return this.id;
    }
}
