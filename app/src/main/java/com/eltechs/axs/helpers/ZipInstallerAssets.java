package com.eltechs.axs.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.eltechs.axs.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Predicate;

/* loaded from: classes.dex */
public class ZipInstallerAssets {

    public static void installIfNecessary(final Context context, final InstallCallback installCallback, final File recipesGuestDir, final String assetName) {
        if (recipesGuestDir.exists()) {
            if (recipesGuestDir.isDirectory()) {
                installCallback.installationFinished(recipesGuestDir.getAbsolutePath());
                return;
            } else {
                installCallback.installationFailed(context.getResources().getString(R.string.directory_is_occupied));
                return;
            }
        }

        new AsyncTask<Object, Void, Void>() { // from class: com.eltechs.axs.helpers.ZipInstallerAssets.1
            @Override // android.os.AsyncTask
            protected Void doInBackground(Object... objArr) {
                File extractedAssets = new File(recipesGuestDir, assetName); // /opt/recipe, recipe.zip
                try {
                    if (!recipesGuestDir.mkdirs()) {
                        throw new IOException(String.format("Failed to create the directory '%s'.", recipesGuestDir.getAbsolutePath()));
                    }
                    InputStream inputStream = context.getAssets().open(assetName);
                    FileOutputStream extAssetsStream = new FileOutputStream(extractedAssets);
                    IOStreamHelpers.copy(inputStream, extAssetsStream);
                    inputStream.close();
                    try {
                        extAssetsStream.close();
                        ZipUnpacker.unpackZip(extractedAssets, recipesGuestDir, null);
                        extractedAssets.delete();
                        UiThread.post(() -> installCallback.installationFinished(recipesGuestDir.getAbsolutePath()));
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        try {
                            extAssetsStream.close();
                        } catch (IOException ignored) {
                        }
                        if (recipesGuestDir.exists()) {
                            recipesGuestDir.delete();
                        }
                        if (extractedAssets.exists()) {
                            extractedAssets.delete();
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