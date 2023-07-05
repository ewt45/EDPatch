package com.example.datainsert.exagear.mutiWine.v2;

import com.example.datainsert.exagear.QH;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomConfig extends ConfigAbstract{

    /**
     * 静态实例，用这个就行
     */
    public static CustomConfig i = new CustomConfig();
    /**
     * 包含下载信息的文件。文件位于 host目录/Package
     *
     * @return file对象
     */
    @Override
    public File getReleaseInfoFile() {
        throw new RuntimeException("不应调用该方法");
    }

    @Override
    public String getHostName() {
        return "custom";
    }

    /**
     * 获取一个tag文件夹中的本地wine压缩包，寻找规则为：在子目录中寻找是file类型且名字以.tar.xz结尾的file，
     * 如果改版作者没那么闲，这里应该只返回一个
     *<p/>
     * 该方法返回的是本地已存在的文件，而非需要下载的文件
     * @param tagName tag文件夹名
     * @return wine压缩包file对象，不存在则返回null
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
     * 规则：读取Packages中的信息，根据tagName获取两个校验码，第一个是体积小的那个如wine-stable，第二个是体积大的如wine-stable-i386
     * <p/>
     * 旧版本没有没有校验码文本
     *
     * @param tagName tag名
     * @return 若没找到校验码，返回一个空的列表
     */
    @Override
    public List<String> getSha256(String tagName) {
        List<String> lines;
        List<String> shaList = new ArrayList<>();

        try {
            lines = FileUtils.readLines(getReleaseInfoFile());
        } catch (IOException e) {
            e.printStackTrace();
            return shaList;
        }
        String pkg = tagName.split("_")[0];
        String version = tagName.split("_")[1];
        String depPkg  = pkg+"-i386";

        HQParser.InfoWrapper wrapper = new HQParser.InfoWrapper();
        wrapper.lines = lines;
        wrapper.pos = 0;

        //通过tag名寻找出对应的info
        do {
            HQParser.readOneInfo(wrapper);
            HQWineInfo info = wrapper.info;
            if(pkg.equals(wrapper.info.mpackage) && version.equals(info.version.split("~")[0]))
                shaList.add(0,info.sha256); //主包放第一位
            else if(depPkg.equals(wrapper.info.mpackage) && version.equals(info.version.split("~")[0]))
                shaList.add(info.sha256); //依赖包放第二位

        } while (wrapper.pos<wrapper.lines.size());

        return shaList;
    }

    @Override
    public String resolveDownloadLink(String url) {
        throw new RuntimeException("不应调用该方法");
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
