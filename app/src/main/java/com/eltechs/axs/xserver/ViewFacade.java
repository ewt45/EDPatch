package com.eltechs.axs.xserver;

import android.util.Log;

import com.eltechs.axs.geom.Point;
import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.helpers.WindowHelpers;
import com.termux.x11.ViewForRendering;
import com.termux.x11.input.InputStub;

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

    public void injectKeyPress(byte keycode) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            xServer.getEventsInjector().injectKeyPress(keycode, 0);
        }
    }

    public void injectKeyPressWithSym(byte keycode, int keySym) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            xServer.getEventsInjector().injectKeyPress(keycode, keySym);
        }
    }

    public void injectKeyRelease(byte keycode) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            xServer.getEventsInjector().injectKeyRelease(keycode, 0);
        }
    }

    public void injectKeyReleaseWithSym(byte keycode, int keySym) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            xServer.getEventsInjector().injectKeyRelease(keycode, keySym);
        }
    }

    public void injectKeyType(byte keycode) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            xServer.getEventsInjector().injectKeyPress(keycode, 0);
            xServer.getEventsInjector().injectKeyRelease(keycode, 0);
        }
    }

    public void injectKeyTypeWithSym(byte keycode, int keySym) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            xServer.getEventsInjector().injectKeyPress(keycode, keySym);
            xServer.getEventsInjector().injectKeyRelease(keycode, keySym);
        }
    }

    public void injectMultiKeyPress(byte[] keycodes) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            for (byte keycode : keycodes)
                this.xServer.getEventsInjector().injectKeyPress(keycode, 0);
        }
    }

    public void injectMultiKeyPressWithSym(byte[] keycodes, byte[] keySyms) {
        Assert.isTrue(keycodes.length == keySyms.length);
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            for (int i = 0; i < keycodes.length; i++)
                xServer.getEventsInjector().injectKeyPress(keycodes[i], keySyms[i]);
        }
    }

    public void injectMultiKeyRelease(byte[] keycodes) {
        try {
            Thread.sleep(50L);
        } catch (InterruptedException ignored) {
        }
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            for (byte keycode : keycodes)
                this.xServer.getEventsInjector().injectKeyRelease(keycode, 0);
        }
    }

    public void injectMultiKeyReleaseWithSym(byte[] keycodes, int[] keySyms) {
        Assert.isTrue(keycodes.length == keySyms.length);
        try {
            Thread.sleep(50L);
        } catch (InterruptedException ignored) {
        }
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            for (int i = 0; i < keycodes.length; i++)
                xServer.getEventsInjector().injectKeyRelease(keycodes[i], keySyms[i]);
        }
    }

    public void injectMultiKeyType(byte[] keycodes) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            for (byte keycode : keycodes) {
                this.xServer.getEventsInjector().injectKeyPress(keycode, 0);
            }
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ignored) {
            }
            for (byte keycode : keycodes) {
                this.xServer.getEventsInjector().injectKeyRelease(keycode, 0);
            }
        }
    }

    public void injectMultiKeyTypeWithSym(byte[] keycodes, int[] keySyms) {
        Assert.isTrue(keycodes.length == keySyms.length);
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            for (int i = 0; i < keycodes.length; i++)
                xServer.getEventsInjector().injectKeyPress(keycodes[i], keySyms[i]);
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ignored) {
            }
            for (int i = 0; i < keycodes.length; i++) {
                this.xServer.getEventsInjector().injectKeyRelease(keycodes[i], keySyms[i]);
            }
        }
    }

    public void injectPointerMove(int x, int y) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            xServer.getEventsInjector().injectPointerMove(x, y);
        }
    }

    public void injectPointerDelta(int dx, int dy, int times) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            EventsInjector eventsInjector = xServer.getEventsInjector();
            for (int i = 0; i < times; i++)
                ViewForRendering.mouseEvent(dx,dy, InputStub.BUTTON_UNDEFINED,false,true);
        }
    }

    public void injectPointerDelta(int dx, int dy) {
        injectPointerDelta(dx, dy, 1);
    }

    public void injectPointerButtonPress(int btnCode) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            xServer.getEventsInjector().injectPointerButtonPress(btnCode);
        }
    }

    public void injectPointerWheelUp(int times) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            for(int i=0; i<times; i++){
                this.xServer.getEventsInjector().injectPointerButtonPress(Pointer.BUTTON_SCROLL_UP);
                this.xServer.getEventsInjector().injectPointerButtonRelease(Pointer.BUTTON_SCROLL_UP);
            }
        }
    }

    public void injectPointerWheelDown(int times) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            for(int i=0; i<times; i++){
                this.xServer.getEventsInjector().injectPointerButtonPress(Pointer.BUTTON_SCROLL_DOWN);
                this.xServer.getEventsInjector().injectPointerButtonRelease(Pointer.BUTTON_SCROLL_DOWN);
            }
        }
    }

    public void injectPointerButtonRelease(int btnCode) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            xServer.getEventsInjector().injectPointerButtonRelease(btnCode);
        }
    }

    public void addPointerListener(PointerListener pointerListener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lock(LocksManager.Subsystem.INPUT_DEVICES)){
            xServer.getPointer().addListener(pointerListener);
        }
    }

    public void removePointerListener(PointerListener pointerListener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lock(LocksManager.Subsystem.INPUT_DEVICES)){
            xServer.getPointer().removeListener(pointerListener);
        }
    }

    public void addWindowLifecycleListener(WindowLifecycleListener windowLifecycleListener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lock(LocksManager.Subsystem.WINDOWS_MANAGER)){
            xServer.getWindowsManager().addWindowLifecycleListener(windowLifecycleListener);
        }
    }

    public void removeWindowLifecycleListener(WindowLifecycleListener windowLifecycleListener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lock(LocksManager.Subsystem.WINDOWS_MANAGER)){
            xServer.getWindowsManager().removeWindowLifecycleListener(windowLifecycleListener);
        }
    }

    public void addWindowContentModificationListner(WindowContentModificationListener listener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.DRAWABLES_MANAGER})){
            xServer.getWindowsManager().addWindowContentModificationListner(listener);
        }
    }

    public void removeWindowContentModificationListner(WindowContentModificationListener listener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.DRAWABLES_MANAGER})){
            xServer.getWindowsManager().removeWindowContentModificationListner(listener);
        }
    }

    public void addWindowChangeListener(WindowChangeListener windowChangeListener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER})){
            xServer.getWindowsManager().addWindowChangeListener(windowChangeListener);
        }
    }

    public void removeWindowChangeListener(WindowChangeListener windowChangeListener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER})){
            xServer.getWindowsManager().removeWindowChangeListener(windowChangeListener);
        }
    }

    public void addCursorLifecycleListener(CursorLifecycleListener cursorLifecycleListener) {
        this.xServer.getCursorsManager().addCursorLifecycleListener(cursorLifecycleListener);
    }

    public void removeCursorLifecycleListener(CursorLifecycleListener cursorLifecycleListener) {
        this.xServer.getCursorsManager().removeCursorLifecycleListener(cursorLifecycleListener);
    }

    public void walkDrawables(DrawableHandler drawableHandler) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.DRAWABLES_MANAGER, LocksManager.Subsystem.CURSORS_MANAGER, LocksManager.Subsystem.INPUT_DEVICES})){
            walkWindow(this.xServer.getWindowsManager().getRootWindow(), 0, 0, drawableHandler);
        }
    }

    public ArrayList<PlacedDrawable> listNonRootWindowDrawables() {
        final ArrayList<PlacedDrawable> arrayList = new ArrayList<>();
        try (LocksManager.XLock lock = xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.DRAWABLES_MANAGER})){
            final Window rootWindow = this.xServer.getWindowsManager().getRootWindow();
            walkWindow(this.xServer.getWindowsManager().getRootWindow(), 0, 0, placedDrawable -> {
                if (placedDrawable.getDrawable() != rootWindow.getActiveBackingStore()) {
                    arrayList.add(placedDrawable);
                }
            });
            return arrayList;
        }
    }

    public PlacedDrawable getCursorDrawable() {
        final ArrayList<PlacedDrawable> arrayList = new ArrayList<>();
        try (LocksManager.XLock lock = xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.DRAWABLES_MANAGER, LocksManager.Subsystem.INPUT_DEVICES, LocksManager.Subsystem.CURSORS_MANAGER})){
            walkCursor(placedDrawable -> arrayList.add(placedDrawable));
            return !arrayList.isEmpty() ? arrayList.get(0) : null;
        }
    }

    private void walkWindow(Window window, int x, int y, DrawableHandler drawableHandler) {
        if (window.getWindowAttributes().isMapped()) {
            Drawable activeBackingStore = window.getActiveBackingStore();
            drawableHandler.handle(new PlacedDrawable(activeBackingStore, new Rectangle(x, y, activeBackingStore.getWidth(), activeBackingStore.getHeight())));
            for (Window child : window.getChildrenBottomToTop()) {
                walkWindow(child, child.getBoundingRectangle().x + x, child.getBoundingRectangle().y + y, drawableHandler);
            }
        }
    }

    private void walkCursor(DrawableHandler drawableHandler) {
        Cursor cursor;
        Window targetWindow = WindowHelpers.getLeafMappedSubWindowByCoords(xServer.getWindowsManager().getRootWindow(), xServer.getPointer().getX(), xServer.getPointer().getY());
        if (targetWindow == null || (cursor = targetWindow.getWindowAttributes().getCursor()) == null) {
            return;
        }
        drawableHandler.handle(new PlacedDrawable(cursor.getCursorImage(), new Rectangle(this.xServer.getPointer().getX() - cursor.getHotSpotX(), this.xServer.getPointer().getY() - cursor.getHotSpotY(), cursor.getCursorImage().getWidth(), cursor.getCursorImage().getHeight())));
    }

    public ScreenInfo getScreenInfo() {
        return this.xServer.getScreenInfo();
    }

    public Point getPointerLocation() {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            Pointer pointer = this.xServer.getPointer();
            return new Point(pointer.getX(), pointer.getY());
        }
    }

    public void addKeyboardListener(KeyboardListener keyboardListener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            this.xServer.getKeyboard().addKeyListener(keyboardListener);
        }
    }

    public void removeKeyboardListener(KeyboardListener keyboardListener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            this.xServer.getKeyboard().removeKeyListener(keyboardListener);
        }
    }

    public void addKeyboardModifiersChangeListener(KeyboardModifiersListener keyboardModifiersListener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            this.xServer.getKeyboard().addModifierListener(keyboardModifiersListener);
        }
    }

    public void removeKeyboardModifiersChangeListener(KeyboardModifiersListener keyboardModifiersListener) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            this.xServer.getKeyboard().removeModifierListener(keyboardModifiersListener);
        }
    }

    public void setModifierState(KeyButNames keyButNames, boolean z, byte keycode, boolean release) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            if (this.xServer.getKeyboard().getModifiersMask().isSet(keyButNames) == z) {
                return;
            }
            if (this.xServer.getKeyboard().getModifiersMask().isSet(keyButNames)) {
                if (release) {
                    this.xServer.getKeyboard().keyReleased(keycode, 0);
                } else {
                    this.xServer.getKeyboard().keyPressed(keycode, 0);
                    this.xServer.getKeyboard().keyReleased(keycode, 0);
                }
            } else if (release) {
                this.xServer.getKeyboard().keyPressed(keycode, 0);
            } else {
                this.xServer.getKeyboard().keyPressed(keycode, 0);
                this.xServer.getKeyboard().keyReleased(keycode, 0);
            }
        }

    }

    public void switchModifierState(KeyButNames keyButNames, byte keycode, boolean release) {
        try (LocksManager.XLock lock = xServer.getLocksManager().lockForInputDevicesManipulation()){
            setModifierState(keyButNames, !this.xServer.getKeyboard().getModifiersMask().isSet(keyButNames), keycode, release);
        }
    }
}