package com.example.datainsert.exagear.mutiWine.v2;

import static java.nio.charset.StandardCharsets.US_ASCII;

import android.util.Log;

import com.eltechs.axs.helpers.SafeFileHelpers;
import com.example.datainsert.exagear.QH;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HQConfig extends ConfigAbstract {
    public final static int PROXY_WINEHQ = 0;
    public final static int PROXY_TSINGHUA = 1;
    private static final String TAG = "HQConfig";
    public static String PROXY_WINEHQ_PREF_KEY = "PROXY_WINEHQ";
    /**
     * 静态实例，用这个就行
     */
    public static HQConfig i = new HQConfig();

    /**
     * 包含下载信息的文件。文件位于 host目录/Package
     *
     * @return file对象
     */
    @Override
    public File getReleaseInfoFile() {
        return new File(getHostFolder(), "Packages");
    }

    @Override
    public String getHostName() {
        return "winehq";
    }


    /**
     * 获取一个tag文件夹中的本地wine安装包（deb），寻找规则为：在子目录中寻找是file类型且名字以.deb结尾的file，
     * 正常应该返回两个，第一个是体积小的如wine-stable，第二个是体积大的如wine-stable-i386
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
            if (child.isFile() && child.getName().endsWith(".deb"))
                returnList.add(child);
        //排序，规定体积小的那个在前面
        if (returnList.size() == 2 && returnList.get(0).length() > returnList.get(1).length()) {
            File larger = returnList.remove(0);
            returnList.add(larger);
        }
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
        String depPkg = pkg + "-i386";

        HQParser.InfoWrapper wrapper = new HQParser.InfoWrapper();
        wrapper.lines = lines;
        wrapper.pos = 0;

        //通过tag名寻找出对应的info
        do {
            HQParser.readOneInfo(wrapper);
            HQWineInfo info = wrapper.info;
            if (pkg.equals(wrapper.info.mpackage) && version.equals(info.version.split("~")[0]))
                shaList.add(0, info.sha256); //主包放第一位
            else if (depPkg.equals(wrapper.info.mpackage) && version.equals(info.version.split("~")[0]))
                shaList.add(info.sha256); //依赖包放第二位

        } while (wrapper.pos < wrapper.lines.size());

        return shaList;
    }

    @Override
    public String resolveDownloadLink(String url) {
        //https://dl.winehq.org/wine-builds/ubuntu/dists/bionic/main/binary-i386/
        //https://mirrors.tuna.tsinghua.edu.cn/wine-builds/ubuntu/dists/bionic/main/binary-i386/
        if (QH.getPreference().getInt(PROXY_WINEHQ_PREF_KEY, PROXY_WINEHQ) == PROXY_TSINGHUA) {
            return url.replace("https://dl.winehq.org/", "https://mirrors.tuna.tsinghua.edu.cn/");
        }
        return url;
    }

    @Override
    public void unpackArchive(String tagName) throws IOException {
        File tagFolder = getTagFolder(tagName);
        //如果已经存在解压后的文件夹（不需要新建文件夹，压缩包解压出来就是一个文件夹
        for (File child : tagFolder.listFiles())
            if (child.isDirectory())
                FileUtils.deleteDirectory(child);//删除重新解压

        for (File debFile : getLocalArchivesByTag(tagName)) {
            //解压tar.xz
            try (ByteArrayInputStream bis = new ByteArrayInputStream(readDataTarXzFromDeb(debFile))) {
                MWFileHelpers.decompressTarXz(bis, tagFolder);
            }
        }

        //解压之后应该会出现opt和usr文件夹，把opt里的wine-devel文件夹拿出来，usr文件夹删掉
        FileUtils.deleteDirectory(new File(tagFolder, "usr"));
        File extractOpt = new File(tagFolder, "opt");
        FileUtils.copyDirectory(extractOpt,tagFolder);//这个会将解压出来的opt文件夹的全部子文件(夹）复制到tag目录下
        //删除解压出的opt目录
        FileUtils.deleteDirectory(extractOpt);

    }

    /**
     * 从一个deb文件中读取其中的data.tar.xz部分。要求必须需是.xz的压缩方式
     *
     * @param debFile deb文件
     * @return 对应data的字节数组
     * @throws IOException 文件头错误，endMarker错误，非xz压缩 或文件io错误
     */
    private byte[] readDataTarXzFromDeb(File debFile) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(debFile);
        byte[] dataBytes = new byte[0]; //存储data.tar.xz 的字节数组

        //捕捉一下数组越界吧，虽然一般应该不会
        try {
            int pos = 0;
            String rHeader = "!<arch>";
            //deb文件头
            byte[] headers = Arrays.copyOfRange(bytes, pos, pos + rHeader.length());
            //直接new 一个String，设定ascii格式，就将byte数组转为字符串了=-=
            if (!"!<arch>".equals(new String(headers, US_ASCII))) {
                throw new IOException("不是deb文件");
            }
            pos = pos + rHeader.length() + 1; //还有个换行

            String memberName;
            int fileSize;
            do {
                //成员名，应该16字节
                memberName = new String(Arrays.copyOfRange(bytes, pos, pos + 16)).trim();
//                System.out.println("成员：" + memberName);
                //如果不是需要解压的内容，就读取其大小并跳过这些长度
                /*
                char    fileName[16];
                char    modification_timestamp[12];
                char    ownerID[6];
                char    groupID[6];
                char    fileMode[8];
                char    fileSize[10];
                char    endMarker[2];
                 */
                pos = pos + 16 + 12 + 6 + 6 + 8; //读文件大小
                String sizeStr = new String(Arrays.copyOfRange(bytes, pos, pos + 10)).trim();
                pos = pos + 10; //读结尾标识
                String endMarker = new String(Arrays.copyOfRange(bytes, pos, pos + 2));
                //根据模版，只有结尾标识为这个的时候才读取下面的文件内容，不知道有什么说法
                if (!endMarker.equals("`\n")) {
                    throw new IOException("成员" + memberName + "的endmarker" + endMarker + "不是`\\n");
                }
                pos = pos + 2; //读取内容
                int sizeInt = Integer.parseInt(sizeStr);
                dataBytes = Arrays.copyOfRange(bytes, pos, pos + sizeInt);
                pos = pos + sizeInt;
                //对齐偶数字节
                if ((pos & 1) != 0)
                    pos++;
            } while (!memberName.startsWith("data.tar"));

//            System.out.println("找到data.tar");
            if (!memberName.endsWith(".xz")) {
                throw new IOException("data.tar不是xz压缩");
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return dataBytes;
    }
}
