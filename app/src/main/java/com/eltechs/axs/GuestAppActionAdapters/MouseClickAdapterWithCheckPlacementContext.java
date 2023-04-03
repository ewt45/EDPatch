package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.GestureStateMachine.PointerContext;

/* loaded from: classes.dex */
public class MouseClickAdapterWithCheckPlacementContext implements MousePointAndClickAdapter {
    private MousePointAndClickAdapter actualClicker;
    private final MousePointAndClickAdapter clickerAim;
    private final MousePointAndClickAdapter clickerDouble;
    private final MousePointAndClickAdapter clickerTap;
    private final int doubleClickMaxInterval;
    private final PointerContext pointerContext;

    public MouseClickAdapterWithCheckPlacementContext(MousePointAndClickAdapter clickerTap, MousePointAndClickAdapter clickerAim, MousePointAndClickAdapter clickerDouble, PointerContext pointerContext, int doubleClickMaxInterval) {
        this.clickerTap = clickerTap;
        this.clickerAim = clickerAim;
        this.clickerDouble = clickerDouble;
        this.pointerContext = pointerContext;
        this.doubleClickMaxInterval = doubleClickMaxInterval;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MousePointAndClickAdapter
    public void click(float f, float f2) {
        if (System.currentTimeMillis() - this.pointerContext.getLastMoveTimestamp() < this.doubleClickMaxInterval) {
            this.actualClicker = this.clickerDouble;
        } else if (this.pointerContext.getLastMoveMethod() == PointerContext.MoveMethod.AIM) {
            this.actualClicker = this.clickerAim;
        } else {
            this.actualClicker = this.clickerTap;
        }
        this.actualClicker.click(f, f2);
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MousePointAndClickAdapter
    public void finalizeClick(float f, float f2) {
        this.actualClicker.finalizeClick(f, f2);
    }
}
