package com.ewt45.patchapp;

import static android.support.v4.content.FileProvider.getUriForFile;
import static com.ewt45.patchapp.PatchUtils.getExaNewPatchedSignedApk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.ewt45.patchapp.model.GithubReleaseData;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AndroidUtils {
    private static final String TAG = "AndroidUtils";

    public static int toPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp(相对大小)
     */
    public static int toDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void uiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static void uiThread(int delay, Runnable runnable) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delay);
    }

    /**
     * 显示snackbar。在step_main的fragment的coordinator中显示，而非activity的根coordinator，以便中间内容躲避。
     */
    public static void showSnack(Activity activity, int msg) {
        showSnack(activity, activity.getString(msg));
    }

    public static void showSnack(Activity activity, String msg) {
        //添加动画效果 (child.setLayoutTransition();好像可以？)
        //如果已经显示，先隐藏再显示
        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.patch_step_coordinator), msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(android.R.string.yes, v -> snackbar.dismiss()); //颜色在theme里设置
        snackbar.show();
    }

    public static SharedPreferences getPrefs() {
        return MyApplication.i.getSharedPreferences(MyApplication.PREFERENCE, Context.MODE_PRIVATE);
    }

    public static void openLink(Context c,String link){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            if (intent.resolveActivity(c.getPackageManager()) != null) {
                c.startActivity(intent);
            }
    }

    public static void installApk(Context c,Uri uri){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        c.startActivity(intent);
    }
}
