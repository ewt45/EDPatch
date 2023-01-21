package com.ewt45.patchapp.widget;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ewt45.patchapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SelectApkDialog extends DialogFragment {
    List<String> mFilterApps;
    List<String> mAppNames;
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
            mFilterApps = new ArrayList<>();
            mAppNames = new ArrayList<>();
            for (ApplicationInfo info : apps) {
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //非系统应用
                    mFilterApps.add(info.sourceDir);
                    mAppNames.add(requireContext().getPackageManager().getApplicationLabel(info).toString());
                }
            }
            changeViewOnUIThread();
        }).start();
    }

    /**
     * 因为新开了线程获取apk名称，所以修改view的时候需要在uithread完成，使用注解即可（阿这注解不好使）
     */
    private void changeViewOnUIThread(){
        rootView.post(()->{
            ArrayAdapter<String> adapter = new ArrayAdapter<>
                    (requireContext(), android.R.layout.simple_expandable_list_item_1, mAppNames);

            ListView listView = rootView.findViewById(R.id.dialog_listview);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onClick: dialog点击的项目为：" + mFilterApps.get(position));
                    mCallback.deployEDApk(new File(mFilterApps.get(position)));
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
}
