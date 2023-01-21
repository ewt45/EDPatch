package com.eltechs.axs.xserver;

import com.eltechs.axs.helpers.Assert;

public class KeyboardLayout {
    private final int[] minKeysyms = new int[KeyboardModel.KEYS_COUNT];
    private final int[] majKeysyms = new int[KeyboardModel.KEYS_COUNT];

    public void setKeysymMapping(int i, int i2, int i3) {
        Assert.isTrue(i >= 8, "Keycoded 0 through 7 are not used by X11.");
        int i4 = i - 8;
        this.minKeysyms[i4] = i2;
        this.majKeysyms[i4] = i3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] getMinusculeKeysyms() {
        return this.minKeysyms;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] getMajusculeKeysyms() {
        return this.majKeysyms;
    }
}
