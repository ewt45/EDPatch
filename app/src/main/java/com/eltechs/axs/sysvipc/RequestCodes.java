package com.eltechs.axs.sysvipc;

/* loaded from: classes.dex */
public enum RequestCodes {
    SHMGET(0),
    SHM_GET_SIZE_AND_FD(1),
    SHM_GET_SIZE(2),
    SHM_GET_STAT(3),
    SHM_RMID(4);

    private final int code;

    RequestCodes(int i) {
        this.code = i;
    }

    public int getCode() {
        return this.code;
    }
}