package com.eltechs.axs.requestHandlers.core;

import android.util.Log;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.OOBParam;
import com.eltechs.axs.proto.input.annotations.ParamLength;
import com.eltechs.axs.proto.input.annotations.ParamName;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.SpecialNullValue;
import com.eltechs.axs.proto.input.annotations.Unsigned;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Cursor;
import com.eltechs.axs.xserver.DeviceGrabMode;
import com.eltechs.axs.xserver.EventName;
import com.eltechs.axs.xserver.GrabsManager;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;
import com.eltechs.axs.xserver.helpers.WindowHelpers;
import com.eltechs.axs.xserver.impl.masks.Mask;
import java.io.IOException;

/* loaded from: classes.dex */
public class GrabManipulationRequests extends HandlerObjectBase {

    /* loaded from: classes.dex */
    private enum GrabReplyTypes {
        SUCCESS,
        ALREADY_GRABBED,
        INVALID_TIME,
        NOT_VIEWABLE,
        FROZEN
    }

    @Locks({"WINDOWS_MANAGER", "CURSORS_MANAGER", "FOCUS_MANAGER"})
    @RequestHandler(opcode = 32)
    public void UngrabKeyboard(@RequestParam int i) {
    }

    public GrabManipulationRequests(XServer xServer) {
        super(xServer);
    }

    @Locks({"WINDOWS_MANAGER", "CURSORS_MANAGER", "INPUT_DEVICES", "FOCUS_MANAGER"})
    @RequestHandler(opcode = 26)
    public void GrabPointer(XClient xClient, XResponse xResponse, @OOBParam @RequestParam boolean z, @RequestParam Window window, @RequestParam @Width(2) Mask<EventName> mask, @RequestParam DeviceGrabMode deviceGrabMode, @RequestParam DeviceGrabMode deviceGrabMode2, @SpecialNullValue(0) @RequestParam Window window2, @SpecialNullValue(0) @RequestParam Cursor cursor, @RequestParam int i) throws IOException {
        XClient xClient2;
        GrabReplyTypes grabReplyTypes;
        GrabsManager grabsManager = this.xServer.getGrabsManager();
        if (i != 0) {
            Assert.notImplementedYet();
        }
        if (grabsManager.getPointerGrabWindow() != null) {
            xClient2 = xClient;
            if (grabsManager.getPointerGrabListener().getClient() != xClient2) {
                grabReplyTypes = GrabReplyTypes.ALREADY_GRABBED;
                xResponse.sendSimpleSuccessReply((byte) grabReplyTypes.ordinal(), new Object[0]);
            }
        } else {
            xClient2 = xClient;
        }
        if (WindowHelpers.getWindowMapState(window) != WindowHelpers.MapState.VIEWABLE) {
            grabReplyTypes = GrabReplyTypes.NOT_VIEWABLE;
        } else {
            GrabReplyTypes grabReplyTypes2 = GrabReplyTypes.SUCCESS;
            grabsManager.initiateActivePointerGrab(window, z, mask, cursor, window2, deviceGrabMode, deviceGrabMode2, i, xClient2);
            grabReplyTypes = grabReplyTypes2;
        }
        xResponse.sendSimpleSuccessReply((byte) grabReplyTypes.ordinal(), new Object[0]);
    }

    @Locks({"WINDOWS_MANAGER", "FOCUS_MANAGER", "INPUT_DEVICES"})
    @RequestHandler(opcode = 27)
    public void UngrabPointer(@RequestParam int i) {
        if (i != 0) {
            Assert.notImplementedYet();
        }
        this.xServer.getGrabsManager().disablePointerGrab();
    }

    @Locks({"WINDOWS_MANAGER", "CURSORS_MANAGER", "FOCUS_MANAGER"})
    @RequestHandler(opcode = 31)
    public void GrabKeyboard(XResponse xResponse, @OOBParam @RequestParam boolean z, @RequestParam Window window, @RequestParam int i, @RequestParam boolean z2, @RequestParam boolean z3, @RequestParam short s) throws IOException {
        xResponse.sendSimpleSuccessReply((byte) 0);
    }
//    @Locks({"WINDOWS_MANAGER", "CURSORS_MANAGER", "FOCUS_MANAGER"})
//    @RequestHandler(opcode = Opcodes.UngrabKey)
//    public void UngrabKey( @RequestParam byte key, @RequestParam short length,@RequestParam Window grabWindow,@RequestParam short modifiers,@RequestParam short pad){
//        /*
//         * typedef struct {
//         *     CARD8 reqType;
//         *     CARD8 key;
//         *     CARD16 length B16;
//         *     Window grabWindow B32;
//         *     CARD16 modifiers B16;
//         *     CARD16 pad B16;
//         * } xUngrabKeyReq;
//         */
//        Log.d(null, String.valueOf("UngrabKey: key=%d length=%d, grabWindow=%d, modifiers=%d, pad=%d"));
//
//    }
//
//    @Locks({"WINDOWS_MANAGER", "CURSORS_MANAGER", "FOCUS_MANAGER"})
//    @RequestHandler(opcode = Opcodes.GrabKey)
//    public void GrabKey(
//            @RequestParam boolean ownerEvents,
//            @RequestParam @Unsigned short length,
//            @RequestParam Window grabWindow,
//            @RequestParam @Unsigned short modifiers,
//            @RequestParam @Unsigned byte key,
//            @RequestParam byte pointerMode,
//            @RequestParam byte keyboardMode,
//            @RequestParam byte pad1,
//            @RequestParam byte pad2,
//            @RequestParam byte pad3
//            ){
//        /*
//        CARD8 reqType;
//        BOOL ownerEvents;
//        CARD16 length B16;
//        Window grabWindow B32;
//        CARD16 modifiers B16;
//        CARD8 key;
//        BYTE pointerMode, keyboardMode;
//        BYTE pad1, pad2, pad3;
//         */
//        Log.d(null, String.valueOf("UngrabKey: key=%d length=%d, grabWindow=%d, modifiers=%d, pad=%d"));
//
//    }
}
