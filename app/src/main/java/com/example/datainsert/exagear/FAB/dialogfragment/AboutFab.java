package com.example.datainsert.exagear.FAB.dialogfragment;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.system.Os;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.FabMenu;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.ControlsFragment;
import com.example.datainsert.exagear.controlsV2.XServerViewHolderImpl;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.io.File;
import java.io.IOException;

/**
 * 关于。关于右下齿轮按钮的介绍。
 * 也可以用于一些首次安装应用后启动时的操作
 */
public class AboutFab extends BaseFragment {
    private static final String TAG = "AboutFab";

    /**
     * 首次安装应用后显示提示。默认false表示还没显示过提示。
     */
    private static final String PREF_FIRST_LAUNCH_INFO_SHOWN = "PREF_FIRST_LAUNCH_INFO_SHOWN";
    /**
     * 走到这里时，应用是否曾经启动过了。（虽然如果process被杀了再重建这个会变为false）
     */
    private static boolean appFirstLaunching = true;
    private static boolean logcatStarted = false;


//    static {
//        System.loadLibrary("some-helper");
//    }

    //    public native int startPulseaudio();
//    public native int setEnv(String s);
    boolean isHiddenOptionsShowing;

    @Override
    protected ViewGroup buildUI() {
        Context c = requireContext();
        LinearLayout linearLayout = new LinearLayout(c);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView tvInfo = new TextView(c);
        tvInfo.setLineSpacing(0, 1.2f);
        tvInfo.setText(Html.fromHtml(getS(RR.abtFab_info)));
        tvInfo.setClickable(true);
        tvInfo.setTextIsSelectable(true);
        LinearLayout.LayoutParams tvInfoParams = new LinearLayout.LayoutParams(-2, -2);
        tvInfoParams.topMargin = 20;

        linearLayout.addView(tvInfo, tvInfoParams);

        //显示一个可以点击的颜文字
        String fulltext = "( ˡ ᴗ ˡ ) ";//⩊ ᴗ

//            AnimationUtils.loadAnimation(requireContext(),R.anim.text_scale)
        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(150);
        scale.setInterpolator(requireContext(), android.R.anim.overshoot_interpolator);

        LinearLayout linearFun = new LinearLayout(c);
        linearFun.setPadding(40, 40, 40, 40);
        linearFun.setOrientation(LinearLayout.HORIZONTAL);
        View.OnClickListener aniListener = new View.OnClickListener() {
            private final int viewCount = fulltext.length() / 2;
            private int animIndex = 0;

            @Override
            public void onClick(View v) {
                //设置部分文字可见
                for (int i = 0; i < linearFun.getChildCount(); i++) {
                    linearFun.getChildAt(i).setVisibility(i <= animIndex ? View.VISIBLE : View.GONE);
                }
                //开始单个文字动画
                Log.d(TAG, "onClick: 第几个视图开始动画：" + animIndex + ", 当前点击视图为" + (String) v.getTag());
                linearFun.getChildAt(animIndex).startAnimation(scale);

                animIndex = (animIndex + 1) % viewCount;
                if (animIndex == 0)
                    showHiddenOptions(linearLayout);
            }
        };

        linearFun.setOnClickListener(aniListener);
        for (int i = 0; i + 2 <= fulltext.length(); i += 2) {
            TextView tv = new TextView(requireContext());
            tv.setText(fulltext.substring(i, i + 2));
//            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            tv.setTypeface(Typeface.MONOSPACE);
            tv.setTag(String.valueOf(i));
            tv.setOnClickListener(aniListener);
            linearFun.addView(tv);
        }

        linearLayout.addView(linearFun);

        if(QH.isTesting()){
        }

        return linearLayout;

//        ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity().registerActivityResultHandler((i, i2, intent) -> {
//            Uri intentUri = intent.getData();
//            DocumentFile documentFile = DocumentFile.fromTreeUri(requireContext(),intentUri);
//            String documentId = DocumentsContract.getDocumentId(documentFile.getUri());// 实际路径，如/data/data/com.termux/home
//            final String column = "_data";
//            final String[] projection = {column};
//            try(Cursor cursor = requireContext().getContentResolver().query(documentFile.getUri(), projection, null, null, null);) {
//                if (cursor != null && cursor.moveToFirst()) {
//                    final int index = cursor.getColumnIndexOrThrow(column);
//                    String dataColumn= cursor.getString(index);
//                    Log.d(TAG, "handleActivityResult: 获取到uri转为字符串="+dataColumn);
//                }
//            }
//            return true;
//        },123);

    }

    /**
     * 点击几次颜文字后显示隐藏选项
     */
    private void showHiddenOptions(LinearLayout linearRoot) {
        if (isHiddenOptionsShowing)
            return;
        Context c = requireContext();
        LinearLayout linearHidden = new LinearLayout(c);
        linearHidden.setOrientation(LinearLayout.VERTICAL);

        TextView tvWarn = new TextView(c);
        tvWarn.setText("请勿乱点，后果自负！\nDO NOT TOUCH ANYTHING UNLESS YOU KNOW WHAT YOU ARE DOING!");

        Button btnSymPriExt = new Button(c);
        btnSymPriExt.setText("/storage/emulated/0");
        btnSymPriExt.setOnClickListener(v -> createOrDelSymFile("/storage/emulated/0", "a_primary_storage"));

        Button btnSymOthExt = new Button(c);
        btnSymOthExt.setText("other storage device");
        btnSymOthExt.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(c, v);
            for (File filesDir : c.getExternalFilesDirs(null)) {
                if (filesDir.getAbsolutePath().startsWith("/storage/emulated/0"))
                    continue;
                try {
                    String extDevPath = filesDir.getAbsolutePath().replace("/Android/data/" + c.getPackageName() + "/files", "");
                    popupMenu.getMenu().add(extDevPath).setOnMenuItemClickListener(item -> {
                        createOrDelSymFile(item.getTitle().toString(), "a_" + item.getTitle().toString().replace("/", "_"));
                        return true;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (popupMenu.getMenu().size() == 0)
                popupMenu.getMenu().add("none").setEnabled(false);
            popupMenu.show();
        });

        Button btnOpenDocTree = new Button(c);
        btnOpenDocTree.setText("选择文件夹");
        btnOpenDocTree.setOnClickListener(v -> {
            // Choose a directory using the system's file picker.
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, 123);
        });

        linearHidden.addView(btnSymPriExt);
        linearHidden.addView(btnSymOthExt);
        linearHidden.addView(btnOpenDocTree);
//        linearRoot.addView(linearHidden);
        isHiddenOptionsShowing = true;
    }

    private void createOrDelSymFile(String androidPath, String symLinkDirName) {
        Context c = requireContext();

        try {
            File symFile = new File(c.getFilesDir(), "image/" + symLinkDirName);
            if (symFile.getCanonicalFile().exists()) {
                symFile.delete();
                Toast.makeText(c, "deleted", Toast.LENGTH_SHORT).show();
            } else {
                boolean isSuccess;
                try {
                    Os.symlink(androidPath, symFile.getAbsolutePath());
                    isSuccess = true;
                } catch (ErrnoException e) {
                    e.printStackTrace();
                    isSuccess = false;
                }
                Toast.makeText(c, "z:/" + symLinkDirName + " created, " + (isSuccess ? "successful" : "failed"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 首次安装后启动，显示snackbar提示用户使用右下角齿轮按钮
     */
    @Override
    public void callWhenFirstStart(AppCompatActivity activity) {
        boolean disableShowInfo = getPreference().getBoolean(PREF_FIRST_LAUNCH_INFO_SHOWN, false);
        if (!disableShowInfo) {
            getPreference().edit().putBoolean(PREF_FIRST_LAUNCH_INFO_SHOWN, true).apply();
            Snackbar snackbar = Snackbar.make(FabMenu.getMainFrameView(activity), getS(RR.firstLaunch_snack), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(android.R.string.yes, v -> snackbar.dismiss());
            snackbar.show();
        }

//        FileTreePrinter.test();

        //尝试重定向logcat到文件中：
        redirectLogcat();
    }


    /**
     * 将logat输出到本地文本中。
     * 要求：app为第一次启动（否则每次退出容器就被清空了） 并且 d盘对应目录存在名为logcat文件夹
     */
    private void redirectLogcat() {

        if (!(appFirstLaunching || !logcatStarted))
            return;
        appFirstLaunching = false;

        File logcatDir = new File(DriveD.getDriveDDir(), "logcat");
        File logcatFile = new File(logcatDir, "logcat.txt");
        if (!logcatDir.exists())
            return;
        if (logcatFile.exists() && !logcatFile.delete())
            return;
        try {
            System.out.println("callWhenFirstStart: 不执行吗？");
            Runtime.getRuntime().exec("logcat -f " + logcatFile.getAbsolutePath() + " *:V");// "*:S",
//            Process process = Runtime.getRuntime().exec(new String[]{"killall","-9","logat;","/system/bin/logat","-c;","/system/bin/logcat","-f",filePath });
            logcatStarted = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getTitle() {
        return getS(RR.abtFab_title);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
