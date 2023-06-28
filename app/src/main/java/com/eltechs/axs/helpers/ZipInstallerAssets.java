package com.eltechs.axs.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.eltechs.axs.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class ZipInstallerAssets {

    public static void installIfNecessary(final Context context, final InstallCallback installCallback, final File dstDir, final String assetsName) {
        if (dstDir.exists()) {
            if (dstDir.isDirectory()) {
                installCallback.installationFinished(dstDir.getAbsolutePath());
                return;
            } else {
                installCallback.installationFailed(context.getResources().getString(R.string.directory_is_occupied));
                return;
            }
        }
        new AsyncTask<Object, Void, Void>() { // from class: com.eltechs.axs.helpers.ZipInstallerAssets.1
            @Override // android.os.AsyncTask
            protected Void doInBackground(Object... objArr) {
                File extractedAssetsInDstDir = new File(dstDir, assetsName);
                try {
                    if (!dstDir.mkdirs()) {
                        throw new IOException(String.format("Failed to create the directory '%s'.", dstDir.getAbsolutePath()));
                    }
                    InputStream open = context.getAssets().open(assetsName);
                    FileOutputStream fileOutputStream2 = new FileOutputStream(extractedAssetsInDstDir);
                    IOStreamHelpers.copy(open, fileOutputStream2);
                    open.close();
                    try {
                        fileOutputStream2.close();
                        ZipUnpacker.unpackZip(extractedAssetsInDstDir, dstDir, null);
                        extractedAssetsInDstDir.delete();

//                        //没法从assets提取文件夹，自己压缩的recipe.zip又有问题，那就手动复制一下单个sh吧
//                        InputStream shIs = context.getAssets().open("recipe/run/simple.sh");
//                        FileOutputStream shFos = new FileOutputStream(new File(dstDir,"/run/simple.sh"));
//                        IOStreamHelpers.copy(shIs,shFos);
//                        shIs.close();
//                        shFos.close();
                        UiThread.post(new Runnable() { // from class: com.eltechs.axs.helpers.ZipInstallerAssets.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                installCallback.installationFinished(dstDir.getAbsolutePath());
                            }
                        });
                    } catch (IOException e2) {
                        try {
                            fileOutputStream2.close();
                        } catch (IOException ignored) {
                        }
                        if (dstDir.exists()) {
                            dstDir.delete();
                        }
                        if (extractedAssetsInDstDir.exists()) {
                            extractedAssetsInDstDir.delete();
                        }
                        UiThread.post(() -> installCallback.installationFailed(
                                String.format("%s; %s", context.getResources().getString(R.string.fail_to_unpack_zip),
                                        e2.getMessage())));
                        return null;
                    }
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    /* loaded from: classes.dex */
    public interface InstallCallback {
        void installationFailed(String str);

        void installationFinished(String str);
    }
}