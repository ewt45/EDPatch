package com.example.datainsert.exagear.action;

import android.widget.PopupMenu;

import com.eltechs.axs.widgets.popupMenu.AXSPopupMenu;
import com.example.datainsert.exagear.FAB.dialogfragment.PulseAudio;
import com.example.datainsert.exagear.QH;

/**
 * 为启动容器后，多指点击弹出的弹窗菜单添加一些选项
 */
public class AddPopupMenuItems {
    /**
     * 为启动容器后，多指点击弹出的弹窗菜单添加一些选项
     */
    public static void addBeforeShow(AXSPopupMenu axsPopupMenu){
        PopupMenu popupMenu = (PopupMenu) QH.reflectPrivateMember(AXSPopupMenu.class,axsPopupMenu,"impl");
        popupMenu.getMenu().add("pulseaudio").setOnMenuItemClickListener(item->{
            new PulseAudio().show(QH.getCurrentActivity().getSupportFragmentManager(),"pulseaudio");
            return true;
        });
    }
}
