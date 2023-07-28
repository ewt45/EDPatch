package com.ewt45.patchapp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatchUtils {
    static String TAG = "patchUtils";
    static String externalFilesDir;
    private static String packageName; //用于记录当前apk包名。解码apk之后应首先设置该值，再进行smali修改.格式：“com/eltechs/ed"

    public static void setExternalFilesDir(String path) {
        externalFilesDir = path;
    }

    /**
     * 获取工作路径。为外部filesDir/patchtmp
     */
    public static File getPatchTmpDir() {
        File patchTmpDir = new File(externalFilesDir + "/patchtmp");
        if (!patchTmpDir.exists() && !patchTmpDir.mkdir())
            Log.d(TAG, "getPatchTmpDir: 创建文件夹失败");
        return patchTmpDir;
    }
//    public static File getPatchTmpOutDir(){
//        File outDir = new File(getPatchTmpDir(),"tmp");
//        if(!outDir.exists() && !outDir.mkdir()){
//            Log.d(TAG, "getPatchTmpOutDir: 创建文件夹失败");
//        }
//        return outDir;
//    }

    /**
     * 获取暂存的原始apk，为filesDir/patchtmp/tmp.apk
     */
    public static File getPatchTmpApk() {
        return new File(getPatchTmpDir(), "tmp.apk");
    }

    /**
     * 获取从assets解压出的patcher.apk，用于apktool的解压
     */
    public static File getLocalPatcherApk() {
        return new File(getPatchTmpDir(), "patcher.apk");
    }

    /**
     * 获取用于获取功能smali的patcher apk 的解包后文件夹，位于/patchtmp/patcher
     * @return
     */
    public static File getPatcherExtractDir(){
        return  new File(getPatchTmpDir(),"patcher");
    }

    /**
     * 获取exa apk解包后的文件夹。位于/patchtmp/tmp
     * @return
     */
    public static File getExaExtractDir(){ return new File(getPatchTmpDir(),"tmp"); }

    /**
     * 获取添加功能后的已签名的exa apk。位于/patchtmp/tmp/dist/tmp_sign.apk
     * @return file对象。该文件不一定存在
     */
    public static File getExaNewPatchedApk(){
        return  new File(getExaExtractDir(),"dist/tmp_sign.apk");
    }

    public static void copyToExternalFiles(Context c, Uri uri) throws Exception {
        //获取文件名
        List<String> list = uri.getPathSegments();
        String[] names = list.get(list.size() - 1).split("/");
        String filename = names[names.length - 1]; //文件名
        Log.d(TAG, "onActivityResult: " + filename + ", " + uri.getPathSegments().toString());
        File copyFile = getPatchTmpApk();
        //不如设计成只存储一个，选择新的时候替换掉旧的。还有新建线程？
        if (copyFile.exists() && !copyFile.delete()) {
//            Toast.makeText(c, "文件已存在，跳过创建", Toast.LENGTH_SHORT).show();
//            return;
            throw new Exception("tmp.apk存在且无法删除");
        }

        try {
            InputStream is = c.getContentResolver().openInputStream(uri);
            FileOutputStream fos = new FileOutputStream(copyFile);
            IOUtils.copy(is, fos);
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 返回ex包名 格式：“com/eltechs/ed"
     *
     * @return
     */
    public static String getPackageName() {
        if (packageName == null || packageName.equals("")) {
//            SmaliFile pkgSmali = new SmaliFile();
//            PatchUtils.setPackageName(pkgSmali.findSmali(null, "EDMainActivity").getmCls());
//            pkgSmali.close();
            //MyApplication.instance.getApplicationContext()
            PackageInfo info = MyApplication.instance.getApplicationContext().getPackageManager().getPackageArchiveInfo(getPatchTmpApk().getAbsolutePath(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                packageName = info.packageName.replace('.', '/');
            }
        }
        return packageName;
    }

    public static void setPackageName(String edmainActivitycls) {
        if (edmainActivitycls == null || edmainActivitycls.equals("")) {
            packageName = null;
            return;
        }
        int endIdx = edmainActivitycls.indexOf("/activities");
        packageName = edmainActivitycls.substring(1, endIdx);
        Log.d(TAG, "setPackageName: 设置包名为：" + packageName);
    }

    /**
     * 不使用$PACKAGE_NAME了，自动检测com.eltechs.ed包名吧
     * <p/>
     * 会转换包含"com/eltechs/ed"的字符串和包含"com.eltechs.ed"的字符串
     *
     * @param origin
     * @return
     */
    public static List<String> scanAndParsePkgName(String[] origin) {
        List<String> returnLists = new ArrayList<>();
        for (String str : origin) {
            returnLists.add(str.replace("com/eltechs/ed", PatchUtils.getPackageName())
                    .replace("com.eltechs.ed", PatchUtils.getPackageName().replace('/', '.')));
        }
        return returnLists;
    }

    /**
     * 检查assets中的patcher与已解压的patcher是否不同
     * <p>
     * 若返回true则需要删除patcher
     *
     * @return 若本地存在已解压的patcher且与assets中的校验码不同，返回true。否则返回false
     */
    public static boolean isPatcherApkChanged(Context context) {
        try {
            //计算压缩包的sha256，与文本的值对比
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] localSha = digest.digest(FileUtils.readFileToByteArray(getLocalPatcherApk()));
            byte[] assetSha;
            try (InputStream is = context.getAssets().open("patcher/release/patcher.apk");
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
                IOUtils.copy(is, bos);
                assetSha = digest.digest(bos.toByteArray());
            }
            return !Arrays.equals(localSha, assetSha);

        } catch (NoSuchAlgorithmException | IOException e) {
            Log.w(TAG, "isPatcherApkChanged: " + e.getMessage());
        }
        return false;
    }

}
