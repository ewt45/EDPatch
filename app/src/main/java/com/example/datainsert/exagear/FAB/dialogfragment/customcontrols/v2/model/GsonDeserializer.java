package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import android.util.Log;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class GsonDeserializer implements JsonDeserializer<TouchAreaModel> {
    private static final String typeFieldName = "modelType";
    Gson gson = new Gson();

    @Override
    public TouchAreaModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d("GsonDeserializer", "deserialize: ");
        if (!(json instanceof JsonObject))
            throw new RuntimeException("反序列化model的JsonElement应该是个JsonObject： " + json);

        JsonObject jsonObject = (JsonObject) json;
        //不能直接用传入的context，因为这个context设置了TouchAreaModel的自定义反序列化（就是这个类本身），会死循环
        return gson.fromJson(json,Const.modelTypeArray.get(jsonObject.get(typeFieldName).getAsInt()));
    }
}
