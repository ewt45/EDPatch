package com.example.datainsert.exagear.controls;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;
//存储在button的tag中
public class NewKeyPrefs implements Serializable {
    public String customName;
    public String keyName;
    public int keycode;
    public int[] coordinate= new int[3];  //左右，列。行
    public boolean floatOnTop; //是否固定在顶部
    public int[] metrics= new int[]{WRAP_CONTENT,WRAP_CONTENT}; //宽，高 单位dp
    public boolean longClick; //是否单击=长按
    public int color;


    @NonNull
    @Override
    public String toString() {
        return "NewKeyPrefs{" +
                "customName='" + customName + '\'' +
                ", keyName='" + keyName + '\'' +
                ", keycode=" + keycode +
                ", coordinate=" + Arrays.toString(coordinate) +
                ", floatOnTop=" + longClick +
                ", metrics=" + Arrays.toString(metrics) +
                ", longClick=" + longClick +
                '}';
    }
}