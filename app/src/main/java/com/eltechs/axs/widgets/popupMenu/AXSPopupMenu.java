package com.eltechs.axs.widgets.popupMenu;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import com.eltechs.axs.helpers.AndroidFeatureTests;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.widgets.actions.Action;
import com.eltechs.axs.widgets.actions.ActionGroup;
import com.example.datainsert.exagear.action.AddPopupMenuItems;
import com.example.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class AXSPopupMenu {
    private final PopupMenu impl;
    private final List<MenuItemWrapper> menuItems = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    private interface MenuItemWrapper {
        void addItemsToMenu(Menu menu, int i);
    }

    public AXSPopupMenu(Activity activity, View view) {
        this.impl = new PopupMenu(activity, view);
    }

    public AXSPopupMenu(Activity activity, View view, int i) {
        if (AndroidFeatureTests.haveAndroidApi(AndroidFeatureTests.ApiLevel.ANDROID_4_4)) {
            this.impl = new PopupMenu(activity, view, i);
        } else {
            this.impl = new PopupMenu(activity, view);
        }
    }

    public void add(Action action) {
        this.menuItems.add(new ActionWrapper(action));
    }

    public void add(List<? extends Action> list) {
        for (Action action : list) {
            add(action);
        }
    }

    public void add(ActionGroup actionGroup) {
        this.menuItems.add(new ActionGroupWrapper(actionGroup));
    }

    public void remove(Action action) {
        Iterator<MenuItemWrapper> it = this.menuItems.iterator();
        while (it.hasNext()) {
            MenuItemWrapper next = it.next();
            if ((next instanceof ActionWrapper) && ((ActionWrapper) next).action == action) {
                it.remove();
                return;
            }
        }
        Assert.isTrue(false, String.format("Action %s is not a member of menu %s.", action, this));
    }

    public void remove(ActionGroup actionGroup) {
        Iterator<MenuItemWrapper> it = this.menuItems.iterator();
        while (it.hasNext()) {
            MenuItemWrapper next = it.next();
            if ((next instanceof ActionGroupWrapper) && ((ActionGroupWrapper) next).actionGroup == actionGroup) {
                it.remove();
                return;
            }
        }
        Assert.isTrue(false, String.format("Action %s is not a member of menu %s.", actionGroup, this));
    }

    public void show() {
        Menu menu = this.impl.getMenu();
        menu.clear();
        int i = 0;
        for (MenuItemWrapper menuItemWrapper : this.menuItems) {
            menuItemWrapper.addItemsToMenu(menu, i);
            i++;
        }

        //大部分操作模式（除了自定义）都用的是这个类来显示弹窗菜单。那么在每次显示前都添加一个选项好了
        AddPopupMenuItems.addBeforeShow(this);
//        test.add_popupmenu(menu);

        this.impl.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    private class ActionWrapper implements MenuItemWrapper {
        private final Action action;

        ActionWrapper(Action action) {
            this.action = action;
        }

        @Override // com.eltechs.axs.widgets.popupMenu.AXSPopupMenu.MenuItemWrapper
        public void addItemsToMenu(Menu menu, int i) {
            MenuItem add = menu.add(i, 0, 0, this.action.getName());
            add.setEnabled(this.action.isEnabled());
            add.setCheckable(this.action.isCheckable());
            add.setChecked(this.action.isChecked());
            add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: com.eltechs.axs.widgets.popupMenu.AXSPopupMenu.ActionWrapper.1
                @Override // android.view.MenuItem.OnMenuItemClickListener
                public boolean onMenuItemClick(MenuItem menuItem) {
                    ActionWrapper.this.action.run();
                    return true;
                }
            });
        }
    }

    /* loaded from: classes.dex */
    private class ActionGroupWrapper implements MenuItemWrapper {
        private final ActionGroup actionGroup;

        ActionGroupWrapper(ActionGroup actionGroup) {
            this.actionGroup = actionGroup;
        }

        @Override // com.eltechs.axs.widgets.popupMenu.AXSPopupMenu.MenuItemWrapper
        public void addItemsToMenu(Menu menu, int i) {
            int i2 = 0;
            for (final Action action : this.actionGroup.getMembers()) {
                MenuItem add = menu.add(i, i2, 0, action.getName());
                add.setEnabled(action.isEnabled());
                add.setChecked(action.isChecked());
                add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: com.eltechs.axs.widgets.popupMenu.AXSPopupMenu.ActionGroupWrapper.1
                    @Override // android.view.MenuItem.OnMenuItemClickListener
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        action.run();
                        return true;
                    }
                });
                i2 ++;
            }
            menu.setGroupCheckable(i, this.actionGroup.isCheckable(), this.actionGroup.isExclusive());
        }
    }
}