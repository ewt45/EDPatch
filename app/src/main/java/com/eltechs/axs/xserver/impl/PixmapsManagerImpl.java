package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.DrawablesManager;
import com.eltechs.axs.xserver.Pixmap;
import com.eltechs.axs.xserver.PixmapLifecycleListener;
import com.eltechs.axs.xserver.PixmapLifecycleListenerList;
import com.eltechs.axs.xserver.PixmapsManager;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class PixmapsManagerImpl implements PixmapsManager {
    private final DrawablesManager drawablesManager;
    private final Map<Integer, Pixmap> pixmaps = new HashMap();
    private final PixmapLifecycleListenerList pixmapLifecycleListenersList = new PixmapLifecycleListenerList();

    public PixmapsManagerImpl(DrawablesManager drawablesManager) {
        this.drawablesManager = drawablesManager;
    }

    @Override // com.eltechs.axs.xserver.PixmapsManager
    public Pixmap getPixmap(int i) {
        return this.pixmaps.get(Integer.valueOf(i));
    }

    @Override // com.eltechs.axs.xserver.PixmapsManager
    public Pixmap createPixmap(Drawable drawable) {
        if (this.pixmaps.containsKey(Integer.valueOf(drawable.getId()))) {
            return null;
        }
        PixmapImpl pixmapImpl = new PixmapImpl(drawable);
        this.pixmaps.put(Integer.valueOf(drawable.getId()), pixmapImpl);
        this.pixmapLifecycleListenersList.sendPixmapCreated(pixmapImpl);
        return pixmapImpl;
    }

    @Override // com.eltechs.axs.xserver.PixmapsManager
    public void freePixmap(Pixmap pixmap) {
        this.pixmaps.remove(Integer.valueOf(pixmap.getBackingStore().getId()));
        this.drawablesManager.removeDrawable(pixmap.getBackingStore());
        this.pixmapLifecycleListenersList.sendPixmapFreed(pixmap);
    }

    @Override // com.eltechs.axs.xserver.PixmapsManager
    public void addPixmapLifecycleListener(PixmapLifecycleListener pixmapLifecycleListener) {
        this.pixmapLifecycleListenersList.addListener(pixmapLifecycleListener);
    }

    @Override // com.eltechs.axs.xserver.PixmapsManager
    public void removePixmapLifecycleListener(PixmapLifecycleListener pixmapLifecycleListener) {
        this.pixmapLifecycleListenersList.removeListener(pixmapLifecycleListener);
    }
}
