package com.example.datainsert.exagear.containerSettings.otherargv;

import static com.example.datainsert.exagear.RR.dimen.margin8Dp;
import static com.example.datainsert.exagear.RR.dimen.minCheckSize;
import static com.example.datainsert.exagear.containerSettings.ConSetOtherArgv.KEY_TASKSET;
import static com.example.datainsert.exagear.containerSettings.ConSetOtherArgv.VAL_TASKSET_DEFAULT;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.util.ArrayList;
import java.util.List;

class AdapterHelper {
    /**
     * @param viewGroup  仅用于获取context
     * @param wrapHeight 是否高度为紧凑型（参数组的子item时）
     * @return onCreate和onBind的子item都可以从这获取
     */
     static @NonNull LinearLayout getOneItemView(ViewGroup viewGroup, boolean wrapHeight) {
        Context c = viewGroup.getContext();
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(minCheckSize(), wrapHeight ? -2 : minCheckSize());
        checkParams.gravity = Gravity.CENTER;
        LinearLayout.LayoutParams linearTextsParams = new LinearLayout.LayoutParams(0, -2);
        linearTextsParams.gravity = Gravity.CENTER_VERTICAL;
        linearTextsParams.weight = 1;
        linearTextsParams.topMargin = wrapHeight ? 0 : margin8Dp();
        linearTextsParams.bottomMargin = wrapHeight ? 0 : margin8Dp();

        //勾选框
        CheckBox checkBox = new CheckBox(c);
        checkBox.setId(android.R.id.checkbox);
        RelativeLayout relativeCheck = new RelativeLayout(c);
        relativeCheck.setGravity(Gravity.CENTER);
        relativeCheck.addView(checkBox, new ViewGroup.LayoutParams(-2, -2));
        relativeCheck.setOnClickListener(v -> checkBox.performClick());

        //两行文本
        TextView text1 = new TextView(c);//(TextView) LayoutInflater.from(c).inflate(android.R.layout.simple_list_item_1, linearRoot, false);
        text1.setTextColor(RR.attr.textColorPrimary(c));
        text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        text1.setId(android.R.id.text1);

        TextView text2 = new TextView(c);
        text2.setId(android.R.id.text2);
        text2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        LinearLayout linearTexts = new LinearLayout(c);
        linearTexts.setOrientation(LinearLayout.VERTICAL);
        linearTexts.addView(text1);
        linearTexts.addView(text2);

        //菜单按钮
        ImageButton btnMenu = new ImageButton(c);
        btnMenu.setId(android.R.id.button1);
        btnMenu.setImageResource(RR.drawable.ic_more_vert_24dp());
        btnMenu.setBackground(RR.attr.selectableItemBackground(c));

        LinearLayout linearPart1 = new LinearLayout(c);
        linearPart1.setOrientation(LinearLayout.HORIZONTAL);
        linearPart1.addView(relativeCheck, checkParams);
        linearPart1.addView(linearTexts, linearTextsParams);
        linearPart1.addView(btnMenu, checkParams);

        LinearLayout linearPart2 = new LinearLayout(c);
        linearPart2.setOrientation(LinearLayout.VERTICAL);
        linearPart2.setPadding(QH.px(c, 24), 0, 0, 0);
//        linearPart2.setScaleX(0.9f);
//        linearPart2.setScaleY(0.9f);

        linearRoot.addView(linearPart1);
        linearRoot.addView(linearPart2);
        linearRoot.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        linearRoot.setLayoutTransition(new LayoutTransition());
        return linearRoot;
    }

    @SuppressLint("SetTextI18n")
    static void addCpuCoresPart2(AllArgsAdapter.ViewHolder holder, int contId, Argument argument){
        Context c = holder.root.getContext();
        GridLayout gridCores = new GridLayout(c);
        gridCores.setColumnCount(100);
        gridCores.setRowCount(1);
        gridCores.setUseDefaultMargins(true);

        //获取全部可用的核心，以及当前设置的核心
        int[] availableCores = new int[]{0, 1, 2, 3, 4, 5, 6, 7}; //既然armv9在任务管理器里也能手动勾选，那就不限制了，默认0-7都可以选择把
        List<Integer> currCores = new ArrayList<>();
        String currTaskSetStr = QH.getContPref(contId).getString(KEY_TASKSET, VAL_TASKSET_DEFAULT);
        assert currTaskSetStr != null;
        for (String s : currTaskSetStr.split(","))
            if (s.matches("[0-9]+")) currCores.add(Integer.parseInt(s));//可能有空字符串的情况，所以需要判断

        //注意checkbox的index不一定等于cpu核心的index。应以checkbox的text为准
        for (int i : availableCores) {
            CheckBox checkBox = new CheckBox(c);
            checkBox.setText("" + i);
            checkBox.setChecked(currCores.contains(i));
            checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
                StringBuilder builder = new StringBuilder();
                for (int allCheckInd = 0; allCheckInd < gridCores.getChildCount(); allCheckInd++) {
                    CheckBox checkCore = (CheckBox) gridCores.getChildAt(allCheckInd);
                    builder.append(checkCore.isChecked() ? "," + checkCore.getText() : "");
                }
                argument.setArg((builder.length() > 1 ? builder.deleteCharAt(0) : builder).toString());
            });
            gridCores.addView(checkBox);
        }

        HorizontalScrollView scrollView = new HorizontalScrollView(c);
        scrollView.addView(gridCores);
        holder.linearPart2.removeAllViews();
        holder.linearPart2.addView(scrollView);
    }
}
