package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.PatchUtils.getPatchTmpDir;

import static brut.androlib.ApkDecoder.DECODE_RESOURCES_FULL;
import static brut.androlib.ApkDecoder.DECODE_RESOURCES_NONE;
import static brut.androlib.ApkDecoder.DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES;

import android.util.Log;

import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

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


//        testExec();

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


    private void testExec() throws IOException, InterruptedException {
        File filesDir = new File("/data/user/0/com.ewt45.patchapp/files");
        for(File file:filesDir.listFiles()){
            Log.d(TAG, "test4: 看看都有什么文件"+file.getName());
        }
        Log.d(TAG, "call: 看看能不能正常运行");
        System.out.println(" 看看能不能正常运行");
        Process process =Runtime.getRuntime().exec(
                new String[]{
                        "./apktool_2.7.0.jar"//原来指定工作路径之后要加./否则会找不到文件
//                        filesDir.getAbsolutePath()+"/apktool_2.7.0.jar"
                        , "-h"
                },
                null,
                filesDir);

        setInStream(process.getInputStream(), System.out);
        setInStream(process.getErrorStream(), System.err);
        int exitCode = process.waitFor();
        Log.d(TAG, "test4: 子进程结束+"+exitCode);
    }
    private void setInStream(InputStream inputStream, PrintStream out){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
