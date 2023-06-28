package com.eltechs.axs.requestHandlers.core;

import android.util.Log;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.NewXId;
import com.eltechs.axs.proto.input.annotations.OOBParam;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.SpecialNullValue;
import com.eltechs.axs.proto.input.annotations.Unsigned;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.proto.input.errors.BadIdChoice;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XRequest;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Colormap;
import com.eltechs.axs.xserver.PixmapsManager;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;
import com.eltechs.axs.xserver.impl.PixmapsManagerImpl;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ColormapManipulationRequests extends HandlerObjectBase {

    /* loaded from: classes.dex */
    public enum Alloc {
        NONE,
        ALL
    }

    public ColormapManipulationRequests(XServer xServer) {
        super(xServer);
    }

    @Locks({"COLORMAPS_MANAGER", "WINDOWS_MANAGER"})
    @RequestHandler(opcode = 78)
    public void CreateColormap(XClient xClient, @OOBParam @RequestParam Alloc alloc, @RequestParam @NewXId int i, @RequestParam Window window, @RequestParam int i2) throws XProtocolError {
        Log.e("CreateColormap", "CreateColormap: "+i);
        Colormap createColormap = this.xServer.getColormapsManager().createColormap(i);
        if (createColormap == null) {
            throw new BadIdChoice(i);
        }
        xClient.registerAsOwnerOfColormap(createColormap);
    }

    @Locks({"COLORMAPS_MANAGER"})
    @RequestHandler(opcode = 79)
    public void FreeColormap(XClient xClient, @RequestParam Colormap colormap) {
        this.xServer.getColormapsManager().freeColormap(colormap);
    }

//    @Locks({"COLORMAPS_MANAGER","PIXMAPS_MANAGER"})
//    @RequestHandler(opcode = Opcodes.AllocColor)
//    public void AllocColor(XClient xClient, XResponse xResponse, @RequestParam @SpecialNullValue(0) Colormap colormap, @RequestParam @Unsigned short red, @RequestParam @Unsigned short green, @RequestParam @Unsigned short blue, @RequestParam @Unsigned short whatisthis) throws IOException {
//        Log.d("AllocColor", String.format("AllocColor: colormap=%d, rgb= %d %d %d %d",colormap==null?null:colormap.getId(),red,green,blue,whatisthis));
//        // from class: com.eltechs.axs.requestHandlers.core.AtomManipulationRequests.1
//// com.eltechs.axs.xconnectors.BufferFiller
//        xResponse.sendSimpleSuccessReply((byte) 0, byteBuffer -> byteBuffer.putShort(red)
//                .putShort(green)
//                .putShort(blue)
//                .putShort((short) 0)
//                .putInt(0)
//                .putInt(0)
//                .putInt(0)
//                .putInt(0));
//    }
}
