package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.RR.getS;

import android.support.annotation.NonNull;

import com.example.datainsert.exagear.RR;

import java.text.DecimalFormat;

public class HQWineInfo implements WineInfo {
    public String mpackage;
    public String architecture;
    public String version;
    public String section;
    public String installedSize;
    public String depends;
    public String filename;
    public String size;
    public String sha256;

    //依赖的winepackagesinfo（就是带i386的那个）
    public HQWineInfo depInfo;


    /**
     * 返回表示该版本wine的tag名，格式为：package+ 下划线 +version第一个~前的部分。例如：wine-staging-7.1
     * <br/>
     * 依赖包的tagName会去掉-i386,与其主包的tagname相等
     * @return
     */
    @Override
    public String getTagName() {
        String[] split = version.split("~");
        return mpackage.replace("-i386","") + "_" + (split.length > 0 ? split[0] : "");
    }

    @Override
    public String getDescription() {
        int totalSize = Integer.parseInt(size);
        if(depInfo!=null)
            totalSize += Integer.parseInt(depInfo.size);
        return  new DecimalFormat("#.00").format(totalSize/1024f/1024) + getS(RR.mw_dataSizeMB);
    }

    public String getSizeInMB() {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(Integer.parseInt(size) / 1024f / 1024);
    }

    @NonNull
    @Override
    public String toString() {
        return  getTagName();
//        return "HQWineInfo{" +
//                "mpackage='" + mpackage + '\'' +
//                ", version='" + version + '\'' +
//                ", architecture='" + architecture + '\'' +
//                ", filename='" + filename + '\'' +
//                '}';
    }
}
