package com.ewt45.patchapp.model;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * 用于检查更新时，从 github 获取的release信息
 */
public class GithubReleaseData {
    public String html_url; //release页面，用浏览器打开链接
    public String tag_name; //版本号 (1.0.2 这样的格式）
    public List<Asset> assets;

    public static class Asset{
        public String name; //用于寻找应该下载哪个文件（后缀为.apk的文件）
        public String browser_download_url; //用于下载链接

        @NonNull
        @Override
        public String toString() {
            return "Asset{" +
                    "name='" + name + '\'' +
                    ", browser_download_url='" + browser_download_url + '\'' +
                    '}';
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "GithubReleaseData{" +
                "html_url='" + html_url + '\'' +
                ", tag_name='" + tag_name + '\'' +
                ", assets=" + assets +
                '}';
    }
}
