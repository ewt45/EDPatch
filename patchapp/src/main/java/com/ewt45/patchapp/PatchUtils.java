package com.ewt45.patchapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.ewt45.patchapp.patching.SmaliFile;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PatchUtils {
    static String TAG="patchUtils";
    static String externalFilesDir;
    private static String packageName; //用于记录当前apk包名。解码apk之后应首先设置该值，再进行smali修改.格式：“com/eltechs/ed"

    public static void setExternalFilesDir(String path){
        externalFilesDir = path;
    }
    /** 获取工作路径。为外部filesDir/patchtmp*/
    public static File getPatchTmpDir(){
        File patchTmpDir = new File(externalFilesDir+"/patchtmp");
        if(!patchTmpDir.exists() && !patchTmpDir.mkdir())
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
    /**获取暂存的原始apk，为filesDir/patchtmp/tmp.apk*/
    public static File getPatchTmpApk(){
        return new File(getPatchTmpDir(),"tmp.apk");
    }
    public static void copyToExternalFiles(Context c, Uri uri) throws Exception {
        //获取文件名
        List<String> list = uri.getPathSegments();
        String[] names = list.get(list.size()-1).split("/");
        String filename = names[names.length-1]; //文件名
        Log.d(TAG, "onActivityResult: "+filename+", "+uri.getPathSegments().toString());
        File copyFile = getPatchTmpApk();
        //不如设计成只存储一个，选择新的时候替换掉旧的。还有新建线程？
        if(copyFile.exists() && !copyFile.delete()){
//            Toast.makeText(c, "文件已存在，跳过创建", Toast.LENGTH_SHORT).show();
//            return;
            throw new Exception("tmp.apk存在且无法删除");
        }

        try {
            InputStream is = c.getContentResolver().openInputStream(uri);
            FileOutputStream fos = new FileOutputStream(copyFile);
            IOUtils.copy(is,fos);
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void setPackageName(String edmainActivitycls){
        int endIdx = edmainActivitycls.indexOf("/activities");
        packageName = edmainActivitycls.substring(1,endIdx);
        Log.d(TAG, "setPackageName: 设置包名为："+packageName);
    }

    /**
     * 返回ex包名 格式：“com/eltechs/ed"
     * @return
     */
    public static String getPackageName() {
        if(packageName==null || packageName.equals("")){
            SmaliFile pkgSmali = new SmaliFile();
            PatchUtils.setPackageName(pkgSmali.findSmali(null, "EDMainActivity").getmCls());
            pkgSmali.close();
        }
        return packageName;
    }

    /**
     * 不使用$PACKAGE_NAME了，自动检测com.eltechs.ed包名吧
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
        return  returnLists;
    }

    public enum version{
//        fab
    }
}
