package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import android.util.Log;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMState2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * 用于反序列化时还原抽象类，以及处理文件位置
 * <br/> 配置存储规则：
 * workdir：所有相关文件的父目录
 * profilesDir: 存放全部配置文件
 * currentProfile： 当前配置文件，在profilesDir外面，是某个配置的软连接
 */
public class DeserializerOfModel implements JsonDeserializer<TouchAreaModel> {
    private static final String TAG = "GsonProcessor";
    /**
     * 有关state的反序列化应该放在这里而不是主gson。
     */
    Gson modelSubclassGson = new GsonBuilder()
            .registerTypeHierarchyAdapter(FSMState2.class,new DeserializerOfModel.DeserializerOfFSMState())
            .create();

    @Override
    public TouchAreaModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d("GsonDeserializer", "deserialize: ");
        if (!(json instanceof JsonObject))
            throw new RuntimeException("反序列化model的JsonElement应该是个JsonObject： " + json);

        JsonObject jsonObject = (JsonObject) json;
        //不能直接用传入的context，因为这个context设置了TouchAreaModel的自定义反序列化（就是这个类本身），会死循环
        int modelType = jsonObject.get(Const.GsonField.md_ModelType).getAsInt();
        return modelSubclassGson.fromJson(json, ModelProvider.getModelClass(modelType));
    }


    private static class DeserializerOfFSMState implements JsonDeserializer<FSMState2> {
        Gson plainGson = new Gson();
        @Override
        public FSMState2 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = (JsonObject) json;
            //不能直接用传入的context，因为这个context设置了TouchAreaModel的自定义反序列化（就是这个类本身），会死循环
            int stateTag = jsonObject.get(Const.GsonField.st_StateType).getAsInt();
            return plainGson.fromJson(json, ModelProvider.getStateClassByTypeInt(stateTag));
        }
    }
}
