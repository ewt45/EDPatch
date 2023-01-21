package com.example.datainsert.exagear.FAB.dialogfragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.ewt45.exagearsupportv7.R;
import com.example.datainsert.exagear.RSIDHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class VirGLOverlay extends BaseFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View root = buildUI();

        return new AlertDialog.Builder(requireContext())
                .setView(root)
                .create();
    }

    //以下是从OverlayBuildUI复制过来的

    public static final String T = "VirGL Overlay";
    //#define FL_GLX (1<<1)
    public static final int FL_GLES = (1 << 2);
    //#define FL_OVERLAY (1<<3)
    public static final int FL_MULTITHREAD = (1 << 4);
    public int overlay_position_var = 0, restart_var = 0, protocol_version = 0;
    public int dxtn_decompress = 0;

    //    private final AppCompatActivity a;
    //选项
    EditText socket_path;
    CheckBox protocol_version_box;
    CheckBox use_gles;
    CheckBox use_threads;
    CheckBox dxtn_decompress_box;
    CheckBox restart_box;
    RadioGroup radioGroup;
    RadioButton overlay_topleft;
    RadioButton overlay_centered;
    RadioButton overlay_hide;
    Button clearButton;
    Button startButton;

    /**
     * 添加vo布局到ed布局
     */
    @SuppressLint("SetTextI18n")
    private View buildUI() {
        AppCompatActivity a = (AppCompatActivity) requireActivity();
        Log.d("", "OverlayBuildUI: 初始化overlay服务的选项");
//        this.a=a;
        socket_path = new EditText(a);
        protocol_version_box = new CheckBox(a);
        use_gles = new CheckBox(a);
        use_threads = new CheckBox(a);
        dxtn_decompress_box = new CheckBox(a);
        restart_box = new CheckBox(a);

        radioGroup = new RadioGroup(a);
        overlay_topleft = new RadioButton(a);
        overlay_centered = new RadioButton(a);
        overlay_hide = new RadioButton(a);

        clearButton = new Button(a);
        startButton = new Button(a);


        //检查悬浮窗权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(a)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + a.getPackageName()));
            a.startActivity(intent);
        }

        //overlay布局的最外层
        LinearLayout root = new LinearLayout(a);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE); //显示分割线
        root.setDividerDrawable(new ColorDrawable(Color.BLACK));
//        int padding = AndroidHelpers.dpToPx(10);
//        root.setPadding(padding, padding, padding, 0 );//padding 5dp,底部10dp
//        root.setBackgroundColor(Color.parseColor("#16B5BAB9"));
//        root.setElevation(-3);

        //标题
        TextView viewTitle = new TextView(a);
        viewTitle.setText("VirGL Overlay 服务");
        viewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        viewTitle.setTextColor(Color.BLACK);
        int padding = AndroidHelpers.dpToPx(8);
        viewTitle.setPadding(padding, padding, padding, padding);

        //多选项
        protocol_version_box.setText("使用vtest协议2（需要Mesa 19.1.0 v3及以上）");
        use_gles.setText("使用GL ES 3.x而不是OpenGL");
        use_threads.setText("使用多线程egl访问");
        dxtn_decompress_box.setText("DXTn（S3纹理压缩）解压（一些游戏需要）");
        restart_box.setText("自动重启服务");

        LinearLayout checkBoxRoot = new LinearLayout(a);
        checkBoxRoot.setOrientation(LinearLayout.VERTICAL);
        checkBoxRoot.setPadding(padding, padding, padding, padding);
        addSomeViews(checkBoxRoot, protocol_version_box, use_gles, use_threads, dxtn_decompress_box, restart_box);

        //单选组（覆盖位置）
        radioGroup.setOrientation(RadioGroup.HORIZONTAL);
        overlay_topleft.setText("左上方");
        overlay_centered.setText("居中");
        overlay_hide.setText("隐藏*");
        addSomeViews(radioGroup, overlay_topleft, overlay_centered, overlay_hide);

        TextView overlayPosTextView = new TextView(a);
        overlayPosTextView.setText("覆盖位置：");
        TextView overlayExplainTextView = new TextView(a);
        overlayExplainTextView.setText("* 与VTEST_WIN=1一起使用以在X11窗口中绘制，而不是悬浮窗覆盖");

        LinearLayout overlayRoot = new LinearLayout(a);
        overlayRoot.setOrientation(LinearLayout.VERTICAL);
        overlayRoot.setPadding(padding, padding, padding, padding);
        addSomeViews(overlayRoot, overlayPosTextView, radioGroup, overlayExplainTextView);

        //socket path
        LinearLayout socketPathRootView = new LinearLayout(a);
        TextView socketPathText = new TextView(a);
        socketPathText.setText("Socekt path (/tmp/.virgl_test):");
        addSomeViews(socketPathRootView, socketPathText, socket_path);

        //按钮
        clearButton.setText("清除服务");
        startButton.setText("开始服务");

        LinearLayout buttonRoot = new LinearLayout(a);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 0.5f;
        buttonRoot.addView(clearButton, params);
        buttonRoot.addView(startButton, params);

        //其他说明
        TextView thanksText = new TextView(a);
        thanksText.setText("Original APK with native library by mittorn: https://github.com/mittorn/virglrenderer-android; https://github.com/mittorn/virglrenderer\n\nRebuild APk v0.0.9 (mod) by alexvorxx\nThe following code was used in this version:\nvirglrenderer-0.10.0: https://github.com/freedesktop/virglrenderer\nlibepoxy-1.5.10: https://github.com/anholt/libepoxy\ngl4es-1.1.4: https://github.com/ptitSeb/gl4es\nSpecial thanks to mittorn for help and advices. Thanks to gabreek for the useful option: https://github.com/gabreek/Virgl-Overlay-Rebuild_mod");
        thanksText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);

        //添加到根布局
        addSomeViews(root, viewTitle, checkBoxRoot, overlayRoot, socketPathRootView, buttonRoot, thanksText);

        //设置按钮监听
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickClean();
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStart();
            }
        });

        //原activity的onCreate
        int flags = 0;

        boolean z = true;
        try {
//            CharBuffer.allocate(128);
            FileReader settings_reader = new FileReader(a.getFilesDir().getPath() + "/settings");
            BufferedReader reader = new BufferedReader(settings_reader);
            String[] parts = reader.readLine().split(" ");
            flags = Integer.parseInt(parts[0]);
            try {
                socket_path.setText(parts[1]);
                reader.close();
                settings_reader.close();
            } catch (Exception e) {
                e.printStackTrace();
                reader.close();
                settings_reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileReader settings_reader1 = new FileReader(a.getFilesDir().getPath() + "/settings2");
            BufferedReader reader1 = new BufferedReader(settings_reader1);
            String[] parts1 = reader1.readLine().split(" ");
            this.overlay_position_var = Integer.parseInt(parts1[0]);
            this.restart_var = Integer.parseInt(parts1[1]);
            this.protocol_version = Integer.parseInt(parts1[2]);
            this.dxtn_decompress = Integer.parseInt(parts1[3]);
            reader1.close();
            settings_reader1.close();
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

//        //root加入scrollview
//        ScrollView scrollView = new ScrollView(a);
//        scrollView.setScrollbarFadingEnabled(false);
//        scrollView.setVerticalScrollBarEnabled(true);
//        scrollView.setScrollBarSize( AndroidHelpers.dpToPx(9)); //9dp
//        //高度固定130dp
//        ViewGroup.LayoutParams scrollParams =new LinearLayout.LayoutParams(-1, AndroidHelpers.dpToPx(130));//130dp
//        scrollView.setLayoutParams(scrollParams);
//        scrollView.addView(root);
//        //底部阴影（20dp）
//        TextView tv3 = new TextView(a);
//        RelativeLayout.LayoutParams tv3Params = new RelativeLayout.LayoutParams(-1, AndroidHelpers.dpToPx(20));
//        tv3Params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        tv3.setLayoutParams(tv3Params);
//        GradientDrawable gradientDrawable = new GradientDrawable();
//        gradientDrawable.setColors(new int[]{Color.parseColor("#19D5D5D5"),Color.parseColor("#DD122647")});
//        tv3.setBackground(gradientDrawable);
//        //包裹阴影和滚动视图的相对布局
//        RelativeLayout shadowWrapper = new RelativeLayout(a);
//        shadowWrapper.setLayoutParams(scrollParams); //借用一下scroll的布局参数，也就是宽高
//        shadowWrapper.addView(scrollView);
//        shadowWrapper.addView(tv3);


//        //添加ov布局到ed布局中(这里改到ex里的时候替换一下）
//        LinearLayout layout = a.findViewById(RSIDHelper.rslvID(R.id.ed_main_content_frame,0x7f09006e));
//        if(layout!=null)
//            layout.addView(shadowWrapper, 1);
//        else
//            Log.d("TAG", "buildUI: 没找到挂载的根视图，无法添加设置视图");

        ScrollView rootScrollView = new ScrollView(a);
        rootScrollView.setScrollbarFadingEnabled(false);
        rootScrollView.setVerticalScrollBarEnabled(true);
        rootScrollView.setPadding(40, 40, 40, 40);
        rootScrollView.addView(root);
        return rootScrollView;

    }


    /**
     * 点击清除按钮后
     */
    public void onClickClean() {
        Context a = Globals.getAppContext();
        if (restart_var == 1) {
            writeStop(1);
        }
        for (int i = 1; i < 32; i++) {
            try {
                a.stopService(new Intent().setClassName(a, "com.mittorn.virgloverlay.process.p" + i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(T, "All services cleaned! resart_var为" + restart_var + "; 若为1则应调用writeStop(0)");
        if (restart_var == 1) {
            writeStop(0);
        }
    }

    public void writeStop(int stop) {
        Context a = Globals.getAppContext();
        try {
            FileWriter writer3 = new FileWriter(a.getFilesDir().getPath() + "/stop");
            Log.d("", "writeStop: " + a.getFilesDir().getPath() + "/stop 写入值为" + stop);
            writer3.write(String.valueOf(stop));
            writer3.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击开始服务按钮后
     */
    public void onClickStart() {
        Context a = Globals.getAppContext();
        try {
            if (overlay_topleft.isChecked()) {
                overlay_position_var = 0;
            } else if (overlay_centered.isChecked()) {
                overlay_position_var = 1;
            } else if (overlay_hide.isChecked()) {
                overlay_position_var = 2;
            }
            if (restart_box.isChecked()) {
                restart_var = 1;
            } else {
                restart_var = 0;
            }
            if (protocol_version_box.isChecked()) {
                protocol_version = 1;
            } else {
                protocol_version = 0;
            }
            if (dxtn_decompress_box.isChecked()) {
                dxtn_decompress = 1;
            } else {
                dxtn_decompress = 0;
            }
            FileWriter fileWriter = new FileWriter(a.getFilesDir().getPath() + "/settings2");
            fileWriter.write(String.valueOf(overlay_position_var));
            fileWriter.write(' ');
            fileWriter.write(String.valueOf(restart_var));
            fileWriter.write(' ');
            fileWriter.write(String.valueOf(protocol_version));
            fileWriter.write(' ');
            fileWriter.write(String.valueOf(dxtn_decompress));
            fileWriter.close();

            int flags = 0;
            if (use_gles.isChecked())
                flags |= FL_GLES;
            if (use_threads.isChecked())
                flags |= FL_MULTITHREAD;

            FileWriter writer = new FileWriter(a.getFilesDir().getPath() + "/settings");
            writer.write(String.valueOf(flags));
            writer.write(' ');
            writer.write(socket_path.getText().toString());
            writer.close();
            if (restart_var == 1) {
                writeStop(0);
            }
            a.startService(new Intent().setClassName(a, "com.mittorn.virgloverlay.process.p1"));
            Log.d(T, "Service p1 started!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(T, "Service p1 failed!");
        }
    }


    /**
     * 向一个父布局中添加多个子布局
     *
     * @param parent 父布局
     * @param sons   子布局
     */
    private void addSomeViews(ViewGroup parent, View... sons) {
        for (View v : sons) {
            parent.addView(v);
        }
    }
}
