package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Cursor;
import com.eltechs.axs.xserver.DeviceGrabMode;
import com.eltechs.axs.xserver.EventName;
import com.eltechs.axs.xserver.GrabsManager;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowLifecycleListener;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;
import com.eltechs.axs.xserver.client.XClientWindowListener;
import com.eltechs.axs.xserver.helpers.WindowHelpers;
import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public class GrabsManagerImpl implements GrabsManager, WindowLifecycleListener {
    private boolean pointerGrabOwnerEvents;
    private boolean pointerGrabReleaseWithButtons;
    private final XServer xServer;
    private int lastPointerGrabTime = 0;
    private XClientWindowListener pointerGrabListener = null;
    private Window pointerGrabWindow = null;
    private Cursor grabCursor = null;

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
    public void windowZOrderChange(Window window) {
    }

    public GrabsManagerImpl(XServer xServer) {
        this.xServer = xServer;
        this.xServer.getWindowsManager().addWindowLifecycleListener(this);
    }

    private void initiateActivePointerGrab(Window window, XClientWindowListener xClientWindowListener, boolean z, Cursor cursor, Window window2, DeviceGrabMode deviceGrabMode, DeviceGrabMode deviceGrabMode2, int i, boolean z2) {
        Assert.state(window != null);
        if (i == 0) {
            this.lastPointerGrabTime = (int) System.currentTimeMillis();
        }
        if (deviceGrabMode != DeviceGrabMode.ASYNCHRONOUS || deviceGrabMode2 != DeviceGrabMode.ASYNCHRONOUS) {
            Assert.notImplementedYet();
        }
        if (this.pointerGrabWindow == null) {
            this.xServer.getPointerEventSender().sendGrabActivationEvents(window);
        }
        this.grabCursor = cursor;
        this.pointerGrabWindow = window;
        this.pointerGrabReleaseWithButtons = z2;
        this.pointerGrabOwnerEvents = z;
        this.pointerGrabListener = xClientWindowListener;
    }

    @Override // com.eltechs.axs.xserver.GrabsManager
    public void disablePointerGrab() {
        if (this.pointerGrabWindow != null) {
            Window window = this.pointerGrabWindow;
            this.pointerGrabWindow = null;
            this.grabCursor = null;
            this.pointerGrabListener = null;
            this.xServer.getPointerEventSender().sendGrabDeactivationEvents(window);
        }
    }

    @Override // com.eltechs.axs.xserver.GrabsManager
    public void disableAutomaticOrPassiveGrab() {
        if (this.pointerGrabReleaseWithButtons) {
            disablePointerGrab();
        }
    }

    @Override // com.eltechs.axs.xserver.GrabsManager
    public void initiateActivePointerGrab(Window window, boolean z, Mask<EventName> mask, Cursor cursor, Window window2, DeviceGrabMode deviceGrabMode, DeviceGrabMode deviceGrabMode2, int i, XClient xClient) {
        initiateActivePointerGrab(window, new XClientWindowListener(xClient, mask), z, cursor, window2, deviceGrabMode, deviceGrabMode2, i, false);
    }

    @Override // com.eltechs.axs.xserver.GrabsManager
    public void initiateAutomaticPointerGrab(Window window) {
        XClientWindowListener buttonPressListener = window.getEventListenersList().getButtonPressListener();
        initiateActivePointerGrab(window, buttonPressListener, buttonPressListener.getMask().isSet(EventName.OWNER_GRAB_BUTTON), (Cursor) null, (Window) null, DeviceGrabMode.ASYNCHRONOUS, DeviceGrabMode.ASYNCHRONOUS, 0, true);
    }

    @Override // com.eltechs.axs.xserver.GrabsManager
    public Window getPointerGrabWindow() {
        return this.pointerGrabWindow;
    }

    @Override // com.eltechs.axs.xserver.GrabsManager
    public XClientWindowListener getPointerGrabListener() {
        return this.pointerGrabListener;
    }

    @Override // com.eltechs.axs.xserver.GrabsManager
    public boolean getPointerGrabOwnerEvents() {
        return this.pointerGrabOwnerEvents;
    }

    @Override // com.eltechs.axs.xserver.GrabsManager
    public int getPointerGrabTime() {
        return this.lastPointerGrabTime;
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowUnmapped(Window window) {
        if (this.pointerGrabWindow == null || WindowHelpers.getWindowMapState(this.pointerGrabWindow) == WindowHelpers.MapState.VIEWABLE) {
            return;
        }
        disablePointerGrab();
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowReparented(Window window, Window window2) {
        if (this.pointerGrabWindow == null || WindowHelpers.getWindowMapState(this.pointerGrabWindow) == WindowHelpers.MapState.VIEWABLE) {
            return;
        }
        disablePointerGrab();
    }
}
