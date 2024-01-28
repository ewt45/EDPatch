package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.adapter;

import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;

public class AndroidPointReporter {
    private static final String TAG ="PointerEventReporter";
    final ViewOfXServer host;
    final int maxDivisor = 20;
    final float maximalDelta;
    private final ViewFacade xServerFacade;
    public AndroidPointReporter() {
        this.host = Const.viewOfXServerRef.get();
        //TODO 日后应该除掉这种null的情况
        this.xServerFacade = host==null?null:host.getXServerFacade();
        this.maximalDelta = xServerFacade==null?20:Math.min(this.xServerFacade.getScreenInfo().heightInPixels, this.xServerFacade.getScreenInfo().widthInPixels) / this.maxDivisor;
    }

    private void sendCoordinates(float f, float f2) {
        //TODO 日后应该除掉这种null的情况
        if(xServerFacade==null)
            return;
        float[] fArr = {f, f2};
        TransformationHelpers.mapPoints(this.host.getViewToXServerTransformationMatrix(), fArr);
//        Log.d(TAG, "sendCoordinates: 设置坐标："+fArr[0]+","+fArr[1]);
        this.xServerFacade.injectPointerMove((int) fArr[0], (int) fArr[1]);
    }

    public void pointerEntered(float f, float f2) {
        sendCoordinates(f, f2);
    }

    public void pointerExited(float f, float f2) {
        sendCoordinates(f, f2);
    }

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
        //TODO 日后应该除掉这种null的情况
        if(xServerFacade==null)
            return;
        int min = Math.min((int) Math.max(Math.abs(dx / this.maximalDelta), Math.abs(dy / this.maximalDelta)), this.maxDivisor);
        float f4 = dx / (float) min;
        float f5 = dy / (float) min;
        this.xServerFacade.injectPointerDelta((int) f4, (int) f5, min);
        this.xServerFacade.injectPointerDelta((int) (dx - (f4 * (float) min)), (int) (dy - (f5 * (float) min)));
    }

    public void buttonPressed(int i) {
        //TODO 日后应该除掉这种null的情况
        if(xServerFacade==null)
            return;
        this.xServerFacade.injectPointerButtonPress(i);
    }

    public void buttonReleased(int i) {
        //TODO 日后应该除掉这种null的情况
        if(xServerFacade==null)
            return;
        this.xServerFacade.injectPointerButtonRelease(i);
    }

    public void wheelScrolledUp() {
        //TODO 日后应该除掉这种null的情况
        if(xServerFacade==null)
            return;
        this.xServerFacade.injectPointerWheelUp(1);
    }

    public void wheelScrolledDown() {
        //TODO 日后应该除掉这种null的情况
        if(xServerFacade==null)
            return;
        this.xServerFacade.injectPointerWheelDown(1);
    }
}
