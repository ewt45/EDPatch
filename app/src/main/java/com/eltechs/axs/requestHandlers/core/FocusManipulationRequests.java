package com.eltechs.axs.requestHandlers.core;

import android.util.Log;

import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.OOBParam;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.errors.BadMatch;
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
    private static final String TAG = "FocusManRequests";
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
        i = focusedWindow != null
                ? focusedWindow == this.xServer.getWindowsManager().getRootWindow() ? 1 : focusedWindow.getId()
                : 0;
        xResponse.sendSimpleSuccessReply((byte) focusReversionPolicy.ordinal(), new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.FocusManipulationRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(i);
            }
        });
        Log.d(TAG, "GetInputFocus: focusWindow="+i+", focus="+focusReversionPolicy);

    }

    /**
     * https://www.x.org/releases/current/doc/xproto/x11protocol.html#requests:SetInputFocus
     *
     * @param revertTo    { None, PointerRoot, Parent }
     * @param focus        WINDOW or PointerRoot or None
     * @param time     TIMESTAMP or CurrentTime
     */
    @Locks({"WINDOWS_MANAGER", "FOCUS_MANAGER"})
    @RequestHandler(opcode = 42)
    public void SetInputFocus(@OOBParam @RequestParam FocusManager.FocusReversionPolicy revertTo, @RequestParam int focus, @RequestParam int time) throws BadValue, BadWindow, BadMatch {
        Window window;
        this.xServer.getFocusManager();

        switch (focus) {
            case 0: //NONE
                window = null;
                break;
            case 1: //PointerRoot
                window = this.xServer.getWindowsManager().getRootWindow();
                break;
            default:
                window = this.xServer.getWindowsManager().getWindow(focus);
                if (window == null ) {
                    throw new BadWindow(focus);
                }
                //新加的，按文档说如果focus的窗口不可见就应该抛出Match异常
                else if(!window.getWindowAttributes().isMapped()){
                    throw new BadMatch();
                }
                break;
        }

        if(revertTo== FocusManager.FocusReversionPolicy.NONE)
            Log.e(TAG, "SetInputFocus: !!! 设置了NONE" );
        else
            Log.d(TAG, "SetInputFocus: focus="+focus+",revertTo="+revertTo+" decidedWindowId=" + (window != null ? window.getId() : null) + ", rootWindow=" + xServer.getWindowsManager().getRootWindow());

        this.xServer.getFocusManager().setFocus(window, revertTo);
    }
}
