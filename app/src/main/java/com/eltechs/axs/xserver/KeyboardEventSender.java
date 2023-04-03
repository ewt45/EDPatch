package com.eltechs.axs.xserver;

import com.eltechs.axs.geom.Point;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.events.Event;
import com.eltechs.axs.xserver.events.KeyPress;
import com.eltechs.axs.xserver.events.KeyRelease;
import com.eltechs.axs.xserver.events.MappingNotify;
import com.eltechs.axs.xserver.helpers.EventHelpers;
import com.eltechs.axs.xserver.helpers.WindowHelpers;
import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public class KeyboardEventSender implements KeyboardListener {
    private final XServer xServer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public KeyboardEventSender(XServer xServer) {
        this.xServer = xServer;
        this.xServer.getKeyboard().addKeyListener(this);
    }

    private void sendKeyEvent(EventName eventName, byte b, int i, Mask<KeyButNames> mask) {
        Window window;
        Window window2;
        Window window3;
        Event keyPress;
        Window focusedWindow = this.xServer.getFocusManager().getFocusedWindow();
        if (focusedWindow == null) {
            return;
        }
        Window calculatePointWindow = WindowHelpers.calculatePointWindow(this.xServer);
        Event event = null;
        if (WindowHelpers.isAncestorOf(calculatePointWindow, focusedWindow)) {
            window = WindowHelpers.getAncestorWithDeviceEventNameInSubtree(calculatePointWindow, eventName, focusedWindow);
            window2 = WindowHelpers.getDirectChild(calculatePointWindow, window);
        } else {
            window = null;
            window2 = null;
        }
        if (window != null) {
            window3 = window;
        } else if (!focusedWindow.getEventListenersList().isListenerInstalledForEvent(eventName)) {
            return;
        } else {
            window3 = focusedWindow;
        }
        Pointer pointer = this.xServer.getPointer();
        Point convertRootCoordsToWindow = WindowHelpers.convertRootCoordsToWindow(window3, pointer.getX(), pointer.getY());
        Mask<KeyButNames> keyButMask = EventHelpers.getKeyButMask(this.xServer, mask);
        switch (eventName) {
            case KEY_PRESS:
                keyPress = new KeyPress(b, (int) System.currentTimeMillis(), WindowHelpers.getRootWindowOf(window3), window3, window2, (short) pointer.getX(), (short) pointer.getY(), (short) convertRootCoordsToWindow.x, (short) convertRootCoordsToWindow.y, keyButMask);
                event = keyPress;
                break;
            case KEY_RELEASE:
                keyPress = new KeyRelease(b, (int) System.currentTimeMillis(), WindowHelpers.getRootWindowOf(window3), window3, window2, (short) pointer.getX(), (short) pointer.getY(), (short) convertRootCoordsToWindow.x, (short) convertRootCoordsToWindow.y, keyButMask);
                event = keyPress;
                break;
            default:
                Assert.unreachable();
                break;
        }
        if (i != 0 && eventName == EventName.KEY_PRESS) {
            KeyboardModel keyboardModel = this.xServer.getKeyboardModelManager().getKeyboardModel();
            int[] iArr = new int[2];
            keyboardModel.getKeysymsForKeycodeGroup1(b, iArr);
            if (iArr[0] != i || iArr[1] != i) {
                keyboardModel.setKeysymsForKeycodeGroup1(b, i, i);
                window3.getEventListenersList().sendEvent(new MappingNotify(MappingNotify.Request.KEYBOARD, b, 1));
            }
        }
        window3.getEventListenersList().sendEventForEventName(event, eventName);
    }

    @Override // com.eltechs.axs.xserver.KeyboardListener
    public void keyPressed(byte b, int i, Mask<KeyButNames> mask) {
        sendKeyEvent(EventName.KEY_PRESS, b, i, mask);
    }

    @Override // com.eltechs.axs.xserver.KeyboardListener
    public void keyReleased(byte b, int i, Mask<KeyButNames> mask) {
        sendKeyEvent(EventName.KEY_RELEASE, b, i, mask);
    }
}
