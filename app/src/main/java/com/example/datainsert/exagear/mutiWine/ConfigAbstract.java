package com.example.datainsert.exagear.mutiWine;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ExagearImageAware;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class ConfigAbstract {
    /**
     * rootfs路径
     */
     public File getImagePath(){
         ExagearImageAware aware = Globals.getApplicationState();
         assert aware != null;
         return aware.getExagearImage().getPath();
    }

    /**
     * 包含下载信息的文件
     */
    public abstract File getReleaseInfoFile();

    /**
     * 去掉首尾空格，中间空格换为 - ，字母全部小写。作为tag文件夹名称
     */
    public  String formatTagName(String tagName){
        return tagName.trim().replace(" ", "-").toLowerCase(Locale.ROOT);

    };

    /**
     * 获取该类wine所属文件夹名，如：kron4ek winehq custom
     */
    public abstract String getHostName();

    /**
     * 获取该类wine的所在文件夹 可标识一类wine的最外层文件夹. 路径为：/opt/wineCollection/host名
     * <p/>
     * host名为{@link #getHostName()}的返回值
     * @return
     */
    public  File getHostFolder(){
        File file = new File(getImagePath(), "opt/wineCollection/"+getHostName());
        if (!file.exists())
            file.mkdirs();
        return file;
    }


    /**
     * 获取一个文件夹。该文件夹位于host目录下，包含一个版本wine的相关内容
     * @param tagName tag名 无需格式化
     * @return 对应的file对象，如果文件夹不存在会自动创建
     */
    public File getTagFolder(String tagName){
        File returnFile = new File(getHostFolder(),formatTagName(tagName));
        if (!returnFile.exists())
            returnFile.mkdirs();
        return returnFile;
    }

    /**
     * 获取一个tag文件夹中的已解压的wine目录，寻找规则为：在子目录中寻找是directory类型且包含./bin/wine 的file，找到就返回
     *
     * @param tagName tag文件夹名
     * @return wine目录file对象，不存在则返回null
     */
    public File getWineFolderByTag(String tagName){
        File tagFolder = getTagFolder(tagName);
        if(!tagFolder.exists())
            return null;
        for (File child : tagFolder.listFiles())
            if (child.isDirectory() && new File(child,"bin/wine").exists())
                return child;
        return null;
    }


    /**
     * 获取一个tag文件夹中的wine压缩包，
     * @param tagName tag文件夹名
     * @return wine压缩包file对象，不存在则返回null
     */
    public abstract File getArchiveByTag(String tagName) ;

    /**
     * 获取对应版本的wine的sha256校验
     * @param tagName
     * @return 若没找到校验码，返回一个空的列表
     */
    public abstract List<String> getSha256(String tagName);
    /**
     * 获取tag目录下的自定义信息info.txt
     */
    public Map<String,String> getInfoTxtByTag(String tagName){
        File tagFolder  = getTagFolder(tagName);
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
     * @param url
     * @return
     */
    public abstract String resolveDownloadLink(String url);
}
