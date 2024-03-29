package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getPreference;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.setDialogTooltip;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_CUSTOM_BTN_POS;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.AvailableKeysView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.BtnColAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.BtnColRecyclerView;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.model.JoyParams;
import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.KeyCodes3;
import com.example.datainsert.exagear.controls.model.OneCol;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.List;

public class SubView2Keys extends LinearLayout {
    private static final String TAG = "SubView2Keys";
    private final KeyCodes2 mKeyCodes2;
    private final KeyCodes3 mKeyCodes3;

    /**
     * 为自由位置或左右侧栏准备的按键编辑视图 0是左右 1是自由
     */
    private final LinearLayout[] mTwoWaysKeyGroups = new LinearLayout[2];
    /**
     * 左右侧栏的列回收视图，能获取到最新的oneCol数据
     */
    private final BtnColRecyclerView[] mTwoSideBars = new BtnColRecyclerView[2];
    int joyStickNum = 1;

    //≡☰
    public SubView2Keys(Context c, @NonNull KeyCodes2 keyCodes2, @NonNull KeyCodes3 keyCodes3) {
        super(c);
        setOrientation(VERTICAL);
        mKeyCodes2 = keyCodes2;
        mKeyCodes3 = keyCodes3;

//        //是否使用自定义操作模式(不使用会创建DefaultCTF）
//        getPreference().edit().putBoolean(PREF_KEY_USE_CUSTOM_CONTROL, true).apply();

        //按键在两侧还是画面上层
//        Spinner spinKeyPosType = new Spinner(c); //用unicode字符？ ≡☰ ⇌ ⇄
//        String[] spinOptions = new String[]{getS(RR.cmCtrl_s2_modeSide), getS(RR.cmCtrl_s2_modeFree)};
//        ArrayAdapter<String> spinKeyPosAdapter = new SpinArrayAdapterSmSize(c, android.R.layout.simple_spinner_item, spinOptions);
//        spinKeyPosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinKeyPosType.setAdapter(spinKeyPosAdapter);
//        spinKeyPosType.setSelection(getPreference().getBoolean(PREF_KEY_CUSTOM_BTN_POS, false) ? 1 : 0);
//        spinKeyPosType.setOnItemSelectedListener(new SimpleItemSelectedListener((parent, view, position, id) -> {
//            getPreference().edit().putBoolean(PREF_KEY_CUSTOM_BTN_POS, position == 1).apply();
//            mTwoWaysKeyGroups[position].setVisibility(VISIBLE);
//            mTwoWaysKeyGroups[(position + 1) % 2].setVisibility(GONE);
//        }));
//        spinKeyPosType.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
//        LinearLayout oneLineSpinKeyPos = getOneLineWithTitle(c, getS(RR.cmCtrl_s2_layoutMode), spinKeyPosType, false);
//        setDialogTooltip(oneLineSpinKeyPos.getChildAt(0), getS(RR.cmCtrl_s2_layoutModeTip));
//        addView(oneLineSpinKeyPos);

        //spinner在开全面屏的时候显示有问题，换radiobutton吧
        RadioButton rdBtnSideBar = new RadioButton(c);
        final int rdBtnSideBarID = View.generateViewId(); //自动分配一个id吧
        rdBtnSideBar.setId(rdBtnSideBarID);
        rdBtnSideBar.setText(getS(RR.cmCtrl_s2_modeSide));

        RadioButton rdBtnFreePos = new RadioButton(c);
        final int rdBtnFreePosID = View.generateViewId();
        rdBtnFreePos.setId(rdBtnFreePosID);
        rdBtnFreePos.setText(getS(RR.cmCtrl_s2_modeFree));
        RadioGroup radioKeyPos = new RadioGroup(c);

        radioKeyPos.setOrientation(HORIZONTAL);
        radioKeyPos.addView(rdBtnSideBar);
        radioKeyPos.addView(rdBtnFreePos);
        radioKeyPos.setOnCheckedChangeListener((group, checkedId) -> {
            int position = checkedId == rdBtnSideBarID ? 0 : 1;
            getPreference().edit().putBoolean(PREF_KEY_CUSTOM_BTN_POS, position == 1).apply();
            mTwoWaysKeyGroups[position].setVisibility(VISIBLE);
            mTwoWaysKeyGroups[(position + 1) % 2].setVisibility(GONE);
        });
        LinearLayout oneLineSpinKeyPos = QH.getOneLineWithTitle(c, getS(RR.cmCtrl_s2_layoutMode), radioKeyPos, true);
        setDialogTooltip(oneLineSpinKeyPos.getChildAt(0), getS(RR.cmCtrl_s2_layoutModeTip));
        addView(oneLineSpinKeyPos);

        //左侧栏按键和右侧栏按键
        LinearLayout linearSideColOuter = new LinearLayout(c);
        linearSideColOuter.setOrientation(VERTICAL);
        LinearLayout linearLeftSide = QH.getOneLineWithTitle(c, getS(RR.cmCtrl_s2_LSideTitle), getAddSideBarGroup(c, true), true);
        setDialogTooltip(linearLeftSide.getChildAt(0), getS(RR.cmCtrl_s2_sideTitleTip));
        linearSideColOuter.addView(linearLeftSide);
        LinearLayout linearRightSide = QH.getOneLineWithTitle(c, getS(RR.cmCtrl_s2_RSideTitle), getAddSideBarGroup(c, false), true);
        setDialogTooltip(linearRightSide.getChildAt(0), getS(RR.cmCtrl_s2_sideTitleTip));
        linearSideColOuter.addView(linearRightSide);

        mTwoWaysKeyGroups[0] = linearSideColOuter;
        addView(linearSideColOuter, QH.LPLinear.one().top().to());

        //按钮与容器画面重叠（思路有问题，这个废弃）
//        getPreference().getBoolean(PREF_KEY_BTN_ON_WIDGET, false);

        //自定义按键栏。点击显示对话框选择按键，关闭时序列化
        Button csPosKeyBtn = new Button(c);
        csPosKeyBtn.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        csPosKeyBtn.setText(getS(RR.cmCtrl_s2_selectBtn));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            csPosKeyBtn.setTextAppearance(android.R.style.TextAppearance_Material_Widget_Button_Borderless_Colored);
            csPosKeyBtn.setBackground(new RippleDrawable(ColorStateList.valueOf(0x44444444), null, csPosKeyBtn.getBackground()));
        }
        csPosKeyBtn.setOnClickListener(v -> {
            boolean[] condition = new boolean[mKeyCodes3.getKeyList().size()];
            for (int i = 0; i < mKeyCodes3.getKeyList().size(); i++)
                condition[i] = mKeyCodes3.getKeyList().get(i).isShow();
            AvailableKeysView allKeysView = new AvailableKeysView(getContext(), condition, mKeyCodes3.getJoyList().size());
            allKeysView.showMouseBtn();
            allKeysView.showWithinDialog((dialog, which) -> {
                //修改按键个数。判断当前按键是否已存在，不存在的话，按keycode大小插入进去吧
//                AvailableKeysView.updateKeyCodes3(mKeyCodes3, allKeysView);
                for (int i = 0; i < mKeyCodes3.getKeyList().size(); i++) {
                    OneKey oneKey = mKeyCodes3.getKeyList().get(i);
                    //如果由隐藏变为显示，那么属性初始化
                    if (!oneKey.isShow() && allKeysView.keySelect[i]) {
                        oneKey.clearPropertiesWhenShow();
                        oneKey.setMarginTop(0);
                        oneKey.setMarginLeft(0);
                    }
                    oneKey.setShow(allKeysView.keySelect[i]);
                }
                //摇杆按键个数同步
                while (allKeysView.joyStickNum < mKeyCodes3.getJoyList().size()) {
                    mKeyCodes3.getJoyList().remove(mKeyCodes3.getJoyList().size() - 1);
                }
                while (allKeysView.joyStickNum > mKeyCodes3.getJoyList().size()) {
                    mKeyCodes3.getJoyList().add(new JoyParams());
                }
            });
        });


        //getS(RR.cmCtrl_s2_FreePosTitle)
        mTwoWaysKeyGroups[1] = QH.getOneLineWithTitle(c, null, csPosKeyBtn, true);
        addView(mTwoWaysKeyGroups[1]);

        //设置按键布局方式 两种方式显示一个，另一个隐藏
        radioKeyPos.check(getPreference().getBoolean(PREF_KEY_CUSTOM_BTN_POS, false) ? rdBtnFreePosID : rdBtnSideBarID);


    }


    /**
     * 生成一个布局，用于管理侧栏按键布局
     */
    private View getAddSideBarGroup(Context c, boolean isLeft) {
        BtnColRecyclerView recyclerView = new BtnColRecyclerView(c, mKeyCodes2, isLeft);
        mTwoSideBars[isLeft ? 0 : 1] = recyclerView;
        //如果之前有记录，就读取并初始化(注意recyclerview内部的nextId也要初始化）
        recyclerView.initItemList(isLeft ? mKeyCodes2.getLeftSide() : mKeyCodes2.getRightSide());

        Button addBtn = new Button(c);
        addBtn.setText("+");
//        TooltipCompat.setTooltipText(addBtn, "新建一列按键");
        int btnWidth = AndroidHelpers.dpToPx(50);
        addBtn.setLayoutParams(new ViewGroup.LayoutParams(btnWidth, btnWidth));
        LinearLayout linearLayout = new LinearLayout(c);
        linearLayout.addView(addBtn);
        linearLayout.addView(recyclerView);

        //点击按钮添加新的一个空列，编辑该列时再添加按键
        addBtn.setOnClickListener(v -> {
            //submit需要新建一个列表，拷贝原列表
            BtnColAdapter adapter = recyclerView.getAdapter();
            List<OneCol> newList = adapter.getCurrentList();
            newList.add(new OneCol(new OneKey[0], recyclerView.getNewId()));
            adapter.submitList(newList);
            //更新model
            if (isLeft)
                mKeyCodes2.setLeftSide(newList);
            else
                mKeyCodes2.setRightSide(newList);

        });
//        //点击添加按钮，新建对话框或fragment？关闭时recyclerview重写submitList
//        addBtn.setOnClickListener(v -> {
//            AvailableKeysView dialogView = new AvailableKeysView(getContext());
//            dialogView.showMouseBtn();
//            dialogView.showWithinDialog((dialog, which) -> {
//                //submit需要新建一个列表，拷贝原列表
//                BtnColAdapter adapter = recyclerView.getAdapter();
//                List<OneCol> newList = adapter.getCurrentList();
//                newList.add(new OneCol(dialogView.getSelectedKeys(), recyclerView.getNewId()));
//                adapter.submitList(newList);
//                //更新model
//                if(isLeft)
//                    mKeyCodes2.setLeftSide(newList);
//                else
//                    mKeyCodes2.setRightSide(newList);
//            });
//
//        });

        return linearLayout;
    }

    /**
     * //之前貌似不是用的同一个实例，没法实时更新。只好关闭视图的时候统一保存一下keycode2了（现在已经改到实时更新了）
     * 而dialog的onclick监听是在detach调用之前，所以没法重写回收视图的detach实现
     */
    public void syncKeyCodes2() {
        mKeyCodes2.setLeftSide(mTwoSideBars[0].getAdapter().getCurrentList());
        mKeyCodes2.setRightSide(mTwoSideBars[1].getAdapter().getCurrentList());
    }

    /**
     * radiobutton 选中哪个布局模式 . 0是左右侧栏  1是自由布局
     */
    public void checkLayoutMode(int index) {

    }
}
