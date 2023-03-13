package com.example.datainsert.exagear.controls;

import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.ed.controls.Controls;
import com.example.datainsert.exagear.controls.interfaceOverlay.FalloutInterfaceOverlay2;

/**
 * 处理操作模式。请勿随意移动此类
 * 存储了一些sharedPreference数据键名，添加一项时，记得在FormatHelper里添加对应的导入导出
 */
public class ControlsResolver {
    public final static String PREF_FILE_NAME_SETTING = "some_settings";
//    public final static String PREF_KEY_USE_CUSTOM_CONTROL = "use_custom_control";
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
    /**
     * 按钮文字大小，单位sp 范围10sp~30sp 默认14sp . 数据存储为0~20 默认4，计算大小时记得+10
     */
    public final static String PREF_KEY_BTN__TXT_SIZE="btn_text_size";
//    public final static String PREF_KEY_BTN_ON_WIDGET="btn_on_widget";
//    public final static String PREF_KEY_BTN_BG_RIPPLE="btn_bg_ripple";
    public final static String PREF_KEY_CUSTOM_BTN_POS="custom_btn_pos";
    public final static String PREF_KEY_MOUSE_MOVE_RELATIVE="mouse_move_relative";
    /**
     * 背景透明度，0-255。文字透明度为该值*2/3+85
     */
    public final static String PREF_KEY_BTN_ALPHA = "btn_alpha";
    /**
     * 默认值为80。鼠标灵敏度，0.2~3.0. progress设置为0~280. 取出后需要（x+20)/100 方可使用
     */
    public final static String PREF_KEY_MOUSE_SENSITIVITY = "mouse_move_sensitivity";
    /**
     * 转动视角模式。允许鼠标移出屏幕. 默认0
     */
    public final static String PREF_KEY_MOUSE_OFFWINDOW_DISTANCE = "mouse_off_distance";
    /**
     * 鼠标滚轮滚动速度
     */
    public final static String PREF_KEY_MS_SCROLL_SPD = "mouse_scroll_speed";
    /**
     * 按钮是否使用圆形。默认为false，即形状使用默认样式的方形。
     */
    public final static String PREF_KEY_BTN_ROUND_SHAPE = "btn_round_shape";

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
