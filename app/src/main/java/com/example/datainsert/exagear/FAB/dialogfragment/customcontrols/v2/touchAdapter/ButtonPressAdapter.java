package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter;

import com.eltechs.axs.xserver.ViewFacade;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.XKeyInjector;

import java.util.List;

public class ButtonPressAdapter implements TouchAdapter {
    private final ViewFacade facade;
    List<Integer> mKeys;
    boolean isTouched=false;
    public ButtonPressAdapter(List<Integer> keys){
        this.facade = Const.getViewFacade();
        mKeys = keys;
    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {

    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        XKeyInjector.release(facade,mKeys);
        isTouched=false;
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        if (isTouched)
            return;

        isTouched=true;
        XKeyInjector.press(facade,mKeys);
    }
}
