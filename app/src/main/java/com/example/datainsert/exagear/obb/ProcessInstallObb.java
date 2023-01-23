package com.example.datainsert.exagear.obb;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.helpers.ZipInstallerObb;
import com.ewt45.exagearsupportv7.R;
import com.ewt45.exagearsupportv7.ui.home.HomeFragment;
import com.example.datainsert.exagear.RSIDHelper;

import java.io.File;

public class ProcessInstallObb {
    static String TAG = "ProcessInstallObb";

    /**
     * 检查是否需要解压数据包，如果需要是否存在，如果不存在显示fragment
     * @param zipInstallerObb 实例
     */
    public static void start(ZipInstallerObb zipInstallerObb){
        Log.d(TAG, "start: 开始新建选择obb的fragment");
        ApplicationStateBase applicationStateBase = (ApplicationStateBase) Globals.getApplicationState();
        if(applicationStateBase==null){
            Log.d(TAG, "start: Globals.getApplicationState()为null，无法获取acitivity");
            return;
        }
        FrameworkActivity edStartupActivity = applicationStateBase.getCurrentActivity();
        //防止多次添加
        SelectObbFragment fragment= (SelectObbFragment) edStartupActivity.getSupportFragmentManager().findFragmentByTag(SelectObbFragment.TAG);
        if(fragment==null){
            fragment= new SelectObbFragment();
            fragment.setZipInstallerObb(zipInstallerObb);
            edStartupActivity.getSupportFragmentManager().beginTransaction()
                    .add(RSIDHelper.rslvID(R.id.startupAdButtons,0x7f0900f2),fragment,SelectObbFragment.TAG).commit();
        }
        //外布局设置成显示，否则隐藏状态下替换fragment也不会显示
        ViewGroup startupAdButtons = edStartupActivity.findViewById(RSIDHelper.rslvID(R.id.startupAdButtons,0x7f0900f2));
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

    public static void startest(HomeFragment homeFragment){
        SelectObbFragment fragment = (SelectObbFragment) homeFragment.requireActivity().getSupportFragmentManager().findFragmentByTag(SelectObbFragment.TAG);
        //防止多次添加
        if(fragment==null){
            fragment=  new SelectObbFragment();
            fragment.setZipInstallerObb(new ZipInstallerObb(null,false ,false,null,null,null));
            homeFragment.requireActivity().getSupportFragmentManager().beginTransaction()
                    .add(RSIDHelper.rslvID(R.id.startupAdButtons,0x7f0900f2),fragment,SelectObbFragment.TAG).commit();
        }
        //外布局设置成显示，否则隐藏状态下替换fragment也不会显示
        ViewGroup startupAdButtons = homeFragment.requireActivity().findViewById(RSIDHelper.rslvID(R.id.startupAdButtons,0x7f0900f2));
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
