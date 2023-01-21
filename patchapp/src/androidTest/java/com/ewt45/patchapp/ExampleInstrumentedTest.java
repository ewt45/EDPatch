package com.ewt45.patchapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import static brut.androlib.ApkDecoder.DECODE_RESOURCES_FULL;
import static brut.androlib.ApkDecoder.DECODE_RESOURCES_NONE;
import static brut.androlib.ApkDecoder.DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES;
import static brut.androlib.ApkDecoder.FORCE_DECODE_MANIFEST_FULL;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import brut.androlib.Androlib;
import brut.androlib.ApkDecoder;
import brut.androlib.options.BuildOptions;
import brut.common.BrutException;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.ewt45.patchapp", appContext.getPackageName());
    }

    @Test
    public void test(){
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

        File apkFile = new File(c.getExternalFilesDir(null).getAbsolutePath()+"/patchtmp/tmp.apk");
        try {
            ApkDecoder decoder = new ApkDecoder();
            decoder.setApkFile(apkFile);
            decoder.setFrameworkDir(c.getExternalFilesDir(null).getAbsolutePath()+"/patchtmp");
            decoder.setForceDelete(true);
            decoder.setDecodeSources(DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES);
            decoder.setDecodeResources(DECODE_RESOURCES_FULL);//DECODE_RESOURCES_NONE
            decoder.setOutDir(new File(c.getExternalFilesDir(null).getAbsolutePath()+"/patchtmp/tmptest"));
            if (!apkFile.isFile() || !apkFile.canRead()) {
                throw new Exception("isFIle?"+apkFile.isFile()+" canread?"+apkFile.canRead());
            }
            decoder.decode();
            decoder.close();
            Log.d("test", "call: apk解包结束");
//                    Log.d(TAG, "onActivityResult: 开始编译apk");
//                    BuildOptions buildOptions = new BuildOptions();
//                    new Androlib().build(PatchUtils.getPatchTmpOutDir(requireContext()),null);
//                Main.main(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    String TAG ="test";
    @Test
    public void test2() throws BrutException {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File out = new File(c.getExternalFilesDir(null).getAbsolutePath()+"/patchtmp/tmptest");
        BuildOptions options = new BuildOptions();
        options.frameworkFolderLocation = c.getExternalFilesDir(null).getAbsolutePath()+"/patchtmp";
        options.aaptPath = c.getFilesDir().getAbsolutePath()+"/patchtmp/libaapt.so";
        new Androlib(options).build(out, null);


    }
    @Test
    public void test3(){
        //卧槽，是不是这样就可以看环境变量了
        Map<String,String> map = System.getenv();
        map.forEach((s, s2) -> Log.d(TAG, "环境变量："+s+"="+s2));
        //上面是不可修改的，这个居然还有个可以修改的
//        ProcessBuilder.environment();
        //这居然还有一个执行命令行并且可以设置工作路径的，我为什么要用java啊草
//        Runtime.getRuntime().exec(new String[]{},new String[]{},new File(""));
        Log.d(TAG, "test2: Build.SUPPORTED_ABIS: "+ Arrays.toString(Build.SUPPORTED_ABIS));
        Log.d(TAG, "test2: System.getenv():\n");
        Logger logger = Logger.getLogger("");
        logger.info("loggger能输出吗");

//        BitmapFactory.decode
//        new Bitmap().getpi
    }
}