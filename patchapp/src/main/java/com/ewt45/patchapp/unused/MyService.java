package com.ewt45.patchapp.unused;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MyService extends Service {


    List<String> dataList = new ArrayList<>();
    
    @Override
    public void onCreate() {
        super.onCreate();
        //todo: 广播获取

        PackageManager packageManager = this.getPackageManager();
        List<PackageInfo> infos = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        dataList.clear();
        for (int i = 0; i < infos.size(); i++) {
            PackageInfo packageInfo = infos.get(i);
            //拿到应用程序的信息
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            //拿到应用程序的程序名
            String appName = packageManager.getApplicationLabel(appInfo).toString();
//            Log.d("TAG", "onCreate: "+"----"+appName);

            dataList.add(appName);
//            //拿到应用程序的图标
//            Drawable icon = packageManager.getApplicationIcon(appInfo);
//            DataBean dataBean = new DataBean(icon, appName);
//            dataBeans.add(dataBean);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends Binder {
        public List<String> getData() {
//            Log.d("TAG", "getData: "+dataBeans.size());
            return dataList;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}

