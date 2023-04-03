package com.eltechs.axs.GuestAppActionAdapters;

/* loaded from: classes.dex */
public interface MouseMoveAdapter {
    public static final MouseMoveAdapter dummy = new MouseMoveAdapter() { // from class: com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter.1
        @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
        public void moveTo(float x, float y) {
        }

        @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
        public void prepareMoving(float x, float y) {
        }
    };

    void moveTo(float x, float y);

    void prepareMoving(float x, float y);
}
