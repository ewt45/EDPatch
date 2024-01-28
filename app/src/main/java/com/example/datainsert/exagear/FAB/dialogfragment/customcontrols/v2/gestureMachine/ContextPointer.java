package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine;


public class ContextPointer {
    private MoveMethod lastMoveMethod = MoveMethod.NOT_INIT;
    private long lastMoveTimestamp;

    /* loaded from: classes.dex */
    public enum MoveMethod {
        TAP,
        AIM,
        NOT_INIT
    }

    public long getLastMoveTimestamp() {
        return this.lastMoveTimestamp;
    }

    public MoveMethod getLastMoveMethod() {
        return this.lastMoveMethod;
    }

    public void setLastMoveMethod(MoveMethod moveMethod) {
        this.lastMoveMethod = moveMethod;
        this.lastMoveTimestamp = System.currentTimeMillis();
    }
}

