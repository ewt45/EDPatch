package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import android.util.Log;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter.EditMoveAdapter;

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
     *
     * @param editing 若为true，设置adapter为editAdapter
     */
    public void syncAreaList(boolean editing) {
        touchAreaList.clear();
        for (int i = 0; i < modelList.size(); i++) {
            TouchAreaModel model = modelList.remove(i);
            addArea(i, model, editing);
        }
        //TODO 最后加上gestureArea
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
     * 根据model添加一个按钮触摸区域，（因为手势触摸区域固定有且只有一块，不会添加或删除），根据model类型添加对应的adapter。
     * <br/>注意area最终使用的model并非传入的实例，所以在调用此方法后应通过area.getModel()来获取实际的model
     * <br/>注意TouchAreaModel可能有继承关系，所以不能用instanceOf，应该用getClass().equals
     *
     * @param reference 用于提供新建触摸区域的属性，该类型不会直接作为touchArea的model，而是会根据它再新建一个，以防多一个区域共用一个model。
     */
    public TouchArea<? extends TouchAreaModel> addArea(TouchAreaModel reference, boolean editing) {
        return addArea(0, reference, editing);
    }

    /**
     * 同{@link #addArea(TouchAreaModel, boolean)}
     */
    public TouchArea<? extends TouchAreaModel> addArea(int index, TouchAreaModel reference, boolean editing) {
        //TODO 要不这个toucharea的类型也像model一样 class形成一个数组
        TouchAreaView host = Const.touchAreaViewRef.get();
        TouchAreaModel finalModel = TouchAreaModel.newInstance(reference, (Class<? extends TouchAreaModel>) ((reference == null) ? OneButton.class : reference.getClass()));
        TouchAdapter adapter = !editing ? null : new EditMoveAdapter(host, finalModel, model -> {
            if (Const.editKeyViewRef != null)
                Const.editKeyViewRef.get().onModelChanged(model);
        });
        TouchArea<?> newArea = null;
        try {
            newArea = ModelProvider.getAreaClass(finalModel.getClass())
                    .getDeclaredConstructor(finalModel.getClass(), TouchAdapter.class)
                    .newInstance(finalModel, adapter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        if (finalModel.getClass().equals(OneButton.class)) {
//            newArea = new TouchAreaButton((OneButton) finalModel, adapter);
//        } else if (finalModel.getClass().equals(OneStick.class)) {
//            newArea = new TouchAreaStick((OneStick) finalModel, adapter);
//        } else if (finalModel.getClass().equals(OneDpad.class)) {
//            newArea = new TouchAreaDpad((OneDpad) finalModel, adapter);
//        } else if (finalModel.getClass().equals(OneGestureArea.class)) {
//            throw new RuntimeException("gestureArea应该不应该通过addArea调用。应该一开始就存在，或者在area列表被清空的那个函数里重新添加一次");
////            newArea = new TouchAreaGesture(host, (OneGestureArea) finalModel, adapter); //TODO 这里到底处不处理gestureArea
//        } else
//            throw new RuntimeException("无法创建该类型的TouchArea" + reference);

        modelList.add(index, newArea.getModel());
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