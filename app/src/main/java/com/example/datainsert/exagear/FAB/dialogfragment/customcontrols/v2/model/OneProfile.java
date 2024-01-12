package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import android.util.Log;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.adapter.EditMoveAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 存model列表和touchArea列表，每个model对应一个area，
 * model添加或删除只能通过这个类进行，且只能通过add和remove函数，直接操作列表会报错
 * model增删时会自动增删area
 * 从json反序列化出来之后记得调用sync函数同步area列表
 */
public class OneProfile {
    private final List<TouchAreaModel> modelList;
    transient private final List<TouchArea<? extends TouchAreaModel>> touchAreaList;
    transient private final List<TouchArea<? extends TouchAreaModel>> umodifiableList;
    //TODO 编辑名称的时候，需要同时修改本地文件的名称。另外还需要检查是否有重复，是否有特殊字符
    public String name = "invalidName";
    int version = 0; //版本号
    private boolean isDebug = false;

    /**
     * 给gson反序列化用的
     */
    private OneProfile() {
        this("stub_name");
    }

    public OneProfile(String name) {
        this.name = name;
        modelList = new ArrayList<>();
        touchAreaList = new ArrayList<>();
        umodifiableList = Collections.unmodifiableList(touchAreaList);

        //默认带一个触摸区域
        modelList.add(new OneGestureArea());
    }

    /**
     * 在无法提供有效构造函数参数时，获取一个无参的实例 。稍后将其信息不全
     */
    public static OneProfile newInstance() {
        return new OneProfile();
    }


    /**
     * 用于反序列化之后，从model同步arealist
     */
    public void syncAreaList(TouchAreaView host) {
        touchAreaList.clear();
        for (int i = 0; i < modelList.size(); i++) {
            TouchAreaModel model = modelList.remove(i);
            //这个要换，不能老用这个。而且切换配置之后就失效了
            addArea(host, i, model, model1 -> {
                if (Const.editKeyViewRef != null)
                    Const.editKeyViewRef.get().onModelChanged(model1);
            });
            Log.e("OneProfile", "syncAreaList:请实现adapter ");
//                throw new RuntimeException("请实现adapter");
        }
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public List<TouchArea<? extends TouchAreaModel>> getTouchAreaList() {
        return umodifiableList;
    }

    /**
     * 根据model添加一个触摸区域，
     * <br/>注意area最终使用的model并非传入的实例，所以在调用此方法后应通过area.getModel()来获取实际的model
     */
    public TouchArea<? extends TouchAreaModel> addArea(TouchAreaView host, TouchAreaModel model, EditMoveAdapter.OnFocusListener focusListener) {
        return addArea(host,0,model,focusListener);
    }

    public TouchArea<? extends TouchAreaModel> addArea(TouchAreaView host, int index, TouchAreaModel model, EditMoveAdapter.OnFocusListener focusListener) {
        TouchArea<?> newArea = TestHelper.newAreaEditable(host, model, focusListener);
        modelList.add(index, newArea.getModel());
        //TODO 不能一直是editable
        touchAreaList.add(index, newArea);
        return newArea;
    }

    public void removeArea(int index) {
        if (modelList.size() <= index)
            return;
        modelList.remove(index);
        touchAreaList.remove(index);
    }

    public int removeArea(TouchAreaModel model) {
        int indexFound = modelList.indexOf(model);
        if (indexFound != -1) {
            modelList.remove(indexFound);
            touchAreaList.remove(indexFound);
        }
        return indexFound;
    }
}