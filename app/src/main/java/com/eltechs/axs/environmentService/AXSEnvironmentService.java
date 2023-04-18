package com.eltechs.axs.environmentService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.SwitchToAxsFromSystemTrayActivity;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.container.annotations.PreRemove;
import com.eltechs.axs.environmentService.AXSEnvironment;
import com.example.datainsert.exagear.QH;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class AXSEnvironmentService extends Service {
    private static final String TAG= "AXSEnvironmentService";
    private final List<EnvironmentComponent> startedComponents = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: intent应该有创建吧");
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
        StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX);
        EnvironmentAware environmentAware = Globals.getApplicationState();
        AXSEnvironment.StartupCallback startupCallback = environmentAware.getEnvironment().startupCallback;
        environmentAware.setEnvironmentServiceInstance(this);
        try {
            startEnvironmentComponents();
            startupCallback.serviceStarted();
            configureAsForegroundService();
            return Service.START_NOT_STICKY;
        } catch (IOException e) {
            //自己的代码里并不能正常启动，但是也不能停止
            Log.e(TAG, "onStartCommand: 现在还会报错嘛" );
            if(QH.isTesting()){
                startupCallback.serviceStarted();
                configureAsForegroundService();
                return Service.START_NOT_STICKY;
            }
            stopSelf();
            environmentAware.setEnvironmentServiceInstance(QH.isTesting()?this:null);
            startupCallback.serviceFailed(e);
            return Service.START_NOT_STICKY;
        }
    }

    @PreRemove
    private void destroy() {
        shutdownComponents();
        stopSelf();
    }

    @Override // android.app.Service
    public void onDestroy() {
        shutdownComponents();
        super.onDestroy();
    }

    private AXSEnvironment getEnvironment() {
        return ((EnvironmentAware) Globals.getApplicationState()).getEnvironment();
    }

    private void startEnvironmentComponents() throws IOException {
        try {
            Iterator<EnvironmentComponent> it = getEnvironment().iterator();
            while (it.hasNext()) {
                EnvironmentComponent next = it.next();
                next.start();
                this.startedComponents.add(0, next);
            }
        } catch (IOException e) {
            for (EnvironmentComponent environmentComponent : this.startedComponents) {
                environmentComponent.stop();
            }
            this.startedComponents.clear();
            throw e;
        }
    }

    private void shutdownComponents() {
        for (EnvironmentComponent environmentComponent : this.startedComponents) {
            environmentComponent.stop();
        }
        this.startedComponents.clear();
        EnvironmentAware environmentAware = (EnvironmentAware) Globals.getApplicationState();
        if (environmentAware != null) {
            environmentAware.setEnvironmentServiceInstance(null);
        }
    }

    private void configureAsForegroundService() {
        TrayConfiguration trayConfiguration = getEnvironment().trayConfiguration;
        Intent intent = new Intent(this, SwitchToAxsFromSystemTrayActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            notificationManager.createNotificationChannel(new NotificationChannel("notification_channel_id", "ExaGear", NotificationManager.IMPORTANCE_MIN));
        }
        Notification build = new NotificationCompat.Builder(this, "notification_channel_id")
                .setSmallIcon(trayConfiguration.getTrayIcon())
                .setContentText(getResources().getText(trayConfiguration.getReturnToDescription()))
                .setContentTitle(getResources().getText(trayConfiguration.getTrayIconName()))
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .build();
        notificationManager.notify(1, build);
        startForeground(1, build);
    }
}