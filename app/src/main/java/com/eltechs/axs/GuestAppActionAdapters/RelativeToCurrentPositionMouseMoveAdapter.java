package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.geom.Point;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;

/* loaded from: classes.dex */
public class RelativeToCurrentPositionMouseMoveAdapter implements MouseMoveAdapter {
    private final ViewFacade facade;
    private final ViewOfXServer host;
    private OffsetMouseMoveAdapter subadapter;
    private final MouseMoveAdapter subsubadapter;

    public RelativeToCurrentPositionMouseMoveAdapter(MouseMoveAdapter mouseMoveAdapter, ViewFacade viewFacade, ViewOfXServer viewOfXServer) {
        this.subsubadapter = mouseMoveAdapter;
        this.facade = viewFacade;
        this.host = viewOfXServer;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void prepareMoving(float f, float f2) {
        Point pointerLocation = this.facade.getPointerLocation();
        float[] fArr = {pointerLocation.x, pointerLocation.y};
        TransformationHelpers.mapPoints(this.host.getXServerToViewTransformationMatrix(), fArr);
        this.subadapter = new OffsetMouseMoveAdapter(this.subsubadapter, fArr[0] - f, fArr[1] - f2);
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void moveTo(float f, float f2) {
        Assert.state(this.subadapter != null);
        this.subadapter.moveTo(f, f2);
    }
}
