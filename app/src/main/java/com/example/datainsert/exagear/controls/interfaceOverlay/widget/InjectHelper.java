package com.example.datainsert.exagear.controls.interfaceOverlay.widget;

import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;

/**
 * 处理自己的keycode与ex的viewfacade的keycode的转换（+8），以及鼠标和键盘的判定（鼠标我设定为256+对应值了）
 */
public class InjectHelper {
    public static void press(ViewFacade viewFacade, int customKeyCode){
        if(viewFacade==null)
            return;
        if(customKeyCode>256){
            viewFacade.injectPointerButtonPress(customKeyCode-256);
        }else{
            viewFacade.injectKeyPress((byte) (customKeyCode+8));
        }
    }
    public static void release(ViewFacade viewFacade, int customKeyCode){
        if(viewFacade==null)
            return;
        if(customKeyCode>256){
            viewFacade.injectPointerButtonRelease(customKeyCode-256);
        }else{
            viewFacade.injectKeyRelease((byte) (customKeyCode+8));
        }
    }
}
