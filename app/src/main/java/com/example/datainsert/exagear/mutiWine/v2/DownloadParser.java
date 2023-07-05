package com.example.datainsert.exagear.mutiWine.v2;

import android.util.Log;

import com.eltechs.axs.helpers.UiThread;
import com.example.datainsert.exagear.QH;

import java.io.File;
import java.util.List;

/**
 * 用于下载页面的adapter中，对数据的获取和处理( 主要是从网络下载。本地文件操作交给config）
 */
public abstract class DownloadParser {
    public static final int PARSER_KRON4EK = 0;
    public static final int PARSER_WINEHQ = 1;
    public static final String PARSER_PREF_KEY = "parser_type";
    private static final String TAG = "DownloadParser";
    Callback mCallback;

    //还不能放到父类里，否则调用parseRelease的时候会调用子类成员变量，而成员变量貌似需要在super()之后才能初始化，所以会报null错误
//    public DownloadParser(Callback callback) {
//        mCallback = callback;
//        parseRelease();
//    }

    /**
     * 该下载源的wine对应的文件路径配置
     */
    public abstract ConfigAbstract config();

    /**
     * 包含全部release信息的列表
     */
    public abstract List<? extends WineInfo> infoList();

    /**
     * 下载最新的release信息，更新infoList
     *
     * @param redownload 是否重新下载release信息，若为false，即使本地不存在release文件也不会下载
     */
    public void syncRelease(boolean redownload) {
        //可能会在onStart里调用，此时没有acitivity，所以没法显示dialog
        if (QH.getCurrentActivity() == null) {
            Log.w(TAG, "syncRelease: 当前在activity的onCreate或onStart阶段，无法获取activity，没有下载release或填充infoList");
            return;
        }
        MyProgressDialog dialog = new MyProgressDialog().init("", false);
        dialog.setMax(0);
        new Thread(() -> {
            //若版本信息不存在，或强制下载，则进行下载。
            //若下载失败，则调用error方法。
            if (redownload) {
                File oldFile  = config().getReleaseInfoFile();
                File backupFile = new File(oldFile.getAbsolutePath()+".backup");
                try {
                    //下载前先备份旧文件，如果下载失败再复原
                    if(oldFile.exists()){
                        backupFile.delete();
                        oldFile.renameTo(backupFile);
                    }

                    onDownloadRelease(dialog);

                    //如果下载失败，复原备份文件 (MWfileHelper内部消化报错了，没扔出来，所以其实catch不到。其逻辑是如果报错则删除文件，那就用exist判断了）
                    if(!oldFile.exists() && backupFile.exists()){
                        backupFile.renameTo(oldFile);
                    }else
                        backupFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();


                }
            }

            parseRelease();
        }
        ).start();
    }

    /**
     * 读取本地release文件,转为infoList存储。在@link #syncRelease(boolean)}时用到。
     * 读取结束后应调用回调.ready通知适配器刷新视图
     */
    protected abstract void parseRelease();

    /**
     * 下载release文件的具体过程。在{@link #syncRelease(boolean)}时用到
     *
     * @param dialog 用于显示下载进度的对话框
     */
    protected abstract void onDownloadRelease(MyProgressDialog dialog);

    /**
     * 根据tag下载对应版本的wine压缩包
     */
    public abstract void downloadWine(WineInfo info);


    /**
     * 当刷新release信息完成时，调用的回调，通知adapter刷新视图
     */
    interface Callback {
        /**
         * 刷新release列表结束。
         *
         * @param errorMsg 为null则无错误，否则是错误信息
         */
        void ready(String errorMsg);

    }
}
