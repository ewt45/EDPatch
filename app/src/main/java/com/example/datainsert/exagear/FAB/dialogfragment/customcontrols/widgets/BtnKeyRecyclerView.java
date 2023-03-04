package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;
import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static android.support.v7.widget.helper.ItemTouchHelper.DOWN;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
import static android.support.v7.widget.helper.ItemTouchHelper.UP;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getPreference;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SIDEBAR_COLOR;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.Arrays;

public class BtnKeyRecyclerView extends RecyclerView {
    BtnKeyAdapter mAdapter;

    public BtnKeyRecyclerView(@NonNull Context context, OneKey[] keys, boolean isLandScape) {
        //根据isLandScape判断横向还是纵向，布局也跟着横或竖着摆放
        super(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(isLandScape ? HORIZONTAL : VERTICAL);
        setLayoutManager(layoutManager);

        mAdapter = new BtnKeyAdapter(isLandScape);
        setAdapter(mAdapter);
        mAdapter.submitList(Arrays.asList(keys));
        //设置拖拽排序
        new ItemTouchHelper(new DragHelperCallback(isLandScape ? LEFT | RIGHT : UP | DOWN, 0)).attachToRecyclerView(this);
        //设置侧栏底色
        setBackgroundColor(getPreference().getInt(PREF_KEY_SIDEBAR_COLOR, Color.BLACK));

        LinearLayout.LayoutParams recyclerViewParams = new LinearLayout.LayoutParams(isLandScape ? -1 : -2, isLandScape ? -2 : -1);
        recyclerViewParams.gravity = isLandScape ? Gravity.CENTER_VERTICAL : Gravity.CENTER_HORIZONTAL;
        setLayoutParams(recyclerViewParams);

    }

    @NonNull
    @Override
    public BtnKeyAdapter getAdapter() {
        return mAdapter;
    }
}
