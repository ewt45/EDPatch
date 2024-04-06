package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.geom.Point;
import com.eltechs.axs.xserver.ViewFacade;

/* loaded from: classes.dex */
public class Helpers {
    /**
     * 调整指针位置，保证其距离视图边缘有一定距离
     */
    public static void adjustPointerPosition(ViewFacade viewFacade, int marginToEdge) {
        Point pointerLocation = viewFacade.getPointerLocation();
        int nowX = pointerLocation.x;
        int nowY = pointerLocation.y;
        if (nowX < marginToEdge) {
            nowX = marginToEdge;
        } else if (nowX > viewFacade.getScreenInfo().widthInPixels - marginToEdge) {
            nowX = viewFacade.getScreenInfo().widthInPixels - marginToEdge;
        }
        if(nowY < marginToEdge)
            nowY = marginToEdge;
        else if(nowY > viewFacade.getScreenInfo().heightInPixels - marginToEdge)
            nowY = viewFacade.getScreenInfo().heightInPixels - marginToEdge;

        viewFacade.injectPointerMove(nowX, nowY);
    }
}
