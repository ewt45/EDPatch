package com.eltechs.axs.requestHandlers.xtest;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.Signed;
import com.eltechs.axs.proto.input.annotations.SpecialNullValue;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.proto.input.errors.BadValue;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Pointer;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.XServer;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class XTestRequests extends HandlerObjectBase {
    public XTestRequests(XServer xServer) {
        super(xServer);
    }

    @RequestHandler(opcode = 0)
    public void GetVersion(XResponse xResponse, @RequestParam byte b, @RequestParam byte b2, @RequestParam short s) throws IOException {
        xResponse.sendSimpleSuccessReply((byte) 2, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.xtest.XTestRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putShort((short) 1);
            }
        });
    }

    @Locks({"INPUT_DEVICES", "WINDOWS_MANAGER", "FOCUS_MANAGER"})
    @RequestHandler(opcode = 2)
    public void FakeInput(@RequestParam byte b, @RequestParam byte b2, @RequestParam short s, @RequestParam int i, @SpecialNullValue(0) @RequestParam Window window, @RequestParam int i2, @RequestParam int i3, @RequestParam @Signed @Width(2) int i4, @RequestParam @Signed @Width(2) int i5, @RequestParam int i6, @RequestParam int i7) throws XProtocolError {
        if (b < 2 || b > 6) {
            throw new BadValue(b);
        }
        if (i != 0) {
            Assert.notImplementedYet();
        }
        if (window != null && window.getId() != this.xServer.getWindowsManager().getRootWindow().getId()) {
            throw new BadValue(window.getId());
        }
        switch (b) {
            case 2:
                if (!this.xServer.getKeyboardModelManager().getKeyboardModel().isKeycodeValid(b2)) {
                    throw new BadValue(b2);
                }
                this.xServer.getEventsInjector().injectKeyPress(b2, 0);
                return;
            case 3:
                if (!this.xServer.getKeyboardModelManager().getKeyboardModel().isKeycodeValid(b2)) {
                    throw new BadValue(b2);
                }
                this.xServer.getEventsInjector().injectKeyRelease(b2, 0);
                return;
            case 4:
                if (!this.xServer.getPointer().isButtonValid(b2)) {
                    throw new BadValue(b2);
                }
                this.xServer.getEventsInjector().injectPointerButtonPress(b2);
                return;
            case 5:
                if (!this.xServer.getPointer().isButtonValid(b2)) {
                    throw new BadValue(b2);
                }
                this.xServer.getEventsInjector().injectPointerButtonRelease(b2);
                return;
            case 6:
                if (b2 != 0 && b2 != 1) {
                    throw new BadValue(b2);
                }
                Pointer pointer = this.xServer.getPointer();
                if (b2 == 1) {
                    i4 += pointer.getX();
                    i5 += pointer.getY();
                }
                pointer.warpOnCoordinates(i4, i5);
                return;
            default:
                throw new BadValue(b2);
        }
    }
}
