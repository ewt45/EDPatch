package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getOneLineWithTitle;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getPreference;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getTextViewWithText;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.setDialogTooltip;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_ON_WIDGET;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_CUSTOM_BTN_POS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_USE_CUSTOM_CONTROL;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.AvailableKeysView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.BtnColAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.BtnColRecyclerView;
import com.example.datainsert.exagear.FAB.widget.SimpleItemSelectedListener;
import com.example.datainsert.exagear.FAB.widget.SpinArrayAdapterSmSize;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.JoyStickBtn;
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

        //是否使用自定义操作模式(不使用会创建DefaultCTF）
        getPreference().edit().putBoolean(PREF_KEY_USE_CUSTOM_CONTROL, true).apply();

        //按键在两侧还是画面上层
        Spinner spinKeyPosType = new Spinner(c); //用unicode字符？ ≡☰ ⇌ ⇄
        String[] spinOptions = new String[]{getS(RR.cmCtrl_s2_modeSide), getS(RR.cmCtrl_s2_modeFree)};
        ArrayAdapter<String> spinKeyPosAdapter = new SpinArrayAdapterSmSize(c, android.R.layout.simple_spinner_item, spinOptions);
        spinKeyPosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinKeyPosType.setAdapter(spinKeyPosAdapter);
        spinKeyPosType.setSelection(getPreference().getBoolean(PREF_KEY_CUSTOM_BTN_POS, false) ? 1 : 0);
        spinKeyPosType.setOnItemSelectedListener(new SimpleItemSelectedListener((parent, view, position, id) -> {
            getPreference().edit().putBoolean(PREF_KEY_CUSTOM_BTN_POS, position == 1).apply();
            mTwoWaysKeyGroups[position].setVisibility(VISIBLE);
            mTwoWaysKeyGroups[(position + 1) % 2].setVisibility(GONE);
        }));
        spinKeyPosType.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        LinearLayout oneLineSpinKeyPos = getOneLineWithTitle(c, getS(RR.cmCtrl_s2_layoutMode), spinKeyPosType, false);
        setDialogTooltip(oneLineSpinKeyPos.getChildAt(0), getS(RR.cmCtrl_s2_layoutModeTip));
        addView(oneLineSpinKeyPos);

//        //按键在两侧还是画面上层(旧版，开关样式）自定义按键位置
//        Switch switchCsPos = new Switch(c);
//        switchCsPos.setText("自定义按键位置");
//        switchCsPos.setChecked(getPreference().getBoolean(PREF_KEY_CUSTOM_BTN_POS, false));
//        switchCsPos.setOnCheckedChangeListener((buttonView, isChecked) -> getPreference().edit().putBoolean(PREF_KEY_CUSTOM_BTN_POS, isChecked).apply());
//        setDialogTooltip(switchCsPos, "如果使用自定义按键位置，下方的侧栏按键键位和布局将不会生效。请进入容器后四指触屏->菜单->编辑按键，然后点击空白处添加按钮并移动按钮位置。设定好后再次点击空白处退出编辑");
//        addView(switchCsPos);

        //左侧栏按键和右侧栏按键
        LinearLayout linearSideColOuter = new LinearLayout(c);
        linearSideColOuter.setOrientation(VERTICAL);
        LinearLayout linearLeftSide = getOneLineWithTitle(c, getS(RR.cmCtrl_s2_LSideTitle), getAddSideBarGroup(c, true), true);
        setDialogTooltip(linearLeftSide.getChildAt(0), getS(RR.cmCtrl_s2_sideTitleTip));
        linearSideColOuter.addView(linearLeftSide);
        LinearLayout linearRightSide = getOneLineWithTitle(c, getS(RR.cmCtrl_s2_RSideTitle), getAddSideBarGroup(c, false), true);
        setDialogTooltip(linearRightSide.getChildAt(0), getS(RR.cmCtrl_s2_sideTitleTip));
        linearSideColOuter.addView(linearRightSide);

        mTwoWaysKeyGroups[0] = linearSideColOuter;
        addView(linearSideColOuter);

        //按钮与容器画面重叠（思路有问题，这个废弃）
        getPreference().getBoolean(PREF_KEY_BTN_ON_WIDGET, false);

        //自定义按键栏。点击显示对话框选择按键，关闭时序列化
        Button csPosKeyBtn = new Button(c);
        csPosKeyBtn.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        csPosKeyBtn.setText(getS(RR.cmCtrl_s2_selectBtn));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            csPosKeyBtn.setTextAppearance(android.R.style.TextAppearance_Material_Widget_Button_Borderless_Colored);
            csPosKeyBtn.setBackground(new RippleDrawable(ColorStateList.valueOf(0x44444444),null,csPosKeyBtn.getBackground()));
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
                    mKeyCodes3.getJoyList().add(new JoyStickBtn.Params());
                }
            });
        });


        //getS(RR.cmCtrl_s2_FreePosTitle)
        mTwoWaysKeyGroups[1] = getOneLineWithTitle(c, null, csPosKeyBtn, true);
        addView(mTwoWaysKeyGroups[1]);

        //设置按键布局方式 两种方式显示一个，另一个隐藏

        boolean customBtnPos = getPreference().getBoolean(PREF_KEY_CUSTOM_BTN_POS, false);
        mTwoWaysKeyGroups[0].setVisibility(customBtnPos ? GONE : VISIBLE);
        mTwoWaysKeyGroups[1].setVisibility(customBtnPos ? VISIBLE : GONE);


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
}
