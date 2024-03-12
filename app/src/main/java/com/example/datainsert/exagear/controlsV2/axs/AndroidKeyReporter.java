package com.example.datainsert.exagear.controlsV2.axs;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;

import android.view.KeyEvent;

import com.eltechs.axs.KeyCodesX;
import com.example.datainsert.exagear.controlsV2.Const;

public class AndroidKeyReporter {
    /** 以安卓的KeyCode为索引,对应元素是 Key.Info的数组，用于处理安卓输入法输入的文字/按键 */
    public static Key.Info[] aKeyIndexedArr = new Key.Info[KeyEvent.getMaxKeyCode()+1];
    /** 用于输入unicode文字时，临时充数的keycode */
    public static final Key.Info[] avaiKeyCode = {Key.key_a, Key.key_b, Key.key_c, Key.key_d, Key.key_e, Key.key_f, Key.key_g, Key.key_h, Key.key_i, Key.key_j, Key.key_k,Key.key_l,Key.key_m,Key.key_n,Key.key_o,Key.key_p,Key.key_q,Key.key_r,Key.key_s,Key.key_t,Key.key_u,Key.key_v,Key.key_w,Key.key_x,Key.key_y,Key.key_z};
    /** 用于输入unicode文字时，记录本次该用哪个充数的keycode，然后++ */
    public static int  currIndex = 0;
    static {
        setupAKeyIndexedArr();
        aKeyIndexedArr[KeyEvent.KEYCODE_UNKNOWN] = null; //确保未知按键没有映射
    }



    /**
     * TouchAreaView中接收到按键事件时调用
     */
    public static boolean handleAKeyEvent(int action, int aKeycode, int unicodeChar, String characters){
        //函数的实现参考参考com.eltechs.axs.Keyboard

        //普通按键
        if(aKeycode!=KeyEvent.KEYCODE_UNKNOWN){
            Key.Info keyInfo = aKeyIndexedArr[aKeycode];
            if (keyInfo == null)
                return false;
            //TODO 不处理unicode会发生什么？
//            Keyboard.XKey convertUnicodeToXKey = convertUnicodeToXKey(keyEvent.getUnicodeChar());

            //unicodeChar可以认为是keySym吗？测试大小写A，俄语字母，+号等
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

    /**
     * 初始化 {@link #aKeyIndexedArr}
     */
    private static void setupAKeyIndexedArr(){
        //TODO 1. 没有做unicode的映射 2.exa的akeycode映射里没有手柄映射，那它是怎么支持手柄的，难道在unicodeMap里映射的？
        //一些Key.Info里没记录的
        aKeyIndexedArr[KeyEvent.KEYCODE_AT] = Key.key_2;
        aKeyIndexedArr[KeyEvent.KEYCODE_POUND] = Key.key_3;
        aKeyIndexedArr[KeyEvent.KEYCODE_STAR] = Key.key_8;
        aKeyIndexedArr[KeyEvent.KEYCODE_PLUS] = Key.key_equal;
        //（根据exa中的按键映射来查缺补漏一下）
        aKeyIndexedArr[KeyEvent.KEYCODE_BACK] = Key.key_esc; //back理论上应该不会进入这里吧，因为被我拦截下来用于显示菜单了
        aKeyIndexedArr[KeyEvent.KEYCODE_MOVE_HOME] = Key.key_home;
        aKeyIndexedArr[KeyEvent.KEYCODE_COMMA] = Key.key_comma;

        //Key.Info里全部按键都放进来
        aKeyIndexedArr[Key.key_esc.aKeyCode] = Key.key_esc;
        aKeyIndexedArr[Key.key_f1.aKeyCode] = Key.key_f1;
        aKeyIndexedArr[Key.key_f2.aKeyCode] = Key.key_f2;
        aKeyIndexedArr[Key.key_f3.aKeyCode] = Key.key_f3;
        aKeyIndexedArr[Key.key_f4.aKeyCode] = Key.key_f4;
        aKeyIndexedArr[Key.key_f5.aKeyCode] = Key.key_f5;
        aKeyIndexedArr[Key.key_f6.aKeyCode] = Key.key_f6;
        aKeyIndexedArr[Key.key_f7.aKeyCode] = Key.key_f7;
        aKeyIndexedArr[Key.key_f8.aKeyCode] = Key.key_f8;
        aKeyIndexedArr[Key.key_f9.aKeyCode] = Key.key_f9;
        aKeyIndexedArr[Key.key_f10.aKeyCode] = Key.key_f10;
        aKeyIndexedArr[Key.key_f11.aKeyCode] = Key.key_f11;
        aKeyIndexedArr[Key.key_f12.aKeyCode] = Key.key_f12;
        aKeyIndexedArr[Key.key_grave.aKeyCode] = Key.key_grave;
        aKeyIndexedArr[Key.key_1.aKeyCode] = Key.key_1;
        aKeyIndexedArr[Key.key_2.aKeyCode] = Key.key_2;
        aKeyIndexedArr[Key.key_3.aKeyCode] = Key.key_3;
        aKeyIndexedArr[Key.key_4.aKeyCode] = Key.key_4;
        aKeyIndexedArr[Key.key_5.aKeyCode] = Key.key_5;
        aKeyIndexedArr[Key.key_6.aKeyCode] = Key.key_6;
        aKeyIndexedArr[Key.key_7.aKeyCode] = Key.key_7;
        aKeyIndexedArr[Key.key_8.aKeyCode] = Key.key_8;
        aKeyIndexedArr[Key.key_9.aKeyCode] = Key.key_9;
        aKeyIndexedArr[Key.key_0.aKeyCode] = Key.key_0;
        aKeyIndexedArr[Key.key_minus.aKeyCode] = Key.key_minus;
        aKeyIndexedArr[Key.key_equal.aKeyCode] = Key.key_equal;
        aKeyIndexedArr[Key.key_backspace.aKeyCode] = Key.key_backspace;
        aKeyIndexedArr[Key.key_tab.aKeyCode] = Key.key_tab;
        aKeyIndexedArr[Key.key_q.aKeyCode] = Key.key_q;
        aKeyIndexedArr[Key.key_w.aKeyCode] = Key.key_w;
        aKeyIndexedArr[Key.key_e.aKeyCode] = Key.key_e;
        aKeyIndexedArr[Key.key_r.aKeyCode] = Key.key_r;
        aKeyIndexedArr[Key.key_t.aKeyCode] = Key.key_t;
        aKeyIndexedArr[Key.key_y.aKeyCode] = Key.key_y;
        aKeyIndexedArr[Key.key_u.aKeyCode] = Key.key_u;
        aKeyIndexedArr[Key.key_i.aKeyCode] = Key.key_i;
        aKeyIndexedArr[Key.key_o.aKeyCode] = Key.key_o;
        aKeyIndexedArr[Key.key_p.aKeyCode] = Key.key_p;
        aKeyIndexedArr[Key.key_open_bracket.aKeyCode] = Key.key_open_bracket;
        aKeyIndexedArr[Key.key_close_bracket.aKeyCode] = Key.key_close_bracket;
        aKeyIndexedArr[Key.key_backslash.aKeyCode] = Key.key_backslash;
        aKeyIndexedArr[Key.key_caps_lock.aKeyCode] = Key.key_caps_lock;
        aKeyIndexedArr[Key.key_a.aKeyCode] = Key.key_a;
        aKeyIndexedArr[Key.key_s.aKeyCode] = Key.key_s;
        aKeyIndexedArr[Key.key_d.aKeyCode] = Key.key_d;
        aKeyIndexedArr[Key.key_f.aKeyCode] = Key.key_f;
        aKeyIndexedArr[Key.key_g.aKeyCode] = Key.key_g;
        aKeyIndexedArr[Key.key_h.aKeyCode] = Key.key_h;
        aKeyIndexedArr[Key.key_j.aKeyCode] = Key.key_j;
        aKeyIndexedArr[Key.key_k.aKeyCode] = Key.key_k;
        aKeyIndexedArr[Key.key_l.aKeyCode] = Key.key_l;
        aKeyIndexedArr[Key.key_semicolon.aKeyCode] = Key.key_semicolon;
        aKeyIndexedArr[Key.key_apostrophe.aKeyCode] = Key.key_apostrophe;
        aKeyIndexedArr[Key.key_enter.aKeyCode] = Key.key_enter;
        aKeyIndexedArr[Key.key_left_shift.aKeyCode] = Key.key_left_shift;
        aKeyIndexedArr[Key.key_z.aKeyCode] = Key.key_z;
        aKeyIndexedArr[Key.key_x.aKeyCode] = Key.key_x;
        aKeyIndexedArr[Key.key_c.aKeyCode] = Key.key_c;
        aKeyIndexedArr[Key.key_v.aKeyCode] = Key.key_v;
        aKeyIndexedArr[Key.key_b.aKeyCode] = Key.key_b;
        aKeyIndexedArr[Key.key_n.aKeyCode] = Key.key_n;
        aKeyIndexedArr[Key.key_m.aKeyCode] = Key.key_m;
        aKeyIndexedArr[Key.key_comma.aKeyCode] = Key.key_comma;
        aKeyIndexedArr[Key.key_dot.aKeyCode] = Key.key_dot;
        aKeyIndexedArr[Key.key_slash.aKeyCode] = Key.key_slash;
        aKeyIndexedArr[Key.key_right_shift.aKeyCode] = Key.key_right_shift;
        aKeyIndexedArr[Key.key_left_ctrl.aKeyCode] = Key.key_left_ctrl;
        aKeyIndexedArr[Key.key_left_win.aKeyCode] = Key.key_left_win;
        aKeyIndexedArr[Key.key_left_alt.aKeyCode] = Key.key_left_alt;
        aKeyIndexedArr[Key.key_spacebar.aKeyCode] = Key.key_spacebar;
        aKeyIndexedArr[Key.key_right_alt.aKeyCode] = Key.key_right_alt;
        aKeyIndexedArr[Key.key_right_win.aKeyCode] = Key.key_right_win;
        aKeyIndexedArr[Key.key_menu.aKeyCode] = Key.key_menu;
        aKeyIndexedArr[Key.key_right_ctrl.aKeyCode] = Key.key_right_ctrl;
        aKeyIndexedArr[Key.key_print_screen.aKeyCode] = Key.key_print_screen;
        aKeyIndexedArr[Key.key_scroll_lock.aKeyCode] = Key.key_scroll_lock;
        aKeyIndexedArr[Key.key_pause.aKeyCode] = Key.key_pause;
        aKeyIndexedArr[Key.key_insert.aKeyCode] = Key.key_insert;
        aKeyIndexedArr[Key.key_home.aKeyCode] = Key.key_home;
        aKeyIndexedArr[Key.key_page_up.aKeyCode] = Key.key_page_up;
        aKeyIndexedArr[Key.key_delete.aKeyCode] = Key.key_delete;
        aKeyIndexedArr[Key.key_end.aKeyCode] = Key.key_end;
        aKeyIndexedArr[Key.key_page_down.aKeyCode] = Key.key_page_down;
        aKeyIndexedArr[Key.key_up.aKeyCode] = Key.key_up;
        aKeyIndexedArr[Key.key_left.aKeyCode] = Key.key_left;
        aKeyIndexedArr[Key.key_down.aKeyCode] = Key.key_down;
        aKeyIndexedArr[Key.key_right.aKeyCode] = Key.key_right;
        aKeyIndexedArr[Key.key_number_lock.aKeyCode] = Key.key_number_lock;
        aKeyIndexedArr[Key.key_keypad_slash.aKeyCode] = Key.key_keypad_slash;
        aKeyIndexedArr[Key.key_keypad_asterisk.aKeyCode] = Key.key_keypad_asterisk;
        aKeyIndexedArr[Key.key_keypad_minus.aKeyCode] = Key.key_keypad_minus;
        aKeyIndexedArr[Key.key_keypad_7.aKeyCode] = Key.key_keypad_7;
        aKeyIndexedArr[Key.key_keypad_8.aKeyCode] = Key.key_keypad_8;
        aKeyIndexedArr[Key.key_keypad_9.aKeyCode] = Key.key_keypad_9;
        aKeyIndexedArr[Key.key_keypad_4.aKeyCode] = Key.key_keypad_4;
        aKeyIndexedArr[Key.key_keypad_5.aKeyCode] = Key.key_keypad_5;
        aKeyIndexedArr[Key.key_keypad_6.aKeyCode] = Key.key_keypad_6;
        aKeyIndexedArr[Key.key_keypad_1.aKeyCode] = Key.key_keypad_1;
        aKeyIndexedArr[Key.key_keypad_2.aKeyCode] = Key.key_keypad_2;
        aKeyIndexedArr[Key.key_keypad_3.aKeyCode] = Key.key_keypad_3;
        aKeyIndexedArr[Key.key_keypad_0.aKeyCode] = Key.key_keypad_0;
        aKeyIndexedArr[Key.key_keypad_dot.aKeyCode] = Key.key_keypad_dot;
        aKeyIndexedArr[Key.key_keypad_plus.aKeyCode] = Key.key_keypad_plus;
        aKeyIndexedArr[Key.key_keypad_enter.aKeyCode] = Key.key_keypad_enter;


//        aKeyIndexedArr[Key.key_pointer_left.aKeyCode] = Key.key_pointer_left;
//        aKeyIndexedArr[Key.key_pointer_scroll_up.aKeyCode] = Key.key_pointer_scroll_up;
//        aKeyIndexedArr[Key.key_pointer_scroll_down.aKeyCode] = Key.key_pointer_scroll_down;
//        aKeyIndexedArr[Key.key_pointer_right.aKeyCode] = Key.key_pointer_right;
//        aKeyIndexedArr[Key.key_pointer_body_stub.aKeyCode] = Key.key_pointer_body_stub;

    }
}
