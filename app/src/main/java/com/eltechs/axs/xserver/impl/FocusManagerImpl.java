package com.eltechs.axs.xserver.impl;

import static com.eltechs.axs.xserver.FocusManager.FocusReversionPolicy.NONE;

import android.util.Log;

import com.eltechs.axs.xserver.FocusListener;
import com.eltechs.axs.xserver.FocusListenersList;
import com.eltechs.axs.xserver.FocusManager;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowLifecycleListener;
import com.eltechs.axs.xserver.XServer;

/* loaded from: classes.dex */
public class FocusManagerImpl implements FocusManager, WindowLifecycleListener {
    private static final String TAG ="FocusManagerImpl";
    private Window focusedWindow;
    private XServer xServer;
    private FocusManager.FocusReversionPolicy reversionPolicy = NONE;
    private FocusListenersList listeners = new FocusListenersList();

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowCreated(Window window) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowMapped(Window window) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowReparented(Window window, Window window2) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowZOrderChange(Window window) {
    }

    public FocusManagerImpl(Window window, XServer xServer) {
        this.focusedWindow = window;
        this.xServer = xServer;
    }

    private void revertFocus() {
        Window window = this.focusedWindow;
        switch (this.reversionPolicy) {
            case NONE:
                this.focusedWindow = null;
                break;
            case POINTER_ROOT:
                this.focusedWindow = this.xServer.getWindowsManager().getRootWindow();
                break;
            case PARENT:
                //改写：应找到最近的可见的父节点。然后revertTo改为NONE
                while(focusedWindow.getParent() !=null){
                    focusedWindow = focusedWindow.getParent();
                    if(focusedWindow.getWindowAttributes().isMapped())
                        break;
                }
                reversionPolicy = NONE;
                //原代码
//                if (this.focusedWindow.getParent() != null) {
//                    this.focusedWindow = this.focusedWindow.getParent();
//                    break;
//                }
                break;
        }
        if (window != this.focusedWindow) {
            this.listeners.sendFocusChanged(window, this.focusedWindow);
        }
    }

    @Override // com.eltechs.axs.xserver.FocusManager
    public Window getFocusedWindow() {
        return this.focusedWindow;
    }

    @Override // com.eltechs.axs.xserver.FocusManager
    public void setFocus(Window window, FocusManager.FocusReversionPolicy focusReversionPolicy) {
        Window window2 = this.focusedWindow;
        this.focusedWindow = window;
        this.reversionPolicy = focusReversionPolicy;
        if (window2 != window) {
            Log.d(TAG, "setFocus: focus的窗口改变了！"+(window!=null?window.getId():null));
            this.listeners.sendFocusChanged(window2, window);
        }
    }

    @Override // com.eltechs.axs.xserver.FocusManager
    public FocusManager.FocusReversionPolicy getFocusReversionPolicy() {
        return this.reversionPolicy;
    }

    @Override // com.eltechs.axs.xserver.FocusManager
    public void addFocusListner(FocusListener focusListener) {
        this.listeners.addListener(focusListener);
    }

    @Override // com.eltechs.axs.xserver.FocusManager
    public void removeFocusListener(FocusListener focusListener) {
        this.listeners.removeListener(focusListener);
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowUnmapped(Window window) {
        if (window == this.focusedWindow) {
            revertFocus();
        }
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowDestroyed(Window window) {
        if (window == this.focusedWindow) {
            revertFocus();
        }
    }
}
