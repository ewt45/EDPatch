package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper.getTextButton;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneButton;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;
import com.example.datainsert.exagear.QH;

public class KeySub1Normal implements KeySubView<OneButton> {
    OneButton mModel = OneButton.newInstance(null);
    Binding mBinding = new Binding();

    @Override
    public void updateUI(OneButton model) {
        if (mModel != model)
            mModel = model;
        mBinding.tvKeycodes.setText(mModel.getKeycodesString());
        ((RadioButton) mBinding.groupShape.getChildAt(mModel.shape)).setChecked(true);
    }

    @Override
    public ViewGroup inflate(KeyPropertiesView host) {
        if (mBinding.root != null)
            return mBinding.root;

        Context c = host.getContext();

        //TODO 新建触摸区域后，这里的model没刷新？
        //形状
        HorizontalScrollView scrollGroupShape = host.buildOptionsGroup(
                new String[]{"矩形", "圆形"},
                new int[]{Const.BtnShape.RECT, Const.BtnShape.OVAL},
                (group, btn, intValue) -> mModel.shape = intValue);

        //按键码
        TextView tvKeycodes = getTextButton(c, "");
        tvKeycodes.setOnClickListener(v -> {
            new AlertDialog.Builder(c)
                    .setView(new KeyOnBoardView(c))
                    .show();
        });


        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.VERTICAL);
        linearRoot.addView(QH.getOneLineWithTitle(c, "形状", scrollGroupShape, false));
        linearRoot.addView(QH.getOneLineWithTitle(c, "按键码", tvKeycodes, false));

        mBinding.root = linearRoot;
        mBinding.groupShape = (RadioGroup) scrollGroupShape.getChildAt(0);
        mBinding.tvKeycodes = tvKeycodes;

        return mBinding.root;
    }

    @Override
    public OneButton adaptModel(TouchAreaModel reference) {
        return OneButton.newInstance(reference);
    }


    private static class Binding {
        ViewGroup root;
        TextView tvKeycodes;
        RadioGroup groupShape;
    }
}
