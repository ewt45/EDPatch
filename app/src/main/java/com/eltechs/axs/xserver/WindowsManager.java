package com.eltechs.axs.xserver;

import com.eltechs.axs.xserver.client.XClient;
import com.eltechs.axs.xserver.impl.drawables.Visual;
import java.util.List;

/* loaded from: classes.dex */
public interface WindowsManager {
    void addWindowChangeListener(WindowChangeListener windowChangeListener);

    void addWindowContentModificationListner(WindowContentModificationListener windowContentModificationListener);

    void addWindowLifecycleListener(WindowLifecycleListener windowLifecycleListener);

    void changeRelativeWindowGeometry(Window window, int x, int y, int width, int height);

    void changeWindowZOrder(Window window, Window window2, StackMode stackMode);

    Window createWindow(int i, Window window, int x, int y, int width, int height, Visual visual, boolean z, XClient xClient);

    void destroySubwindows(Window window);

    void destroyWindow(Window window);

    List<PlacedDrawable> getDrawablesForOutput();

    Window getRootWindow();

    Window getWindow(int i);

    void mapSubwindows(Window window);

    void mapWindow(Window window);

    void removeWindowChangeListener(WindowChangeListener windowChangeListener);

    void removeWindowContentModificationListner(WindowContentModificationListener windowContentModificationListener);

    void removeWindowLifecycleListener(WindowLifecycleListener windowLifecycleListener);

    void unmapSubwindows(Window window);

    void unmapWindow(Window window);
}