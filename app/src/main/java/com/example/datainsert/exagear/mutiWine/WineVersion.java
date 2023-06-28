package com.example.datainsert.exagear.mutiWine;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class WineVersion {
    //wine版本信息列表
    public static ArrayList<WineVersion> wineList;
    /**
     * 用于按钮显示，不一定表示对应的tag名
     */
    public String name; //尽量不要包含特殊字符，会用于按钮显示，  算了吧，wineprefix文件夹名称再定义一个吧
    public String installPath;    //安装路径，此路径下应该有./bin/wine和./lib
    /**
     * @Deprecated 取消自定义pattern功能，使用默认的/opt/guestcont-pattern
     */
    @Deprecated
    public String patternPath; //wineprefix路径

    public WineVersion(String[] arrs) {
        name = arrs[0];
        installPath =arrs[1];
        patternPath =arrs[2];
    }
    public WineVersion(String name,String installPath,String patternPath){
        this.name = name;
        this.installPath = installPath;
        this.patternPath = patternPath;
    }

    /**
     * 从z:/opt/wineCollection下寻找符合条件的文件夹，初始化wine版本列表
     */
    public static void initList() {
        wineList = new ArrayList<>();
        //wine文件夹父目录
        File parentFolder = KronConfig.i.getHostFolder();
        if (parentFolder.exists()) {
            //获取wine程序文件夹
            for (File tagFolder : parentFolder.listFiles()) {
                //要求是文件夹
                if (!tagFolder.isDirectory())
                    continue;
                //如果子目录是文件夹且有bin/wine这个文件，则属于wine程序文件夹
                File wineFolder = KronConfig.i.getWineFolderByTag(tagFolder.getName());
                if (wineFolder == null)
                    continue;
                //初始化填充默认内容
                String wineFolderPath = wineFolder.getAbsolutePath();
                String customName = KronConfig.i.getInfoTxtByTag(tagFolder.getName()).get("name");//如果有info.txt，读取其内容
                WineVersion wineVersion = new WineVersion(
                        customName != null ? customName : wineFolder.getName().replace("-x86", ""),
                        wineFolderPath.substring(wineFolderPath.indexOf("/opt/wineCollection/")),
                        "/opt/guestcont-pattern");

                wineList.add(wineVersion);

            }
        }
        //如果一个都没找到，至少添加一个
        if (wineList.isEmpty())
            wineList.add(new WineVersion("新建", "", "/opt/guestcont-pattern"));

        //按名称 排下序
        Collections.sort(wineList, new WineNameComparator());
    }

    @NonNull
    @Override
    public String toString() {
        return "WineVersion{" +
                "name='" + name + '\'' +
                ", installPath='" + installPath + '\'' +
                ", patternPath='" + patternPath + '\'' +
                '}';
    }




}
