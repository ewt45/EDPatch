package com.example.datainsert.exagear.controlsV2.model;

import android.graphics.Point;
import android.graphics.Rect;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 一列按钮组。宽=列宽，高=单个按钮高
 */
public class OneColumn extends TouchAreaModel {
    /** 名称列表，每个元素与keycodes列表中的元素是一一对应的 */
    private final List<String> nameList = new ArrayList<>();
    /** 允许的最大长度，小于等于0为无限制 */
    private int lengthLimit = 0; //TODO 必须要有个限制，至少不能超过屏幕高度/宽度
    /** 横向还是竖向排列 */
    private boolean isVertical = true;


    public OneColumn(){
        super(TYPE_COLUMN);
        setKeycodes(Arrays.asList(34,35,36,37,38,39,40,41));
    }


    /**
     * 自定义对最大长度的限制
     */
    public void setLengthLimit(int lengthLimit) {
        this.lengthLimit = floorDivToSmallestUnit(lengthLimit);
    }

    /**
     * 获取额外对长度的限制。如果总长度超过此限制，则只显示该长度，其他部分可通过滑动来显示
     * <br/> 编辑属性时应调用此函数，此函数可能返回小于等于0 (无自定义限制）
     */
    public int getLengthLimit(){
        return lengthLimit;
    }

    /**
     * getHeight/Width获取的是单个按钮的宽高。此函数获取的是沿排列方向的总长度。不受maxLength限制
     */
    public int getTotalLength(){
        return getKeycodes().size() * (isVertical ? getHeight() : getWidth());
    }

    /**
     * 额外对长度的限制。如果总长度超过此限制，则只显示该长度，其他部分可通过滑动来显示
     * <br/> 绘制和计算滚动时，应调用此函数。此函数会保证返回的值一定大于0，且小于等于视图宽高
     */
    public int getLengthLimitInsideWindow(){
        int windowLimitLength; //当前视图的宽高。若获取不到则设为int最大值
        Point p = Const.windowDisplaySize;
        if(p != null)
            windowLimitLength = isVertical ? p.y-top : p.x-left; //例如垂直，如果距离顶端不为0，那么限制高度应该变小
        else windowLimitLength = Integer.MAX_VALUE;

        if(lengthLimit <= 0)
            return windowLimitLength;
        else
            return Math.min(lengthLimit, windowLimitLength); //即使有自定义限制，也不能超过视图宽高
    }

    /**
     * 获取加上最大长度限制后的长度。可能小于总长度
     */
    public int getRestrictedLength(){
        int unlimited = getTotalLength();
        return Math.min(unlimited, getLengthLimitInsideWindow());
    }

    /**
     * 获取该按钮组的边界，设置到传入的 rect 上
     */
    public void getBounds(Rect rect){
        int length = getRestrictedLength();
        int width = isVertical() ? getWidth() : length;
        int height = isVertical() ? length : getHeight();
        rect.set(getLeft(), getTop(), getLeft() + width, getTop() + height);
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

    /**
     * 横向还是竖向排列
     */
    public boolean isVertical(){
        return isVertical;
    }

    @Override
    public void setKeycodes(List<Integer> newKeys) {
        List<Integer> oldKeys = new ArrayList<>(getKeycodes());
        super.setKeycodes(newKeys);
        //更新keycode的时候，要同步更新nameList (如果keycode在旧列表中，则取出其在列表中索引 对应旧nameList中的名称，否则设为null）
        List<String> newNames = new ArrayList<>();
        for(Integer keycode : getKeycodes()) {
            int idxInOld = oldKeys.indexOf(keycode);
            newNames.add(idxInOld == -1 ? null : nameList.get(idxInOld));
        }
        nameList.clear();
        nameList.addAll(newNames);
    }

    /**
     * 根据索引获取对应keycode的用户友好名称
     */
    public String getNameAt(int index){
        String name = nameList.get(index);
        return name != null ? name : Const.getKeyOrPointerButtonName(getKeycodes().get(index));
    }

    /**
     * 根据给定keycode返回用户友好名称。若该keycode不存于列表中会抛出异常
     */
    public String getNameByKeycode(Integer keycode){
        List<Integer> keycodes = getKeycodes();
        for(int i=0; i<keycodes.size(); i++)
            if(keycodes.get(i).equals(keycode))
                return getNameAt(i);
        throw new RuntimeException("model不包含此keycode" + keycode);
    }

    /**
     * 设置索引对应的keycode的用户友好名称
     */
    public void setNameAt(int index, String name) {
        nameList.set(index, name);
    }

    /**
     * 根据给定keycode 设置其用户友好名称。若该keycode不存于列表中会抛出异常
     */
    public void setNameByKeycode(Integer keycode, String name) {
        List<Integer> keycodes = getKeycodes();
        for(int i=0; i<keycodes.size(); i++)
            if(keycodes.get(i).equals(keycode)) {
                setNameAt(i, name);
                return;
            }
        throw new RuntimeException("model不包含此keycode" + keycode);
    }

    @Override
    protected void cloneSelfFields(TouchAreaModel ref) {
        if(ref instanceof OneColumn){
            OneColumn ref2 = (OneColumn) ref;
            this.lengthLimit = ref2.lengthLimit;
            this.isVertical =  ref2.isVertical;
            this.nameList.clear();
            this.nameList.addAll(ref2.nameList);
        }
    }
}
