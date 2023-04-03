package com.eltechs.axs.requestHandlers.core;

import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.OOBParam;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.errors.BadValue;
import com.eltechs.axs.proto.input.errors.BadWindow;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.FocusManager;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.XServer;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class FocusManipulationRequests extends HandlerObjectBase {
    private final int FOCUS_WINDOW_NONE;
    private final int FOCUS_WINDOW_POINTER_ROOT;

    public FocusManipulationRequests(XServer xServer) {
        super(xServer);
        this.FOCUS_WINDOW_NONE = 0;
        this.FOCUS_WINDOW_POINTER_ROOT = 1;
    }

    @Locks({"WINDOWS_MANAGER", "FOCUS_MANAGER"})
    @RequestHandler(opcode = 43)
    public void GetInputFocus(XResponse xResponse) throws IOException {
        final int i;
        FocusManager focusManager = this.xServer.getFocusManager();
        Window focusedWindow = focusManager.getFocusedWindow();
        FocusManager.FocusReversionPolicy focusReversionPolicy = focusManager.getFocusReversionPolicy();
        if (focusedWindow != null) {
            i = focusedWindow == this.xServer.getWindowsManager().getRootWindow() ? 1 : focusedWindow.getId();
        } else {
            i = 0;
        }
        xResponse.sendSimpleSuccessReply((byte) focusReversionPolicy.ordinal(), new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.FocusManipulationRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(i);
            }
        });
    }

    @Locks({"WINDOWS_MANAGER", "FOCUS_MANAGER"})
    @RequestHandler(opcode = 42)
    public void SetInputFocus(@OOBParam @RequestParam FocusManager.FocusReversionPolicy focusReversionPolicy, @RequestParam int i, @RequestParam int i2) throws BadValue, BadWindow {
        Window window;
        this.xServer.getFocusManager();
        switch (i) {
            case 0:
                window = null;
                break;
            case 1:
                window = this.xServer.getWindowsManager().getRootWindow();
                break;
            default:
                Window window2 = this.xServer.getWindowsManager().getWindow(i);
                if (window2 != null) {
                    window = window2;
                    break;
                } else {
                    throw new BadWindow(i);
                }
        }
        this.xServer.getFocusManager().setFocus(window, focusReversionPolicy);
    }
}
