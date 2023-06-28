package com.example.datainsert.exagear.mutiWine.v2;

import android.util.Log;

import com.eltechs.axs.helpers.UiThread;
import com.example.datainsert.exagear.mutiWine.KronConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KronParser {
    private static final String TAG = "KronPackagesParser";
    File packageFile = KronConfig.i.getReleaseInfoFile();

    List<KronWineInfo> infoList = new ArrayList<>();
    Callback mCallback;

    public KronParser(Callback callback) {
        mCallback = callback;
        //初始时添加一个空的，用于刷新显示的填充项
//        infoList.add(new WineKron4ekInfo());
//        refresh(false);
        parsePackages();
    }

    public void refresh(boolean force) {
        MyProgressDialog dialog = new MyProgressDialog().init("", false);
        dialog.setMax(0);
        new Thread(() -> {
            //若版本信息不存在，或强制下载，则进行下载。
            //若下载失败，则调用error方法。

            try {
                if (!packageFile.exists() || force)
                    downloadFull(dialog);

                parsePackages();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        ).start();
    }

    /**
     * 一次下载50条，下载全部release信息
     */
    private void downloadFull(MyProgressDialog dialog) {
        int page = 1, maxTry=5;
        Gson gson = new Gson();
        //先清空列表
        infoList.clear();
        packageFile.delete();
        List<KronWineInfo> infoParts = new ArrayList<>();//用于接收一部分json的列表

        UiThread.post(dialog::show);

        StringBuilder historyMessage = new StringBuilder();
        //最多只下载5次
        for(int i=0; i<maxTry; i++){
            //一次下载一部分。最后整合成一个文件
            String url = "https://api.github.com/repos/Kron4ek/Wine-Builds/releases?per_page=50&page=" + page;
            Log.d(TAG, String.format("downloadFull: 下载第%d页", page));


            historyMessage.append('\n').append(packageFile.getName()).append("-").append(page);//json文件名后面加个序号
            UiThread.post(()-> dialog.init(historyMessage.toString(),false));

            DownloadFileHelper.download(packageFile, url, false, message -> {
                historyMessage.append(message.substring(packageFile.getName().length()));
                dialog.done(historyMessage.toString());
            });
            //下载后转为json，如果列表为空则停止
            try (FileReader fileReader = new FileReader(packageFile);) {
                infoParts = gson.fromJson(fileReader, new TypeToken<List<KronWineInfo>>() {
                });
                if (infoParts.isEmpty()) {
                    //完整json存到本地文件，覆盖原有文件
                    FileUtils.write(packageFile, gson.toJson(infoList), StandardCharsets.UTF_8, false);
                    break;
                } else {
                    infoList.addAll(infoParts);
                    page++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * 读取本地json文本转换为java对象。读取结束后会调用回调.ready通知适配器刷新视图
     */
    private void parsePackages()  {
        Gson gson = new Gson();
        if(!packageFile.exists()){
            mCallback.ready(false);
            return;
        }

        boolean wentWrong = false;
        try (FileReader fileReader = new FileReader(packageFile);) {
            List<KronWineInfo> infos = gson.fromJson(fileReader, new TypeToken<List<KronWineInfo>>(){});
            this.infoList.clear();
            infoList.addAll(infos);
        } catch (IOException e) {
            e.printStackTrace();
            //下载出错的话，列表里就装一条数据，名字是报错内容。待会显示在回收视图上
            infoList.clear();
            infoList.add(new KronWineInfo());
            infoList.get(0).name = e.getMessage();
            wentWrong  =true;
        }
        mCallback.ready(wentWrong);
    }


    interface Callback {
        void ready(boolean wentWrong);

    }
}
