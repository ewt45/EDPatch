package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * 用于反序列化时还原抽象类，以及处理文件位置
 * <br/> 配置存储规则：
 * workdir：所有相关文件的父目录
 * profilesDir: 存放全部配置文件
 * currentProfile： 当前配置文件，在profilesDir外面，是某个配置的软连接
 */
public class ModelFileSaver implements JsonDeserializer<TouchAreaModel> {
    private static final String TAG = "GsonProcessor";
    private static final String typeFieldName = "modelType";
    public static File workDir;
    public static File profilesDir;
    public static File currentProfile;
    Gson mSubGson = new Gson();

    public ModelFileSaver(String workPath) {
        workDir = new File(workPath);
        profilesDir = new File(workDir, "profiles");
        currentProfile = new File(workDir, "current");
    }

    public static String getCurrentProfileCanonicalName() {
        try {
            return currentProfile.getCanonicalFile().getName();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将profile转为json存为本地文件。
     * <br/> 存储路径：profilesDir内，profile.name作为文件名。
     * <br/> 若不存在该名称的文件，则会新建一个
     */
    public static void saveProfile(OneProfile profile) {
        try {
            Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(TouchAreaModel.class, Const.modelSaver).create();
            String jsonStr = gson.toJson(profile);
            Log.d(TAG, "转为json：" + jsonStr);

            File file = new File(profilesDir, profile.name);
            if (file.exists() && !file.delete())
                Log.e(TAG, "文件存在且无法删除");
            FileUtils.writeStringToFile(file, jsonStr);

//            OneProfile oneProfile = gson.fromJson(jsonStr, OneProfile.class);
//            oneProfile.syncAreaList(mHost.mHost);
//            Log.d(TAG, "转为对象：" + oneProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地文件读取json转为oneProfile对象
     * <br/> 由于反序列化时，只能生成model列表，不能生成area列表，所以获取实例后请手动调用sync函数同步area列表
     *
     * @param name 配置名，在profilesDir目录下寻找同名文件
     */
    public static @NonNull OneProfile readProfile(String name) {
        try {
            return readProfileFromFile(new File(profilesDir, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static OneProfile readProfileFromFile(File file) throws IOException {
        if (!file.exists())
            throw new RuntimeException("文件不存在");

        String jsonStr = FileUtils.readFileToString(file);
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(TouchAreaModel.class, Const.modelSaver).create();
        OneProfile oneProfile = gson.fromJson(jsonStr, OneProfile.class);
        Log.d(TAG, "转为对象：" + oneProfile);
        return oneProfile;
    }

    public static @NonNull OneProfile readCurrentProfile() {
        try {
            return readProfile(currentProfile.getCanonicalFile().getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将某个配置作为当前选中的配置
     */
    public static void makeCurrent(String name) {
        File profile = new File(profilesDir, name);
        try {
            boolean b = currentProfile.delete();
            Os.symlink(profile.getAbsolutePath(), currentProfile.getAbsolutePath());
        } catch (ErrnoException e) {
            e.printStackTrace();
        }
    }

    /**
     * 第一次创建目录/新建空白配置/复制已有配置
     * <br/>创建一个新的配置，（并将该配置设置为当前配置）
     * <br/>最后同{@link #readProfile(String)}，请手动调用sync函数同步area列表
     *
     * @param newName     新配置的名称，请先通过{@link #getNiceProfileName(String)} 确保该名字的配置不存在
     * @param ref         可选。若不为空，则复制该配置，并修改名称
     * @param makeCurrent 创建后是否设置为当前配置
     * @return 新生成的配置
     */
    public static OneProfile createNewProfile(String newName, @Nullable String ref, boolean makeCurrent) {
        File newFile = new File(profilesDir, newName);
        if (newFile.exists())
            throw new RuntimeException("同名profile已存在，无法创建");
        //为防止与现在的实例冲突，所以需要从json重新构建一个实例。
        // 然后需要改成指定的名字，再存为json
        OneProfile newProfile = ref != null ? readProfile(ref) : OneProfile.newInstance();
        newProfile.name = newName;
        saveProfile(newProfile);
        if (makeCurrent)
            makeCurrent(newName);
        return readProfile(newName);
    }

    /**
     * 重命名或新建时，检查想用的名字是否有特殊字符，和是否已存在，若有冲突，则返回不冲突的名字
     * <br/> <a href="https://www.jianshu.com/p/e1c8e4934015">文件名特殊字符检查</a>
     */
    public static String getNiceProfileName(String idealName) {
        StringBuilder builder = new StringBuilder();
        //检查有无特殊字符
        for (int i = 0; i < idealName.length(); i++) {
            char ch = idealName.charAt(i);
            builder.append(ch <= 0x1f || ch == '"' || ch == '*' || ch == '/' || ch == '\\' || ch == ':' || ch == '<' || ch == '>' || ch == '?' || ch == 0x7F
                    ? '_'
                    : ch);
        }
        if (builder.length() == 0)
            builder.append("control_profile");

        //检查是否有同名的
        for (int i = 1; ; i++) {
            if (new File(profilesDir, builder.toString()).exists()) {
                if (i == 1) builder.append('_');
                builder.deleteCharAt(builder.length() - 1).append(i);
            } else
                break;
        }
        return builder.toString();
    }

    /**
     * 从uri读取出一个oneProfile对象，并存入profilesDir
     */
    public static OneProfile importProfileFromUri(Uri uri) throws Exception {
        Context c = Const.getContext();
        File tmpFile = new File(c.getFilesDir(), "tmp_control_profile");
        tmpFile.delete();
        try (InputStream is = c.getContentResolver().openInputStream(uri);
             FileOutputStream fos = new FileOutputStream(tmpFile);) {
            assert is != null;
            IOUtils.copy(is, fos);
        }
        OneProfile profile = readProfileFromFile(tmpFile);

        String fitName = getNiceProfileName(profile.name);
        profile.name = fitName;
        FileUtils.moveFile(tmpFile, new File(profilesDir, fitName));

        return profile;

    }

    /**
     * 将配置导出到uri对应的路径。
     */
    public static void exportProfileToUri(Uri uri, String name) throws Exception {
        Context c = Const.getContext();
        File tmpFile = new File(c.getFilesDir(), "tmp_control_profile");
        tmpFile.delete();
        try (OutputStream os = c.getContentResolver().openOutputStream(uri);
             FileInputStream fis = new FileInputStream(new File(profilesDir, name));) {
            IOUtils.copy(fis, os);
        }
    }

    @Override
    public TouchAreaModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d("GsonDeserializer", "deserialize: ");
        if (!(json instanceof JsonObject))
            throw new RuntimeException("反序列化model的JsonElement应该是个JsonObject： " + json);

        JsonObject jsonObject = (JsonObject) json;
        //不能直接用传入的context，因为这个context设置了TouchAreaModel的自定义反序列化（就是这个类本身），会死循环
        return mSubGson.fromJson(json, Const.modelTypeArray.get(jsonObject.get(typeFieldName).getAsInt()));
    }


}
