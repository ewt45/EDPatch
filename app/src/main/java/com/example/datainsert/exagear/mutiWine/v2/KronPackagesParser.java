package com.example.datainsert.exagear.mutiWine.v2;

import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.UiThread;
import com.example.datainsert.exagear.mutiWine.Config;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class KronPackagesParser {
    private static final String TAG = "KronPackagesParser";
    File packageFile = Config.getReleaseJsonFile();

    List<WineKron4ekInfo> infoList = new ArrayList<>();
    Callback mCallback;

    public KronPackagesParser(Callback callback) {
        mCallback = callback;
        //初始时添加一个空的，用于刷新显示的填充项
        infoList.add(new WineKron4ekInfo());
        refresh(false);
    }

    public void refresh(boolean force) {
        new Thread(() -> {
            //若版本信息不存在，或强制下载，则进行下载。
            //若下载失败，则调用error方法。

            try {
                if (!packageFile.exists() || force) {
                    downloadFull();
                }
                parsePackages();
                mCallback.ready(false);
            } catch (Exception e) {
                e.printStackTrace();
                //下载出错的话，列表里就装一条数据，名字是报错内容。待会显示在回收视图上
                infoList.clear();
                infoList.add(new WineKron4ekInfo());
                infoList.get(0).name = e.getMessage();
                mCallback.ready(true);
            }
        }
        ).start();
    }

    /**
     * 一次下载50条，下载全部release信息
     */
    private void downloadFull() throws IOException {
        int page = 1;
        Gson gson = new Gson();
        //先清空列表
        infoList.clear();
        packageFile.delete();
        List<WineKron4ekInfo> infoParts;//用于接收一部分json的列表
        while (true) {
            //一次下载一部分。最后整合成一个文件
            String url = "https://api.github.com/repos/Kron4ek/Wine-Builds/releases?per_page=50&page=" + page;
            Log.d(TAG, String.format("downloadFull: 下载第%d页",page));
            GithubDownload.download(packageFile, url);
            //下载后转为json，如果列表为空则停止
            try (FileReader fileReader = new FileReader(packageFile);) {
                infoParts = gson.fromJson(fileReader, new TypeToken<List<WineKron4ekInfo>>() {
                });
            }
            if(infoParts.isEmpty()){
                //完整json存到本地文件，覆盖原有文件
                FileUtils.write(packageFile,gson.toJson(infoList), StandardCharsets.UTF_8,false);
                break;
            }
            else {
                infoList.addAll(infoParts);
                page++;
            }
        }
    }

    private void parsePackages() throws IOException {
        Gson gson = new Gson();

        try (FileReader fileReader = new FileReader(packageFile);) {
            List<WineKron4ekInfo> infos = gson.fromJson(fileReader, new TypeToken<List<WineKron4ekInfo>>() {
            });

//            for (WineKron4ekInfo info : infos) {
//                System.out.println(info);
//            }
            this.infoList.clear();
            infoList.addAll(infos);
        }
    }

    /**
     * 下载全部release的信息（不检查文件是否存在）
     */
    private void download() throws Exception {
        //检查是否能连接github

        //开始下载

        //完成下载任务
        try {
            //  s = "http://115.238.91.202:3181/images/2.jpg";
            URL url = new URL("https://api.github.com/repos/Kron4ek/Wine-Builds/releases?per_page=50&page=1");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            int resCode = conn.getResponseCode();
            if (resCode == 200) {

                int size = conn.getContentLength();//获取到最大值之后设置到进度条的MAX

                //开始下载
                byte[] bytes = new byte[1024];//可以设置大点提高下载速度
                int len = -1;
                InputStream in = conn.getInputStream();
                //断点续传
//                boolean append = false;
//                if(completeLen > 0){
//                    in.skip(completeLen);
//                    append = true;
//                }

                //先清除掉上一次下载内容吧( 草，放outputstream下面会把刚创建的文件直接删掉导致下载了个寂寞）
                if (packageFile.exists()) {
                    boolean b = packageFile.delete();
                    Log.d(TAG, "download: 删除旧的json, 是否成功：" + b);
                }
                FileOutputStream out = new FileOutputStream(packageFile, false);

                int copylength = IOUtils.copy(in, out);
                Log.d(TAG, String.format("download: 复制长度：%d", copylength));
                //这样能显示进度，不过先不显示了吧
//                while( (len = in.read(bytes)) != -1 ){
//                    out.write(bytes, 0, len);
//                    completeLen += len;
//
//                    publishProgress(DOWNLOAD_PROGRESS, len);
//                    out.flush();
//                }
                out.close();
                in.close();
                UiThread.post(() -> AndroidHelpers.toastShort("下载release信息成功"));

            } else {
                throw new Exception("responseCode=" + resCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("下载release信息失败, " + e.getMessage());
        }
    }


    interface Callback {
        void ready(boolean wentWrong);

    }
}
