package com.example.datainsert.exagear.controls.model;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KeyCodes2 implements Serializable {

    public static final String KeyStoreFileName = "custom_control2.ser";


    private List<OneCol> mLeftSide = new ArrayList<>();
    private List<OneCol> mRightSide = new ArrayList<>();

    public KeyCodes2() {

        //初始化mKeyList

    }

    public List<OneCol> getLeftSide() {
        return mLeftSide;
    }

    public List<OneCol> getRightSide() {
        return mRightSide;
    }

    public void setLeftSide(List<OneCol> mLeftSide) {
        this.mLeftSide = mLeftSide;
    }

    public void setRightSide(List<OneCol> mRightSide) {
        this.mRightSide = mRightSide;
    }

    public static void serialize(KeyCodes2 keyCodes2, File file) throws IOException {
        if(file.exists())
            file.delete();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(keyCodes2);
        oos.close();
        fos.close();
    }

    public static KeyCodes2 deserialize(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        KeyCodes2 keyread = (KeyCodes2) ois.readObject();
        ois.close();
        return keyread;

    }

}
