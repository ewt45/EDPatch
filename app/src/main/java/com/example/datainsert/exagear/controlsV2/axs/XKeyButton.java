package com.example.datainsert.exagear.controlsV2.axs;

import android.view.KeyEvent;

import java.lang.reflect.Field;
import java.util.Objects;

public class XKeyButton {
    /** 以安卓的KeyCode为索引,对应元素是 Key.Info的数组，用于处理安卓输入法输入的文字/按键 */
    public static Info[] aKeyIndexedArr = new Info[KeyEvent.getMaxKeyCode()+1];
    /** 记录x keycode对应的显示名称*/
    public static String[] xKeyNameArr = new String[0x300+7];
    public static final int POINTER_LEFT = 1;
    public static final int POINTER_CENTER = 2;
    public static final int POINTER_RIGHT = 3;
    public static final int POINTER_SCROLL_UP = 4;
    public static final int POINTER_SCROLL_DOWN = 5;

    final public static Info key_esc = new Info(1,0,KeyEvent.KEYCODE_ESCAPE,"Esc");
    final public static Info key_f1 = new Info(59,0,KeyEvent.KEYCODE_F1,"F1");
    final public static Info key_f2 = new Info(60,0,KeyEvent.KEYCODE_F2,"F2");
    final public static Info key_f3 = new Info(61,0,KeyEvent.KEYCODE_F3,"F3");
    final public static Info key_f4 = new Info(62,0,KeyEvent.KEYCODE_F4,"F4");
    final public static Info key_f5 = new Info(63,0,KeyEvent.KEYCODE_F5,"F5");
    final public static Info key_f6 = new Info(64,0,KeyEvent.KEYCODE_F6,"F6");
    final public static Info key_f7 = new Info(65,0,KeyEvent.KEYCODE_F7,"F7");
    final public static Info key_f8 = new Info(66,0,KeyEvent.KEYCODE_F8,"F8");
    final public static Info key_f9 = new Info(67,0,KeyEvent.KEYCODE_F9,"F9");
    final public static Info key_f10 = new Info(68,0,KeyEvent.KEYCODE_F10,"F10");
    final public static Info key_f11 = new Info(87,0,KeyEvent.KEYCODE_F11,"F11");
    final public static Info key_f12 = new Info(88,0,KeyEvent.KEYCODE_F12,"F12");
    final public static Info key_grave = new Info(41,0,KeyEvent.KEYCODE_GRAVE,"~\n`");
    final public static Info key_1 = new Info(2,0,KeyEvent.KEYCODE_1,"!\n1");
    final public static Info key_2 = new Info(3,0,KeyEvent.KEYCODE_2,"@\n2");
    final public static Info key_3 = new Info(4,0,KeyEvent.KEYCODE_3,"#\n3");
    final public static Info key_4 = new Info(5,0,KeyEvent.KEYCODE_4,"$\n4");
    final public static Info key_5 = new Info(6,0,KeyEvent.KEYCODE_5,"%\n5");
    final public static Info key_6 = new Info(7,0,KeyEvent.KEYCODE_6,"^\n6");
    final public static Info key_7 = new Info(8,0,KeyEvent.KEYCODE_7,"&\n7");
    final public static Info key_8 = new Info(9,0,KeyEvent.KEYCODE_8,"*\n8");
    final public static Info key_9 = new Info(10,0,KeyEvent.KEYCODE_9,"(\n9");
    final public static Info key_0 = new Info(11,0,KeyEvent.KEYCODE_0,")\n0");
    final public static Info key_minus = new Info(12,0,KeyEvent.KEYCODE_MINUS,"_\n-");
    final public static Info key_equal = new Info(13,0,KeyEvent.KEYCODE_EQUALS,"+\n=");
    final public static Info key_backspace = new Info(14,0,KeyEvent.KEYCODE_DEL,"BackSpace");
    final public static Info key_tab = new Info(15,0,KeyEvent.KEYCODE_TAB,"Tab");
    final public static Info key_q = new Info(16,0,KeyEvent.KEYCODE_Q,"Q");
    final public static Info key_w = new Info(17,0,KeyEvent.KEYCODE_W,"W");
    final public static Info key_e = new Info(18,0,KeyEvent.KEYCODE_E,"E");
    final public static Info key_r = new Info(19,0,KeyEvent.KEYCODE_R,"R");
    final public static Info key_t = new Info(20,0,KeyEvent.KEYCODE_T,"T");
    final public static Info key_y = new Info(21,0,KeyEvent.KEYCODE_Y,"Y");
    final public static Info key_u = new Info(22,0,KeyEvent.KEYCODE_U,"U");
    final public static Info key_i = new Info(23,0,KeyEvent.KEYCODE_I,"I");
    final public static Info key_o = new Info(24,0,KeyEvent.KEYCODE_O,"O");
    final public static Info key_p = new Info(25,0,KeyEvent.KEYCODE_P,"P");
    final public static Info key_open_bracket = new Info(26,0,KeyEvent.KEYCODE_LEFT_BRACKET,"{\n[");
    final public static Info key_close_bracket = new Info(27,0,KeyEvent.KEYCODE_RIGHT_BRACKET,"}\n]");
    final public static Info key_backslash = new Info(43,0,KeyEvent.KEYCODE_BACKSLASH,"|\n\\");
    final public static Info key_caps_lock = new Info(58,0,KeyEvent.KEYCODE_CAPS_LOCK,"Caps Lock");
    final public static Info key_a = new Info(30,0,KeyEvent.KEYCODE_A,"A");
    final public static Info key_s = new Info(31,0,KeyEvent.KEYCODE_S,"S");
    final public static Info key_d = new Info(32,0,KeyEvent.KEYCODE_D,"D");
    final public static Info key_f = new Info(33,0,KeyEvent.KEYCODE_F,"F");
    final public static Info key_g = new Info(34,0,KeyEvent.KEYCODE_G,"G");
    final public static Info key_h = new Info(35,0,KeyEvent.KEYCODE_H,"H");
    final public static Info key_j = new Info(36,0,KeyEvent.KEYCODE_J,"J");
    final public static Info key_k = new Info(37,0,KeyEvent.KEYCODE_K,"K");
    final public static Info key_l = new Info(38,0,KeyEvent.KEYCODE_L,"L");
    final public static Info key_semicolon = new Info(39,0,KeyEvent.KEYCODE_SEMICOLON,":\n;");
    final public static Info key_apostrophe = new Info(40,0,KeyEvent.KEYCODE_APOSTROPHE,"\"\n'");
    final public static Info key_enter = new Info(28,0,KeyEvent.KEYCODE_ENTER,"Enter");// ⏎
    final public static Info key_left_shift = new Info(42,0,KeyEvent.KEYCODE_SHIFT_LEFT,"Shift");
    final public static Info key_z = new Info(44,0,KeyEvent.KEYCODE_Z,"Z");
    final public static Info key_x = new Info(45,0,KeyEvent.KEYCODE_X,"X");
    final public static Info key_c = new Info(46,0,KeyEvent.KEYCODE_C,"C");
    final public static Info key_v = new Info(47,0,KeyEvent.KEYCODE_V,"V");
    final public static Info key_b = new Info(48,0,KeyEvent.KEYCODE_B,"B");
    final public static Info key_n = new Info(49,0,KeyEvent.KEYCODE_N,"N");
    final public static Info key_m = new Info(50,0,KeyEvent.KEYCODE_M,"M");
    final public static Info key_comma = new Info(51,0,KeyEvent.KEYCODE_COMMA,"<\n,");
    final public static Info key_dot = new Info(52,0,KeyEvent.KEYCODE_PERIOD,">\n.");
    final public static Info key_slash = new Info(53,0,KeyEvent.KEYCODE_SLASH,"?\n/");
    final public static Info key_right_shift = new Info(54,0,KeyEvent.KEYCODE_SHIFT_RIGHT,"Shift");
    final public static Info key_left_ctrl = new Info(29,0,KeyEvent.KEYCODE_CTRL_LEFT,"Ctrl");
    final public static Info key_left_win = new Info(0,0,KeyEvent.KEYCODE_UNKNOWN,"Win");
    final public static Info key_left_alt = new Info(56,0,KeyEvent.KEYCODE_ALT_LEFT,"Alt");
    final public static Info key_spacebar = new Info(57,0,KeyEvent.KEYCODE_SPACE,"Space");
    final public static Info key_right_alt = new Info(100,0,KeyEvent.KEYCODE_ALT_RIGHT,"AltGr");
    final public static Info key_right_win = new Info(0,0,KeyEvent.KEYCODE_UNKNOWN,"Win");
    final public static Info key_menu = new Info(139,0,KeyEvent.KEYCODE_MENU,"Menu");
    final public static Info key_right_ctrl = new Info(97,0,KeyEvent.KEYCODE_CTRL_RIGHT,"Ctrl");
    final public static Info key_print_screen = new Info(99,0,KeyEvent.KEYCODE_SYSRQ,"PrtSc\nSysRq");
    final public static Info key_scroll_lock = new Info(70,0,KeyEvent.KEYCODE_SCROLL_LOCK,"Scroll\nLock");
    final public static Info key_pause = new Info(119,0,KeyEvent.KEYCODE_BREAK,"Pause\nBreak");
    final public static Info key_insert = new Info(110,0,KeyEvent.KEYCODE_INSERT,"Insert");
    final public static Info key_home = new Info(102,0,KeyEvent.KEYCODE_HOME,"Home");
    final public static Info key_page_up = new Info(104,0,KeyEvent.KEYCODE_PAGE_UP,"Page\nUp");
    final public static Info key_delete = new Info(111,0,KeyEvent.KEYCODE_FORWARD_DEL,"Delete");
    final public static Info key_end = new Info(107,0,KeyEvent.KEYCODE_MOVE_END,"End");
    final public static Info key_page_down = new Info(109,0,KeyEvent.KEYCODE_PAGE_DOWN,"Page\nDown");
    final public static Info key_up = new Info(103,0,KeyEvent.KEYCODE_DPAD_UP,"↑"); 
    final public static Info key_left = new Info(105,0,KeyEvent.KEYCODE_DPAD_LEFT,"←");
    final public static Info key_down = new Info(108,0,KeyEvent.KEYCODE_DPAD_DOWN,"↓");
    final public static Info key_right = new Info(106,0,KeyEvent.KEYCODE_DPAD_RIGHT,"→");
    final public static Info key_number_lock = new Info(69,0,KeyEvent.KEYCODE_NUM_LOCK,"Num\nLock");
    final public static Info key_keypad_slash = new Info(98,0,KeyEvent.KEYCODE_NUMPAD_DIVIDE,"/");
    final public static Info key_keypad_asterisk = new Info(55,0,KeyEvent.KEYCODE_NUMPAD_MULTIPLY,"*");
    final public static Info key_keypad_minus = new Info(74,0,KeyEvent.KEYCODE_NUMPAD_SUBTRACT,"-");
    final public static Info key_keypad_7 = new Info(71,0,KeyEvent.KEYCODE_NUMPAD_7,"7");
    final public static Info key_keypad_8 = new Info(72,0,KeyEvent.KEYCODE_NUMPAD_8,"8");
    final public static Info key_keypad_9 = new Info(73,0,KeyEvent.KEYCODE_NUMPAD_9,"9");
    final public static Info key_keypad_4 = new Info(75,0,KeyEvent.KEYCODE_NUMPAD_4,"4");
    final public static Info key_keypad_5 = new Info(76,0,KeyEvent.KEYCODE_NUMPAD_5,"5");
    final public static Info key_keypad_6 = new Info(77,0,KeyEvent.KEYCODE_NUMPAD_6,"6");
    final public static Info key_keypad_1 = new Info(79,0,KeyEvent.KEYCODE_NUMPAD_1,"1");
    final public static Info key_keypad_2 = new Info(80,0,KeyEvent.KEYCODE_NUMPAD_2,"2");
    final public static Info key_keypad_3 = new Info(81,0,KeyEvent.KEYCODE_NUMPAD_3,"3");
    final public static Info key_keypad_0 = new Info(82,0,KeyEvent.KEYCODE_NUMPAD_0,"0");
    final public static Info key_keypad_dot = new Info(83,0,KeyEvent.KEYCODE_NUMPAD_DOT,".");
    final public static Info key_keypad_plus = new Info(78,0,KeyEvent.KEYCODE_NUMPAD_ADD,"+");
    final public static Info key_keypad_enter = new Info(96,0,KeyEvent.KEYCODE_NUMPAD_ENTER,"Enter");

    final public static Info key_max = new Info(0x2ff,0,KeyEvent.KEYCODE_UNKNOWN,"最后一个按键"); //仅用作记录xkeycode的最大值
    /**
     * 由于 键盘keycode和鼠标的buttoncode混在一起用了，所以需要用个mask隔开一下，规定大于0x300的就是鼠标按键，减去0x300是实际buttoncode
     * <br/> 比如左键就是0x300 | 1 = 0x301;
     * <br/> pointermask只要保证大于 <a href="https://elixir.bootlin.com/linux/v6.8/source/include/uapi/linux/input-event-codes.h#L808">KEY_MAX</a> 就可以了
     * <br/> 由于需要借助key_max，所以必须声明到它下面
     */
    public static final int POINTER_MASK = key_max.xKeyCode+1; //0x300
    final public static Info pointer_left = new Info(POINTER_MASK|POINTER_LEFT,0,KeyEvent.KEYCODE_UNKNOWN,"🖱️\nLeft");
    final public static Info pointer_right = new Info(POINTER_MASK|POINTER_RIGHT,0,KeyEvent.KEYCODE_UNKNOWN,"🖱️\nRight");
    final public static Info pointer_center = new Info(POINTER_MASK|POINTER_CENTER,0,KeyEvent.KEYCODE_UNKNOWN,"️🖱️\nCenter");
    final public static Info pointer_scroll_up = new Info(POINTER_MASK|POINTER_SCROLL_UP,0,KeyEvent.KEYCODE_UNKNOWN,"Scroll\nUp");
    final public static Info pointer_scroll_down = new Info(POINTER_MASK|POINTER_SCROLL_DOWN,0,KeyEvent.KEYCODE_UNKNOWN,"Scroll\nDown");
    final public static Info pointer_body_stub = new Info(0,0,KeyEvent.KEYCODE_UNKNOWN,"🖱️");


    //static块必须在这些变量之后声明，否则反射获取到的都是null
    static{
        Field[] keyFields = XKeyButton.class.getFields();
        //直接反射填充akey映射数组，和xkeycode对应名字吧
        try {
            for(Field field:keyFields){
                Object obj = field.get(null);
                if(!(obj instanceof Info))
                    continue;
                Info info = (Info) Objects.requireNonNull(obj);
                aKeyIndexedArr[info.aKeyCode] = info;
                xKeyNameArr[info.xKeyCode] = info.name;
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //确保这些没有对应的按键名称
        xKeyNameArr[0] = null;
        xKeyNameArr[POINTER_MASK] = null;

        //TODO 1. 没有做unicode的映射 2.exa的akeycode映射里没有手柄映射，那它是怎么支持手柄的，难道在unicodeMap里映射的？
        aKeyIndexedArr[KeyEvent.KEYCODE_UNKNOWN] = null; //确保未知按键没有映射
        //一些Key.Info里没记录的
        aKeyIndexedArr[KeyEvent.KEYCODE_AT] = XKeyButton.key_2;
        aKeyIndexedArr[KeyEvent.KEYCODE_POUND] = XKeyButton.key_3;
        aKeyIndexedArr[KeyEvent.KEYCODE_STAR] = XKeyButton.key_8;
        aKeyIndexedArr[KeyEvent.KEYCODE_PLUS] = XKeyButton.key_equal;
        //（根据exa中的按键映射来查缺补漏一下）
        aKeyIndexedArr[KeyEvent.KEYCODE_BACK] = XKeyButton.key_esc; //back理论上应该不会进入这里吧，因为被我拦截下来用于显示菜单了
        aKeyIndexedArr[KeyEvent.KEYCODE_MOVE_HOME] = XKeyButton.key_home;
        aKeyIndexedArr[KeyEvent.KEYCODE_COMMA] = XKeyButton.key_comma;
    }

    public static class Info{
        public int xKeyCode;
        public int xKeySym;
        public int aKeyCode;
        public String name;
        public Info(int xKeyCode, int xKeySym, int aKeyCode, String name){
            this.xKeyCode = xKeyCode;
            this.xKeySym = xKeySym;
            this.aKeyCode = aKeyCode;
            this.name = name;
        }
    }
}
