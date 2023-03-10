package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.PatchUtils.getPatchTmpDir;

import static brut.androlib.ApkDecoder.DECODE_RESOURCES_FULL;
import static brut.androlib.ApkDecoder.DECODE_RESOURCES_NONE;
import static brut.androlib.ApkDecoder.DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES;

import android.util.Log;

import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;

import org.apache.commons.io.FileUtils;

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
//        throw new Exception( "aaa");
        //先清空上一次解包的目录吧，貌似有些文件不会被自动清除
        File decodeDir = new File(getPatchTmpDir(),apkName);
        if(decodeDir.exists() && decodeDir.isDirectory()){
            FileUtils.forceDelete(decodeDir);
            Log.d(TAG, "call: 删除上一次解压的文件夹成功");
        }

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

        if(apkName.equals("tmp")){
            //每次解包apk后，更新包名
            PatchUtils.setPackageName("");
            Log.d(TAG, "decodeApk: 解压完成，重新读取包名"+PatchUtils.getPackageName());
        }
        return R.string.actmsg_decodeapk;
    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_decodeapk;
    }
}
