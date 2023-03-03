package com.example.datainsert.exagear.controls.interfaceOverlay.widget;

import android.content.Context;
import android.view.SubMenu;
import android.view.View;
import android.widget.PopupMenu;

import com.eltechs.axs.activities.menus.Quit;
import com.eltechs.axs.activities.menus.ShowKeyboard;
import com.eltechs.axs.activities.menus.ShowUsage;
import com.eltechs.axs.activities.menus.ToggleHorizontalStretch;
import com.eltechs.axs.widgets.actions.Action;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.menus.ControlEdit;
import com.example.datainsert.exagear.controls.menus.ControlToggleVisibility;
import com.example.datainsert.exagear.controls.menus.ControlUsage;

/**
 * 用于自定义操作模式的三指触屏弹出的弹窗菜单
 * 不知道为什么挂载到tscWidget上不显示。只好抄activity里，新建了一个textview挂载上去了
 */
public class SpecialPopupMenu extends PopupMenu {
    final Action[] mActions = new Action[]{
            new ControlEdit(),new ControlToggleVisibility(),new ControlUsage(),
            new ShowKeyboard(),
            new ToggleHorizontalStretch(),
            new Quit()};

    public SpecialPopupMenu(Context context, View anchor) {
        super(context, anchor);
    }


    @Override
    public void show() {
        SubMenu subMenu = getMenu().addSubMenu(RR.getS(RR.cmCtrl_actionSubCtrl)  );

        subMenu.add(mActions[0].getName()).setOnMenuItemClickListener(item -> {
            mActions[0].run();
            return true;
        });
        subMenu.add(mActions[1].getName()).setOnMenuItemClickListener(item->{
            mActions[1].run();
            return  true;
        });
        subMenu.add(mActions[2].getName()).setOnMenuItemClickListener(item->{
            mActions[2].run();
            return  true;
        });
        getMenu().add(mActions[3].getName()).setOnMenuItemClickListener(item->{
            mActions[3].run();
            return true;
        });

        getMenu().add(mActions[4].getName()).setOnMenuItemClickListener(item->{
            mActions[4].run();
            return true;
        });
        getMenu().add(mActions[5].getName()).setOnMenuItemClickListener(item->{
            mActions[5].run();
            return true;
        });
        super.show();
    }
}
