package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controls.axs.gamesControls.FalloutInterfaceOverlay2;
import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.KeyCodes3;

public class ControlsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        XServerDisplayActivityConfigurationAware aware = Globals.getApplicationState();
//        if (aware != null)
//            mUiOverlay = ((FalloutInterfaceOverlay2) aware.getXServerDisplayActivityInterfaceOverlay());
//
//        if (mUiOverlay != null) {
//            mKeyCodes2 = mUiOverlay.getControlsFactory().getKeyCodes2();
//            mKeyCodes3 = mUiOverlay.getControlsFactory().getKeyCodes3();
//        } else {
//            mKeyCodes2 = KeyCodes2.read(requireContext());
//            mKeyCodes3 = KeyCodes3.read(requireContext());
//        }

        if(QH.isTesting())
            requireActivity().findViewById(R.id.ed_main_toolbar).setVisibility(View.GONE);

        Context c = requireContext();
        //TODO 必须在任何操作前初始化一次
        Const.init(c);
        int frameRootId = View.generateViewId();
        FrameLayout frameRoot = new FrameLayout(c);
        frameRoot.setId(frameRootId);

        TouchAreaView touchAreaView = new TouchAreaView(c);
        touchAreaView.startEdit();
        frameRoot.addView(touchAreaView);

        return frameRoot;
//        return QH.wrapAsDialogScrollView(buildUI());
    }
}
