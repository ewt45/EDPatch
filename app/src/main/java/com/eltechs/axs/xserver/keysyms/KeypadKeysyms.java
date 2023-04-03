package com.eltechs.axs.xserver.keysyms;

/* loaded from: classes.dex */
public enum KeypadKeysyms implements KeysymsEnum {
    KEYPAD_DEL(65439);
    
    private final int keysym;

    KeypadKeysyms(int i) {
        this.keysym = i;
    }

    @Override // com.eltechs.axs.xserver.keysyms.KeysymsEnum
    public int getKeysym() {
        return this.keysym;
    }
}
