
package com.example.datainsert.exagear.controls.interfaceOverlay.GuestAppActionAdapters;

import com.eltechs.axs.GuestAppActionAdapters.DragAndDropAdapter;
import com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;

/* loaded from: classes.dex */
public class RelativeDragAndDropAdapter implements DragAndDropAdapter {
    private final Runnable cancellationHandler;
    private final MouseClickAdapter clicker;
    private final MouseMoveAdapter mover;

    public RelativeDragAndDropAdapter(MouseMoveAdapter mouseMoveAdapter, MouseClickAdapter mouseClickAdapter, Runnable runnable) {
        this.mover = mouseMoveAdapter;
        this.clicker = mouseClickAdapter;
        this.cancellationHandler = runnable;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.DragAndDropAdapter
    public void start(float f, float f2) {
        this.mover.prepareMoving(f, f2);

        this.mover.moveTo(f, f2);
        this.clicker.click();
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.DragAndDropAdapter
    public void move(float f, float f2) {
        this.mover.moveTo(f, f2);
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.DragAndDropAdapter
    public void stop(float f, float f2) {
        this.clicker.finalizeClick();
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.DragAndDropAdapter
    public void cancel(float f, float f2) {
        this.cancellationHandler.run();
    }
}
