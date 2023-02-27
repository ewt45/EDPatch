package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.datainsert.exagear.controls.model.OneCol;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.Collections;
import java.util.List;

public class DragHelperCallback extends ItemTouchHelper.SimpleCallback {
    String TAG = "DragHelperCallback";
    public DragHelperCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

//        if(!(viewHolder.getBindingAdapter() instanceof AddColArngAdapter &&
//                target.getBindingAdapter() instanceof  AddColArngAdapter)  ){
//            return false;
//        }
        if(recyclerView.getAdapter() instanceof BtnKeyAdapter){
            BtnKeyAdapter adapter = (BtnKeyAdapter) recyclerView.getAdapter();
            //获取列表，交换元素，提交新列表
            List<OneKey> newList = adapter.getCurrentList();
            Collections.swap(newList,viewHolder.getAdapterPosition(),target.getAdapterPosition());
            adapter.submitList(newList);
        }else if(recyclerView.getAdapter() instanceof BtnColAdapter){
            BtnColAdapter adapter = (BtnColAdapter) recyclerView.getAdapter();
            //获取列表，交换元素，提交新列表
            List<OneCol> newList = adapter.getCurrentList();
            Collections.swap(newList,viewHolder.getAdapterPosition(),target.getAdapterPosition());
            adapter.submitList(newList);
        }else{
            return false;
        }
//        ((ItemMoveCallBack) viewHolder.getBindingAdapter()).changeItemPos(viewHolder.getBindingAdapterPosition(),target.getBindingAdapterPosition());
//        RecyclerView.Adapter adapter =  viewHolder.getBindingAdapter();
//        adapter.changeItemPos(viewHolder.getBindingAdapterPosition(),target.getBindingAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if(viewHolder instanceof BtnKeyAdapter.ViewHolder){
           BtnKeyRecyclerView recyclerView = (BtnKeyRecyclerView) ((BtnKeyAdapter.ViewHolder) viewHolder).getmBtn().getParent();
           BtnKeyAdapter adapter = recyclerView.getAdapter();
            List<OneKey> newList = adapter.getCurrentList();
            newList.remove(viewHolder.getAdapterPosition());
            adapter.submitList(newList);
        }
    }

    @Override
    public float getMoveThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.5f;
    }
}