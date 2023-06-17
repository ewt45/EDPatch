package com.example.datainsert.exagear.mutiWine.v2;

import android.util.Log;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.UiThread;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * 网络下载。github
 */
public class GithubDownload {
    private static final String TAG = "GithubDownload";
    private static boolean isDownloadingReleaseInfo = false;
    private static boolean isDownloading = false; //标记当前是否正在下载

    public GithubDownload() {

    }

    public static void download(File savedFile, String downloadUrl){
        download(savedFile,downloadUrl,false);
    }

    //没法同时下载checksum和wine压缩包。要么用线程池要么线程创建放函数外面吧

    /**
     * 下载一个文件。请勿在主线程调用该函数
     * @param savedFile 保存在本地的位置
     * @param downloadUrl 下载链接（默认https格式）
     * @param append 如果为true，下载内容会被追加到文件尾而不是开头
     */
    public static void download(File savedFile, String downloadUrl, boolean append) {
        if (isDownloading) {
            UiThread.post(()->AndroidHelpers.toastShort("有下载正在进行中，无法新建下载"));
            return;
        }
        isDownloading = true;

        String filename = savedFile.getName();
        //检查是否能连接github
        //新建线程开始下载
        try {

            URL url = new URL(downloadUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            int resCode = conn.getResponseCode();
            if (resCode == 200) {
                UiThread.post(() -> AndroidHelpers.toastShort("开始下载"));
                int size = conn.getContentLength();//获取到最大值之后设置到进度条的MAX
                //开始下载
                byte[] bytes = new byte[1024];//可以设置大点提高下载速度
                int len = -1;
                try (InputStream in = conn.getInputStream();
                     FileOutputStream out = new FileOutputStream(savedFile, append);
                ) {
                    int copylength = IOUtils.copy(in, out);
                    Log.d(TAG, String.format("download: 复制长度：%d", copylength));
                }
                UiThread.post(() -> AndroidHelpers.toastShort(filename + "下载成功"));
            } else {
                throw new Exception("responseCode=" + resCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            UiThread.post(() -> AndroidHelpers.toastShort(filename + "下载失败, " + e.getMessage()));
            savedFile.delete();
        } finally {
            isDownloading = false;
        }

    }



    /**
     * 检查下载的wine压缩包 是否完整
     * <p/>
     * 旧版本没有sha256.txt，所以校验完整性不应该阻碍核心操作继续
     * @return 是否完整
     */
    public static boolean checkWineTarSum(File tarFile) {
        File checksumFile = new File(tarFile.getParentFile(),"sha256sums.txt");
        //校验码文本不存在
        if(!checksumFile.exists() || !tarFile.exists())
            return false;

        try {
            List<String> list  = FileUtils.readLines(checksumFile, StandardCharsets.UTF_8);
            for(String s:list){
                if(s.contains(tarFile.getName())){
                    //计算压缩包的sha256，与文本的值对比
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] tarSha = digest.digest(FileUtils.readFileToByteArray(tarFile));

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
                    Log.d(TAG, "checkWineTarSum: \n正确sha="+s.substring(0,64)+"\n计算sha="+calSha);
                    return s.substring(0, 64).equals(calSha) ;
                }
            }
        } catch (IOException |NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private void test() {

    }
}
