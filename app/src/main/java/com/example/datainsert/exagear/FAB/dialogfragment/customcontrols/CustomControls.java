package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.widget.LinearLayout.VERTICAL;

import static com.example.datainsert.exagear.RR.cmCtrl_tabOther;
import static com.example.datainsert.exagear.RR.getS;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.Toast;

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
import com.example.datainsert.exagear.controls.model.FormatHelper;
import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.KeyCodes3;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CustomControls extends BaseFragment implements DialogInterface.OnClickListener {
    public static final String TAG = "CustomControls";
    private final BtnColRecyclerView[] mSidebarKeyRecyclerView = new BtnColRecyclerView[2];
    ViewGroup[] mPages;
    FalloutInterfaceOverlay2 mUiOverlay;
    //    private File mSerFile2;
    private KeyCodes2 mKeyCodes2;//????????????dialog???????????????????????????????????????
//    private File mSerFile3;
    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
        Log.d(TAG, "onCreateDialog: ????????????fragment?????????????????????????????????????????????onCreateDialog???");//??????????????????
//        if(mDialog==null)
//            mDialog=this;

        //????????????factory??????????????????????????????????????????????????????????????????????????????
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
        //?????????viewpager??????edittext???????????????????????????
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        //?????????????????????
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        //??????????????????(??????????????????Dialog???
        Log.d(TAG, "onStart: getdialog=" + getDialog());
//        ((AlertDialog)getDialog()).setButton(BUTTON_POSITIVE, getString(android.R.string.yes),this);


//        Window window = getDialog().getWindow();
//        //??????decorview?????????
//        int padding = QH.dp(requireContext(),16);
//        window.getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
//        window.getAttributes().gravity = Gravity.CENTER;
//        window.setAttributes(window.getAttributes());
        //????????????(????????????????????????
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
//        if (((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity() instanceof XServerDisplayActivity)
//            tipsOverAll = tipsOverAll.substring(0, tipsOverAll.indexOf('\n'));
        rootView.addView(getTextViewWithText(c, tipsOverAll));

        //?????????????????????
        TabLayout tabLayout = new TabLayout(requireContext());
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        ViewPager viewPager = new WrapContentViewPager(requireContext());
        LinearLayout.LayoutParams viewPagerParams = new LinearLayout.LayoutParams(-1, -1);
        viewPagerParams.setMargins(0, 20, 0, 20);
//        viewPager.setLabelFor(View.NO_ID);
//        viewPager.setId(VIEWPAGER_RESOURCE_ID);
        rootView.addView(tabLayout, new ViewGroup.LayoutParams(-1, -2));
        rootView.addView(viewPager, viewPagerParams);


        //???????????????
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

        //fragmentAdapter???dialogfragment?????????????????????????????????adapter???
        viewPager.setAdapter(new SubNormalPagerAdapter(mPages, tabTitles));
        tabLayout.setupWithViewPager(viewPager, false);
        return scrollView;
    }


    /**
     * ??????????????????????????????
     *
     * @param dialog the dialog that received the click
     * @param which  the button that was clicked (ex.
     *               {@link DialogInterface#BUTTON_POSITIVE}) or the position
     *               of the item clicked
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "onClick: detach??????onclick???????????????");

//        Log.d(TAG, "onClick: getdialog?????????????????????"+ getDialog());
        //????????????????????????????????????????????????????????????keycode2???3??????????????????????????????
        if (which == BUTTON_POSITIVE) {

            //????????????????????????????????????tscWidget???onLayout?????????????????????????????????????????????
            if (mUiOverlay != null && ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity() instanceof XServerDisplayActivity) {
                mUiOverlay.refreshControlUI();
                Log.d(TAG, "onClick: ?????????????????????????????????keycode?????? ????????????");
            }
            //?????????????????????
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
            public void exportData() {
                String data = FormatHelper.dataExport(mKeyCodes2, mKeyCodes3);
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getS(RR.cmCtrl_title), data);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), getS(RR.cmCtrl_s4_exportResult), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void importData() {
                try {
                    ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    String data = "";
                    if (!(clipboard.hasPrimaryClip())) {
                        throw new Exception("?????????????????????");
                    } else if (!(Objects.requireNonNull(clipboard.getPrimaryClipDescription()).hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                        throw new Exception("?????????????????????????????????");
                        // This disables the paste menu item, since the clipboard has data but it is not plain text
                    }

                    // Examines the item on the clipboard. If getText() does not return null, the clip item contains the text.
                    // Assumes that this application can only handle one item at a time.
                    ClipData.Item item = Objects.requireNonNull(clipboard.getPrimaryClip()).getItemAt(0);
                    data = (String) item.getText(); // Gets the clipboard as text.
                    if (data == null)
                        throw new Exception("??????????????????????????????????????????????????????URI???");// The clipboard does not contain text. If it contains a URI, attempts to get data from it

                    //???????????????????????????
                    FormatHelper.dataImport(data);
                    //????????????????????????????????????????????????????????????????????? (????????????????????????????????????????????????model???????????????????????????????????????????????????????????????
//                    mKeyCodes2 = KeyCodes2.read(requireContext());
//                    mKeyCodes3 = KeyCodes3.read(requireContext());
                    getDialog().dismiss();//????????????????????????positive button???onclicklisntenr?????????
                    Toast.makeText(requireContext(), getS(RR.cmCtrl_s4_importResult).split("\\$")[0], Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), getS(RR.cmCtrl_s4_importResult).split("\\$")[1], Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void callWhenFirstStart() {

    }

    @Override
    public String getTitle() {
        return getS(RR.cmCtrl_title);
    }
}

