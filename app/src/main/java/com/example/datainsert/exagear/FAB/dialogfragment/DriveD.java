package com.example.datainsert.exagear.FAB.dialogfragment;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.RR.getSArr;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.SubMenu;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.drived.DrivePathChecker;
import com.example.datainsert.exagear.FAB.dialogfragment.drived.SimpleTabSelectListener;
import com.example.datainsert.exagear.FAB.widget.MyTextInputEditText;
import com.example.datainsert.exagear.FAB.widget.SimpleTextWatcher;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DriveD extends BaseFragment {
    private static final String TAG = "DriveD";
    private static final File drivesSavedFile = new File(((ExagearImageAware) Globals.getApplicationState()).getExagearImage().getPath().getAbsolutePath(), "opt/drives.txt");
    //PREF_VAL_DST_NAME 没有自定义的时候，默认的文件夹名,可以改这个默认的路径
    public static String PREF_VAL_PAR_NAME = Environment.getExternalStorageDirectory().getAbsolutePath(), PREF_VAL_DST_NAME = "Exagear"; //值为string
    /**
     * 存储的盘符及其路径。列表，列表的一项是字盘符名（大写），父路径，文件夹名 用空格分开
     */
    private List<String> drivesList = new ArrayList<>();
    private DrivePathChecker mPathChecker;

    /**
     * 在StartGuest中初始化d盘路径时调用，返回对应的file
     *
     * @return file
     */
    public static File getDriveDDir() {
        String[] splits = savedTxtFileRead().get(0).split(" ");
        return new File(splits[1], splits[2]);
//        return new File("/storage/BA73-022B");//825E-837B
    }

    /**
     * 从txt中读取存储的盘符及其路径
     * 生成： 列表，列表的一项是字符串数组，数组0是盘符名，数组1是盘符路径
     * 运行此函数后，保证drivesList中至少有一个元素
     */
    public static List<String> savedTxtFileRead() {
        List<String> drivesList = new ArrayList<>();

        //初始化前，应考虑从旧版迁移（读取sp中的数据）
        String oldWayDst = getPreference().getString("PREF_KEY_DST_IND", null);
        int oldWayPar = getPreference().getInt("PREF_KEY_PAR_IND", -1);
        String[] oldWayParList = new String[]{Environment.getExternalStorageDirectory().getAbsolutePath(), Globals.getAppContext().getExternalFilesDir(null).getAbsolutePath(), Environment.getExternalStorageDirectory().getAbsolutePath()};//sdcard的数组不好模拟，干脆替换成外部存储吧
        if (oldWayDst != null && oldWayPar != -1) {
            drivesList.add("D " + oldWayParList[oldWayPar] + " " + oldWayDst);
            savedTxtFileWrite(drivesList);
            getPreference().edit().remove("PREF_KEY_DST_IND").remove("PREF_KEY_PAR_IND").apply();
        }

        try {
            drivesList = FileUtils.readLines(drivesSavedFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < drivesList.size(); i++)
            if (drivesList.get(i).split(" ").length != 3) {
                drivesList.remove(i);
                i--;
            }

        if (drivesList.size() == 0)
            drivesList.add(String.format("%s %s %s", "D", PREF_VAL_PAR_NAME, PREF_VAL_DST_NAME));

        return drivesList;
    }

    /**
     * 将列表存储到txt中。格式：一个列表项为一行，盘符名和盘符路径用空格分开
     */
    private static void savedTxtFileWrite(List<String> drivesList) {
        try {
            FileUtils.writeLines(drivesSavedFile, StandardCharsets.UTF_8.name(), drivesList, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置tvInput的文本改变的监听器。内容：更新路径到list，让pathchecker检查路径可用性
     */
    private void setTextChangeListener(MyTextInputEditText tvInput, TabLayout tabLayout, boolean isPar) {
        tvInput.addTextChangedListener((SimpleTextWatcher) s -> {
            int pos = tabLayout.getSelectedTabPosition();
            String[] splits = drivesList.get(pos).split(" ");
            drivesList.set(pos, String.format("%s %s %s", splits[0], isPar ? s.toString() : splits[1], isPar ? splits[2] : s.toString()));
            if (isPar) mPathChecker.setStrPar(s.toString());
            else mPathChecker.setStrDst(s.toString());
            mPathChecker.updateCheckResult();
        });
    }

    @Override
    protected ViewGroup buildUI() {
        Context c = requireContext();

        drivesList = savedTxtFileRead();

        LinearLayout rootView = new LinearLayout(c);
        rootView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams paddingParams = new LinearLayout.LayoutParams(-1, -2);
        paddingParams.topMargin = AndroidHelpers.dpToPx(24);

        rootView.addView(getTextViewWithText(c, getS(RR.DriveD2_info)));

        //盘符名
        TabLayout tabLayout = new TabLayout(c);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setZ(100);
        for (String s : drivesList)
            tabLayout.addTab(tabLayout.newTab().setText(s.charAt(0) + ":\\"));

        //跟在盘符tab后的操作按钮
        ImageButton btnDriveOpt = new ImageButton(c);
        btnDriveOpt.setBackground(requireContext().getDrawable(QH.rslvID(R.drawable.ic_add_24dp, 0x7f08009b)));
        btnDriveOpt.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(c, v);
            //新建盘符
            popupMenu.getMenu().add(getS(RR.DriveD2_newDrive)).setOnMenuItemClickListener(item -> {
                //找到合适的盘符
                List<Character> existedDriveNameList = new ArrayList<>(Arrays.asList('A', 'B', 'C', 'E', 'Z'));
                char properDriveName = '0';
                for (String s : drivesList)
                    existedDriveNameList.add(s.charAt(0));
                for (int i = 0x41; i < 0x5B; i++)
                    if (!existedDriveNameList.contains((char) i)) {
                        properDriveName = (char) i;
                        break;
                    }
                //添加到list中； 新建tab并选中
                if (properDriveName != '0') {
                    drivesList.add(String.format("%s %s %s", properDriveName, PREF_VAL_PAR_NAME, PREF_VAL_DST_NAME));
                    tabLayout.addTab(tabLayout.newTab().setText(properDriveName + ":\\"), true);
                }
                return true;
            });
            //删除当前盘符
            popupMenu.getMenu().add(getS(RR.DriveD2_delDrive)).setEnabled(tabLayout.getTabCount() > 1).setOnMenuItemClickListener(item -> {
                //从list中删除->删除tab-> 选择一个新tab
                int delPos = tabLayout.getSelectedTabPosition();
                drivesList.remove(delPos);
                tabLayout.removeTabAt(delPos);
                Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                return true;
            });
            popupMenu.show();
        });

        LinearLayout linearTab = new LinearLayout(c);
        linearTab.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams btnAddParams = new LinearLayout.LayoutParams(-2, -2);
        btnAddParams.gravity = Gravity.CENTER_VERTICAL;
        btnAddParams.setMarginStart(QH.px(requireContext(), 8));
        btnAddParams.setMarginEnd(QH.px(requireContext(), 16));
        linearTab.addView(tabLayout);
        linearTab.addView(btnDriveOpt, btnAddParams);
        HorizontalScrollView scrollTab = new HorizontalScrollView(c);
        scrollTab.addView(linearTab);
        rootView.addView(scrollTab, new LinearLayout.LayoutParams(-1, -2));

        //文件夹父目录
        MyTextInputEditText tvInputParDir = new MyTextInputEditText(requireContext(), null, null, getS(RR.DriveD_EditParTitle));
        tvInputParDir.setInputType(InputType.TYPE_NULL);
        String[] deviceType = getSArr(RR.DriveD2_devType);
        String[] parType = getSArr(RR.DriveD2_parType);
        tvInputParDir.setPopupMenuCallback(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            File[] extFiles = c.getExternalFilesDirs(null);


            for (int i = 0; i < extFiles.length; i++) {
                String filesDirPath = extFiles[i].getAbsolutePath();
                int cutInd = filesDirPath.indexOf("/Android/data");
                String rootExtDevPath = cutInd != -1 ? filesDirPath.substring(0, cutInd) : filesDirPath;
                SubMenu subMenu = popupMenu.getMenu().addSubMenu(i == 0 ? deviceType[0] : deviceType[1] + " - " + i);//手机存储 其他外部存储设备
                subMenu.add(parType[0]).setOnMenuItemClickListener(item -> { //根目录
                    tvInputParDir.setText(rootExtDevPath);
                    return true;
                });
                subMenu.add(parType[1]).setOnMenuItemClickListener(item -> { //应用专属目录
                    tvInputParDir.setText(filesDirPath);
                    return true;
                });
            }

            if (extFiles.length == 1)
                popupMenu.getMenu().add(deviceType[2]).setEnabled(false); //其他外部存储设备(无)

            popupMenu.show();
        });
        setTextChangeListener(tvInputParDir, tabLayout, true);
        TextInputLayout textInputLayout = new TextInputLayout(c);
        textInputLayout.addView(tvInputParDir);
        rootView.addView(textInputLayout, paddingParams);

        //文件夹自身名称
        String[] dirNames = new String[]{"Exagear", "Download"};
        MyTextInputEditText tvInputDstDir = new MyTextInputEditText(requireContext(), dirNames, dirNames, getS(RR.DriveD_EditDstTitle));
        tvInputDstDir.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//        InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;用于禁用语法检查
        tvInputDstDir.setSingleLine();
        tvInputDstDir.setImeOptions(EditorInfo.IME_ACTION_DONE);
        setTextChangeListener(tvInputDstDir, tabLayout, false);
        TextInputLayout textInputLayout2 = new TextInputLayout(c);
        textInputLayout2.addView(tvInputDstDir);
        rootView.addView(textInputLayout2, paddingParams);

        //显示文件夹完整路径及该路径是否可用
        TextView tvTestResult = new TextView(c);
        tvTestResult.setTextIsSelectable(true);
        rootView.addView(tvTestResult, paddingParams);

        //说明 (*应用专属目录)
        rootView.addView(QH.getOnePrefLine(new TextView(c), "*" + parType[1], getS(RR.DriveD_DescCont), null), paddingParams);

        mPathChecker = new DrivePathChecker(tvInputParDir, tvInputDstDir, tvTestResult);

        //初始化当前路径
        TabLayout.OnTabSelectedListener tabSelectedListener = new SimpleTabSelectListener() {
            @Override
            public void onTabSelectedOrReSel(TabLayout.Tab tab) {
                String fullPath = drivesList.get(tab.getPosition());
                tvInputParDir.setText(fullPath.split(" ")[1]);
                tvInputDstDir.setText(fullPath.split(" ")[2]); //触发tvInput的监听，然后重新设置路径，并检查可用性
            }
        };
        //exa的库里还没有 TabLayout.BaseOnTabSelectedListener,只有OnTabSelectedListener。改依赖版本又没有用。试试反射吧
        if(QH.isTesting()){
            tabLayout.addOnTabSelectedListener(tabSelectedListener);
        }else{
            try {
                TabLayout.class.getDeclaredMethod("addOnTabSelectedListener", TabLayout.OnTabSelectedListener.class)
                        .invoke(tabLayout, tabSelectedListener);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();

        return rootView;
    }

    @Override
    public void callWhenFirstStart(AppCompatActivity activity) {
        File file = new File(PREF_VAL_PAR_NAME, PREF_VAL_DST_NAME);
        if (!file.exists()) {
            boolean b = file.mkdir();
            Log.d(TAG, "callWhenFirstStart: 初次安装后启动，尝试创建文件夹结果 " + b);
        }
    }

    @Override
    public String getTitle() {
        return getS(RR.DriveD2_title);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == BUTTON_POSITIVE) {
            savedTxtFileWrite(drivesList);
            //现在不需要重启了，因为d盘是在action里自己设置
//            Snackbar.make(FabMenu.getMainFrameView((AppCompatActivity) requireActivity()), getS(RR.DriveD_SncBrTxt), Snackbar.LENGTH_LONG)
//                    .setAction(getS(RR.DriveD_SncBrBtn), v -> android.os.Process.killProcess(android.os.Process.myPid()))
//                    .show();
        }
    }

}
