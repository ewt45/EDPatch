package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.GraphicsContext;
import com.eltechs.axs.xserver.graphicsContext.PixelCompositionRule;
import com.eltechs.axs.xserver.graphicsContext.SubwindowMode;

/* loaded from: classes.dex */
public class GraphicsContextImpl implements GraphicsContext {
    private final int id;
    private final Drawable referenceDrawable;
    private PixelCompositionRule function = PixelCompositionRule.COPY;
    private int planeMask = -1;
    private int foreground = 0;
    private int background = 1;
    private SubwindowMode subwindowMode = SubwindowMode.CLIP_BY_CHILDREN;
    private int lineWidth = 1;

    public GraphicsContextImpl(int i, Drawable drawable) {
        this.id = i;
        this.referenceDrawable = drawable;
    }

    @Override // com.eltechs.axs.xserver.GraphicsContext
    public int getId() {
        return this.id;
    }

    @Override // com.eltechs.axs.xserver.GraphicsContext
    public Drawable getReferenceDrawable() {
        return this.referenceDrawable;
    }

    @Override // com.eltechs.axs.xserver.GraphicsContext
    public PixelCompositionRule getFunction() {
        return this.function;
    }

    public void setFunction(PixelCompositionRule pixelCompositionRule) {
        this.function = pixelCompositionRule;
    }

    @Override // com.eltechs.axs.xserver.GraphicsContext
    public int getPlaneMask() {
        return this.planeMask;
    }

    public void setPlaneMask(int i) {
        if (i != -1) {
            Assert.notImplementedYet("GC::PlaneMask must be all ones. Other values are not supported yet.");
        }
        this.planeMask = i;
    }

    @Override // com.eltechs.axs.xserver.GraphicsContext
    public int getForeground() {
        return this.foreground;
    }

    public void setForeground(int i) {
        this.foreground = i;
    }

    @Override // com.eltechs.axs.xserver.GraphicsContext
    public int getBackground() {
        return this.background;
    }

    public void setBackground(int i) {
        this.background = i;
    }

    @Override // com.eltechs.axs.xserver.GraphicsContext
    public SubwindowMode getSubwindowMode() {
        return this.subwindowMode;
    }

    public void setSubwindowMode(SubwindowMode subwindowMode) {
        this.subwindowMode = subwindowMode;
    }

    @Override // com.eltechs.axs.xserver.GraphicsContext
    public int getLineWidth() {
        return this.lineWidth;
    }

    public void setLineWidth(int i) {
        this.lineWidth = i;
    }
}
