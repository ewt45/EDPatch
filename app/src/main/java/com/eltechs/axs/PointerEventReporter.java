package com.eltechs.axs;

import android.util.Log;

import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;

/* loaded from: classes.dex */
public class PointerEventReporter implements PointerEventListener {
    private static final String TAG ="PointerEventReporter";
    final ViewOfXServer host;
    final int maxDivisor = 20;
    final float maximalDelta;
    private final ViewFacade xServerFacade;

    public PointerEventReporter(ViewOfXServer viewOfXServer) {
        this.host = viewOfXServer;
        this.xServerFacade = viewOfXServer.getXServerFacade();
        this.maximalDelta = Math.min(this.xServerFacade.getScreenInfo().heightInPixels, this.xServerFacade.getScreenInfo().widthInPixels) / this.maxDivisor;
    }

    private void sendCoordinates(float f, float f2) {
        float[] fArr = {f, f2};
        TransformationHelpers.mapPoints(this.host.getViewToXServerTransformationMatrix(), fArr);
//        Log.d(TAG, "sendCoordinates: 设置坐标："+fArr[0]+","+fArr[1]);
        this.xServerFacade.injectPointerMove((int) fArr[0], (int) fArr[1]);
    }

    @Override // com.eltechs.axs.PointerEventListener
    public void pointerEntered(float f, float f2) {
        sendCoordinates(f, f2);
    }

    @Override // com.eltechs.axs.PointerEventListener
    public void pointerExited(float f, float f2) {
        sendCoordinates(f, f2);
    }

    @Override // com.eltechs.axs.PointerEventListener
    public void pointerMove(float f, float f2) {
        sendCoordinates(f, f2);
    }

    public void clickAtPoint(float f, float f2, int i, int i2) {
        pointerMove(f, f2);
        click(i, i2);
    }

    public void click(int i, int i2) {
        try {
            Thread.sleep(i2, 0);
        } catch (InterruptedException ignored) {
        }
        buttonPressed(i);
        try {
            Thread.sleep(i2, 0);
        } catch (InterruptedException ignored) {
        }
        buttonReleased(i);
    }

    public void pointerMoveDelta(float dx, float dy) {
        int min = Math.min((int) Math.max(Math.abs(dx / this.maximalDelta), Math.abs(dy / this.maximalDelta)), this.maxDivisor);
        float f4 = dx / (float) min;
        float f5 = dy / (float) min;
        this.xServerFacade.injectPointerDelta((int) f4, (int) f5, min);
        this.xServerFacade.injectPointerDelta((int) (dx - (f4 * (float) min)), (int) (dy - (f5 * (float) min)));
    }

    public void buttonPressed(int i) {
        this.xServerFacade.injectPointerButtonPress(i);
    }

    public void buttonReleased(int i) {
        this.xServerFacade.injectPointerButtonRelease(i);
    }

    public void wheelScrolledUp() {
        this.xServerFacade.injectPointerWheelUp(1);
    }

    public void wheelScrolledDown() {
        this.xServerFacade.injectPointerWheelDown(1);
    }
}