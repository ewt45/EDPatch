package com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters;

import com.eltechs.axs.GestureStateMachine.GestureContext;

/**
 * 用于相对移动，但是移动起始时先将鼠标位置重置到中心点
 */
public class OffsetMouseFromCenterAdapter extends OffsetMouseAdapter{
    public OffsetMouseFromCenterAdapter(GestureContext gestureContext) {
        super(gestureContext);
    }

    @Override
    public void prepareMoving(float x, float y) {
        int x1 = this.viewOfXServer.getXServerFacade().getScreenInfo().widthInPixels/2;
        int y1 = this.viewOfXServer.getXServerFacade().getScreenInfo().heightInPixels/2;
        this.viewOfXServer.getXServerFacade().injectPointerMove(x1,y1);

        super.prepareMoving(x, y);

    }
}
