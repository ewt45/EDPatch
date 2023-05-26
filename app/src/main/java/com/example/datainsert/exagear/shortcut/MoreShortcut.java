package com.example.datainsert.exagear.shortcut;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.configuration.startup.StartupActionsCollection;
import com.eltechs.ed.EDApplicationState;
import com.eltechs.ed.XDGLink;
import com.eltechs.ed.activities.EDStartupActivity;
import com.eltechs.ed.guestContainers.GuestContainer;
import com.eltechs.ed.guestContainers.GuestContainersManager;
import com.eltechs.ed.startupActions.StartGuest;
import com.eltechs.ed.startupActions.WDesktop;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MoreShortcut {
    private static final int VERSION_FOR_EDPATCH = 1;
    private static final String TAG = "MoreShortcut";
    private static final String DESKTOP_FILE_ABSOLUTE_PATH = "desktop_file_absolute_path";
    private static final String CONTAINER_ID = "container_id";
    private static final String SHOULD_SHOW_TIP = "should_show_tip";

    /**
     * 插入位置：com.eltechs.ed.fragmentsChooseXDGLinkFragment.XDGNodeAdapter.onBindViewHolder popupMenu.show();之前 (ChooseXDGLinkFragment$XDGNodeAdapter$2.onClick())
     * 进入安卓视图桌面板块后，为快捷方式的菜单项添加一些内容。
     *
     * @param isStartMenu 目前先只考虑桌面的吧，开始菜单不管？（而且现在版本都没开始菜单了）
     * @param popupMenu   （需要显示的弹窗菜单）
     * @param xdgLink     待保存的快捷方式
     */
    public static void addOptionsToMenu(boolean isStartMenu, PopupMenu popupMenu, XDGLink xdgLink) {
        if (isStartMenu)
            return;

        //给菜单项设置了单独的监听器后，只会调用自己的监听器，不会调用统一设置的监听器
        popupMenu.getMenu().add(RR.getS(RR.shortcut_menuItem_addAppSc)).setOnMenuItemClickListener(item -> {
            Log.d(TAG, "onMenuItemClick: 会调用自己的监听器嘛？");


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                //调起初始acitivity的intent，并添加额外参数

                Intent intent = new Intent(Globals.getAppContext(), EDStartupActivity.class);//Globals.getAppContext(),EDStartupActivity.class
                //extra好像要带包名前缀
                intent.setAction(Intent.ACTION_MAIN);
                intent.putExtra(DESKTOP_FILE_ABSOLUTE_PATH, xdgLink.linkFile.getAbsolutePath());
                intent.putExtra(CONTAINER_ID, xdgLink.guestCont.mId);

                PersistableBundle persistableBundle = new PersistableBundle();
                persistableBundle.putString(DESKTOP_FILE_ABSOLUTE_PATH, xdgLink.linkFile.getAbsolutePath());
                persistableBundle.putLong(CONTAINER_ID, xdgLink.guestCont.mId);

                //构建shortcutinfo，设置intent
                ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(Globals.getAppContext(), xdgLink.name)
                        .setShortLabel(xdgLink.name)
                        .setExtras(persistableBundle)
                        .setIntent(intent) //设置intent又不一定非要指向目标activity，那难道会加到栈中？如果不指定

                        .setActivity(new ComponentName(Globals.getAppContext().getPackageName(), Globals.getAppContext().getPackageName() + ".activities.EDStartupActivity")) //设置目标activity
                        .build();

                //使用旧版shortcutmanager，设置动态快捷方式
                ShortcutManager shortcutManager = Globals.getAppContext().getSystemService(ShortcutManager.class);
                List<ShortcutInfo> shortcutInfoList = shortcutManager.getDynamicShortcuts();
                shortcutInfoList.add(shortcutInfo);
                //动态+静态快捷方式上限好像是4个，再添加会失败了 shortcutManager.getMaxShortcutCountPerActivity()返回的是15
                if (shortcutInfoList.size() > 4)
                    shortcutInfoList.remove(0);

                shortcutManager.setDynamicShortcuts(shortcutInfoList);
            }


            //                ShortcutManagerCompat.pushDynamicShortcut(context, shortcut);
            if (QH.isTesting() || QH.getPreference().getBoolean(SHOULD_SHOW_TIP, true))
                showDialogHint();

            return true;
        });
//            popupMenu.getMenu().add("编辑");


    }


    /**
     * 插入位置：com.eltechs.ed.activities.EDStartupActivity.initialiseStartupActions()结尾，删掉 startupActionsCollection.addAction(new WDesktop<>());
     * 应用启动后，EDStartupActivity中，最后一个action根据情况，选择添加WDesktop还是直接StartGuest启动游戏
     *
     * @param a activity
     */
    public static void launchFromShortCutOrNormally(AppCompatActivity a) {

        updateCurrentShortcuts(a);
        //获取路径，然后新建xdglink
        String shortcutPath = a.getIntent().getStringExtra(DESKTOP_FILE_ABSOLUTE_PATH);
        //如果不是从快捷方式启动，正常进入WDesktop
        if (shortcutPath == null) {
            startNormally(a);
            return;
        }
        GuestContainer container = GuestContainersManager.getInstance(a).getContainerById(a.getIntent().getLongExtra(CONTAINER_ID, 0));
        File desktopFile = new File(shortcutPath);

        //如果快捷方式不存在了，就正常启动
        if (container == null || !desktopFile.exists()) {
//            Toast.makeText(a, "快捷方式已失效", Toast.LENGTH_LONG).show();
            //TO-DO: 删除这个快捷方式

            startNormally(a);
            return;
        }

        try {
            XDGLink xdgLink = new XDGLink(container, desktopFile);
            ((EDApplicationState) Globals.getApplicationState()).getStartupActionsCollection().addAction(new StartGuest<>(new StartGuest.RunXDGLink(xdgLink)));
            Log.d(TAG, "launchFromShortCut: 从快捷方式入口直接进入xserver");
        } catch (IOException e) {
            Log.e(TAG, "launchFromShortCut: ", e);
            startNormally(a);
        }


    }

    /**
     * 正常流程启动，即进入安卓应用主界面
     */
    private static void startNormally(AppCompatActivity a) {
        EDApplicationState eDApplicationState = Globals.getApplicationState();
        StartupActionsCollection<EDApplicationState> startupActionsCollection = eDApplicationState.getStartupActionsCollection();
        startupActionsCollection.addAction(new WDesktop<>());
    }

    /**
     * 检查现有的快捷方式，删除已经失效的
     */
    private static void updateCurrentShortcuts(AppCompatActivity a) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N_MR1) {
            Log.d(TAG, "updateCurrentShortcuts: 低于安卓7，无法使用快捷方式功能");
            return;
        }
        ShortcutManager shortcutManager = Globals.getAppContext().getSystemService(ShortcutManager.class);
        List<ShortcutInfo> list = shortcutManager.getDynamicShortcuts();
        for (int i = 0; i < list.size(); i++) {
            //应用还无法查看shortcutinfo里的intent，那只能另加一个extra了
            if (!new File(list.get(i).getExtras().getString(DESKTOP_FILE_ABSOLUTE_PATH)).exists()) {
                list.remove(i);
                i--;
            }
        }
        shortcutManager.setDynamicShortcuts(list);
    }

    private static void showDialogHint() {
        AppCompatActivity a = ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity();
        TextView textView = new TextView(a);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setLineSpacing(0, 1.5f);
        textView.setText(RR.getS(RR.shortcut_TipAfterAdd));

        CheckBox checkBox = new CheckBox(a);
        checkBox.setText(RR.getS(RR.shortcut_DontShowUp));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> QH.getPreference().edit().putBoolean(SHOULD_SHOW_TIP, !isChecked).apply());
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(-2, -2);
        checkParams.topMargin = 20;

        LinearLayout linearLayout = new LinearLayout(a);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = QH.px(a, RR.attr.dialogPaddingDp);
        linearLayout.setPadding(padding, padding, padding, padding);
        linearLayout.addView(textView);
        linearLayout.addView(checkBox, checkParams);
        ScrollView scrollView = new ScrollView(a);
        scrollView.addView(linearLayout);
        new AlertDialog.Builder(a).setView(scrollView).setPositiveButton(android.R.string.yes,null).create().show();
    }
}
