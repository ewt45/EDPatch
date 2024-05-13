package com.example.datainsert.exagear.controlsV2.edit.gestures;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.edit.EditConfigWindow;
import com.example.datainsert.exagear.controlsV2.model.OneGestureArea;

public class EditGestureRootView extends FrameLayout implements EditConfigWindow.RequestFullScreen {
    public EditGestureRootView(@NonNull Context c, OneGestureArea model) {
        super(c);

        //添加。一个添加按钮。创建界面可选状态还是操作
        Button btnAdd = new Button(c);
        btnAdd.setText("+");
        btnAdd.setOnClickListener(v -> {

        });



        LinearLayout linearToolbar = new LinearLayout(c);
        linearToolbar.setOrientation(LinearLayout.HORIZONTAL);
        linearToolbar.addView(btnAdd);

        //绘制视图
        EditGestureDrawLayout drawView = new EditGestureDrawLayout(c, model);
        HorizontalScrollView horizonView = new HorizontalScrollView(c);
        horizonView.addView(drawView);
//        NestedScrollView verticalView = new NestedScrollView(c);
//        verticalView.addView(horizonView);

        addView(horizonView);
    }

    @Override
    public boolean isApplyLimit() {
        return false;
    }
}
