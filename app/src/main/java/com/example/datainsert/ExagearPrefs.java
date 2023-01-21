package com.example.datainsert;

import static android.content.ContentValues.TAG;

import android.content.Context;

import android.os.Build;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.inputmethod.InputMethodManager;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ExagearPrefs {
    /*判断当前键盘是否已经调出显示，以确定是调用隐藏还是显示键盘的方法(也就是给安卓10往上用的） **/
    static boolean inputShow = false;
    /**
     * 解决安卓11和安卓12 弹窗菜单调出键盘闪退的问题
     * @param a 当前的activity
     */
    static public void showInputCorrect(AppCompatActivity a){
        Log.d(TAG, "showInputCorrect: 1秒后显示键盘，传入的参数类型为"+a.getLocalClassName());
        InputMethodManager imm = (InputMethodManager)a.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //看ex的，第一个是Force第二个是0，网上第一个是0第二个是NotALways
//                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        Log.d(TAG, "onClick: 当前顶层布局为"+a.getWindow().getDecorView());
        //安卓10以上，更改键盘调起方式

        if (Build.VERSION.SDK_INT > 29 ) {
            //如果应该显示键盘，等一秒，顶层view调起键盘（如果应该取消键盘那么什么都不做，点完菜单项的时候键盘应该会自动消失）
            a.getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//                        imm.showSoftInput(a.getWindow().getDecorView(), 0); //原来安卓12只要延迟1秒，用toggle也没问题（阿这ex用show反而没法显示？）
                }
            }, 1000);
//            Toast.makeText(a, "1秒后显示键盘。关闭请按手机返回键", Toast.LENGTH_SHORT).show();

        }
        //安卓10及以下保留原来的方式(或者安卓12要隐藏键盘的时候，这个应该没问题吧）
        else {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
        inputShow = ! inputShow;
    }

    static public void setSP(Context ctx) {
        try {
            String[] configs = ctx.getAssets().list("containerConfig");

            if (configs.length == 0)
                throw new Exception("没有环境配置文件");

            //创建share_prefs文件夹
            File dir = new File(ctx.getApplicationInfo().dataDir + "/shared_prefs");
            if (!dir.exists()) {
                boolean b = dir.mkdir();
                assert b;
            }
            ;
            //将文件写入到存档路径中
            for (String datName : configs) {
                Log.i("getAssets().list", datName);

                InputStream is = ctx.getAssets().open("containerConfig/" + datName);      //源文件输入流
                File newFile = new File(dir.getAbsolutePath() + "/" + datName); //创建新文件

                //如果没有，创建该文件
                if (newFile.createNewFile()) {
                    FileOutputStream fos = new FileOutputStream(newFile);                   //新文件输出流
                    int len = -1;
                    byte[] buffer = new byte[4096];
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                is.close();
                //Log.i("dataDirectory",getFilesDir().getAbsolutePath()); ///data/user/0/com.example.datainsert/files
            }
            Log.d("ExagearPrefs", "setSP: 现在的sp文件夹：" + Arrays.toString(dir.list()));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exagear", "setSP: 出错：" + e.getMessage());
        }
    }

    /**
     * 原函数里有IOexception接着
     * obb从assets/obb/*.obb 移动到 data/data/包名/files/*.obb
     * //记得在创建完成之后的地方删除这个obb
     *
     * @param ctx
     * @return
     */
    static public File setTmpObb(Context ctx) throws IOException {
        Log.d("exagear", "setTmpObb: 进入生成临时数据包的函数 ");
        try{
            File destDir = ctx.getFilesDir();

            if (!destDir.exists()) {
                boolean b = destDir.mkdir();
                assert b;
            }

            String[] list = ctx.getAssets().list("obb");
            //如果目录下没有文件或者多个文件，返回null，让exagear显示报错
            if (list.length != 1)
                return null;
            //获取asstes的inputstream
            InputStream is = ctx.getAssets().open("obb/" + list[0]);
            //复制到files文件夹
//        File.createTempFile("exa","obb",destDir);
            File newFile = new File(destDir.getAbsolutePath() + "/" + list[0]); //创建新文件

            //如果已经存在（一般不会，但是如果在第一次解压的时候强制关掉的话，第二次启动）
            if (newFile.exists()) {
                boolean b = newFile.delete();
                assert b;
            }
            //vm正常关闭（程序退出时）会删掉此文件。
            newFile.deleteOnExit();
            //创建该文件
            if (!newFile.createNewFile()) {
                return null;
            }

            FileOutputStream fos = new FileOutputStream(newFile);                   //新文件输出流
            int len = -1;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
            return newFile;
        }catch (IOException e){
            Log.d("Exagear", "setTempObbFile: 出错："+e.getMessage());
            throw e;
        }
    }

    static public void delTmpObb(Context ctx){
        Log.d("exagear", "setTempObbFile: 进入删除临时数据包的函数 ");

        try{
            File destDir = ctx.getFilesDir();
            String[] list = ctx.getAssets().list("obb");
            if(list.length == 1){
                File newFile = new File(destDir.getAbsolutePath() + "/" + list[0]); //创建新文件
                //如果存在，就删除
                if (newFile.exists()) {
                    boolean b = newFile.delete();
                    assert b;
                }
            }

        }catch (IOException e){
            Log.d("Exagear", "delTmpObb: 出错："+e.getMessage());
        }
    }

//    /**
//     * 从apk/assets读取数据包，直接返回file文件 貌似实现不了，算了
//     * 需要数据包添加到apk时选 仅存储
//     */
//    public static File getObbInApk() throws IOException {
//
//        Globals.getAppContext().getAssets().openFd("").;
//        File file = new File("1/2");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Files.copy(Globals.getAppContext().getAssets().open(""), Paths.get(file.getAbsolutePath()));
//        }
//        return file;
//
//    }

//    static public void setSP(Context ctx){
//        SharedPreferences sp = ctx.getSharedPreferences("com.eltechs.zc.CONTAINER_CONFIG_1", Context.MODE_PRIVATE);
//        //如果没获取到sp就创建一个
//        if(sp.getString("NAME","NAME_NOT_FOUND").equals("NAME_NOT_FOUND")){
//            SharedPreferences.Editor editor = sp.edit();
////        Log.d("ExagearPrefs", "setSP: "+sp.getString("NAME","NAME_NOT_FOUND"));
//            editor.putString("SCREEN_SIZE","1024,768");
//            editor.putString("SCREEN_COLOR_DEPTH","32");
//            editor.putString("NAME","预设容器");
//            editor.putString("RUN_ARGUMENTS","");
//            editor.putString("CONTROLS","default");
//            editor.putBoolean("HIDE_TASKBAR_SHORTCUT",false);
//            editor.putBoolean("DEFAULT_CONTROLS_NOT_SHORTCUT",false);
//            editor.putBoolean("DEFAULT_RESOLUTION_NOT_SHORTCUT",false);
//            editor.putString("STARTUP_ACTIONS","");
//            editor.putString("RUN_GUIDE","");
//            editor.putBoolean("RUN_GUIDE_SHOWN",false);
//            editor.putString("CREATED_BY","补补23456"); //用这个检测存不存在吧
//
//            editor.apply(); //同步写入，commit是异步写入
//        }else{
//            Log.d("ExagearPrefs", "setSP: sp已存在，跳过写入");
//        }
//
//    }
}
