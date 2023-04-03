package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.xserver.Colormap;
import com.eltechs.axs.xserver.ColormapLifecycleListener;
import com.eltechs.axs.xserver.ColormapLifecycleListenerList;
import com.eltechs.axs.xserver.ColormapsManager;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class ColormapsManagerImpl implements ColormapsManager {
    private final Map<Integer, Colormap> colormaps = new HashMap();
    private final ColormapLifecycleListenerList colormapLifecycleListenerList = new ColormapLifecycleListenerList();

    @Override // com.eltechs.axs.xserver.ColormapsManager
    public Colormap createColormap(int i) {
        if (this.colormaps.containsKey(Integer.valueOf(i))) {
            return null;
        }
        ColormapImpl colormapImpl = new ColormapImpl(i);
        this.colormaps.put(Integer.valueOf(i), colormapImpl);
        this.colormapLifecycleListenerList.sendColormapCreated(colormapImpl);
        return colormapImpl;
    }

    @Override // com.eltechs.axs.xserver.ColormapsManager
    public Colormap getColormap(int i) {
        return this.colormaps.get(Integer.valueOf(i));
    }

    @Override // com.eltechs.axs.xserver.ColormapsManager
    public void freeColormap(Colormap colormap) {
        this.colormaps.remove(Integer.valueOf(colormap.getId()));
        this.colormapLifecycleListenerList.sendColormapFreed(colormap);
    }

    @Override // com.eltechs.axs.xserver.ColormapsManager
    public void addColormapLifecycleListener(ColormapLifecycleListener colormapLifecycleListener) {
        this.colormapLifecycleListenerList.addListener(colormapLifecycleListener);
    }

    @Override // com.eltechs.axs.xserver.ColormapsManager
    public void removeColormapLifecycleListener(ColormapLifecycleListener colormapLifecycleListener) {
        this.colormapLifecycleListenerList.removeListener(colormapLifecycleListener);
    }
}
