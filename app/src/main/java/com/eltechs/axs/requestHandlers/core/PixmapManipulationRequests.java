package com.eltechs.axs.requestHandlers.core;

import android.util.Log;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.NewXId;
import com.eltechs.axs.proto.input.annotations.OOBParam;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.Unsigned;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.proto.input.errors.BadIdChoice;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.Pixmap;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;

/* loaded from: classes.dex */
public class PixmapManipulationRequests extends HandlerObjectBase {
    public PixmapManipulationRequests(XServer xServer) {
        super(xServer);
    }

    @Locks({"PIXMAPS_MANAGER", "DRAWABLES_MANAGER"})
    @RequestHandler(opcode = 53)
    public void CreatePixmap(XClient xClient, @OOBParam @RequestParam byte depth, @RequestParam @NewXId int i, @RequestParam Drawable drawable, @RequestParam @Unsigned @Width(2) int width, @RequestParam @Unsigned @Width(2) int height) throws XProtocolError {
        Log.e("TAG", "CreatePixmap: "+i );
        Drawable createDrawable = this.xServer.getDrawablesManager().createDrawable(i, drawable.getRoot(), width, height, depth);
        if (createDrawable == null) {
            Log.e("TAG", "CreatePixmap: drawable创建为空，色深为"+depth);
            throw new BadIdChoice(i);
        }
        Pixmap createPixmap = this.xServer.getPixmapsManager().createPixmap(createDrawable);
        Assert.notNull(createPixmap, String.format("Id %d approved by the drawables manager appears to be already used for a pixmap.", Integer.valueOf(i)));
        xClient.registerAsOwnerOfPixmap(createPixmap);
    }

    @Locks({"PIXMAPS_MANAGER", "DRAWABLES_MANAGER"})
    @RequestHandler(opcode = 54)
    public void FreePixmap(XClient xClient, @RequestParam Pixmap pixmap) {
        this.xServer.getPixmapsManager().freePixmap(pixmap);
    }
}
