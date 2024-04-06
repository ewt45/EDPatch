package com.eltechs.axs.helpers.imageDetector;

import com.eltechs.axs.finiteStateMachine.AbstractFSMState;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.xserver.ViewFacade;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowContentModificationListener;

/* loaded from: classes.dex */
public class FSMStateWaitForImageMatchOnWindow extends AbstractFSMState implements WindowContentModificationListener {
    public static FSMEvent IMAGE_MATCHED = new FSMEvent() { // from class: com.eltechs.axs.helpers.imageDetector.FSMStateWaitForImageMatchOnWindow.1
    };
    final ImageDetector comparer1;
    final ImageDetector comparer2;
    final ViewFacade viewFacade;
    final int windowHeight;
    final int windowWidth;

    @Override // com.eltechs.axs.xserver.WindowContentModificationListener
    public void frontBufferReplaced(Window window) {
    }

    public FSMStateWaitForImageMatchOnWindow(int i, int i2, Rectangle rectangle, Rectangle rectangle2, byte[] bArr, byte[] bArr2, ViewFacade viewFacade) {
        this.windowWidth = i;
        this.windowHeight = i2;
        this.viewFacade = viewFacade;
        this.comparer1 = new ImageDetector(bArr, rectangle);
        this.comparer2 = new ImageDetector(bArr2, rectangle2);
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        System.out.printf("WAiting for match\n", new Object[0]);
        this.viewFacade.addWindowContentModificationListner(this);
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        this.viewFacade.removeWindowContentModificationListner(this);
    }

    @Override // com.eltechs.axs.xserver.WindowContentModificationListener
    public void contentChanged(Window window, int i, int i2, int i3, int i4) {
        if (this.windowWidth == window.getBoundingRectangle().width && this.windowHeight == window.getBoundingRectangle().height) {
            if (this.comparer1.isSamplePresentInDrawable(window.getActiveBackingStore()) || this.comparer2.isSamplePresentInDrawable(window.getActiveBackingStore())) {
                sendEventIfActive(IMAGE_MATCHED);
            }
        }
    }
}
