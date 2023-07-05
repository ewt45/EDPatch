package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.RR.getS;

import android.util.Log;

import com.eltechs.axs.helpers.UiThread;
import com.example.datainsert.exagear.RR;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KronParser extends DownloadParser {
    private static final String TAG = "KronPackagesParser";
    private final KronConfig config =KronConfig.i;
    public List<KronWineInfo> infoList = new ArrayList<>();

    public KronParser(Callback callback) {
        mCallback = callback;
    }

    /**
     * 下载release的一个asset
     */
    private void downloadReleaseAsset(String tagName, KronWineInfo.Asset asset, MyProgressDialog.Callback callback) {
        MWFileHelpers.download(
                new File(config.getTagFolder(tagName), asset.name),
                config.resolveDownloadLink(asset.browser_download_url),
                false,
                callback);
    }

    @Override
    public ConfigAbstract config() {
        return config;
    }

    @Override
    public List<? extends WineInfo> infoList() {
        return infoList;
    }

    @Override
    public void downloadWine(WineInfo absInfo) {
        KronWineInfo info = (KronWineInfo) absInfo;
        String filename = info.getTagName() + "-x86.tar.xz";

        //是否存在符合命名规则的下载文件，以及是否有其他文件正在下载
        KronWineInfo.Asset tarAsset = info.getAssetByName(filename);
        assert tarAsset != null; //这个在外部 bind 的时候判断过了，能进入这个函数的都不是null

        MyProgressDialog dialog = new MyProgressDialog().init("", false);
        dialog.show();

        String skipStr = getS(RR.mw_dialog_download).split("\\$")[2];
        String cancelStr = getS(RR.mw_dialog_download).split("\\$")[3];

        //如果本地存在 跳过下载(需要下载sha256sums.txt比较吧，先略过了）
        File wineTarFile = new File(KronConfig.i.getTagFolder(info.getTagName()), filename);
        if (wineTarFile.exists()) {
            try {
                config.checkSha256(info.getTagName());
                dialog.done(skipStr);
                return;
            } catch (Exception e) {
                //如果本地压缩包不完整，删了重下
                wineTarFile.delete();
            }
        }
        //是否有其他文件正在下载
        if (MWFileHelpers.isDownloading) {
            dialog.fail(cancelStr);
            return;
        }

        //新建线程开始下载
        new Thread(() -> {
            //下载sha256.txt
            KronWineInfo.Asset shaAsset = info.getAssetByName("sha256sums.txt");
            if (shaAsset != null) {
                UiThread.post(() -> dialog.setMessage("sha256sums.txt"));//要在主线程？
                downloadReleaseAsset(info.getTagName(), shaAsset, dialog.defaultCallback);
            }

            //下载压缩包
            UiThread.post(() -> {
                dialog.init(wineTarFile.getName(), false);
                dialog.setMax(tarAsset.size);
            });

            downloadReleaseAsset(info.getTagName(), tarAsset, dialog.defaultCallback);
        }).start();
    }

    @Override
    protected void onDownloadRelease(MyProgressDialog dialog) {
        File packageFile = config.getReleaseInfoFile();
        //一次下载50条，下载全部release信息
        int page = 1, maxTry = 5;
        Gson gson = new Gson();
        //先清空列表
        infoList.clear();
        packageFile.delete();
        List<KronWineInfo> infoParts;//用于接收一部分json的列表

        UiThread.post(dialog::show);

        StringBuilder historyMessage = new StringBuilder();
        //最多只下载5次
        for (int i = 0; i < maxTry; i++) {
            //一次下载一部分。最后整合成一个文件
            String url = "https://api.github.com/repos/Kron4ek/Wine-Builds/releases?per_page=50&page=" + page;
            Log.d(TAG, String.format("downloadFull: 下载第%d页", page));


            historyMessage.append('\n').append(packageFile.getName()).append("-").append(page);//json文件名后面加个序号
            UiThread.post(() -> dialog.init(historyMessage.toString(), false));

            MWFileHelpers.download(packageFile, url, false, message -> {
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


    @Override
    protected void parseRelease() {
        Gson gson = new Gson();

        if (!config.getReleaseInfoFile().exists()) {
            mCallback.ready(null);
            return;
        }
        String errMsg = null;
        try (FileReader fileReader = new FileReader(config.getReleaseInfoFile());) {
            List<KronWineInfo> infos = gson.fromJson(fileReader, new TypeToken<List<KronWineInfo>>() {
            });
            this.infoList.clear();
            infoList.addAll(infos);
        } catch (IOException e) {
            e.printStackTrace();
            infoList.clear();
            errMsg = e.getMessage();
        }
        //去除掉不符合条件的
        for (int i = 0; i < infoList.size(); i++) {
            KronWineInfo info = infoList.get(i);
            if (info.getAssetByName(info.getTagName() + "-x86.tar.xz") == null) {
                infoList.remove(i);
                i--;
            }
        }
        mCallback.ready(errMsg);
    }


}
