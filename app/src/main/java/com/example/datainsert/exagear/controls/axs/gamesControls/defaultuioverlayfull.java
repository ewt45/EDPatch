package com.example.datainsert.exagear.controls.axs.gamesControls;
//
//import static android.view.View.GONE;
//import static android.view.View.VISIBLE;
//
//import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SHOW_CURSOR;
//import static com.example.datainsert.exagear.controls.ControlsResolver.SHARED_PREFERENCE_SETTING;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.support.constraint.ConstraintLayout;
//import android.support.v7.app.AlertDialog;
//import android.util.DisplayMetrics;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//
//import com.eltechs.axs.Globals;
//import com.eltechs.axs.KeyCodesX;
//import com.eltechs.axs.activities.StartupActivity;
//import com.eltechs.axs.activities.XServerDisplayActivity;
//import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
//import com.eltechs.axs.configuration.TouchScreenControlsInputConfiguration;
//import com.eltechs.axs.helpers.AndroidHelpers;
//import com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsWidget;
//import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
//import com.eltechs.axs.xserver.ViewFacade;
//import com.eltechs.ed.R;
//import com.eltechs.ed.controls.Controls;
//import com.eltechs.ed.controls.touchControls.AbstractTCF;
//
///* loaded from: classes.dex */
//public class DefaultUIOverlay2 implements XServerDisplayActivityInterfaceOverlay {
//    private static final float displaySizeThresholdHeightInches = 3.0f;
//    private static final float displaySizeThresholdWidthInches = 5.0f;
//    private static final float sideToolbarWidthNormalDisplayInches = 0.4f;
//    private static final float sideToolbarWidthSmallDisplayInches = 0.35f;
//    private static final float toolbarHeightNormalDisplayInches = 0.27f;
//    private static final float toolbarHeightSmallDisplayInches = 0.23f;
//    private final Controls mControls;
//    private final AbstractTCF mControlsFactory;
//    protected XServerDisplayActivity mHostActivity;
//    protected int mLeftTbWidth;
//    protected LinearLayout mLeftToolbar;
//    protected int mRightTbWidth;
//    protected LinearLayout mRightToolbar;
//    protected View mToolbar;
//    private TouchScreenControlsWidget mTscWidget;
//    protected ViewOfXServer mViewOfXServer;
//    protected ViewFacade mXServerFacade;
//
//    /* JADX INFO: Access modifiers changed from: package-private */
//    /* loaded from: classes.dex */
//    public enum KeyButtonHandlerType {
//        CLICK,
//        PRESS_RELEASE,
//        CUSTOM
//    }
//
//    /* JADX INFO: Access modifiers changed from: protected */
//    /* loaded from: classes.dex */
//    public class ScrollArea {
//        LinearLayout mLinearLayout;
//        ScrollView mScrollView;
//
//        ScrollArea(ScrollView scrollView, LinearLayout linearLayout) {
//            this.mScrollView = scrollView;
//            this.mLinearLayout = linearLayout;
//        }
//    }
//
//    public DefaultUIOverlay2(Controls controls, AbstractTCF abstractTCF) {
//        this.mControls = controls;
//        this.mControlsFactory = abstractTCF;
//        this.mControlsFactory.setUIOverlay(this);
//    }
//
//    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
//    public View attach(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
//        this.mHostActivity = xServerDisplayActivity;
//        this.mViewOfXServer = viewOfXServer;
//        this.mXServerFacade = viewOfXServer.getXServerFacade();
//        this.mTscWidget = new TouchScreenControlsWidget(xServerDisplayActivity, viewOfXServer, this.mControlsFactory, TouchScreenControlsInputConfiguration.DEFAULT);
//        this.mTscWidget.setZOrderMediaOverlay(true);
//        FrameLayout frameLayout = new FrameLayout(xServerDisplayActivity);
//        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
//        LinearLayout linearLayout = new LinearLayout(xServerDisplayActivity);
//        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
//        linearLayout.addView(createLeftToolbar());
//        linearLayout.addView(this.mTscWidget, new LinearLayout.LayoutParams(0, -1, 1.0f));
//        linearLayout.addView(createRightToolbar());
//        frameLayout.addView(linearLayout);
//        frameLayout.addView(createToolbar());
//        viewOfXServer.setHorizontalStretchEnabled(false);
//
//        //设置鼠标显隐
//        viewOfXServer.getConfiguration().setShowCursor(xServerDisplayActivity.getSharedPreferences(SHARED_PREFERENCE_SETTING, Context.MODE_PRIVATE).getBoolean(PREF_KEY_SHOW_CURSOR,false));
//        return frameLayout;
//    }
//
//    private View createToolbar() {
//        ConstraintLayout constraintLayout = (ConstraintLayout) this.mHostActivity.getLayoutInflater().inflate(R.layout.default_ui_overlay_toolbar, (ViewGroup) null);
//        final ImageButton imageButton = (ImageButton) constraintLayout.findViewById(R.id.button_fullscreen);
//        imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay.1
//            @Override // android.view.View.OnClickListener
//            public void onClick(View view) {
//                boolean z = !DefaultUIOverlay2.this.mViewOfXServer.isHorizontalStretchEnabled();
//                DefaultUIOverlay2.this.mViewOfXServer.setHorizontalStretchEnabled(z);
//                imageButton.setImageResource(z ? R.drawable.ic_fullscreen_exit_24dp : R.drawable.ic_fullscreen_24dp);
//            }
//        });
//        ((ImageButton) constraintLayout.findViewById(R.id.button_sidetoolbars)).setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay.2
//            @Override // android.view.View.OnClickListener
//            public void onClick(View view) {
//                DefaultUIOverlay2.this.toggleRightToolbar();
//                DefaultUIOverlay2.this.toggleLeftToolbar();
//            }
//        });
//        ((ImageButton) constraintLayout.findViewById(R.id.button_kbd)).setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay.3
//            @Override // android.view.View.OnClickListener
//            public void onClick(View view) {
//                AndroidHelpers.toggleSoftInput();
//            }
//        });
//        ((ImageButton) constraintLayout.findViewById(R.id.button_help)).setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay.4
//            @Override // android.view.View.OnClickListener
//            public void onClick(View view) {
//                DefaultUIOverlay2.this.mControls.createInfoDialog().show(DefaultUIOverlay2.this.mHostActivity.getSupportFragmentManager(), "CONTROLS_INFO");
//            }
//        });
//        ((ImageButton) constraintLayout.findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay.5
//            @Override // android.view.View.OnClickListener
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(DefaultUIOverlay2.this.mHostActivity);
//                builder.setTitle("Confirm Exit");
//                builder.setIcon(R.drawable.ic_warning_24dp);
//                builder.setMessage("Are you sure you want to exit?");
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay.5.1
//                    @Override // android.content.DialogInterface.OnClickListener
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        StartupActivity.shutdownAXSApplication(true);
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay.5.2
//                    @Override // android.content.DialogInterface.OnClickListener
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.show();
//            }
//        });
//        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
//        constraintLayout.setMaxHeight((int) ((isDisplaySmallHeight(displayMetrics) ? toolbarHeightSmallDisplayInches : toolbarHeightNormalDisplayInches) * displayMetrics.ydpi));
//        this.mToolbar = constraintLayout;
//        return constraintLayout;
//    }
//
//    private LinearLayout createSideToolbar() {
//        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
//        int i = (int) ((isDisplaySmallWidth(displayMetrics) ? sideToolbarWidthSmallDisplayInches : 0.4f) * displayMetrics.xdpi);
//        LinearLayout linearLayout = new LinearLayout(this.mHostActivity);
//        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(i, -1, 0.0f));
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        linearLayout.setBackgroundColor(-10066330);
//        linearLayout.setVisibility(GONE);
//        return linearLayout;
//    }
//
//    private View createLeftToolbar() {
//        this.mLeftToolbar = createSideToolbar();
//        this.mLeftTbWidth = this.mLeftToolbar.getLayoutParams().width;
//        return this.mLeftToolbar;
//    }
//
//    private View createRightToolbar() {
//        this.mRightToolbar = createSideToolbar();
//        this.mRightTbWidth = this.mRightToolbar.getLayoutParams().width;
//        return this.mRightToolbar;
//    }
//
//    /* JADX INFO: Access modifiers changed from: protected */
//    public ScrollArea createToolbarScrollArea() {
//        ScrollView scrollView = new ScrollView(this.mHostActivity);
//        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.0f));
//        LinearLayout linearLayout = new LinearLayout(this.mHostActivity);
//        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        scrollView.addView(linearLayout);
//        return new ScrollArea(scrollView, linearLayout);
//    }
//
//    /* JADX INFO: Access modifiers changed from: protected */
//    public void setButtonStyleByState(Button button, boolean z) {
//        if (z) {
//            button.setBackgroundResource(R.drawable.side_tb_button_pressed);
//            button.setTextColor(-2236963);
//            return;
//        }
//        button.setBackgroundResource(R.drawable.side_tb_button_normal);
//        button.setTextColor(-14540254);
//    }
//
//    /* JADX INFO: Access modifiers changed from: protected */
//    public Button createSideTbButton(int i, String str, KeyButtonHandlerType keyButtonHandlerType) {
//        return createSideTbButton(i, str, keyButtonHandlerType, KeyCodesX.KEY_NONE);
//    }
//
//    /* JADX INFO: Access modifiers changed from: protected */
//    public Button createSideTbButton(int i, String str, KeyButtonHandlerType keyButtonHandlerType, final KeyCodesX keyCodesX) {
//        Button button = new Button(this.mHostActivity);
//        button.setLayoutParams(new LinearLayout.LayoutParams(i, i));
//        button.setText(str);
//        setButtonStyleByState(button, false);
//        switch (keyButtonHandlerType) {
//            case CLICK:
//                button.setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay2.6
//                    @Override // android.view.View.OnClickListener
//                    public void onClick(View view) {
//                        DefaultUIOverlay2.this.mXServerFacade.injectKeyType((byte) keyCodesX.getValue());
//                    }
//                });
//                break;
//            case PRESS_RELEASE:
//                button.setOnTouchListener(new View.OnTouchListener() { // from class: com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay2.7
//                    @Override // android.view.View.OnTouchListener
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//                        int action = motionEvent.getAction();
//                        if (action != 3) {
//                            switch (action) {
//                                case 0:
//                                    DefaultUIOverlay2.this.setButtonStyleByState((Button) view, true);
//                                    DefaultUIOverlay2.this.mXServerFacade.injectKeyPress((byte) keyCodesX.getValue());
//                                    break;
//                            }
//                            return false;
//                        }
//                        DefaultUIOverlay2.this.setButtonStyleByState((Button) view, false);
//                        DefaultUIOverlay2.this.mXServerFacade.injectKeyRelease((byte) keyCodesX.getValue());
//                        return false;
//                    }
//                });
//                break;
//        }
//        return button;
//    }
//
//    /* JADX INFO: Access modifiers changed from: protected */
//    public void setToolbarSideToolbarsButtonVisibility(boolean z) {
//        ((ImageButton) this.mToolbar.findViewById(R.id.button_sidetoolbars)).setVisibility(z ? VISIBLE : GONE);
//    }
//
//    public void toggleToolbar() {
//        this.mToolbar.setVisibility(this.mToolbar.getVisibility() != VISIBLE ? VISIBLE : GONE);
//    }
//
//    public void toggleLeftToolbar() {
//        this.mLeftToolbar.setVisibility(this.mLeftToolbar.getVisibility() != VISIBLE ? VISIBLE : GONE);
//    }
//
//    public void toggleRightToolbar() {
//        this.mRightToolbar.setVisibility(this.mRightToolbar.getVisibility() != VISIBLE ? VISIBLE : GONE);
//    }
//
//    private static boolean isDisplaySmallHeight(DisplayMetrics displayMetrics) {
//        return ((float) displayMetrics.heightPixels) / displayMetrics.ydpi < displaySizeThresholdHeightInches;
//    }
//
//    private static boolean isDisplaySmallWidth(DisplayMetrics displayMetrics) {
//        return ((float) displayMetrics.widthPixels) / displayMetrics.xdpi < displaySizeThresholdWidthInches;
//    }
//
//    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
//    public void detach() {
//        this.mTscWidget.detach();
//        this.mTscWidget = null;
//        this.mToolbar = null;
//        this.mLeftToolbar = null;
//        this.mRightToolbar = null;
//    }
//}