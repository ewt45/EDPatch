package com.ewt45.patchapp.patching;

import static com.ewt45.patchapp.PatchUtils.scanAndParsePkgName;
import static com.ewt45.patchapp.thread.Func.INVALID_VERSION;

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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SmaliFile {
    public final static int LOCATION_BEFORE = 0;
    public final static int LOCATION_AFTER = 1;
    public final static int ACTION_DELETE = 0;
    public final static int ACTION_INSERT = 1;
    public final static int LIMIT_TYPE_METHOD = 0;
    /**
     * java类中用于记录该功能版本号的成员变量
     */
    private static final String VERSION_FIELD_PREFIX = ".field private static final VERSION_FOR_EDPATCH:I = ";
    private static final String TAG = "SmaliFile";
    File mFile;//Smali对应的File
    private String mCls;//smali类名。格式如：Lcom/eltechs/ed/AppRunGuide;
    private String mMethodLimit;//限制范围。目前仅想到了方法限制
    private List<String> mAllLines = new ArrayList<>(); //文本转为字符串列表，一个字符串为一行



    /**
     * 寻找到一个smali文件
     * fullclassname 格式为： 类的完成路径，包名+类名。 com.package.classname
     *
     * @return this。如果没找到对应smali文件会返回null，此后不应再对该实例进行操作
     */
    public SmaliFile findSmali(String fullClassName) {

        //在这里转换一下包名吧
        fullClassName = PatchUtils.scanAndParsePkgName(new String[]{fullClassName}).get(0);
        mFile = new File(PatchUtils.getPatchTmpDir().getAbsolutePath() + "/tmp/smali/" + fullClassName.replace(".", "/") + ".smali");

        //文件不存在就返回null吧
        if(!mFile.exists()){
            Log.e(TAG, "findSmali: "+fullClassName+".smali不存在");
            return null;
        }
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
    public SmaliFile patch(int location, int action, String[] origin1, String[] patch1) throws Exception {

        //先转换一下代码中的包名
        List<String> originPkgCorrected = scanAndParsePkgName(origin1);
        List<String> patchPkgCorrected = scanAndParsePkgName(patch1);

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
            if (line.contains(originPkgCorrected.get(0)) && mAllLines.size() - i >= originPkgCorrected.size()) {
                boolean locFound = true;
                for (int j = 0; j < originPkgCorrected.size(); j++) {
                    if (!mAllLines.get(i + j).contains(originPkgCorrected.get(j))) {
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

            if (location == LOCATION_AFTER) {
                patchPosition += originPkgCorrected.size();
            }

            switch (action) {
                case ACTION_INSERT: {
                    mAllLines.addAll(patchPosition, patchPkgCorrected);
                    break;
                }
                case ACTION_DELETE: {
                    if (patchPosition + patchPkgCorrected.size() > mAllLines.size())
                        throw new Exception("删除失败：当前起始位置行数+删除代码行数>全部代码行数");
                    for (String str : patchPkgCorrected) {
                        String removedStr = mAllLines.remove(patchPosition);
                        if (!removedStr.contains(str)) {
                            Log.e(TAG, "patch: 原始行：" + removedStr + ", 待删除行：" + str, new Exception("删除错误：当前原始行与要删除行内容不匹配"));
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
     * 检查该功能是否已有
     * - 在之后：
     * - 插入：如果这之后第一句是插入代码第一句，则说明功能已有
     * - 删除：如果这之后第一句不是删除代码第一句，则说明功能已有。
     * - 在之前：
     * - 插入：如果这之前的一句是插入代码的最后一句，则功能已有
     * - 删除：如果这之前的一句不是删除代码的最后一句，则功能已有
     * delete和before的情况有问题，请勿使用本方法，应该使用patchedEarlier(String method, String[] deletedLines)
     */
    public boolean patchedEarlier(String method, int location, int action, String[] origin, String[] patch) {
        boolean methodStart = false; //限制范围的函数是否开始
        int patchPosition = -1;//定位的原始代码位置是否找到
        //找到函数起始位置
        for (int i = 0; i < mAllLines.size(); i++) {
            if (mAllLines.get(i).contains(scanAndParsePkgName(new String[]{method}).get(0))) {
                patchPosition = i;
                break;
            }
        }
        //更新包名
        origin = scanAndParsePkgName(origin).toArray(new String[0]);
        patch = scanAndParsePkgName(patch).toArray(new String[0]);
        //找到函数中定位代码的位置
        boolean locFound = false;
        for (int i = patchPosition; i < mAllLines.size(); i++) {
            String line = mAllLines.get(i);
            if (line.contains(origin[0]) && mAllLines.size() - i >= origin.length) {
                int matchLength = 0;
                for (int j = 0; j < origin.length; j++) {
                    if (mAllLines.get(i + j).contains(origin[j]))
                        matchLength++;
                }
                if (matchLength == origin.length) {
                    locFound = true;
                    patchPosition = i;
                    break;
                }
            }
        }

        if (location == LOCATION_AFTER) {
            patchPosition += origin.length;
            return (action == ACTION_INSERT && mAllLines.get(patchPosition).contains(patch[0])) ||
                    (action == ACTION_DELETE && !mAllLines.get(patchPosition).contains(patch[0]));
        } else if (location == LOCATION_BEFORE) {
            return (action == ACTION_INSERT && mAllLines.get(patchPosition - 1).contains(patch[patch.length - 1]))
//                    || (action ==ACTION_DELETE && !locFound); delete和before的情况有问题
                    || (action == ACTION_DELETE && !mAllLines.get(patchPosition - 1).contains(patch[patch.length - 1]));
        }
        return false;

    }

    /**
     * 判断该功能已经存在。用于delete+before的情况
     *
     * @param method       给定方法
     * @param deletedLines 如果该功能已存在，则这些行代码应该已被删除
     * @return 是否已存在
     */
    public boolean patchedEarlier(String method, String[] deletedLines) {
        boolean methodBegin = false;
        boolean patched = false;
        for (int i = 0; i < mAllLines.size(); i++) {
            if (!methodBegin && mAllLines.get(i).contains(scanAndParsePkgName(new String[]{method}).get(0))) {
                methodBegin = true;
                continue;
            }
            //更新包名
            deletedLines = scanAndParsePkgName(deletedLines).toArray(new String[0]);
            if (methodBegin && mAllLines.get(i).contains(deletedLines[0])
                    && isEqualInOrder(i, deletedLines)) {
                return false; //如果找到了完整的这几行代码，说明没被删除，说明功能不存在
            }
        }
        return true;
    }

    /**
     * 添加一整个方法
     * @param add 这个方法的全部代码行
     */
    public SmaliFile addMethod(String[] add) {
        mAllLines.addAll(scanAndParsePkgName(add));
        return this;
    }

    /**
     * 删除一整个方法
     *
     * @param methodName 能够标识该行在smali文件中为某方法的起点。例如：.method public getCursor()Lcom/eltechs/axs/xserver/Cursor;
     */
    public SmaliFile deleteMethod(String methodName) {
        boolean startDel = false;
        for (int ind = 0; ind < mAllLines.size(); ind++) {
            //处于函数内部
            if (startDel) {
                //到结尾了，删除后停止循环
                if (mAllLines.get(ind).startsWith(".end method")) {
                    mAllLines.remove(ind);
                    break;
                } else {
                    mAllLines.remove(ind);
                    ind--;
                    continue;
                }
            }
            //如果找到函数起点，设置flag。从此往后开始删除
            if (mAllLines.get(ind).contains(methodName)) {
                ind--; //别忘了第一句也要删
                startDel = true;
            }
        }
        return this;
    }

//    private String parsePkgName(String str) {
//        return str.replace("$PACKAGE_NAME", PatchUtils.getPackageName());
//    }

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

    public String getmCls() {
        return mCls;
    }

    /**
     * 已知mAllLines在startPosition处的字符串与compared的第一个字符串相等，判断从start往后的compared.length长度个字符串，是否都与compared相等
     *
     * @param startPosition 首个字符窜相等的位置
     * @param compared      要比较的字符串数组
     * @return 是否相等
     */
    private boolean isEqualInOrder(int startPosition, String[] compared) {
        if (startPosition + compared.length > mAllLines.size())
            return false;
        for (int i = 0; i < compared.length; i++) {
            if (!mAllLines.get(startPosition + i).contains(compared[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 在该文件中是否能找到这包含该字符串的一行
     *
     * @param s 要寻找的字符串
     */
    public boolean containsLine(String s) {
        for (String line : mAllLines) {
            if (line.contains(PatchUtils.scanAndParsePkgName(new String[]{s}).get(0)))
                return true;
        }
        return false;
    }

    /**
     * 获取该文件内全部行。请谨慎修改
     * @return 列表
     */
    public List<String> getAllLines() {
        return mAllLines;
    }

    /**
     * 从某个smali文件中，尝试寻找成员变量，以获取当前功能的安装版本号
     * <p/>
     * java格式应该如：private static final int VERSION_FOR_EDPATCH = 1;
     * <p/>
     * smali格式应该如：.field private static final VERSION_FOR_EDPATCH:I = 0x1
     *
     * @param targetClassName 完整包名和类名。格式如：com.package.SomeClass
     * @return 版本号，如果找不到返回0 （还是说抛出异常比较好？）
     */
    public static int findVersionInClass(String targetClassName){
        SmaliFile smaliFile = new SmaliFile().findSmali(targetClassName);
        int version = INVALID_VERSION;
        if(smaliFile!=null){
            for (String line : smaliFile.mAllLines)
                if (line.contains(VERSION_FIELD_PREFIX)){
                    version =  Integer.parseInt(line.substring(VERSION_FIELD_PREFIX.length()+"0x".length()), 16);
                    break;
                }
            smaliFile.close();
        }
        return version;
    }

    public void getStaticFinalField(){

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


