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

    public static void installIfNecessary(final Context context, final InstallCallback installCallback, final File file, final String assetsName) {
        if (file.exists()) {
            if (file.isDirectory()) {
                installCallback.installationFinished(file.getAbsolutePath());
                return;
            } else {
                installCallback.installationFailed(context.getResources().getString(R.string.directory_is_occupied));
                return;
            }
        }
        new AsyncTask<Object, Void, Void>() { // from class: com.eltechs.axs.helpers.ZipInstallerAssets.1
            @Override // android.os.AsyncTask
            protected Void doInBackground(Object... objArr) {
                File file2 = new File(file, assetsName);
                try {
                    if (!file.mkdirs()) {
                        throw new IOException(String.format("Failed to create the directory '%s'.", file.getAbsolutePath()));
                    }
                    InputStream open = context.getAssets().open(assetsName);
                    FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
                    IOStreamHelpers.copy(open, fileOutputStream2);
                    open.close();
                    try {
                        fileOutputStream2.close();
                        ZipUnpacker.unpackZip(file2, file, null);
                        file2.delete();

                        //没法从assets提取文件夹，自己压缩的recipe.zip又有问题，那就手动复制一下单个sh吧
                        InputStream shIs = context.getAssets().open("recipe/run/simple.sh");
                        FileOutputStream shFos = new FileOutputStream(new File(file,"/run/simple.sh"));
                        IOStreamHelpers.copy(shIs,shFos);
                        shIs.close();
                        shFos.close();
                        UiThread.post(new Runnable() { // from class: com.eltechs.axs.helpers.ZipInstallerAssets.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                installCallback.installationFinished(file.getAbsolutePath());
                            }
                        });
                    } catch (IOException e2) {
                        try {
                            fileOutputStream2.close();
                        } catch (IOException ignored) {
                        }
                        if (file.exists()) {
                            file.delete();
                        }
                        if (file2.exists()) {
                            file2.delete();
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