package com.example.datainsert.exagear.FAB.dialogfragment;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.example.datainsert.exagear.RR.getS;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.example.datainsert.exagear.FAB.FabMenu;
import com.example.datainsert.exagear.FAB.widget.MyTextInputEditText;
import com.example.datainsert.exagear.RR;

import java.io.File;

public class DriveD_origin extends BaseFragment implements DialogInterface.OnClickListener {
    public final static String TAG = "DriveD";
    private static final String[] dstDirArrKeys = new String[]{"Exagear", "Download"};
    private static final String[] dstDirArrVals = new String[]{"Exagear", "Download"};
    private static final String[] parDirArrKeys = new String[]{getS(RR.DriveD_ParDirKey_1), getS(RR.DriveD_ParDirKey_2), getS(RR.DriveD_ParDirKey_3)};
    private static final ParDirEnum[] parDirArrVals = new ParDirEnum[]{ParDirEnum.ExternalStorage, ParDirEnum.ExternalFilesDir, ParDirEnum.SDCardExternalFilesDir};
    //sharedpreference里记录父路径和文件夹的key
    public static String PREF_KEY_PAR_IND = "PREF_KEY_PAR_IND"; //值为int
    public static String PREF_KEY_DST_NAME = "PREF_KEY_DST_IND"; //值为string
    public static String PREF_VAL_DST_NAME = "Exagear";//没有自定义的时候，默认的文件夹名,可以改这个默认的路径
    String[] testCheckStr = new String[]{getS(RR.DriveD_check_1), getS(RR.DriveD_check_2), getS(RR.DriveD_check_3), getS(RR.DriveD_check_4), getS(RR.DriveD_check_5)};
    //用于动态显示父目录和测试结果的textview
    private TextView tvTestResult;
    private MyTextInputEditText tvInputParDir;
    //用于编辑文件夹名的edittext
    private MyTextInputEditText tvInputDstDir;
    //当前设置的父目录和文件夹名称
    private int currentParDir; //考虑到持久化保存，还是用enum好一些，也许外部路径会有变化？
    private String currentDstDirName;

    /**
     * 根据parDirEnum生成对应文件路径字符串并返回
     *
     * @param parDirEnum enum
     * @return 文件字符串，
     */
    private static File dirEnumToFile(ParDirEnum parDirEnum) {
        File parDir = null;
        try {
            if (parDirEnum == ParDirEnum.ExternalFilesDir) {
                parDir = Globals.getAppContext().getExternalFilesDir(null);
            } else if (parDirEnum == ParDirEnum.ExternalStorage) {
                parDir = Environment.getExternalStorageDirectory();
            } else if (parDirEnum == ParDirEnum.SDCardExternalFilesDir) {
                parDir = Globals.getAppContext().getExternalFilesDirs(null)[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parDir;
    }

    /**
     * 在StartGuest中初始化d盘路径时调用，返回对应的file
     *
     * @return file
     */
    public static File getDriveDDir() {
        if (getPreference() == null)
            return null;
        File parFile = dirEnumToFile(parDirArrVals[getPreference().getInt(PREF_KEY_PAR_IND, 0)]);
        try {
            return new File(parFile, getPreference().getString(PREF_KEY_DST_NAME, PREF_VAL_DST_NAME));
//            return new File("/storage/BA73-022B");//825E-837B
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    protected ViewGroup buildUI() {
        Context c = requireContext();
        tvTestResult = getTextViewWithText(c, "");

        LinearLayout rootView = new LinearLayout(c);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.addView(getTextViewWithText(c, getS(RR.DriveD_Explain)));

        TextInputLayout textInputLayout = new TextInputLayout(c);
        textInputLayout.addView(setupETPar());
        rootView.addView(textInputLayout);

        TextInputLayout textInputLayout2 = new TextInputLayout(c);
        textInputLayout2.addView(setupETDst());
        rootView.addView(textInputLayout2);

        tvTestResult.setTextIsSelectable(true);
        rootView.addView(tvTestResult);


        rootView.addView(getOneLineWithTitle(c, getS(RR.DriveD_DescTitle), getTextViewWithText(c, getS(RR.DriveD_DescCont)), false));
        //初始化当前路径
        tvInputDstDir.setText(getPreference().getString(PREF_KEY_DST_NAME, PREF_VAL_DST_NAME));
        updateCurrentParDir(getPreference().getInt(PREF_KEY_PAR_IND, 0));
        return rootView;
    }

    /**
     * 当本dialog关闭时的操作
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == BUTTON_POSITIVE) {
            //将设置更新到preference。整个dialog应该只有此时向pref中写入数据
            if (checkCurrDirAvailable()
                    //如果当前路径可用，且设置与之前设置不一样了
                    && (currentParDir != getPreference().getInt(PREF_KEY_PAR_IND, 0)
                    || !currentDstDirName.equals(getPreference().getString(PREF_KEY_DST_NAME, PREF_VAL_DST_NAME))
            )

            ) {
                getPreference().edit().putInt(PREF_KEY_PAR_IND, currentParDir).putString(PREF_KEY_DST_NAME, currentDstDirName).apply();
//                Toast.makeText(requireActivity(), "设置已更新", Toast.LENGTH_SHORT).show();
                Snackbar.make(FabMenu.getMainFrameView((AppCompatActivity) requireActivity()), getS(RR.DriveD_SncBrTxt), Snackbar.LENGTH_LONG)
                        .setAction(getS(RR.DriveD_SncBrBtn), v -> android.os.Process.killProcess(android.os.Process.myPid()))
                        .show();
            } else {
                Snackbar.make(FabMenu.getMainFrameView((AppCompatActivity) requireActivity()), getS(RR.DriveD_ToastExitFail), Snackbar.LENGTH_LONG).show();
            }
        } else if (which == BUTTON_NEGATIVE) {
            Snackbar.make(FabMenu.getMainFrameView((AppCompatActivity) requireActivity()), getS(RR.DriveD_ToastExitFail), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * 设置文件夹名称的edittext
     */
    private MyTextInputEditText setupETDst() {
        tvInputDstDir = new MyTextInputEditText(requireContext(), dstDirArrKeys, dstDirArrVals, getS(RR.DriveD_EditDstTitle));
        tvInputDstDir.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//        InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;用于禁用语法检查
        tvInputDstDir.setSingleLine();
        tvInputDstDir.setImeOptions(EditorInfo.IME_ACTION_DONE);
        tvInputDstDir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCurrentDstDir(s.toString().trim());
            }
        });
        return tvInputDstDir;
    }

    /**
     * 更新文件夹名称请调用edittext.settext，然后会自动触发这个函数。
     * 输入的当前文件夹名称更新后，更新成员变量currentDstDir(不要在这里设置edittext文本显示，否则会循环调用）
     * 然后重新检查可用性并将结果显示到textview上
     *
     * @param s 新更新的文件夹名称
     */
    private void updateCurrentDstDir(String s) {
        currentDstDirName = s;
        checkCurrDirAvailable();
    }

    /**
     * 设置文件夹父目录的edittext
     */
    private MyTextInputEditText setupETPar() {
        tvInputParDir = new MyTextInputEditText(requireContext(), null, null, getS(RR.DriveD_EditParTitle));
        tvInputParDir.setInputType(InputType.TYPE_NULL);
        //设置弹窗菜单
        PopupMenu popupMenu = new PopupMenu(requireContext(), this.tvInputParDir);
        for (int i = 0; i < parDirArrKeys.length; i++) {
            popupMenu.getMenu().add(Menu.NONE, i, Menu.NONE, parDirArrKeys[i]).setOnMenuItemClickListener(item -> {
                //点击某个选项后更新成员变量和tv的文件夹父目录
                updateCurrentParDir(item.getItemId());
                return true;
            });
        }
//        tvInputParDir.setPopupMenu(popupMenu);
        return tvInputParDir;
    }

    /**
     * 更新父目录应调用此函数。
     * 输入的当前文件夹父目录更新后，更新成员变量currentParDir以及edittext显示
     * 然后重新检查可用性并将结果显示到textview上
     *
     * @param i 新更新的文件夹父目录
     */
    private void updateCurrentParDir(int i) {
        currentParDir = i;
        File file = dirEnumToFile(parDirArrVals[currentParDir]);
        this.tvInputParDir.setText(file == null ? getS(RR.DriveD_getPathFail) : file.getAbsolutePath());
        checkCurrDirAvailable();
    }

    /**
     * 检查当前设置的文件夹是否有效，并将检查结果显示到textview上
     *
     * @return 是否有效
     */
    private boolean checkCurrDirAvailable() {
        int result = 0;
        //先更新一下文件夹名（不允许文件夹名为空，如果为""就改成Download) （不更新了，最后点击确定的时候再说吧）
        //如果文件夹或父目录获取不到返回false。
        if (currentDstDirName == null || currentDstDirName.equals("") || dirEnumToFile(parDirArrVals[currentParDir]) == null) {
//            tvInputDstDir.setText(dstDirArrVals[0]);
            setTvTestResult(null, result);
            return false;
        }
        String str = null;
        try {
            File dstFile = new File(dirEnumToFile(parDirArrVals[currentParDir]), currentDstDirName);
            //用位记录吧.从低位到高位：父目录是否存在，文件夹是否存在，是否为文件夹，是否可读，是否可写
            result = 0b00000001
                    | (dstFile.exists() ? 1 : 0) << 1
                    | (dstFile.isDirectory() ? 1 : 0) << 2
                    | (dstFile.canRead() ? 1 : 0) << 3
                    | (dstFile.canWrite() ? 1 : 0) << 4;
        } catch (Exception e) {
            str = e.getLocalizedMessage();
        }
        setTvTestResult(str, result);
        return result == 0b00011111;
    }

    /**
     * 设置测试结果的文字到tv
     *
     * @param s         文字
     * @param checkFlag 类型，0为错误，1为成功，null为exception
     */
    private void setTvTestResult(String s, int checkFlag) {
        SpannableStringBuilder str = new SpannableStringBuilder("\n");
        //有报错，或者没权限，或者无法获取路径，不进行下一步测试，直接红色消息显示商。
        if (s != null) {
            str.append(s);
            str.setSpan(new ForegroundColorSpan(0xffF56C6C), str.length() - s.length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //检查是否有存储权限
        else if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            str.append(getS(RR.DriveD_NoStrgPmsn));
            str.setSpan(new ForegroundColorSpan(0xffF56C6C), str.length() - getS(RR.DriveD_NoStrgPmsn).length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", requireContext().getPackageName(), null));
            startActivity(intent);
        }
        //如果父路径或子路径为null
        else if (dirEnumToFile(parDirArrVals[currentParDir]) == null || currentDstDirName == null || "".equals(currentDstDirName)) {
            str.append(getS(RR.DriveD_getPathFail));
            str.setSpan(new ForegroundColorSpan(0xffF56C6C), str.length() - getS(RR.DriveD_getPathFail).length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //获取文件夹file，并检查是否可用
        else {
            File parFile = dirEnumToFile(parDirArrVals[currentParDir]);
            boolean stillGood = true; //判断当前是否已经循环到错误的地方。如果是的话下面都改成灰色
            String completePath = parFile.getAbsolutePath() + "/" + currentDstDirName;
            str.append(completePath);
            str.setSpan(new StyleSpan(Typeface.BOLD), str.length() - completePath.length(), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.append("\n\n");
            for (int i = 0; i < 5; i++) {
                str.append(testCheckStr[i]);
                if (stillGood) {
                    stillGood = ((checkFlag >> i) & 0b000000001) == 1;
                    str.append(stillGood ? " √" : " ×");
                    str.setSpan(new ForegroundColorSpan(stillGood ? 0xff67C23A : 0xffF56C6C), str.length() - 1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    str.setSpan(new StrikethroughSpan(), str.length() - testCheckStr[i].length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                str.append('\n');
            }

        }
        tvTestResult.setText(str);
    }

    /**
     * 初始化时，创建Exagear文件夹
     */
    @Override
    public void callWhenFirstStart(AppCompatActivity activity) {
        File file = new File(Environment.getExternalStorageDirectory(), PREF_VAL_DST_NAME);
        if (!file.exists()) {
            boolean b = file.mkdir();
            Log.d(TAG, "callWhenFirstStart: 初次安装后启动，尝试创建文件夹结果 " + b);
        }

    }

    @Override
    public String getTitle() {
        return getS(RR.DriveD_Title);
    }

    private enum ParDirEnum {
        ExternalStorage,
        ExternalFilesDir,
        SDCardExternalFilesDir
    }


//    /**
//     * 创建 “测试可用性”按钮
//     *
//     * @return 按钮
//     */
//    private Button getValidateBtn() {
//        Button btn = new Button(requireActivity());
//        btn.setText("测试可用性");
//        btn.setOnClickListener(v -> checkCurrDirAvailable());
//        return btn;
//    }
    //突然发现不是重启activity能解决的，放弃。让用户手动重启吧
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//        Log.d(TAG, "onDismiss: actvity类型是 "+requireActivity().getClass());
//        if(requireActivity() instanceof EDMainActivity){
//            EDMainActivity a = (EDMainActivity) requireActivity();
////            ((StartupActionsCollectionAware)Globals.getApplicationState()).getStartupActionsCollection().addAction(new EmptyAction());
////            a.signalUserInteractionFinished(WDesktop.UserRequestedAction.RESTART_ME);
//            Intent intent = new Intent();
//            intent.putExtra("AxsActivityResult", WDesktop.UserRequestedAction.RESTART_ME);
//            a.setResult(2, intent);
//            a.finish();
//        }else{
//            requireActivity().recreate();
//        }
//    }
//    private Button getBrowseBtn() {
//        Button btn = new Button(requireActivity());
//        btn.setText("浏览文件夹");
//        btn.setOnClickListener(v -> {
//            try {
//                File file = new File(dirEnumToFile(parDirArrVals[currentParDir]), tvInputDstDir.getText().toString().trim());
////                MediaScannerConnection.scanFile(requireContext(), new String[]{file.getAbsolutePath()}, null,
////                        new MediaScannerConnection.OnScanCompletedListener() {
////                            @Override
////                            public void onScanCompleted(String path, Uri uri) {
////                                Log.d(TAG, "onScanCompleted: 扫描完成，uri是啥样的" + uri + ", path是啥样的" + path);
////                                if (uri != null) {
////                                    Intent intent = new Intent(Intent.ACTION_VIEW);
////
//////                                  DocumentsContract.Document.MIME_TYPE_DIR
////                                    intent.setDataAndType(uri, DocumentsContract.Document.MIME_TYPE_DIR);
////                                    //如果没有处理隐式intent的应用，用startActivity会崩溃
////                                    if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
////                                        startActivity(intent);
////                                    }
////                                }
////                            }
////                        });
//
//                Intent intent = new Intent(Intent.ACTION_SEND);
////                                  DocumentsContract.Document.MIME_TYPE_DIR
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                Uri uri = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID+".provider",file);
//                Log.d(TAG, "getBrowseBtn: fileProvider获取的uri为"+uri);
//                intent.putExtra(Intent.EXTRA_STREAM, uri);
//                intent.setType(DocumentsContract.Document.MIME_TYPE_DIR);
////                                    intent.setDataAndType(Uri.parse("content://"+path), DocumentsContract.Document.MIME_TYPE_DIR);
//                //如果没有处理隐式intent的应用，用startActivity会崩溃
//                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
//                    startActivity(intent);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        });
//        return btn;
//    }

}
