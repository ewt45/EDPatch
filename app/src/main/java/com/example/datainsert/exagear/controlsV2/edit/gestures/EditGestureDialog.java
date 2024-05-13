package com.example.datainsert.exagear.controlsV2.edit.gestures;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.datainsert.exagear.controlsV2.model.OneGestureArea;

public class EditGestureDialog extends AlertDialog {
    private int modeIdx = 0;
    protected EditGestureDialog(Context context) {
        super(context);
    }

    public static void show(Context c, OneGestureArea model) {
        new AlertDialog.Builder(c)
                .setCancelable(false)
                .setNegativeButton("关闭", null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context c = getContext();
        //根视图
        FrameLayout frameRoot = new FrameLayout(c);
        //操作选项
        String[] modeStrs = new String[]{"浏览模式", "编辑模式"};
        Button btnMode = new Button(c);
        btnMode.setText(modeStrs[modeIdx]);
        btnMode.setOnClickListener(v -> {
            modeIdx = (modeIdx+1) % modeStrs.length;
            btnMode.setText(modeStrs[modeIdx]);
        });


        //绘制视图
        setContentView(frameRoot);
    }

    @Override
    public void show() {
        super.show();
        changeWindowAttr();
    }

    private void changeWindowAttr() {
        Point point = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getSize(point);

        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.width = -1;
        attr.height = -1;
        attr.gravity = Gravity.CENTER;
        getWindow().setAttributes(attr);
        onWindowAttributesChanged(attr);
    }
}
