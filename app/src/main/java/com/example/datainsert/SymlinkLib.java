package com.example.datainsert;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SymlinkLib {
    public static void create(Context c)  {
        File target2ParentFile = c.getFilesDir().getParentFile();
        File target2File = new File(target2ParentFile, "lib2");
        File extFile = c.getExternalFilesDir("");

        if (
//                !target2File.exists() && !target2File.mkdirs() ||
                        extFile == null
                || !extFile.exists() && !extFile.mkdirs()) {
            Log.d("", "create: 创建失败，return");
            return;
        }
        File linkParentFile = extFile.getParentFile();
        File linkFile = new File(linkParentFile, "lib");
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.createSymbolicLink(target2File.toPath(),linkFile.toPath() );
            }
            Log.d("TAG", "create: 创建成功");
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
