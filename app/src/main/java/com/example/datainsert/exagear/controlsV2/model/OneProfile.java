package com.example.datainsert.exagear.controlsV2.model;

import static com.example.datainsert.exagear.controlsV2.TouchAreaModel.floorDivToSmallestUnit;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.TouchArea;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.touchAdapter.EditMoveAdapter;
import com.example.datainsert.exagear.controlsV2.touchArea.TouchAreaGesture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 存model列表和touchArea列表，每个model对应一个area，
 * model添加或删除只能通过这个类进行，且只能通过add和remove函数，直接操作列表会报错
 * 添加新toucharea的时候，应该插入到0的位置。然后遍历的时候先遍历到。手势区域应该放在最后一个。
 * model增删时会自动增删area
 * 从json反序列化出来之后记得调用sync函数同步area列表
 */
public class OneProfile {
    private static final String TAG = "OneProfile";
    private final List<TouchAreaModel> modelList;
    transient private final List<TouchArea<? extends TouchAreaModel>> touchAreaList;
    transient private final List<TouchArea<? extends TouchAreaModel>> umodifiableList;
    private String name;
    private final int version = 20240229; //版本号
    private boolean showTouchArea = true; //是否显示按键
    private float mouseMoveSpeed = 1f; //全局鼠标移动速度
    private final int[] resolution = new int[]{1280, 720};//记录手机屏幕宽高，以便在其他设备导入时，计算应该缩放的大小
    transient private boolean isEditing = false;
    transient private float[] scaleXY = new float[]{1,1};//记录当前窗口宽高/配置的宽高的比值，动态记录，而非修改model的原始数据


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
     * 不同设备分辨率不同。所以需要根据touchAreaView宽高，来调整一下每个toucharea的区域
     * <br/> 只应该在导入配置/解压内置配置 的时候调整一次。因为该操作会选择缩放后宽高中较小的一个，
     * 所以像屏幕旋转这样的操作，两次之后按钮会缩小两次而非变回原大小
     */
    public void adjustProfileToFullscreen(){
        //因为不会随时更新，所以还是按系统的来吧。不然万一小窗的时候导入以后就一直是小窗大小了
        Point point = TestHelper.getSystemDisplaySize(Const.getActivity());
        int[] currWH = {point.x, point.y};

        if(resolution[0]==0 || resolution[1]==0) {
            resolution[0] = currWH[0];
            resolution[1] = currWH[1];
            return;
        }else if((currWH[0]==resolution[0] && currWH[1]== resolution[1])
        || currWH[0]==0 || currWH[1]==0 )
            return;

        //不是matrix的问题，我把坐标强制按4整除处理了，所以缩放后传入的坐标不一定是显示的坐标。。。
        // 也不准确，是精度问题。比如下减上作为高度再变为4的倍数，和下变为4倍数 减去 上变为4倍数 二者结果不一定一样的
        // 草，是被最小允许宽度给限制了。。。另外矩阵反而不行，还是直接算宽高缩放吧
        float scaleX = 1f*currWH[0]/resolution[0], scaleY = 1f*currWH[1]/resolution[1];
        for(TouchAreaModel model:modelList) {
            model.setLeft(floorDivToSmallestUnit((int) (model.getLeft() * scaleX)));
            model.setTop((floorDivToSmallestUnit((int) (model.getTop() * scaleY))));
            int minSize = (int) Math.min(model.getWidth() * scaleX, model.getWidth() * scaleY);
            model.setWidth(minSize);
            model.setHeight(minSize);
        }

//        Matrix matrix = new Matrix();
//        matrix.postScale(1f*currWH[0]/resolution[0], 1f*currWH[1]/resolution[1]);
//        RectF rect = new RectF();
//        for(TouchAreaModel model:modelList){
//            rect.set(model.getLeft(), model.getTop(), model.getLeft()+model.getWidth(), model.getTop()+model.getHeight());
//            String logStr = "缩放前："+rect.toString();
//            matrix.mapRect(rect);
//            rect.left = floorDivToSmallestUnit((int) rect.left);
//            rect.right = floorDivToSmallestUnit((int) rect.right);
//            rect.top = floorDivToSmallestUnit((int) rect.top);
//            rect.bottom = floorDivToSmallestUnit((int) rect.bottom);
//            logStr += "， 缩放后："+rect;
//
//            int minSize = (int) Math.min(rect.width(), rect.height());
//            model.setLeft((int) rect.left);
//            model.setTop((int) rect.top);
//            model.setWidth(minSize);
//            model.setHeight(minSize);
//
//            logStr += "变为dp8整数倍后: "+model.getLeft()+", "+model.getTop()+", "+(model.getLeft()+model.getWidth())+", "+(model.getTop()+model.getHeight());
//            Log.d(TAG, "adjustProfileToFullscreen: "+logStr);
//        }

        Log.d(TAG, "adjustProfileToFullscreen: 当前视图分辨率与配置设定分辨率不同，正在调整："+ Arrays.toString(resolution)+" -> "+Arrays.toString(currWH));
        resolution[0] = currWH[0];
        resolution[1] = currWH[1];
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
     * 同{@link #addModelAndAddArea(int, TouchAreaModel)} ， index传入0
     */
    public TouchArea<? extends TouchAreaModel> addModelAndAddArea(TouchAreaModel model) {
        return addModelAndAddArea(0, model);
    }

    /**
     * 根据model添加一个按钮触摸区域，（因为手势触摸区域固定有且只有一块，不会添加或删除），根据model类型添加对应的adapter。
     * <br/>注意model应不属于当前任何一个touchArea （这句已废弃：注意area最终使用的model并非传入的实例，所以在调用此方法后应通过area.getModel()来获取实际的model
     * <br/>注意TouchAreaModel可能有继承关系，所以不能用instanceOf，应该用getClass().equals
     * @param index 如果是新建按钮时，应该将index设为0，因为最后一个index代表的是手势区域
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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}