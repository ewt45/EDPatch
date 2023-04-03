package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.geom.Point;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;

/* loaded from: classes.dex */
public class AlignedMouseClickAdapter implements MousePointAndClickAdapter {
    private MouseClickAdapter actualClicker = null;
    private final float clickAlignThreshold;
    private final MouseClickAdapter clickerIfFar;
    private final MouseClickAdapter clickerIfNear;
    private final ViewOfXServer host;
    private final MouseMoveAdapter mover;
    private final PointerContext pointerContext;

    public AlignedMouseClickAdapter(MouseMoveAdapter mover, MouseClickAdapter clickerIfNear, MouseClickAdapter clickerIfFar, ViewOfXServer viewOfXServer, PointerContext pointerContext, float clickAlignThreshold) {
        this.mover = mover;
        this.clickerIfNear = clickerIfNear;
        this.clickerIfFar = clickerIfFar;
        this.host = viewOfXServer;
        this.pointerContext = pointerContext;
        this.clickAlignThreshold = clickAlignThreshold;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MousePointAndClickAdapter
    public void click(float f, float f2) {
        Assert.state(this.actualClicker == null, "click() and finalizeClick() were called in wrong order!");
        Point pointerLocation = this.host.getXServerFacade().getPointerLocation();
        float[] fArr = {pointerLocation.x, pointerLocation.y};
        float[] fArr2 = new float[2];
        this.host.getXServerToViewTransformationMatrix().mapPoints(fArr2, fArr);
        this.mover.prepareMoving(f, f2);
        if (GeometryHelpers.distance(f, f2, fArr2[0], fArr2[1]) >= this.clickAlignThreshold) {
            this.mover.moveTo(f, f2);
            this.actualClicker = this.clickerIfFar;
        } else {
            this.actualClicker = this.clickerIfNear;
        }
        this.actualClicker.click();
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MousePointAndClickAdapter
    public void finalizeClick(float f, float f2) {
        Assert.state(this.actualClicker != null, "click() and finalizeClick() were called in wrong order!");
        this.actualClicker.finalizeClick();
        this.pointerContext.setLastMoveMethod(PointerContext.MoveMethod.TAP);
        this.actualClicker = null;
    }
}
