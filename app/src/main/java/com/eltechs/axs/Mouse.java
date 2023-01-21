package com.eltechs.axs;

import android.view.MotionEvent;

public class Mouse {
    PointerEventReporter eventReporter;
    int mouseButton = 0;

    public Mouse(PointerEventReporter pointerEventReporter) {
        this.eventReporter = pointerEventReporter;
    }

    public boolean handleMouseEvent(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        switch (motionEvent.getActionMasked()) {
            case 0:
            case 5:
                this.eventReporter.pointerMove(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                pressMouseButton(motionEvent);
                return true;
            case 1:
            case 3:
            case 6:
                this.eventReporter.pointerMove(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                releaseMouseButton();
                return true;
            case 2:
            case 7:
                this.eventReporter.pointerMove(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                return true;
            case 4:
            case 8:
            default:
                return true;
            case 9:
                this.eventReporter.pointerMove(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                return true;
            case 10:
                this.eventReporter.pointerMove(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                return true;
        }
    }

    private int getMouseButton(MotionEvent motionEvent) {
        int buttonState = motionEvent.getButtonState();
        if (buttonState != 4) {
            switch (buttonState) {
                case 1:
                    return 1;
                case 2:
                    return 3;
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
            this.eventReporter.buttonPressed(this.mouseButton);
        }
    }

    private void releaseMouseButton() {
        if (this.mouseButton != 0) {
            this.eventReporter.buttonReleased(this.mouseButton);
            this.mouseButton = 0;
        }
    }


}
