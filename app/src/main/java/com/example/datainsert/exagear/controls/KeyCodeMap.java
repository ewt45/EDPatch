package com.example.datainsert.exagear.controls;

import com.eltechs.axs.KeyCodesX;

import java.util.HashMap;
import java.util.Map;

public class KeyCodeMap {
    static Map<Integer,KeyCodesX> map=null;

    public static KeyCodesX get(int i){
        if(map == null){
            map = new HashMap<>();
            map.put(0,KeyCodesX.KEY_NONE);
            map.put(1,KeyCodesX.KEY_ESC);
            map.put(2,KeyCodesX.KEY_1);
            map.put(3,KeyCodesX.KEY_2);
            map.put(4,KeyCodesX.KEY_3);
            map.put(5,KeyCodesX.KEY_4);
            map.put(6,KeyCodesX.KEY_5);
            map.put(7,KeyCodesX.KEY_6);
            map.put(8,KeyCodesX.KEY_7);
            map.put(9,KeyCodesX.KEY_8);
            map.put(10,KeyCodesX.KEY_9);
            map.put(11,KeyCodesX.KEY_0);
            map.put(12,KeyCodesX.KEY_MINUS);
            map.put(13,KeyCodesX.KEY_EQUAL);
            map.put(14,KeyCodesX.KEY_BACKSPACE);
            map.put(15,KeyCodesX.KEY_TAB);
            map.put(16,KeyCodesX.KEY_Q);
            map.put(17,KeyCodesX.KEY_W);
            map.put(18,KeyCodesX.KEY_E);
            map.put(19,KeyCodesX.KEY_R);
            map.put(20,KeyCodesX.KEY_T);
            map.put(21,KeyCodesX.KEY_Y);
            map.put(22,KeyCodesX.KEY_U);
            map.put(23,KeyCodesX.KEY_I);
            map.put(24,KeyCodesX.KEY_O);
            map.put(25,KeyCodesX.KEY_P);
            map.put(26,KeyCodesX.KEY_BRACKET_LEFT);
            map.put(27,KeyCodesX.KEY_BRACKET_RIGHT);
            map.put(28,KeyCodesX.KEY_RETURN);
            map.put(29,KeyCodesX.KEY_CONTROL_LEFT);
            map.put(30,KeyCodesX.KEY_A);
            map.put(31,KeyCodesX.KEY_S);
            map.put(32,KeyCodesX.KEY_D);
            map.put(33,KeyCodesX.KEY_F);
            map.put(34,KeyCodesX.KEY_G);
            map.put(35,KeyCodesX.KEY_H);
            map.put(36,KeyCodesX.KEY_J);
            map.put(37,KeyCodesX.KEY_K);
            map.put(38,KeyCodesX.KEY_L);
            map.put(39,KeyCodesX.KEY_SEMICOLON);
            map.put(40,KeyCodesX.KEY_APOSTROPHE);
            map.put(41,KeyCodesX.KEY_GRAVE);
            map.put(42,KeyCodesX.KEY_SHIFT_LEFT);
            map.put(43,KeyCodesX.KEY_BACKSLASH);
            map.put(44,KeyCodesX.KEY_Z);
            map.put(45,KeyCodesX.KEY_X);
            map.put(46,KeyCodesX.KEY_C);
            map.put(47,KeyCodesX.KEY_V);
            map.put(48,KeyCodesX.KEY_B);
            map.put(49,KeyCodesX.KEY_N);
            map.put(50,KeyCodesX.KEY_M);
            map.put(51,KeyCodesX.KEY_COMMA);
            map.put(52,KeyCodesX.KEY_PERIOD);
            map.put(53,KeyCodesX.KEY_SLASH);
            map.put(54,KeyCodesX.KEY_SHIFT_RIGHT);
            map.put(55,KeyCodesX.KEY_KP_MULTIPLY);
            map.put(56,KeyCodesX.KEY_ALT_LEFT);
            map.put(57,KeyCodesX.KEY_SPACE);
            map.put(58,KeyCodesX.KEY_CAPS_LOCK);
            map.put(59,KeyCodesX.KEY_F1);
            map.put(60,KeyCodesX.KEY_F2);
            map.put(61,KeyCodesX.KEY_F3);
            map.put(62,KeyCodesX.KEY_F4);
            map.put(63,KeyCodesX.KEY_F5);
            map.put(64,KeyCodesX.KEY_F6);
            map.put(65,KeyCodesX.KEY_F7);
            map.put(66,KeyCodesX.KEY_F8);
            map.put(67,KeyCodesX.KEY_F9);
            map.put(68,KeyCodesX.KEY_F10);
            map.put(69,KeyCodesX.KEY_NUM_LOCK);
            map.put(70,KeyCodesX.KEY_SCROLL_LOCK);
            map.put(71,KeyCodesX.KEY_KP_7);
            map.put(72,KeyCodesX.KEY_KP_8);
            map.put(73,KeyCodesX.KEY_KP_9);
            map.put(74,KeyCodesX.KEY_KP_SUB);
            map.put(75,KeyCodesX.KEY_KP_4);
            map.put(76,KeyCodesX.KEY_KP_5);
            map.put(77,KeyCodesX.KEY_KP_6);
            map.put(78,KeyCodesX.KEY_KP_ADD);
            map.put(79,KeyCodesX.KEY_KP_1);
            map.put(80,KeyCodesX.KEY_KP_2);
            map.put(81,KeyCodesX.KEY_KP_3);
            map.put(82,KeyCodesX.KEY_KP_0);
            map.put(83,KeyCodesX.KEY_KP_DEL);
            map.put(87,KeyCodesX.KEY_F11);
            map.put(88,KeyCodesX.KEY_F12);
            map.put(96,KeyCodesX.KEY_KP_ENTER);
            map.put(97,KeyCodesX.KEY_CONTROL_RIGHT);
            map.put(98,KeyCodesX.KEY_KP_DIV);
            map.put(99,KeyCodesX.KEY_PRINT);
            map.put(100,KeyCodesX.KEY_ALT_RIGHT);
            map.put(102,KeyCodesX.KEY_HOME);
            map.put(103,KeyCodesX.KEY_UP);
            map.put(104,KeyCodesX.KEY_PRIOR);
            map.put(105,KeyCodesX.KEY_LEFT);
            map.put(106,KeyCodesX.KEY_RIGHT);
            map.put(107,KeyCodesX.KEY_END);
            map.put(108,KeyCodesX.KEY_DOWN);
            map.put(109,KeyCodesX.KEY_NEXT);
            map.put(110,KeyCodesX.KEY_INSERT);
            map.put(111,KeyCodesX.KEY_DELETE);

        }

        return map.get(i)==null?map.get(0):map.get(i);
    }
}
