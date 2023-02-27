package com.example.datainsert.exagear.controls.interfaceOverlay;

import static android.widget.LinearLayout.VERTICAL;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_FILE_NAME_SETTING;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_HEIGHT;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_WIDTH;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SIDEBAR_COLOR;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.BtnContainer;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.JoyStickBtn;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.RegularKeyBtn;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.UnmovableBtn;
import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.KeyCodes3;
import com.example.datainsert.exagear.controls.model.OneCol;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 用于维护keycode，填充布局和生成touchArea
 */
public class FalloutUIAndAreaAdapter {
    private final BtnContainer mBtnContainer;
    private final LinearLayout mLeftBar;
    private final LinearLayout mRightBar;
    private KeyCodes2 mKeyCodes2;
    private KeyCodes3 mKeyCodes3;

    public FalloutUIAndAreaAdapter(BtnContainer mBtnContainer, LinearLayout mLeftBar, LinearLayout mRightBar){
        this.mBtnContainer = mBtnContainer;
        this.mLeftBar = mLeftBar;
        this.mRightBar = mRightBar;
    }


}
