package com.example.datainsert.exagear.cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ExagearImageAware;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CursorImage {
    private static final int VERSION_FOR_EDPATCH = 2;
    public static Bitmap createBitmap(){

        //先尝试从/opt/mouse.png读取图片，再尝试从apk读取图片
        File pngInImage = new File(((ExagearImageAware) Globals.getApplicationState()).getExagearImage().getPath().getAbsolutePath()+"/opt/mouse.png");
        if(pngInImage.exists()){
            try (FileInputStream fis = new FileInputStream(pngInImage);) {
                return BitmapFactory.decodeStream(fis);
            } catch (IOException e) {
                Log.d("TAG", "createXCursorBitmap: 图片不在数据包中，尝试从apk中寻找");
                e.printStackTrace();
            }
        }
        try {
            return  BitmapFactory.decodeStream(Globals.getAppContext().getResources().getAssets().open("mouse.png"));
        } catch (IOException e) {
            Log.d("TAG", "createXCursorBitmap: 找不到鼠标图片，还是用×");
            e.printStackTrace();
        }
        Bitmap createBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < 10; i++) {
            createBitmap.setPixel(i, i, -1);
            createBitmap.setPixel(i, 9 - i, -1);
        }
        return createBitmap;
    }
}
