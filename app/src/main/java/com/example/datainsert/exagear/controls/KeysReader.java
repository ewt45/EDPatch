package com.example.datainsert.exagear.controls;

import java.util.ArrayList;

public class KeysReader {

    /**
     * 一个按键的信息
     */
    public class KeyModel{
        String keycodeName;
        String text;
        int width;
        int height;
    }

    ArrayList<KeyModel> modifiers = new ArrayList<>();
    ArrayList<KeyModel> normals = new ArrayList<>();

    /**
     * 读取文件中的按钮数据
     */
    public void read(){

    }

}
