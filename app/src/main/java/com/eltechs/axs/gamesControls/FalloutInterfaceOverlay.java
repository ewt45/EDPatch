package com.eltechs.axs.gamesControls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.eltechs.axs.CommonApplicationConfigurationAccessor;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.PointerEventReporter;
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
import com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsWidget;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.KeyButNames;
import com.eltechs.axs.xserver.KeyboardModifiersListener;
import com.eltechs.axs.xserver.ViewFacade;
import com.eltechs.axs.xserver.impl.masks.Mask;
import java.util.Arrays;

/* loaded from: classes.dex */
@SuppressLint("WrongConstant")

public class FalloutInterfaceOverlay implements XServerDisplayActivityInterfaceOverlay, XServerDisplayActivityUiOverlaySidePanels {
    public static final float buttonSizeInches = 0.4f;
    private static final float buttonSzNormalDisplayInches = 0.4f;
    private static final float buttonSzSmallDisplayInches = 0.3f;
    private static final float displaySizeThresholdInches = 5.0f;
    private View leftToolbar;
    private View rightToolbar;
    private TouchScreenControlsWidget tscWidget;
    private final int buttonWidthPixelsFixup = 30;
    private boolean isToolbarsVisible = true;
    private final TouchScreenControlsFactory controlsFactory = new FalloutTouchScreenControlsFactory();

    private static boolean isDisplaySmall(DisplayMetrics displayMetrics) {
        return ((float) displayMetrics.widthPixels) / ((float) displayMetrics.densityDpi) < displaySizeThresholdInches;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
    public View attach(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        this.tscWidget = new TouchScreenControlsWidget(xServerDisplayActivity, viewOfXServer, this.controlsFactory, TouchScreenControlsInputConfiguration.DEFAULT);
        this.tscWidget.setZOrderMediaOverlay(true);
        LinearLayout linearLayout = new LinearLayout(xServerDisplayActivity);
        linearLayout.setOrientation(0);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        linearLayout.addView(createLeftToolbar(xServerDisplayActivity, viewOfXServer));
        linearLayout.addView(this.tscWidget, new LinearLayout.LayoutParams(0, -1, 1.0f));
        linearLayout.addView(createRightToolbar(xServerDisplayActivity, viewOfXServer));
        viewOfXServer.setHorizontalStretchEnabled(new CommonApplicationConfigurationAccessor().isHorizontalStretchEnabled());
        xServerDisplayActivity.addDefaultPopupMenu(Arrays.asList(new ShowKeyboard(), new ToggleHorizontalStretch(), new ToggleUiOverlaySidePanels(), new ShowUsage(), new Quit()));
        return linearLayout;
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

    private static Button createSyncButton(Activity activity, final ViewOfXServer viewOfXServer, int i) {
        String string = activity.getResources().getString(R_original.string.fal_sync);
        Button button = new Button(activity);
        button.setWidth(50);
        button.setMaxWidth(50);
        button.setMinWidth(50);
        button.setHeight(50);
        button.setMaxHeight(50);
        button.setMinHeight(50);
        button.setText(string);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.axs.gamesControls.FalloutInterfaceOverlay.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ViewFacade xServerFacade = viewOfXServer.getXServerFacade();
                PointerEventReporter pointerEventReporter = new PointerEventReporter(viewOfXServer);
                pointerEventReporter.pointerMove(0.0f, 0.0f);
                try {
                    Thread.sleep(40L, 0);
                } catch (InterruptedException unused) {
                }
                pointerEventReporter.pointerMove(xServerFacade.getScreenInfo().widthInPixels, 0.0f);
                try {
                    Thread.sleep(40L, 0);
                } catch (InterruptedException unused2) {
                }
                pointerEventReporter.pointerMove(xServerFacade.getScreenInfo().widthInPixels + 1000, xServerFacade.getScreenInfo().heightInPixels + 1000);
                try {
                    Thread.sleep(40L, 0);
                } catch (InterruptedException unused3) {
                }
                pointerEventReporter.pointerMove(0.0f, xServerFacade.getScreenInfo().heightInPixels);
                try {
                    Thread.sleep(40L, 0);
                } catch (InterruptedException unused4) {
                }
                pointerEventReporter.pointerMove(50.0f, 50.0f);
            }
        });
        return button;
    }

    private static Button createAltButton(Activity activity, final ViewFacade viewFacade, int i) {

        final Button button = new Button(activity);

        return button;
    }

    private static Button createCtrlButton(Activity activity, final ViewFacade viewFacade, int i) {

        final Button button = new Button(activity);
        button.setWidth(50);
        button.setMaxWidth(50);
        button.setMinWidth(50);
        button.setHeight(50);
        button.setMaxHeight(50);
        button.setMinHeight(50);

        return button;
    }

    private static Button createRunButton(Activity activity, final ViewFacade viewFacade, int i) {
        final String string = activity.getResources().getString(R_original.string.mm_run_off);
        final String string2 = activity.getResources().getString(R_original.string.mm_run_on);
        final Button button = new Button(activity);
        button.setWidth(50);
        button.setMaxWidth(50);
        button.setMinWidth(50);
        button.setHeight(50);
        button.setMaxHeight(50);
        button.setMinHeight(50);
        button.setText(string);

        viewFacade.addKeyboardModifiersChangeListener(new KeyboardModifiersListener() { // from class: com.eltechs.axs.gamesControls.FalloutInterfaceOverlay.4
            @Override // com.eltechs.axs.xserver.KeyboardModifiersListener
            public void modifiersChanged(Mask<KeyButNames> mask) {
                if (mask.isSet(KeyButNames.SHIFT)) {
                    button.setText(string2);
                } else {
                    button.setText(string);
                }
            }
        });
        return button;
    }

    private View createLeftToolbar(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        LinearLayout linearLayout = new LinearLayout(xServerDisplayActivity);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-2, -1, 0.0f));
        linearLayout.setOrientation(1);
        int i = (int) (0.4f * AndroidHelpers.getDisplayMetrics().densityDpi);
        ViewFacade xServerFacade = viewOfXServer.getXServerFacade();
        linearLayout.addView(createRunButton(xServerDisplayActivity, xServerFacade, i));
        linearLayout.addView(createCtrlButton(xServerDisplayActivity, xServerFacade, i));
        linearLayout.addView(createAltButton(xServerDisplayActivity, xServerFacade, i));
        linearLayout.addView(createLeftScrollViewWithButtons(xServerDisplayActivity, xServerFacade, i));
        if (!this.isToolbarsVisible) {
            linearLayout.setVisibility(8);
        }
        this.leftToolbar = linearLayout;
        return linearLayout;
    }

    private static ScrollView createLeftScrollViewWithButtons(Activity activity, ViewFacade viewFacade, int i) {
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        linearLayout.setOrientation(1);
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_UP, i, "↑"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_DOWN, i, "↓"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_LEFT, i, "←"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_A, i, "A"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_B, i, "B"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_C, i, "C"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_D, i, "D"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_E, i, "E"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F, i, "F"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_G, i, "G"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_H, i, "H"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_I, i, "I"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_J, i, "J"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_K, i, "K"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_L, i, "L"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_M, i, "M"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_N, i, "N"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_O, i, "O"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_P, i, "P"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_Q, i, "Q"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_R, i, "R"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_S, i, "S"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_T, i, "T"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_U, i, "U"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_V, i, "V"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_W, i, "W"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_X, i, "X"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_Y, i, "Y"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_Z, i, "Z"));
        scrollView.addView(linearLayout);
        return scrollView;
    }

    private View createRightToolbar(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        LinearLayout linearLayout = new LinearLayout(xServerDisplayActivity);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-2, -1, 0.0f));
        linearLayout.setOrientation(1);
        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
        int i = (int) ((isDisplaySmall(displayMetrics) ? buttonSzSmallDisplayInches : 0.4f) * displayMetrics.densityDpi);
        linearLayout.addView(createSyncButton(xServerDisplayActivity, viewOfXServer, (isDisplaySmall(displayMetrics) ? 30 : 0) + i));
        linearLayout.addView(createRightScrollViewWithButtons(xServerDisplayActivity, viewOfXServer.getXServerFacade(), i));
        if (!this.isToolbarsVisible) {
            linearLayout.setVisibility(8);
        }
        this.rightToolbar = linearLayout;
        return linearLayout;
    }

    private static ScrollView createRightScrollViewWithButtons(Activity activity, ViewFacade viewFacade, int i) {
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        linearLayout.setOrientation(1);
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_ESC, i, "Esc"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_RETURN, i, "Ren"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_SPACE, i, "Spa"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_TAB, i, "Tab"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_BACKSPACE, i, "Bap"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_RIGHT, i, "→"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_1, i, "1"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_2, i, "2"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_3, i, "3"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_4, i, "4"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_5, i, "5"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_6, i, "6"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_7, i, "7"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_8, i, "8"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_9, i, "9"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_0, i, "0"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F1, i, "F1"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F2, i, "F2"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F3, i, "F3"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F4, i, "F4"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F5, i, "F5"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F6, i, "F6"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F7, i, "F7"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F8, i, "F8"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F9, i, "F9"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F10, i, "F10"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F11, i, "F11"));
        linearLayout.addView(createLetterButton(activity, viewFacade, KeyCodesX.KEY_F12, i, "F12"));
        scrollView.addView(linearLayout);
        return scrollView;
    }

    private static void setHandlerToButton(Button button, final KeyCodesX keyCodesX, final ViewFacade viewFacade) {

    }

    private static Button createLetterButton(Activity activity, ViewFacade viewFacade, KeyCodesX keyCodesX, int i, String str) {
        Button button = new Button(activity);
        button.setWidth(50);
        button.setMinWidth(50);
        button.setMaxWidth(50);
        button.setHeight(50);
        button.setMinHeight(50);
        button.setMaxHeight(50);
        button.setText(str);
        setHandlerToButton(button, keyCodesX, viewFacade);
        return button;
    }
}
