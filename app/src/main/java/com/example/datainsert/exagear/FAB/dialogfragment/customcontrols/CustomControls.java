package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.widget.LinearLayout.VERTICAL;

import static com.example.datainsert.exagear.RR.getS;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.BtnColRecyclerView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.SubNormalPagerAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.WrapContentViewPager;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.interfaceOverlay.FalloutInterfaceOverlay2;
import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.KeyCodes3;

import java.io.File;
import java.io.IOException;

public class CustomControls extends BaseFragment implements DialogInterface.OnClickListener {
    public static final String TAG = "CustomControls";
    private final BtnColRecyclerView[] mSidebarKeyRecyclerView = new BtnColRecyclerView[2];
    ViewGroup[] mPages = new ViewGroup[3];
    FalloutInterfaceOverlay2 mUiOverlay;
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
        if(aware!=null)
            mUiOverlay = ((FalloutInterfaceOverlay2) aware.getXServerDisplayActivityInterfaceOverlay());

        if (mUiOverlay != null) {
            mKeyCodes2 = mUiOverlay.getControlsFactory().getKeyCodes2();
            mKeyCodes3 = mUiOverlay.getControlsFactory().getKeyCodes3();
        } else {
            try {
                mKeyCodes2 = KeyCodes2.deserialize(requireContext());
            } catch (IOException | ClassNotFoundException e) {
                mKeyCodes2 = new KeyCodes2();
                Log.d(TAG, "buildUI: KeyCodes2反序列化文件失败");
            }
            try {
                mKeyCodes3 = KeyCodes3.deserialize(requireContext());
            } catch (IOException | ClassNotFoundException e) {
                mKeyCodes3 = new KeyCodes3();
                Log.d(TAG, "buildUI: 反序列化文件失败");
            }
        }


        return super.onCreateDialog(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        //解决在viewpager中的edittext弹不出输入法的问题
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

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
        ScrollView scrollView = new ScrollView(c);
//        scrollView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        int dialogPadding = QH.px(c, RR.attr.dialogPaddingDp);
        scrollView.setPadding(dialogPadding, 0, dialogPadding, 0);
        LinearLayout rootView = new LinearLayout(c);
        rootView.setOrientation(VERTICAL);
        scrollView.addView(rootView);

        String tipsOverAll = getS(RR.cmCtrl_lgPressHint);
        if(((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity() instanceof XServerDisplayActivity)
            tipsOverAll = tipsOverAll.substring(0,tipsOverAll.indexOf('\n'));
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
        mPages[0] = new SubView1Mouse(c);
        mPages[1] = new SubView2Keys(c, mKeyCodes2, mKeyCodes3);
        mPages[2] = new SubView3Style(c);

        //fragmentAdapter在dialogfragment里有问题，就用普通视图adapter吧
        viewPager.setAdapter(new SubNormalPagerAdapter(3, mPages, new String[]{getS(RR.cmCtrl_tabMouse), getS(RR.cmCtrl_tabKeys), getS(RR.cmCtrl_tabStyle)}));
        tabLayout.setupWithViewPager(viewPager, false);
        return scrollView;
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
                try {
                    KeyCodes2.serialize(mKeyCodes2, requireContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    KeyCodes3.serialize(mKeyCodes3, requireContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (which == BUTTON_NEGATIVE) {
//            Snackbar.make(FabMenu.getMainFrameView((AppCompatActivity) requireActivity()), S.get(S.DriveD_ToastExitFail), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void callWhenFirstStart() {

    }

    @Override
    public String getTitle() {
        return getS(RR.cmCtrl_title);
    }
}

