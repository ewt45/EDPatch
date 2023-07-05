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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.AboutFab;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.FAB.dialogfragment.DriveD;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.CustomControls;
import com.example.datainsert.exagear.QH;

import java.util.ArrayList;
import java.util.List;

public class FabMenu {
    private static final String TAG = "FabMenu";

    //由多个版本号构成，每个占4位
    private static final int VERSION_FOR_EDPATCH =
            /*
            自定义d盘的版本号，如果这个为0说明整个fabmenu没有
            1：初版（旧版没写入版本号）
            2：初次安装后会自动创建Exagear文件夹
             */
            0x2
                    | 0x1 << 4 //自定义按键的版本号
//                    | 0x1 << 8 //pulseaudio
            ;


    @SuppressLint("RtlHardcoded")
    public FabMenu(AppCompatActivity a) {
        FloatingActionButton fab = new FloatingActionButton(a);
        //不知道为什么，下面设置了customSize，这里如果是wrap content 宽高都变成0
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(AndroidHelpers.dpToPx(60), AndroidHelpers.dpToPx(60));//AndroidHelpers.dpToPx(60),AndroidHelpers.dpToPx(60)
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.rightMargin = AndroidHelpers.dpToPx(40);
        fab.setTranslationY(-AndroidHelpers.dpToPx(100));//不知道为啥margin那里说应该是正数，那用translation吧
        fab.setElevation(100); //感觉高度舍不设置都无所谓
        fab.setCustomSize(AndroidHelpers.dpToPx(60)); //要用这个设置一遍否则图片不居中
        //设置图标
        try {
            Drawable iconDrawable = a.getDrawable(QH.rslvID(R.drawable.ic_settings_24dp, 0x7f0800aa));
            //设置icon颜色
            if (fab.getBackgroundTintList() != null) {
                int bgColor = fab.getBackgroundTintList().getDefaultColor() | 0xff000000;
                int icColor = ColorUtils.calculateMinimumAlpha(Color.BLACK, bgColor, 4.5f) == -1
                        ? Color.WHITE : Color.BLACK;
                assert iconDrawable != null;
                iconDrawable.setTintList(ColorStateList.valueOf(icColor));
                Log.d(TAG, "FabMenu: 背景色是？" + bgColor + " 选择设置图标颜色为：" + icColor);
            }
//            DrawableCompat.setTint();
            fab.setImageDrawable(iconDrawable);
        } catch (Exception ignored) {
        }

        //用于显示的功能对话框 这个class newInstance不会出问题吧
//        Class<? extends BaseFragment> clz = DriveD.class;
        final List<Class<? extends BaseFragment>> fragmentClsArray = new ArrayList<>(); //使用add的添加方式，便于在smali中删除
        fragmentClsArray.add(DriveD.class);
        fragmentClsArray.add(CustomControls.class);
        fragmentClsArray.add(AboutFab.class);
        //先调用一次初次启动需要执行的操作
        for (Class<? extends BaseFragment> clz : fragmentClsArray) {
            try {
                clz.newInstance().callWhenFirstStart(a);
                Log.d(TAG, "FabMenu: 启动应用，执行初始化操作" + clz);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        //点击时出现菜单
        fab.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            for (Class<? extends BaseFragment> clz : fragmentClsArray) {
                try {
                    BaseFragment fragment = clz.newInstance();
                    //点击菜单项时显示fragment，用title当tag了
                    menu.add(fragment.getTitle()).setOnMenuItemClickListener(item1 -> {
                        fragment.show(a.getSupportFragmentManager(), fragment.getTitle());
                        return true;
                    });
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        });
        //可以隐藏吧
        fab.setOnLongClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.getMenu().add("隐藏").setOnMenuItemClickListener(item -> {
                fab.hide();
                return true;
            });
            popupMenu.show();
            return true;
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
        return a.findViewById(QH.rslvID(R.id.ed_main_content_frame, 0x7f09006e));
    }

}
