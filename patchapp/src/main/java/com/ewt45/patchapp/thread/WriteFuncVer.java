package com.ewt45.patchapp.thread;

import android.util.Log;

import com.ewt45.patchapp.patching.PatcherFile;

import java.util.List;

public class WriteFuncVer implements Action{
    private final static String TAG="WriteFuncVer";
    private final List<Func> mFuncList;
    public WriteFuncVer(List<Func> mFuncList){
        this.mFuncList = mFuncList;
    }

    @Override
    public int getStartMessage() {
        return 0;
    }

    @Override
    public Integer call() throws Exception {
        Log.d(TAG, "call: 写入已安装功能的信息到apk中");
        PatcherFile.writeAddedFunVer(mFuncList);//写入本次安装功能的版本号
        return 0;
    }
}
