package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.xserver.Cursor;
import com.eltechs.axs.xserver.CursorLifecycleListener;
import com.eltechs.axs.xserver.CursorsManager;
import com.eltechs.axs.xserver.Pixmap;
import com.eltechs.axs.xserver.impl.drawables.DrawablesFactory;

public class CursorsManagerImpl implements CursorsManager {
    public CursorsManagerImpl(DrawablesFactory drawablesFactory) {
    }

    @Override
    public void addCursorLifecycleListener(CursorLifecycleListener cursorLifecycleListener) {

    }

    @Override
    public Cursor createCursor(int i, int i2, int i3, Pixmap pixmap, Pixmap pixmap2) {
        return null;
    }

    @Override
    public Cursor createFakeCursor(int i) {
        return null;
    }

    @Override
    public void freeCursor(Cursor cursor) {

    }

    @Override
    public Cursor getCursor(int i) {
        return null;
    }

    @Override
    public void recolorCursor(Cursor cursor, int i, int i2, int i3, int i4, int i5, int i6) {

    }

    @Override
    public void removeCursorLifecycleListener(CursorLifecycleListener cursorLifecycleListener) {

    }
}
