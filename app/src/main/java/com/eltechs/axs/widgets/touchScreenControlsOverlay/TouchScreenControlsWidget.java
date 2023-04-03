package com.eltechs.axs.widgets.touchScreenControlsOverlay;

import android.util.Log;
import android.widget.FrameLayout;
import com.eltechs.axs.TouchScreenControls;
import com.eltechs.axs.TouchScreenControlsFactory;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.configuration.TouchScreenControlsInputConfiguration;
import com.eltechs.axs.helpers.UiThread;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;

/* loaded from: classes.dex */
public class TouchScreenControlsWidget extends FrameLayout {
    private final static String TAG=  "TSCWidget";
    private final TouchScreenControlsFactory controlsFactory;
    private final TouchScreenControlsDisplayWidget displayWidget;
    private final XServerDisplayActivity host;
    private final TouchScreenControlsInputWidget inputWidget;
    private final ViewOfXServer target;

    public TouchScreenControlsWidget(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer, TouchScreenControlsFactory touchScreenControlsFactory, TouchScreenControlsInputConfiguration tscInputConfig) {
        super(xServerDisplayActivity);
        this.host = xServerDisplayActivity;
        this.target = viewOfXServer;
        this.controlsFactory = touchScreenControlsFactory;
        if (!touchScreenControlsFactory.hasVisibleControls()) {
            this.displayWidget = null;
        } else {
            this.displayWidget = new TouchScreenControlsDisplayWidget(xServerDisplayActivity);
            addView(this.displayWidget);
        }
//        if(getContext().getPackageName().equals("com.ewt45.exagearsupportv7")){
//            tscInputConfig = new TouchScreenControlsInputConfiguration(TouchScreenControlsInputConfiguration.BackKeyAction.SHOW_POPUP_MENU);
//        }
        this.inputWidget = new TouchScreenControlsInputWidget(xServerDisplayActivity, viewOfXServer, tscInputConfig);
        addView(this.inputWidget);

    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, final int i, final int i2, final int i3, final int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (i == i3 || i2 == i4) {
            return;
        }
        // from class: com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsWidget.1
// java.lang.Runnable
        UiThread.post(() -> TouchScreenControlsWidget.this.host.placeViewOfXServer(i, i2, i3 - i, i4 - i2));
        Log.d(TAG, "onLayout: 设置inputWidget的TouchScreenControls（toucharea）");
        TouchScreenControls create = this.controlsFactory.create(this, this.target);
        this.inputWidget.setTouchScreenControls(create);
        if (this.displayWidget != null) {
            this.displayWidget.setTouchScreenControls(create);
        }
    }

    public void setZOrderMediaOverlay(boolean z) {
        if (this.displayWidget != null) {
            this.displayWidget.setZOrderMediaOverlay(z);
        }
    }

    public void detach() {
        if (this.displayWidget != null) {
            this.displayWidget.onPause();
        }
    }
}