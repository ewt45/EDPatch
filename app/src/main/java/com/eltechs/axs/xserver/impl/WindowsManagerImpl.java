package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.xserver.DrawablesManager;
import com.eltechs.axs.xserver.ScreenInfo;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowsManager;

import java.util.HashMap;
import java.util.Map;

public class WindowsManagerImpl implements WindowsManager {
    private final DrawablesManager drawablesManager;
    private final Window rootWindow;
    private final Map<Integer, Window> windows = new HashMap();
//    private final WindowContentModificationListenersList windowContentModificationListenersList = new WindowContentModificationListenersList();
//    private final WindowLifecycleListenersList windowLifecycleListenersList = new WindowLifecycleListenersList();
//    private final WindowChangeListenersList windowChangeListenersList = new WindowChangeListenersList();

    public WindowsManagerImpl(ScreenInfo screenInfo, DrawablesManager drawablesManager) {
        this.drawablesManager = drawablesManager;
        rootWindow =null;

    }
    @Override // com.eltechs.axs.xserver.WindowsManager
    public Window getRootWindow() {
        return this.rootWindow;
    }

}
