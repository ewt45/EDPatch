package com.eltechs.axs.requestHandlers.core;

import android.util.Log;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.NewXId;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.SpecialNullValue;
import com.eltechs.axs.proto.input.annotations.Unsigned;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.proto.input.errors.BadIdChoice;
import com.eltechs.axs.proto.input.errors.BadMatch;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xserver.Cursor;
import com.eltechs.axs.xserver.CursorsManager;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.Pixmap;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;

/* loaded from: classes.dex */
public class CursorManipulationRequests extends HandlerObjectBase {
    public CursorManipulationRequests(XServer xServer) {
        super(xServer);
    }

    @Locks({"CURSORS_MANAGER", "PIXMAPS_MANAGER", "DRAWABLES_MANAGER"})
    @RequestHandler(opcode = 93)
    public void CreateCursor(
            XClient xClient,
            @RequestParam @NewXId int i,
            @RequestParam Pixmap pixmap,
            @SpecialNullValue(0) @RequestParam Pixmap pixmap2,
            @RequestParam @Unsigned @Width(2) int i2,
            @RequestParam @Unsigned @Width(2) int i3,
            @RequestParam @Unsigned @Width(2) int i4,
            @RequestParam @Unsigned @Width(2) int i5,
            @RequestParam @Unsigned @Width(2) int i6,
            @RequestParam @Unsigned @Width(2) int i7,
            @RequestParam @Unsigned @Width(2) int i8,
            @RequestParam @Unsigned @Width(2) int i9
    ) throws XProtocolError {
//        Log.e("", "CreateCursor: opcode外部调用能logcat到吗");
        Drawable backingStore = pixmap.getBackingStore();
        if (pixmap2 != null) {
            Drawable backingStore2 = pixmap2.getBackingStore();
            if (backingStore2.getVisual().getDepth() != 1 || backingStore2.getWidth() != backingStore.getWidth() || backingStore2.getHeight() != backingStore.getHeight()) {
                throw new BadMatch();
            }
        }
        if (i8 > backingStore.getWidth() || i9 > backingStore.getHeight()) {
            throw new BadMatch();
        }
        CursorsManager cursorsManager = this.xServer.getCursorsManager();
        Cursor createCursor = cursorsManager.createCursor(i, i8, i9, pixmap, pixmap2);
        if (createCursor == null) {
            throw new BadIdChoice(i);
        }
        cursorsManager.recolorCursor(createCursor, i2, i3, i4, i5, i6, i7);
        xClient.registerAsOwnerOfCursor(createCursor);
    }

    @Locks({"CURSORS_MANAGER", "PIXMAPS_MANAGER", "DRAWABLES_MANAGER"})
    @RequestHandler(opcode = 94)
    public void CreateGlyphCursor(XClient xClient, @RequestParam @NewXId int i, @RequestParam Integer num, @RequestParam Integer num2, @RequestParam @Unsigned @Width(2) int i2, @RequestParam @Unsigned @Width(2) int i3, @RequestParam @Unsigned @Width(2) int i4, @RequestParam @Unsigned @Width(2) int i5, @RequestParam @Unsigned @Width(2) int i6, @RequestParam @Unsigned @Width(2) int i7, @RequestParam @Unsigned @Width(2) int i8, @RequestParam @Unsigned @Width(2) int i9) throws XProtocolError {
        CursorsManager cursorsManager = this.xServer.getCursorsManager();
        Cursor createFakeCursor = cursorsManager.createFakeCursor(i);
        if (createFakeCursor == null) {
            throw new BadIdChoice(i);
        }
        cursorsManager.recolorCursor(createFakeCursor, i4, i5, i6, i7, i8, i9);
        xClient.registerAsOwnerOfCursor(createFakeCursor);
    }

    @Locks({"CURSORS_MANAGER", "PIXMAPS_MANAGER", "DRAWABLES_MANAGER"})
    @RequestHandler(opcode = 95)
    public void FreeCursor(XClient xClient, @RequestParam Cursor cursor) {
        this.xServer.getCursorsManager().freeCursor(cursor);
    }

    @Locks({"CURSORS_MANAGER", "DRAWABLES_MANAGER"})
    @RequestHandler(opcode = 96)
    public void RecolorCursor(@RequestParam Cursor cursor, @RequestParam @Unsigned @Width(2) int i, @RequestParam @Unsigned @Width(2) int i2, @RequestParam @Unsigned @Width(2) int i3, @RequestParam @Unsigned @Width(2) int i4, @RequestParam @Unsigned @Width(2) int i5, @RequestParam @Unsigned @Width(2) int i6) {
        this.xServer.getCursorsManager().recolorCursor(cursor, i, i2, i3, i4, i5, i6);
    }
}
