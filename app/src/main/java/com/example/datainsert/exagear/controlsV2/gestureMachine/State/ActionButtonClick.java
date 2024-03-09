package com.example.datainsert.exagear.controlsV2.gestureMachine.State;

import static com.example.datainsert.exagear.controlsV2.TestHelper.getTextButton;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.widget.KeyOnBoardView;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;

@StateTag(tag = FSMR.state.操作_点击, isAction = true)
public class ActionButtonClick extends FSMAction2 {
    @SerializedName(value = Const.GsonField.st_keycode)
    public int mKeycode = 0;
    @SerializedName(value = Const.GsonField.st_doPress)
    public boolean mDoPress = true;
    @SerializedName(value = Const.GsonField.st_doRelease)
    public boolean mDoRelease = true;

    @Override
    protected void onAttach() {
    }

    @Override
    public void run() {
        if (mDoPress)
            Const.getXServerHolder().pressKeyOrPointer(mKeycode);
        if (mDoRelease)
            Const.getXServerHolder().releaseKeyOrPointer(mKeycode);
    }

    @Override
    public View createPropEditView(Context c) {
        TextView tvKeycodes = getTextButton(c, Const.keyNames[mKeycode]);
        tvKeycodes.setOnClickListener(v -> {
            KeyOnBoardView keyOnBoardView = new KeyOnBoardView(c);
            keyOnBoardView.setInitSelectedKeys(Collections.singletonList(mKeycode));
            keyOnBoardView.setOnlyAllowOne(true);
            new AlertDialog.Builder(v.getContext())
                    .setView(keyOnBoardView)
                    .setNegativeButton(android.R.string.cancel,null)
                    .setPositiveButton(android.R.string.ok,(dialog, which) -> {
                        mKeycode = keyOnBoardView.getSelectedKeys().get(0);
                        tvKeycodes.setText(Const.keyNames[mKeycode]);
                    })
                    .show();
        });

        String[] pressReleaseStr = RR.getSArr(RR.ctr2_stateProp_PrsRlsCheck);
        CheckBox checkDoPress = new CheckBox(c);
        checkDoPress.setText(pressReleaseStr[0]);//按下
        checkDoPress.setChecked(mDoPress);
        checkDoPress.setOnCheckedChangeListener((buttonView, isChecked) -> mDoPress = isChecked);

        CheckBox checkDoRelease = new CheckBox(c);
        checkDoRelease.setText(pressReleaseStr[1]);//松开
        checkDoRelease.setChecked(mDoRelease);
        checkDoRelease.setOnCheckedChangeListener((buttonView, isChecked) -> mDoRelease = isChecked);

        return createEditViewQuickly(c,
                new String[][]{{RR.getS(RR.global_keycode),null},{"",null},{"",null}},
                new View[]{tvKeycodes,checkDoPress,checkDoRelease});
    }
}
