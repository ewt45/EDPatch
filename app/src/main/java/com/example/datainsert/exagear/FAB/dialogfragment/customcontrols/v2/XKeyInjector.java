package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2;

import android.os.Handler;
import android.os.Looper;

import com.eltechs.axs.xserver.ViewFacade;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;

import java.util.List;

public class XKeyInjector {
    private static final MouseWheelInjector mouseWheelInjector = new MouseWheelInjector();

    public static void press(ViewFacade viewFacade, int key) {
        if (viewFacade == null)
            return;
        if (!handlePointerButtonPress(viewFacade, key))
            viewFacade.injectKeyPress((byte) (key + 8));

    }

    public static void press(ViewFacade viewFacade, List<Integer> keycodes) {
        if (viewFacade == null)
            return;
        for (int key : keycodes)
            press(viewFacade,key);
    }

    public static void release(ViewFacade viewFacade, int key) {
        if (viewFacade == null)
            return;
        if (!handlePointerButtonRelease(viewFacade, key))
            viewFacade.injectKeyRelease((byte) (key + 8));
    }

    public static void release(ViewFacade viewFacade, List<Integer> keycodes) {
        if (viewFacade == null)
            return;
        for (int key : keycodes)
            release(viewFacade,key);
    }

    private static boolean handlePointerButtonPress(ViewFacade viewFacade, int key) {
        if ((key & Const.keycodePointerMask) ==0)
            return false;

        //如果是滚轮则不停按下松开
        int pointerKeycode = key - Const.keycodePointerMask;
        if (pointerKeycode == 4 || pointerKeycode == 5)
            mouseWheelInjector.start(pointerKeycode);
        else
            viewFacade.injectPointerButtonPress(pointerKeycode);
        return true;
    }




    private static boolean handlePointerButtonRelease(ViewFacade viewFacade, int key) {
        if ((key & Const.keycodePointerMask) ==0)
            return false;

        int pointerKeycode = key - Const.keycodePointerMask;
        if (pointerKeycode == 4 || pointerKeycode == 5)
            mouseWheelInjector.stop(pointerKeycode);
        else
            viewFacade.injectPointerButtonRelease(pointerKeycode);
        return true;
    }

    private static class MouseWheelInjector implements Runnable {
        static final int intervalMs = 30;
        ViewFacade viewFacade;
        Handler handler = new Handler(Looper.getMainLooper()); //不知道用handler的话延迟会不会太大
        boolean isPressing = false;
        int pointerButton = -1;

        public MouseWheelInjector() {
            viewFacade = Const.getViewFacade();
        }

        public void start(int newkey) {
            this.pointerButton = newkey;
            isPressing = true;
            handler.post(this);

        }

        public void stop(int pointerButton) {
            isPressing = false;
        }

        @Override
        public void run() {
            if (!isPressing || pointerButton < 0 || viewFacade == null)
                return;

            int finalPointer = pointerButton;
            viewFacade.injectPointerButtonPress(finalPointer);
            viewFacade.injectPointerButtonRelease(finalPointer);
            handler.postDelayed(this, 30);
        }
    }
}
