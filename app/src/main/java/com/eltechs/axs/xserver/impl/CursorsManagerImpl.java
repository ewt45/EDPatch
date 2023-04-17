package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.xserver.Cursor;
import com.eltechs.axs.xserver.CursorLifecycleListener;
import com.eltechs.axs.xserver.CursorLifecycleListenerList;
import com.eltechs.axs.xserver.CursorsManager;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.Pixmap;
import com.eltechs.axs.xserver.impl.drawables.DrawablesFactory;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class CursorsManagerImpl implements CursorsManager {
    private final DrawablesFactory drawablesFactory;
    private final Map<Integer, Cursor> cursors = new HashMap();
    private final CursorLifecycleListenerList cursorLifecycleListenersList = new CursorLifecycleListenerList();

    public CursorsManagerImpl(DrawablesFactory drawablesFactory) {
        this.drawablesFactory = drawablesFactory;
    }

    @Override // com.eltechs.axs.xserver.CursorsManager
    public Cursor createCursor(int i, int i2, int i3, Pixmap pixmap, Pixmap pixmap2) {
        if (this.cursors.containsKey(i)) {
            return null;
        }
        Drawable backingStore = pixmap2 != null ? pixmap2.getBackingStore() : null;
        Drawable backingStore2 = pixmap.getBackingStore();
        CursorImpl cursorImpl = new CursorImpl(i, i2, i3, this.drawablesFactory.create(0, null, backingStore2.getWidth(), backingStore2.getHeight(), backingStore2.getVisual()), backingStore2, backingStore);
        this.cursors.put(i, cursorImpl);
        this.cursorLifecycleListenersList.sendCursorCreated(cursorImpl);
        return cursorImpl;
    }

    @Override // com.eltechs.axs.xserver.CursorsManager
    public Cursor createFakeCursor(int i) {
        if (this.cursors.containsKey(Integer.valueOf(i))) {
            return null;
        }
        Drawable create = this.drawablesFactory.create(0, null, 1, 1, this.drawablesFactory.getPreferredVisualForDepth(1));
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(4);
        allocateDirect.put(new byte[4]);
        allocateDirect.rewind();
        create.getPainter().drawBitmap(0, 0, 1, 1, allocateDirect);
        CursorImpl cursorImpl = new CursorImpl(i, 0, 0, this.drawablesFactory.create(0, null, 1, 1, this.drawablesFactory.getPreferredVisualForDepth(1)), create, create);
        this.cursors.put(Integer.valueOf(i), cursorImpl);
        this.cursorLifecycleListenersList.sendCursorCreated(cursorImpl);
        return cursorImpl;
    }

    @Override // com.eltechs.axs.xserver.CursorsManager
    public Cursor getCursor(int i) {
        return this.cursors.get(Integer.valueOf(i));
    }

    @Override // com.eltechs.axs.xserver.CursorsManager
    public void freeCursor(Cursor cursor) {
        this.cursors.remove(Integer.valueOf(cursor.getId()));
        this.cursorLifecycleListenersList.sendCursorFreed(cursor);
    }

    @Override // com.eltechs.axs.xserver.CursorsManager
    public void recolorCursor(Cursor cursor, int i, int i2, int i3, int i4, int i5, int i6) {
        ((CursorImpl) cursor).recolorCursor(i, i2, i3, i4, i5, i6);
    }

    @Override // com.eltechs.axs.xserver.CursorsManager
    public void addCursorLifecycleListener(CursorLifecycleListener cursorLifecycleListener) {
        this.cursorLifecycleListenersList.addListener(cursorLifecycleListener);
    }

    @Override // com.eltechs.axs.xserver.CursorsManager
    public void removeCursorLifecycleListener(CursorLifecycleListener cursorLifecycleListener) {
        this.cursorLifecycleListenersList.removeListener(cursorLifecycleListener);
    }
}