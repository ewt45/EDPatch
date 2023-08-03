package com.ewt45.patchapp.patching;

import android.util.Log;

import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.thread.Func;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用于自己的apk的smali加入到exagear 的smali中
 */
public class PatcherFile {
    public final static int TYPE_SMALI = 0;
    public final static int TYPE_ASSETS = 1;
    public final static int TYPE_LIB_ARMV7 = 2;
    static String TAG = "PatcherFile";

    /**
     * {@link #copy(int, String[], String[], String)}
     */
    public static void copy(int type, String[] strings) throws Exception{
        copy(type,strings,new String[0],null);
    }


    /**
     *
     * 将自己的apk中的smali复制到exagear的输出文件夹中
     *
     * @param type    类型，目前仅支持smali和assets
     * @param names 要复制的文件名。格式如："/com/a/b", "/com/a/b/c.smali" 斜线开头，完整文件（夹）名结尾
     * @param preservedFields 需要从旧smali复制过来的成员变量名（用于标识其他功能是否启用）
     * @param setFieldLine 当前添加功能对应成员变量的完整行。e.g. {@code .field private static final enable_custom_resolution:Z = true}
     */
    public static void copy(int type, String[] names, String[] preservedFields, String setFieldLine) throws Exception {
//        if(type!=TYPE_SMALI)
//            throw new Exception("不支持的类型："+type);
        String subfolder;
        if (type == TYPE_SMALI)
            subfolder = "smali";
        else if (type == TYPE_ASSETS)
            subfolder = "assets";
        else if (type == TYPE_LIB_ARMV7)
            subfolder = "lib/armeabi-v7a";
        else
            subfolder = "";

        if (type == TYPE_SMALI) {
            LinkedList<File> fileList = new LinkedList<>();

            //要求传的不带*和/了，文件还是文件夹自己判断
            for (String str : names) {
                File oneFile = new File(PatchUtils.getPatcherExtractDir(),subfolder + str);
                if(!oneFile.exists()){
                    Log.e(TAG, "copy: 该文件不存在，无法复制："+oneFile.getAbsolutePath() );
                    continue;
                }
                if (oneFile.isDirectory())
                    fileList.addFirst(oneFile);
                else {
                    //如果是文件，需要考虑到内部类（即同类名，但带$的smali）
                    fileList.add(oneFile);
                    File parent = oneFile.getParentFile();
                    String baseFileName = oneFile.getName().length() > ".smali".length()
                            ? oneFile.getName().substring(0, oneFile.getName().length() - ".smali".length())
                            : null;
                    if (parent == null || baseFileName == null)
                        continue;
                    File[] innerClasses = parent.listFiles((dir, name) -> name.startsWith(baseFileName + "$"));
                    if (innerClasses != null)
                        fileList.addAll(Arrays.asList(innerClasses));
                }

            }
            //将目录转为文件
            while (fileList.get(0).isDirectory()) {
                File dir = fileList.removeFirst();
                File[] subs = dir.listFiles();
                if (subs == null)
                    continue;
                for (File f : subs) {
                    if (f.isDirectory())
                        fileList.addFirst(f);
                    else
                        fileList.add(f);
                }
            }
            //如果原文件路径包含路径，目标路径替换包名，文件中代码替换包名
            for (File file : fileList) {
                //获取目的地路径
                String dstPath = file.getAbsolutePath().replace(
                                PatchUtils.getPatchTmpDir().getAbsolutePath() + "/patcher/",
                                PatchUtils.getPatchTmpDir().getAbsolutePath() + "/tmp/")
                        .replace("com/eltechs/ed", PatchUtils.getPackageName());
                //复制过去
                File dstFile = new File(dstPath);
                //考虑要保留的成员变量flag
                Map<String,String> oldFields = getPreservedFields(dstFile, preservedFields);
                FileUtils.copyFile(file, dstFile);
                //如果需要，替换代码中包名.不对，路径包含包名和文本包含包名没有关系，应该对每个文件文本都进行检查
                List<String> allLines = PatchUtils.scanAndParsePkgName(FileUtils.readLines(dstFile,StandardCharsets.UTF_8).toArray(new String[0]));

                restorePreservedFields(allLines,oldFields,setFieldLine);

                dstFile.delete();
                FileUtils.writeLines(dstFile,StandardCharsets.UTF_8.name(),allLines);
            }
        } else {
            //TODO: 星号判断去掉
            for (String str : names) {
                String srcPath = PatchUtils.getPatchTmpDir().getAbsolutePath() + "/patcher/" + subfolder + str;
                String dstPath = PatchUtils.getPatchTmpDir().getAbsolutePath() + "/tmp/" + subfolder + str;
                File srcFile = new File(srcPath);
                if (srcFile.isDirectory()) {
                    FileUtils.copyDirectory(srcFile, new File(dstPath));
                } else {
                    FileUtils.copyFile(srcFile, new File(dstPath));
                }
            }
        }


    }

    /**
     * 复制文件后，将旧文件中的指定成员变量复原
     *
     * @param allLines     该文件所有行
     * @param oldFields    指定成员变量及其对应的完整行
     * @param setFieldLine
     */
    private static void restorePreservedFields(List<String> allLines, Map<String, String> oldFields, String setFieldLine) {
        for(String field: oldFields.keySet()){
            for(int i=0; i<allLines.size();i++){
                String line = allLines.get(i);
                if(line.startsWith(".field") && line.contains(field)){
                    allLines.remove(i);
                    allLines.add(i,oldFields.get(field));
                }
            }

        }

        //将指定行覆写
        if(setFieldLine!=null){
            String setFieldName = setFieldLine.split(":")[0].split("static final ")[1];
            for(int i=0; i<allLines.size();i++){
                String line = allLines.get(i);
                if(line.startsWith(".field") && line.contains(setFieldName)){
                    allLines.remove(i);
                    allLines.add(i,setFieldLine);
                }
            }
        }

    }

    /**
     * 从一个smali文件中获取指定成员变量
     * @param dstFile smali文件
     * @param preservedFields 要保留的变量名，必须为final static
     * @return map k为传入的preservedFields，v为完整的一行
     */
    private static Map<String, String> getPreservedFields(File dstFile, String[] preservedFields) throws IOException {
        Map<String,String> fieldsMap = new HashMap<>();
        if(!dstFile.getName().endsWith(".smali") || !dstFile.exists())
            return fieldsMap;

        List<String> allLines = FileUtils.readLines(dstFile,StandardCharsets.UTF_8);
        for(String field:preservedFields){
            for(String line:allLines){
                if(line.trim().startsWith(".field") && line.contains("static final") && line.contains(field))
                    fieldsMap.put(field,line);
            }
        }

        return fieldsMap;
    }


    /**
     * 将文件中代码替换包名为对应apk的包名
     *
     * @param dstFile
     * @return
     */
    private static void parsePkgNameInFile(File dstFile) {
        try {
            List<String> allLines = PatchUtils.scanAndParsePkgName(FileUtils.readLines(dstFile,StandardCharsets.UTF_8).toArray(new String[0]));
            dstFile.delete();
            FileUtils.writeLines(dstFile,StandardCharsets.UTF_8.name(),allLines);


//            //获取文件内容并替换包名（类形式，字符串形式）
//            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dstFile), StandardCharsets.UTF_8));
//            String line;
//            List<String> mAllLines = new ArrayList<>();
//            //找到该smali类名
//            while ((line = br.readLine()) != null) {
//                //如果不是空行的话就添加到列表中
//                if (!line.equals("")) {
//                    mAllLines.add(line
//                            .replace("com/eltechs/ed", PatchUtils.getPackageName())
//                            .replace("com.eltechs.ed", PatchUtils.getPackageName().replace('/', '.')));
//                }
//            }
//            br.close();
//            //生成新文件
//            File newFile = new File(dstFile.getAbsolutePath() + ".1"); //修改后的文件
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), StandardCharsets.UTF_8));
//            for (String s : mAllLines) {
//                bw.write(s);
//                bw.newLine();
//            }
//            bw.flush();
//            bw.close();
//            //重命名修改后的文件
//            dstFile.delete();
//            if (!newFile.renameTo(dstFile)) {
//                Log.e(TAG, "close: 新建的临时smali文件无法重命名为原本的smali文件", new Exception("重命名失败"));
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从patcher的app中某个smali中取出新定义的一个方法，加到exapk中. smali格式如"/com/a/b/c.smali"
     */

    public static String[] getSmaliMethod(String smaliLocation, String methodName) throws IOException {
        //定位smali文件
        File oneFile = new File(PatchUtils.getPatchTmpDir().getAbsolutePath() + "/patcher/smali/" + smaliLocation);
        List<String> fileLines = FileUtils.readLines(oneFile, StandardCharsets.UTF_8);
        int start = -1, end = -1;

        for (int i = 0; i < fileLines.size(); i++) {
            //如果已经找到开头，就看是不是结尾(trim后用equal吧）
            if (start != -1 && fileLines.get(i).trim().equals(".end method")) {
                end = i + 1;
                break;
            }
            //否则看是不是开头
            else if (fileLines.get(i).contains(methodName)) {
                start = i;
            }
        }
        if (end == -1) {
            Log.e(TAG, "getSmaliMethod: 不应该找不到方法的结尾啊", null);
            return new String[0];
        }
        return fileLines.subList(start, end).toArray(new String[0]);
    }

    /**
     * 从解包的apk中获取已安装的功能的版本信息
     *
     * @param funcName 功能名称，为对应类的名称
     * @return 版本号 INVALID_VERSION为未添加(也可能是初版没有这个txt）
     */
    public static int getAddedFuncVer(String funcName) {
        File file = new File(PatchUtils.getPatchTmpDir().getAbsolutePath() + "/tmp/assets/EDPatch/utilsVers.txt");
        if (file.exists()) {
            List<String> infos = new ArrayList<>();
            //读取所有行
            try {
                infos = FileUtils.readLines(file, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //找到对应行，返回版本号
            for (String str : infos) {
                String[] nameNVer = str.split(" ");
                assert nameNVer.length == 2;
                if (nameNVer[0].equals(funcName))
                    return Integer.parseInt(nameNVer[1]);
            }

        }
        //没有信息或者没找到对应功能，返回INVALID_VERSION
        return Func.INVALID_VERSION;
    }


    /**
     * 将本次安装的功能 版本号写入
     *
     * @param funcList    所有功能，以及对应版本号
     */
    public static void writeAddedFunVer(Map<Func, Integer> funcList) {
        File file = new File(PatchUtils.getPatchTmpDir().getAbsolutePath() + "/tmp/assets/EDPatch/utilsVers.txt");
        if (file.exists()){
            boolean b = file.delete();
            if(!b)
                Log.e(TAG, "writeAddedFunVer: ", new Throwable("utilsVers.txt无法删除，能正常写入吗？"));
        }
        try {
            //获取本次安装的功能的类名和版本号，写入文件
            List<String> nameNVer = new ArrayList<>();
            for (Func func : funcList.keySet()) {
                nameNVer.add(func.getClass().getSimpleName() + " " + funcList.get(func));
            }
            FileUtils.writeLines(file, String.valueOf(StandardCharsets.UTF_8), nameNVer, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
