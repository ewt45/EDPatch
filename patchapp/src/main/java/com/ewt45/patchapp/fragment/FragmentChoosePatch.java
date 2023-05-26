package com.ewt45.patchapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ewt45.patchapp.ActionPool;
import com.ewt45.patchapp.MyApplication;
import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.databinding.FragmentChoosePatchBinding;
import com.ewt45.patchapp.thread.BuildApk;
import com.ewt45.patchapp.thread.DecodeApk;
import com.ewt45.patchapp.thread.Func;
import com.ewt45.patchapp.thread.FuncCursor;
import com.ewt45.patchapp.thread.FuncFAB;
import com.ewt45.patchapp.thread.FuncResl;
import com.ewt45.patchapp.thread.FuncSInput;
import com.ewt45.patchapp.thread.FuncSelObb;
import com.ewt45.patchapp.thread.FuncShortcut;
import com.ewt45.patchapp.thread.SignApk;
import com.ewt45.patchapp.thread.SignalDone;
import com.ewt45.patchapp.thread.WriteFuncVer;
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

//import brut.androlib.ApkDecoder;
//import brut.apktool.Main;


public class FragmentChoosePatch extends Fragment {
    // Request code for selecting a apk.
    private static final int PICK_APK_FILE = 2;
    String TAG = "FragmentChoosePatch";
    //    ExecutorService singleThreadExecutor; //用于处理多线程同步
    ActionPool mActionPool; //用于替代单线程池，并处理返回结果显示
    SpannableStringBuilder spanInfo;
    List<FuncWithCheckBox> funcList; //记录功能和对应勾选框的列表
    int CHECK_DISABLE = 0b1; //不允许点击按钮，但没有任务正在执行
    int CHECK_ENABLE = 0b10; //没有任务正在执行，有ex apk，允许点击按钮
    int CHECK_DISABLE_WAITING = 0b101; //不允许点击按钮，因为有任务正在执行，需要显示旋转进度条
    private FragmentChoosePatchBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentChoosePatchBinding.inflate(inflater, container, false);

        //初始化功能和对应勾选框的列表。在changeview之前初始化 (这一段还不能加到onViewCreated里，从另一个fragment返回到这里的时候那个函数会重新进入，导致重复添加fuc）
        funcList= new ArrayList<>();
        funcList.add(new FuncWithCheckBox(binding.checkFab, new FuncFAB()));
        funcList.add(new FuncWithCheckBox(binding.checkCursor, new FuncCursor()));
        funcList.add(new FuncWithCheckBox(binding.checkResl, new FuncResl()));
        funcList.add(new FuncWithCheckBox(binding.checkInput, new FuncSInput()));
        funcList.add(new FuncWithCheckBox(binding.checkSelobb, new FuncSelObb()));
        funcList.add(new FuncWithCheckBox(binding.checkShortcut,new FuncShortcut()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //提醒先看指南
        Snackbar snackCheckGuide = Snackbar.make(binding.getRoot(), R.string.tips_check_guide, Snackbar.LENGTH_INDEFINITE);
        snackCheckGuide.setAction(android.R.string.yes, v -> snackCheckGuide.dismiss()).setActionTextColor(getResources().getColor(R.color.purple_200));
        snackCheckGuide.show();

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
                Snackbar snackBackup = Snackbar.make(binding.getRoot(), R.string.tips_backup, Snackbar.LENGTH_INDEFINITE);
                snackBackup.setAction(android.R.string.yes, v -> snackBackup.dismiss()).setActionTextColor(getResources().getColor(R.color.purple_200));
                snackBackup.show();
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
                    StringBuilder builder = new StringBuilder(getString(R.string.actmsg_fail));
                    //添加报错的消息和栈记录
                    builder.append('\n').append(e.getLocalizedMessage());
                    for (StackTraceElement element : e.getStackTrace())
                        builder.append('\n').append(element.toString());
                    Throwable errorCause = e.getCause();
                    //添加cause的信息和栈信息
                    while (errorCause != null) {
                        builder.append('\n').append("Caused by: ").append(errorCause.getLocalizedMessage());
                        for (StackTraceElement element : errorCause.getStackTrace())
                            builder.append('\n').append(element.toString());
                        errorCause = errorCause.getCause();
                    }
                    builder.append('\n');

                    String msg = getString(resId) + getString(R.string.actmsg_start);
                    int index = spanInfo.toString().lastIndexOf(msg);
                    if (index == -1) {
                        spanInfo.append("\n").append(getString(resId)).append(builder.toString());
                    } else {
                        spanInfo.insert(index + msg.length(), builder.toString());
                    }


                    binding.tvInfolist.setText(spanInfo);
                });
            }
        });



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
                    .setCallback(file -> binding.getRoot().post(() -> deployEDApk(file)))
                    .show(getChildFragmentManager(), null);
        });
        binding.btnSelectApkFiles.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.android.package-archive");//仅显示apk类型
            startActivityForResult(intent, PICK_APK_FILE);
            Log.d(TAG, "onViewCreated: startActivityForResult跳转到文件选择界面之后函数是立刻返回吗");//是的
        });

        binding.btnStartPatch.setOnClickListener(v -> {
            //提醒用户等待
            Snackbar snackWaiting = Snackbar.make(binding.getRoot(), R.string.tips_waiting, Snackbar.LENGTH_INDEFINITE);
            snackWaiting.setAction(android.R.string.yes, snackbutton -> snackWaiting.dismiss()).setActionTextColor(getResources().getColor(R.color.purple_200));
            snackWaiting.show();
            //解包自己的代码的apk（最好优化一下，仅当必要的时候才解包）
            try {
                //将自己的apk拷贝出来
                File patcherApk = new File(PatchUtils.getPatchTmpDir(), "patcher.apk");
                //先发现已经有就跳过解包吧。之后单独抽出方法，加个判断好更新
                if (!patcherApk.exists()) {
                    InputStream is = requireContext().getAssets().open("patcher/release/patcher.apk");
                    FileOutputStream fos = new FileOutputStream(patcherApk);
                    IOUtils.copy(is, fos);
                    fos.close();
                    is.close();
                    mActionPool.submit(new DecodeApk(DecodeApk.PATCHER));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            Map<Func, Integer> addingFuncList = new HashMap<>();
            boolean patchNew = false;
            //如果勾选（启用说明是用户自己勾选的）添加功能
            for (FuncWithCheckBox fnc : funcList) {
                if (fnc.checkBox.isChecked() && fnc.checkBox.isEnabled()) {
                    patchNew = true;
                    mActionPool.submit(fnc.func);
                }
                //所有功能都添加版本号，如果勾选了那么肯定要升到最新，没勾选可能是没添加也可能是老版本，然后func查一下就行了
                // 因为没有卸载补丁功能，所以不会出现从勾选到取消勾选（已安装功能但是修改后的apk没有该功能）的情况
                addingFuncList.put(fnc.func, fnc.checkBox.isChecked() ? fnc.func.getLatestVersion() : fnc.func.getInstalledVersion());
            }

            changeView(CHECK_DISABLE_WAITING); //操作过程先禁用按钮(草这个要放到判断isEnable下面不然就全是disable了）
            if (patchNew) {
//                mActionPool.submit(new WriteFuncVer(addingFuncList));//添加功能时要获取旧的版本号，所以等功能添加完了，再写入版本号（获取和写入都是写到解包apk的asset里，打包的时候会自动加进去）（还是不用这种方法了，手动改的没法识别）
                mActionPool.submit(new BuildApk());//回编译apk
                boolean useDefaultKey = requireActivity().getSharedPreferences(MyApplication.PREFERENCE,Context.MODE_PRIVATE).getBoolean("use_default_signature",true);
                mActionPool.submit(new SignApk(requireContext().getAssets(),useDefaultKey));//签名
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

    /**
     * 选择apk后，进行解包
     */
    private void decodeApk() {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.tips_backup, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(android.R.string.yes, v -> snackbar.dismiss()).setActionTextColor(getResources().getColor(R.color.purple_200));
        snackbar.show();

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
            for (FuncWithCheckBox fnc : funcList) {
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
            for (FuncWithCheckBox fnc : funcList) {
                int version = fnc.func.getInstalledVersion();
                Log.d(TAG, String.format("changeView: 功能%s, 已安装版本 %d，最新版本 %d", fnc.func.getClass().getSimpleName(), version, fnc.func.getLatestVersion()));
                // 如果已安装功能版本号和最新的相等则自动勾选，否则不勾选
                boolean added = version == fnc.func.getLatestVersion();
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

    static class FuncWithCheckBox {
        public Func func;
        public CheckBox checkBox;

        public FuncWithCheckBox(CheckBox checkBox, Func func) {
            this.func = func;
            this.checkBox = checkBox;
        }
    }
}