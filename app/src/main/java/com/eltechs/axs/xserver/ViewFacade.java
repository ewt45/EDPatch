package com.eltechs.axs.xserver;


import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.xserver.helpers.WindowHelpers;

public class ViewFacade {


    final XServer xServer;
    public interface DrawableHandler {
        void handle(PlacedDrawable placedDrawable);
    }

    public ViewFacade(XServer xServer) {
        this.xServer = xServer;
    }

    public void injectKeyTypeWithSym(byte b, int i) {
        LocksManager.XLock lockForInputDevicesManipulation = this.xServer.getLocksManager().lockForInputDevicesManipulation();
        try {
            this.xServer.getEventsInjector().injectKeyPress(b, i);
            this.xServer.getEventsInjector().injectKeyRelease(b, i);
            if (lockForInputDevicesManipulation == null) {
                return;
            }
            lockForInputDevicesManipulation.close();
        } catch (Throwable th) {
            if (lockForInputDevicesManipulation != null) {
                try {
                    lockForInputDevicesManipulation.close();
                } catch (Throwable th3) {
                    th.addSuppressed(th3);
                }
            }
            throw th;
        }
    }

    public ScreenInfo getScreenInfo() {
        return this.xServer.getScreenInfo();
    }


    private void walkCursor(DrawableHandler drawableHandler) {
        Cursor cursor;
        Window leafMappedSubWindowByCoords = WindowHelpers.getLeafMappedSubWindowByCoords(this.xServer.getWindowsManager().getRootWindow(), this.xServer.getPointer().getX(), this.xServer.getPointer().getY());
        if (leafMappedSubWindowByCoords == null || (cursor = leafMappedSubWindowByCoords.getWindowAttributes().getCursor()) == null) {
            return;
        }
        drawableHandler.handle(new PlacedDrawable(cursor.getCursorImage(), new Rectangle(this.xServer.getPointer().getX() - cursor.getHotSpotX(), this.xServer.getPointer().getY() - cursor.getHotSpotY(), cursor.getCursorImage().getWidth(), cursor.getCursorImage().getHeight())));
    }

}
