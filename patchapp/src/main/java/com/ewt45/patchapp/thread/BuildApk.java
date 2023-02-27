package com.ewt45.patchapp.thread;

import android.util.Log;

import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;

import java.io.File;

import brut.androlib.Androlib;
import brut.androlib.options.BuildOptions;
import brut.common.BrutException;

public class BuildApk implements Action {

    String TAG="BuildApk";

    @Override
    public Integer call() throws Exception {
        try {
            Log.d(TAG, "run: 开始回编译apk");
            BuildOptions options = new BuildOptions();
            options.debugMode=true;
            new Androlib(options).build(new File(PatchUtils.getPatchTmpDir(), "tmp"), null);
        } catch (BrutException e) {
            e.printStackTrace();
        }
        return R.string.actmsg_buildapk;
    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_buildapk;
    }
}
