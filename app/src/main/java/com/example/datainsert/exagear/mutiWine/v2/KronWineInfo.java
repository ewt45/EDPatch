package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.RR.getS;

import android.support.annotation.NonNull;

import com.example.datainsert.exagear.RR;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * 用于Kron4ek github release api 获取到的json的类
 */
public class KronWineInfo implements WineInfo {
    private String name;
    List<Asset> assets;

    @Override
    public String getTagName() {
        return name.trim().replace(" ", "-").toLowerCase(Locale.ROOT);
    }

    /**
     * 在下载对应版本的wine时，通过名称寻找到要下载的文件
     *
     * @param name 文件名
     * @return 对应的Asset或null
     */
    public Asset getAssetByName(String name) {
        for (Asset asset : assets) {
            if (asset.name.contains(name))
                return asset;
        }
        return null;
    }


    @Override
    public String getDescription() {
        //去掉首尾空格，中间空格变为横杠，字母小写，结尾加上.tar.xz
        String filename = getTagName() + "-x86.tar.xz";
        //检查服务器是否存在tag名-x86的文件 (这个放到parser里 获取infoList的时候检查吧，没有的直接删掉）
        final KronWineInfo.Asset asset = getAssetByName(filename);
        if(asset==null)
            return "";
        return  asset.getSizeInMB() + getS(RR.mw_dataSizeMB) + "  |  " + asset.updated_at.substring(0, 10);
    }

    @NonNull
    @Override
    public String toString() {
        return "WineReleaseInfo{" +
                "name='" + name + '\'' +
                ", assets=" + assets.toString() +
                '}';
    }

    public void setName(String message) {
        name  = message;
    }


    static class Asset {
        String name;
        int size;
        String updated_at = "0000-00-00";
        String browser_download_url;

        public String getSizeInMB() {
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            return decimalFormat.format(size / 1024f / 1024);
        }

        @NonNull
        @Override
        public String toString() {
            return "Asset{" +
                    "name='" + name + '\'' +
                    ", size(MB)=" + getSizeInMB() +
                    ", browser_download_url='" + browser_download_url + '\'' +
                    '}';
        }
    }


}
