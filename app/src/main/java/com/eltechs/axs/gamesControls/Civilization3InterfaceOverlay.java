package com.eltechs.axs.gamesControls;

import static com.eltechs.axs.GestureStateMachine.GestureMouseMode.MouseModeState.MOUSE_MODE_LEFT;
import static com.eltechs.axs.GestureStateMachine.GestureMouseMode.MouseModeState.MOUSE_MODE_RIGHT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.eltechs.axs.CommonApplicationConfigurationAccessor;
import com.eltechs.axs.GestureStateMachine.GestureMouseMode;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.R_original;
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
import com.eltechs.axs.widgets.helpers.ButtonHelpers;
import com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsWidget;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.KeyButNames;
import com.eltechs.axs.xserver.KeyboardListener;
import com.eltechs.axs.xserver.KeyboardModifiersListener;
import com.eltechs.axs.xserver.ViewFacade;
import com.eltechs.axs.xserver.impl.masks.Mask;
import java.util.Arrays;
@SuppressLint("WrongConstant")

/* loaded from: classes.dex */
public class Civilization3InterfaceOverlay implements XServerDisplayActivityInterfaceOverlay, XServerDisplayActivityUiOverlaySidePanels {
    private static final float buttonSzNormalDisplayInches = 0.4f;
    private static final float buttonSzSmallDisplayInches = 0.3f;
    private static final float displaySizeThresholdInches = 3.0f;
    private View leftToolbar;
    private TouchScreenControlsWidget tscWidget;
    private boolean isLeftToolbarVisible = true;
    private final GestureMouseMode mouseMode = new GestureMouseMode(MOUSE_MODE_LEFT);
    private final TouchScreenControlsFactory controlsFactory = new Civ3TouchScreenControlsFactory(this.mouseMode);

    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
    public View attach(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        this.tscWidget = new TouchScreenControlsWidget(xServerDisplayActivity, viewOfXServer, this.controlsFactory, TouchScreenControlsInputConfiguration.DEFAULT);
        this.tscWidget.setZOrderMediaOverlay(true);
        LinearLayout linearLayout = new LinearLayout(xServerDisplayActivity);
        linearLayout.setOrientation(0);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        linearLayout.addView(createLeftToolbar(xServerDisplayActivity, viewOfXServer));
        linearLayout.addView(this.tscWidget, new LinearLayout.LayoutParams(0, -1, 1.0f));
        viewOfXServer.setHorizontalStretchEnabled(new CommonApplicationConfigurationAccessor().isHorizontalStretchEnabled());
        xServerDisplayActivity.addDefaultPopupMenu(Arrays.asList(new ShowKeyboard(), new ToggleHorizontalStretch(), new ToggleUiOverlaySidePanels(), new ShowUsage(), new Quit()));
        return linearLayout;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
    public void detach() {
        this.tscWidget.detach();
        this.tscWidget = null;
        this.leftToolbar = null;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels
    public boolean isSidePanelsVisible() {
        return this.isLeftToolbarVisible;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels
    public void toggleSidePanelsVisibility() {
        this.isLeftToolbarVisible = !this.isLeftToolbarVisible;
        if (this.isLeftToolbarVisible) {
            this.leftToolbar.setVisibility(0);
        } else {
            this.leftToolbar.setVisibility(8);
        }
    }

    private View createLeftToolbar(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        LinearLayout linearLayout = new LinearLayout(xServerDisplayActivity);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-2, -1, 0.0f));
        linearLayout.setOrientation(1);
        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
        int i = (int) ((isDisplaySmall(displayMetrics) ? buttonSzSmallDisplayInches : 0.4f) * displayMetrics.densityDpi);
        ViewFacade xServerFacade = viewOfXServer.getXServerFacade();
        linearLayout.addView(createMouseModeButton(xServerDisplayActivity, this.mouseMode, i));
        linearLayout.addView(createShiftButton(xServerDisplayActivity, xServerFacade, i));
        linearLayout.addView(createScrollViewWithButtons(xServerDisplayActivity, xServerFacade, i));
        if (!this.isLeftToolbarVisible) {
            linearLayout.setVisibility(8);
        }
        this.leftToolbar = linearLayout;
        return linearLayout;
    }

    private static boolean isDisplaySmall(DisplayMetrics displayMetrics) {
        return ((float) displayMetrics.widthPixels) / ((float) displayMetrics.densityDpi) < displaySizeThresholdInches;
    }

    private static Button createShiftButton(Activity activity, final ViewFacade viewFacade, int i) {
        final String str_shiftOff = activity.getResources().getString(R_original.string.civ3_shift_off);
        final String str_shiftOn = activity.getResources().getString(R_original.string.civ3_shift_on);
        final Button button = new Button(activity);
        button.setWidth(i);
        button.setMaxWidth(i);
        button.setMinWidth(i);
        button.setHeight(i);
        button.setMaxHeight(i);
        button.setMinHeight(i);
        button.setText(str_shiftOff);
        button.setOnClickListener(view ->
                viewFacade.switchModifierState(KeyButNames.SHIFT, (byte) KeyCodesX.KEY_SHIFT_LEFT.getValue(), true));
        viewFacade.addKeyboardListener(new KeyboardListener() { // from class: com.eltechs.axs.gamesControls.Civilization3InterfaceOverlay.2
            @Override // com.eltechs.axs.xserver.KeyboardListener
            public void keyPressed(byte b, int i2, Mask<KeyButNames> mask) {
            }

            @Override // com.eltechs.axs.xserver.KeyboardListener
            public void keyReleased(byte b, int i2, Mask<KeyButNames> mask) {
                if (b != ((byte) KeyCodesX.KEY_SHIFT_LEFT.getValue()) && b!= ((byte) KeyCodesX.KEY_SHIFT_RIGHT.getValue()))
                    viewFacade.setModifierState(KeyButNames.SHIFT, false, (byte) KeyCodesX.KEY_SHIFT_LEFT.getValue(), true);
            }
        });

        viewFacade.addKeyboardModifiersChangeListener(mask -> button.setText(mask.isSet(KeyButNames.SHIFT) ? str_shiftOn : str_shiftOff));
        return button;
    }

    private static ScrollView createScrollViewWithButtons(Activity activity, ViewFacade viewFacade, int i) {
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        linearLayout.setOrientation(1);
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_R, i, "R"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F, i, "F"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_I, i, "I"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_J, i, "J"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_M, i, "M"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_B, i, "B"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_P, i, "P"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_C, i, "C"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_S, i, "S"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_W, i, "W"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F1, i, "F1"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_DELETE, i, "DEL"));
        scrollView.addView(linearLayout);
        return scrollView;
    }

    private static ImageButton createMouseModeButton(Activity activity, final GestureMouseMode gestureMouseMode, int size) {
        final ImageButton createRegularImageButton = ButtonHelpers.createRegularImageButton(activity, size, size, R_original.drawable.mouse_left);
        createRegularImageButton.setOnClickListener(view -> gestureMouseMode.setState(
                gestureMouseMode.getState().equals(MOUSE_MODE_LEFT)
                        ? MOUSE_MODE_RIGHT
                        : MOUSE_MODE_LEFT));

        gestureMouseMode.addListener((gestureMouseMode2, mouseModeState) -> {
            if (mouseModeState == MOUSE_MODE_LEFT) {
                createRegularImageButton.setImageResource(R_original.drawable.mouse_left);
            } else {
                createRegularImageButton.setImageResource(R_original.drawable.mouse_right);
            }
        });
        return createRegularImageButton;
    }

    private static void setHandlerToButton(Button button, final KeyCodesX keyCodesX, final ViewFacade viewFacade) {
        button.setOnClickListener(view -> viewFacade.injectKeyType((byte) keyCodesX.getValue()));
    }

    private static Button createLetterButton(Activity activity, ViewFacade viewFacade, KeyCodesX keyCodesX, int i, String str) {
        Button button = new Button(activity);
        button.setWidth(i);
        button.setMinWidth(i);
        button.setMaxWidth(i);
        button.setHeight(i);
        button.setMinHeight(i);
        button.setMaxHeight(i);
        button.setText(str);
        setHandlerToButton(button, keyCodesX, viewFacade);
        return button;
    }
}