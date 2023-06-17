package com.example.datainsert.exagear.mutiWine.v2;

public class WinePackagesInfo {
    public String Package;
    public String Version;
    public String Section;
    public String InstalledSize;
    public String Depends;
    public String Filename;
    public String Size;
    public String MD5sum;

    //依赖的winepackagesinfo（就是带i386的那个）
    public WinePackagesInfo SubInfo;

    /**
     * 返回版本号和类型（devel,staging,stable)
     * @return
     */
    public String getVersion(){
        return Version.split("~",2)[0] +" "+ Package;
    }




}
