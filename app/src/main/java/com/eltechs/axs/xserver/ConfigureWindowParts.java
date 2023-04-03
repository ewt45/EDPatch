package com.eltechs.axs.xserver;

import com.eltechs.axs.xserver.impl.masks.FlagsEnum;

/* loaded from: classes.dex */
public enum ConfigureWindowParts implements FlagsEnum {
    X(1),
    Y(2),
    WIDTH(4),
    HEIGHT(8),
    BORDER_WIDTH(16),
    SIBLING(32),
    STACK_MODE(64);
    
    private final int part;

    ConfigureWindowParts(int i) {
        this.part = i;
    }

    @Override // com.eltechs.axs.xserver.impl.masks.FlagsEnum
    public int flagValue() {
        return this.part;
    }
}
