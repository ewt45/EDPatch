package com.example.datainsert.exagear.controls;

import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.ed.controls.Controls;
import com.example.datainsert.exagear.controls.interfaceOverlay.FalloutInterfaceOverlay2;

/**
 * 处理操作模式。请勿随意移动此类
 */
public class ControlsResolver {
    public final static String PREF_FILE_NAME_SETTING = "some_settings";
    public final static String PREF_KEY_USE_CUSTOM_CONTROL = "use_custom_control";
    public final static String PREF_KEY_SHOW_CURSOR="show_cursor";
    public final static String PREF_KEY_BTN_BG_COLOR = "btn_background_color";
    public final static String PREF_KEY_BTN_TXT_COLOR = "btn_txt_color";
    public final static String PREF_KEY_SIDEBAR_COLOR="sidebar_color";
    /**
     * 按钮宽度，单位px
     */
    public final static String PREF_KEY_BTN_WIDTH="btn_width";
    /**
     * 按钮高度，单位px
     */
    public final static String PREF_KEY_BTN_HEIGHT="btn_height";
    public final static String PREF_KEY_BTN_ON_WIDGET="btn_on_widget";
    public final static String PREF_KEY_BTN_BG_RIPPLE="btn_bg_ripple";
    public final static String PREF_KEY_CUSTOM_BTN_POS="custom_btn_pos";

    public final static String PREF_KEY_MOUSE_MOVE_RELATIVE="mouse_move_relative";

    /**
     * 背景透明度，0-255。文字透明度为该值*2/3+85
     */
    public final static String PREF_KEY_BTN_ALPHA = "btn_alpha";
    public static XServerDisplayActivityInterfaceOverlay getCurrentControls(Controls controls) {
//        //如果preference不存在，不写入直接获取会出错吗(没问题）
//        boolean b = Globals.getAppContext().getSharedPreferences(PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE).getBoolean(PREF_KEY_USE_CUSTOM_CONTROL, false);
//        //记得在自定义的类里根据偏好显示或隐藏鼠标
//        return b
//                ? new FalloutInterfaceOverlay2()
//                : new DefaultUIOverlay2(controls, new DefaultTCF());
        return new FalloutInterfaceOverlay2();
    }
}
