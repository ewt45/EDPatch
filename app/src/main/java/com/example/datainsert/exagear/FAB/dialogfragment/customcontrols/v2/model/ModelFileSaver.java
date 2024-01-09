package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import android.support.annotation.NonNull;
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

import java.io.File;
import java.io.IOException;
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

    /**
     * 将profile转为json存为本地文件
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
     */
    public static @NonNull OneProfile readProfile(String name) {
        try {
            File file = new File(profilesDir, name);
            if (!file.exists())
                throw new RuntimeException("文件不存在");

            String jsonStr = FileUtils.readFileToString(file);
            Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(TouchAreaModel.class, Const.modelSaver).create();
            OneProfile oneProfile = gson.fromJson(jsonStr, OneProfile.class);
            Log.d(TAG, "转为对象：" + oneProfile);
            return oneProfile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static @NonNull OneProfile readCurrentProfile(){
        try {
            return readProfile(currentProfile.getCanonicalFile().getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将某个配置作为当前选中的配置
     */
    public static void makeCurrent(String name){
        File profile = new File(profilesDir,name);
        try {
            Os.symlink(profile.getAbsolutePath(),currentProfile.getAbsolutePath());
        } catch (ErrnoException e) {
            e.printStackTrace();
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
