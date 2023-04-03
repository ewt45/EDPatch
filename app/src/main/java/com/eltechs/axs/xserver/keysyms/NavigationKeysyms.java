package com.eltechs.axs.xserver.keysyms;

/* loaded from: classes.dex */
public enum NavigationKeysyms implements KeysymsEnum {
    RIGHT(65363),
    UP(65362),
    LEFT(65361),
    DOWN(65364),
    PRIOR(65365),
    NEXT(65366),
    HOME(65360),
    END(65367);
    
    private final int keysym;

    NavigationKeysyms(int i) {
        this.keysym = i;
    }

    @Override // com.eltechs.axs.xserver.keysyms.KeysymsEnum
    public int getKeysym() {
        return this.keysym;
    }
}
