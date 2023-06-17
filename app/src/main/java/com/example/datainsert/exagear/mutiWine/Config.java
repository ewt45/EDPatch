package com.example.datainsert.exagear.mutiWine;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ExagearImageAware;

import java.io.File;
import java.util.Locale;

/**
 * 工具类
 */
public class Config {
    /**
     * 获取rootfs根路径。
     * @return File对象
     */
    public static File getImagePath(){
        ExagearImageAware aware = Globals.getApplicationState();
        assert aware != null;
        return aware.getExagearImage().getPath();

    }

    /**
     * 获取kron4ek的release信息在本地保存的文件
     * @return file对象
     */
    public static File getReleaseJsonFile(){
        return new File(Config.getImagePath(), "opt/wineCollection/Kron4ek-releases.json");
    }

    /**
     * 获取下载的wine压缩包所在的文件夹
     * @return file对象
     */
    @Deprecated
    public static File getWineTarFolder(){
        return  new File(getImagePath(),"opt/wineCollection/winePackages");
    }

    /**
     * 获取解压后的wine程序目录所在的文件夹
     * @return file对象
     */
    public static File getTagParentFolder(){
        return  new File(getImagePath(),"opt/wineCollection");

    }

    /**
     * 获取该release tag对应的文件夹。路径格式如：/opt/wineCollection/tag名去除首尾空格 中间空格替换为- 全部转为小写
     * @param tag tag名
     * @return 对应的file对象，如果文件夹不存在会自动创建
     */
    public static File getTagFolder(String tag){
        File returnFile = new File(getImagePath(),"/opt/wineCollection/"+tag.trim().replace(" ","-").toLowerCase(Locale.ROOT));
        if(!returnFile.exists())
            returnFile.mkdirs();
        return  returnFile;
    }

    public static File getWineFolderFromTagFolder(File tagFolder){
        for (File child : tagFolder.listFiles())
            if (child.isDirectory())
                return child;
        return null;
    }

    public static File getTarFileFromTagFolder(File tagFolder){
        for (File child : tagFolder.listFiles())
            if (child.isFile() && child.getName().endsWith(".tar.xz"))
                return child;
        return null;
    }

    public static File getShaTxtFromTagFolder(File tagFolder){
        for (File child : tagFolder.listFiles())
            if (child.isFile() && child.getName().contains("sha256sums.txt"))
                return child;
        return null;
    }
}
