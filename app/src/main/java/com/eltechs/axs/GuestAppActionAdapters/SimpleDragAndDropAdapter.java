package com.eltechs.axs.GuestAppActionAdapters;

/* loaded from: classes.dex */
public class SimpleDragAndDropAdapter implements DragAndDropAdapter {
    private final Runnable cancellationHandler;
    private final MouseClickAdapter clicker;
    private final MouseMoveAdapter mover;

    public SimpleDragAndDropAdapter(MouseMoveAdapter mouseMoveAdapter, MouseClickAdapter mouseClickAdapter, Runnable runnable) {
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
