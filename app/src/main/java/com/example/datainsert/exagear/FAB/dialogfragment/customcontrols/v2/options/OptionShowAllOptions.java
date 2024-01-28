package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.options;

import android.app.AlertDialog;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupMenu;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;

/**
 * 显示全部可用的选项
 */
public class OptionShowAllOptions extends AbstractOption {
    @Override
    public void run() {
        Log.d("TAG", "run: 显示全部选项");
        View view = Const.touchAreaViewRef.get();
        view.post(()->{
            new AlertDialog.Builder(Const.activityRef.get())
                    .setMessage("这里应该显示全部可用选项")
                    .show();
        });
        //为啥popupMenu不显示呢，是anchor有问题吗
//        PopupMenu popupMenu = new PopupMenu(Const.activityRef.get(), view,Gravity.LEFT|Gravity.TOP);
//        popupMenu.getMenu().add("测试");
//        popupMenu.getMenu().add("这里应该显示全部可用选项");
//        popupMenu.show();
    }

}
