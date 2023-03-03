package com.example.datainsert.exagear.controls.interfaceOverlay.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import com.eltechs.axs.TouchScreenControls;
import com.eltechs.axs.TouchScreenControlsFactory;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.gamesControls.FalloutTouchScreenControlsFactory;
import com.eltechs.axs.helpers.UiThread;
import com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsDisplayWidget;
import com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsInputWidget;
import com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsWidget;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.example.datainsert.exagear.controls.interfaceOverlay.FalloutTouchScreenControlsFactory2;

public class TouchScreenControlsWidgetTest extends FrameLayout {
    private final FalloutTouchScreenControlsFactory controlsFactory;
    private final TouchScreenControlsDisplayWidget displayWidget;
    private final XServerDisplayActivity host;
    private final TouchScreenControlsInputWidgetTest inputWidget;
    private final ViewOfXServer target;

    public TouchScreenControlsWidgetTest(@NonNull Context context,XServerDisplayActivity host) {
        super(context);
        controlsFactory = new FalloutTouchScreenControlsFactory();
        displayWidget = null;
        target=null;
        this.host=host;

        this.inputWidget = new TouchScreenControlsInputWidgetTest(host);
        addView(this.inputWidget);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(left==right || top== bottom)
            return;
        UiThread.post(new Runnable() { // from class: com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsWidget.1
            @Override // java.lang.Runnable
            public void run() {
                host.placeViewOfXServer(left, right, right-left, bottom-top);
            }
        });
        TouchScreenControls create = this.controlsFactory.create(this,null);
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
