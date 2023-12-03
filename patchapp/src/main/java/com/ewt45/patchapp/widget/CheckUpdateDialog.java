package com.ewt45.patchapp.widget;

import static android.content.Context.RECEIVER_EXPORTED;
import static com.ewt45.patchapp.AndroidUtils.uiThread;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ewt45.patchapp.AndroidUtils;
import com.ewt45.patchapp.BuildConfig;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.databinding.DialogCheckupdateBinding;
import com.ewt45.patchapp.model.GithubReleaseData;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CheckUpdateDialog extends AppCompatDialogFragment {
    private static final String TAG = "CheckUpdateDialog";
    private static boolean isHandlingDownloadComplete = false; //不知道为什么接收器会接收到两次，只好用个flag卡一下了
    private static long downloadID = -1; //下载文件对应的id
    private final BroadcastReceiver broadcastReceiver = new DownloadCompletedReceiver(); //最好在关闭dialog的时候取消注册这个接收器
    private DialogCheckupdateBinding binding;
    private boolean dialogClosed = false;
    /**
     * 下载新apk的相关信息
     */
    private GithubReleaseData.Asset mAsset = null;

    @SuppressLint({"UnspecifiedRegisterReceiverFlag", "WrongConstant"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogCheckupdateBinding.inflate(requireContext().getSystemService(LayoutInflater.class));
        binding.progressbar.setVisibility(View.VISIBLE);
        binding.linear.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), RECEIVER_EXPORTED);//要用exported，不然接收不到
        } else {
            requireActivity().registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        new Thread(() -> {
            Request request = new Request.Builder()
                    .url("https://api.github.com/repos/ewt45/EDPatch/releases/latest")
                    .get()
                    .build();

            try (Response response = new OkHttpClient().newCall(request).execute()) {
                if (!response.isSuccessful())
                    throw new IOException("response.isSuccessful()=false " + response);

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null)
                        throw new IOException("responseBody=null");

                    GithubReleaseData releaseData = new Gson().fromJson(responseBody.string(), GithubReleaseData.class);
                    uiThread(() -> compareVersion(releaseData));
                }
            } catch (IOException e) {
                e.printStackTrace();
                uiThread(() -> compareVersion(null));
            }
        }, "线程：检查更新").start();

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(binding.getRoot())
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        setCancelable(false);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(TAG, "onDismiss: cancel会进到这里嘛");//会进
        dialogClosed = true;
        requireActivity().unregisterReceiver(broadcastReceiver);
    }


    /**
     * 从github下载最新的release json后，调用此函数刷新ui
     *
     * @param data 下载的json，下载失败则为null
     */
    @SuppressLint({"UnspecifiedRegisterReceiverFlag", "SetTextI18n"})
    @MainThread
    public void compareVersion(GithubReleaseData data) {
        if (dialogClosed) {
            Log.d(TAG, "afterCheck: dialog关闭后，这个会识别到吗"); //会
            return;
        }

        Log.d(TAG, "afterCheck: 获取到最新release信息：" + data);
        binding.progressbar.setVisibility(View.GONE);
        binding.linear.setVisibility(View.VISIBLE);

        //下载json失败
        if (data == null) {
            setNetError("https://github.com/ewt45/EDPatch/releases");
            return;
        }

        boolean hasNewVersion = !BuildConfig.VERSION_NAME.equals(data.tag_name);
        //当前版本: %s\n最新版本: %s
        binding.text.setText(getString(R.string.tv_version_compare_versions, BuildConfig.VERSION_NAME, data.tag_name) +
                (hasNewVersion ? getString(R.string.tv_version_compare_need_update) : getString(R.string.tv_version_compare_is_latest)));
        binding.btn.setVisibility(hasNewVersion ? View.VISIBLE : View.GONE);

        //可更新
        if (hasNewVersion) {
            binding.btn.setText(R.string.btntxt_update);
            binding.btn.setOnClickListener(v -> {
                for (GithubReleaseData.Asset asset : data.assets)
                    if (asset.name.endsWith(".apk"))
                        mAsset = asset;
                if (mAsset == null)
                    return;

                //开始下载。获取唯一id
                Log.d(TAG, "downloadFromGithub: 提交下载链接到downloadManager：" + mAsset.browser_download_url);
                downloadID = requireActivity().getSystemService(DownloadManager.class).enqueue(
                        new DownloadManager.Request(Uri.parse(mAsset.browser_download_url))
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mAsset.name)
                                .setTitle("EDPatch"));

                binding.text.setText(R.string.tv_downloading);//正在下载，请勿切换界面。下载进度可在手机通知栏查看
                binding.btn.setVisibility(View.GONE);
            });
        }

    }

    public void setNetError(String link) {
        binding.text.setText(R.string.tv_please_open_manually);//网络连接错误，请手动打开外部链接查看或下载。
        binding.btn.setText(R.string.btntxt_open_link);
        binding.btn.setOnClickListener(v -> AndroidUtils.openLink(v.getContext(), link));

    }


    class DownloadCompletedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isHandlingDownloadComplete) {
                return;
            }
            isHandlingDownloadComplete = true;
            Log.d(TAG, "onReceive: 接收器接收到信息");

            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction()) || id == -1 || id != downloadID)
                return;

            Uri obbUri = requireContext().getSystemService(DownloadManager.class).getUriForDownloadedFile(id);
            if (obbUri != null) {
                binding.text.setText(R.string.tv_installing);//下载完成，正在安装
                binding.btn.setVisibility(View.VISIBLE);
                binding.btn.setText(R.string.btntxt_installnew);
                AndroidUtils.installApk(requireContext(), obbUri);
                binding.btn.setOnClickListener(v -> AndroidUtils.installApk(requireContext(), obbUri));
            } else if (mAsset != null) {
                setNetError(mAsset.browser_download_url);
            }
        }
    }
}
