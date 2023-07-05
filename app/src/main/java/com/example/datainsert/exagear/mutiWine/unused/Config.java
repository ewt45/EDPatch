package com.example.datainsert.exagear.mutiWine.unused;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.mutiWine.v2.KronConfig;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 工具类
 */
public class Config {


    /**
     * 获取rootfs根路径。
     *
     * @return File对象
     */
    public static File getImagePath() {
        ExagearImageAware aware = Globals.getApplicationState();
        assert aware != null;
        return aware.getExagearImage().getPath();

    }

    /**
     * 获取kron4ek的release信息在本地保存的文件
     *
     * @return file对象
     */
    public static File getReleaseJsonFile() {
        return new File(getImagePath(), "opt/wineCollection/Kron4ek-releases.json");
    }

    /**
     * release的tag名，去掉首尾空格，中间空格换为 - ，字母全部小写。作为tag文件夹名称
     */
    public static String formatTagName(String tagName) {
        return tagName.trim().replace(" ", "-").toLowerCase(Locale.ROOT);
    }

    /**
     * 获取解压后的wine程序目录所在的文件夹
     *
     * @return file对象
     */
    public static File getTagParentFolder() {
        File file = new File(getImagePath(), "opt/wineCollection");
        if (!file.exists())
            file.mkdirs();
        return file;

    }

    /**
     * 获取该release tag对应的文件夹。路径格式如：/opt/wineCollection/tag名去除首尾空格 中间空格替换为- 全部转为小写
     *
     * @param tag tag名 无需格式化
     * @return 对应的file对象，如果文件夹不存在会自动创建
     */
    public static File getTagFolder(String tag) {
        File returnFile = new File(getImagePath(), "/opt/wineCollection/" + formatTagName(tag));
        if (!returnFile.exists())
            returnFile.mkdirs();
        return returnFile;
    }

    /**
     * 获取一个tag文件夹中的已解压的wine目录，寻找规则为：在子目录中寻找是directory类型的file，找到就返回
     *
     * @param tagFolder tag文件夹 的file对象
     * @return wine目录file对象，不存在则返回null
     */
    public static File getWineFolderFromTagFolder(File tagFolder) {
        if(!tagFolder.exists())
            return null;
        for (File child : tagFolder.listFiles())
            if (child.isDirectory() && new File(child,"bin/wine").exists())
                return child;
        return null;
    }

    /**
     * 获取一个tag文件夹中的wine压缩包，寻找规则为：在子目录中寻找是file类型且名字以.tar.xz结尾的file，找到就返回
     *
     * @param tagFolder tag文件夹 的file对象
     * @return wine压缩包file对象，不存在则返回null
     */
    public static File getTarFileFromTagFolder(File tagFolder) {
        for (File child : tagFolder.listFiles())
            if (child.isFile() && child.getName().endsWith(".tar.xz"))
                return child;
        return null;
    }

    /**
     * 获取一个tag文件夹中的校验码文本，寻找规则为：在子目录中寻找是file类型且名字包含sha256sums.txt的file，找到就返回
     * <p/>
     * 旧版本没有没有校验码文本
     *
     * @param tagFolder tag文件夹 的file对象
     * @return file对象，不存在则返回null
     */
    public static File getShaTxtFromTagFolder(File tagFolder) {
        for (File child : tagFolder.listFiles())
            if (child.isFile() && child.getName().contains("sha256sums.txt"))
                return child;
        return null;
    }

    /**
     * 获取tag目录下的自定义信息info.txt
     */
    public static Map<String,String> getInfoTxtInTagFolder(File tagFolder){
        if(!tagFolder.exists())
            return new HashMap<>();
        for(File child:tagFolder.listFiles()){
            if(!(child.isFile() && child.getName().equals("info.txt")))
                continue;
            try {
                List<String> lines = FileUtils.readLines(child);
                Map<String,String> infoMap = new HashMap<>();
                for(String s:lines){
                    String[] split = s.split("=");
                    if(split.length<2)
                        continue;
                    infoMap.put(split[0].trim(),split[1].trim());
                }
                return infoMap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    /**
     * 根据代理设置，返回对应的下载地址
     *
     * @param url
     * @return
     */
    public static String getGithubUrlByProxy(String url) {
        switch (QH.getPreference().getInt(KronConfig.PROXY_GITHUB_PREF_KEY, KronConfig.PROXY_GITHUB)) {
            case KronConfig.PROXY_KGITHUB: {
                return url.replace("https://github.com/", "https://kgithub.com/");
            }
            case KronConfig.PROXY_GHPROXY: {
                //https://ghproxy.com/https://github.com/Kron4ek/Wine-Builds/releases/download/8.7/sha256sums.txt
                return "https://ghproxy.com/" + url;
            }
        }
        ;
        return url;
    }

    //    /**
//     * 获取下载的wine压缩包所在的文件夹
//     * @return file对象
//     */
//    @Deprecated
//    public static File getWineTarFolder(){
//        return  new File(getImagePath(),"opt/wineCollection/winePackages");
//    }

}
