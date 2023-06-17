
package com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters;

import com.eltechs.axs.GuestAppActionAdapters.DragAndDropAdapter;
import com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;

/**
 * 与SimpleDragAndDropAdapter一致但初始时不会进行一次点击
 * 用于触摸板模式，已经点过一次左键的情况下，如果再次快速按下，那么应该进入拖拽。
 */
public class DragAndDropAdapterRel implements DragAndDropAdapter {
    private final Runnable cancellationHandler;
    private final MouseClickAdapter clicker;
    private final MouseMoveAdapter mover;

    public DragAndDropAdapterRel(MouseMoveAdapter mouseMoveAdapter, MouseClickAdapter mouseClickAdapter, Runnable runnable) {
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
