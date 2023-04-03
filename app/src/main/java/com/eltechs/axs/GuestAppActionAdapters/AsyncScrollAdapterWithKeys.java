package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.xserver.ViewFacade;

/* loaded from: classes.dex */
public class AsyncScrollAdapterWithKeys implements AsyncScrollAdapter {
    private final ViewFacade facade;
    private final KeyCodesX kcLeft;
    private final KeyCodesX kcRight;
    private boolean leftPressed;
    private boolean rightPressed;

    @Override // com.eltechs.axs.GuestAppActionAdapters.AsyncScrollAdapter
    public void notifyStart() {
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.AsyncScrollAdapter
    public void notifyStop() {
    }

    public AsyncScrollAdapterWithKeys(ViewFacade viewFacade, KeyCodesX keyCodesX, KeyCodesX keyCodesX2) {
        this.facade = viewFacade;
        this.kcLeft = keyCodesX;
        this.kcRight = keyCodesX2;
    }

    private void pressLeft() {
        this.facade.injectKeyPress((byte) this.kcLeft.getValue());
        this.leftPressed = true;
    }

    private void pressRight() {
        this.facade.injectKeyPress((byte) this.kcRight.getValue());
        this.rightPressed = true;
    }

    private void releaseLeft() {
        this.facade.injectKeyRelease((byte) this.kcLeft.getValue());
        this.leftPressed = false;
    }

    private void releaseRight() {
        this.facade.injectKeyRelease((byte) this.kcRight.getValue());
        this.rightPressed = false;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.AsyncScrollAdapter
    public void setScrolling(ScrollDirections.DirectionX directionX, ScrollDirections.DirectionY directionY) {
        switch (directionX) {
            case LEFT:
                if (this.rightPressed) {
                    releaseRight();
                }
                pressLeft();
                return;
            case RIGHT:
                if (this.leftPressed) {
                    releaseLeft();
                }
                pressRight();
                return;
            case NONE:
                if (this.leftPressed) {
                    releaseLeft();
                    return;
                } else if (!this.rightPressed) {
                    return;
                } else {
                    releaseRight();
                    return;
                }
            default:
                return;
        }
    }
}
