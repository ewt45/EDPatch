package com.ewt45.patchapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ewt45.patchapp.databinding.FragmentChoosePatchBinding;
import com.ewt45.patchapp.thread.BuildApk;
import com.ewt45.patchapp.thread.DecodeApk;
import com.ewt45.patchapp.thread.Func;
import com.ewt45.patchapp.thread.FuncCursor;
import com.ewt45.patchapp.thread.FuncFAB;
import com.ewt45.patchapp.thread.FuncResl;
import com.ewt45.patchapp.thread.FuncSInput;
import com.ewt45.patchapp.thread.FuncSelObb;
import com.ewt45.patchapp.thread.SignApk;
import com.ewt45.patchapp.thread.SignalDone;
import com.ewt45.patchapp.widget.SelectApkDialog;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

//import brut.androlib.ApkDecoder;
//import brut.apktool.Main;


public class FragmentChoosePatch extends Fragment {
    String TAG = "FragmentChoosePatch";
    private FragmentChoosePatchBinding binding;
    // Request code for selecting a apk.
    private static final int PICK_APK_FILE = 2;
    //    ExecutorService singleThreadExecutor; //用于处理多线程同步
    ActionPool mActionPool; //用于替代单线程池，并处理返回结果显示
    SpannableStringBuilder spanInfo;
    List<FuncWithCheckBox> funcList = new ArrayList<>(); //记录功能和对应勾选框的列表

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChoosePatchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置外部文件绝对路径
        PatchUtils.setExternalFilesDir(requireContext().getExternalFilesDir(null).getAbsolutePath());
        //初始化textSwitcher
//        binding.textSwitcher.setFactory(() -> new TextView(requireContext()));
//        binding.textSwitcher.setInAnimation(requireContext(), android.R.anim.slide_in_left);
//        binding.textSwitcher.setOutAnimation(requireContext(), android.R.anim.slide_out_right);
//        binding.textSwitcher.setText("测试switcher");
        spanInfo = new SpannableStringBuilder(getString(R.string.tv_logtitle));
        binding.tvInfolist.setText(spanInfo);
        //初始化线程池
        mActionPool = new ActionPool(new ActionPool.DoneCallback() {

            @Override
            public void restoreViewVis() {
                binding.getRoot().post(() -> changeView(CHECK_ENABLE));
            }

            @Override
            public void setMessageStart(int resId) {
                if (resId == R.string.actmsg_signaldone)
                    return;
                binding.getRoot().post(() -> {
                    spanInfo.append("\n").append(getString(resId)).append(getString(R.string.actmsg_start));
                    binding.tvInfolist.setText(spanInfo);
                });
            }

            @Override
            public void setMessageFinish(int resId) {
                binding.getRoot().post(() -> {
                    String msg = getString(resId) + getString(R.string.actmsg_start);
                    int index = spanInfo.toString().lastIndexOf(msg);
                    if (index == -1) {
                        spanInfo.append("\n").append(getString(resId)).append(getString(R.string.actmsg_finish));
                    } else {
                        spanInfo.insert(index + msg.length(), getString(R.string.actmsg_finish));
                    }
                    binding.tvInfolist.setText(spanInfo);
                });
            }

            @Override
            public void setMessageFail(int resId, Exception e) {
                binding.getRoot().post(() -> {
                    String msg = getString(resId) + getString(R.string.actmsg_start);
                    int index = spanInfo.toString().lastIndexOf(msg);
                    if (index == -1) {
                        spanInfo.append("\n").append(getString(resId)).append(getString(R.string.actmsg_fail)).append(e.getLocalizedMessage());
                    } else {
                        spanInfo.insert(index + msg.length(), getString(R.string.actmsg_fail) + (e.getLocalizedMessage()));
                    }
                    binding.tvInfolist.setText(spanInfo);
                });
            }
        });

        //初始化功能和对应勾选框的列表。在changeview之前初始化
        funcList.add(new FuncWithCheckBox(binding.checkFab, new FuncFAB()));
        funcList.add(new FuncWithCheckBox(binding.checkCursor, new FuncCursor()));
        funcList.add(new FuncWithCheckBox(binding.checkResl, new FuncResl()));
        funcList.add(new FuncWithCheckBox(binding.checkInput, new FuncSInput()));
        funcList.add(new FuncWithCheckBox(binding.checkSelobb, new FuncSelObb()));

//        //先初始化包名(调用的时候自己去检查然后初始化吧）
//        SmaliFile pkgSmali = new SmaliFile();
//        PatchUtils.setPackageName(pkgSmali.findSmali(null, "EDMainActivity").getmCls());
//        pkgSmali.close();
        //禁用或显示功能按钮
        File tmpOutDir = new File(PatchUtils.getPatchTmpDir(), "tmp");
        changeView(PatchUtils.getPatchTmpApk().exists() && tmpOutDir.exists() ? CHECK_ENABLE : CHECK_DISABLE);

        binding.btnSelectApkInstalled.setOnClickListener(v -> {
//            List<ApplicationInfo> apps = requireContext().getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
//            List<String> filterApps = new ArrayList<>();
//            List<String> appNames = new ArrayList<>();
//            for (ApplicationInfo info : apps) {
//                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
//                    //非系统应用
//                    filterApps.add(info.sourceDir);
//                    appNames.add(requireContext().getPackageManager().getApplicationLabel(info).toString());
//                }
//            }
//            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//                final List<String> mFilterApps = filterApps;
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Log.d(TAG, "onClick: dialog点击的项目为：" + which);
//                    if (which >= 0) {
//                        deployEDApk(new File(mFilterApps.get(which)));
//                    }
//                }
//            };
//            new AlertDialog.Builder(requireContext())
//                    .setItems(appNames.toArray(new String[0]), listener)
//                    .setNegativeButton("取消", null)
//                    .create().show();
            new SelectApkDialog()
                    .setCallback(file -> binding.getRoot().post(()-> deployEDApk(file)))
                    .show(getChildFragmentManager(),null);
        });
        binding.btnSelectApkFiles.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.android.package-archive");//仅显示apk类型
            startActivityForResult(intent, PICK_APK_FILE);
            Log.d(TAG, "onViewCreated: startActivityForResult跳转到文件选择界面之后函数是立刻返回吗");//是的
        });

        binding.btnStartPatch.setOnClickListener(v -> {
            //解包自己的代码的apk（最好优化一下，仅当必要的时候才解包）
            try {
                //将自己的apk拷贝出来
                File patcherApk = new File(PatchUtils.getPatchTmpDir(), "patcher.apk");
                //先发现已经有就跳过解包吧。之后单独抽出方法，加个判断好更新
                if (!patcherApk.exists()) {
                    InputStream is = requireContext().getAssets().open("patcher.apk");
                    FileOutputStream fos = new FileOutputStream(patcherApk);
                    IOUtils.copy(is, fos);
                    fos.close();
                    is.close();
                    mActionPool.submit(new DecodeApk(DecodeApk.PATCHER));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean funcModified = false;
            //如果勾选（启用说明是用户自己勾选的）添加功能
            for(FuncWithCheckBox fnc :funcList){
                if(fnc.checkBox.isChecked() && fnc.checkBox.isEnabled()){
                    mActionPool.submit(fnc.func);
                    funcModified = true;
                }
            }
//
//            if (binding.checkFab.isChecked() && binding.checkFab.isEnabled()) {
//                mActionPool.submit(new FuncFAB());//添加功能
//                funcModified = true;
//            }
//            if (binding.checkCursor.isChecked() && binding.checkCursor.isEnabled()) {
//                mActionPool.submit(new FuncCursor());
//                funcModified = true;
//            }
//            if (binding.checkResl.isChecked() && binding.checkResl.isEnabled()) {
//                mActionPool.submit(new FuncResl());
//                funcModified = true;
//            }
//            if (binding.checkInput.isChecked() && binding.checkInput.isEnabled()) {
//                mActionPool.submit(new FuncSInput());
//                funcModified = true;
//            }
            changeView(CHECK_DISABLE_WAITING); //操作过程先禁用按钮(草这个要放到判断isEnable下面不然就全是disable了）
            if (funcModified) {
                mActionPool.submit(new BuildApk());//回编译apk
                mActionPool.submit(new SignApk(requireContext().getAssets()));//签名
            }
            mActionPool.submit(new SignalDone()); //恢复按钮可用
        });

        binding.btnInstallNew.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File apk = new File(PatchUtils.getPatchTmpDir().getAbsolutePath() + "/tmp/dist/tmp_sign.apk");
            Uri uri = FileProvider.getUriForFile(requireContext(), "com.ewt45.patchapp.fileprovider", apk);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取选中的，想要修改apk
        if (requestCode == PICK_APK_FILE && data != null) {
            deployEDApk(data.getData());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 将apk复制到自己目录后，进行解包（复制一个是file一个是uri，就不在这里操作了吧）(emmm试试uri.parse)
     */
    private void deployEDApk(Uri uri) {
        try {
            PatchUtils.copyToExternalFiles(requireContext(), uri);
            decodeApk();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deployEDApk(File file) {
        File tmpApk = PatchUtils.getPatchTmpApk();
        try {
            FileUtils.copyFile(file, tmpApk);
            decodeApk();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void decodeApk() {
        //解包exagear的apk
        Log.d(TAG, "decodeApk: 已复制ed apk到自己目录下，开始解包");
        changeView(CHECK_DISABLE_WAITING);
        mActionPool.submit(new DecodeApk(DecodeApk.EXAGEAR));
        mActionPool.submit(new SignalDone());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActionPool = null;
    }

    int CHECK_DISABLE = 0b1; //不允许点击按钮，但没有任务正在执行
    int CHECK_ENABLE = 0b10; //没有任务正在执行，有ex apk，允许点击按钮
    int CHECK_DISABLE_WAITING = 0b101; //不允许点击按钮，因为有任务正在执行，需要显示旋转进度条

    /**
     * 更新视图。
     * 1. 刚进入fragment且没有apk，禁用多选框和添加按钮
     * 2. 刚进入fragment有apk或选择了一个apk之后，根据情况勾选已有的功能（并禁用）
     * 3. 点击添加功能按钮后，禁用全部多选框和添加按钮
     * 4. 当线程池全部任务执行完后，再开放多选框和添加按钮（相当于2）
     * <p>
     * 如果需要在线程池全部做完之后再调用该函数，传入线程SignalDone，然后actionPool会通过回调函数调用此函数
     *
     * @param flag 禁用或启用
     */
    private void changeView(int flag) {


        //禁用全部勾选框和添加按钮
        if (flag == CHECK_DISABLE || flag == CHECK_DISABLE_WAITING) {
            for(FuncWithCheckBox fnc :funcList){
                fnc.checkBox.setEnabled(false);
            }
            binding.btnStartPatch.setEnabled(false);
            binding.btnInstallNew.setEnabled(false);
            binding.progressbar.setVisibility((flag & CHECK_DISABLE_WAITING) == CHECK_DISABLE_WAITING ? View.VISIBLE : View.GONE);
            binding.btnSelectApkInstalled.setEnabled(flag != CHECK_DISABLE_WAITING);
            binding.btnSelectApkFiles.setEnabled(flag != CHECK_DISABLE_WAITING);
        }
        //根据实际情况开放勾选框
        else {
            for(FuncWithCheckBox fnc :funcList){
                boolean added = fnc.func.funcAdded();
                fnc.checkBox.setChecked(added);
                fnc.checkBox.setEnabled(!added);
            }

            binding.btnStartPatch.setEnabled(true);
            binding.btnInstallNew.setEnabled(true);
            binding.progressbar.setVisibility(View.GONE);
            binding.btnSelectApkInstalled.setEnabled(true);
            binding.btnSelectApkFiles.setEnabled(true);
        }

    }

    static class FuncWithCheckBox{
        public Func func;
        public CheckBox checkBox;
        public FuncWithCheckBox(CheckBox checkBox,Func func){
            this.func=func;
            this.checkBox=checkBox;
        }
    }
}