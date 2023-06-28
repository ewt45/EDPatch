package com.example.datainsert.exagear.mutiWine.v2;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 用于Kron4ek github release api 获取到的json的类
 */
public class KronWineInfo {
    String name;
    List<Asset> assets;

    static class Asset{
        String name;
        int size;
        String updated_at="0000-00-00";
        String browser_download_url;

        public String getSizeInMB() {
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            return decimalFormat.format(size/1024f/1024);
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

    @NonNull
    @Override
    public String toString() {
        return "WineReleaseInfo{" +
                "name='" + name + '\'' +
                ", assets=" + assets.toString() +
                '}';
    }

    /**
     * 在下载对应版本的wine时，通过名称寻找到要下载的文件
     * @param name 文件名
     * @return 对应的Asset或null
     */
    public Asset getAssetByName(String name){
        for(Asset asset:assets){
            if(asset.name.contains(name))
                return asset;
        }
        return null ;
    }
}
