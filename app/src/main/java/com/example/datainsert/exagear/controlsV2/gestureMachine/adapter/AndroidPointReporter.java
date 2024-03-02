package com.example.datainsert.exagear.controlsV2.gestureMachine.adapter;

import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.XServerViewHolder;

public class AndroidPointReporter {
    private static final String TAG = "PointerEventReporter";
    final XServerViewHolder host;
    final int maxDivisor = 20;
    final float maximalDelta;
//    private final ViewFacade xServerFacade;

    public AndroidPointReporter() {
        this.host = Const.getXServerHolder();
        //TODO 日后应该除掉这种null的情况
        int[] xWH = host.getXScreenPixels();
        this.maximalDelta = xWH == null ? 20 : 1.0f * Math.min(xWH[0], xWH[1]) / this.maxDivisor;
    }

    private void sendCoordinates(float f, float f2) {
        float[] fArr = {f, f2};
        TransformationHelpers.mapPoints(this.host.getViewToXServerTransformationMatrix(), fArr);
//        Log.d(TAG, "sendCoordinates: 设置坐标："+fArr[0]+","+fArr[1]);
        host.injectPointerMove((int) fArr[0], (int) fArr[1]);
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
        int min = Math.min((int) Math.max(Math.abs(dx / this.maximalDelta), Math.abs(dy / this.maximalDelta)), this.maxDivisor);
        float f4 = dx / (float) min;
        float f5 = dy / (float) min;
        host.injectPointerDelta((int) f4, (int) f5, min);
        host.injectPointerDelta((int) (dx - (f4 * (float) min)), (int) (dy - (f5 * (float) min)));
    }

    public void buttonPressed(int i) {
        host.injectPointerButtonPress(i);
    }

    public void buttonReleased(int i) {
        host.injectPointerButtonRelease(i);
    }

    public void wheelScrolledUp() {
        this.host.injectPointerWheelUp(1);
    }

    public void wheelScrolledDown() {
        this.host.injectPointerWheelDown(1);
    }
}
