package com.example.datainsert.exagear.mutiWine.v2;

import android.util.Log;

import com.eltechs.axs.helpers.SafeFileHelpers;
import com.example.datainsert.exagear.QH;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KronConfig extends ConfigAbstract {
    public final static int PROXY_GITHUB = 0;
    public final static int PROXY_GHPROXY = 2;
    public final static int PROXY_KGITHUB = 1;
    private static final String TAG = "KronConfig";
    public static String PROXY_GITHUB_PREF_KEY = "PROXY_GITHUB";
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
     * 获取一个tag文件夹中的本地wine压缩包，寻找规则为：在子目录中寻找是file类型且名字以.tar.xz结尾的file，添加到列表中返回
     * <p/>
     * 该方法返回的是本地已存在的文件，而非需要下载的文件
     *
     * @param tagName tag文件夹名
     * @return wine压缩包file对象，不存在则返回空列表
     */
    @Override
    public List<File> getLocalArchivesByTag(String tagName) {
        List<File> returnList = new ArrayList<>();
        for (File child : getTagFolder(tagName).listFiles())
            if (child.isFile() && child.getName().endsWith(".tar.xz"))
                returnList.add(child);
        return returnList;
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
        switch (QH.getPreference().getInt(PROXY_GITHUB_PREF_KEY, PROXY_GITHUB)) {
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

    @Override
    public void unpackArchive(String tagName) throws IOException {
        File tagFolder = getTagFolder(tagName);
        //如果已经存在解压后的文件夹（不需要新建文件夹，压缩包解压出来就是一个文件夹
        for (File child : tagFolder.listFiles())
            if (child.isDirectory())
                FileUtils.deleteDirectory(child);//删除重新解压

        for (File wineTarFile : getLocalArchivesByTag(tagName)) {
            try(FileInputStream fis = new FileInputStream(wineTarFile)){
                MWFileHelpers.decompressTarXz(fis,tagFolder);
            }
        }
    }
}
