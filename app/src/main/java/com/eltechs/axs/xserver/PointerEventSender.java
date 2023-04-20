package com.eltechs.axs.xserver;

import android.util.Log;

import com.eltechs.axs.geom.Point;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.client.XClientWindowListener;
import com.eltechs.axs.xserver.events.ButtonPress;
import com.eltechs.axs.xserver.events.ButtonRelease;
import com.eltechs.axs.xserver.events.EnterNotify;
import com.eltechs.axs.xserver.events.Event;
import com.eltechs.axs.xserver.events.LeaveNotify;
import com.eltechs.axs.xserver.events.MotionNotify;
import com.eltechs.axs.xserver.events.PointerWindowEvent;
import com.eltechs.axs.xserver.helpers.EventHelpers;
import com.eltechs.axs.xserver.helpers.WindowHelpers;
import com.eltechs.axs.xserver.impl.masks.Mask;
import com.example.datainsert.exagear.controls.interfaceOverlay.FalloutInterfaceOverlay2;

/* loaded from: classes.dex */
public class PointerEventSender implements PointerListener, WindowLifecycleListener, WindowChangeListener {
    private static final String TAG = "PointerEventSender";
    private final XServer xServer;
    private Window pointWindow;

    public PointerEventSender(XServer xServer) {
        this.pointWindow = xServer.getWindowsManager().getRootWindow();
        this.xServer = xServer;
        this.xServer.getPointer().addListener(this);
        this.xServer.getWindowsManager().addWindowLifecycleListener(this);
        this.xServer.getWindowsManager().addWindowChangeListener(this);
    }

    @Override // com.eltechs.axs.xserver.WindowChangeListener
    public void attributesChanged(Window window, Mask<WindowAttributeNames> mask) {
    }

    private void sendEventForEventMask(Event event, EventName eventName, Window window) {
        GrabsManager grabsManager = this.xServer.getGrabsManager();
        Window pointerGrabWindow = grabsManager.getPointerGrabWindow();
        if (pointerGrabWindow != null) {
            XClientWindowListener pointerGrabListener = grabsManager.getPointerGrabListener();
            if (grabsManager.getPointerGrabOwnerEvents() && window != null) {
                window.getEventListenersList().sendEventForEventNameToClient(event, eventName, pointerGrabListener.getClient());
                return;
            } else if (pointerGrabListener.isInterestedIn(eventName)) {
                pointerGrabListener.onEvent(pointerGrabWindow, event);
                return;
            } else {
                return;
            }
        }
        window.getEventListenersList().sendEventForEventName(event, eventName);
    }

    private void sendVirtualLeaveNotify(Window window, Window window2, PointerWindowEvent.Detail detail, PointerWindowEvent.Mode mode, int i) {
        Window window3 = window2;
        PointerWindowEvent.Detail detail2 = detail;
        boolean z = false;
        Assert.isTrue(window != window3);
        Assert.isTrue(WindowHelpers.isAncestorOf(window, window2));
        if (detail2 == PointerWindowEvent.Detail.VIRTUAL || detail2 == PointerWindowEvent.Detail.NONLINEAR_VIRTUAL) {
            z = true;
        }
        Assert.isTrue(z);
        Mask<KeyButNames> keyButMask = EventHelpers.getKeyButMask(this.xServer);
        int x = this.xServer.getPointer().getX();
        int y = this.xServer.getPointer().getY();
        Window focusedWindow = this.xServer.getFocusManager().getFocusedWindow();
        Window rootWindow = this.xServer.getWindowsManager().getRootWindow();
        Window parent = window.getParent();
        Window window4 = window;
        while (parent != window3) {
            boolean isAncestorOf = WindowHelpers.isAncestorOf(parent, focusedWindow);
            Point convertRootCoordsToWindow = WindowHelpers.convertRootCoordsToWindow(parent, x, y);
            Window window5 = parent;
            sendEventForEventMask(new LeaveNotify(detail2, mode, i, rootWindow, parent, window4, (short) x, (short) y, (short) convertRootCoordsToWindow.x, (short) convertRootCoordsToWindow.y, keyButMask, isAncestorOf), EventName.LEAVE_WINDOW, window5);
            parent = window5.getParent();
            window4 = window5;
            y = y;
            focusedWindow = focusedWindow;
            x = x;
            window3 = window2;
            detail2 = detail;
        }
    }

    private void sendVirtualEnterNotify(Window window, Window window2, PointerWindowEvent.Detail detail, PointerWindowEvent.Mode mode, int i) {
        Window window3 = window2;
        PointerWindowEvent.Detail detail2 = detail;
        boolean z = false;
        Assert.isTrue(window != window3);
        Assert.isTrue(WindowHelpers.isAncestorOf(window3, window));
        if (detail2 == PointerWindowEvent.Detail.VIRTUAL || detail2 == PointerWindowEvent.Detail.NONLINEAR_VIRTUAL) {
            z = true;
        }
        Assert.isTrue(z);
        Mask<KeyButNames> keyButMask = EventHelpers.getKeyButMask(this.xServer);
        int x = this.xServer.getPointer().getX();
        int y = this.xServer.getPointer().getY();
        Window focusedWindow = this.xServer.getFocusManager().getFocusedWindow();
        Window rootWindow = this.xServer.getWindowsManager().getRootWindow();
        Window directChild = WindowHelpers.getDirectChild(window3, window);
        while (directChild != window3) {
            boolean isAncestorOf = WindowHelpers.isAncestorOf(directChild, focusedWindow);
            Point convertRootCoordsToWindow = WindowHelpers.convertRootCoordsToWindow(directChild, x, y);
            Window directChild2 = WindowHelpers.getDirectChild(window3, directChild);
            sendEventForEventMask(new EnterNotify(detail2, mode, i, rootWindow, directChild, directChild2, (short) x, (short) y, (short) convertRootCoordsToWindow.x, (short) convertRootCoordsToWindow.y, keyButMask, isAncestorOf), EventName.ENTER_WINDOW, directChild);
            detail2 = detail;
            directChild = directChild2;
            x = x;
            y = y;
            focusedWindow = focusedWindow;
            window3 = window2;
        }
    }

    private void sendEnterLeaveNotify(Window window, Window window2, PointerWindowEvent.Mode mode) {
        PointerWindowEvent.Detail detail;
        PointerWindowEvent.Detail detail2;
        short s;
        short s2;
        Window window3;
        Point point;
        if (window2 != window) {
            Mask<KeyButNames> keyButMask = EventHelpers.getKeyButMask(this.xServer);
            int x = this.xServer.getPointer().getX();
            int y = this.xServer.getPointer().getY();
            Point convertRootCoordsToWindow = WindowHelpers.convertRootCoordsToWindow(window, x, y);
            Point convertRootCoordsToWindow2 = WindowHelpers.convertRootCoordsToWindow(window2, x, y);
            Window rootWindow = this.xServer.getWindowsManager().getRootWindow();
            boolean isAncestorOf = WindowHelpers.isAncestorOf(window2, this.xServer.getFocusManager().getFocusedWindow());
            int currentTimeMillis = (int) System.currentTimeMillis();
            if (WindowHelpers.isAncestorOf(window, window2)) {
                detail = PointerWindowEvent.Detail.ANCESTOR;
                detail2 = PointerWindowEvent.Detail.INFERIOR;
            } else if (WindowHelpers.isAncestorOf(window2, window)) {
                detail = PointerWindowEvent.Detail.INFERIOR;
                detail2 = PointerWindowEvent.Detail.ANCESTOR;
            } else {
                detail = PointerWindowEvent.Detail.NONLINEAR;
                detail2 = PointerWindowEvent.Detail.NONLINEAR;
            }
            PointerWindowEvent.Detail detail3 = detail;
            PointerWindowEvent.Detail detail4 = detail2;
            short s3 = (short) x;
            short s4 = (short) y;
            sendEventForEventMask(new LeaveNotify(detail3, mode, currentTimeMillis, rootWindow, window, null, s3, s4, (short) convertRootCoordsToWindow.x, (short) convertRootCoordsToWindow.y, keyButMask, isAncestorOf), EventName.LEAVE_WINDOW, window);
            switch (detail3) {
                case ANCESTOR:
                    s = s3;
                    s2 = s4;
                    window3 = window2;
                    point = convertRootCoordsToWindow2;
                    sendVirtualLeaveNotify(window, window3, PointerWindowEvent.Detail.VIRTUAL, mode, currentTimeMillis);
                    break;
                case INFERIOR:
                    s = s3;
                    s2 = s4;
                    window3 = window2;
                    point = convertRootCoordsToWindow2;
                    sendVirtualEnterNotify(window, window3, PointerWindowEvent.Detail.VIRTUAL, mode, currentTimeMillis);
                    break;
                case NONLINEAR:
                    Window leastCommonAncestor = WindowHelpers.getLeastCommonAncestor(window, window2);
                    s = s3;
                    s2 = s4;
                    point = convertRootCoordsToWindow2;
                    window3 = window2;
                    sendVirtualLeaveNotify(window, leastCommonAncestor, PointerWindowEvent.Detail.NONLINEAR_VIRTUAL, mode, currentTimeMillis);
                    sendVirtualEnterNotify(leastCommonAncestor, window3, PointerWindowEvent.Detail.NONLINEAR_VIRTUAL, mode, currentTimeMillis);
                    break;
                default:
                    s = s3;
                    s2 = s4;
                    window3 = window2;
                    point = convertRootCoordsToWindow2;
                    break;
            }
            sendEventForEventMask(new EnterNotify(detail4, mode, currentTimeMillis, rootWindow, window3, null, s, s2, (short) point.x, (short) point.y, keyButMask, isAncestorOf), EventName.ENTER_WINDOW, window3);
        }
    }

    private void updatePointWindow() {
        Window calculatePointWindow = WindowHelpers.calculatePointWindow(this.xServer);
        sendEnterLeaveNotify(this.pointWindow, calculatePointWindow, PointerWindowEvent.Mode.NORMAL);
        this.pointWindow = calculatePointWindow;
    }

    public void sendGrabActivationEvents(Window window) {
        Assert.state(this.xServer.getGrabsManager().getPointerGrabWindow() == null);
        sendEnterLeaveNotify(this.pointWindow, window, PointerWindowEvent.Mode.GRAB);
    }

    public void sendGrabDeactivationEvents(Window window) {
        Assert.state(this.xServer.getGrabsManager().getPointerGrabWindow() == null);
        sendEnterLeaveNotify(window, this.pointWindow, PointerWindowEvent.Mode.UNGRAB);
    }

    private Mask<EventName> createCurrentEventMask() {
        Mask<KeyButNames> buttonMask = this.xServer.getPointer().getButtonMask();
        Mask<EventName> emptyMask = Mask.emptyMask(EventName.class);
        emptyMask.set(EventName.POINTER_MOTION);
        if (!buttonMask.isEmpty()) {
            emptyMask.set(EventName.BUTTON_MOTION);
            if (buttonMask.isSet(KeyButNames.BUTTON1)) {
                emptyMask.set(EventName.BUTTON_1_MOTION);
            }
            if (buttonMask.isSet(KeyButNames.BUTTON2)) {
                emptyMask.set(EventName.BUTTON_2_MOTION);
            }
            if (buttonMask.isSet(KeyButNames.BUTTON3)) {
                emptyMask.set(EventName.BUTTON_3_MOTION);
            }
            if (buttonMask.isSet(KeyButNames.BUTTON4)) {
                emptyMask.set(EventName.BUTTON_4_MOTION);
            }
            if (buttonMask.isSet(KeyButNames.BUTTON5)) {
                emptyMask.set(EventName.BUTTON_5_MOTION);
            }
        }
        return emptyMask;
    }

    @Override // com.eltechs.axs.xserver.PointerListener
    public void pointerMoved(int rootX, int rootY) {
        updatePointWindow();
        Mask<EventName> createCurrentEventMask = createCurrentEventMask();
        GrabsManager grabsManager = this.xServer.getGrabsManager();
        //鼠标移动也用的grab的吗，看XClientWindowListener貌似只有grab里new了一个
        XClientWindowListener pointerGrabListener = grabsManager.getPointerGrabListener();
        Window pointerGrabWindow = grabsManager.getPointerGrabWindow();

        Window ancestorWithDeviceEventMask = (pointerGrabWindow == null || grabsManager.getPointerGrabOwnerEvents()) ? WindowHelpers.getAncestorWithDeviceEventMask(this.pointWindow, createCurrentEventMask) : null;
        if (pointerGrabWindow == null && ancestorWithDeviceEventMask == null) {
            return;
        }
        Window eventWindow = ancestorWithDeviceEventMask != null ? ancestorWithDeviceEventMask : pointerGrabWindow;
        Mask<KeyButNames> keyButMask = EventHelpers.getKeyButMask(this.xServer);
        Point convertRootCoordsToWindow = WindowHelpers.convertRootCoordsToWindow(eventWindow, rootX, rootY);
        Window rootWindow = this.xServer.getWindowsManager().getRootWindow();
        Window childWindow = (this.pointWindow != eventWindow && WindowHelpers.isAncestorOf(this.pointWindow, eventWindow))
                ? WindowHelpers.getDirectChild(this.pointWindow, eventWindow)
                : null;
        MotionNotify motionNotify =
                new MotionNotify(false, (int) System.currentTimeMillis(), rootWindow, eventWindow, childWindow, (short) rootX, (short) rootY, (short) convertRootCoordsToWindow.x, (short) convertRootCoordsToWindow.y, keyButMask);

//        FalloutInterfaceOverlay2.isCursorLocked
//                        ? new MotionNotify(false, (int) System.currentTimeMillis(), rootWindow, eventWindow, childWindow, (short) (this.xServer.getScreenInfo().widthInPixels / 2), (short) (this.xServer.getScreenInfo().heightInPixels / 2) ,(short) convertRootCoordsToWindow.x, (short) convertRootCoordsToWindow.y, keyButMask)
//                        : new MotionNotify(false, (int) System.currentTimeMillis(), rootWindow, eventWindow, childWindow, (short) rootX, (short) rootY, (short) convertRootCoordsToWindow.x, (short) convertRootCoordsToWindow.y, keyButMask);

        Log.d(TAG, String.format("pointerMoved: 新建motionnotify, rootX=%d, rootY=%d, eventX=%d, eventY=%d, mask=%s", motionNotify.getRootX(), motionNotify.getRootY(), motionNotify.getEventX(), motionNotify.getEventY(), motionNotify.getState()));


        if (pointerGrabWindow != null) {
            if (grabsManager.getPointerGrabOwnerEvents() && ancestorWithDeviceEventMask != null) {
                ancestorWithDeviceEventMask.getEventListenersList().sendEventForEventMaskToClient(motionNotify, createCurrentEventMask, pointerGrabListener.getClient());
                return;
            } else if (pointerGrabListener.isInterestedIn(createCurrentEventMask)) {
                pointerGrabListener.onEvent(pointerGrabWindow, motionNotify);
                return;
            } else {
                return;
            }
        }
        Log.d(TAG, "pointerMoved: 将motionotify送给XClientWindowListener");
        ancestorWithDeviceEventMask.getEventListenersList().sendEventForEventMask(motionNotify, createCurrentEventMask);
    }

    @Override // com.eltechs.axs.xserver.PointerListener
    public void pointerButtonPressed(int i) {
        Window pointerGrabWindow = this.xServer.getGrabsManager().getPointerGrabWindow();
        if (pointerGrabWindow == null && (pointerGrabWindow = WindowHelpers.getAncestorWithDeviceEventName(this.pointWindow, EventName.BUTTON_PRESS)) != null) {
            this.xServer.getGrabsManager().initiateAutomaticPointerGrab(pointerGrabWindow);
        }
        if (pointerGrabWindow != null) {
            Mask<KeyButNames> keyButMask = EventHelpers.getKeyButMask(this.xServer);
            keyButMask.clear(KeyButNames.getFlagForButtonNumber(i));
            int x = this.xServer.getPointer().getX();
            int y = this.xServer.getPointer().getY();
            Point convertRootCoordsToWindow = WindowHelpers.convertRootCoordsToWindow(pointerGrabWindow, x, y);
            Window rootWindow = this.xServer.getWindowsManager().getRootWindow();
            Window window = null;
            if (this.pointWindow != pointerGrabWindow && WindowHelpers.isAncestorOf(this.pointWindow, pointerGrabWindow)) {
                window = WindowHelpers.getDirectChild(this.pointWindow, pointerGrabWindow);
            }
            Log.d(TAG, "pointerButtonPressed: 发送了ButtonPress事件");
            pointerGrabWindow.getEventListenersList().sendEventForEventName(new ButtonPress((byte) i, (int) System.currentTimeMillis(), rootWindow, pointerGrabWindow, window, (short) x, (short) y, (short) convertRootCoordsToWindow.x, (short) convertRootCoordsToWindow.y, keyButMask), EventName.BUTTON_PRESS);
        }
    }

    @Override // com.eltechs.axs.xserver.PointerListener
    public void pointerButtonReleased(int i) {
        GrabsManager grabsManager = this.xServer.getGrabsManager();
        Window pointerGrabWindow = grabsManager.getPointerGrabWindow();
        KeyButNames flagForButtonNumber = KeyButNames.getFlagForButtonNumber(i);
        Mask<KeyButNames> keyButMask = EventHelpers.getKeyButMask(this.xServer);
        if (KeyButNames.isModifierReal(flagForButtonNumber)) {
            keyButMask.set(flagForButtonNumber);
        }
        Window window = null;
        Window ancestorWithDeviceEventName = (pointerGrabWindow == null || grabsManager.getPointerGrabOwnerEvents()) ? WindowHelpers.getAncestorWithDeviceEventName(this.pointWindow, EventName.BUTTON_RELEASE) : null;
        if (pointerGrabWindow != null || ancestorWithDeviceEventName != null) {
            Window window2 = ancestorWithDeviceEventName != null ? ancestorWithDeviceEventName : pointerGrabWindow;
            int x = this.xServer.getPointer().getX();
            int y = this.xServer.getPointer().getY();
            Point convertRootCoordsToWindow = WindowHelpers.convertRootCoordsToWindow(window2, x, y);
            Window rootWindow = this.xServer.getWindowsManager().getRootWindow();
            if (this.pointWindow != window2 && WindowHelpers.isAncestorOf(this.pointWindow, window2)) {
                window = WindowHelpers.getDirectChild(this.pointWindow, window2);
            }
            sendEventForEventMask(new ButtonRelease((byte) i, (int) System.currentTimeMillis(), rootWindow, window2, window, (short) x, (short) y, (short) convertRootCoordsToWindow.x, (short) convertRootCoordsToWindow.y, keyButMask), EventName.BUTTON_RELEASE, ancestorWithDeviceEventName);
        }
        if (this.xServer.getPointer().getButtonMask().isEmpty()) {
            grabsManager.disableAutomaticOrPassiveGrab();
        }
    }

    @Override // com.eltechs.axs.xserver.PointerListener
    public void pointerWarped(int i, int i2) {
        pointerMoved(i, i2);
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowCreated(Window window) {
        updatePointWindow();
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowMapped(Window window) {
        updatePointWindow();
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowUnmapped(Window window) {
        updatePointWindow();
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowReparented(Window window, Window window2) {
        updatePointWindow();
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowZOrderChange(Window window) {
        updatePointWindow();
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowDestroyed(Window window) {
        updatePointWindow();
    }

    @Override // com.eltechs.axs.xserver.WindowChangeListener
    public void geometryChanged(Window window) {
        updatePointWindow();
    }
}