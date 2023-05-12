package com.eltechs.axs.xserver.helpers;

import com.eltechs.axs.geom.Point;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.Predicate;
import com.eltechs.axs.xserver.EventName;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public abstract class WindowHelpers {

    /* loaded from: classes.dex */
    public enum MapState {
        UNMAPPED,
        UNVIEWABLE,
        VIEWABLE
    }

    private WindowHelpers() {
    }

    public static MapState getWindowMapState(Window window) {
        if (!window.getWindowAttributes().isMapped()) {
            return MapState.UNMAPPED;
        }
        do {
            window = window.getParent();
            if (window == null) {
                return MapState.VIEWABLE;
            }
        } while (window.getWindowAttributes().isMapped());
        return MapState.UNVIEWABLE;
    }

    public static Window getRootWindowOf(Window window) {
        while (true) {
            Window parent = window.getParent();
            if (parent == null) {
                return window;
            }
            window = parent;
        }
    }

    public static Window getLeafSubWindowByCondition(Window window, Predicate<Window> predicate) {
        if (predicate.apply(window)) {
            Window directSubWindowByCondition = getDirectSubWindowByCondition(window, predicate);
            return directSubWindowByCondition != null ? getLeafSubWindowByCondition(directSubWindowByCondition, predicate) : window;
        }
        return null;
    }

    public static Window getDirectSubWindowByCondition(Window window, Predicate<Window> predicate) {
        for (Window window2 : window.getChildrenTopToBottom()) {
            if (predicate.apply(window2)) {
                return window2;
            }
        }
        return null;
    }

    public static Window getAncestorWindowByCondition(Window window, Predicate<Window> predicate) {
        while (window != null) {
            if (predicate.apply(window)) {
                return window;
            }
            window = window.getParent();
        }
        return null;
    }

    public static Window getAncestorWindowByConditionWithBreak(Window window, Predicate<Window> predicate, Predicate<Window> predicate2) {
        while (window != null) {
            if (predicate.apply(window)) {
                return window;
            }
            if (predicate2.apply(window)) {
                return null;
            }
            window = window.getParent();
        }
        return null;
    }

    public static Window getDirectChild(Window window, final Window window2) {
        if (window2 == window || !isAncestorOf(window, window2)) {
            return null;
        }
        return getAncestorWindowByCondition(window, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.1
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window3) {
                return window2 == window3.getParent();
            }
        });
    }

    public static Window getLeastCommonAncestor(Window window, final Window window2) {
        return getAncestorWindowByCondition(window, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.2
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window3) {
                return WindowHelpers.isAncestorOf(window2, window3);
            }
        });
    }

    public static boolean isAncestorOf(Window window, final Window window2) {
        return getAncestorWindowByCondition(window, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.3
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window3) {
                return window2 == window3;
            }
        }) != null;
    }

    public static Window getAncestorWithDeviceEventName(Window window, final EventName eventName) {
        return getAncestorWindowByConditionWithBreak(window, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.4
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window2) {
                return window2.getEventListenersList().isListenerInstalledForEvent(eventName);
            }
        }, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.5
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window2) {
                return window2.getWindowAttributes().getDoNotPropagateMask().isSet(eventName);
            }
        });
    }

    public static Window getAncestorWithDeviceEventMask(Window window, final Mask<EventName> mask) {
        return getAncestorWindowByConditionWithBreak(window, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.6
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window2) {
                return window2.getEventListenersList().isListenerInstalledForEventMask(mask);
            }
        }, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.7
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window2) {
                return window2.getWindowAttributes().getDoNotPropagateMask().intersects(mask);
            }
        });
    }

    public static Window getAncestorWithDeviceEventNameInSubtree(Window window, final EventName eventName, final Window window2) {
        Assert.isTrue(isAncestorOf(window, window2));
        return getAncestorWindowByConditionWithBreak(window, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.8
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window3) {
                return window3.getEventListenersList().isListenerInstalledForEvent(eventName);
            }
        }, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.9
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window3) {
                return window3 == window2 || window3.getWindowAttributes().getDoNotPropagateMask().isSet(eventName);
            }
        });
    }

    public static Window getLeafMappedSubWindowByCoords(Window window, final int x, final int y) {
        return getLeafSubWindowByCondition(window, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.10
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window2) {
                return window2.getWindowAttributes().isMapped() && window2.getBoundingRectangle().containsInnerPoint(WindowHelpers.convertRootCoordsToWindow(window2, x, y));
            }
        });
    }

    public static Window getDirectMappedSubWindowByCoords(Window window, final int i, final int i2) {
        return getDirectSubWindowByCondition(window, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.11
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window2) {
                return window2.getWindowAttributes().isMapped() && window2.getBoundingRectangle().containsInnerPoint(WindowHelpers.convertRootCoordsToWindow(window2, i, i2));
            }
        });
    }

    public static Point convertRootCoordsToWindow(Window window, int i, int i2) {
        while (window != null) {
            i -= window.getBoundingRectangle().x;
            i2 -= window.getBoundingRectangle().y;
            window = window.getParent();
        }
        return new Point(i, i2);
    }

    public static Point convertWindowCoordsToRoot(Window window, int i, int i2) {
        while (window != null) {
            i += window.getBoundingRectangle().x;
            i2 += window.getBoundingRectangle().y;
            window = window.getParent();
        }
        return new Point(i, i2);
    }

    public static Window calculatePointWindow(XServer xServer) {
        return getLeafMappedSubWindowByCoords(xServer.getWindowsManager().getRootWindow(), xServer.getPointer().getX(), xServer.getPointer().getY());
    }
}