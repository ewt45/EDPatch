package com.eltechs.axs.helpers;

import android.content.Context;
import android.os.AsyncTask;
import com.eltechs.axs.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class ZipInstallerAssets {

    /* loaded from: classes.dex */
    public interface InstallCallback {
        void installationFailed(String str);

        void installationFinished(String str);
    }

    public static void installIfNecessary(final Context context, final InstallCallback installCallback, final File file, final String str) {
        if (file.exists()) {
            if (file.isDirectory()) {
                installCallback.installationFinished(file.getAbsolutePath());
                return;
            } else {
                installCallback.installationFailed(context.getResources().getString(R.string.directory_is_occupied));
                return;
            }
        }

    }
}