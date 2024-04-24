package com.eltechs.axs.guestApplicationsTracker.impl;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.SingleshotTimer;
import java.io.IOException;

/* loaded from: classes.dex */
public class Translator {
    private static final int TIME_TO_CONNECT = 750;
    private TranslatorConnection connection;
    private final SingleshotTimer connectionTimeoutTracker = new SingleshotTimer();
    private boolean isForking;
    private final GuestApplicationsCollection owner;
    private final int pid;

    public Translator(GuestApplicationsCollection owner, int pid) {
        this.owner = owner;
        this.pid = pid;
        scheduleDestructionIfNoConnectionIsMade();
    }

    public int getPid() {
        return this.pid;
    }

    public void connectionEstablished(TranslatorConnection translatorConnection) {
        this.connectionTimeoutTracker.cancel();
        this.connection = translatorConnection;
        translatorConnection.associate(this);
    }

    public void connectionLost(TranslatorConnection connection) {
        Assert.state(connection != null);
        if (this.connection == connection) {
            this.connection = null;
            scheduleDestructionIfNoConnectionIsMade();
        }
    }

    private void scheduleDestructionIfNoConnectionIsMade() {
        this.connectionTimeoutTracker.schedule(TIME_TO_CONNECT, () -> owner.killTranslator(Translator.this));
    }

    public void forkRequested() {
        synchronized (this.owner) {
            sendEmptyPacket();
            this.isForking = true;
        }
    }

    public void forkDone(int i) {
        synchronized (this.owner) {
            if (i > 0) {
                try {
                    this.owner.registerTranslator(i);
                } catch (Throwable th) {
                    throw th;
                }
            }
            sendEmptyPacket();
            this.isForking = false;
        }
    }

    public boolean isForking() {
        return this.isForking;
    }

    public void sendEmptyPacket() {
        try {
            this.connection.sendEmptyPacket();
        } catch (IOException e) {
            e.printStackTrace();
            this.owner.killTranslator(this);
        }
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    public String toString() {
        return String.format("ubt[%d]", this.pid);
    }
}