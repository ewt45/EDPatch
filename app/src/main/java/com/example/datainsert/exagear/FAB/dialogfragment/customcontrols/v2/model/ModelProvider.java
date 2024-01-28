package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchArea.TouchAreaButton;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchArea.TouchAreaDpad;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchArea.TouchAreaGesture;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchArea.TouchAreaStick;

import java.util.Arrays;

public class ModelProvider {
    public static final int[] modelTypeInts = new int[]{
            TouchAreaModel.TYPE_BUTTON,
            TouchAreaModel.TYPE_STICK,
            TouchAreaModel.TYPE_DPAD,
            TouchAreaModel.TYPE_GESTURE,};
    public static final Class<? extends TouchAreaModel>[] modelClasses = new Class[]{
            OneButton.class,
            OneStick.class,
            OneDpad.class,
            OneGestureArea.class};
    public static final Class<? extends TouchArea<?>>[] areaClasses = new Class[]{
            TouchAreaButton.class,
            TouchAreaStick.class,
            TouchAreaDpad.class,
            TouchAreaGesture.class
    };

    public static Class<? extends TouchArea<? extends TouchAreaModel>> getAreaClass(Class<? extends TouchAreaModel> modelClass) {
           return areaClasses[indexOf(modelClasses,modelClass)];
    }

    public static <T>int indexOf(T[] arr, T var){
        for(int i=0 ; i<arr.length; i++)
            if(arr[i].equals(var))
                return i;
       throw new RuntimeException("无法在数组中找到元素："+ var+", "+Arrays.toString(arr));
    }


}
