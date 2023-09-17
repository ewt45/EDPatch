package com.winlator.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.system.ErrnoException;
import android.system.Os;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class FileUtils {
    public static void symlink(String linkName, String absolutePath) {
    }

    public static void chmod(File file, int i) {
        try {
            Os.chmod(file.getAbsolutePath(), i);
        } catch (ErrnoException ignored) {
        }

    }

    public static boolean isSymlink(File file) {
        return file.listFiles().length>1;
    }

    public static int findOBBFile(Context context, AtomicReference<File> result) {
        result.set(new File(context.getFilesDir(),"obb"));
        return 1;
    }

}
