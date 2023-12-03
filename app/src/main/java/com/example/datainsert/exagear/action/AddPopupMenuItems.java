package com.example.datainsert.exagear.action;

import static com.eltechs.axs.Globals.getAppContext;
import static com.eltechs.axs.Globals.getApplicationState;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.os.Process;
import android.support.v4.app.ActivityManagerCompat;
import android.system.Os;
import android.text.TextUtils;
import android.widget.PopupMenu;

import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.environmentService.AXSEnvironment;
import com.eltechs.axs.environmentService.components.GuestApplicationsTrackerComponent;
import com.eltechs.axs.environmentService.components.XServerComponent;
import com.eltechs.axs.guestApplicationsTracker.GuestApplicationsTracker;
import com.eltechs.axs.guestApplicationsTracker.impl.GuestApplicationsCollection;
import com.eltechs.axs.guestApplicationsTracker.impl.ProcessHelpers;
import com.eltechs.axs.guestApplicationsTracker.impl.Translator;
import com.eltechs.axs.widgets.popupMenu.AXSPopupMenu;
import com.example.datainsert.exagear.FAB.dialogfragment.PulseAudio;
import com.example.datainsert.exagear.QH;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        popupMenu.getMenu().add("process").setOnMenuItemClickListener(item->{
            try {
                GuestApplicationsTrackerComponent component = ((EnvironmentAware) getApplicationState()).getEnvironment().getComponent(GuestApplicationsTrackerComponent.class);
                GuestApplicationsTracker tracker= (GuestApplicationsTracker) QH.reflectPrivateMember(GuestApplicationsTrackerComponent.class,component,"tracker");
                GuestApplicationsCollection collection = (GuestApplicationsCollection) QH.reflectPrivateMember(GuestApplicationsTracker.class,tracker,"guestApplicationsCollection");
                Collection<Translator> translators = (Collection<Translator>) QH.reflectPrivateMember(GuestApplicationsCollection.class,collection,"translators");

                List<String> showingList = new ArrayList<>();
                for(Translator translator:translators)
                    try{
                        showingList.add(translator.getPid()+" "+getProcessName(translator.getPid()));
                    }catch (Throwable throwable){
                        throwable.printStackTrace();
                    }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    ActivityManager manager = getAppContext().getSystemService(ActivityManager.class);
                    List<ActivityManager.RunningAppProcessInfo> procInfoList = manager.getRunningAppProcesses();
                    showingList.add("以下是通过activityManager获取的进程");
                    for(ActivityManager.RunningAppProcessInfo info:procInfoList)
                        showingList.add(info.pid+" "+info.processName+" ");
                }

                new AlertDialog.Builder(QH.getCurrentActivity())
                        .setItems(showingList.toArray(new String[0]), null)
                        .show();

//                QH.reflectInvokeMethod(android.os.Process.class,"readProcLines",null,)

            }catch (Throwable throwable){
                throwable.printStackTrace();
            }
            return true;
        });
    }

    private static String getProcessName(int pid) {
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/" + pid + "/status"));){
            return reader.readLine().trim();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
