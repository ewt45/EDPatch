package com.eltechs.axs.helpers.imageDetector;

import com.eltechs.axs.finiteStateMachine.AbstractFSMState;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.PlacedDrawable;
import com.eltechs.axs.xserver.ViewFacade;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowContentModificationListener;
import com.eltechs.axs.xserver.WindowLifecycleListener;
import java.util.Iterator;

/* loaded from: classes.dex */
public class FSMStateWaitForImageUnmatchOnWindow extends AbstractFSMState implements WindowContentModificationListener, WindowLifecycleListener {
    public static FSMEvent IMAGE_UNMATCHED = new FSMEvent() { // from class: com.eltechs.axs.helpers.imageDetector.FSMStateWaitForImageUnmatchOnWindow.1
    };
    public static FSMEvent TARGET_NOT_FOUND = new FSMEvent() { // from class: com.eltechs.axs.helpers.imageDetector.FSMStateWaitForImageUnmatchOnWindow.2
    };
    final ImageDetector comparer1;
    final ImageDetector comparer2;
    Drawable target = null;
    final ViewFacade viewFacade;

    @Override // com.eltechs.axs.xserver.WindowContentModificationListener
    public void frontBufferReplaced(Window window) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowCreated(Window window) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowDestroyed(Window window) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowMapped(Window window) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowReparented(Window window, Window window2) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowZOrderChange(Window window) {
    }

    public FSMStateWaitForImageUnmatchOnWindow(int i, int i2, Rectangle rectangle, Rectangle rectangle2, byte[] bArr, byte[] bArr2, ViewFacade viewFacade) {
        this.viewFacade = viewFacade;
        this.comparer1 = new ImageDetector(bArr, rectangle);
        this.comparer2 = new ImageDetector(bArr2, rectangle2);
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        System.out.printf("WAiting for unmatch\n", new Object[0]);
        Assert.state(this.target == null);
        Iterator<PlacedDrawable> it = this.viewFacade.listNonRootWindowDrawables().iterator();
        while (it.hasNext()) {
            PlacedDrawable next = it.next();
            if (checkMatch(next.getDrawable())) {
                this.target = next.getDrawable();
                this.viewFacade.addWindowContentModificationListner(this);
                this.viewFacade.addWindowLifecycleListener(this);
                return;
            }
        }
        sendEvent(TARGET_NOT_FOUND);
    }

    boolean checkMatch(Drawable drawable) {
        return this.comparer1.isSamplePresentInDrawable(drawable) || this.comparer2.isSamplePresentInDrawable(drawable);
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        if (this.target != null) {
            this.viewFacade.removeWindowContentModificationListner(this);
            this.viewFacade.removeWindowLifecycleListener(this);
            this.target = null;
        }
    }

    @Override // com.eltechs.axs.xserver.WindowContentModificationListener
    public void contentChanged(Window window, int i, int i2, int i3, int i4) {
        Assert.state(this.target != null);
        if (window.getActiveBackingStore() != this.target || checkMatch(this.target)) {
            return;
        }
        System.out.printf("image unmatched\n", new Object[0]);
        sendEventIfActive(IMAGE_UNMATCHED);
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowUnmapped(Window window) {
        Assert.state(this.target != null);
        if (window.getActiveBackingStore() == this.target) {
            System.out.printf("image unmatched (d gone)\n", new Object[0]);
            sendEventIfActive(IMAGE_UNMATCHED);
        }
    }
}
