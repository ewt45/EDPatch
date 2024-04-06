package com.eltechs.axs.gamesControls;

import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.eltechs.axs.ButtonEventReporter;
import com.eltechs.axs.CommonApplicationConfigurationAccessor;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.StateButton;
import com.eltechs.axs.TouchScreenControlsFactory;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels;
import com.eltechs.axs.activities.menus.Quit;
import com.eltechs.axs.activities.menus.ShowKeyboard;
import com.eltechs.axs.activities.menus.ShowUsage;
import com.eltechs.axs.activities.menus.ToggleHorizontalStretch;
import com.eltechs.axs.activities.menus.ToggleUiOverlaySidePanels;
import com.eltechs.axs.configuration.TouchScreenControlsInputConfiguration;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsWidget;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;
import java.util.Arrays;

/**
 * @deprecated 非原版代码
 */
@Deprecated
public class HybridInterfaceOverlay implements XServerDisplayActivityInterfaceOverlay, XServerDisplayActivityUiOverlaySidePanels {
    public static final float buttonSizeInches = 0.4f;
    private static final float buttonSzNormalDisplayInches = 0.45f;
    private static final float buttonSzSmallDisplayInches = 0.4f;
    private static final float displaySizeThresholdInches = 5.0f;
    private int buttonHeight;
    private int buttonWidth;
    private final int buttonWidthPixelsFixup = 30;
    private final TouchScreenControlsFactory controlsFactory = new HybridScreenControlsFactory();
    private boolean isToolbarsVisible = true;
    private View leftToolbar;
    protected ViewOfXServer mViewOfXServer;
    protected ViewFacade mXServerFacade;
    private View rightToolbar;
    private TouchScreenControlsWidget tscWidget;

    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
    public View attach(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        this.tscWidget = new TouchScreenControlsWidget(xServerDisplayActivity, viewOfXServer, this.controlsFactory, TouchScreenControlsInputConfiguration.DEFAULT);
        this.tscWidget.setZOrderMediaOverlay(true);
        this.mViewOfXServer = viewOfXServer;
        this.mXServerFacade = viewOfXServer.getXServerFacade();
        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
        this.buttonWidth = (int) ((((((float) displayMetrics.widthPixels) / ((float) displayMetrics.densityDpi)) > displaySizeThresholdInches ? 1 : ((((float) displayMetrics.widthPixels) / ((float) displayMetrics.densityDpi)) == displaySizeThresholdInches ? 0 : -1)) < 0 ? 0.4f : buttonSzNormalDisplayInches) * displayMetrics.densityDpi);
        this.buttonHeight = displayMetrics.heightPixels / 8;
        int i = this.buttonHeight;
        int i2 = this.buttonWidth;
        if (i > i2) {
            i = i2;
        }
        this.buttonHeight = i;
        FrameLayout frameLayout = new FrameLayout(xServerDisplayActivity);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        LinearLayout linearLayout = new LinearLayout(xServerDisplayActivity);
        linearLayout.setOrientation(0);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        linearLayout.addView(createLeftToolbar(xServerDisplayActivity, viewOfXServer));
        linearLayout.addView(this.tscWidget, new LinearLayout.LayoutParams(0, -1, 1.0f));
        linearLayout.addView(createRightToolbar(xServerDisplayActivity, viewOfXServer));
        viewOfXServer.setHorizontalStretchEnabled(new CommonApplicationConfigurationAccessor().isHorizontalStretchEnabled());
        xServerDisplayActivity.addDefaultPopupMenu(Arrays.asList(new ShowKeyboard(), new ToggleHorizontalStretch(), new ToggleUiOverlaySidePanels(), new ShowUsage(), new Quit()));
        viewOfXServer.getConfiguration().setShowCursor(true);
        frameLayout.addView(linearLayout);
        return frameLayout;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
    public void detach() {
        this.tscWidget.detach();
        this.tscWidget = null;
        this.leftToolbar = null;
        this.rightToolbar = null;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels
    public boolean isSidePanelsVisible() {
        return this.isToolbarsVisible;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels
    public void toggleSidePanelsVisibility() {
        this.isToolbarsVisible = !this.isToolbarsVisible;
        if (this.isToolbarsVisible) {
            this.leftToolbar.setVisibility(0);
            this.rightToolbar.setVisibility(0);
            return;
        }
        this.leftToolbar.setVisibility(8);
        this.rightToolbar.setVisibility(8);
    }

    private StateButton createNormalButton(Activity activity, KeyCodesX keyCodesX, String str) {
        return new StateButton(activity, new ButtonEventReporter(this.mXServerFacade, keyCodesX), false, this.buttonWidth, this.buttonHeight, str);
    }

    private StateButton createStateButton(Activity activity, KeyCodesX keyCodesX, String str) {
        return new StateButton(activity, new ButtonEventReporter(this.mXServerFacade, keyCodesX), true, this.buttonWidth, this.buttonHeight, str);
    }

    LinearLayout createScrollView(Activity activity) {
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(this.buttonWidth, -1));
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(Color.parseColor("#292c33"));
        return linearLayout;
    }

    private View createLeftToolbar(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        LinearLayout createScrollView = createScrollView(xServerDisplayActivity);
        createScrollView.addView(createStateButton(xServerDisplayActivity, KeyCodesX.KEY_SHIFT_LEFT, "SHIFT"));
        createScrollView.addView(createStateButton(xServerDisplayActivity, KeyCodesX.KEY_CONTROL_LEFT, "CTRL"));
        createScrollView.addView(createStateButton(xServerDisplayActivity, KeyCodesX.KEY_ALT_LEFT, "ALT"));
        ScrollView scrollView = new ScrollView(xServerDisplayActivity);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        scrollView.setBackgroundColor(Color.parseColor("#292c33"));
        LinearLayout createScrollView2 = createScrollView(xServerDisplayActivity);
        createLeftScrollViewWithButtons(xServerDisplayActivity, createScrollView2);
        scrollView.addView(createScrollView2);
        createScrollView.addView(scrollView);
        if (!this.isToolbarsVisible) {
            createScrollView.setVisibility(8);
        }
        this.leftToolbar = createScrollView;
        return createScrollView;
    }

    private void createLeftScrollViewWithButtons(Activity activity, LinearLayout linearLayout) {
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_UP, "↑"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_DOWN, "↓"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_LEFT, "←"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_RIGHT, "→"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_A, "A"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_B, "B"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_C, "C"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_D, "D"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_E, "E"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F, "F"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_G, "G"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_H, "H"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_I, "I"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_J, "J"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_K, "K"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_L, "L"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_M, "M"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_N, "N"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_O, "O"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_P, "P"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_Q, "Q"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_R, "R"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_S, "S"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_T, "T"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_U, "U"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_V, "V"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_W, "W"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_X, "X"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_Y, "Y"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_Z, "Z"));
    }

    private View createRightToolbar(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        LinearLayout createScrollView = createScrollView(xServerDisplayActivity);
        ScrollView scrollView = new ScrollView(xServerDisplayActivity);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        scrollView.setBackgroundColor(Color.parseColor("#292c33"));
        LinearLayout createScrollView2 = createScrollView(xServerDisplayActivity);
        createRightScrollViewWithButtons(xServerDisplayActivity, createScrollView2);
        scrollView.addView(createScrollView2);
        createScrollView.addView(scrollView);
        if (!this.isToolbarsVisible) {
            createScrollView.setVisibility(8);
        }
        this.rightToolbar = createScrollView;
        return createScrollView;
    }

    private void createRightScrollViewWithButtons(Activity activity, LinearLayout linearLayout) {
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_ESC, "Esc"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_RETURN, "Ren"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_SPACE, "Spa"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_TAB, "Tab"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_BACKSPACE, "Bap"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_1, "1"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_2, "2"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_3, "3"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_4, "4"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_5, "5"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_6, "6"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_7, "7"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_8, "8"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_9, "9"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_0, "0"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F1, "F1"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F2, "F2"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F3, "F3"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F4, "F4"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F5, "F5"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F6, "F6"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F7, "F7"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F8, "F8"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F9, "F9"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F10, "F10"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F11, "F11"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F12, "F12"));
    }
}
