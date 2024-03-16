package org.ewt45.customcontrols;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class InterfaceOverlayImpl implements InterfaceOverlay<FullscreenActivity, XserverView> {
    private static final String TAG = "InterfaceOverlayImpl";

    @Override
    public View attach(FullscreenActivity a, XserverView viewOfXServer) {

        FrameLayout frameRoot = new FrameLayout(a);
        frameRoot.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
//        frameRoot.setFocusable(true);
//        frameRoot.setFocusableInTouchMode(true);
//        frameRoot.requestFocus();

        //编辑布局
        //TODO 新建SampleButn的时候，可以新建一个空的自己的抽象类而不用判断viewOfXserver是否为null了
        // 把Keyboard类里小键盘的那个映射给删了
        // 添加按钮：1:您要添加几个按键？ 2:选择按键的键码 （可不选） 3:分配已选的键码，剩余的分配空的键码

        FrameLayout frameEditRoot = new FrameLayout(a);
        frameEditRoot.setOnClickListener(v -> {
            Log.d(TAG, "setOnClickListener: ");
            v.showContextMenu();
        });
        frameEditRoot.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            Log.d(TAG, "setOnCreateContextMenuListener: ");
            menu.setHeaderTitle("自定义操作模式 - 编辑模式");
            menu.add("添加按键").setOnMenuItemClickListener(item -> {
                //TODO 新建一个toucharea放左上角
                showAddBtnDialog(a, viewOfXServer);
                
                return true;
            });
            menu.add("全局设置").setOnMenuItemClickListener(item -> {

                return true;
            });
            menu.add("退出编辑").setOnMenuItemClickListener(item -> {

                return true;
            });
        });

        frameRoot.addView(frameEditRoot, new FrameLayout.LayoutParams(-1, -1));
        return frameRoot;
    }

    private void showAddBtnDialog(FullscreenActivity a, XserverView viewOfXServer) {
        LinearLayout linearRoot = new LinearLayout(a);
        linearRoot.setOrientation(LinearLayout.VERTICAL);

        //按钮类型
        RadioGroup radioGroupType = new RadioGroup(a);
        radioGroupType.setOrientation(LinearLayout.HORIZONTAL);

        final String[] radioTypeStrs = new String[]{"按钮", "摇杆", "十字键"};
        final int[] radioTypeFlag = new int[]{Const.BTN_TYPE_NORMAL, Const.BTN_TYPE_STICK, Const.BTN_TYPE_DPAD};
        final int[] radioBtnIds = new int[]{0, 0, 0};
        for (int i = 0; i < radioTypeStrs.length; i++) {
            RadioButton radioBtn = new RadioButton(a);
            radioBtn.setText(radioTypeStrs[i]);
            radioBtn.setId(View.generateViewId());
            radioBtnIds[i] = radioBtn.getId();
            radioGroupType.addView(radioBtn);
        }
        radioGroupType.clearCheck();
        radioGroupType.check(radioBtnIds[0]);

        //按钮数量
        SeekBar seekNum = new SeekBar(a);
        seekNum.setMax(9);
        LinearLayout linearSeek = QH.getOneLineWithTitle(a, "按钮数量: %d", seekNum, true);
        seekNum.setOnSeekBarChangeListener((QH.SimpleSeekChangeListener) (seekBar, progress, fromUser) ->
                ((TextView) linearSeek.getChildAt(0)).setText(String.format("按钮数量: %d", progress + 1)));
        int initNum = 0;
        seekNum.setProgress((initNum+1)%9);
        seekNum.setProgress(initNum);

        linearRoot.addView(QH.getOneLineWithTitle(a, "按钮类型", radioGroupType, true),QH.LPLinear.one(a).top().to());
        linearRoot.addView(linearSeek,QH.LPLinear.one(a).top().to());

        new AlertDialog.Builder(a)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, ((dialog, which) -> {
                    RadioButton selectTypeBtn = radioGroupType.findViewById(radioGroupType.getCheckedRadioButtonId());
                    int type = radioTypeFlag[radioGroupType.indexOfChild(selectTypeBtn)];

                }))
                .setView(QH.wrapAsDialogScrollView(linearRoot))
                .setCancelable(false)
                .setTitle("添加按键")
                .show();
    }

    private void changeTextOfNum(View tv, int progress) {
        ((TextView) tv).setText(String.format("按钮数量: %d", progress + 1));
    }

    private RadioButton getRadioBtnType(Context a, String text) {
        RadioButton radioBtn = new RadioButton(a);
        radioBtn.setText(text);
        radioBtn.setId(View.generateViewId());
        return radioBtn;
    }

    @Override
    public void detach() {

    }
}
