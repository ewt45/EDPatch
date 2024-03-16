package com.ewt45.exagearsupportv7;

import android.content.Context;
import android.graphics.Matrix;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;
import com.example.datainsert.exagear.controlsV2.model.DeserializerOfModel;
import com.example.datainsert.exagear.controlsV2.model.OneProfile;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "ExampleInstrumentedTest";

    @Test
    public void 测试_安卓dalvikvm运行外部java代码是否是在同进程下() throws IOException {
//        //对比：进程号，某个类的变量
//        int mainPid = Process.myPid();
//        Const.activityRef = new WeakReference<>(null);
//        Log.d(TAG, "主进程： "+mainPid+", "+Const.activityRef);
////        System.out.println("system:主进程： "+mainPid+", "+Const.activityRef);
////        runProcessWithOutput("dalvikvm","-help");
//        runProcessWithOutput("dalvikvm","-cp","/sdcard/test.dex","com.example.test");
    }

    private void runProcessWithOutput(String ... cmds) throws IOException {
        java.lang.Process process = new ProcessBuilder()
                .command(cmds)
                .redirectErrorStream(true)
                .start();
        try (InputStream is = process.getInputStream();
             InputStreamReader reader = new InputStreamReader(is, Charset.defaultCharset())){
            char[] chars = new char[2048];
            int len;
            do{
                len = reader.read(chars,0,2048);
                System.out.println("system.out: "+new String(chars));
            }while (len!=-1);
        }
    }

    @Test
    public void gson反序列化state怎么少了一个(){

        String json = "{\"modelList\":[{\"allStateList\":[{\"id\":1,\"stateType\":2},{\"id\":2,\"stateType\":3},{\"countDownMs\":250,\"fastMoveThreshold\":36.0,\"fingerIndex\":0,\"noMoveThreshold\":12.0,\"id\":3,\"niceName\":\"\",\"stateType\":1}],\"tranActionsList\":[[]],\"tranEventList\":[1],\"tranPostStateList\":[1],\"tranPreStateList\":[2],\"colorStyle\":0,\"height\":1600,\"keycodes\":[0],\"left\":0,\"mMinAreaSize\":80,\"mainColor\":-1286,\"modelType\":3,\"name\":\"None\",\"top\":0,\"width\":2560}],\"name\":\"1\",\"version\":0}\n";
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(TouchAreaModel.class, new DeserializerOfModel())
                .create();
        OneProfile oneProfile = gson.fromJson(json, OneProfile.class);
        System.out.println("state个数="+oneProfile.getGestureAreaModel().getAllStateList().size());
    }

    @Test
    public void 测试_矩阵先平移再缩放和先缩放再平移有区别吗(){
        Matrix matrix = new Matrix();
        System.out.println("初始矩阵：");
        print_matrix(matrix);

        matrix.postTranslate(1,2);
        matrix.postScale(3.1f,2.4f);
        matrix.postTranslate(3,4);
        System.out.println("先缩放再平移：");
        print_matrix(matrix);

        matrix.postTranslate(1,2);
        matrix.postTranslate(3,4);
        matrix.postScale(3.1f,2.4f);
        System.out.println("先平移再缩放：");
        print_matrix(matrix);
    }

    public static void print_matrix(Matrix matrix){
        float[] values =new float[9];
        matrix.getValues(values);
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                System.out.print(values[i * 3 + j] +"  \t");
            }
            System.out.print("\n");
        }
    }


    public static class ParentTest{
        public ParentTest(){
            StateTag ant = getClass().getAnnotation(StateTag.class);
            if(ant==null)
                throw new RuntimeException("state类缺少注解StateTag");
            System.out.println("ParentTest: 子类tag为："+ant.tag());
            Log.d(TAG, "ParentTest: 子类tag为："+ant.tag());
        }
    }
    @StateTag(tag = 1)
    public static class ChildTest extends ParentTest{
        public ChildTest(){
            super();
        }
    }

    @Test
    public void test_father_class_get_child_annotation(){
        new ChildTest();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.ewt45.exagearsupportv7", appContext.getPackageName());
    }

    @Test
    public void getAssets() throws IOException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        assertEquals("com.ewt45.exagearsupportv7", appContext.getPackageName());
        try (InputStream is = appContext.getAssets().open("pulseaudio-xsdl.zip");){
            Log.d(TAG, "getAssets: 读取不到？");
        }

    }
}