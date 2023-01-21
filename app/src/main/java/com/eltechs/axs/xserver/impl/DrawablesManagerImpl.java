package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.DrawablesManager;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.impl.drawables.DrawablesFactory;
import com.eltechs.axs.xserver.impl.drawables.ImageFormat;
import com.eltechs.axs.xserver.impl.drawables.Visual;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DrawablesManagerImpl implements DrawablesManager {
    private final Map<Integer, Drawable> drawables = new HashMap();
    private final DrawablesFactory factory;

    public DrawablesManagerImpl(DrawablesFactory drawablesFactory) {
        this.factory = drawablesFactory;
    }

    @Override
    public Drawable createDrawable(int i, Window window, int i2, int i3, byte b) {
        return null;
    }

    @Override
    public Drawable createDrawable(int i, Window window, int i2, int i3, Visual visual) {
        return null;
    }

    @Override
    public Drawable getDrawable(int i) {
        return null;
    }

    @Override
    public Visual getPreferredVisual() {
        return null;
    }

    @Override
    public Collection<ImageFormat> getSupportedImageFormats() {
        return null;
    }

    @Override
    public Collection<Visual> getSupportedVisuals() {
        return null;
    }

    @Override
    public Visual getVisual(int i) {
        return null;
    }

    @Override
    public void removeDrawable(Drawable drawable) {

    }
}
