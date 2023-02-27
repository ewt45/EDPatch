package com.example.datainsert.exagear.controls.model;

import java.io.Serializable;

public class OneCol implements Serializable {

    private int id;//给每列分配一个独有的id吧
    OneKey[] mAllKeys;
//    public OneCol(OneKey[] allKeys){
//        this(allKeys,true);
//    }

    public OneCol(OneKey[] allKeys, int newId){
        mAllKeys = allKeys;
        this.id = newId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OneKey[] getmAllKeys() {
        return mAllKeys;
    }

    public void setmAllKeys(OneKey[] mAllKeys) {
        this.mAllKeys = mAllKeys;
    }

    /**
     * 用于修改回收视图的数据时，获取一份深拷贝
     * @return
     */
    public OneCol clone(){
        return new OneCol(this.mAllKeys.clone(),this.id);
    }
}
