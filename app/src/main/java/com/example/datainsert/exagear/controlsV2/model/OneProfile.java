package com.example.datainsert.exagear.controlsV2.model;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.TouchArea;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.touchAdapter.EditMoveAdapter;
import com.example.datainsert.exagear.controlsV2.touchArea.TouchAreaGesture;

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
    private final int version = 20240229; //版本号
    private boolean showTouchArea = true; //是否显示按键
    private float mouseMoveSpeed = 1f; //全局鼠标移动速度

    transient private boolean isEditing = false;


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
     * 用于反序列化之后，从model同步arealist
     *
     * @param editing 若为true，设置adapter为editAdapter
     */
    public void syncAreaList(boolean editing) {
        isEditing =editing;
        touchAreaList.clear();
        for (int i = 0; i < modelList.size(); i++) {
            TouchAreaModel model = modelList.remove(i);
            addModelAndAddArea(i, model);
        }

        //如果当前不显示按键，则area列表中只留一个手势触摸区域. 但是编辑模式下必须要显示按键
        if(!showTouchArea && !isEditing)
            for(int i=0; i<touchAreaList.size(); i++)
                if(!(touchAreaList.get(i) instanceof TouchAreaGesture)){
                    touchAreaList.remove(i);
                    i--;
                }
    }


    /**
     * 获取当前是否在编辑状态。切换编辑状态调用syncAreaList
     */
    public boolean isEditing() {
        return isEditing;
    }

    /**
     * 设置是否显示按键，函数内刷新area列表，若为false则清空列表
     */
    public void setShowTouchArea(boolean showTouchArea) {
        this.showTouchArea = showTouchArea;
        syncAreaList(isEditing);
    }
    public boolean isShowTouchArea() {
        return showTouchArea;
    }

    public void setMouseMoveSpeed(float mouseMoveSpeed) {
        this.mouseMoveSpeed = mouseMoveSpeed;
    }

    public float getMouseMoveSpeed() {
        return mouseMoveSpeed;
    }

    public List<TouchArea<? extends TouchAreaModel>> getTouchAreaList() {
        return umodifiableList;
    }



    /**
     * 同{@link #addModelAndAddArea(int, TouchAreaModel)}
     */
    public TouchArea<? extends TouchAreaModel> addModelAndAddArea(TouchAreaModel model) {
        return addModelAndAddArea(0, model);
    }

    /**
     * 根据model添加一个按钮触摸区域，（因为手势触摸区域固定有且只有一块，不会添加或删除），根据model类型添加对应的adapter。
     * <br/>注意model应不属于当前任何一个touchArea （这句已废弃：注意area最终使用的model并非传入的实例，所以在调用此方法后应通过area.getModel()来获取实际的model
     * <br/>注意TouchAreaModel可能有继承关系，所以不能用instanceOf，应该用getClass().equals
     */
    public TouchArea<? extends TouchAreaModel> addModelAndAddArea(int index, TouchAreaModel model) {
        if(model==null)
            throw new RuntimeException("model不能为null");
        for(TouchArea<?> area:touchAreaList)
            if(area.getModel() == model)
                throw new RuntimeException("该model已属于一个area，无法再新建area");

        TouchAdapter adapter = !isEditing ? null : new EditMoveAdapter(model, editModel -> {
            if (Const.editKeyView != null)
                Const.editKeyView.onModelChanged(editModel);
        });
        TouchArea<?> newArea;
        try {
            newArea = ModelProvider.getAreaClass(model.getClass())
                    .getDeclaredConstructor(model.getClass(), TouchAdapter.class)
                    .newInstance(model, adapter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        modelList.add(index, model);
        touchAreaList.add(index, newArea);
        return newArea;
    }

    public void removeModelAndArea(int index) {
        if (modelList.size() <= index)
            return;
        modelList.remove(index);
        touchAreaList.remove(index);
    }

    public int removeModelAndArea(TouchAreaModel model) {
        int indexFound = modelList.indexOf(model);
        if (indexFound != -1) {
            modelList.remove(indexFound);
            touchAreaList.remove(indexFound);
        }
        return indexFound;
    }

    /**
     * 获取gestureArea的model，应该是列表中的最后一个元素
     */
    public OneGestureArea getGestureAreaModel(){
        return (OneGestureArea) modelList.get(modelList.size()-1);
    }

    public String getName() {
        return name;
    }
}