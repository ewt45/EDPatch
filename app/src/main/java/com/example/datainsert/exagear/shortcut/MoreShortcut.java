package com.example.datainsert.exagear.shortcut;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.FileUriExposedException;
import android.os.PersistableBundle;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.ed.BuildConfig;
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
    /**
     * 1: 初次添加
     * 2: 支持图标（如果有）
     */
    private static final int VERSION_FOR_EDPATCH = 2;
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

                //设置图标，由于get到的信息会丢失图标，所以每次设置快捷方式前需要重新设置一遍
                setDynamicShortcuts(shortcutInfoList);

                shortcutManager.setDynamicShortcuts(shortcutInfoList);
            }


            //                ShortcutManagerCompat.pushDynamicShortcut(context, shortcut);
            if (QH.isTesting() || QH.getPreference().getBoolean(SHOULD_SHOW_TIP, true))
                showDialogHint();

            return true;
        });


//        popupMenu.getMenu().add("编辑").setOnMenuItemClickListener(item->{
//            Intent shareIntent = new Intent();
//            shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            Uri uri = FileProvider.getUriForFile(Globals.getAppContext(), BuildConfig.APPLICATION_ID.concat(".provider"), xdgLink.linkFile);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//            shareIntent.setType("text/plain");
//            Globals.getAppContext().startActivity(shareIntent);
//            return true;
//        });
    }


    /**
     * 插入位置：com.eltechs.ed.activities.EDStartupActivity.initialiseStartupActions()结尾，删掉 startupActionsCollection.addAction(new WDesktop<>());
     * 应用启动后，EDStartupActivity中，最后一个action根据情况，选择添加WDesktop还是直接StartGuest启动游戏
     *
     * @param a activity
     */
    public static void launchFromShortCutOrNormally(AppCompatActivity a) {
        setDynamicShortcuts(null);
        try {
            //获取路径，然后新建xdglink
            String shortcutPath = a.getIntent().getStringExtra(DESKTOP_FILE_ABSOLUTE_PATH);
            //如果不是从快捷方式启动，正常进入WDesktop
            if (shortcutPath == null)
                throw new Exception("正常启动app，进入WDesktop");

            GuestContainer container = GuestContainersManager.getInstance(a).getContainerById(a.getIntent().getLongExtra(CONTAINER_ID, 0));
            File desktopFile = new File(shortcutPath);
            //如果快捷方式不存在了，就正常启动
            if (container == null || !desktopFile.exists())
                throw new Exception("快捷方式不存在，正常启动");

            Log.d(TAG, "launchFromShortCut: 从快捷方式入口直接进入xserver");
            XDGLink xdgLink = new XDGLink(container, desktopFile);
            ((EDApplicationState) Globals.getApplicationState()).getStartupActionsCollection().addAction(new StartGuest<>(new StartGuest.RunXDGLink(xdgLink)));
        } catch (Exception e) {
            Log.d(TAG, "launchFromShortCut: 正常启动 " + e.getMessage());
            ((EDApplicationState) Globals.getApplicationState()).getStartupActionsCollection().addAction(new WDesktop<>());
        }


    }


    /**
     * 添加动态快捷方式。同时会检查就的快捷方式是否失效，失效则删除
     *
     * @param list 新增的动态快捷方式列表，为null的话则获取当前的并检查是否有应该删除的
     */
    private static void setDynamicShortcuts(List<ShortcutInfo> list) {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N_MR1) {
            Log.d(TAG, "updateCurrentShortcuts: 低于安卓7，无法使用快捷方式功能");
            return;
        }
        ShortcutManager shortcutManager = Globals.getAppContext().getSystemService(ShortcutManager.class);
        if (list == null) {
            list = shortcutManager.getDynamicShortcuts();
        }
        for (int i = 0; i < list.size(); i++) {
            ShortcutInfo info = list.get(i);
            PersistableBundle bundle = list.get(i).getExtras();
            File desktopFile;
            if (bundle == null || !(desktopFile = new File(bundle.getString(DESKTOP_FILE_ABSOLUTE_PATH))).exists()) {
                list.remove(i);
                i--;
            }
            //通过getDynamicShortcuts获取的信息丢失了图标，需要重新设置
            else {
                try {
                    GuestContainersManager manager = GuestContainersManager.getInstance(Globals.getAppContext());
                    GuestContainer container = manager.getContainerById(bundle.getLong(CONTAINER_ID, 0));
                    XDGLink xdgLink = new XDGLink(container, desktopFile);
                    Bitmap icon = BitmapFactory.decodeFile(manager.getIconPath(xdgLink));
                    //构建shortcutinfo
                    ShortcutInfo.Builder builder = new ShortcutInfo.Builder(Globals.getAppContext(), xdgLink.name)
                            .setShortLabel(xdgLink.name)
                            .setExtras(info.getExtras())
                            .setIntent(info.getIntent()) //设置intent又不一定非要指向目标activity，那难道会加到栈中？如果不指定
                            .setActivity(info.getActivity()) //设置目标activity
                            ;
                    if (icon != null)
                        builder.setIcon(Icon.createWithBitmap(icon));
                    else{
                        Log.d(TAG, "setDynamicShortcuts: 没有图标："+xdgLink.guestCont.mIconsPath+"/"+xdgLink.icon + ".png");
                    }
                    list.remove(i);
                    list.add(i, builder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        new AlertDialog.Builder(a).setView(scrollView).setPositiveButton(android.R.string.yes, null).create().show();
    }
}
