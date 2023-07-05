package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.RR.getS;

import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.example.datainsert.exagear.RR;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 对本地文件路径的定义，以及一些操作（校验，解压，删除）
 */
public abstract class ConfigAbstract {
    private static final String TAG= "ConfigAbstract";
    /**
     * rootfs路径
     */
     public File getImagePath(){
         ExagearImageAware aware = Globals.getApplicationState();
         assert aware != null;
         return aware.getExagearImage().getPath();
    }

    /**
     * 包含下载信息的文件
     */
    public abstract File getReleaseInfoFile();

    /**
     * 去掉首尾空格，中间空格换为 - ，字母全部小写。作为tag文件夹名称
     * @Deprecated 请使用WineInfo.getTagName
     */
    @Deprecated
    public  String formatTagName(String tagName){
        return tagName.trim().replace(" ", "-").toLowerCase(Locale.ROOT);
    };

    /**
     * 获取该类wine所属文件夹名，如：kron4ek winehq custom
     */
    public abstract String getHostName();

    /**
     * 获取该类wine的所在文件夹 可标识一类wine的最外层文件夹. 路径为：/opt/wineCollection/host名
     * <p/>
     * host名为{@link #getHostName()}的返回值
     * @return
     */
    public  File getHostFolder(){
        File file = new File(getImagePath(), "opt/wineCollection/"+getHostName());
        if (!file.exists())
            file.mkdirs();
        return file;
    }


    /**
     * 获取一个文件夹。该文件夹位于host目录下，包含一个版本wine的相关内容。
     * @param tagName tag名 需格式化
     * @return 对应的file对象，如果文件夹不存在会自动创建
     */
    public File getTagFolder(String tagName){
        File returnFile = new File(getHostFolder(),tagName);
        if (!returnFile.exists())
            returnFile.mkdirs();
        return returnFile;
    }

    /**
     * 获取一个tag文件夹中的已解压的wine目录，寻找规则为：在子目录中寻找是directory类型且包含./bin/wine 的file，找到就返回
     *
     * @param tagName tag文件夹名
     * @return wine目录file对象，不存在则返回null
     */
    public File getWineFolderByTag(String tagName){
        File tagFolder = getTagFolder(tagName);
        if(!tagFolder.exists())
            return null;
        for (File child : tagFolder.listFiles())
            if (child.isDirectory() && new File(child,"bin/wine").exists())
                return child;
        return null;
    }


    /**
     * 获取一个tag文件夹中的本地wine压缩包，
     * @param tagName tag文件夹名
     * @return wine压缩包file对象，不存在则返回空列表
     */
    public abstract List<File> getLocalArchivesByTag(String tagName) ;

    /**
     * 获取对应版本的wine的sha256校验
     * @param tagName
     * @return 若没找到校验码，返回一个空的列表
     */
    public abstract List<String> getSha256(String tagName);

    /**
     * 检查压缩包是否完好，如果不报错则说明校验完毕且没有损坏
     * @param tagName tag名
     */
    public void checkSha256(String tagName) throws Exception {
        String checkNoTxt = getS(RR.mw_dialog_checksum).split("\\$")[1]; //校验码或压缩包不存在
        String checkCorrupt = getS(RR.mw_dialog_checksum).split("\\$")[2]; //压缩包损坏，请尝试删除并重新下载


        List<String> corShaList = getSha256(tagName);
        List<File> archiveList = getLocalArchivesByTag(tagName);


        if(corShaList.size()==0 || archiveList.size() ==0 || corShaList.size()!=archiveList.size())
            throw new Exception(checkNoTxt);

        for(int i=0;i<corShaList.size();i++){
            String correctSha = corShaList.get(i);
            File archive = archiveList.get(i);
            if(!archive.exists())
                throw new Exception(checkNoTxt);


            try {
                //计算压缩包的sha256，与文本的值对比
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] tarSha = digest.digest(FileUtils.readFileToByteArray(archive));

                //获取的字节数组还不能直接用Arrays转，要一个一个转（也许是因为16进制问题？）
                StringBuilder builder = new StringBuilder();
                for (byte b : tarSha) {
                    //java.lang.Integer.toHexString() 方法的参数是int(32位)类型，
                    //如果输入一个byte(8位)类型的数字，这个方法会把这个数字的高24为也看作有效位，就会出现错误
                    //如果使用& 0XFF操作，可以把高24位置0以避免这样错误
                    String temp = Integer.toHexString(b & 0xFF);
                    if (temp.length() == 1) {
                        //1得到一位的进行补0操作
                        builder.append("0");
                    }
                    builder.append(temp);
                }
                String calSha = builder.toString();
                Log.d(TAG, "checkWineTarSum: \n正确sha=" + correctSha + "\n计算sha=" + calSha);
                if(!correctSha.equals(calSha))
                    throw new Exception(checkCorrupt);
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }




    }
    /**
     * 获取tag目录下的自定义信息info.txt
     */
    public Map<String,String> getInfoTxtByTag(String tagName){
        File tagFolder  = getTagFolder(tagName);
        if(!tagFolder.exists())
            return new HashMap<>();
        for(File child:tagFolder.listFiles()){
            if(!(child.isFile() && child.getName().equals("info.txt")))
                continue;
            try {
                List<String> lines = FileUtils.readLines(child);
                Map<String,String> infoMap = new HashMap<>();
                for(String s:lines){
                    String[] split = s.split("=");
                    if(split.length<2)
                        continue;
                    infoMap.put(split[0].trim(),split[1].trim());
                }
                return infoMap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    /**
     * 根据代理设置，返回对应的下载地址
     * @param url
     * @return
     */
    public abstract String resolveDownloadLink(String url);

    /**
     * 解压某个tag对应的wine。内部不会新建线程，所以请新建线程后再调用该方法
     *
     * @param tagName 该wine对应的WineInfo
     */
    public abstract void unpackArchive(String tagName) throws IOException;
}
