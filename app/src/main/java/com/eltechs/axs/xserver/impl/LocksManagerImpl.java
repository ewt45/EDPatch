package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.xserver.LocksManager;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class LocksManagerImpl implements LocksManager {
    private static final LocksManager.Subsystem[] locksOnInputDevices = {LocksManager.Subsystem.INPUT_DEVICES, LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.KEYBOARD_MODEL_MANAGER, LocksManager.Subsystem.FOCUS_MANAGER};
    private final EnumMap<LocksManager.Subsystem, ReentrantLock> locks = new EnumMap<>(LocksManager.Subsystem.class);

    public LocksManagerImpl() {
        for (LocksManager.Subsystem subsystem : LocksManager.Subsystem.values()) {
            this.locks.put( subsystem,new ReentrantLock());
        }
    }

    public boolean isLocked(LocksManager.Subsystem subsystem) {
        return this.locks.get(subsystem).isLocked();
    }

    @Override // com.eltechs.axs.xserver.LocksManager
    public LocksManager.XLock lock(LocksManager.Subsystem subsystem) {
        return new SingleXLock(Objects.requireNonNull(this.locks.get(subsystem)));
    }

    @Override // com.eltechs.axs.xserver.LocksManager
    public LocksManager.XLock lock(LocksManager.Subsystem[] subsystemArr) {
        if (subsystemArr.length != 0) {
            if (subsystemArr.length == 1) {
                return lock(subsystemArr[0]);
            }
            Arrays.sort(subsystemArr);
            return new MultiXLock(subsystemArr);
        }
        return NullXLock.INSTANCE;
    }

    @Override // com.eltechs.axs.xserver.LocksManager
    public LocksManager.XLock lockForInputDevicesManipulation() {
        return new MultiXLock(locksOnInputDevices);
    }

    @Override // com.eltechs.axs.xserver.LocksManager
    public LocksManager.XLock lockAll() {
        return lock(LocksManager.Subsystem.values());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    private static final class SingleXLock implements LocksManager.XLock {
        private final ReentrantLock lock;

        public SingleXLock(ReentrantLock reentrantLock) {
            this.lock = reentrantLock;
            reentrantLock.lock();
        }

        @Override // com.eltechs.axs.xserver.LocksManager.XLock, java.lang.AutoCloseable
        public void close() {
            this.lock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    private final class MultiXLock implements LocksManager.XLock {
        private final LocksManager.Subsystem[] systems;

        public MultiXLock(LocksManager.Subsystem[] subsystemArr) {
            this.systems = subsystemArr;
            for (LocksManager.Subsystem subsystem : subsystemArr) {
                LocksManagerImpl.this.locks.get(subsystem).lock();
            }
        }

        @Override // com.eltechs.axs.xserver.LocksManager.XLock, java.lang.AutoCloseable
        public void close() {
            for (int length = this.systems.length - 1; length >= 0; length--) {
                ((ReentrantLock) ((EnumMap) LocksManagerImpl.this.locks).get(this.systems[length])).unlock();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    private static final class NullXLock implements LocksManager.XLock {
        private static final LocksManager.XLock INSTANCE = new NullXLock();

        @Override // com.eltechs.axs.xserver.LocksManager.XLock, java.lang.AutoCloseable
        public void close() {
        }

        private NullXLock() {
        }
    }

}
