package com.example.datainsert.exagear.controls.widget;

import android.content.Context;
import android.view.SubMenu;
import android.view.View;
import android.widget.PopupMenu;

import com.eltechs.axs.activities.menus.Quit;
import com.eltechs.axs.activities.menus.ToggleHorizontalStretch;
import com.eltechs.axs.widgets.actions.Action;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.menus.ControlEdit;
import com.example.datainsert.exagear.controls.menus.ControlToggleVisibility;
import com.example.datainsert.exagear.controls.menus.ControlUsage;
import com.example.datainsert.exagear.controls.menus.RotateScreen;
import com.example.datainsert.exagear.controls.menus.ShowKeyboardA11;

/**
 * 用于自定义操作模式的三指触屏弹出的弹窗菜单
 * 不知道为什么挂载到tscWidget上不显示。只好抄activity里，新建了一个textview挂载上去了
 */
public class SpecialPopupMenu extends PopupMenu {
    final Action[] mSubActions = new Action[]{
            new ControlEdit(),new ControlToggleVisibility(),new ControlUsage(),
    };
    final Action[] mActions = new Action[]{
            new ShowKeyboardA11(),
            new ToggleHorizontalStretch(),
            new RotateScreen(),
            new Quit()};

    public SpecialPopupMenu(Context context, View anchor) {
        super(context, anchor);
    }


    @Override
    public void show() {
        SubMenu subMenu = getMenu().addSubMenu(RR.getS(RR.cmCtrl_actionSubCtrl)  );

        for(Action action:mSubActions){
            subMenu.add(action.getName()).setOnMenuItemClickListener(item->{
                action.run();
                return true;
            });
        }

        for(Action action:mActions){
            getMenu().add(action.getName()).setOnMenuItemClickListener(item->{
                action.run();
                return true;
            });
        }

        super.show();
    }
}
