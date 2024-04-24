package com.example.datainsert.exagear.controlsV2.edit.props;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.model.OneColumn;
import com.example.datainsert.exagear.controlsV2.widget.KeyOnBoardView;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;

import java.util.ArrayList;
import java.util.List;

public class Prop5Keys extends Prop<TouchAreaModel> {
    private LinearLayout linearAllKeys;

    public Prop5Keys(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    public String getTitle() {
        return RR.getS(RR.global_keycode);
    }

    @Override
    protected View createMainEditView(Context c) {
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.VERTICAL);

        TextView btnSelect = QH.TV.one(c).button().text("重新选择按键").text16Sp().textGravity(Gravity.START).to();
        btnSelect.setOnClickListener(v -> {
            TouchAreaModel model = mHost.getModel();
            KeyOnBoardView keyOnBoardView = new KeyOnBoardView(v.getContext());
            keyOnBoardView.setInitSelectedKeys(model.getKeycodes());
            new AlertDialog.Builder(v.getContext())
                    .setView(keyOnBoardView)
                    .setNegativeButton(android.R.string.cancel,null)
                    .setPositiveButton(android.R.string.ok,(dialog, which) -> {
                        model.setKeycodes(keyOnBoardView.getSelectedKeys());
                        updateUIFromModel(model);
                        onWidgetListener();
                    })
                    .show();
        });

        linearAllKeys = new LinearLayout(c);
        linearAllKeys.setOrientation(LinearLayout.VERTICAL);

        linearRoot.addView(btnSelect);
        linearRoot.addView(linearAllKeys);
        return linearRoot;
    }

    @Override
    protected View createAltEditView(Context c) {
        return null;
    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        if (!(model instanceof OneColumn))
            return;

        OneColumn data = (OneColumn) model;

        //删除多余的子视图
        while (linearAllKeys.getChildCount() > data.getKeycodes().size()) {
            linearAllKeys.removeViewAt(linearAllKeys.getChildCount() - 1);
        }

        List<Integer> tmpList = new ArrayList<>();
        //遍历检查每个keycode及其名称是否有变化
        // 如果有变化，且当前序号已经有对应视图了，则不新建view，直接在旧的上改。如果超出当前线性布局的子视图个数则新建。
        for (int i = 0; i < data.getKeycodes().size(); i++) {
            Integer keycode = data.getKeycodes().get(i);
            //新建一行view
            if (i >= linearAllKeys.getChildCount()) {
                KeyAndNameView newLine = new KeyAndNameView(linearAllKeys.getContext());
                linearAllKeys.addView(newLine, QH.LPLinear.one(-1, -2).top().to());
            }

            KeyAndNameView line = (KeyAndNameView) linearAllKeys.getChildAt(i);
            tmpList.clear();
            tmpList.add(keycode);
            line.tvKey.setText(TouchAreaModel.getKeycodesString(tmpList));
            line.keycode = keycode;
            line.editName.setStringValue(data.getNameAt(i));
        }
    }

    private class KeyAndNameView extends LinearLayout {
        TextView tvKey;
        LimitEditText editName;
        Integer keycode;

        public KeyAndNameView(Context c) {
            super(c);
            setOrientation(HORIZONTAL);
            tvKey = QH.TV.one(c).text16Sp().solidColor().to();
            editName = new LimitEditText(c)
                    .setCustomInputType(LimitEditText.TYPE_TEXT_SINGLE_LINE)
                    .setUpdateListener(editText -> {
                        if (!(mHost.getModel() instanceof OneColumn)) return;
                        ((OneColumn) mHost.getModel()).setNameByKeycode(keycode, editName.getStringValue());
                        onWidgetListener();
                    });
            addView(tvKey, QH.LPLinear.one(0, -2).weight().to());
            addView(editName, QH.LPLinear.one(0, -2).weight().to());
        }
    }

}
