package com.eltechs.axs.requestHandlers.core;

import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.OOBParam;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.XServer;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class DrawablesManipulationRequests extends HandlerObjectBase {

    /* loaded from: classes.dex */
    public enum QuerySizeObject {
        CURSOR,
        TILE,
        STIPPLE
    }

    public DrawablesManipulationRequests(XServer xServer) {
        super(xServer);
    }

    @Locks({"DRAWABLES_MANAGER", "WINDOWS_MANAGER"})
    @RequestHandler(opcode = 14)
    public void GetGeometry(XResponse xResponse, @RequestParam Drawable drawable) throws XProtocolError, IOException {
        final short s;
        final short s2;
        final short s3;
        byte depth = (byte) drawable.getVisual().getDepth();
        final short width = (short) drawable.getWidth();
        final short height = (short) drawable.getHeight();
        Window window = this.xServer.getWindowsManager().getWindow(drawable.getId());
        final Window rootWindow = this.xServer.getWindowsManager().getRootWindow();
        if (window != null) {
            Rectangle boundingRectangle = window.getBoundingRectangle();
            s3 = (short) window.getWindowAttributes().getBorderWidth();
            s2 = (short) boundingRectangle.y;
            s = (short) boundingRectangle.x;
        } else {
            s = 0;
            s2 = 0;
            s3 = 0;
        }
        xResponse.sendSimpleSuccessReply(depth, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.DrawablesManipulationRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(rootWindow.getId());
                byteBuffer.putShort(s);
                byteBuffer.putShort(s2);
                byteBuffer.putShort(width);
                byteBuffer.putShort(height);
                byteBuffer.putShort(s3);
            }
        });
    }

    @Locks({"DRAWABLES_MANAGER"})
    @RequestHandler(opcode = 97)
    public void QueryBestSize(XResponse xResponse, @OOBParam @RequestParam QuerySizeObject querySizeObject, @RequestParam Drawable drawable, @RequestParam final short s, @RequestParam final short s2) throws IOException {
        xResponse.sendSimpleSuccessReply((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.DrawablesManipulationRequests.2
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putShort(s);
                byteBuffer.putShort(s2);
            }
        });
    }
}
