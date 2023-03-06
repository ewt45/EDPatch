package com.example.datainsert.exagear.controls.model;

import android.content.Context;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.widget.Button;

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
    private static final long serialVersionUID = -4696658597434288880L;


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

    public static void serialize(KeyCodes2 keyCodes2, Context c) throws IOException {
        File file = new File(c.getExternalFilesDir(null), KeyCodes2.KeyStoreFileName);
        if(file.exists())
            file.delete();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(keyCodes2);
        oos.close();
        fos.close();
    }

    public static KeyCodes2 deserialize(Context c) throws IOException, ClassNotFoundException {
        File file = new File(c.getExternalFilesDir(null), KeyCodes2.KeyStoreFileName);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        KeyCodes2 keyread = (KeyCodes2) ois.readObject();
        ois.close();
        return keyread;

    }


}
