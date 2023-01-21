package com.example.datainsert.exagear.cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.eltechs.axs.Globals;

import java.io.IOException;

public class CursorImage {
    public static Bitmap createBitmap(){

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
