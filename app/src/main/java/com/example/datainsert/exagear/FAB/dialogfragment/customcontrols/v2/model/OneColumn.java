package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import android.animation.TypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * 一列按钮组。宽=列宽，高=单个按钮高
 */
public class OneColumn extends TouchAreaModel{
    public OneColumn(){
        super();

    }
    private final List<OneButton> btnList = new ArrayList<>();

    public List<OneButton> getBtnList() {
        return btnList;
    }

    public void setBtnList(List<OneButton> newList){
        btnList.clear();
        btnList.addAll(newList);
    }

    public void addBtn(OneButton btn){
        if(!btnList.contains(btn))
            btnList.add(btn);
    }

    public void removeBtn(OneButton btn){
        btnList.remove(btn);
    }
}
