package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getOneLineWithTitle;
import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.datainsert.exagear.FAB.widget.SimpleTextWatcher;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.model.OneKey;

/**
 * 用于编辑单个普通按钮的属性的视图
 * 重命名，组合键，长按
 */
public class BtnPropertiesView extends LinearLayout {

    private final OneKey mOneKey;
    public BtnPropertiesView(Context context, OneKey oneKey, boolean isFreePos) {
        super(context);
        mOneKey = oneKey;

        int padding = QH.px(getContext(), RR.attr.dialogPaddingDp);
        setPadding(padding,0,padding,0);
        inflateUI();
    }


    /**
     * 初始化自身视图
     */
    private void inflateUI(){
        setOrientation(VERTICAL);
        //重命名
        Context c = getContext();
        EditText editText = new EditText(c);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setSingleLine();
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setText(mOneKey.getName());
        editText.setLayoutParams(new ViewGroup.LayoutParams(QH.px(c, 100), -2));
        editText.addTextChangedListener((SimpleTextWatcher)s -> mOneKey.setName(s.toString()));
        LinearLayout renameLinear = getOneLineWithTitle(c, getS(RR.cmCtrl_BtnEditReName), editText, false);
        addView(renameLinear);

        //组合键
        Button selectKeyBtn = new Button(c);
        selectKeyBtn.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        selectKeyBtn.setText(getS(RR.cmCtrl_s2_selectBtn));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            selectKeyBtn.setTextAppearance(android.R.style.TextAppearance_Material_Widget_Button_Borderless_Colored);
            selectKeyBtn.setBackground(new RippleDrawable(ColorStateList.valueOf(0x44444444),null,selectKeyBtn.getBackground()));
        }
        selectKeyBtn.setOnClickListener(v -> {

            boolean[] condition = new boolean[AvailableKeysView.codes.length];
            //预选中自身全部组合键
            for(int subKeycode:mOneKey.getSubCodes())
                for(int i=0; i<condition.length;i++)
                    if(AvailableKeysView.codes[i]==subKeycode)
                        condition[i]=true;


            AvailableKeysView allKeysView = new AvailableKeysView(getContext(), condition, -1);
            allKeysView.showMouseBtn();
            allKeysView.showWithinDialog((dialog, which) -> {
                //将选中的按键设置为自身model的组合键
                mOneKey.getSubCodes().clear();
                for(int i=0; i<allKeysView.keySelect.length;i++)
                    if(allKeysView.keySelect[i])
                        mOneKey.getSubCodes().add(AvailableKeysView.codes[i]);
            });
        });

        addView(getOneLineWithTitle(c,getS(RR.cmCtrl_BtnEditComb), selectKeyBtn,false));

        //点击一次 = 保持按下
        CheckBox checkTrigger = new CheckBox(c);
        checkTrigger.setText(getS(RR.cmCtrl_BtnEditTrigger));
        checkTrigger.setChecked(mOneKey.isTrigger());
        checkTrigger.setOnCheckedChangeListener((buttonView, isChecked) -> mOneKey.setTrigger(isChecked));
        addView(checkTrigger);
    }

    /**
     * 将此视图显示到一个对话框中
     */
    public void showWithInDialog( @Nullable  DialogInterface.OnClickListener callback  ) {
        new AlertDialog.Builder(getContext())
                .setView(this)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,callback)
                .setNegativeButton(android.R.string.cancel,null)
                .create().show();
    }
}
