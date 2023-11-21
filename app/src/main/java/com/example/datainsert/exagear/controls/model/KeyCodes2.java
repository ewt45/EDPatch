package com.example.datainsert.exagear.controls.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.eltechs.axs.Globals;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.CustomControls;
import com.example.datainsert.exagear.QH;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KeyCodes2 implements Serializable {

    public static final String KeyStoreFileName = "custom_control2.ser";  //"custom_control2.ser";
    public static final String KeyStoreFileNameNew = "custom_control2.txt";  //"custom_control2.ser";

    private static final long serialVersionUID = -4696658597434288880L;
    public static final File oldKeyStoreFile = new File(Globals.getAppContext().getExternalFilesDir(null),KeyStoreFileNameNew);
    public static final File keyStoreFile = new File(CustomControls.dataDir(),KeyStoreFileNameNew);

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

    /**
     * 从本地读取按键。若没有文件或读取失败，返回一个空的KeyCodes2
     */
    public static @NonNull KeyCodes2 read(Context c){

        File file = oldKeyStoreFile;

        KeyCodes2 keyCodes2  = new KeyCodes2();
        if(!file.exists())
            return keyCodes2;
        List<String> fileLines ;
        try {
            fileLines= FileUtils.readLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return keyCodes2;
        }
        return FormatHelper.stringToKeyCodes2(fileLines);

    }
    /**
     * 将按键写入本地。

     */
    public static void  write(KeyCodes2 keyCodes2,Context c){
        File file = oldKeyStoreFile;
        if(file.exists()){
            boolean b=file.delete();
        }
        try {
            FileUtils.writeLines(file,StandardCharsets.UTF_8.name(),FormatHelper.keyCodes2ToString(keyCodes2),FormatHelper.lineSeparator,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @deprecated 序列化不好转移。请使用read 和 write方法读写txt
     */
    @Deprecated
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
    /**
     * @deprecated 序列化不好转移。请使用read 和 write方法读写txt
     */
    @Deprecated
    public static KeyCodes2 deserialize(Context c) throws IOException, ClassNotFoundException {
        File file = new File(c.getExternalFilesDir(null), KeyCodes2.KeyStoreFileName);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        KeyCodes2 keyread = (KeyCodes2) ois.readObject();
        ois.close();
        return keyread;

    }


}
