package com.example.datainsert.exagear.controls.widget;

import com.eltechs.axs.xserver.ViewFacade;

import java.util.List;

/**
 * 处理自己的keycode与ex的viewfacade的keycode的转换（+8），以及鼠标和键盘的判定（鼠标我设定为256+对应值了）
 */
public class InjectHelper {
    public static void press(ViewFacade viewFacade, int mainCode, List<Integer> subCodes, MouseWheelInject mouseWheelInject) {
        if (viewFacade == null)
            return;
        pressKeyOrPointer(viewFacade, mainCode, mouseWheelInject);
        for (int sub : subCodes) {
            pressKeyOrPointer(viewFacade, sub, mouseWheelInject);
        }

    }

    public static void release(ViewFacade viewFacade, int mainCode, List<Integer> subCodes, MouseWheelInject mouseWheelInject) {
        if (viewFacade == null)
            return;
        releaseKeyOrPointer(viewFacade, mainCode, mouseWheelInject);
        for (int sub : subCodes) {
            releaseKeyOrPointer(viewFacade, sub, mouseWheelInject);
        }
    }

    private static void pressKeyOrPointer(ViewFacade viewFacade, int keycode, MouseWheelInject mouseWheelInject) {
        if (keycode > 256) {
            int pointerKeycode = keycode - 256;
            //如果是滚轮则不停按下松开
            if (pointerKeycode == 4 || pointerKeycode == 5)
                mouseWheelInject.start();
            else
                viewFacade.injectPointerButtonPress(pointerKeycode);
        } else {
            viewFacade.injectKeyPress((byte) (keycode + 8));
        }
    }

    private static void releaseKeyOrPointer(ViewFacade viewFacade, int keycode, MouseWheelInject mouseWheelInject) {
        if (keycode > 256) {
            int pointerKeycode = keycode - 256;
            //如果是滚轮则结束定时器
            if (pointerKeycode == 4 || pointerKeycode == 5)
                mouseWheelInject.stop();
            else
                viewFacade.injectPointerButtonRelease(pointerKeycode);

        } else {
            viewFacade.injectKeyRelease((byte) (keycode + 8));
        }
    }

    /**
     * 如果是鼠标滚轮的话，需要不停地滚动？
     *
     * @param viewFacade
     * @param keycode
     */
    private static void pressPointerScroll(ViewFacade viewFacade, int keycode) {


    }
}
