package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.adapter;

public abstract class MouseMoveAdapter {
    //TODO 序列化时final的也会序列化吗，那每个实例都存一遍太占地方了，把这些移到别的地方去？
    public static final int TYPE_UNKNOWN=0;
    public static final int TYPE_SIMPLE=1;
    /**
     * 用于gson，记录子类的类型
     */
    private int adapterType=TYPE_UNKNOWN;
    public MouseMoveAdapter(int adapterType){
        this.adapterType = adapterType;
    }
    public abstract void moveTo(float x, float y);

    public abstract void prepareMoving(float x, float y);
}
