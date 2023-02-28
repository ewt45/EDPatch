package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.OneCol;

import java.util.List;

/**
 * 记得设置一个detachCallback，在关闭窗口时更新keycode2到最新
 */
public class BtnColRecyclerView extends RecyclerView {
    int nextId=0;//每个回收视图都单独维护一个nextId
    BtnColAdapter mAdapter ;
    public BtnColRecyclerView(@NonNull Context context, @NonNull KeyCodes2 keyCodes2, boolean isLeft) {
        super(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(HORIZONTAL);
        setLayoutManager(layoutManager);
        mAdapter = new BtnColAdapter(keyCodes2,isLeft);
        setAdapter(mAdapter);

//        OneKey[] keys = {new OneKey(1),new OneKey(2)};
//        List<OneCol> mylist = new ArrayList<>();
//        mylist.add(new OneCol(keys,getNewId()));
//        mylist.add(new OneCol(keys.clone(),getNewId()));
//        mAdapter.submitList(mylist);
        //设置拖拽排序
        new ItemTouchHelper(new DragHelperCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0)).attachToRecyclerView(this);

    }


    /**
     * 给每个新建的item分配一个id。每个回收视图都单独维护一个nextId
     * @return
     */
    public int getNewId(){
        this.nextId++;
        return nextId-1;
    }

    @NonNull
    @Override
    public BtnColAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 根据已有的按键，初始化布局
     * @param colList 从文件 反序列化得到的列表
     */
    public void initItemList(List<OneCol> colList){
        //先给每个OneCol重新分配id，并设置自己的nextid
        for(OneCol oneCol:colList){
            oneCol.setId(getNewId());
        }
        mAdapter.submitList(colList);

    }

}



