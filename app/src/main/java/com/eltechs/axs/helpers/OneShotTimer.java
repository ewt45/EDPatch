package com.eltechs.axs.helpers;

import android.os.CountDownTimer;

/* loaded from: classes.dex */
public abstract class OneShotTimer extends CountDownTimer {
    @Override // android.os.CountDownTimer
    public final void onTick(long j) {
    }

    public OneShotTimer(long j) {
        super(j, 10 * j);
    }
}