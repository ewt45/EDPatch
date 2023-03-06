package com.example.datainsert.exagear.controls.model;

import android.content.Context;
import android.widget.CompoundButton;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.AvailableKeysView;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.JoyStickBtn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于自定义位置的全部按键记录
 */
public class KeyCodes3 implements Serializable {
    public static final String KeyStoreFileName = "custom_control3.ser";
    private static final long serialVersionUID = 4579853863658738317L;
    List<OneKey> mKeyList;

    List<JoyStickBtn.Params> mJoyList;

    public KeyCodes3(){
        mKeyList = new ArrayList<>();
        mJoyList = new ArrayList<>();
        //直接把全部按键加进去吧，然后加个true false判断是否显示（emmm从本地反序列化使用不到这个构造方法的，用这个说明是初次还没序列化过）
        for(int i = 0; i< AvailableKeysView.codes.length; i++){
            OneKey oneKey = new OneKey(AvailableKeysView.codes[i],AvailableKeysView.names[i]);
            oneKey.setShow(false);
            mKeyList.add(oneKey);
        }
    }

    public List<OneKey> getKeyList() {
        return mKeyList;
    }

    public List<JoyStickBtn.Params> getJoyList() {
        return mJoyList;
    }

    public static void serialize(KeyCodes3 keyCodes3, Context c) throws IOException {
        File file = new File(c.getExternalFilesDir(null), KeyCodes3.KeyStoreFileName);

        if(file.exists())
            file.delete();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(keyCodes3);
        oos.close();
        fos.close();

    }

    public static KeyCodes3 deserialize(Context c) throws IOException, ClassNotFoundException {
        File file = new File(c.getExternalFilesDir(null), KeyCodes3.KeyStoreFileName);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        KeyCodes3 keyread = (KeyCodes3) ois.readObject();
        ois.close();
        //老版本的按键个数只有5个。。。
        if(keyread.mKeyList.size()!=AvailableKeysView.codes.length)
            keyread = new KeyCodes3();
        return keyread;

    }
}
