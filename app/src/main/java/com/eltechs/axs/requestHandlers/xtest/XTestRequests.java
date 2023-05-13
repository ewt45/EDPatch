package com.eltechs.axs.requestHandlers.xtest;

import android.util.Log;

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
import com.eltechs.axs.xserver.events.CoreEventCodes;

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
    public void FakeInput(@RequestParam byte eventType, @RequestParam byte btnOrKey, @RequestParam short unused, @RequestParam int delayTime, @SpecialNullValue(0) @RequestParam Window rootWindow, @RequestParam int unused2, @RequestParam int i3, @RequestParam @Signed @Width(2) int motionX, @RequestParam @Signed @Width(2) int motionY, @RequestParam int i6, @RequestParam int i7) throws XProtocolError {

        /*
        FAKE_EVENT_TYPE
         2     KeyPress
         3     KeyRelease
         4     ButtonPress
         5     ButtonRelease
         6     MotionNotify
         */
        if (eventType < 2 || eventType > 6) {
            throw new BadValue(eventType);
        }
        if (delayTime != 0) {
            Assert.notImplementedYet();
        }
        if (rootWindow != null && rootWindow.getId() != this.xServer.getWindowsManager().getRootWindow().getId()) {
            throw new BadValue(rootWindow.getId());
        }
        switch (eventType) {
            case CoreEventCodes.KEY_PRESS:
                if (!this.xServer.getKeyboardModelManager().getKeyboardModel().isKeycodeValid(btnOrKey)) {
                    throw new BadValue(btnOrKey);
                }
                this.xServer.getEventsInjector().injectKeyPress(btnOrKey, 0);
                return;
            case CoreEventCodes.KEY_RELEASE:
                if (!this.xServer.getKeyboardModelManager().getKeyboardModel().isKeycodeValid(btnOrKey)) {
                    throw new BadValue(btnOrKey);
                }
                this.xServer.getEventsInjector().injectKeyRelease(btnOrKey, 0);
                return;
            case CoreEventCodes.BUTTON_PRESS:
                if (!this.xServer.getPointer().isButtonValid(btnOrKey)) {
                    throw new BadValue(btnOrKey);
                }
                this.xServer.getEventsInjector().injectPointerButtonPress(btnOrKey);
                return;
            case CoreEventCodes.BUTTON_RELEASE:
                if (!this.xServer.getPointer().isButtonValid(btnOrKey)) {
                    throw new BadValue(btnOrKey);
                }
                this.xServer.getEventsInjector().injectPointerButtonRelease(btnOrKey);
                return;
            case CoreEventCodes.MOTION_NOTIFY:
                if (btnOrKey != 0 && btnOrKey != 1) {
                    throw new BadValue(btnOrKey);
                }
                Pointer pointer = this.xServer.getPointer();
                if (btnOrKey == 1) {
                    motionX += pointer.getX();
                    motionY += pointer.getY();
                }
                Log.d("TAG", "FakeInput修改了指针坐标");

                pointer.warpOnCoordinates(motionX, motionY);
                return;
            default:
                throw new BadValue(btnOrKey);
        }
    }

}
