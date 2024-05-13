package com.example;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.media.AudioRecordingConfiguration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Process;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.ed.R;
import com.eltechs.ed.fragments.ManageContainersFragment;
import com.eltechs.ed.guestContainers.GuestContainer;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controls.CursorToggle;
import com.example.datainsert.exagear.controlsV2.gestureMachine.state.ActionButtonClick;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class test {
//    private static final File mUserAreaDir = DriveD.getDriveDDir();
    private static boolean staticNotFinal = false;
    private final static boolean staticAndFinal  = true; //smali声明变量那一行就会赋值
    Object i2;

    public test() {

    }

    public static void main(String[] args){
//
//        int mainPid = Process.myPid(); //这个用不了？
//        Log.d("TAG", "log: 主进程： "+mainPid+", "+Const.activityRef);
//        System.out.println("system: 主进程： "+mainPid+", "+Const.activityRef);

    }

    public static void get_all_class_in_a_package() {
    }
    @SuppressLint("NewApi")
    public  static void test(List<Object> list ){
        Log.d("testtag", "test: 查看列表项");
        for(Object o :list){
            try {
                String name = (String) o.getClass().getField("name").get(o);
                Log.d("testtag", "test: "+name);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void test2(){
        test.test(new ArrayList<>());
    }


    public static void add_popupmenu(Menu menu){
        menu.add("current playing audio").setOnMenuItemClickListener(item -> {
            viewNowPlayback();
           return true;
        });
    }
    public static void test_call_audioset(){
        //1. AXSPopupMenu show show前添加这个
        test.add_popupmenu(null);
        //2. activity onCreate时调用这个
        test.setAllowAudioRecord();
    }

    @SuppressLint("WrongConstant")
    public static void setAllowAudioRecord(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AudioManager manager = Globals.getAppContext().getSystemService(AudioManager.class);
            manager.setAllowedCapturePolicy(AudioAttributes.ALLOW_CAPTURE_BY_ALL);
            //这个应该是此时正在播放的音频的设置？
//            manager.getActivePlaybackConfigurations();
        }
    }

    public static void viewNowPlayback(){
        /*
        Usage:
            0 = USAGE_UNKNOWN
            1 = USAGE_MEDIA
        Allow:
            1 = ALLOW_CAPTURE_BY_ALL
            2 = ALLOW_CAPTURE_BY_SYSTEM
            3 = ALLOW_CAPTURE_BY_NONE
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AudioManager manager = Globals.getAppContext().getSystemService(AudioManager.class);
            //这个应该是此时正在播放的音频的设置？
            List<AudioPlaybackConfiguration> list = manager.getActivePlaybackConfigurations();
            StringBuilder builder = new StringBuilder();
            builder.append("ActivePlaybackConfigurations:\n");
            for(AudioPlaybackConfiguration c:list){
                AudioAttributes attr = c.getAudioAttributes();
                builder.append("Usage: ").append(attr.getUsage()).append(", AllowedCapturePolicy").append(attr.getAllowedCapturePolicy()).append("\n");
            }
            if(list.size()==0)
                builder.append("\nActiveRecordingConfigurations\n");
            List<AudioRecordingConfiguration> list2 = manager.getActiveRecordingConfigurations();
            for(AudioRecordingConfiguration  c:list2){
                builder.append(c).append("\n");
            }
            if(list2.size()==0)
                builder.append("none\n");

            new AlertDialog.Builder(((ApplicationStateBase)Globals.getApplicationState()).getCurrentActivity())
                    .setMessage(builder.toString()).show();
        }
    }

    private void test_get_string_from_context_with_resid(){
        int stringId = 0x7f09452;
        String str = Globals.getAppContext().getString(stringId);

    }
//
//    private void test1(int i1,float f1,float f2, float f3, float f4, float f5, int i2, float f6, boolean b){
//       logthis(i1,f1,f2,f3,f4,f5,i2,f6,b);
//        SoftInput.toggle();
//    }
//    private void logthis(int i1,float f1,float f2, float f3, float f4, float f5, int i2, float f6, boolean b){
//        QH.logD("placeRectangle参数："+i1+","+f1+","+f2+","+f3+","+f4+","+f5+","+i2+","+f6+","+b);
//    }

    public void test_int_to_smali(){
        int i= 0x424242;
    }

    public void send_inputstream_instead_of_file(){
//        TarZstdUtils.extract(this,new File(""));
    }
    public static int winlatorObb(Context context, AtomicReference<File> result){
        result.set(new File("/this/should/not/exist"));
        return  1;
    }


    public static void reflectInvoke(ManageContainersFragment fragment) {
        for (Class<?> clz : fragment.getClass().getDeclaredClasses()) {
            //clz.getSimpleName().equals("ContAsyncTask")
            if (AsyncTask.class.equals(clz.getSuperclass())) {
                try {
                    Constructor<AsyncTask<GuestContainer, Void, Void>> constructor = ((Class<AsyncTask<GuestContainer, Void, Void>>) clz).getDeclaredConstructor(fragment.getClass(), int.class);//参考smali中实例化的时候，传入外部类实例和参数
                    constructor.setAccessible(true); //允许访问private
                    AsyncTask<GuestContainer, Void, Void> task =  constructor.newInstance(fragment, 0);
                    task.execute();
                } catch (NoSuchMethodException | InvocationTargetException |
                         IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void test2(float a) {
        CursorToggle cursorToggle;
        Toolbar toolbar = new Toolbar(Globals.getAppContext());
        toolbar.setBackgroundResource(R.drawable.someimg);
        Class<test> s = test.class;
        try {
            s.getDeclaredConstructor(Map.class).newInstance(new HashMap<>());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void test3(int i) {
        while (true) {
            try {
                File file = new File("");
                file.createNewFile();

                i--;
                if (i == 0)
                    throw new RuntimeException();
                synchronized (i2) {
                    try {
                        i -= 2;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {

            }
        }

    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode!=10001){
//            SelectObbFragment.receiveResultManually(this,requestCode,resultCode,data);
//            return;
//        }
//        assert resultCode==2||resultCode==0;
//        int i=0;
//        test2(1f);
//    }
}
