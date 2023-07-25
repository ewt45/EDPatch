package com.example.datainsert.exagear.obb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AtomicFile;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.CurrentActivityAware;
import com.eltechs.axs.helpers.ZipInstallerObb;
import com.eltechs.ed.BuildConfig;
import com.eltechs.ed.R;
import com.eltechs.ed.activities.EDStartupActivity;
import com.ewt45.exagearsupportv7.ui.home.HomeFragment;
import com.example.datainsert.exagear.QH;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ProcessInstallObb {
    static String TAG = "ProcessInstallObb";
    private static final int VERSION_FOR_EDPATCH = 2;

    /**
     * 检查是否需要解压数据包，如果需要是否存在，如果不存在显示fragment
     * @param zipInstallerObb 实例
     */
    public static void start(ZipInstallerObb zipInstallerObb){
        Log.d(TAG, "start: 开始新建选择obb的fragment");
        CurrentActivityAware applicationStateBase = Globals.getApplicationState();
        if(applicationStateBase==null){
            return;
        }
        FrameworkActivity<?> edStartupActivity = applicationStateBase.getCurrentActivity();
        //防止多次添加
        SelectObbFragment fragment= (SelectObbFragment) edStartupActivity.getSupportFragmentManager().findFragmentByTag(SelectObbFragment.TAG);
        if(fragment==null){
            fragment= new SelectObbFragment();
            fragment.setZipInstallerObb(zipInstallerObb);
            edStartupActivity.getSupportFragmentManager().beginTransaction()
                    .add(QH.rslvID(R.id.startupAdButtons,0x7f0900f2),fragment,SelectObbFragment.TAG).commit();
        }
        //外布局设置成显示，否则隐藏状态下替换fragment也不会显示
        ViewGroup startupAdButtons = edStartupActivity.findViewById(QH.rslvID(R.id.startupAdButtons,0x7f0900f2));
        startupAdButtons.setVisibility(View.VISIBLE);
        //清空原有子布局,如果遇到fragment保留
        int i=0;
        while(i<startupAdButtons.getChildCount()){
            View child = startupAdButtons.getChildAt(i);
            if(child.getTag()==null || !child.getTag().equals(SelectObbFragment.TAG))
                startupAdButtons.removeView(child);
            else i++;
        }
        //显示fragment
        edStartupActivity.getSupportFragmentManager().beginTransaction().show(fragment).addToBackStack(null).commit();



        //高度怎么不wrapcontent了呢
//        startupAdButtons.requestLayout();
//        startupAdButtons.invalidate();
    }

    /**
     * 在原本的副文本显示区显示文字
     */
    private static void showTextOnTop(Context edStartupActivity){
        //尝试将文字显示到上方原本的显示区
        final File progressFile = new File(edStartupActivity.getFilesDir(), "ed_progress");
        try {
            if (progressFile.exists() && !progressFile.delete())
                return;
            if(!progressFile.createNewFile())
                return;

            AtomicFile atomicFile = new AtomicFile(progressFile);
            FileOutputStream startWrite = atomicFile.startWrite();
            String pkgName = edStartupActivity.getPackageName();
            startWrite.write((-1 + IOUtils.LINE_SEPARATOR_UNIX + String.format(Locale.getDefault(),"未找到obb数据包。数据包应位于%s/%s/main.%d.%s.obb。若不会放数据包，可以点击按钮手动定位找到obb文件（该方式无法查看Android/obb或data目录）",edStartupActivity.getObbDir().getAbsolutePath(),pkgName,BuildConfig.VERSION_CODE,pkgName)).getBytes());
            atomicFile.finishWrite(startWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startest(HomeFragment homeFragment){
        SelectObbFragment fragment = (SelectObbFragment) homeFragment.requireActivity().getSupportFragmentManager().findFragmentByTag(SelectObbFragment.TAG);
        //防止多次添加
        if(fragment==null){
            fragment=  new SelectObbFragment();
            fragment.setZipInstallerObb(new ZipInstallerObb(null,false ,false,null,null,null));
            homeFragment.requireActivity().getSupportFragmentManager().beginTransaction()
                    .add(QH.rslvID(R.id.startupAdButtons,0x7f0900f2),fragment,SelectObbFragment.TAG).commit();
        }
        //外布局设置成显示，否则隐藏状态下替换fragment也不会显示
        ViewGroup startupAdButtons = homeFragment.requireActivity().findViewById(QH.rslvID(R.id.startupAdButtons,0x7f0900f2));
        startupAdButtons.setVisibility(View.VISIBLE);
        //清空原有子布局,如果遇到fragment保留
        while(startupAdButtons.getChildCount()>0){
            View child = startupAdButtons.getChildAt(0);
            if(child.getTag()==null || !child.getTag().equals(SelectObbFragment.TAG))
                startupAdButtons.removeView(child);
        }
        //显示fragment
        homeFragment.requireActivity().getSupportFragmentManager().beginTransaction().show(fragment).addToBackStack(null).commit();
    }


}
