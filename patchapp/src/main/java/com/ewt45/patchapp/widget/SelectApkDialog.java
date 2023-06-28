package com.ewt45.patchapp.widget;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ewt45.patchapp.R;
import com.google.common.collect.Collections2;
import com.google.common.collect.Comparators;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectApkDialog extends DialogFragment {
//    List<String> mFilterApps;
//    List<String> mAppNames;
    List<SelectAppInfo> mAppsInfo= new ArrayList<>();
    String TAG = "SelectApkDialog";
    Callback mCallback;
    View rootView;
    ProgressBar progressBar;
    ListView listView;
    public interface Callback{
        public void deployEDApk(File file);
    }

    public SelectApkDialog  setCallback( Callback callback){
        mCallback = callback;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        rootView = ((LayoutInflater)requireContext().getSystemService(LAYOUT_INFLATER_SERVICE )).inflate(R.layout.dialog_installedapk,null);
        rootView.findViewById(R.id.dialog_progressbar).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.dialog_listview).setVisibility(View.GONE);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(rootView)
//                .setItems(appNames.toArray(new String[0]), listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        setCancelable(false);
        prepareApkList();
        return dialog;
    }


    /**
     * 新开线程获取apk名称（新线程设置成员变量，回到ui线程修改view）
     */
    private void prepareApkList(){
        new Thread(()->{
            List<ApplicationInfo> apps = requireContext().getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo info : apps) {
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //非系统应用
                    mAppsInfo.add(new SelectAppInfo(requireContext().getPackageManager().getApplicationLabel(info).toString(),info.sourceDir));
                }
            }
            Collections.sort(mAppsInfo);
            //排一下序吧受不了了
//            String[] apkLocations = mFilterApps.toArray(new String[0]);
//            String[] appNames = mAppNames.toArray(new String[0]);
//            Arrays.sort(appNames, new Comparator<String>() {
//                private Collator comparator = Collator.getInstance();
//                @Override
//                public int compare(String o1, String o2) {
//                    return 0;
//                }
//            });

            changeViewOnUIThread();
        }).start();
    }

    /**
     * 因为新开了线程获取apk名称，所以修改view的时候需要在uithread完成，使用注解即可（阿这注解不好使）
     */
    private void changeViewOnUIThread(){
        rootView.post(()->{
            ArrayAdapter<SelectAppInfo> adapter = new ArrayAdapter<>
                    (requireContext(), android.R.layout.simple_expandable_list_item_1, mAppsInfo);

            ListView listView = rootView.findViewById(R.id.dialog_listview);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onClick: dialog点击的项目为：" + mAppsInfo.get(position));
                    mCallback.deployEDApk(new File(mAppsInfo.get(position).getSourceDir()));
                    dismiss();
                }
            });
            //准备好之后显示listview
            listView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.dialog_progressbar).setVisibility(View.GONE);
            Log.d(TAG, "prepareApkList: 获取已安装应用列表 完成");
        });

    }

    int FLAG_WAITING = 0;
    int FLAG_LIST = 1;
    private void setView(int flag){
        if (FLAG_WAITING == flag){
        }else{

        }
    }
    private static class SelectAppInfo implements Comparable<SelectAppInfo>{
        String name;
        String sourceDir;
        public SelectAppInfo(String name, String sourceDir){
            this.name = name;
            this.sourceDir = sourceDir;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }

        public String getSourceDir() {
            return sourceDir;
        }

        @Override
        public int compareTo(SelectAppInfo o) {
            return name.compareTo(o.name);
        }
    }
}
