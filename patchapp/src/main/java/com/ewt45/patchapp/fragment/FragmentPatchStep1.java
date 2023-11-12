package com.ewt45.patchapp.fragment;

import static android.content.pm.PackageManager.GET_META_DATA;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ewt45.patchapp.AndroidUtils;
import com.ewt45.patchapp.MyApplication;
import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.databinding.FragmentPatchStep1Binding;
import com.ewt45.patchapp.thread.DecodeApk;
import com.ewt45.patchapp.thread.SignalDone;
import com.ewt45.patchapp.widget.ActionProgressDialog;
import com.ewt45.patchapp.widget.SelectApkDialog;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class FragmentPatchStep1 extends BaseFragmentPatchStep {
    private static final int REQUEST_PICK_APK_FILE = 2;
    private static final String TAG = "FragmentPatchStep1";
    FragmentPatchStep1Binding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 点击tab的时候会重新初始化布局吗？");
        binding = FragmentPatchStep1Binding.inflate(inflater, container, false);


        binding.btnSelectApkFiles.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.android.package-archive");//仅显示apk类型
            startActivityForResult(intent, REQUEST_PICK_APK_FILE);
            Log.d(TAG, "onViewCreated: startActivityForResult跳转到文件选择界面之后函数是立刻返回吗");//是的
        });

        binding.btnSelectApkInstalled.setOnClickListener(v -> new SelectApkDialog()
                .setCallback(file -> binding.getRoot().post(() -> updateApkInfo(Uri.fromFile(file))))
                .show(getChildFragmentManager(), null));


        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: step1");
        MyApplication.data.currentStepIndex = 1;

        getFAB().setOnClickListener(v -> getNavController().navigate(R.id.action_patch_step1_to_step2));
        getFAB().setImageResource(R.drawable.ic_arrow_forward);
        getFAB().hide();
        setStepTitle(R.string.patchstep1_title);
        updateApkInfo(null);
        AndroidUtils.showSnack(requireActivity(), R.string.tips_backup);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取选中的，想要修改apk
        if (requestCode == REQUEST_PICK_APK_FILE && data != null) {
            updateApkInfo(data.getData()); //只有不为null时才执行。
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 如果传入为null，则从本地filesdir中读取已复制的apk的信息。
     * 如果传入不为null，则说明是新选择了一个apk，将这个apk复制到filesdir
     */
    private void updateApkInfo(Uri uri) {
        if (uri == null) {
            File patchTmpFile = PatchUtils.getPatchTmpApk();
            File tmpExtractDir = PatchUtils.getExaExtractDir();
            boolean isExaApkDecoded = patchTmpFile.exists() && tmpExtractDir.isDirectory() && tmpExtractDir.list().length > 0;
            //解析apk，获取信息
            if (isExaApkDecoded) {

                getFAB().show();
                PackageManager pm = requireContext().getPackageManager();
                PackageInfo pkgInfo = pm.getPackageArchiveInfo(patchTmpFile.getAbsolutePath(), GET_META_DATA);
                ApplicationInfo appInfo = pkgInfo.applicationInfo;
                binding.appIcon.setImageDrawable(appInfo.loadIcon(pm));
                String infoName = appInfo.loadLabel(pm).toString();
                String infoPkgName = pkgInfo.packageName;
                String infoVersion = pkgInfo.versionName;
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(infoName).setSpan(new RelativeSizeSpan(1.5f), 0, infoName.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append('\n').append(infoPkgName).append('\n').append(infoVersion);
                binding.appInfo.setText(builder);
            } else {
                getFAB().hide();
                binding.appIcon.setImageResource(R.drawable.ic_apk_document);
                binding.appInfo.setText(R.string.patchstep1_tv_noapkfound);
            }
            return;
        }

        //uri！=null
        try {
            PatchUtils.copyToExternalFiles(requireContext(), uri);

            //解包exagear的apk
            Log.d(TAG, "decodeApk: 已复制ed apk到自己目录下，开始解包");

            ActionProgressDialog.startActionsWithDialog(requireContext(), noError -> {
                if (!noError) {
                    FileUtils.deleteQuietly(PatchUtils.getPatchTmpApk());
                    FileUtils.deleteQuietly(PatchUtils.getExaExtractDir());
                }
                AndroidUtils.showSnack(requireActivity(), noError ? R.string.tips_backup : R.string.tips_action_failed);
                updateApkInfo(null);
            }, new DecodeApk(DecodeApk.EXAGEAR), new SignalDone());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
