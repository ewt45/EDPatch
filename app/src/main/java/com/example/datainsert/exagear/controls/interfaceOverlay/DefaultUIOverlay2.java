package com.example.datainsert.exagear.controls.interfaceOverlay;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_FILE_NAME_SETTING;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SHOW_CURSOR;


import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.activities.StartupActivity;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.configuration.TouchScreenControlsInputConfiguration;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsWidget;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;
import com.eltechs.ed.R;
import com.eltechs.ed.controls.Controls;
import com.eltechs.ed.controls.touchControls.AbstractTCF;
import com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay;

/* loaded from: classes.dex */
public class DefaultUIOverlay2 extends DefaultUIOverlay {

    public DefaultUIOverlay2(Controls controls, AbstractTCF abstractTCF) {
        super(controls, abstractTCF);
    }

    @Override
    public View attach(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {

        //设置鼠标显隐
        viewOfXServer.getConfiguration().setShowCursor(xServerDisplayActivity.getSharedPreferences(PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE).getBoolean(PREF_KEY_SHOW_CURSOR,false));

        return super.attach(xServerDisplayActivity, viewOfXServer);
    }
}