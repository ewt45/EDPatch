package com.eltechs.axs.gamesControls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.eltechs.axs.ButtonEventListener;
import com.eltechs.axs.ButtonEventReporter;
import com.eltechs.axs.CommonApplicationConfigurationAccessor;
import com.eltechs.axs.FloatButton;
import com.eltechs.axs.GestureStateMachine.GestureJoyStickMode;
import com.eltechs.axs.GestureStateMachine.GestureMouseMode;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.PointerEventReporter;
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
import com.eltechs.axs.xserver.ScreenInfo;
import com.eltechs.axs.xserver.ViewFacade;
import java.util.Arrays;
@SuppressLint("WrongConstant")

/**
 * 非原版代码。暗黑2操作模式
 */
@Deprecated
public class DiabloInterfaceOverlay implements XServerDisplayActivityInterfaceOverlay, XServerDisplayActivityUiOverlaySidePanels {
    public static final float buttonMaxSizeInches = 0.35f;
    private static final float buttonSzNormalDisplayInches = 0.3f;
    private static final float buttonSzSmallDisplayInches = 0.25f;
    private static final float displaySizeThresholdInches = 6.0f;
    private int buttonHeight;
    private int buttonWidth;
    private boolean isDisplayWide;
    private View leftToolbar;
    protected ViewOfXServer mViewOfXServer;
    protected ViewFacade mXServerFacade;
    private View rightToolbar;
    private TouchScreenControlsWidget tscWidget;
    private final int buttonWidthPixelsFixup = 30;
    private final GestureMouseMode mouseMode = new GestureMouseMode(GestureMouseMode.MouseModeState.MOUSE_MODE_LEFT);
    private final GestureJoyStickMode joyStickMode = new GestureJoyStickMode(GestureJoyStickMode.JoyStickModeState.JOYSTICK_MODE_OFF);
    private final TouchScreenControlsFactory controlsFactory = new DiabloTouchScreenControlsFactory(this.mouseMode, this.joyStickMode);
    private boolean isToolbarsVisible = true;

    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
    public View attach(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        FrameLayout frameLayout = new FrameLayout(xServerDisplayActivity);
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

    }

    LinearLayout createScrollView(Activity activity) {
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(this.buttonWidth, -1));
        linearLayout.setBackgroundColor(Color.parseColor("#292c33"));
        return linearLayout;
    }

    private View createLeftToolbar(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        ScrollView scrollView = new ScrollView(xServerDisplayActivity);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        scrollView.setBackgroundColor(Color.parseColor("#292c33"));
        LinearLayout linearLayout = new LinearLayout(xServerDisplayActivity);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-2, -1, 0.0f));
        LinearLayout createScrollView = createScrollView(xServerDisplayActivity);
        createLeftScrollViewWithButtons1(xServerDisplayActivity, createScrollView);
        if (this.isDisplayWide) {
            linearLayout.addView(createScrollView);
            createScrollView = createScrollView(xServerDisplayActivity);
        }
        createLeftScrollViewWithButtons2(xServerDisplayActivity, createScrollView);
        linearLayout.addView(createScrollView);
        scrollView.addView(linearLayout);
        if (!this.isToolbarsVisible) {
            scrollView.setVisibility(8);
        }
        this.leftToolbar = scrollView;
        return scrollView;
    }

    private void createLeftScrollViewWithButtons1(Activity activity, LinearLayout linearLayout) {
        linearLayout.addView(createStateButton(activity, KeyCodesX.KEY_SHIFT_LEFT, "SHIFT"));
        linearLayout.addView(createStateButton(activity, KeyCodesX.KEY_CONTROL_LEFT, "CTRL"));
        linearLayout.addView(createStateButton(activity, KeyCodesX.KEY_ALT_LEFT, "ALT"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_ESC, "ESC"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F, "全显"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_TAB, "地图"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_BACKSPACE, "回城"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_RETURN, "回车"));
    }

    private void createLeftScrollViewWithButtons2(Activity activity, LinearLayout linearLayout) {
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_INSERT, "孔锁"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_D, "四防"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_V, "价格"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_J, "底材"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_Q, "任务"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_P, "队友"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_A, "属性"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_T, "技能"));
    }

    private View createRightToolbar(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        ScrollView scrollView = new ScrollView(xServerDisplayActivity);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        scrollView.setBackgroundColor(Color.parseColor("#292c33"));
        LinearLayout linearLayout = new LinearLayout(xServerDisplayActivity);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-2, -1, 0.0f));
        linearLayout.setOrientation(0);
        LinearLayout createScrollView = createScrollView(xServerDisplayActivity);
        createRightScrollViewWithButtons1(xServerDisplayActivity, createScrollView);
        if (this.isDisplayWide) {
            linearLayout.addView(createScrollView);
            createScrollView = createScrollView(xServerDisplayActivity);
        }
        createRightScrollViewWithButtons2(xServerDisplayActivity, createScrollView);
        linearLayout.addView(createScrollView);
        scrollView.addView(linearLayout);
        if (!this.isToolbarsVisible) {
            scrollView.setVisibility(8);
        }
        this.rightToolbar = scrollView;
        return scrollView;
    }

    private void createRightScrollViewWithButtons1(Activity activity, LinearLayout linearLayout) {
        linearLayout.addView(createMouseModeButton(activity, this.mouseMode));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_W, "主副"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_I, "装备"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_O, "佣兵"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_1, "1"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_2, "2"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_3, "3"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_4, "4"));
    }

    private void createRightScrollViewWithButtons2(Activity activity, LinearLayout linearLayout) {
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F1, "F1"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F2, "F2"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F3, "F3"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F4, "F4"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F5, "F5"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F6, "F6"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F7, "F7"));
        linearLayout.addView(createNormalButton(activity, KeyCodesX.KEY_F8, "F8"));
    }

    private StateButton createNormalButton(Activity activity, KeyCodesX keyCodesX, String str) {
        return new StateButton(activity, new ButtonEventReporter(this.mXServerFacade, keyCodesX), false, this.buttonWidth, this.buttonHeight, str);
    }

    private StateButton createStateButton(Activity activity, KeyCodesX keyCodesX, String str) {
        return new StateButton(activity, new ButtonEventReporter(this.mXServerFacade, keyCodesX), true, this.buttonWidth, this.buttonHeight, str);
    }

    private StateButton createMouseModeButton(Activity activity, final GestureMouseMode gestureMouseMode) {
        final StateButton stateButton = new StateButton(activity, null, false, this.buttonWidth, this.buttonHeight, "左键");
        stateButton.setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.axs.gamesControls.DiabloInterfaceOverlay.1_fix
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (gestureMouseMode.getState().equals(GestureMouseMode.MouseModeState.MOUSE_MODE_LEFT)) {
                    gestureMouseMode.setState(GestureMouseMode.MouseModeState.MOUSE_MODE_RIGHT);
                } else {
                    gestureMouseMode.setState(GestureMouseMode.MouseModeState.MOUSE_MODE_LEFT);
                }
            }
        });
        gestureMouseMode.addListener(new GestureMouseMode.MouseModeChangeListener() { // from class: com.eltechs.axs.gamesControls.DiabloInterfaceOverlay.2_fix
            @Override // com.eltechs.axs.GestureStateMachine.GestureMouseMode.MouseModeChangeListener
            public void mouseModeChanged(GestureMouseMode gestureMouseMode2, GestureMouseMode.MouseModeState mouseModeState) {
                if (mouseModeState == GestureMouseMode.MouseModeState.MOUSE_MODE_LEFT) {
                    stateButton.setText("左键");
                } else {
                    stateButton.setText("右键");
                }
            }
        });
        return stateButton;
    }
}
