package com.eltechs.axs.requestHandlers.core;

import com.eltechs.axs.geom.Point;
import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.SpecialNullValue;
import com.eltechs.axs.proto.input.errors.BadValue;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Pointer;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;
import com.eltechs.axs.xserver.helpers.EventHelpers;
import com.eltechs.axs.xserver.helpers.WindowHelpers;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class PointerRelatedRequests extends HandlerObjectBase {
    public PointerRelatedRequests(XServer xServer) {
        super(xServer);
    }

    @Locks({"INPUT_DEVICES", "WINDOWS_MANAGER"})
    @RequestHandler(opcode = 38)
    public void QueryPointer(XResponse xResponse, @RequestParam Window window) throws IOException {
        final Pointer pointer = this.xServer.getPointer();
        final Window directMappedSubWindowByCoords = WindowHelpers.getDirectMappedSubWindowByCoords(window, pointer.getX(), pointer.getY());
        final Point convertRootCoordsToWindow = WindowHelpers.convertRootCoordsToWindow(window, pointer.getX(), pointer.getY());
        xResponse.sendSimpleSuccessReply((byte) 1, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.PointerRelatedRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(PointerRelatedRequests.this.xServer.getWindowsManager().getRootWindow().getId());
                byteBuffer.putInt(directMappedSubWindowByCoords == null ? 0 : directMappedSubWindowByCoords.getId());
                byteBuffer.putShort((short) pointer.getX());
                byteBuffer.putShort((short) pointer.getY());
                byteBuffer.putShort((short) convertRootCoordsToWindow.x);
                byteBuffer.putShort((short) convertRootCoordsToWindow.y);
                byteBuffer.putShort((short) EventHelpers.getKeyButMask(PointerRelatedRequests.this.xServer).getRawMask());
            }
        });
    }

    @Locks({"INPUT_DEVICES", "WINDOWS_MANAGER", "FOCUS_MANAGER"})
    @RequestHandler(opcode = 41)
    public void WarpPointer(@SpecialNullValue(0) @RequestParam Window window, @SpecialNullValue(0) @RequestParam Window window2, @RequestParam short s, @RequestParam short s2, @RequestParam short s3, @RequestParam short s4, @RequestParam short s5, @RequestParam short s6) {
        int i;
        int i2;
        Pointer pointer = this.xServer.getPointer();
        if (window != null) {
            Rectangle boundingRectangle = window.getBoundingRectangle();
            if (s < 0 || s2 < 0 || s3 < 0 || s4 < 0 || s > boundingRectangle.width || s2 > boundingRectangle.height) {
                return;
            }
            int i3 = (s4 == 0 || s2 + s4 >= boundingRectangle.height) ? boundingRectangle.height - s2 : s4 + 1;
            int i4 = (s3 == 0 || s + s3 >= boundingRectangle.width) ? boundingRectangle.width - s : s3 + 1;
            Point convertRootCoordsToWindow = WindowHelpers.convertRootCoordsToWindow(window, pointer.getX(), pointer.getY());
            if (!new Rectangle(s, s2, i4, i3).containsPoint(convertRootCoordsToWindow.x, convertRootCoordsToWindow.y)) {
                return;
            }
        }
        if (window2 == null) {
            i2 = pointer.getX() + s5;
            i = pointer.getY() + s6;
        } else {
            Point convertWindowCoordsToRoot = WindowHelpers.convertWindowCoordsToRoot(window2, s5, s6);
            int i5 = convertWindowCoordsToRoot.x;
            i = convertWindowCoordsToRoot.y;
            i2 = i5;
        }
        pointer.warpOnCoordinates(i2, i);
    }

    @RequestHandler(opcode = 106)
    public void GetPointerControl(XResponse xResponse) throws IOException {
        xResponse.sendSimpleSuccessReply((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.PointerRelatedRequests.2
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putShort((short) 1);
                byteBuffer.putShort((short) 1);
                byteBuffer.putShort((short) 0);
            }
        });
    }

    @RequestHandler(opcode = 105)
    public void ChangePointerControl(XClient xClient, @RequestParam short s, @RequestParam short s2, @RequestParam short s3, @RequestParam boolean z, @RequestParam boolean z2) throws XProtocolError {
        if (s < -1) {
            throw new BadValue(s);
        }
        if (s2 != 0) {
            return;
        }
        throw new BadValue(s2);
    }
}
