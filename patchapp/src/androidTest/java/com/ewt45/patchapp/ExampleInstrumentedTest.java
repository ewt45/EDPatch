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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
    String TAG = "test";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.ewt45.patchapp", appContext.getPackageName());
    }

    @Test
    public void test() {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

        File apkFile = new File(c.getExternalFilesDir(null).getAbsolutePath() + "/patchtmp/tmp.apk");
        try {
            ApkDecoder decoder = new ApkDecoder();
            decoder.setApkFile(apkFile);
            decoder.setFrameworkDir(c.getExternalFilesDir(null).getAbsolutePath() + "/patchtmp");
            decoder.setForceDelete(true);
            decoder.setDecodeSources(DECODE_SOURCES_SMALI_ONLY_MAIN_CLASSES);
            decoder.setDecodeResources(DECODE_RESOURCES_FULL);//DECODE_RESOURCES_NONE
            decoder.setOutDir(new File(c.getExternalFilesDir(null).getAbsolutePath() + "/patchtmp/tmptest"));
            if (!apkFile.isFile() || !apkFile.canRead()) {
                throw new Exception("isFIle?" + apkFile.isFile() + " canread?" + apkFile.canRead());
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

    @Test
    public void test2() throws BrutException {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File out = new File(c.getExternalFilesDir(null).getAbsolutePath() + "/patchtmp/tmptest");
        BuildOptions options = new BuildOptions();
        options.frameworkFolderLocation = c.getExternalFilesDir(null).getAbsolutePath() + "/patchtmp";
        options.aaptPath = c.getFilesDir().getAbsolutePath() + "/patchtmp/libaapt.so";
        new Androlib(options).build(out, null);


    }

    @Test
    public void test3() {
        //卧槽，是不是这样就可以看环境变量了
        Map<String, String> map = System.getenv();
        map.forEach((s, s2) -> Log.d(TAG, "环境变量：" + s + "=" + s2));
        //上面是不可修改的，这个居然还有个可以修改的
//        ProcessBuilder.environment();
        //这居然还有一个执行命令行并且可以设置工作路径的，我为什么要用java啊草
//        Runtime.getRuntime().exec(new String[]{},new String[]{},new File(""));
        Log.d(TAG, "test2: Build.SUPPORTED_ABIS: " + Arrays.toString(Build.SUPPORTED_ABIS));
        Log.d(TAG, "test2: System.getenv():\n");
        Logger logger = Logger.getLogger("");
        logger.info("loggger能输出吗");

//        BitmapFactory.decode
//        new Bitmap().getpi
    }

    /**
     * 试试执行命令行
     */
    @Test
    public void test4() throws IOException, InterruptedException {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File filesDir = c.getFilesDir();
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