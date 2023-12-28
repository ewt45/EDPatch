package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.widget.LinearLayout.VERTICAL;
import static com.example.datainsert.exagear.RR.getS;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.adapter.ClickAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.adapter.EmptyAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneGestureArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneProfile;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.BtnColRecyclerView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.SubNormalPagerAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.WrapContentViewPager;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.axs.gamesControls.FalloutInterfaceOverlay2;
import com.example.datainsert.exagear.controls.model.FormatHelper;
import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.KeyCodes3;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomControls extends BaseFragment implements DialogInterface.OnClickListener {
    public static final String TAG = "CustomControls";
    private final BtnColRecyclerView[] mSidebarKeyRecyclerView = new BtnColRecyclerView[2];
    ViewGroup[] mPages;
    FalloutInterfaceOverlay2 mUiOverlay;
    private static final File dir = new File(QH.Files.edPatchDir(),"custom_controls");
    //    private File mSerFile2;
    private KeyCodes2 mKeyCodes2;//用于新建dialog的时候，初始化侧栏按键列表
//    private File mSerFile3;
    /**
     * 直接反序列化，如果是容器内界面，退出编辑后也从本地反序列化读取新的配置。
     */
    private KeyCodes3 mKeyCodes3;

//    private BtnContainer btnContainer;

//    private  static CustomControls mDialog;
//    public static void checkFocus() {
//        Log.d(TAG, "checkFocus: getCurrentFocus="+mDialog.getDialog().getCurrentFocus()+
//                "\ngetDecorView().findFocus()="+mDialog.getDialog().getWindow().getDecorView().findFocus());
//
//    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        XServerDisplayActivityConfigurationAware aware = Globals.getApplicationState();
        if (aware != null)
            mUiOverlay = ((FalloutInterfaceOverlay2) aware.getXServerDisplayActivityInterfaceOverlay());

        if (mUiOverlay != null) {
            mKeyCodes2 = mUiOverlay.getControlsFactory().getKeyCodes2();
            mKeyCodes3 = mUiOverlay.getControlsFactory().getKeyCodes3();
        } else {
            mKeyCodes2 = KeyCodes2.read(requireContext());
            mKeyCodes3 = KeyCodes3.read(requireContext());
        }

        Context c = requireContext();
        int frameRootId = View.generateViewId();
        FrameLayout frameRoot = new FrameLayout(c);
        frameRoot.setId(frameRootId);

        TouchAreaView touchAreaView = new TouchAreaView(c);
        touchAreaView.startEdit();
        frameRoot.addView(touchAreaView);

        return frameRoot;
//        return QH.wrapAsDialogScrollView(buildUI());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: 只用一个fragment，多次显示的时候是否会进入这个onCreateDialog？");//会，那没事了
//        if(mDialog==null)
//            mDialog=this;

        //先尝试从factory直接获取实例（已经在图形界面的情况）。没有再反序列化
//        mSerFile2 = new File(requireContext().getFilesDir(), KeyCodes2.KeyStoreFileName);
//        mSerFile3 = new File(requireContext().getFilesDir(), KeyCodes3.KeyStoreFileName);
        XServerDisplayActivityConfigurationAware aware = Globals.getApplicationState();
        if (aware != null)
            mUiOverlay = ((FalloutInterfaceOverlay2) aware.getXServerDisplayActivityInterfaceOverlay());

        if (mUiOverlay != null) {
            mKeyCodes2 = mUiOverlay.getControlsFactory().getKeyCodes2();
            mKeyCodes3 = mUiOverlay.getControlsFactory().getKeyCodes3();
        } else {
            mKeyCodes2 = KeyCodes2.read(requireContext());
            mKeyCodes3 = KeyCodes3.read(requireContext());
        }


        return super.onCreateDialog(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        //解决在viewpager中的edittext弹不出输入法的问题
        if(getDialog()!=null && getDialog().getWindow()!=null){
            getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }

        //然后弹出输入法
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        //设置确定按钮(不对这是普通Dialog）
        Log.d(TAG, "onStart: getdialog=" + getDialog());
//        ((AlertDialog)getDialog()).setButton(BUTTON_POSITIVE, getString(android.R.string.yes),this);


//        Window window = getDialog().getWindow();
//        //设置decorview的属性
//        int padding = QH.dp(requireContext(),16);
//        window.getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
//        window.getAttributes().gravity = Gravity.CENTER;
//        window.setAttributes(window.getAttributes());
        //设置标题(不起作用。。。）
//        getDialog().setTitle(S.get(S.CmCtrl_title));

    }

    @Override
    protected ViewGroup buildUI() {
        Context c = requireContext();
//        scrollView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        LinearLayout rootView = new LinearLayout(c);
        rootView.setOrientation(VERTICAL);

        String tipsOverAll = getS(RR.cmCtrl_lgPressHint);
//        if (((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity() instanceof XServerDisplayActivity)
//            tipsOverAll = tipsOverAll.substring(0, tipsOverAll.indexOf('\n'));
        rootView.addView(getTextViewWithText(c, tipsOverAll));

        //标签页滑动视图
        TabLayout tabLayout = new TabLayout(requireContext());
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        ViewPager viewPager = new WrapContentViewPager(requireContext());
        LinearLayout.LayoutParams viewPagerParams = new LinearLayout.LayoutParams(-1, -1);
        viewPagerParams.setMargins(0, 20, 0, 20);
//        viewPager.setLabelFor(View.NO_ID);
//        viewPager.setId(VIEWPAGER_RESOURCE_ID);
        rootView.addView(tabLayout, new ViewGroup.LayoutParams(-1, -2));
        rootView.addView(viewPager, viewPagerParams);


        //三个标签页
        mPages = new ViewGroup[]{
                new SubView1Mouse(c),
                new SubView2Keys(c, mKeyCodes2, mKeyCodes3),
                new SubView3Style(c),
                new SubView4Other(c)};
        addTransferCallback((SubView4Other) mPages[3]);

        String[] tabTitles = new String[]{
                getS(RR.cmCtrl_tabMouse),
                getS(RR.cmCtrl_tabKeys),
                getS(RR.cmCtrl_tabStyle),
                getS(RR.cmCtrl_tabOther)};

        //fragmentAdapter在dialogfragment里有问题，就用普通视图adapter吧
        viewPager.setAdapter(new SubNormalPagerAdapter(mPages, tabTitles));
        tabLayout.setupWithViewPager(viewPager, false);
        return rootView;
    }


    /**
     * 点击确定或取消按钮时
     *
     * @param dialog the dialog that received the click
     * @param which  the button that was clicked (ex.
     *               {@link DialogInterface#BUTTON_POSITIVE}) or the position
     *               of the item clicked
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "onClick: detach是在onclick监听之前吗");

//        Log.d(TAG, "onClick: getdialog能获取到自身吗"+ getDialog());
        //现在是只有点确定关闭对话框的时候才会更新keycode2和3（序列化存储为文件）
        if (which == BUTTON_POSITIVE) {

            //如果当前是图形界面，调用tscWidget的onLayout去更新一下布局（在这里序列化）
            if (mUiOverlay != null && ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity() instanceof XServerDisplayActivity) {
                mUiOverlay.refreshControlUI();
                Log.d(TAG, "onClick: 在图形界面内，直接修改keycode实例 不序列化");
            }
            //否则自己序列化
            else {
                KeyCodes2.write(mKeyCodes2, requireContext());
                KeyCodes3.write(mKeyCodes3, requireContext());
            }

        } else if (which == BUTTON_NEGATIVE) {
//            Snackbar.make(FabMenu.getMainFrameView((AppCompatActivity) requireActivity()), S.get(S.DriveD_ToastExitFail), Snackbar.LENGTH_LONG).show();
        }
    }

    private void addTransferCallback(SubView4Other subView4Other) {
        subView4Other.setCallback(new SubView4Other.TransferCallback() {
            @Override
            public void dismiss() {
                CustomControls.this.dismiss();
            }

            @Override
            public void exportData() {
                String data = FormatHelper.dataExport(mKeyCodes2, mKeyCodes3);
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getS(RR.cmCtrl_title), data);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), getS(RR.cmCtrl_s4_exportResult), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void importData() {
                //先获取一下当前的数据，如果导入失败了，恢复当前的数据
                String currentData = FormatHelper.dataExport(mKeyCodes2, mKeyCodes3);
                String data = null;
                try {
                    ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard.getPrimaryClip() == null || !(clipboard.hasPrimaryClip())
                            || !(Objects.requireNonNull(clipboard.getPrimaryClipDescription()).hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                        throw new Exception("剪切板没有数据 或者剪切板数据类型不是文本");
                    }

                    // Examines the item on the clipboard. If getText() does not return null, the clip item contains the text.
                    // Assumes that this application can only handle one item at a time.
                    data = (String) clipboard.getPrimaryClip().getItemAt(0).getText(); // Gets the clipboard as text.
                    if (data == null)
                        throw new Exception("剪切板有文本，但不是普通文本。可能是URI？");// The clipboard does not contain text. If it contains a URI, attempts to get data from it

                    //尝试转换为按键数据
                    FormatHelper.dataImport(data);
                    //如果成功了，关闭对话框，并且设置本次存储新数据 (最好直接关闭了，不知道在中途修改model整体实例，会不会导致其他功能未更新而出错）
                    getDialog().dismiss();//这个貌似不会调用positive button的onclicklisntenr，好耶
                    Toast.makeText(requireContext(), getS(RR.cmCtrl_s4_importResult).split("\\$")[0], Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    List<String> filelines = new ArrayList<>();
                    if(data!=null)
                        filelines.add(data);
                    filelines.add("");
                    filelines.add(e.getMessage());
                    for(StackTraceElement element: e.getStackTrace())
                        filelines.add(element.toString());

                    Throwable errorCause = e.getCause();
                    while(errorCause!=null){
                        filelines.add("Caused by: "+errorCause.getMessage());
                        for(StackTraceElement element: errorCause.getStackTrace())
                            filelines.add(element.toString());
                        errorCause = errorCause.getCause();
                    }

                    try {
                        FileUtils.writeLines(new File("/sdcard/Download/errors.txt"),filelines);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    e.printStackTrace();
                    FormatHelper.dataImport(currentData);
                    Toast.makeText(requireContext(), getS(RR.cmCtrl_s4_importResult).split("\\$")[1], Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public static File dataDir(){
        if(!dir.exists())
            dir.mkdirs();

        return dir;
    }

    @Override
    public void callWhenFirstStart(AppCompatActivity activity) {
//        //TODO 初始化前先为多配置做好准备。以后这个函数可能需要更改
//        SubView4Other.prepareForProfilesV2();
    }

    @Override
    public String getTitle() {
        return getS(RR.cmCtrl_title);
    }
}

