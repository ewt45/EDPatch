package com.ewt45.patchapp.patching;

import android.support.annotation.Nullable;
import android.util.Log;

import com.ewt45.patchapp.PatchUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SmaliFile {
    static String TAG = "SmaliFile";
    public final static int LOCATION_BEFORE = 0;
    public final static int LOCATION_AFTER = 1;
    public final static int ACTION_DELETE = 0;
    public final static int ACTION_INSERT = 1;
    public final static int LIMIT_TYPE_METHOD = 0;
    File mFile;//Smali对应的File
    private String mCls;//smali类名。格式如：Lcom/eltechs/ed/AppRunGuide;
    private String mMethodLimit;//限制范围。目前仅想到了方法限制
    private List<String> mAllLines = new ArrayList<>(); //文本转为字符串列表，一个字符串为一行

    public SmaliFile() {

    }

    /**
     * 寻找到一个smali文件
     *
     * @param pkgName   类所属包名。不确定的话可以为null，会遍历查找 格式为com.package.name
     * @param className 类名
     * @return this
     */
    public SmaliFile findSmali(@Nullable String pkgName, String className)  {
        File smaliRoot = new File(PatchUtils.getPatchTmpDir() + "/tmp/smali");
        try {
            mFile = pkgName == null
                    ? traverseDir(smaliRoot, className)
                    : new File(smaliRoot.getAbsolutePath() + "/" + pkgName.replace(".", "/") + "/" + className + ".smali");
        } catch (Exception e) {
            e.printStackTrace();
            mFile=null;
        }
        Log.d(TAG, "locate: 找到smali：" + mFile);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    private File traverseDir(File root, String target) throws Exception {
        LinkedList<File> dirList = new LinkedList<>();
        dirList.add(root);//先添加一个元素
        while (!dirList.isEmpty()) {
            //从头取出一个元素
            File[] firstFile = dirList.removeFirst().listFiles();
            if (firstFile == null)
                continue;
            //处理这个文件的所有直接子节点，是文件夹就放到列表中，是文件且名称对应就结束
            for (File f : firstFile) {
                if (f.isDirectory())
                    dirList.add(f);
                else if ((target + ".smali").equals(f.getName())) {
                    dirList.clear();
                    return f;
                }
            }
        }

//        return null;
        throw new Exception("无法找到名称为 " + target + " 的smali文件");
    }

    /**
     * 初始化一些成员变量啥的吧
     */
    private void init() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mFile), StandardCharsets.UTF_8));
        String line;
        //找到该smali类名
        while ((line = br.readLine()) != null) {
            if (line.contains(".class")) {
                mCls = line.split(" ")[line.split(" ").length - 1];
            }
            //如果不是空行的话就添加到列表中
            if (!line.equals("")) {
                mAllLines.add(line);
            }
        }

        br.close();
    }

    /**
     * 修改一处 如果有多个patch，请注意是否需要修改limit
     * 注意，before会定位到origin第一句的位置，如果是insert，那么插入到这个位置会把origin第一句挤到后面（正常），但是如果是delete，那么会删除origin第一句而非origin第一句的上一句。
     *
     * @return
     */
    public SmaliFile patch(int location, int action, String[] origin, String[] patch) throws Exception {

        boolean methodStart = false; //限制范围的函数是否开始
        int patchPosition = -1;//定位的原始代码位置是否找到
        //匹配和修改时注意替换包名
        for (int i = 0; i < mAllLines.size(); i++) {
            String line = mAllLines.get(i);
            //如果有定位函数，先找到函数起始位置
            if (mMethodLimit != null && !methodStart) {
                if (line.contains(mMethodLimit)) {
                    methodStart = true;
                } else {
                    continue;
                }
            }
            //找原始代码的位置
            //如果和原始代码第一行匹配，且后续长度够用，匹配剩余行数
            if (line.contains(origin[0]) && mAllLines.size() - i >= origin.length) {
                boolean locFound = true;
                for (int j = 0; j < origin.length; j++) {
                    if (!mAllLines.get(i + j).contains(parsePkgName(origin[j]))) {
                        locFound = false;
                        break;
                    }
                }
                patchPosition = locFound ? i : -1; //如果全部匹配，则设置修改起始点
            }

            /*
             * 如果是修改位置了
             * 1. 如果是after，当前位置（修改位置）+原始代码行数，定位到原始代码结尾
             * 2. 如果是before，就当前位置
             *
             *
             * 开始修改
             * 1. 如果是insert， 列表在当前位置插入新代码的全部行
             * 2. 如果是delete，从这里往后删掉 新代码的行数个 元素，最好比较一下列表删除的行与新代码的行 内容是否匹配
             *
             * 修改结束，没什么操作吧，直接break
             */
            if (patchPosition == -1)
                continue;

            if (location == LOCATION_AFTER){
                patchPosition += origin.length;
            }

            switch (action) {
                case ACTION_INSERT: {
                    mAllLines.addAll(patchPosition, Arrays.asList(patch));
                    break;
                }
                case ACTION_DELETE: {
                    if (patchPosition + patch.length > mAllLines.size())
                        throw new Exception("删除失败：当前起始位置行数+删除代码行数>全部代码行数");
                    for (String str : patch) {
                        String removedStr = mAllLines.remove(patchPosition);
                        if (!removedStr.contains(parsePkgName(str))) {
                            Log.e(TAG, "patch: 原始行：" + removedStr + ", 待删除行：" + parsePkgName(str), new Exception("删除错误：当前原始行与要删除行内容不匹配"));
                        }
                    }
                    break;
                }
            }
            //如果走到这里说明已经是修改完的了， 直接break就好
            break;

        }
        Log.d(TAG, "patch: 结束");

        return this;
    }

    /**
     * 将修改范围缩小到某一范围（比如某个方法内），如果有多个patch，请注意是否需要修改limit
     *
     * @return
     */
    public SmaliFile limit(int type, String limit) {
        if (type == LIMIT_TYPE_METHOD) {
            this.mMethodLimit = limit;
        }
        return this;
    }

    /**
     检查该功能是否已有
     - 在之后：
     - 插入：如果这之后第一句是插入代码第一句，则说明功能已有
     - 删除：如果这之后第一句不是删除代码第一句，则说明功能已有。
     - 在之前：
     - 插入：如果这之前的一句是插入代码的最后一句，则功能已有
     - 删除：如果这之前的一句不是删除代码的最后一句，则功能已有
     delete和before的情况有问题，请勿使用
     */
    public boolean patchedEarlier(String method, int location, int action, String[] origin, String[] patch) {
        boolean methodStart = false; //限制范围的函数是否开始
        int patchPosition = -1;//定位的原始代码位置是否找到
        //找到函数起始位置
        for(int i=0; i<mAllLines.size(); i++){
            if(mAllLines.get(i).contains(method)){
                patchPosition = i;
                break;
            }
        }

        //找到函数中定位代码的位置
        boolean locFound = false;
        for(int i=patchPosition; i<mAllLines.size(); i++){
            String line = mAllLines.get(i);
            if (line.contains(origin[0]) && mAllLines.size() - i >= origin.length) {
                int matchLength=0;
                for (int j = 0; j < origin.length; j++) {
                    if (mAllLines.get(i + j).contains(parsePkgName(origin[j])))
                        matchLength++;
                }
                if(matchLength== origin.length){
                    locFound=true;
                    patchPosition = i;
                    break;
                }
            }
        }

        if (location == LOCATION_AFTER){
            patchPosition += origin.length;
            return (action == ACTION_INSERT && mAllLines.get(patchPosition).contains(patch[0])) ||
                    (action == ACTION_DELETE && !mAllLines.get(patchPosition).contains(patch[0]));
        }else if(location == LOCATION_BEFORE){
            return (action == ACTION_INSERT && mAllLines.get(patchPosition - 1).contains(patch[patch.length - 1]))
//                    || (action ==ACTION_DELETE && !locFound); delete和before的情况有问题
                    || (action == ACTION_DELETE && !mAllLines.get(patchPosition - 1).contains(patch[patch.length - 1]));
        }
        return false;

    }

    /**
     * 判断该功能已经存在。用于delete+before的情况
     * @param method 给定方法
     * @param deletedLines 如果该功能已存在，则这些行代码应该已被删除
     * @return 是否已存在
     */
    public boolean patchedEarlier(String method,String[] deletedLines){
        boolean methodBegin = false;
        boolean patched=false;
        for(int i =0; i<mAllLines.size(); i++){
            if(!methodBegin && mAllLines.get(i).contains(method)){
                methodBegin = true;
                continue;
            }
            if(methodBegin && mAllLines.get(i).contains(deletedLines[0])
            && isEqualInOrder(i,deletedLines)){
                return false; //如果找到了完整的这几行代码，说明没被删除，说明功能不存在
            }
        }
        return true;
    }

    /**添加一整个方法*/
    public SmaliFile addMethod(String[] add){
        mAllLines.addAll(Arrays.asList(add));
        return this;
    }

    /**
     * 生成修改后的文件。
     * 关闭，不再使用该对象
     */
    public void close() {
        try {
            File newFile = new File(mFile.getAbsolutePath() + ".1"); //修改后的文件
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), StandardCharsets.UTF_8));
            for (String s : mAllLines) {
                bw.write(s);
                bw.newLine();
            }
            bw.flush();
            bw.close();
            //重命名修改后的文件
            mFile.delete();
            if (!newFile.renameTo(mFile)) {
                Log.e(TAG, "close: 新建的临时smali文件无法重命名为原本的smali文件", new Exception("重命名失败"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mFile = null;
        mAllLines = null;
        Log.d(TAG, "close: 生成新文件，关闭");
    }

    private String parsePkgName(String str) {
        return str.replace("$PACKAGE_NAME", PatchUtils.getPackageName());
    }

    public String getmCls() {
        return mCls;
    }

    /**
     * 已知mAllLines在startPosition处的字符串与compared的第一个字符串相等，判断从start往后的compared.length长度个字符串，是否都与compared相等
     * @param startPosition 首个字符窜相等的位置
     * @param compared 要比较的字符串数组
     * @return 是否相等
     */
    private boolean isEqualInOrder(int startPosition, String[] compared){
        if(startPosition+compared.length>mAllLines.size())
            return false;
        for(int i=0; i< compared.length; i++){
            if(!mAllLines.get(startPosition+i).contains(compared[i])){
                return false;
            }
        }
        return true;
    }

//    public static class ModPosition {
//
//        public int location;
//        public int action;
//        String[] origin;
//        String[] patch;
//
//        //用于辅助定位修改位置的类？类型：[某个smali+]特定字符串前后
//        public ModPosition(int location, int action, String[] origin, String[] patch) {
//
//        }
//    }
}


