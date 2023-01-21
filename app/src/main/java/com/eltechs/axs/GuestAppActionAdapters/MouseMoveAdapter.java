package com.eltechs.axs.GuestAppActionAdapters;

public interface MouseMoveAdapter {
    public static final MouseMoveAdapter dummy = new MouseMoveAdapter() { // from class: com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter.1
        @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
        public void moveTo(float f, float f2) {
        }

        @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
        public void prepareMoving(float f, float f2) {
        }
    };

    void moveTo(float f, float f2);

    void prepareMoving(float f, float f2);

}
