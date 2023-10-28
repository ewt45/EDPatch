package com.ewt45.patchapp.widget;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ewt45.patchapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
            PackageManager pm = requireContext().getPackageManager()    ;
            List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo info : apps) {
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //非系统应用
                    mAppsInfo.add(new SelectAppInfo(info.loadLabel(pm).toString(),info.sourceDir,info.loadIcon(pm)));
                }
            }
            Collections.sort(mAppsInfo); //排一下序吧受不了了
            changeViewOnUIThread();
        }).start();
    }

    /**
     * 因为新开了线程获取apk名称，所以修改view的时候需要在uithread完成，使用注解即可（阿这注解不好使）
     */
    private void changeViewOnUIThread(){
        rootView.post(()->{
//            ArrayAdapter<SelectAppInfo> adapter = new ArrayAdapter<>
//                    (requireContext(), android.R.layout.simple_expandable_list_item_1, mAppsInfo);
            ListAdapter listAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return mAppsInfo.size();
                }

                @Override
                public Object getItem(int position) {
                    return mAppsInfo.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View itemView = (convertView!=null && convertView.getId()==R.id.app_name_view_root)
                            ?convertView : LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_name,parent,false);
                    ((ImageView)itemView.findViewById(R.id.app_icon)).setImageDrawable(mAppsInfo.get(position).icon);
                    ((TextView)itemView.findViewById(R.id.app_name)).setText(mAppsInfo.get(position).name);
                    return itemView;
                }
            };
            ListView listView = rootView.findViewById(R.id.dialog_listview);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Log.d(TAG, "onClick: dialog点击的项目为：" + mAppsInfo.get(position));
                mCallback.deployEDApk(new File(mAppsInfo.get(position).getSourceDir()));
                dismiss();
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
        Drawable icon;
        public SelectAppInfo(String name, String sourceDir, Drawable drawable){
            this.name = name;
            this.sourceDir = sourceDir;
            this.icon = drawable;
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
            return name.toLowerCase(Locale.ROOT).compareTo(o.name.toLowerCase(Locale.ROOT));//忽略大小写，不然小写全在大写下面了
        }
    }
}
