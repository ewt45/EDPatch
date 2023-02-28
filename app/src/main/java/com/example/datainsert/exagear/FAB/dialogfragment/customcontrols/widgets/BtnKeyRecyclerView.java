package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

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

import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.Arrays;

public class BtnKeyRecyclerView extends RecyclerView {
    BtnKeyAdapter mAdapter ;
    public BtnKeyRecyclerView(@NonNull Context context, OneKey[] keys) {
        super(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(layoutManager);

        mAdapter = new BtnKeyAdapter();
        setAdapter(mAdapter);
        mAdapter.submitList(Arrays.asList(keys));
        //设置拖拽排序
        new ItemTouchHelper(new DragHelperCallback(UP | DOWN, 0)).attachToRecyclerView(this);
        //设置侧栏底色
        setBackgroundColor(getPreference().getInt(PREF_KEY_SIDEBAR_COLOR, Color.BLACK));
    }
    @NonNull
    @Override
    public BtnKeyAdapter getAdapter() {
        return mAdapter;
    }
}
