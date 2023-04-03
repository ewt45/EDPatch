package com.eltechs.axs.xserver;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.impl.masks.FlagsEnum;
import com.eltechs.axs.xserver.impl.masks.Mask;

public enum KeyButNames implements FlagsEnum {
    SHIFT(1),
    CAPS_LOCK(2),
    CONTROL(4),
    ALT(8),
    NUM_LOCK(16),
    MOD3(32),
    MOD4(64),
    MOD5(128),
    BUTTON1(256),
    BUTTON2(512),
    BUTTON3(1024),
    BUTTON4(2048),
    BUTTON5(4096),
    BUTTON6(536870912),
    BUTTON7(1073741824);

    private final int flag;

    KeyButNames(int i) {
        this.flag = i;
    }

    @Override // com.eltechs.axs.xserver.impl.masks.FlagsEnum
    public int flagValue() {
        return this.flag;
    }

    public static KeyButNames getFlagForButtonNumber(int keyOffset) {
        boolean z = true;
        int index = BUTTON1.ordinal() - 1 + keyOffset ;
        if (index < BUTTON1.ordinal() || index > BUTTON7.ordinal()) {
            z = false;
        }
        Assert.isTrue(z);
        return values()[index];
    }

    public static boolean isModifierReal(KeyButNames keyButNames) {
        return keyButNames.ordinal() <= BUTTON5.ordinal();
    }

    public static void clearVirtualModifiers(Mask<KeyButNames> mask) {
        mask.clear(BUTTON6);
        mask.clear(BUTTON7);
    }
}