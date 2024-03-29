package com.example.datainsert.exagear.FAB.dialogfragment;

import static android.widget.LinearLayout.SHOW_DIVIDER_BEGINNING;
import static android.widget.LinearLayout.SHOW_DIVIDER_MIDDLE;

import static com.example.datainsert.exagear.RR.getSArr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eltechs.axs.Globals;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * 目前问题：设置不保存
 */
public class VirglOverlay extends BaseFragment {
    public static final String T = "VirGL Overlay";
    //#define FL_GLX (1<<1)
    public static final int FL_GLES = (1 << 2);
    //#define FL_OVERLAY (1<<3)
    public static final int FL_MULTITHREAD = (1 << 4);
    public int overlay_position_var = 0, restart_var = 0, protocol_version = 0;
    public int dxtn_decompress = 0;

    /**
     * edpatch无法修改xml，所以只能是在原本已经存在旧VIRGL overlay的情况下显示。检查：libvirgl-lib.so，com.mittorn.virgloverlay包
     */
    public static boolean isAlreadyExist(Context c) {
        if(QH.isTesting())
            return true;

        if(!new File(c.getApplicationInfo().nativeLibraryDir, "libvirgl-lib.so").exists()
        ||!QH.classExist("com.mittorn.virgloverlay.process.p1") )
            return false;

        try {
            PackageInfo pkgInfo  = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_SERVICES);
            if(pkgInfo==null || pkgInfo.services==null)
                return false;

            for(ServiceInfo serInfo:pkgInfo.services)
                if ("com.mittorn.virgloverlay.process.p1".equals(serInfo.name))
                   return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected ViewGroup buildUI() {
        Context a = requireContext();
        Log.d("", "buildUI: 初始化overlay服务的选项");
        EditText socket_path = new EditText(a);
        CheckBox protocol_version_box = new CheckBox(a);
        CheckBox use_gles = new CheckBox(a);
        CheckBox use_threads = new CheckBox(a);
        CheckBox dxtn_decompress_box = new CheckBox(a);
        CheckBox restart_box = new CheckBox(a);

        RadioGroup radioGroup = new RadioGroup(a);
        RadioButton overlay_topleft = new RadioButton(a);
        RadioButton overlay_centered = new RadioButton(a);
        RadioButton overlay_hide = new RadioButton(a);

        //overlay布局的最外层
        LinearLayout root = new LinearLayout(a);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setShowDividers(SHOW_DIVIDER_BEGINNING | SHOW_DIVIDER_MIDDLE); //显示分割线
        root.setDividerDrawable(new ColorDrawable(Color.BLACK));
//        int padding = AndroidHelpers.dpToPx(8);
//        root.setPadding(padding, padding, padding, padding);//padding 5dp,底部10dp
        LinearLayout.LayoutParams topMarginParams = new LinearLayout.LayoutParams(-1, -2);
        int topMargin = QH.px(a, 8);
        topMarginParams.setMargins(0, topMargin, 0, topMargin);

        //多选项
        String[] checkStrs = getSArr(RR.fabVO_checkTitles);
        protocol_version_box.setText(checkStrs[0]);
        use_gles.setText(checkStrs[1]);
        use_threads.setText(checkStrs[2]);
        dxtn_decompress_box.setText(checkStrs[3]);
        restart_box.setText(checkStrs[4]);

        LinearLayout checkBoxRoot = new LinearLayout(a);
        checkBoxRoot.setOrientation(LinearLayout.VERTICAL);
        addSomeViews(checkBoxRoot, null, protocol_version_box, use_gles, use_threads, dxtn_decompress_box, restart_box);

        //单选组（覆盖位置）
        String[] radioStrs = getSArr(RR.fabVO_radioTitles);
        radioGroup.setOrientation(RadioGroup.HORIZONTAL);
        overlay_topleft.setText(radioStrs[1]);
        overlay_centered.setText(radioStrs[2]);
        overlay_hide.setText(radioStrs[3]);
        addSomeViews(radioGroup, null, overlay_topleft, overlay_centered, overlay_hide);

        TextView overlayPosTextView = new TextView(a);
        overlayPosTextView.setText(radioStrs[0]);
        TextView overlayExplainTextView = new TextView(a);
        overlayExplainTextView.setText(radioStrs[4]);

        LinearLayout overlayRoot = new LinearLayout(a);
        overlayRoot.setOrientation(LinearLayout.VERTICAL);
        addSomeViews(overlayRoot, null, overlayPosTextView, radioGroup, overlayExplainTextView);

        //socket path
        LinearLayout socketPathRootView = new LinearLayout(a);
        TextView socketPathText = new TextView(a);
        socketPathText.setText("Socekt path (/tmp/.virgl_test):");
        addSomeViews(socketPathRootView, null, socketPathText, socket_path);

        //按钮
        String[] btnStrs = getSArr(RR.fabVO_btnTitles);
        Button clearButton = new Button(a);
        Button startButton = new Button(a);
        clearButton.setText(btnStrs[0]);
        clearButton.setAllCaps(false);
        startButton.setText(btnStrs[1]);
        startButton.setAllCaps(false);

        //如果没有悬浮窗权限，则只显示一个按钮跳转到设置界面。否则显示正常选项
        Button btnRequestOverlay = new Button(a);
        btnRequestOverlay.setText(btnStrs[2]);
        btnRequestOverlay.setAllCaps(false);
        btnRequestOverlay.setOnClickListener(v -> {
            dismiss();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                a.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + a.getPackageName())));
        });

        LinearLayout buttonRoot = new LinearLayout(a);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 0.5f;
        buttonRoot.addView(clearButton, params);
        buttonRoot.addView(startButton, params);

        //其他说明
        TextView thanksText = new TextView(a);
        thanksText.setText("Original APK with native library by mittorn: https://github.com/mittorn/virglrenderer-android; https://github.com/mittorn/virglrenderer\n\nRebuild APk v0.0.9 (mod) by alexvorxx\nThe following code was used in this version:\nvirglrenderer-0.10.0: https://github.com/freedesktop/virglrenderer\nlibepoxy-1.5.10: https://github.com/anholt/libepoxy\ngl4es-1.1.4: https://github.com/ptitSeb/gl4es\n\nSpecial thanks to mittorn for help and advices.\n\nThanks to gabreek for the useful option\nhttps://github.com/gabreek/Virgl-Overlay-Rebuild_mod");
        thanksText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        thanksText.setTextIsSelectable(true);
//        QH.setTextViewExpandable(thanksText);

        //添加到根布局
        addSomeViews(root, topMarginParams, checkBoxRoot, overlayRoot, socketPathRootView, buttonRoot, thanksText);

        //设置按钮监听
        clearButton.setOnClickListener(v -> {
            restart_var = restart_box.isChecked() ? 1 : 0;
            if (restart_var == 1)
                writeStop(1);
            for (int i = 1; i < 32; i++) {
                try {
                    a.stopService(new Intent().setClassName(a, "com.mittorn.virgloverlay.process.p" + i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d(T, "All services cleaned! resart_var为" + restart_var + "; 若为1则应调用writeStop(0)");
            if (restart_var == 1)
                writeStop(0);

        });
        startButton.setOnClickListener(v -> {
            boolean successful = true;
            try {
                overlay_position_var = overlay_topleft.isChecked() ? 0 : (overlay_centered.isChecked() ? 1 : (overlay_hide.isChecked() ? 2 : 0));
                restart_var = restart_box.isChecked() ? 1 : 0;
                protocol_version = protocol_version_box.isChecked() ? 1 : 0;
                dxtn_decompress = dxtn_decompress_box.isChecked() ? 1 : 0;
                try (FileWriter fileWriter = new FileWriter(a.getFilesDir().getPath() + "/settings2");) {
                    fileWriter.write(String.format(Locale.ROOT, "%d %d %d %d", overlay_position_var, restart_var, protocol_version, dxtn_decompress));
                }

                int flags = (use_gles.isChecked() ? FL_GLES : 0) | (use_threads.isChecked() ? FL_MULTITHREAD : 0);
                try (FileWriter fileWriter = new FileWriter(a.getFilesDir().getPath() + "/settings");) {
                    fileWriter.write(String.format(Locale.ROOT, "%d %s", flags, socket_path.getText().toString()));
                }

                if (restart_var == 1)
                    writeStop(0);
                a.startService(new Intent().setClassName(a, "com.mittorn.virgloverlay.process.p1"));
                Log.d(T, "Service p1 started!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(T, "Service p1 failed!");
                successful=false;
            }
            dismiss();
            String[] resultStrs = getSArr(RR.fabVO_startResults);
            Toast.makeText(v.getContext(), successful?resultStrs[0]:resultStrs[1],Toast.LENGTH_LONG).show();//服务启动成功。 启动失败
        });

        //原activity的onCreate
        int flags = 0;

        try (FileReader settings_reader = new FileReader(a.getFilesDir().getPath() + "/settings");
             BufferedReader reader = new BufferedReader(settings_reader)) {
            String[] parts = reader.readLine().split(" ");
            flags = Integer.parseInt(parts[0]);
            socket_path.setText(parts[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (FileReader settings_reader1 = new FileReader(a.getFilesDir().getPath() + "/settings2");
             BufferedReader reader1 = new BufferedReader(settings_reader1);) {
            String[] parts1 = reader1.readLine().split(" ");
            this.overlay_position_var = Integer.parseInt(parts1[0]);
            this.restart_var = Integer.parseInt(parts1[1]);
            this.protocol_version = Integer.parseInt(parts1[2]);
            this.dxtn_decompress = Integer.parseInt(parts1[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        use_gles.setChecked((flags & FL_GLES) != 0);
        use_threads.setChecked((flags & FL_MULTITHREAD) != 0);
        restart_box.setChecked(this.restart_var != 0);
        protocol_version_box.setChecked(this.protocol_version != 0);
        dxtn_decompress_box.setChecked(this.dxtn_decompress != 0);
        overlay_topleft.setChecked(this.overlay_position_var == 0);
        overlay_centered.setChecked(this.overlay_position_var == 1);
        overlay_hide.setChecked(overlay_position_var == 2);

        LinearLayout linearRootWrapper = new LinearLayout(a);
        linearRootWrapper.setOrientation(LinearLayout.VERTICAL);
        linearRootWrapper.addView(btnRequestOverlay, new ViewGroup.LayoutParams(-2, -2));
        linearRootWrapper.addView(root);

        boolean lackOverlay = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(a);
        btnRequestOverlay.setVisibility(lackOverlay ? View.VISIBLE : View.GONE);
        root.setVisibility(lackOverlay ? View.GONE : View.VISIBLE);

        return linearRootWrapper;
    }


    public void writeStop(int stop) {
        Context a = Globals.getAppContext();
        try (FileWriter writer3 = new FileWriter(a.getFilesDir().getPath() + "/stop");) {
            Log.d("", "writeStop: " + a.getFilesDir().getPath() + "/stop 写入值为" + stop);
            writer3.write(String.valueOf(stop));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 向一个父布局中添加多个子布局
     *
     * @param parent 父布局
     * @param sons   子布局
     */
    private void addSomeViews(ViewGroup parent, ViewGroup.LayoutParams params, View... sons) {
        for (View v : sons) {
            if (params == null)
                parent.addView(v);
            else
                parent.addView(v, params);
        }
    }


    @Override
    public void callWhenFirstStart(AppCompatActivity activity) {
        //删除旧OverlayBuildUI插入的视图
        try {
            LinearLayout frame =  activity.findViewById(RR.id.ed_main_content_frame());
            FrameLayout container = activity.findViewById(RR.id.ed_main_fragment_container());
            if(frame==null || container==null)
                return;
            if(frame.indexOfChild(container)==2)
                frame.removeViewAt(1);
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
        return "Virgl Overlay";
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
