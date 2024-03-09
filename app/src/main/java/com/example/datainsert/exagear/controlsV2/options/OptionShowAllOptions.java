package com.example.datainsert.exagear.controlsV2.options;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.RR;

/**
 * 显示全部可用的选项
 */
public class OptionShowAllOptions extends AbstractOption{

    AlertDialog mDialog;
    @SuppressLint("SetTextI18n")
    @Override
    public void run() {
        Log.d("TAG", "run: 显示全部选项");
        assert Looper.myLooper() == Looper.getMainLooper();

        Context c = Const.getContext();


        LinearLayout linearControlOuter = new LinearLayout(c);
        linearControlOuter.setOrientation(LinearLayout.VERTICAL);
        linearControlOuter.setVerticalGravity(Gravity.CENTER_VERTICAL);
        final String customControlTitle = RR.getS(RR.ctr2_customControls2);
        TextView tvControl = TestHelper.getTextButton(Const.getContext(),"▶ "+customControlTitle);//▶ ▼

        LinearLayout linearControlPart2 = new LinearLayout(c);
        linearControlPart2.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams part2Params = new LinearLayout.LayoutParams(-1,0);
        linearControlPart2.setLayoutParams(part2Params);
        linearControlPart2.addView(addOption(new OptionEditInputControl()));
        linearControlPart2.addView(addOption(new OptionTouchAreaDisplay()));

        tvControl.setOnClickListener(v->{
            boolean isShowing = part2Params.height!=0;
            tvControl.setText((isShowing?"▶ ":"▼ ")+customControlTitle);
            part2Params.height=isShowing?0:-2;
            linearControlPart2.setLayoutParams(part2Params);
        });

        linearControlOuter.addView(tvControl);
        linearControlOuter.addView(linearControlPart2);

        TextView tvCancel = TestHelper.getTextButton(c,c.getString(android.R.string.cancel));
        tvCancel.setOnClickListener(v-> runOptionAndCloseDialog(null));

        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.VERTICAL);
        linearRoot.setDividerDrawable(RR.attr.listDivider(c));
        linearRoot.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        linearRoot.setLayoutTransition(layoutTransition);
        linearRoot.addView(linearControlOuter);
        linearRoot.addView(addOption(new OptionToggleSoftInput()));
        linearRoot.addView(addOption(new OptionQuit()));
        linearRoot.addView(tvCancel);

        mDialog=new AlertDialog.Builder(c)
                .setView(linearRoot)
                .show();

        //为啥popupMenu不显示呢，是anchor有问题吗
//        PopupMenu popupMenu = new PopupMenu(Const.activityRef.get(), view,Gravity.LEFT|Gravity.TOP);
//        popupMenu.getMenu().add("测试");
//        popupMenu.getMenu().add("这里应该显示全部可用选项");
//        popupMenu.show();
    }

    private void runOptionAndCloseDialog(AbstractOption option){
        if(option!=null)
            option.run();
        if(mDialog!=null)
            mDialog.dismiss();
        mDialog = null;
    }

    private TextView addOption(AbstractOption option){
        TextView tv = TestHelper.getTextButton(Const.getContext(),option.getName());
        tv.setOnClickListener(v->runOptionAndCloseDialog(option));
        return tv;
    }


    @Override
    public String getName() {
        return RR.getS(RR.ctr2_option_showAll);
    }
}
