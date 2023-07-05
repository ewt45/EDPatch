package com.example.datainsert.exagear.mutiWine;


import static com.example.datainsert.exagear.RR.getS;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.ed.R;
import com.eltechs.ed.fragments.ManageContainersFragment;
import com.eltechs.ed.guestContainers.GuestContainer;
import com.eltechs.ed.guestContainers.GuestContainerConfig;
import com.eltechs.ed.guestContainers.GuestContainersManager;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.mutiWine.v2.WineStoreFragment;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MutiWine {
    private static final int VERSION_FOR_EDPATCH = 2;
    private static final String TAG = "MutiWine";
    //pref前缀，用于容器设置，记录wine版本，需要根据apk包名自行修改
    //内容有：wineVersion，wineExecutePath
    public static String CONTAINER_CONFIG_FILE_KEY_PREFIX = GuestContainerConfig.CONTAINER_CONFIG_FILE_KEY_PREFIX;

    //pref前缀，用于新建容器时临时记录wine版本，和启动容器时临时记录wine版本
    //内容有：新建容器时 wineVersion，wineExecutePath，winePatternPath
    public static String TMP_WINE_VER_PREF = "tmpWineVerPref";
    //一些写入xml的键名
    public static String KEY_WINE_VERSION = "wineVersion";
    public static String KEY_WINE_INSTALL_PATH = "wineInstallPath";
    public static String KEY_WINE_PATTERN_PATH = "winePatternPath";
    public static String KEY_WINE_CONTAINER_NAME = "NAME";
    public static String KEY_CONTAINER_ID = "CID";

    /**
     * ex的managerContainersFragment里的onCreateOptionsMenu方法
     * 会创建右上角的新建容器菜单，修改其内容以供用户选择wine版本
     *
     * @param menu             toolbar的右侧菜单
     * @param fragmentInstance ManageContainersFragment类的实例，用于获取task  //ManageContainersFragment类中的同步任务，类型写成其继承的父类看看行不行 AsyncTask<GuestContainer, Void, Void> task
     */
    @SuppressLint("ResourceType")
    public static void setOptionMenu(Menu menu, ManageContainersFragment fragmentInstance) {
         /*
         摘抄自onCreateOptionsMenu的介绍，这个menu可以自己留着用吧
         You can safely hold on to menu (and any items created from it),
         making modifications to it as desired, until the next time onCreateOptionsMenu() is called.
         */
        WineVersion.initList();
        Log.d(TAG, "setOptionMenu: 开始设置右上角菜单，获取到的fragment实例为" + fragmentInstance +
                "\n读取到的wine版本有" + WineVersion.wineList);
        //管理wine版本界面
        menu.add(getS(RR.mw_fragTitle))
                .setIcon(QH.rslvID(R.drawable.ic_file_download_24dp, 0x7f0800a1))
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener(item -> {
                    //参考EDMainActivity的onNavigationItemSelected
                    FragmentManager supportFragmentManager = ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity().getSupportFragmentManager();
                    for (int i = 0; i < supportFragmentManager.getBackStackEntryCount(); i++) {
                        supportFragmentManager.popBackStack();
                    }
                    WineStoreFragment wineStoreFragment = new WineStoreFragment();
                    supportFragmentManager.beginTransaction()
                            .replace(QH.rslvID(R.id.ed_main_fragment_container, 0x7f090070), wineStoreFragment, "CONTAINER_PROP")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null)
                            .commit();
                    return true;
                });

        //新建容器按钮
        SubMenu subMenu = menu.addSubMenu("+");
        //原来通过getItem()就可以吧submenu转为menuItem，就可以设置直接显示或者在三个点里显示了! 草，我说menuItem怎么没有adSubMenu()
        subMenu.getItem()
                //当subMenu和其对应的menuItem都设置了title，显示的以menuItem为准
                .setTitle("+")
                .setIcon(QH.rslvID(R.drawable.ic_add_24dp, 0x7f08009b))
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        //添加wine版本的菜单项，将其在wineList的下标作为ItemId，ItemId作为监听判断是哪个选项的依据。
        if (WineVersion.wineList.size() == 1) { //当只有一个选项的时候，不显示子菜单了
            subMenu.getItem().setOnMenuItemClickListener(item -> {
                new InnerContAsyncTask(WineVersion.wineList.get(0), fragmentInstance).execute();
                return true;
            });
        } else {
            for (int i = 0; i < WineVersion.wineList.size(); i++) {
                WineVersion wineVersion = WineVersion.wineList.get(i);
                subMenu.add(1, i, 0, wineVersion.name).setOnMenuItemClickListener(item -> {
                    //直接自己类里复刻一个ContAsyncTask，不搞什么反射或者插入好几处了
                    new InnerContAsyncTask(wineVersion, fragmentInstance).execute();
                    //                writeTmpWineVerPref(wineVersion);//v1方法，现在直接写到xdroid_n目录下。新建容器. 将创建容器的wine版本写入临时pref
                    //                fragmentInstance.callToCreateNewContainer();//v1方法，现在用反射。调用fragment类以新建内部类
                    return true;
                });
            }
        }

//        subMenu.add(2, WineVersionConfig.wineList.size(), 0, "说明").setOnMenuItemClickListener(listener);
//        subMenu.add(3, 100, 0, "管理").setOnMenuItemClickListener(listener);


        //为什么子菜单的内容不会进到这个监听啊（啊原来只监听本menuitem。。。）
    }


    /**
     * 启动容器时，在StartGuest里调用，添加环境变量（wine的执行路径和链接库路径）
     *
     * @param id   容器id
     * @param list 环境变量列表
     * @deprecated 环境变量通过添加action {@link com.example.datainsert.exagear.action.AddEnvironmentVariables}实现
     */
    @Deprecated
    public static void addEnvVars(Long id, List<String> list) {
//        String winePath = null;//wine执行路径，从xdroid_n/envp.txt中读取
//        File envpFile = new File(((ExagearImageAware) Globals.getApplicationState()).getExagearImage().getPath(), "home/xdroid_" + id + "/envp.txt");
//        try {
//            List<String> lines = FileUtils.readLines(envpFile);
//            for (String s : lines) {
//                if (s.startsWith(KEY_WINE_INSTALL_PATH))
//                    winePath = s.substring((KEY_WINE_INSTALL_PATH + "=").length());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            //如果不存在v2的txt，考虑读取v1的pref
//            winePath = Globals.getAppContext().getSharedPreferences(CONTAINER_CONFIG_FILE_KEY_PREFIX + id, Context.MODE_PRIVATE).getString(KEY_WINE_INSTALL_PATH, "/usr");
//        }
//        if (winePath == null) {
//            winePath = "usr";
//        }
//
//        SharedPreferences sp = Globals.getAppContext().getSharedPreferences(CONTAINER_CONFIG_FILE_KEY_PREFIX + id, Context.MODE_PRIVATE);
//        //添加执行路径（会覆盖原来的 $PATH不起作用，只能把默认的都写一遍了，希望没有漏掉）
//        list.add("PATH="
//                + winePath
//                + "/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games");
//        Log.d(TAG, "getEnvVarBin: wine执行路径为" + list.get(list.size() - 1));
//        //添加链接库路径（可以用于设置渲染模式）
//        String[] rendererValues = {""};
//        try {
//            rendererValues = Globals.getAppContext().getResources().getStringArray(QH.rslvID(R.array.cont_pref_renderer_values, 0x7f030009));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String renderer = sp.getString(KEY_RENDERER, rendererValues[0]);
//        String ldPath = ""; //不同渲染模式选择不同链接库路径
//        if ("llvmpipe".equals(renderer))
//            ldPath = "/usr/bin/llvmpipe";
//        else if ("virgloverlay".equals(renderer)) {
//            //如果是vo，顺便添加一下vtest_win
//            list.add("VTEST_WIN=1");
//            list.add("VTEST_SOCK=");
//            ldPath = "/usr/bin/virgloverlay";
//        } else if ("virpipe".equals(renderer))
//            //这里 三合一 vtest叫virpipe，ubt启动设置那个类里，只有判断是virpipe的时候才会new MCat().改动时注意
//            ldPath = "/usr/bin/vtest";
//        else if ("gallium_xlib_zink_turnip".equals(renderer))
//            ldPath = "/usr/bin/zink";
//        list.add("LD_LIBRARY_PATH="
//                + ":" + ldPath
//                + ":/usr/lib/i386-linux-gnu"
//                + ":" + sp.getString(KEY_WINE_INSTALL_PATH, "/usr") + "/lib");
//        Log.d(TAG, "getEnvVarBin: 链接库路径为" + list.get(list.size() - 1) + ", 所选模式为" + renderer);
    }


    /**
     * 新建容器时，从临时pref中读取wine版本信息，并写入新建容器的设置pref中。
     * 顺便设置一下容器名。放在loadDefault()的结尾，这样开头写入Container_n就会被此时的名字覆盖
     *
     * @deprecated 废弃，无作用。改在执行新建task时一并写入xdroid文件夹
     */
    @Deprecated
    public static void writeWineVerToContainerConfig(Long id) {
//        //容器设置pref的名字,这前缀不好获取，自己改吧
//        String prefName = CONTAINER_CONFIG_FILE_KEY_PREFIX + id;
//        //临时的pref
//        SharedPreferences sp = Globals.getAppContext().getSharedPreferences(TMP_WINE_VER_PREF, Context.MODE_PRIVATE);
//        //将临时pref的内容转移到容器设置pref中
//        Globals.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE).edit()
//                .putString(KEY_WINE_VERSION, sp.getString(KEY_WINE_VERSION, ""))
//                .putString(KEY_WINE_INSTALL_PATH, sp.getString(KEY_WINE_INSTALL_PATH, "/usr"))
//                //顺便修改一下容器的名字，提高辨识度
//                .putString(KEY_WINE_CONTAINER_NAME, sp.getString(KEY_WINE_VERSION, "") + "_" + id).apply();
    }

    /**
     * 和writeWineVerToContainerConfig很像。但是是复制容器时，
     *
     * @param oldId 被复制的容器id
     * @param newId 新容器id
     * @deprecated 废弃，无作用。改在执行新建task时一并写入xdroid文件夹
     * (还是留着吧，复制不走新建的那个task。如果apk从v1升到v2的就有这个功能，直接装v2的就没这个功能，反正就一个名字也不影响啥）
     */
    @Deprecated
    public static void cloneWineVerToContainerConfig(Long oldId, Long newId) {
        //旧容器设置
        SharedPreferences sp = Globals.getAppContext().getSharedPreferences(CONTAINER_CONFIG_FILE_KEY_PREFIX + oldId, Context.MODE_PRIVATE);
        //复制旧容器设置
        Globals.getAppContext().getSharedPreferences(CONTAINER_CONFIG_FILE_KEY_PREFIX + newId, Context.MODE_PRIVATE).edit()
                .putString(KEY_WINE_VERSION, sp.getString(KEY_WINE_VERSION, ""))
                .putString(KEY_WINE_INSTALL_PATH, sp.getString(KEY_WINE_INSTALL_PATH, "/usr"))
                //顺便修改一下容器的名字，提高辨识度
                .putString(KEY_WINE_CONTAINER_NAME, sp.getString("NAME", "Container_" + newId)).apply();
    }

    /**
     * 获取自定义的WINEPREFIX路径，即包含.wine文件夹的路径，原本是在/opt/guestcont-pattern/
     *
     * @return 返回当前版本的wine对应的WINEPREFIX路径，用于创建新的容器时复制该文件夹到xdroid_n中
     * @deprecated 废弃。现在只会返回"/opt/guestcont-pattern/"
     */
    @Deprecated
    public static String getCustomPatternPath() {
//        //新建容器的wine版本
//        String wineVersion = Globals.getAppContext().getSharedPreferences(TMP_WINE_VER_PREF, Context.MODE_PRIVATE).getString(KEY_WINE_VERSION, "");
//        //根据wine版本修改wineprefix路径,路径定义在txt中
//        for (WineVersion wine : WineVersionConfig.wineList) {
//            if (!wine.name.equals(wineVersion))
//                continue;
//            //如果找到对应的wine版本路径，返回
//            return wine.patternPath;
//
//        }
//        //如果没找到，返回默认的pattern路径
        return "/opt/guestcont-pattern/";
    }

    private static class InnerContAsyncTask extends AsyncTask<GuestContainer, Void, Void> {
        private final int mAction = 0;//0是新建，这里只用到了0
        private final ManageContainersFragment mFragment;
        private final WineVersion mWineVersion;
        private boolean mIsAsyncTaskRun;//原本应为ManageContainersFragment成员变量
        private ProgressDialog mProgressDialog;//原本应为ManageContainersFragment成员变量

        public InnerContAsyncTask(WineVersion wineVersion, ManageContainersFragment fragment) {
            mFragment = fragment;
            mWineVersion = wineVersion;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            mIsAsyncTaskRun = true;
            mProgressDialog = ProgressDialog.show(mFragment.getContext(), "", getS(RR.mw_newContProgress), true, false);
            //如果guestcont-pattern不存在手动创建
            File patternFile  = new File(((ExagearImageAware) Globals.getApplicationState()).getExagearImage().getPath(), "opt/guestcont-pattern/");
            if(!patternFile.exists())
                patternFile.mkdirs();
        }

        @Override // android.os.AsyncTask
        public Void doInBackground(GuestContainer... guestContainerArr) {
            GuestContainer newContainer = GuestContainersManager.getInstance(Globals.getAppContext()).createContainer();
            //复制完pattern文件夹之后，写入环境变量文本（在之前写入会导致文件夹复制失败）/xdroid_n/envp.txt
            if (newContainer != null) {
                try {
                    //写入/home/xdroid_n/envp.txt
                    File envpFile = new File(newContainer.mPath, "envp.txt");
                    envpFile.getParentFile().mkdirs();
                    FileUtils.writeLines(envpFile, Arrays.asList(
                            KEY_WINE_INSTALL_PATH + "=" + mWineVersion.installPath,
                            KEY_WINE_VERSION + "=" + mWineVersion.name));
                    //设置容器名称到pref
                    Globals.getAppContext().getSharedPreferences(CONTAINER_CONFIG_FILE_KEY_PREFIX + newContainer.mId, Context.MODE_PRIVATE).edit()
                            .putString(KEY_WINE_VERSION, mWineVersion.name)  //这两条用不到了，其实可以不写
                            .putString(KEY_WINE_INSTALL_PATH, mWineVersion.installPath)
                            .putString(KEY_WINE_CONTAINER_NAME, mWineVersion.name + "_" + newContainer.mId).apply();//容器显示的名字
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Void r2) {
            //草了，refresh是private还得用反射
            try {
                Method refresh = ManageContainersFragment.class.getDeclaredMethod("refreshContainersList");
                refresh.setAccessible(true);
                refresh.invoke(mFragment); //第一个参数是所属类的实例
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
//            mFragment.refreshContainersList();
            mProgressDialog.dismiss();
            mIsAsyncTaskRun = false;
        }
    }

//    /**
//     * v1方法，现在直接记录在xdroid文件夹里
//     * 新建容器时临时记录wine的版本，稍后会合并到容器设置的pref中
//     *
//     * @param wine wine版本信息
//     */
//    public static void writeTmpWineVerPref(WineVersion wine) {
//        Log.d(TAG, "writeTmpWineVerPref: 新建容器时 将wine版本信息写入临时pref。当前wine版本：" + wine.name);
//        Globals.getAppContext().getSharedPreferences(TMP_WINE_VER_PREF, Context.MODE_PRIVATE).edit()
//                .putString(KEY_WINE_VERSION, wine.name)
//                .putString(KEY_WINE_INSTALL_PATH, wine.installPath)
//                .putString(KEY_WINE_PATTERN_PATH, wine.patternPath).apply();
//    }

//    /**(不用了，在startguest里改吧）
//     * 启动容器时，修改UBTLaunchConfiguration的addArgumentsToEnvironment()
//     * 在该函数开头添加环境变量, 返回当前版本wine的执行文件所在的路径，加入到PATH中
//     */
//    public static String getEnvVarBin(){
//        long id = Globals.getAppContext().getSharedPreferences(TMP_WINE_VER_PREF, Context.MODE_PRIVATE).getLong(KEY_CONTAINER_ID,0);
//        String prefName = CONTAINER_CONFIG_FILE_KEY_PREFIX+id;
//        String wineBinPath = "PATH=$PATH:"+Globals.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE)
//                .getString(KEY_WINE_INSTALL_PATH,"/usr")+"/bin";
//       Log.d(TAG, "getEnvVarBin: wine执行路径为"+wineBinPath);
//       return wineBinPath;
//    }
//
//    /**  (不用了，在startguest里改吧）
//     * 同上
//     * 在该函数开头添加环境变量, 返回当前版本wine的链接库文件所在的路径，加入到LD_LIBRARY_PATH中
//     */
//    public static String getEnvVarLib(){
//        long id = Globals.getAppContext().getSharedPreferences(TMP_WINE_VER_PREF, Context.MODE_PRIVATE).getLong(KEY_CONTAINER_ID,0);
//        String prefName = CONTAINER_CONFIG_FILE_KEY_PREFIX+id;
//        String wineLibPath = "LD_LIBRARY_PATH=$LD_LIBRARY_PATH:"+Globals.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE)
//                .getString(KEY_WINE_INSTALL_PATH,"/usr")+"/lib:/usr/lib/i386-linux-gnu";
//        Log.d(TAG, "getEnvVarBin: wine链接库路径为"+wineLibPath);
//        return wineLibPath;
//    }
//
//    /**(不用了，在startguest里改吧）
//     * 启动容器时，在StartGuest里调用此函数，将当前容器id写入tmp中，
//     * 以便之后创建UBT设置时添加对应版本的wine的环境变量
//     */
//    public static void writeIdToTmp(Long id){
//        Globals.getAppContext().getSharedPreferences(TMP_WINE_VER_PREF,Context.MODE_PRIVATE).edit()
//                .putLong(KEY_CONTAINER_ID,id).apply();
//    }

//    /**
//     * 启动容器时，修改UBTLaunchConfiguration那里没用，只好修改StartGuest里的启动参数，将wine改为绝对路径
//     * @param id 要启动的容器id
//     * @return  该容器的wine执行路径
//     */
//    public static String getExeEvalArgv(Long id){
//        String prefName = CONTAINER_CONFIG_FILE_KEY_PREFIX+id;
//        String winePath = Globals.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE)
//                .getString(KEY_WINE_INSTALL_PATH,"wine");
//        Log.d(TAG, "getExeEvalArgv: 从容器设置启动，当前wine的绝对路径为"+winePath);
////        return "eval \"wine /opt/exec_wrapper.exe /opt/TFM.exe D:/\"";
//        return "eval \""+winePath+" winecfg \"";
//    }


//    /**
//     * 启动容器时，在ubtLaunchConfiguration中，获取当前容器的wine执行路径（ubt这个改了不行，这个函数应该没用了吧）
//     * @return wine执行路径
//     */
//    public static String getWineExecutePath(){
//        String path = Globals.getAppContext().getSharedPreferences(TMP_WINE_VER_PREF,Context.MODE_PRIVATE)
//                .getString("currWineExecutePath","/usr/bin/wine");
//        Log.d(TAG, "getWineExecutePath: 当前启动容器的wine执行路径为"+path);
//        return path;
//
//    }

//    /**
//     * 启动容器时，将当前启动的容器的wine版本和执行路径写入pref的currWineVersion和currWineExecutePath，
//     * 以便ubtLaunchConfiguration读取，设置wine执行路径（ubt这个改了不行，这个函数应该没用了吧）
//     * @param id 当前启动的容器的id
//     */
//    public static void setCurrLauchWineVer(Long id){
//        String prefName = CONTAINER_CONFIG_FILE_KEY_PREFIX+id;
//        //当前启动的容器的设置pref
//        SharedPreferences sp = Globals.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
//        //将当前启动的容器的wine版本和执行路径写入pref
//        Globals.getAppContext().getSharedPreferences(TMP_WINE_VER_PREF,Context.MODE_PRIVATE).edit()
//                .putString("currWineVersion",sp.getString(KEY_WINE_VERSION,""))
//                //默认是usr/bin/wine，或者根据wine版本自定义的
//                .putString("currWineExecutePath",sp.getString(KEY_WINE_INSTALL_PATH,"/usr/bin/wine")).apply();
//        Log.d(TAG, "setCurrLauchWineVer: 当前读取的容器设置pref为"+prefName+", wineVersion为"+sp.getString(KEY_WINE_VERSION,""));
//    }


//    public static 
//    /**
//     * 显示弹窗菜单，选择新建容器的wine版本
//     * @param v 弹窗菜单关联到的视图
//     */
//    public void ShowOpt(View v){
//        //新建弹窗菜单，根据选项写入pref
//        PopupMenu popupMenu = new PopupMenu(Globals.getAppContext(), (View) v.getParent());
//        Menu menu = popupMenu.getMenu();
//        menu.add("wine3.0.5");
//        menu.add("wine4.21");
//        popupMenu.setOnMenuItemClickListener(this);
//        popupMenu.show();
//    }

//    /**
//     * 糟了，ex里这个不是view，是menuitem，怎么加二级菜单啊啊啊（那就不用popupMenu了用popupWindow试试）(也不行，dialog吧）
//     * @param v
//     */
//    public void ShowOpt(MenuItem v){
//        Context a = Globals.getAppContext();
//        //创建popupwindow
//        PopupWindow popupWindow = new PopupWindow(a);
//        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        //创建内部视图线性布局
//        LinearLayout linearLayout = new LinearLayout(a);
//        Button button1 = new Button(a);
//        button1.setText("wine3.0.5");
//        Button button2 = new Button(a);
//        button2.setText("wine4.21");
//        linearLayout.addView(button1);
//        linearLayout.addView(button2);
//        popupWindow.setContentView(linearLayout);
////        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
////        popupWindow.setOutsideTouchable(false);
//        popupWindow.setFocusable(true);
////        popupWindow.showAtLocation();
//    }

//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        if(item.getTitle().equals("wine3.0.5")){
//            Log.d(TAG, "onContextItemSelected: 选择了wine3");
//            SharedPreferences.Editor editor = Globals.getAppContext().getApplicationContext().
//                    getSharedPreferences("ExagearPref", Context.MODE_PRIVATE).edit();
//            editor.putString(KEY_WINE_VERSION,"wine3.0.5").apply();
//            return true;
//        }else if(item.getTitle().equals("wine4.21")){
//            Log.d(TAG, "onContextItemSelected: 选择了wine4");
//            SharedPreferences.Editor editor = Globals.getAppContext().getApplicationContext().
//                    getSharedPreferences("ExagearPref", Context.MODE_PRIVATE).edit();
//            editor.putString(KEY_WINE_VERSION,"wine4.21").apply();
//            return true;
//        }
//        return false;
//    }
}
