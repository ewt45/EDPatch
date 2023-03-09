package com.example.datainsert.exagear.controls.interfaceOverlay.widget;

import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;

import java.util.List;

/**
 * 处理自己的keycode与ex的viewfacade的keycode的转换（+8），以及鼠标和键盘的判定（鼠标我设定为256+对应值了）
 */
public class InjectHelper {
    public static void press(ViewFacade viewFacade, int mainCode, List<Integer> subCodes){
        if(viewFacade==null)
            return;
        pressKeyOrPointer(viewFacade,mainCode);
        for(int sub:subCodes){
            pressKeyOrPointer(viewFacade,sub);
        }

    }
    public static void release(ViewFacade viewFacade, int mainCode, List<Integer> subCodes){
        if(viewFacade==null)
            return;
        releaseKeyOrPointer(viewFacade,mainCode);
        for(int sub:subCodes){
            releaseKeyOrPointer(viewFacade,sub);
        }
    }

    private static void pressKeyOrPointer(ViewFacade viewFacade, int keycode){
        if(keycode>256){
            viewFacade.injectPointerButtonPress(keycode-256);
        }else{
            viewFacade.injectKeyPress((byte) (keycode+8));
        }
    }

    private static void releaseKeyOrPointer(ViewFacade viewFacade, int keycode){
        if(keycode>256){
            viewFacade.injectPointerButtonRelease(keycode-256);
        }else{
            viewFacade.injectKeyRelease((byte) (keycode+8));
        }
    }
}
