package com.eltechs.axs.xserver.impl.drawables;

import java.util.Collection;
import java.util.Collections;

/* loaded from: classes.dex */
public abstract class DrawablesFactoryImplBase implements DrawablesFactory {
    private final Visual preferredVisual;
    private final Collection<ImageFormat> supportedImageFormats;
    private final Collection<Visual> supportedVisuals;

    public DrawablesFactoryImplBase(Collection<Visual> collection, Collection<ImageFormat> collection2, Visual visual) {
        this.preferredVisual = visual;
        this.supportedVisuals = collection;
        this.supportedImageFormats = collection2;
    }

    @Override // com.eltechs.axs.xserver.impl.drawables.DrawablesFactory
    public final Collection<Visual> getSupportedVisuals() {
        return Collections.unmodifiableCollection(this.supportedVisuals);
    }

    @Override // com.eltechs.axs.xserver.impl.drawables.DrawablesFactory
    public final Collection<ImageFormat> getSupportedImageFormats() {
        return Collections.unmodifiableCollection(this.supportedImageFormats);
    }

    @Override // com.eltechs.axs.xserver.impl.drawables.DrawablesFactory
    public final Visual getVisual(int i) {
        if (i == this.preferredVisual.getId()) {
            return this.preferredVisual;
        }
        for (Visual visual : this.supportedVisuals) {
            if (i == visual.getId() && visual.isDisplayable()) {
                return visual;
            }
        }
        return null;
    }

    @Override // com.eltechs.axs.xserver.impl.drawables.DrawablesFactory
    public final Visual getPreferredVisual() {
        return this.preferredVisual;
    }

    @Override // com.eltechs.axs.xserver.impl.drawables.DrawablesFactory
    public final Visual getPreferredVisualForDepth(int i) {
        if (i == this.preferredVisual.getDepth()) {
            return this.preferredVisual;
        }
        for (Visual visual : this.supportedVisuals) {
            if (i == visual.getDepth()) {
                return visual;
            }
        }
        return null;
    }
}
