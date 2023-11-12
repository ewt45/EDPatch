package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.PatchUtils.getPatchTmpDir;

import static brut.androlib.ApkDecoder.DECODE_RESOURCES_NONE;
import static brut.androlib.ApkDecoder.DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES;

import android.util.Log;

import com.ewt45.patchapp.MyApplication;
import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import brut.androlib.ApkDecoder;

public class DecodeApk implements Action {

    public static final int PATCHER = 0;
    public static final int EXAGEAR = 1;
    static final String TAG = "DecodeApk";
    private final int which;

    public DecodeApk(int which) {
        this.which = which;
    }

    @Override
    public Integer call() throws Exception {

        File decodeDir = which == PATCHER ? PatchUtils.getPatcherExtractDir() : PatchUtils.getExaExtractDir();
        File apkFile = which == PATCHER ? PatchUtils.getLocalPatcherApk() : PatchUtils.getPatchTmpApk();

        if (which == PATCHER) {
            //若校验码相同，则不解包patcher
            if (!PatchUtils.isPatcherApkChanged(MyApplication.i)) {
                Log.d(TAG, "call: patcher已解压且未更新，跳过解压");
                return R.string.actmsg_decodeapk;
            }
            //否则应该把新的patcher从apk内复制到本地
            else {
                try (InputStream is = MyApplication.i.getAssets().open("patcher/release/patcher.apk");
                     FileOutputStream fos = new FileOutputStream(apkFile);) {
                    IOUtils.copy(is, fos);
                }
            }
        }


        //先清空上一次解包的目录吧，貌似有些文件不会被自动清除
        if (decodeDir.exists() && decodeDir.isDirectory()) {
            FileUtils.forceDelete(decodeDir);
            Log.d(TAG, "call: 删除上一次解压的文件夹成功");
        }

//        testExec();

        ApkDecoder decoder = new ApkDecoder();
        decoder.setApkFile(apkFile);
        decoder.setFrameworkDir(getPatchTmpDir().getAbsolutePath());
        decoder.setForceDelete(true);
        decoder.setDecodeSources(DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES);
        decoder.setDecodeResources(DECODE_RESOURCES_NONE);//DECODE_RESOURCES_NONE
        decoder.setOutDir(decodeDir);
        decoder.decode();
        decoder.close();
        Log.d(TAG, "call: apk解包结束");

        if (which == EXAGEAR) {
            //每次解包apk后，更新包名
            PatchUtils.setPackageName("");
            PatchUtils.getPackageName();
            Log.d(TAG, "decodeApk: 解压完成，重新读取包名" + PatchUtils.getPackageName());
        }
        return R.string.actmsg_decodeapk;
    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_decodeapk;
    }


    private void testExec() throws IOException, InterruptedException {
        File filesDir = new File("/data/user/0/com.ewt45.patchapp/files");
        for (File file : filesDir.listFiles()) {
            Log.d(TAG, "test4: 看看都有什么文件" + file.getName());
        }
        Log.d(TAG, "call: 看看能不能正常运行");
        System.out.println(" 看看能不能正常运行");
        Process process = Runtime.getRuntime().exec(
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
        Log.d(TAG, "test4: 子进程结束+" + exitCode);
    }

    private void setInStream(InputStream inputStream, PrintStream out) {
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
