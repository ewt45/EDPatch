package com.eltechs.axs.xserver;

import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.helpers.Assert;

public class KeyboardModel {
    public static final int KEYSYMS_PER_KEYCODE_IN_LAYOUT = 2;
    public static final int KEYS_COUNT = 248;
    public static final int MAX_KEYCODE = 255;
    public static final int MIN_KEYCODE = 8;
    /** 记录keycode和对应keysym的数组。每个键盘布局对应一维的两个元素（小写和小写字母），二维的索引是keycode，值是keysym */
    private final int[][] keymap;
    private final KeyboardModifiersLayout modifiersLayout;

    public KeyboardModel(KeyboardModifiersLayout keyboardModifiersLayout, KeyboardLayout... keyboardLayoutArr) {
        this.modifiersLayout = keyboardModifiersLayout;
        Assert.isTrue(keyboardLayoutArr.length <= 127, "Can have at most 127 keyboard layouts");
        Assert.state(true, "Keyboard layout must define 2 keysyms per keycode: for minuscule and majuscule letters.");
        this.keymap = new int[keyboardLayoutArr.length*2][];
        for (int i = 0; i < keyboardLayoutArr.length; i++) {
            int i2 = 2 * i;
            int minSymsIdx = i2 + 0;
            this.keymap[minSymsIdx] = new int[KEYS_COUNT];
            int majSymsIdx = i2 + 1;
            this.keymap[majSymsIdx] = new int[KEYS_COUNT];
            System.arraycopy(keyboardLayoutArr[i].getMinusculeKeysyms(), 0, this.keymap[minSymsIdx], 0, KEYS_COUNT);
            System.arraycopy(keyboardLayoutArr[i].getMajusculeKeysyms(), 0, this.keymap[majSymsIdx], 0, KEYS_COUNT);
        }
    }

    public int getLayoutsCount() {
        return this.keymap.length / 2;
    }

    /**
     * 获取keycode对应的keysym。第一个是小写第二个是大写
     */
    public void getKeysymsForKeycode(int keycode, int[] keySyms) {
        for (int i = 0; i < this.keymap.length; i++) {
            keySyms[i] = this.keymap[i][keycode - 8];
        }
    }

    public void getKeysymsForKeycodeGroup1(int keycode, int[] keySyms) {
        int oriKeycode = keycode - 8;
        keySyms[0] = this.keymap[0][oriKeycode];
        keySyms[1] = this.keymap[1][oriKeycode];
    }

    public void setKeysymsForKeycodeGroup1(int keycode, int minKeySym, int majKeySym) {
        int oriKeycode = keycode - 8;
        this.keymap[0][oriKeycode] = minKeySym;
        this.keymap[1][oriKeycode] = majKeySym;
    }

    public KeyboardModifiersLayout getModifiersLayout() {
        return this.modifiersLayout;
    }

    public boolean isKeycodeValid(byte b) {
        int extendAsUnsigned = ArithHelpers.extendAsUnsigned(b);
        return extendAsUnsigned >= MIN_KEYCODE && extendAsUnsigned <= MAX_KEYCODE;
    }
}
