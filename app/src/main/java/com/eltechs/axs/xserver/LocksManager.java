package com.eltechs.axs.xserver;

public interface LocksManager {

    /* loaded from: classes.dex */
    public enum Subsystem {
        INPUT_DEVICES,
        WINDOWS_MANAGER,
        PIXMAPS_MANAGER,
        DRAWABLES_MANAGER,
        ATOMS_MANAGER,
        GRAPHICS_CONTEXTS_MANAGER,
        CURSORS_MANAGER,
        COLORMAPS_MANAGER,
        SHM_SEGMENTS_MANAGER,
        KEYBOARD_MODEL_MANAGER,
        FOCUS_MANAGER,
        DESKTOP_EXPERIENCE,
        ID_INTERVALS_MANAGER,
        SELECTIONS_MANAGER,
        SCREEN_INFO,
        RENDERING_ENGINE
    }

    /* loaded from: classes.dex */
    public interface XLock extends AutoCloseable {
        @Override // java.lang.AutoCloseable
        void close();
    }

    XLock lock(Subsystem subsystem);

    XLock lock(Subsystem[] subsystemArr);

    XLock lockAll();

    XLock lockForInputDevicesManipulation();
}
