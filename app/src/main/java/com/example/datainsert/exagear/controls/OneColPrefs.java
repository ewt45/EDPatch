package com.example.datainsert.exagear.controls;

import android.support.annotation.NonNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class OneColPrefs implements Serializable {
    public boolean special; //用于放特殊按钮的列，不属于两侧栏
    public int color;
    public int lor;
    public int col;
    public int width; //单位dp
    public ArrayList<NewKeyPrefs> btnPrefs;
    public OneColPrefs(){
        btnPrefs = new ArrayList<>();
        special=false;
    }

    public static OneColPrefs[][] deserialize(String pathStr) {
        if (pathStr == null) {
            pathStr = "/storage/emulated/0/Download/1.txt";
        }
        //试试反序列化
        OneColPrefs[][] localList = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathStr));
            localList = (OneColPrefs[][]) ois.readObject();
//            Log.d(TAG, "onViewCreated: 反序列化结果：" + Arrays.toString(localList));
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return localList;
    }

    @NonNull
    @Override
    public String toString() {
        return "OneColPrefs{" +
                "special=" + special +
                ", color=" + color +
                ", lor=" + lor +
                ", col=" + col +
                ", width=" + width +
                ", btnPrefs=" + btnPrefs +
                '}';
    }
}