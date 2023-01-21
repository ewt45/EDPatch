package com.example.datainsert.exagear.mutiWine;

public class WineVersion {
    public String name; //尽量不要包含特殊字符，会用于按钮显示，  算了吧，wineprefix文件夹名称再定义一个吧
    public String installPath;    //安装路径，此路径下应该有./bin/wine和./lib
    public String patternPath; //wineprefix路径

    public WineVersion(String[] arrs) {
        name = arrs[0];
        installPath =arrs[1];
        patternPath =arrs[2];
    }

    @Override
    public String toString() {
        return "WineVersion{" +
                "name='" + name + '\'' +
                ", installPath='" + installPath + '\'' +
                ", patternPath='" + patternPath + '\'' +
                '}';
    }
}
