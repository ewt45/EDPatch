package com.example.datainsert.exagear.mutiWine;

import com.example.datainsert.exagear.QH;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KronConfig extends ConfigAbstract {
    public final static int PROXY_GITHUB = 0;
    public final static int PROXY_GHPROXY = 2;
    public final static int PROXY_KGITHUB = 1;
    public static String PROXY_PREF_KEY = "PROXY_GITHUB";
    /**
     * 静态实例，用这个就行
     */
    public static KronConfig i = new KronConfig();
    /**
     * 包含下载信息的文件。文件位于 host目录/release.json
     *
     * @return file对象
     */
    @Override
    public File getReleaseInfoFile() {
        return new File(getHostFolder(), "release.json");
    }

    @Override
    public String getHostName() {
        return "kron4ek";
    }

    /**
     * 获取一个tag文件夹中的wine压缩包，寻找规则为：在子目录中寻找是file类型且名字以.tar.xz结尾的file，找到就返回
     *
     * @param tagName tag文件夹名
     * @return wine压缩包file对象，不存在则返回null
     */
    @Override
    public File getArchiveByTag(String tagName) {
        for (File child : getTagFolder(tagName).listFiles())
            if (child.isFile() && child.getName().endsWith(".tar.xz"))
                return child;
        return null;
    }

    /**
     * 获取对应版本的wine的sha256校验
     * 规则：tag目录下 文件名包含sha256的文件
     * <p/>
     * 旧版本没有没有校验码文本
     *
     * @param tagName
     * @return 若没找到校验码，返回一个空的列表
     */
    @Override
    public List<String> getSha256(String tagName) {
        List<String> returnList = new ArrayList<>();
        File checkFile = null;
        for (File child : getTagFolder(tagName).listFiles())
            if (child.isFile() && child.getName().contains("sha256"))
                checkFile = child;

        if (checkFile == null)
            return returnList;

        try {
            List<String> list = FileUtils.readLines(checkFile, StandardCharsets.UTF_8);
            for (String s : list) {
                if (s.contains("x86") && s.contains("tar.xz") && !s.contains("staging")) {
                    String[] split = s.split(" ");
                    if (split.length > 0)
                        returnList.add(split[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnList;
    }

    @Override
    public String resolveDownloadLink(String url) {
        switch (QH.getPreference().getInt(PROXY_PREF_KEY, PROXY_GITHUB)) {
            case PROXY_KGITHUB: {
                return url.replace("https://github.com/", "https://kgithub.com/");
            }
            case PROXY_GHPROXY: {
                //https://ghproxy.com/https://github.com/Kron4ek/Wine-Builds/releases/download/8.7/sha256sums.txt
                return "https://ghproxy.com/" + url;
            }
        }
        ;
        return url;
    }
}
