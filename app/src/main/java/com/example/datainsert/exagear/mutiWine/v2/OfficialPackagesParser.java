package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.mutiWine.v2.OfficialBuildAdapter.Branch.STABLE;

import com.eltechs.axs.Globals;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.FileHelpers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OfficialPackagesParser {
    File packageFile = new File(Globals.getAppContext().getFilesDir(), "image/opt/wineCollection/winePackages/official-Packages");

    int pos = 0;
    private final List<WinePackagesInfo> stableList= new ArrayList<>();
    private final List<WinePackagesInfo> develList= new ArrayList<>();
    private final List<WinePackagesInfo> stagingList= new ArrayList<>();
    private final OfficialPackagesParser.Callback mCallback;


    public OfficialPackagesParser(Callback callback) {
        mCallback = callback;
        refresh();

    }

    /**
     * 重新下载Packages
     */
    public void refresh() {
        new Thread(() -> {
            download();
            try {
                parsePackages();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCallback.ready();
        }
        ).start();
    }

    public List<WinePackagesInfo> getListByBranch(OfficialBuildAdapter.Branch branch){
        switch (branch){
            case STABLE:
                return stableList;
            case DEVEL:
                return develList;
            case STAGING:
                return stagingList;
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 读取有关wine的信息的Package文件，并将数据记录在三个分支的列表
     */
    private void parsePackages() throws IOException {

        Assert.isTrue(packageFile.exists(),"package文件不存在");
        List<String> allLines = FileHelpers.readAsLines(packageFile);
        List<WinePackagesInfo> winePackagesInfos = new ArrayList<>();
        pos = 0;

        while (pos < allLines.size()) {
            WinePackagesInfo info = new WinePackagesInfo();
            info.Package = readContent(allLines, "Package");
            info.Version = readContent(allLines, "Version");
            info.Section = readContent(allLines, "Section");
            info.InstalledSize = readContent(allLines, "Installed-Size");
            info.Depends = readContent(allLines, "Depends");
            info.Filename = readContent(allLines, "Filename");
            info.Size = readContent(allLines, "Size");
            info.MD5sum = readContent(allLines, "MD5sum");
            winePackagesInfos.add(info);
        }


        //按种类分类
        for (WinePackagesInfo info : winePackagesInfos) {
            if (info.Version.contains("~rc"))
                continue;
            switch (info.Package) {
                case "wine-stable":
                    stableList.add(info);
                    break;
                case "wine-devel":
                    develList.add(info);
                    break;
                case "wine-staging":
                    stagingList.add(info);
                    break;
            }
        }

        Comparator<WinePackagesInfo> listComparator = new Comparator<WinePackagesInfo>() {
            @Override
            public int compare(WinePackagesInfo o1, WinePackagesInfo o2) {
                String[] versionSplit1 = o1.Version.split("~", 2)[0].split("\\.");
                String[] versionSplit2 = o2.Version.split("~", 2)[0].split("\\.");
                int compare = 0;
                //先比第一位
                if (versionSplit1.length > 0 && versionSplit2.length > 0) {
                    compare = compareStrFormatInt(versionSplit1[0], versionSplit2[0]);
                }
                if (compare != 0)
                    return compare;
                //再比第二位
                if (versionSplit1.length < 2 || versionSplit2.length < 2) {
                    return versionSplit1.length - versionSplit2.length;
                } else {
                    compare = compareStrFormatInt(versionSplit1[1], versionSplit2[1]);
                }
                if (compare != 0)
                    return compare;
                //再比第三位
                if (versionSplit1.length < 3 || versionSplit2.length < 3) {
                    return versionSplit1.length - versionSplit2.length;
                } else {
                    compare = compareStrFormatInt(versionSplit1[2], versionSplit2[2]);
                }
                return compare;

            }
        };
        Collections.sort(stableList, listComparator);
        Collections.sort(develList, listComparator);
        Collections.sort(stagingList, listComparator);


//        for (WinePackagesInfo info : stableList)
//            System.out.println(info.getVersion());
//        for (WinePackagesInfo info : develList)
//            System.out.println(info.getVersion());
//        for (WinePackagesInfo info : stagingList)
//            System.out.println(info.getVersion());


    }

    /**
     * 下载Packages
     *
     * @return
     */
    private boolean download() {
        if (packageFile.exists())
            return true;
        else
            throw new RuntimeException("还没实现下载功能");
    }

    /**
     * 从Packages中按顺序找到需要的属性并返回
     *
     * @param allLines
     * @param header
     * @return
     */
    private String readContent(List<String> allLines, String header) {
        while (pos < allLines.size()) {
            if (allLines.get(pos).startsWith(header))
                return allLines.get(pos).substring(header.length() + 2);//去掉冒号和空格
            pos++;
        }
        return "";
    }

    /**
     * 比较两个字符串转为数字后的大小，由于可能转换失败所以单独使用一个函数来操作了
     *
     * @param s1
     * @param s2
     * @return
     */
    private int compareStrFormatInt(String s1, String s2) {
        int i1 = 0;
        int i2 = 0;
        try {
            i1 = Integer.parseInt(s1);
        } catch (Exception ignored) {
        }
        try {
            i2 = Integer.parseInt(s2);
        } catch (Exception ignored) {
        }
        return i1 - i2;
    }

    interface Callback{
        public void ready();
    }
}
