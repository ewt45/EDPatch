package com.eltechs.axs;

import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;
import com.eltechs.axs.xserver.XServer;

public class PointerEventReporter implements PointerEventListener {
    final ViewOfXServer host;
    final int maxDivisor = 20;
    final float maximalDelta;
    private final ViewFacade xServerFacade;

    public PointerEventReporter(ViewOfXServer viewOfXServer) {
        this.host = viewOfXServer;
        if(viewOfXServer!=null){
            this.xServerFacade = viewOfXServer.getXServerFacade();
            this.maximalDelta = Math.min(this.xServerFacade.getScreenInfo().heightInPixels, this.xServerFacade.getScreenInfo().widthInPixels) / this.maxDivisor;

        }else{
            maximalDelta = 0;
            xServerFacade = new ViewFacade(null);
        }
    }


    @Override
    public void pointerEntered(float f, float f2) {

    }

    @Override
    public void pointerExited(float f, float f2) {

    }

    @Override
    public void pointerMove(float f, float f2) {

    }

    public void buttonPressed(int mouseButton) {
    }

    public void buttonReleased(int mouseButton) {
    }
}
