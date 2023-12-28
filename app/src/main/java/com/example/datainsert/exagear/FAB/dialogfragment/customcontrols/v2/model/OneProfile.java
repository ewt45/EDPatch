package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;

import java.util.ArrayList;
import java.util.List;

public class OneProfile {
    int alpha=255;
    final List<TouchArea<? extends TouchAreaModel>> touchAreaList = new ArrayList<>();


    public List<TouchArea<? extends TouchAreaModel>> getTouchAreaList() {
        return touchAreaList;
    }

    public void addTouchArea(TouchArea<? extends TouchAreaModel> touchArea){
        touchAreaList.add(0,touchArea);
    }
    public void addTouchAreaList(List<TouchArea<? extends TouchAreaModel>> list){
        touchAreaList.addAll(0,list);
    }
}