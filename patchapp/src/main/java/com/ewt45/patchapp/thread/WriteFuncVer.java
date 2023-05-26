package com.ewt45.patchapp.thread;

import android.util.Log;

import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.patching.PatcherFile;

import java.io.File;
import java.util.Map;

/**
 * @deprecated 还是不要用这种方法了。如果别人没用EDPatch，手改的话就不会添加txt。还是把版本号写到java类里吧
 */
@Deprecated
public class WriteFuncVer implements Action{
    private final static String TAG="WriteFuncVer";
    private final Map<Func,Integer> mFuncList;
    public WriteFuncVer(Map<Func,Integer> mFuncList){
        this.mFuncList = mFuncList;
    }

    @Override
    public int getStartMessage() {
        return 0;
    }

    @Override
    public Integer call() throws Exception {
        Log.d(TAG, "call: 写入已安装功能的信息到apk中 ");
        PatcherFile.writeAddedFunVer(mFuncList);//写入本次安装功能的版本号

        return 0;
    }
}
