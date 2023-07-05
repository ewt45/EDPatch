package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.RR.getS;

import android.util.Log;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.SafeFileHelpers;
import com.eltechs.axs.helpers.UiThread;
import com.example.datainsert.exagear.RR;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

/**
 * 网络下载。github
 */
public class MWFileHelpers {
    private static final String TAG = "MWFileHelpers";
    static boolean isDownloading = false; //标记当前是否正在下载
    private static boolean isDownloadingReleaseInfo = false;

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
            //不检查域名和证书？
//            trustAllHosts(conn);
            conn.connect();


            int resCode = conn.getResponseCode();
            if (resCode == 200) {
//                UiThread.post(() -> AndroidHelpers.toastShort("开始下载"));
                int size = conn.getContentLength();//获取到最大值之后设置到进度条的MAX
                Log.d(TAG, "download: 尝试获取文件大小："+size);
                if(size!=-1)
                    finalCallback.prepare(size);
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

    private static void trustAllHosts(HttpsURLConnection conn) {
        conn.setHostnameVerifier((hostname, session) -> {
            Log.d(TAG, "trustAllHosts: hostname="+hostname+", PeerHost="+session.getPeerHost()+", peerPort="+session.getPeerPort());
            return true;
        });


        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

            }

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压一个.tar.xz压缩文件
     * @param archiveInputStream 对应压缩文件的输入流
     */
    public static void decompressTarXz(InputStream archiveInputStream, File dstDir) throws IOException {
        //文件->xz->tar
        try (
             XZCompressorInputStream xzis = new XZCompressorInputStream(archiveInputStream);
             TarArchiveInputStream tis = new TarArchiveInputStream(xzis);
        ) {

            TarArchiveEntry nextEntry;
            while ((nextEntry = tis.getNextTarEntry()) != null) {

                String name = nextEntry.getName();
                File file = new File(dstDir, name);
                //如果是目录，创建目录
                if (nextEntry.isDirectory()) {
                    file.mkdirs();
                    continue;
                }
                //文件则写入具体的路径中
                try (OutputStream os = new FileOutputStream(file);) {
                    IOUtils.copy(tis, os); // FileUtils.copyInputStreamToFile(tis, file); //不能用这个，会自动关闭输入流
                }
                //如果是符号链接，需要手动链接(参考ZipUnpacker）
                if (nextEntry.isSymbolicLink()) {
                    file.delete();
                    SafeFileHelpers.symlink(nextEntry.getLinkName(), file.getAbsolutePath());
                    Log.d(TAG, String.format("extract: 解压时发现符号链接：链接文件：%s，指向文件：%s", nextEntry.getName(), nextEntry.getLinkName()));
                }
            }

        }
    }



}
