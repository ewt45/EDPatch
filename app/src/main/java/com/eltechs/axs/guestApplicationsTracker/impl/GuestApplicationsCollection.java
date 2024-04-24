package com.eltechs.axs.guestApplicationsTracker.impl;

import android.annotation.SuppressLint;
import android.util.Log;

import com.eltechs.axs.guestApplicationsTracker.GuestApplicationsLifecycleListener;
import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class GuestApplicationsCollection {
    private static final String TAG = "GuestAppCollection";
    private final Collection<Translator> translators = new ArrayList<>();
    private final GuestApplicationLifecycleListenersList listeners = new GuestApplicationLifecycleListenersList();
    private boolean guestApplicationsAreRunnable = true;

    public synchronized void addListener(GuestApplicationsLifecycleListener listener) {
        this.listeners.addListener(listener);
    }

    public synchronized void removeListener(GuestApplicationsLifecycleListener listener) {
        this.listeners.removeListener(listener);
    }

    public synchronized Translator registerTranslator(int pid) {
        Translator translator = getTranslator(pid);
        if (translator == null) {
            translator = new Translator(this, pid);
            this.translators.add(translator);
            this.listeners.sendTranslatorStarted(translator);
            Log.d(TAG, "registerTranslator: 这里translator被添加"+ translator);
        }
        return translator;
    }

    public synchronized void translatorStarted(int pid, TranslatorConnection translatorConnection) {
        Translator registerTranslator = registerTranslator(pid);
        registerTranslator.connectionEstablished(translatorConnection);
        registerTranslator.sendEmptyPacket();
    }

    public synchronized void killTranslator(Translator translator) {
        ProcessHelpers.killProcess(translator.getPid());
        if (this.translators.remove(translator)) {
            this.listeners.sendTranslatorExited(translator);
            Log.d(TAG, "killTranslator: 这里translator被删除一个"+translator);
        }
    }

    private Translator getTranslator(int pid) {
        for (Translator translator : this.translators)
            if (translator.getPid() == pid)
                return translator;
        return null;
    }

    public synchronized boolean haveGuestApplications() {
        return !this.translators.isEmpty();
    }

    public void freezeGuestApplications() {
        while (true) {
            synchronized (this) {
                if (canFreeze()) {
                    break;
                }
            }
            this.yield();
        }
        for (Translator translator : this.translators) {
            ProcessHelpers.suspendProcess(translator.getPid());
        }
        this.guestApplicationsAreRunnable = false;
    }

    public synchronized void resumeGuestApplications() {
        if (this.guestApplicationsAreRunnable) {
            return;
        }
        for (Translator translator : this.translators) {
            ProcessHelpers.resumeProcess(translator.getPid());
        }
        this.guestApplicationsAreRunnable = true;
    }

    public synchronized void killGuestApplications() {
        Log.d(TAG, "killGuestApplications: 这里translator被清空");
        ArrayList<Translator> arrayList = new ArrayList<>(this.translators);
        this.translators.clear();
        for (Translator translator : arrayList) {
            ProcessHelpers.killProcess(translator.getPid());
            this.listeners.sendTranslatorExited(translator);
        }
    }

    private boolean canFreeze() {
        for (Translator translator : this.translators) {
            if (translator.isForking()) {
                return false;
            }
        }
        return true;
    }

    private void yield() {
        sleep(10);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}