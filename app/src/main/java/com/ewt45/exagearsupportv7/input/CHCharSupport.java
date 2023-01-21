package com.ewt45.exagearsupportv7.input;

import com.eltechs.axs.KeyCodesX;

public class CHCharSupport {
    public static final KeyCodesX[] avaiKeyCode = {KeyCodesX.KEY_A, KeyCodesX.KEY_B, KeyCodesX.KEY_C, KeyCodesX.KEY_D, KeyCodesX.KEY_E, KeyCodesX.KEY_F, KeyCodesX.KEY_G, KeyCodesX.KEY_H, KeyCodesX.KEY_I, KeyCodesX.KEY_J, KeyCodesX.KEY_K, KeyCodesX.KEY_L, KeyCodesX.KEY_M, KeyCodesX.KEY_N, KeyCodesX.KEY_O, KeyCodesX.KEY_P, KeyCodesX.KEY_Q, KeyCodesX.KEY_R, KeyCodesX.KEY_S, KeyCodesX.KEY_T, KeyCodesX.KEY_U, KeyCodesX.KEY_V, KeyCodesX.KEY_W, KeyCodesX.KEY_X, KeyCodesX.KEY_Y, KeyCodesX.KEY_Z,};
    public static int  currIndex = 0;

//    /**
//     * 判断是否为中文，修改其keycode
//     * @param xKey 当前需要输入的xkey
//     */
//    public static void setKeyCodeForCHChar(Keyboard.XKey xKey) {
//        //判断是否为中文，修改其keycode 判断就用keysym是否大于0x1000000判断了，ex里貌似原本没有这么大的
//        if (xKey.keysym > 0x1000000) {
//            xKey.keycode= avaiKeyCode[currIndex]; //如果是，设置一个keycode防止重复
//            currIndex = (currIndex+1)%avaiKeyCode.length; //数组下标+1，为下一次设置另一个keycode做准备
//        }
//    }
}
