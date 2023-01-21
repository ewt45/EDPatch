package com.ewt45.patchapp.unused;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GetInstalledApk  {
    private static  GetInstalledApk mInstance;
    List<String> dataList = new ArrayList<>();
    PackageManager packageManager;
    ExecutorService pool = Executors.newSingleThreadExecutor();
    boolean working = false;
    Callback mCallback;
    private GetInstalledApk(Context context, Callback callback){
        packageManager = context.getPackageManager();
        mCallback = callback;
    }
    public static GetInstalledApk Instance(Context context,Callback call){
        if(mInstance==null){
            mInstance = new GetInstalledApk(context,call);
        }
        return mInstance;
    }

    public void get(){
        if(!working){
           working=true;
           Future<List<String>> future = pool.submit(new Callable<List<String>>() {
               @Override
               public List<String> call() {
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
                   return dataList;
               }
           });
           pool.submit(new Runnable() {
               @Override
               public void run() {
                   try {
                       List<String> list = future.get();
                       working = false;
                       mCallback.showList(list);
                   } catch (ExecutionException | InterruptedException e) {
                       e.printStackTrace();
                   }
               }
           });
        }

    }
    public interface Callback{
        public void showList(List<String> list);
    }
}
