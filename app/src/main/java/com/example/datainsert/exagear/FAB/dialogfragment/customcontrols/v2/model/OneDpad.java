package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

public class OneDpad extends OneStick{
    OneDpad(){
        super(TYPE_DPAD);
        direction = WAY_8;
    }

    @Override
    protected void cloneSelfFields(TouchAreaModel ref) {
        if(ref.getClass().equals(OneDpad.class)){
            size = ((OneDpad) ref).size;
            direction = ((OneDpad) ref).direction;
        }
    }
}
