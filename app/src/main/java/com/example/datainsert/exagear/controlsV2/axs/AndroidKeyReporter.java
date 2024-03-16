package com.example.datainsert.exagear.controlsV2.axs;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;

import android.view.KeyEvent;

import com.example.datainsert.exagear.controlsV2.Const;

public class AndroidKeyReporter {
    /** 用于输入unicode文字时，临时充数的keycode */
    public static final XKeyButton.Info[] avaiKeyCode = {XKeyButton.key_a, XKeyButton.key_b, XKeyButton.key_c, XKeyButton.key_d, XKeyButton.key_e, XKeyButton.key_f, XKeyButton.key_g, XKeyButton.key_h, XKeyButton.key_i, XKeyButton.key_j, XKeyButton.key_k, XKeyButton.key_l, XKeyButton.key_m, XKeyButton.key_n, XKeyButton.key_o, XKeyButton.key_p, XKeyButton.key_q, XKeyButton.key_r, XKeyButton.key_s, XKeyButton.key_t, XKeyButton.key_u, XKeyButton.key_v, XKeyButton.key_w, XKeyButton.key_x, XKeyButton.key_y, XKeyButton.key_z};
    /** 用于输入unicode文字时，记录本次该用哪个充数的keycode，然后++ */
    public static int  currIndex = 0;

    /**
     * TouchAreaView中接收到按键事件时调用
     */
    public static boolean handleAKeyEvent(int action, int aKeycode, int unicodeChar, String characters){
        //函数的实现参考参考com.eltechs.axs.Keyboard

        //普通按键
        if(aKeycode!=KeyEvent.KEYCODE_UNKNOWN){
            XKeyButton.Info keyInfo = XKeyButton.aKeyIndexedArr[aKeycode];
            if (keyInfo == null)
                return false;
//            Keyboard.XKey convertUnicodeToXKey = convertUnicodeToXKey(keyEvent.getUnicodeChar());//不处理unicode会发生什么？
            //unicodeChar可以认为是keySym吗？测试大小写A，俄语字母，+号等（貌似可以）
            int finalUnicodeChar = unicodeChar ==0x0a?0x0d:unicodeChar;//回车 应该是return 0xff0d .但是从keyevent获取到的unicode是0x0a linefeed，手动改一下
            if(action == ACTION_DOWN)
                Const.getXServerHolder().injectKeyPress(keyInfo.xKeyCode,finalUnicodeChar);
            else if(action == ACTION_UP)
                Const.getXServerHolder().injectKeyRelease(keyInfo.xKeyCode,finalUnicodeChar);

            return true;
        }
        //输入文字（多个字符）
        else if(action == KeyEvent.ACTION_MULTIPLE){
            boolean handled = false;
            //输入可能不止一个字符，循环输入
            //codePointAt可能把一个较长的字符拆成两份？也许这就是emoji无法输入的原因？
            //好像真有点关系https://stackoverflow.com/a/53195348//😀用网页转换是\ud83d\ude00, 这里keySym获取出来是0x1F600（但也是正确的），为啥一个符号会有两个数值？然后加了0x100,0000后exa里显示就是两个口口
            for (int i = 0; i < characters.codePointCount(0, characters.length()); i++) {
                //如果直接加上0x100,0000会怎么样
                // 不行，小于等于0xFF的加上0x100,0000会不识别，
                // 还有像π这样的，unicode是U+03C0，x11的keysym 指定了是0x07f0。exa中的unicodeMap数组中是做了映射的。（emmm实际测试，直接unicode+0x1000000也行，个别符号不显示可能是字体原因，一开始俄语不显示可能也是字体）
                int keySym = characters.codePointAt(characters.offsetByCodePoints(0, i));
                if(keySym>0xff)
                    keySym = keySym | 0x1000000;
                Const.getXServerHolder().injectKeyPress(avaiKeyCode[currIndex].xKeyCode,keySym);
                Const.getXServerHolder().injectKeyRelease(avaiKeyCode[currIndex].xKeyCode,keySym);
                currIndex = (currIndex+1)%avaiKeyCode.length;//数组下标+1，为下一次设置另一个keycode做准备
                handled = true;
//                Keyboard.XKey xKey = convertUnicodeToXKey2(characters.codePointAt(characters.offsetByCodePoints(0, i)));
//
//                //如果初始化时设置了字符对应的xKey（没设置的默认就是0）
//                if (xKey != null && xKey.keycode != KeyCodesX.KEY_NONE) {
//                    this.reporter.reportKeyWithSym(xKey.keycode, xKey.keysym);
//                    handled = true;
//                }
            }
            return handled;
        }
        return false;
    }

}
