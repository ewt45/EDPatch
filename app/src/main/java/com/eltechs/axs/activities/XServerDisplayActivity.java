package com.eltechs.axs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.eltechs.axs.AppConfig;
import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.PurchasableComponentsCollectionAware;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.axs.container.annotations.Autowired;
import com.eltechs.axs.environmentService.AXSEnvironment;
import com.eltechs.axs.environmentService.components.XServerComponent;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.payments.PurchasableComponentsCollection;
import com.eltechs.axs.widgets.actions.Action;
import com.eltechs.axs.widgets.popupMenu.AXSPopupMenu;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.ed.R;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class XServerDisplayActivity<StateClass extends ApplicationStateBase<StateClass> & PurchasableComponentsCollectionAware & XServerDisplayActivityConfigurationAware & SelectedExecutableFileAware<StateClass>> extends FrameworkActivity<StateClass> {
    private static final long COUNT_DOWN_INTERVAL = 20000;
    private static final long COUNT_DOWN_TOTAL = 86400000;
    private static final boolean ENABLE_TRACING_METHODS = false;
    private static final int REQUEST_CODE_INFORMER = 10003;
    private Runnable contextMenuRequestHandler;
    private XServerDisplayActivityInterfaceOverlay interfaceOverlay = new TrivialInterfaceOverlay();
    private CountDownTimer periodicIabCheckTimer = new CountDownTimer(86400000, COUNT_DOWN_INTERVAL) { // from class: com.eltechs.axs.activities.XServerDisplayActivity.1
        @Override // android.os.CountDownTimer
        public void onTick(long j) {
            XServerDisplayActivity.this.checkUiThread();
            if (XServerDisplayActivity.this.isActivityResumed()) {
                XServerDisplayActivity.this.checkIab();
            }
        }

        @Override // android.os.CountDownTimer
        public void onFinish() {
            XServerDisplayActivity.this.periodicIabCheckTimer.start();
        }
    };
    private View uiOverlayView;
    private ViewOfXServer viewOfXServer;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:11:0x005c  */
    /* JADX WARN: Type inference failed for: r8v1, types: [com.eltechs.axs.applicationState.ApplicationStateBase] */
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected void onCreate(Bundle bundle) {
        ViewFacade viewFacade = null;
        super.onCreate(bundle);
        XServerComponent xServerComponent = getApplicationState().getEnvironment().getComponent(XServerComponent.class);
        Class<?> cls = (Class<?>) getIntent().getSerializableExtra("facadeclass");
        if (cls != null) {
            try {
                viewFacade = (ViewFacade) cls.getDeclaredConstructor(XServer.class, ApplicationStateBase.class).newInstance(xServerComponent.getXServer(), getApplicationState());
            } catch (Exception e) {
                e.printStackTrace();
                Assert.state(false);
            }
        }
        //这个jadx反编译完串位置了
        getWindow().addFlags(128);
        setContentView(R.layout.main);
        if (!checkForSuddenDeath()) {
            this.viewOfXServer = new ViewOfXServer(this, xServerComponent.getXServer(), viewFacade, getApplicationState().getXServerViewConfiguration());
            this.periodicIabCheckTimer.start();
        }
        hideDecor();

    }

    @Autowired
    private void setXServerDisplayActivityInterfaceOverlay(XServerDisplayActivityInterfaceOverlay xServerDisplayActivityInterfaceOverlay) {
        this.interfaceOverlay = xServerDisplayActivityInterfaceOverlay;
    }

    public void hideDecor() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(7942);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() { // from class: com.eltechs.axs.activities.XServerDisplayActivity.1MyOnSystemUiVisibilityChangeListener
            @Override // android.view.View.OnSystemUiVisibilityChangeListener
            public void onSystemUiVisibilityChange(int i) {
                if (i == 0) {
                    decorView.setSystemUiVisibility(260);
                } else {
                    decorView.setSystemUiVisibility(5894);
                }
            }
        });
    }

    /* JADX WARN: Type inference failed for: r0v4, types: [com.eltechs.axs.applicationState.ApplicationStateBase] */
    @Override // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (checkForSuddenDeath()) {
            return;
        }
        this.contextMenuRequestHandler = NoMenuPopup.INSTANCE;
        buildUI();
        this.viewOfXServer.onResume();
        this.uiOverlayView.requestFocus();
        AXSEnvironment environment = getApplicationState().getEnvironment();
        if (environment != null) {
            environment.resumeEnvironment();
        }
        checkIab();
        if (getApplicationContext().getPackageName().equals("com.eltechs.ed")) {
            AppConfig appConfig = AppConfig.getInstance(this);
            DialogFragment controlsInfoDialog = ((SelectedExecutableFileAware) getApplicationState()).getSelectedExecutableFile().getControlsInfoDialog();
            String controlsId = ((SelectedExecutableFileAware) getApplicationState()).getSelectedExecutableFile().getControlsId();
            Set<String> controlsWithInfoShown = appConfig.getControlsWithInfoShown();
            if (controlsWithInfoShown.contains(controlsId)) {
                return;
            }
            controlsInfoDialog.show(getSupportFragmentManager(), "CONTROLS_INFO");
            controlsWithInfoShown.add(controlsId);
            appConfig.setControlsWithInfoShown(controlsWithInfoShown);
        }
    }

    private void buildUI() {
        setContentView((int) R.layout.main);
        if (this.viewOfXServer.getParent() != null) {
            ((ViewGroup) this.viewOfXServer.getParent()).removeView(this.viewOfXServer);
        }
        getRootLayout().addView(this.viewOfXServer);
        this.uiOverlayView = this.interfaceOverlay.attach(this, this.viewOfXServer);
        getRootLayout().addView(this.uiOverlayView);
    }

    private boolean checkForSuddenDeath() {
        if (Globals.getApplicationState() != null) {
            return false;
        }
        FatalErrorActivity.showFatalError(getResources().getString(R.string.xda_guest_suddenly_died));
        finish();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkIab() {
        if (getApplicationContext().getPackageName().equals("com.eltechs.ed")) {
            return;
        }
        PurchasableComponentsCollection purchasableComponentsCollection = ((PurchasableComponentsCollectionAware) getApplicationState()).getPurchasableComponentsCollection();
        if (purchasableComponentsCollection.isTrialPeriodActive() || purchasableComponentsCollection.isPrepaidPeriodActive()) {
            return;
        }
        startActivityForResult(createIntent(this, CustomizationPackageInformerActivity.class, CustomizationPackageInformerActivity.AVAILABLE_PLAY_EXPIRED), (int) REQUEST_CODE_INFORMER);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.eltechs.axs.applicationState.ApplicationStateBase] */
    @Override // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        AXSEnvironment environment = getApplicationState().getEnvironment();
        if (environment != null) {
            environment.freezeEnvironment();
        }
        this.viewOfXServer.onPause();
        this.interfaceOverlay.detach();
        this.uiOverlayView = null;
        getRootLayout().removeAllViews();
    }

    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.viewOfXServer = null;
        setContentView(new TextView(this));
        this.periodicIabCheckTimer.cancel();
        this.periodicIabCheckTimer = null;
    }

    @Override // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i != REQUEST_CODE_INFORMER) {
            super.onActivityResult(i, i2, intent);
        } else if (i2 == 0) {
            StartupActivity.shutdownAXSApplication();
            finish();
        }
    }

    public void addDefaultPopupMenu(List<? extends Action> list) {
        TextView textView = new TextView(this);
        textView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        getRootLayout().addView(textView, new FrameLayout.LayoutParams(0, 0, 5));
        final AXSPopupMenu aXSPopupMenu = new AXSPopupMenu(this, textView);
        aXSPopupMenu.add(list);
        this.contextMenuRequestHandler = new Runnable() { // from class: com.eltechs.axs.activities.XServerDisplayActivity.2
            @Override // java.lang.Runnable
            public void run() {
                aXSPopupMenu.show();
            }
        };
    }

    public void placeViewOfXServer(int i, int i2, int i3, int i4) {
        if (this.viewOfXServer != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.viewOfXServer.getLayoutParams();
            if (layoutParams.leftMargin == i && layoutParams.topMargin == i2 && layoutParams.width == i3 && layoutParams.height == i4) {
                return;
            }
            layoutParams.leftMargin = i;
            layoutParams.topMargin = i2;
            layoutParams.width = i3;
            layoutParams.height = i4;
            this.viewOfXServer.setLayoutParams(layoutParams);
            this.viewOfXServer.invalidate();
        }
    }

    public boolean isHorizontalStretchEnabled() {
        return this.viewOfXServer.isHorizontalStretchEnabled();
    }

    public void setHorizontalStretchEnabled(boolean z) {
        this.viewOfXServer.setHorizontalStretchEnabled(z);
    }

    public void showPopupMenu() {
        this.contextMenuRequestHandler.run();
    }

    private FrameLayout getRootLayout() {
        return (FrameLayout) findViewById(R.id.mainView);
    }

    /* loaded from: classes.dex */
    private static class NoMenuPopup implements Runnable {
        public static final Runnable INSTANCE = new NoMenuPopup();

        @Override // java.lang.Runnable
        public void run() {
        }

        private NoMenuPopup() {
        }
    }

    /* loaded from: classes.dex */
    private static class TrivialInterfaceOverlay implements XServerDisplayActivityInterfaceOverlay {
        @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
        public void detach() {
        }

        private TrivialInterfaceOverlay() {
        }

        @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
        public View attach(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
            View view = new View(xServerDisplayActivity);
            view.setBackgroundColor(xServerDisplayActivity.getResources().getColor(android.R.color.transparent));
            return view;
        }
    }

    public void startInformerActivity(Intent intent) {
        startActivityForResult(intent, (int) REQUEST_CODE_INFORMER);
    }

    public void freezeXServerScene() {
        if (this.viewOfXServer != null) {
            this.viewOfXServer.freezeRenderer();
        }
    }

    public void unfreezeXServerScene() {
        if (this.viewOfXServer != null) {
            this.viewOfXServer.unfreezeRenderer();
        }
    }
}