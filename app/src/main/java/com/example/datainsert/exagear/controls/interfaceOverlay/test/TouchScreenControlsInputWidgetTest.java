package com.example.datainsert.exagear.controls.interfaceOverlay.test;

import android.content.Context;
import android.support.v4.view.InputDeviceCompat;
import android.view.MotionEvent;
import android.view.View;

import com.eltechs.axs.Finger;
import com.eltechs.axs.TouchScreenControls;

public class TouchScreenControlsInputWidgetTest extends View {

    private static final int MAX_FINGERS = 10;
    private final Finger[] userFingers;
    private TouchScreenControls touchScreenControls;

    public TouchScreenControlsInputWidgetTest(Context context) {
        super(context);
        this.userFingers = new Finger[10];
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void setTouchScreenControls(TouchScreenControls touchScreenControls) {
        this.touchScreenControls = touchScreenControls;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = (motionEvent.getSource() & InputDeviceCompat.SOURCE_TOUCHSCREEN) == 4098;
        boolean z2 = (motionEvent.getSource() & InputDeviceCompat.SOURCE_STYLUS) == 16386;
        boolean z3 = (motionEvent.getSource() & 8194) == 8194;
        return handleTouchEvent(motionEvent);
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
}
