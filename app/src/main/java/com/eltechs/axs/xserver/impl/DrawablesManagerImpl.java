package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.DrawablesManager;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.impl.drawables.DrawablesFactory;
import com.eltechs.axs.xserver.impl.drawables.ImageFormat;
import com.eltechs.axs.xserver.impl.drawables.Visual;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class DrawablesManagerImpl implements DrawablesManager {
    private final Map<Integer, Drawable> drawables = new HashMap();
    private final DrawablesFactory factory;

    public DrawablesManagerImpl(DrawablesFactory drawablesFactory) {
        this.factory = drawablesFactory;
    }

    @Override // com.eltechs.axs.xserver.DrawablesManager
    public Drawable getDrawable(int i) {
        return this.drawables.get(i);
    }

    @Override // com.eltechs.axs.xserver.DrawablesManager
    public Drawable createDrawable(int i, Window window, int width, int height, byte depth) {
        Visual preferredVisualForDepth = this.factory.getPreferredVisualForDepth(ArithHelpers.extendAsUnsigned(depth));
        if (preferredVisualForDepth == null) {
            return null;
        }
        return createDrawable(i, window, width, height, preferredVisualForDepth);
    }

    @Override // com.eltechs.axs.xserver.DrawablesManager
    public Drawable createDrawable(int i, Window window, int width, int height, Visual visual) {
        if (this.drawables.containsKey(i)) {
            return null;
        }
        Drawable create = this.factory.create(i, window, width, height, visual);
        this.drawables.put(i, create);
        return create;
    }

    @Override // com.eltechs.axs.xserver.DrawablesManager
    public void removeDrawable(Drawable drawable) {
        this.drawables.remove(drawable.getId());
    }

    @Override // com.eltechs.axs.xserver.DrawablesManager
    public Collection<Visual> getSupportedVisuals() {
        return this.factory.getSupportedVisuals();
    }

    @Override // com.eltechs.axs.xserver.DrawablesManager
    public Visual getPreferredVisual() {
        return this.factory.getPreferredVisual();
    }

    @Override // com.eltechs.axs.xserver.DrawablesManager
    public Visual getVisual(int i) {
        return this.factory.getVisual(i);
    }

    @Override // com.eltechs.axs.xserver.DrawablesManager
    public Collection<ImageFormat> getSupportedImageFormats() {
        return this.factory.getSupportedImageFormats();
    }
}