package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.RR.getS;

import android.util.Log;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.UiThread;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.mutiWine.KronConfig;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * 网络下载。github
 */
public class DownloadFileHelper {
    private static final String TAG = "GithubDownload";
    static boolean isDownloading = false; //标记当前是否正在下载
    private static boolean isDownloadingReleaseInfo = false;


    /**
     * 下载release的一个asset
     */
    public static void downloadReleaseAsset(String tagName, KronWineInfo.Asset asset, MyProgressDialog.Callback callback) {

        download(
                new File(KronConfig.i.getTagFolder(tagName), asset.name),
                KronConfig.i.resolveDownloadLink(asset.browser_download_url),
                false,
                callback);
    }


    //没法同时下载checksum和wine压缩包。要么用线程池要么线程创建放函数外面吧

    /**
     * 下载一个文件。请勿在主线程调用该函数
     *
     * @param savedFile   保存在本地的位置
     * @param downloadUrl 下载链接（默认https格式）
     * @param append      如果为true，下载内容会被追加到文件尾而不是开头
     */
    public static void download(File savedFile, String downloadUrl, boolean append, MyProgressDialog.Callback callback) {
        MyProgressDialog.Callback finalCallback = callback != null? callback:message -> {};

        String successStr = getS(RR.mw_dialog_download).split("\\$")[0];
        String failedStr = getS(RR.mw_dialog_download).split("\\$")[1];
        String cancelStr = getS(RR.mw_dialog_download).split("\\$")[3];

        if (isDownloading) {
            UiThread.post(() -> AndroidHelpers.toastShort(cancelStr));
            return;
        }
        isDownloading = true;
        Log.d(TAG, "download: 下载文件：" + downloadUrl + "\n保存在" + savedFile);
        String filename = savedFile.getName();
        String doneMsg = filename + ' ' + successStr;
        //检查是否能连接github
        //新建线程开始下载
        HttpsURLConnection conn = null;
        try {

            URL url = new URL(downloadUrl);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            int resCode = conn.getResponseCode();
            if (resCode == 200) {
//                UiThread.post(() -> AndroidHelpers.toastShort("开始下载"));
                int size = conn.getContentLength();//获取到最大值之后设置到进度条的MAX
                //开始下载
                byte[] bytes = new byte[4096];//可以设置大点提高下载速度
                int completeLen = 0, len;
                try (InputStream in = conn.getInputStream();
                     FileOutputStream out = new FileOutputStream(savedFile, append);) {

                    while ((len = in.read(bytes)) != -1) {
                        out.write(bytes, 0, len);
                        completeLen += len;
                        int finalCompleteLen = completeLen;
                        UiThread.post(() -> finalCallback.updateProgress(finalCompleteLen));
                        out.flush();
                    }

//                    completeLen = IOUtils.copy(in, out);
                    Log.d(TAG, String.format("download: 复制长度：%d", completeLen));
                }

//                UiThread.post(() -> AndroidHelpers.toastShort(filename + "下载成功"));
            } else {
                throw new Exception("responseCode=" + resCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            doneMsg = filename + ' ' + failedStr + ' ' + e.getMessage();
//            UiThread.post(() -> AndroidHelpers.toastShort(filename + "下载失败, " + e.getMessage()));
            savedFile.delete();
        } finally {
            isDownloading = false;
            //关闭连接（如果不关闭的话，也许连接数过多会报错。不过不关闭连接，重复下载的话速度貌似会变快？
            if (conn != null)
                conn.disconnect();


        }
        String finalDoneMsg = doneMsg;
        UiThread.post(() -> finalCallback.onFinish(finalDoneMsg));
    }


    /**
     * 检查下载的wine压缩包 是否完整
     * <p/>
     * 旧版本没有sha256.txt，所以校验完整性不应该阻碍核心操作继续
     *
     * @return 是否完整
     */
    public static boolean checkWineTarSum(File tarFile) {
        List<String> checksumList = KronConfig.i.getSha256(tarFile.getParentFile().getName());
//        File checksumFile = Config.getShaTxtFromTagFolder(tarFile.getParentFile());
        //校验码文本不存在
        if (checksumList.size() == 0 || !tarFile.exists())
            return false;

        try {
            String correctSha = checksumList.get(0);
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
            Log.d(TAG, "checkWineTarSum: \n正确sha=" + correctSha + "\n计算sha=" + calSha);
            return correctSha.equals(calSha);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }


}
