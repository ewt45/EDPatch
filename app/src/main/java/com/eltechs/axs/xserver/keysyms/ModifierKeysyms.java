package com.eltechs.axs.xserver.keysyms;

/* loaded from: classes.dex */
public enum ModifierKeysyms implements KeysymsEnum {
    SHIFT_L(65505),
    SHIFT_R(65506),
    CONTROL_L(65507),
    CONTROL_R(65508),
    ALT_L(65511),
    ALT_R(65512);
    
    private final int keysym;

    ModifierKeysyms(int i) {
        this.keysym = i;
    }

    @Override // com.eltechs.axs.xserver.keysyms.KeysymsEnum
    public int getKeysym() {
        return this.keysym;
    }
}
