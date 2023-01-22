package com.example.datainsert.exagear.FAB;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.ewt45.exagearsupportv7.R;
import com.example.datainsert.exagear.FAB.dialogfragment.DriveD;
import com.example.datainsert.exagear.FAB.dialogfragment.VirGLOverlay;
import com.example.datainsert.exagear.RSIDHelper;
import com.example.datainsert.exagear.S;

public class FabMenu{
    private static final String TAG = "FabMenu";

    @SuppressLint("RtlHardcoded")
    public FabMenu(AppCompatActivity a) {
        FloatingActionButton fab = new FloatingActionButton(a);
        //不知道为什么，下面设置了customSize，这里如果是wrap content 宽高都变成0
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(AndroidHelpers.dpToPx(60), AndroidHelpers.dpToPx(60));//AndroidHelpers.dpToPx(60),AndroidHelpers.dpToPx(60)
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.rightMargin = AndroidHelpers.dpToPx(20);
        fab.setTranslationY(-AndroidHelpers.dpToPx(80));//不知道为啥margin那里说应该是正数，那用translation吧
        fab.setElevation(100); //感觉高度舍不设置都无所谓
        fab.setCustomSize(AndroidHelpers.dpToPx(60)); //要用这个设置一遍否则图片不居中
        //设置图标
        try {
            Drawable iconDrawable = a.getDrawable(RSIDHelper.rslvID(R.drawable.ic_menu_camera, 0x7f0800aa));
            //设置icon颜色
            if(fab.getBackgroundTintList()!=null){
                int bgColor = fab.getBackgroundTintList().getDefaultColor() | 0xff000000;

                int icColor = ColorUtils.calculateMinimumAlpha(Color.WHITE,bgColor,4.5f)==-1
                        ? Color.BLACK : Color.WHITE;
                assert iconDrawable != null;
                iconDrawable.setTintList(ColorStateList.valueOf(icColor));
                Log.d(TAG, "FabMenu: 背景色是？"+bgColor+" 选择设置图标颜色为："+icColor);
            }
//            DrawableCompat.setTint();
            fab.setImageDrawable(iconDrawable);
        } catch (Exception ignored) {
        }
        fab.setOnCreateContextMenuListener((menu, v, menuInfo) -> {

            MenuItem item = menu.add(S.get(S.DriveD_Title));
            item.setOnMenuItemClickListener(item1 -> {
                new DriveD().show(a.getSupportFragmentManager(), null);
                return true;
            });
//            menu.add("测试：vo").setOnMenuItemClickListener(item1->{
//                new VirGLOverlay().show(a.getSupportFragmentManager(),null);
//                return true;
//            });
        });
        fab.setOnClickListener(view -> fab.showContextMenu());
        //findViewById找到线性布局，添加fab和params

//        View view = a.findViewById(R.id.ed_main_content_frame);
        View view = getMainFrameView(a);
        Log.d("TAG", "FabMenu: 没找到ed_main_content_frame吗" + view);
        if (view instanceof LinearLayout) {
            LinearLayout edFrameLayout = (LinearLayout) view;
            edFrameLayout.addView(fab, params);
        }
    }


    public static View getMainFrameView(AppCompatActivity a) {
        return a.findViewById(RSIDHelper.rslvID(R.id.ed_main_content_frame, 0x7f09006e));
    }

}
