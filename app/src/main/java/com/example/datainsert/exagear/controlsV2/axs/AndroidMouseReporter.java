package com.example.datainsert.exagear.controlsV2.axs;

import static android.view.MotionEvent.ACTION_BUTTON_PRESS;
import static android.view.MotionEvent.ACTION_BUTTON_RELEASE;
import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_HOVER_ENTER;
import static android.view.MotionEvent.ACTION_HOVER_EXIT;
import static android.view.MotionEvent.ACTION_HOVER_MOVE;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_OUTSIDE;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_SCROLL;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.MotionEvent.BUTTON_PRIMARY;
import static android.view.MotionEvent.BUTTON_SECONDARY;
import static android.view.MotionEvent.BUTTON_TERTIARY;

import android.util.Log;
import android.view.MotionEvent;

import com.example.datainsert.exagear.controlsV2.axs.AndroidPointReporter;
import com.example.datainsert.exagear.controlsV2.axs.XKeyButton;

public class AndroidMouseReporter {
    int mouseButton = 0;

    public AndroidMouseReporter() {

    }

    public boolean handleMouseEvent(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        switch (motionEvent.getActionMasked()) {
            case ACTION_DOWN:
            case ACTION_POINTER_DOWN:
                AndroidPointReporter.pointerMove(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                pressMouseButton(motionEvent);
                return true;
            case ACTION_UP:
            case ACTION_CANCEL:
            case ACTION_POINTER_UP:
                AndroidPointReporter.pointerMove(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                releaseMouseButton();
                return true;
            case ACTION_MOVE:
            case ACTION_HOVER_MOVE:
            case ACTION_HOVER_ENTER:
            case ACTION_HOVER_EXIT:
                AndroidPointReporter.pointerMove(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                return true;
            case ACTION_BUTTON_PRESS:
            case ACTION_BUTTON_RELEASE:
                Log.d("Mouse", "这俩没处理，没问题吗");
                return true;
            case ACTION_OUTSIDE:
            case ACTION_SCROLL:
            default:
                return true;
        }
    }


    private int getMouseButton(MotionEvent motionEvent) {
        int buttonState = motionEvent.getButtonState();
        if (buttonState != BUTTON_TERTIARY) {
            switch (buttonState) {
                case BUTTON_PRIMARY:
                    return XKeyButton.POINTER_LEFT;
                case BUTTON_SECONDARY:
                    return XKeyButton.POINTER_RIGHT;
                default:
                    return 0;
            }
        }
        return 2;
    }

    private void pressMouseButton(MotionEvent motionEvent) {
        releaseMouseButton();
        this.mouseButton = getMouseButton(motionEvent);
        if (this.mouseButton != 0) {
            AndroidPointReporter.buttonPressed(this.mouseButton);
        }
    }

    private void releaseMouseButton() {
        if (this.mouseButton != 0) {
            AndroidPointReporter.buttonReleased(this.mouseButton);
            this.mouseButton = 0;
        }
    }


}
