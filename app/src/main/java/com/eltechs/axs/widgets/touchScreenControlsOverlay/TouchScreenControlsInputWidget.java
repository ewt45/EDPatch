package com.eltechs.axs.widgets.touchScreenControlsOverlay;

import static com.eltechs.axs.configuration.TouchScreenControlsInputConfiguration.BackKeyAction.SHOW_POPUP_MENU;

import android.support.v4.view.InputDeviceCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.eltechs.axs.Finger;
import com.eltechs.axs.Globals;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.KeyEventReporter;
import com.eltechs.axs.Keyboard;
import com.eltechs.axs.Mouse;
import com.eltechs.axs.PointerEventReporter;
import com.eltechs.axs.TouchScreenControls;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.axs.configuration.TouchScreenControlsInputConfiguration;
import com.eltechs.axs.widgets.popupMenu.AXSPopupMenu;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;
import com.example.datainsert.exagear.controls.interfaceOverlay.FalloutInterfaceOverlay2;

/* loaded from: classes.dex */
public class TouchScreenControlsInputWidget extends View {
    String TAG =  "tscInputWidget";
    private final int MAX_FINGERS;
    private final TouchScreenControlsInputConfiguration configuration;
    private final Keyboard keyboard;
    private final Mouse mouse;
    private TouchScreenControls touchScreenControls;
    private final Finger[] userFingers;
    private final ViewFacade xServerFacade;

    public TouchScreenControlsInputWidget(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer, TouchScreenControlsInputConfiguration touchScreenControlsInputConfiguration) {
        super(xServerDisplayActivity);
        this.MAX_FINGERS = 10;
        this.userFingers = new Finger[10];
        this.xServerFacade = viewOfXServer==null?null:viewOfXServer.getXServerFacade();
        this.mouse = viewOfXServer==null?null:new Mouse(new PointerEventReporter(viewOfXServer));
        this.keyboard = new Keyboard(new KeyEventReporter(this.xServerFacade));

        if(xServerDisplayActivity.getPackageName().equals("com.ewt45.exagearsupportv7"))
            configuration = new TouchScreenControlsInputConfiguration(SHOW_POPUP_MENU);
        else this.configuration = touchScreenControlsInputConfiguration;
        setFocusable(true);
        setFocusableInTouchMode(true);
        installKeyListener();
    }

    public void setTouchScreenControls(TouchScreenControls touchScreenControls) {
        this.touchScreenControls = touchScreenControls;
    }

    private void installKeyListener() {
        setOnKeyListener(new View.OnKeyListener() { // from class: com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsInputWidget.1
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getSource() == 8194) {
                    if (i == 4) {
                        if (keyEvent.getAction() == 0) {
                            TouchScreenControlsInputWidget.this.xServerFacade.injectPointerButtonPress(3);
                        } else if (keyEvent.getAction() == 1) {
                            TouchScreenControlsInputWidget.this.xServerFacade.injectPointerButtonRelease(3);
                        }
                    }
                    return true;
                } else if (i != 82 || keyEvent.getAction() != 1) {
                    if (i == 4 && TouchScreenControlsInputWidget.this.configuration.backKeyAction == SHOW_POPUP_MENU) {
                        if (keyEvent.getAction() == 1) {
                            //仅供调试
                            if( Globals.getAppContext().getPackageName().equals("com.ewt45.exagearsupportv7")){
                                XServerDisplayActivityInterfaceOverlay ui = ((XServerDisplayActivityConfigurationAware) Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay();
                                ((FalloutInterfaceOverlay2)ui).getControlsFactory().getPopupMenu().getMenu().clear();
                                ((FalloutInterfaceOverlay2)ui).getControlsFactory().getPopupMenu().show();
                            }
//                          getHost().showPopupMenu();
                        }
                        return true;
                    } else if (i == 23) {
                        if (keyEvent.getAction() == 0) {
                            TouchScreenControlsInputWidget.this.xServerFacade.injectPointerButtonPress(3);
                        } else if (keyEvent.getAction() == 1) {
                            TouchScreenControlsInputWidget.this.xServerFacade.injectPointerButtonRelease(3);
                        }
                        return true;
                    } else if (i == 102 || i == 104) {
                        if (keyEvent.getAction() == 0) {
                            TouchScreenControlsInputWidget.this.xServerFacade.injectKeyPress((byte) KeyCodesX.KEY_SHIFT_LEFT.getValue());
                        } else if (keyEvent.getAction() == 1) {
                            TouchScreenControlsInputWidget.this.xServerFacade.injectKeyRelease((byte) KeyCodesX.KEY_SHIFT_LEFT.getValue());
                        }
                        return true;
                    } else if (i == 103 || i == 105) {
                        if (keyEvent.getAction() == 0) {
                            TouchScreenControlsInputWidget.this.xServerFacade.injectKeyPress((byte) KeyCodesX.KEY_CONTROL_LEFT.getValue());
                        } else if (keyEvent.getAction() == 1) {
                            TouchScreenControlsInputWidget.this.xServerFacade.injectKeyRelease((byte) KeyCodesX.KEY_CONTROL_LEFT.getValue());
                        }
                        return true;
                    } else if (i != 0) {
                        if (keyEvent.getAction() == 0) {
                            return TouchScreenControlsInputWidget.this.keyboard.handleKeyDown(i, keyEvent);
                        }
                        if (keyEvent.getAction() == 1) {
                            return TouchScreenControlsInputWidget.this.keyboard.handleKeyUp(i, keyEvent);
                        }
                        return false;
                    } else if (keyEvent.getAction() == 2) {
                        return TouchScreenControlsInputWidget.this.keyboard.handleUnicodeKeyType(keyEvent);
                    } else {
                        return false;
                    }
                } else {
                    TouchScreenControlsInputWidget.this.getHost().showPopupMenu();
                    return true;
                }
            }
        });
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        int source = motionEvent.getSource() & InputDeviceCompat.SOURCE_TOUCHSCREEN;
        boolean z = (motionEvent.getSource() & InputDeviceCompat.SOURCE_STYLUS) == 16386;
        boolean z2 = (motionEvent.getSource() & 8194) == 8194;
        if (z || z2) {
            return this.mouse.handleMouseEvent(motionEvent);
        }
        return super.onGenericMotionEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {

        boolean z = (motionEvent.getSource() & InputDeviceCompat.SOURCE_TOUCHSCREEN) == 4098;
        boolean z2 = (motionEvent.getSource() & InputDeviceCompat.SOURCE_STYLUS) == 16386;
        boolean z3 = (motionEvent.getSource() & 8194) == 8194;
        if (z || z2) {
            return handleTouchEvent(motionEvent);
        }
        if (z3) {
            return this.mouse.handleMouseEvent(motionEvent);
        }
        return super.onTouchEvent(motionEvent);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private boolean handleTouchEvent(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        int pointerId = motionEvent.getPointerId(actionIndex);
        int actionMasked = motionEvent.getActionMasked();
        if (pointerId >= 10) {
            return true;
        }
        int i = 0;
        switch (actionMasked) {
            case 0:
            case 5:
                this.userFingers[pointerId] = new Finger(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                this.touchScreenControls.handleFingerDown(this.userFingers[pointerId]);
                break;
            case 1:
            case 6:
                if (this.userFingers[pointerId] != null) {
                    this.userFingers[pointerId].release(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                    this.touchScreenControls.handleFingerUp(this.userFingers[pointerId]);
                    this.userFingers[pointerId] = null;
                    break;
                }
                break;
            case 2:
                while (i < 10) {
                    if (this.userFingers[i] != null) {
                        int findPointerIndex = motionEvent.findPointerIndex(i);
                        if (findPointerIndex >= 0) {
                            this.userFingers[i].update(motionEvent.getX(findPointerIndex), motionEvent.getY(findPointerIndex));
                            this.touchScreenControls.handleFingerMove(this.userFingers[i]);
                        } else {
                            this.touchScreenControls.handleFingerUp(this.userFingers[i]);
                            this.userFingers[i] = null;
                        }
                    }
                    i++;
                }
                break;
            case 3:
                while (i < 10) {
                    if (this.userFingers[i] != null) {
                        this.touchScreenControls.handleFingerUp(this.userFingers[i]);
                        this.userFingers[i] = null;
                    }
                    i++;
                }
                break;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public XServerDisplayActivity getHost() {
        return (XServerDisplayActivity) getContext();
    }
}