package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.PatchUtils.getPatchTmpDir;

import static brut.androlib.ApkDecoder.DECODE_RESOURCES_FULL;
import static brut.androlib.ApkDecoder.DECODE_RESOURCES_NONE;
import static brut.androlib.ApkDecoder.DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES;

import android.util.Log;

import com.ewt45.patchapp.R;

import java.io.File;

import brut.androlib.ApkDecoder;

public class DecodeApk implements Action {

    static final String TAG = "DecodeApk";
    public static final int PATCHER= 0;
    public static final int EXAGEAR= 1;
    private final String apkName;
    public DecodeApk(int which){
        apkName = which ==PATCHER?"patcher":"tmp";
    }
    @Override
    public Integer call() throws Exception {

        try {
            ApkDecoder decoder = new ApkDecoder();
            decoder.setApkFile(new File(getPatchTmpDir(),apkName+".apk"));
            decoder.setFrameworkDir(getPatchTmpDir().getAbsolutePath());
            decoder.setForceDelete(true);
            decoder.setDecodeSources(DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES);
            decoder.setDecodeResources(DECODE_RESOURCES_NONE);//DECODE_RESOURCES_NONE
            decoder.setOutDir(new File(getPatchTmpDir(), apkName));
            decoder.decode();
            decoder.close();
            Log.d(TAG, "call: apk解包结束");
//                    Log.d(TAG, "onActivityResult: 开始编译apk");
//                    BuildOptions buildOptions = new BuildOptions();
//                    new Androlib().build(PatchUtils.getPatchTmpOutDir(requireContext()),null);
//                Main.main(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.string.actmsg_decodeapk;
    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_decodeapk;
    }
}