package com.example.datainsert.exagear.controlsV2.model;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TouchArea;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.ActionButtonClick;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.ActionPointerMove;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.ActionRunOption;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.State1FingerMoveToMouseMove;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.State2FingersZoom;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateCheckFingerNearToPointer;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateCountDownMeasureSpeed;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateCountDownWaitFingerNumChange;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateFingerMoveToMouseScroll;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateNeutral;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateWaitForNeutral;
import com.example.datainsert.exagear.controlsV2.touchArea.TouchAreaButton;
import com.example.datainsert.exagear.controlsV2.touchArea.TouchAreaDpad;
import com.example.datainsert.exagear.controlsV2.touchArea.TouchAreaGesture;
import com.example.datainsert.exagear.controlsV2.touchArea.TouchAreaStick;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class ModelProvider {
    private static final String TAG = "ModelProvider";
    public static final int[] modelTypeInts = new int[]{
            TouchAreaModel.TYPE_BUTTON,
            TouchAreaModel.TYPE_STICK,
            TouchAreaModel.TYPE_DPAD,
            TouchAreaModel.TYPE_GESTURE,};
    public static final Class<? extends TouchAreaModel>[] modelClasses = new Class[]{
            OneButton.class,
            OneStick.class,
            OneDpad.class,
            OneGestureArea.class};
    public static final Class<? extends TouchArea<?>>[] areaClasses = new Class[]{
            TouchAreaButton.class,
            TouchAreaStick.class,
            TouchAreaDpad.class,
            TouchAreaGesture.class
    };

    public static  final int[] stateTypeInts = new int[]{
            FSMR.state.限时测速,
            FSMR.state.初始状态,
            FSMR.state.回归初始状态,
            FSMR.state.一指移动带动鼠标移动,
            FSMR.state.操作_点击,
            FSMR.state.操作_鼠标移动,
            FSMR.state.手指移动_鼠标滚轮,
            FSMR.state.判断_手指与鼠标位置距离,
            FSMR.state.监测手指数量变化,
            FSMR.state.两根手指缩放,
            FSMR.state.操作_直接执行选项
    };

    public static final Class<? extends FSMState2>[] stateClasses = new Class[]{
            StateCountDownMeasureSpeed.class,
            StateNeutral.class,
            StateWaitForNeutral.class,
            State1FingerMoveToMouseMove.class,
            ActionButtonClick.class,
            ActionPointerMove.class,
            StateFingerMoveToMouseScroll.class,
            StateCheckFingerNearToPointer.class,
            StateCountDownWaitFingerNumChange.class,
            State2FingersZoom.class,
            ActionRunOption.class
    };
    public static File workDir;
    public static File profilesDir;
    public static File currentProfile;

    public static Class<? extends TouchArea<? extends TouchAreaModel>> getAreaClass(Class<? extends TouchAreaModel> modelClass) {
           return areaClasses[indexOf(modelClasses,modelClass)];
    }

    public static <T>int indexOf(T[] arr, T var){
        for(int i=0 ; i<arr.length; i++)
            if(arr[i].equals(var))
                return i;
       throw new RuntimeException("无法在数组中找到元素："+ var+", "+Arrays.toString(arr));
    }

    /**
     * 根据type int值 获取对应的model class
     */
    public static  Class<? extends TouchAreaModel> getModelClass(@TouchAreaModel.ModelType int typeInt){
        int typeIndex = 0;
        for(; typeIndex<ModelProvider.modelTypeInts.length; typeIndex++)
            if(ModelProvider.modelTypeInts[typeIndex] == typeInt)
                break;

        return ModelProvider.modelClasses[typeIndex];
    }



    /**
     * 根据stateTag int值 获取对应的state class
     */
    public static  Class<? extends FSMState2> getStateClass(int typeInt){
        int typeIndex = 0;
        for(; typeIndex<ModelProvider.stateTypeInts.length; typeIndex++)
            if(ModelProvider.stateTypeInts[typeIndex] == typeInt)
                break;

        return ModelProvider.stateClasses[typeIndex];
    }

    /**
     * 根据状态类型，新加该状态实例
     */
    public static <T extends FSMState2> T getStateInstance(int typeInt){
        try {
            return (T) getStateClass(typeInt).newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
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
            Gson gson = new Gson();
            String jsonStr = gson.toJson(profile);
//            Log.d(TAG, "转为json：" + jsonStr);

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
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(TouchAreaModel.class, new DeserializerOfModel())
                .create();
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
        OneProfile newProfile = ref != null ? readProfile(ref) : new OneProfile(newName);
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


}