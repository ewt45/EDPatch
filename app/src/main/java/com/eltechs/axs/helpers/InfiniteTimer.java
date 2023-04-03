package com.eltechs.axs.helpers;

import android.os.CountDownTimer;

/* loaded from: classes.dex */
public abstract class InfiniteTimer extends CountDownTimer {
    private static long veryLongPeriod = 86400000;

    public InfiniteTimer(long j) {
        super(veryLongPeriod, j);
    }

    @Override // android.os.CountDownTimer
    public final void onFinish() {
        start();
    }
}