package com.eltechs.axs.xserver;

import android.util.Log;

import com.eltechs.axs.geom.Point;
import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.helpers.WindowHelpers;

import java.util.ArrayList;

/* loaded from: classes.dex */
public class ViewFacade {
    final XServer xServer;

    /* loaded from: classes.dex */
    public interface DrawableHandler {
        void handle(PlacedDrawable placedDrawable);
    }

    public ViewFacade(XServer xServer) {
        this.xServer = xServer;
    }

    public XServer getXServer() {
        return this.xServer;
    }

    public void injectKeyPress(byte b) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getEventsInjector().injectKeyPress(b, 0);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void injectKeyPressWithSym(byte b, int i) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getEventsInjector().injectKeyPress(b, i);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void injectKeyRelease(byte b) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getEventsInjector().injectKeyRelease(b, 0);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void injectKeyReleaseWithSym(byte b, int i) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getEventsInjector().injectKeyRelease(b, i);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void injectKeyType(byte b) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getEventsInjector().injectKeyPress(b, 0);
            this.xServer.getEventsInjector().injectKeyRelease(b, 0);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void injectKeyTypeWithSym(byte b, int i) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getEventsInjector().injectKeyPress(b, i);
            this.xServer.getEventsInjector().injectKeyRelease(b, i);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void injectMultiKeyPress(byte[] bArr) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            for (byte b : bArr) {
                this.xServer.getEventsInjector().injectKeyPress(b, 0);
            }
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void injectMultiKeyPressWithSym(byte[] bArr, byte[] bArr2) {
        Assert.isTrue(bArr.length == bArr2.length);
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        for (int i = 0; i < bArr.length; i++) {
            try {
                this.xServer.getEventsInjector().injectKeyPress(bArr[i], bArr2[i]);
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    if (lockForInputDevicesManipulation != null) {
                        try {
                            lockForInputDevicesManipulation.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    }
                    throw th2;
                }
            }
        }
        if (lockForInputDevicesManipulation != null) {
            lockForInputDevicesManipulation.close();
        }
    }

    public void injectMultiKeyRelease(byte[] bArr) {
        try {
            Thread.sleep(50L);
        } catch (InterruptedException ignored) {
        }
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            for (byte b : bArr) {
                this.xServer.getEventsInjector().injectKeyRelease(b, 0);
            }
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void injectMultiKeyReleaseWithSym(byte[] bArr, int[] iArr) {
        Assert.isTrue(bArr.length == iArr.length);
        try {
            Thread.sleep(50L);
        } catch (InterruptedException ignored) {
        }
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        for (int i = 0; i < bArr.length; i++) {
            try {
                this.xServer.getEventsInjector().injectKeyRelease(bArr[i], iArr[i]);
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    if (lockForInputDevicesManipulation != null) {
                        try {
                            lockForInputDevicesManipulation.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    }
                    throw th2;
                }
            }
        }
        if (lockForInputDevicesManipulation != null) {
            lockForInputDevicesManipulation.close();
        }
    }

    public void injectMultiKeyType(byte[] bArr) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        Throwable th = null;
        try {
            for (byte b : bArr) {
                this.xServer.getEventsInjector().injectKeyPress(b, 0);
            }
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ignored) {
            }
            for (byte b2 : bArr) {
                this.xServer.getEventsInjector().injectKeyRelease(b2, 0);
            }
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th2) {
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
            throw th2;
        }
    }

    public void injectMultiKeyTypeWithSym(byte[] bArr, int[] iArr) {
        Assert.isTrue(bArr.length == iArr.length);
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        Throwable th = null;
        for (int i = 0; i < bArr.length; i++) {
            try {
                this.xServer.getEventsInjector().injectKeyPress(bArr[i], iArr[i]);
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    lockForInputDevicesManipulation.close();
                }
                throw th2;
            }
        }
        try {
            Thread.sleep(50L);
        } catch (InterruptedException ignored) {
        }
        for (int i2 = 0; i2 < bArr.length; i2++) {
            this.xServer.getEventsInjector().injectKeyRelease(bArr[i2], iArr[i2]);
        }
        if (lockForInputDevicesManipulation != null) {
            lockForInputDevicesManipulation.close();
        }
    }

    public void injectPointerMove(int x, int y) {
//        Log.d("ViewFacade", String.format("injectPointerMove: 移动坐标 (%d,%d)",x,y));
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getEventsInjector().injectPointerMove(x, y);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void injectPointerDelta(int i, int i2, int times) {
        Log.d("ViewFacade", String.format("injectPointerDelta: 偏移坐标 (%d,%d),次数 %d",i,i2,times));
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            EventsInjector eventsInjector = this.xServer.getEventsInjector();
            for (int i4 = 0; i4 < times; i4++) {
                eventsInjector.injectPointerMove(this.xServer.getPointer().getX() + i, this.xServer.getPointer().getY() + i2);
            }
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void injectPointerDelta(int i, int i2) {
        injectPointerDelta(i, i2, 1);
    }

    public void injectPointerButtonPress(int i) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getEventsInjector().injectPointerButtonPress(i);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void injectPointerWheelUp(int i) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        while (true) {
            int i2 = i - 1;
            if (i <= 0) {
                break;
            }
            try {
                this.xServer.getEventsInjector().injectPointerButtonPress(4);
                this.xServer.getEventsInjector().injectPointerButtonRelease(4);
                i = i2;
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    if (lockForInputDevicesManipulation != null) {
                        try {
                            lockForInputDevicesManipulation.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    }
                    throw th2;
                }
            }
        }
        if (lockForInputDevicesManipulation != null) {
            lockForInputDevicesManipulation.close();
        }
    }

    public void injectPointerWheelDown(int i) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        while (true) {
            int i2 = i - 1;
            if (i <= 0) {
                break;
            }
            try {
                this.xServer.getEventsInjector().injectPointerButtonPress(5);
                this.xServer.getEventsInjector().injectPointerButtonRelease(5);
                i = i2;
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    if (lockForInputDevicesManipulation != null) {
                        try {
                            lockForInputDevicesManipulation.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    }
                    throw th2;
                }
            }
        }
        if (lockForInputDevicesManipulation != null) {
            lockForInputDevicesManipulation.close();
        }
    }

    public void injectPointerButtonRelease(int i) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getEventsInjector().injectPointerButtonRelease(i);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void addPointerListener(PointerListener pointerListener) {
        LocksManager.XLock lock = this.xServer.getLocksManager().lock(LocksManager.Subsystem.INPUT_DEVICES);
        try {
            this.xServer.getPointer().addListener(pointerListener);
            if (lock != null) {
                lock.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void removePointerListener(PointerListener pointerListener) {
        LocksManager.XLock lock = this.xServer.getLocksManager().lock(LocksManager.Subsystem.INPUT_DEVICES);
        try {
            this.xServer.getPointer().removeListener(pointerListener);
            if (lock != null) {
                lock.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void addWindowLifecycleListener(WindowLifecycleListener windowLifecycleListener) {
        LocksManager.XLock lock = this.xServer.getLocksManager().lock(LocksManager.Subsystem.WINDOWS_MANAGER);
        try {
            this.xServer.getWindowsManager().addWindowLifecycleListener(windowLifecycleListener);
            if (lock != null) {
                lock.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void removeWindowLifecycleListener(WindowLifecycleListener windowLifecycleListener) {
        LocksManager.XLock lock = this.xServer.getLocksManager().lock(LocksManager.Subsystem.WINDOWS_MANAGER);
        try {
            this.xServer.getWindowsManager().removeWindowLifecycleListener(windowLifecycleListener);
            if (lock != null) {
                lock.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void addWindowContentModificationListner(WindowContentModificationListener windowContentModificationListener) {
        LocksManager.XLock lock = this.xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.DRAWABLES_MANAGER});
        try {
            this.xServer.getWindowsManager().addWindowContentModificationListner(windowContentModificationListener);
            if (lock != null) {
                lock.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void removeWindowContentModificationListner(WindowContentModificationListener windowContentModificationListener) {
        LocksManager.XLock lock = this.xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.DRAWABLES_MANAGER});
        try {
            this.xServer.getWindowsManager().removeWindowContentModificationListner(windowContentModificationListener);
            if (lock != null) {
                lock.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void addWindowChangeListener(WindowChangeListener windowChangeListener) {
        LocksManager.XLock lock = this.xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER});
        try {
            this.xServer.getWindowsManager().addWindowChangeListener(windowChangeListener);
            if (lock != null) {
                lock.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void removeWindowChangeListener(WindowChangeListener windowChangeListener) {
        LocksManager.XLock lock = this.xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER});
        try {
            this.xServer.getWindowsManager().removeWindowChangeListener(windowChangeListener);
            if (lock != null) {
                lock.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void addCursorLifecycleListener(CursorLifecycleListener cursorLifecycleListener) {
        this.xServer.getCursorsManager().addCursorLifecycleListener(cursorLifecycleListener);
    }

    public void removeCursorLifecycleListener(CursorLifecycleListener cursorLifecycleListener) {
        this.xServer.getCursorsManager().removeCursorLifecycleListener(cursorLifecycleListener);
    }

    public void walkDrawables(DrawableHandler drawableHandler) {
        LocksManager.XLock lock = this.xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.DRAWABLES_MANAGER, LocksManager.Subsystem.CURSORS_MANAGER, LocksManager.Subsystem.INPUT_DEVICES});
        try {
            walkWindow(this.xServer.getWindowsManager().getRootWindow(), 0, 0, drawableHandler);
            if (lock != null) {
                lock.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public ArrayList<PlacedDrawable> listNonRootWindowDrawables() {
        final ArrayList<PlacedDrawable> arrayList = new ArrayList<>();
        LocksManager.XLock lock = this.xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.DRAWABLES_MANAGER});
        try {
            final Window rootWindow = this.xServer.getWindowsManager().getRootWindow();
            walkWindow(this.xServer.getWindowsManager().getRootWindow(), 0, 0, new DrawableHandler() { // from class: com.eltechs.axs.xserver.ViewFacade.1
                @Override // com.eltechs.axs.xserver.ViewFacade.DrawableHandler
                public void handle(PlacedDrawable placedDrawable) {
                    if (placedDrawable.getDrawable() != rootWindow.getActiveBackingStore()) {
                        arrayList.add(placedDrawable);
                    }
                }
            });
            if (lock != null) {
                lock.close();
            }
            return arrayList;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public PlacedDrawable getCursorDrawable() {
        final ArrayList<PlacedDrawable> arrayList = new ArrayList<>();
        LocksManager.XLock lock = this.xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.DRAWABLES_MANAGER, LocksManager.Subsystem.INPUT_DEVICES, LocksManager.Subsystem.CURSORS_MANAGER});
        try {
            walkCursor(new DrawableHandler() { // from class: com.eltechs.axs.xserver.ViewFacade.2
                @Override // com.eltechs.axs.xserver.ViewFacade.DrawableHandler
                public void handle(PlacedDrawable placedDrawable) {
                    arrayList.add(placedDrawable);
                }
            });
            PlacedDrawable placedDrawable = arrayList.size() > 0 ? (PlacedDrawable) arrayList.get(0) : null;
            if (lock != null) {
                lock.close();
            }
            return placedDrawable;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    private void walkWindow(Window window, int i, int i2, DrawableHandler drawableHandler) {
        if (window.getWindowAttributes().isMapped()) {
            Drawable activeBackingStore = window.getActiveBackingStore();
            drawableHandler.handle(new PlacedDrawable(activeBackingStore, new Rectangle(i, i2, activeBackingStore.getWidth(), activeBackingStore.getHeight())));
            for (Window window2 : window.getChildrenBottomToTop()) {
                walkWindow(window2, window2.getBoundingRectangle().x + i, window2.getBoundingRectangle().y + i2, drawableHandler);
            }
        }
    }

    private void walkCursor(DrawableHandler drawableHandler) {
        Cursor cursor;
        Window leafMappedSubWindowByCoords = WindowHelpers.getLeafMappedSubWindowByCoords(this.xServer.getWindowsManager().getRootWindow(), this.xServer.getPointer().getX(), this.xServer.getPointer().getY());
        if (leafMappedSubWindowByCoords == null || (cursor = leafMappedSubWindowByCoords.getWindowAttributes().getCursor()) == null) {
            return;
        }
        drawableHandler.handle(new PlacedDrawable(cursor.getCursorImage(), new Rectangle(this.xServer.getPointer().getX() - cursor.getHotSpotX(), this.xServer.getPointer().getY() - cursor.getHotSpotY(), cursor.getCursorImage().getWidth(), cursor.getCursorImage().getHeight())));
    }

    public ScreenInfo getScreenInfo() {
        return this.xServer.getScreenInfo();
    }

    public Point getPointerLocation() {
        LocksManager.XLock lock = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            Pointer pointer = this.xServer.getPointer();
            Point point = new Point(pointer.getX(), pointer.getY());
            if (lock != null) {
                lock.close();
            }
            return point;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void addKeyboardListener(KeyboardListener keyboardListener) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getKeyboard().addKeyListener(keyboardListener);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void removeKeyboardListener(KeyboardListener keyboardListener) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getKeyboard().removeKeyListener(keyboardListener);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void addKeyboardModifiersChangeListener(KeyboardModifiersListener keyboardModifiersListener) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getKeyboard().addModifierListener(keyboardModifiersListener);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void removeKeyboardModifiersChangeListener(KeyboardModifiersListener keyboardModifiersListener) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getKeyboard().removeModifierListener(keyboardModifiersListener);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void setModifierState(KeyButNames keyButNames, boolean z, byte b, boolean z2) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            if (this.xServer.getKeyboard().getModifiersMask().isSet(keyButNames) == z) {
                if (lockForInputDevicesManipulation != null) {
                    lockForInputDevicesManipulation.close();
                    return;
                }
                return;
            }
            if (this.xServer.getKeyboard().getModifiersMask().isSet(keyButNames)) {
                if (z2) {
                    this.xServer.getKeyboard().keyReleased(b, 0);
                } else {
                    this.xServer.getKeyboard().keyPressed(b, 0);
                    this.xServer.getKeyboard().keyReleased(b, 0);
                }
            } else if (z2) {
                this.xServer.getKeyboard().keyPressed(b, 0);
            } else {
                this.xServer.getKeyboard().keyPressed(b, 0);
                this.xServer.getKeyboard().keyReleased(b, 0);
            }
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public void switchModifierState(KeyButNames keyButNames, byte b, boolean z) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            setModifierState(keyButNames, !this.xServer.getKeyboard().getModifiersMask().isSet(keyButNames), b, z);
            if (lockForInputDevicesManipulation != null) {
                lockForInputDevicesManipulation.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockForInputDevicesManipulation != null) {
                    try {
                        lockForInputDevicesManipulation.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }
}