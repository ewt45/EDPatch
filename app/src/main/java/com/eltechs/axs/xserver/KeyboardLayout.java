package com.eltechs.axs.xserver;

import com.eltechs.axs.helpers.Assert;

public class KeyboardLayout {
    private final int[] minKeysyms = new int[KeyboardModel.KEYS_COUNT];
    private final int[] majKeysyms = new int[KeyboardModel.KEYS_COUNT];

    /**
     * 设置keycode对应的keysym
     * @param keycode 按键的keycode
     * @param minKeysym 小写字母对应的keysym
     * @param majKeysym 大写字母对应的kesym。若没有则填0
     */
    public void setKeysymMapping(int keycode, int minKeysym, int majKeysym) {
        Assert.isTrue(keycode >= 8, "Keycoded 0 through 7 are not used by X11.");
        int i4 = keycode - 8;
        this.minKeysyms[i4] = minKeysym;
        this.majKeysyms[i4] = majKeysym;
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
