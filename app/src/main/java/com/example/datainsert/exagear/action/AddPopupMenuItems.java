package com.example.datainsert.exagear.action;

import static com.eltechs.axs.Globals.getAppContext;
import static com.eltechs.axs.Globals.getApplicationState;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.ActivityManagerCompat;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.Log;
import android.widget.PopupMenu;

import com.eltechs.axs.Globals;
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

import org.apache.commons.io.output.StringBuilderWriter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
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

                try {
                    File procDir = new File("/proc");
                    String[] names = procDir.list((dir, name) -> name.matches("[0-9]+"));
                    showingList.add("以下是通过/proc目录获取的进程");
                    if(names!=null)
                        for(String name:names)
                            showingList.add(name+" "+getProcessName(Integer.parseInt(name)));
                }catch (Exception e){
                    e.printStackTrace();
                }


                new AlertDialog.Builder(QH.getCurrentActivity())
                        .setItems(showingList.toArray(new String[0]), (dialog, which) -> {
                            int pid = Integer.parseInt(showingList.get(which).split(" ",2)[0]);
                            try {
//                                new ProcessBuilder("echo","this_is_an_echo_"+pid+" > 0")
//                                        .directory(new File("/proc/"+pid+"/fd"))
//                                        .start();
                                String workDir = "/proc/"+pid+"/fd";

//                                //这样会报错找不到 0
//                                Process process0 = exec("ln","-snf","/sdcard/aaa.txt",workDir+"/0");
//                                Log.d("TAG", "能把标准输入链接到一个文本里吗 "+getExecOutput(process0));

                                FileOutputStream fos = new FileOutputStream("/sdcard/aaa.txt");
//                                Os.dup2(fos.getFD(),0);//好像不行啊，这个0是当前进程的0

                                //看看标准输出链接到哪了（dev/null）
                                Process process1 = exec("ls","-la","/proc/"+pid+"/fd/0");
                                Log.d("TAG", "simple.sh的标准输入连到哪去了 "+getExecOutput(process1));

                                Process process4 = exec("ls","-la","/proc/"+pid+"/fd/1");
                                Log.d("TAG", "simple.sh的标准输出呢 "+getExecOutput(process4));

                                Process process2 =exec("echo","this_is_an_echo_"+pid,">",workDir+"/0");
                                Log.d("TAG", "echo xxx > 0 :"+getExecOutput(process2));

                                fos.write("write_something_can_be_received_?".getBytes());
                                Thread.sleep(500);
                                fos.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
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

    @SuppressLint("NewApi")
    private static Process exec(String... cmds) throws IOException {
        return new ProcessBuilder(cmds)
                .redirectErrorStream(true)
//                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start();
    }

    private static String getExecOutput(Process process){
        try (InputStream is = process.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is));
        ){
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine())!=null)
                builder.append(line).append("\n");
           return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
