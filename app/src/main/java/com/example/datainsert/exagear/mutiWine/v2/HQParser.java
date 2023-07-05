package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.RR.getS;

import com.eltechs.axs.helpers.FileHelpers;
import com.eltechs.axs.helpers.UiThread;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.mutiWine.WineNameComparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HQParser extends DownloadParser{
    private static final String TAG = "HQDownloadParser";
    private final HQConfig config = HQConfig.i;
//    private final File packageFile = config.getReleaseInfoFile();
    /**
     * 下载链接前半部分，info.filename路径如：
     * {@code dists/bionic/main/binary-i386/winehq-staging_4.15~bionic_i386.deb}
     */
    private final static String downloadUrlPrefix = "https://dl.winehq.org/wine-builds/ubuntu/";
    private static final String releaseDownloadUrl = "https://dl.winehq.org/wine-builds/ubuntu/dists/bionic/main/binary-i386/Packages";
    public List<HQWineInfo> infoList = new ArrayList<>();

    public HQParser(Callback callback) {
        mCallback = callback;
    }

    /**
     * 从字符串行中读取一个完整的HQWineInfo，存入wrapper
     *
     * @param wrapper 包裹了字符串行，当前读取位置
     */
    public static void readOneInfo(InfoWrapper wrapper) {

        HQWineInfo info = new HQWineInfo();
        while (wrapper.pos < wrapper.lines.size() && !wrapper.lines.get(wrapper.pos).startsWith("Package: ")) {
            wrapper.pos++;
        }

        info.mpackage = readOneProp(wrapper, "Package: ").oneProp;
        info.architecture = readOneProp(wrapper, "Architecture: ").oneProp;
        info.version = readOneProp(wrapper, "Version: ").oneProp;
        info.section = readOneProp(wrapper, "Section: ").oneProp;
        info.depends = readOneProp(wrapper, "Depends: ").oneProp;
        info.filename = readOneProp(wrapper, "Filename: ").oneProp;
        info.size = readOneProp(wrapper, "Size: ").oneProp;
        info.sha256 = readOneProp(wrapper, "SHA256: ").oneProp;

        wrapper.info = info;
    }

    /**
     * 从当前行往下寻找到第一个符合条件的属性，读取并存入wrapper
     *
     * @param wrapper 包裹了字符串行和当前读取位置
     * @param header  应包含冒号和空格
     * @return wrapper本身
     */
    public static InfoWrapper readOneProp(InfoWrapper wrapper, String header) {
        while (wrapper.pos < wrapper.lines.size()) {
            if (!wrapper.lines.get(wrapper.pos).startsWith(header)) {
                wrapper.pos++;
                continue;
            }
            //找到了就返回
            wrapper.oneProp = wrapper.lines.get(wrapper.pos).substring(header.length());
            wrapper.pos++;
            return wrapper;
        }
        //如果没找到也设置一下空字符串
        wrapper.oneProp = "";
        return wrapper;
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
        String cancelStr = getS(RR.mw_dialog_download).split("\\$")[3];
        HQWineInfo info = (HQWineInfo) absInfo;

        MyProgressDialog dialog = new MyProgressDialog().init("", false);
        dialog.show();

        //是否有其他文件正在下载
        if (MWFileHelpers.isDownloading) {
            dialog.fail(cancelStr);
            return;
        }
        //新建线程开始下载
        new Thread(() -> {
            downloadDeb(info, dialog);
            downloadDeb(info.depInfo, dialog);
        }).start();
    }

    /**
     * 下载一个info对应的deb
     */
    private void downloadDeb(HQWineInfo info, MyProgressDialog dialog) {
        String skipStr = getS(RR.mw_dialog_download).split("\\$")[2];

        //info的fileName与下载前缀拼凑起来，然后下载主包和依赖包
        String mainDownloadUrl = downloadUrlPrefix + info.filename;
        String mainFileName = info.filename.split("/")[info.filename.split("/").length - 1];

        //检查本地文件是否存在
        boolean shouldDownload = true;
        File mainDebFile = new File(HQConfig.i.getTagFolder(info.getTagName()), mainFileName);
        if (mainDebFile.exists()) {
            try {
                config.checkSha256(info.getTagName());
                dialog.done(skipStr);
                shouldDownload = false;
            } catch (Exception e) {
                //如果本地压缩包不完整，删了重下
                mainDebFile.delete();
            }
        }
        //下载
        if (shouldDownload) {
            UiThread.post(() -> {
                dialog.init(mainDebFile.getName(), false);
                dialog.setMax(Integer.parseInt(info.size));
            });
            MWFileHelpers.download(
                    new File(config.getTagFolder(info.getTagName()), mainFileName),
                    config.resolveDownloadLink(mainDownloadUrl),
                    false,
                    dialog.defaultCallback);
        }
    }

    @Override
    protected void onDownloadRelease(MyProgressDialog dialog) {
        //先清空列表
        infoList.clear();
        config.getReleaseInfoFile().delete();
        UiThread.post(()->{
            dialog.init("Packages", false);
            dialog.show();
        });
        MWFileHelpers.download(config.getReleaseInfoFile(), releaseDownloadUrl, false, dialog.defaultCallback);

    }


    @Override
    protected void parseRelease() {

        if (!config.getReleaseInfoFile().exists()) {
            mCallback.ready(null);
            return;
        }
        String errMsg = null;

        try {
            List<String> allLines = FileHelpers.readAsLines(config.getReleaseInfoFile());
            List<HQWineInfo> tmpList = new ArrayList<>();
            InfoWrapper wrapper = new InfoWrapper();
            wrapper.lines = allLines;
            wrapper.pos = 0;

            //读取文本
            do {
                readOneInfo(wrapper);
                HQWineInfo info = wrapper.info;
                //otherosfs是正常的，其他可能是调试什么的。版本带~rc的是抢先体验，会造成多个info版本号完全相同。winehq是纯快捷方式不需要
                if (info.section.equals("otherosfs") && !info.version.contains("~rc") && !info.mpackage.contains("winehq"))
                    tmpList.add(info);

            } while (wrapper.pos < wrapper.lines.size());

            //把依赖包从列表中挪到主包中，
            for (int i = 0; i < tmpList.size(); i++) {
                HQWineInfo depInfo = tmpList.get(i);
                if (!depInfo.mpackage.endsWith("-i386"))
                    continue;

                tmpList.remove(i);
                i--;
                //寻找对应的主包
                int mainIndex = 0;
                while (!(tmpList.get(mainIndex).getTagName().equals(depInfo.getTagName().replace("-i386", ""))))
                    mainIndex++;
                assert tmpList.get(mainIndex).depInfo == null;
                tmpList.get(mainIndex).depInfo = depInfo;
            }
            Collections.sort(tmpList, new WineNameComparator());
            infoList.clear();
            infoList.addAll(tmpList);
        } catch (IOException e) {
            infoList.clear();
            errMsg = e.getMessage();
        }

        //回调要不写在父类里
        mCallback.ready(errMsg);
    }

    /**
     * 用于包裹一个读取完的info和当前指向List的位置
     */
    public static class InfoWrapper {
        //packages文件转为的全部文件行
        public List<String> lines;
        //当前读取到第几行
        public int pos;
        //读取出来的一个完整info
        public HQWineInfo info;
        //读取出来的info中的某一个属性
        public String oneProp;
    }
}
