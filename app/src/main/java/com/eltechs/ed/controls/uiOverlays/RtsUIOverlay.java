package com.eltechs.ed.controls.uiOverlays;

import android.view.View;
import android.widget.Button;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.helpers.UiThread;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.ed.controls.Controls;
import com.eltechs.ed.controls.touchControls.AbstractTCF;
import com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay;

/* loaded from: classes.dex */
public class RtsUIOverlay extends DefaultUIOverlay {
    private Button mCtrlButton;
    private boolean mIsCtrlPressed;

    public RtsUIOverlay(Controls controls, AbstractTCF abstractTCF) {
        super(controls, abstractTCF);
    }

    @Override // com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay, com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
    public View attach(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        View attach = super.attach(xServerDisplayActivity, viewOfXServer);
        this.mLeftToolbar.addView(createSideTbButton(this.mLeftTbWidth, "A", DefaultUIOverlay.KeyButtonHandlerType.PRESS_RELEASE, KeyCodesX.KEY_A));
        this.mLeftToolbar.addView(createSideTbButton(this.mLeftTbWidth, "S", DefaultUIOverlay.KeyButtonHandlerType.PRESS_RELEASE, KeyCodesX.KEY_S));
        this.mLeftToolbar.addView(createSideTbButton(this.mLeftTbWidth, "H", DefaultUIOverlay.KeyButtonHandlerType.PRESS_RELEASE, KeyCodesX.KEY_H));
        this.mLeftToolbar.addView(createSideTbButton(this.mLeftTbWidth, "P", DefaultUIOverlay.KeyButtonHandlerType.PRESS_RELEASE, KeyCodesX.KEY_P));
        this.mLeftToolbar.addView(createSideTbButton(this.mLeftTbWidth, "SHIFT", DefaultUIOverlay.KeyButtonHandlerType.PRESS_RELEASE, KeyCodesX.KEY_SHIFT_LEFT));
        this.mCtrlButton = createSideTbButton(this.mRightTbWidth, "CTRL", DefaultUIOverlay.KeyButtonHandlerType.CUSTOM);
        this.mCtrlButton.setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.controls.uiOverlays.RtsUIOverlay.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RtsUIOverlay.this.mIsCtrlPressed = !RtsUIOverlay.this.mIsCtrlPressed;
                RtsUIOverlay.this.setButtonStyleByState((Button) view, RtsUIOverlay.this.mIsCtrlPressed);
            }
        });
        this.mRightToolbar.addView(this.mCtrlButton);
        DefaultUIOverlay.ScrollArea createToolbarScrollArea = createToolbarScrollArea();
        createToolbarScrollArea.mLinearLayout.addView(createNumButton(KeyCodesX.KEY_1, "1"));
        createToolbarScrollArea.mLinearLayout.addView(createNumButton(KeyCodesX.KEY_2, "2"));
        createToolbarScrollArea.mLinearLayout.addView(createNumButton(KeyCodesX.KEY_3, "3"));
        createToolbarScrollArea.mLinearLayout.addView(createNumButton(KeyCodesX.KEY_4, "4"));
        createToolbarScrollArea.mLinearLayout.addView(createNumButton(KeyCodesX.KEY_5, "5"));
        createToolbarScrollArea.mLinearLayout.addView(createNumButton(KeyCodesX.KEY_6, "6"));
        createToolbarScrollArea.mLinearLayout.addView(createNumButton(KeyCodesX.KEY_7, "7"));
        createToolbarScrollArea.mLinearLayout.addView(createNumButton(KeyCodesX.KEY_8, "8"));
        createToolbarScrollArea.mLinearLayout.addView(createNumButton(KeyCodesX.KEY_9, "9"));
        createToolbarScrollArea.mLinearLayout.addView(createNumButton(KeyCodesX.KEY_0, "0"));
        this.mRightToolbar.addView(createToolbarScrollArea.mScrollView);
        this.mLeftToolbar.setVisibility(View.VISIBLE);
        this.mRightToolbar.setVisibility(View.VISIBLE);
        setToolbarSideToolbarsButtonVisibility(true);
        return attach;
    }

    private Button createNumButton(final KeyCodesX keyCodesX, String str) {
        Button createSideTbButton = createSideTbButton(this.mRightTbWidth, str, DefaultUIOverlay.KeyButtonHandlerType.CUSTOM);
        createSideTbButton.setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.controls.uiOverlays.RtsUIOverlay.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (RtsUIOverlay.this.mIsCtrlPressed) {
                    RtsUIOverlay.this.mXServerFacade.injectKeyPress((byte) KeyCodesX.KEY_CONTROL_LEFT.getValue());
                    RtsUIOverlay.this.mXServerFacade.injectKeyType((byte) keyCodesX.getValue());
                    RtsUIOverlay.this.mXServerFacade.injectKeyRelease((byte) KeyCodesX.KEY_CONTROL_LEFT.getValue());
                    RtsUIOverlay.this.mIsCtrlPressed = false;
                    RtsUIOverlay.this.setButtonStyleByState(RtsUIOverlay.this.mCtrlButton, RtsUIOverlay.this.mIsCtrlPressed);
                } else {
                    RtsUIOverlay.this.mXServerFacade.injectKeyType((byte) keyCodesX.getValue());
                }
                final Button button = (Button) view;
                RtsUIOverlay.this.setButtonStyleByState(button, true);
                UiThread.postDelayed(25L, new Runnable() { // from class: com.eltechs.ed.controls.uiOverlays.RtsUIOverlay.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        RtsUIOverlay.this.setButtonStyleByState(button, false);
                    }
                });
            }
        });
        return createSideTbButton;
    }
}