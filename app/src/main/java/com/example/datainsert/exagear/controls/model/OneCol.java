package com.example.datainsert.exagear.controls.model;

import java.io.Serializable;

public class OneCol implements Serializable {

    private static final long serialVersionUID = -8663849085661560546L;
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

    public OneKey[] getAllKeys() {
        return mAllKeys;
    }

    public void setAllKeys(OneKey[] mAllKeys) {
        this.mAllKeys = mAllKeys==null?new OneKey[0]: mAllKeys;
    }

    /**
     * 用于修改回收视图的数据时，获取一份深拷贝
     * @return
     */
    public OneCol clone(){
        return new OneCol(this.mAllKeys.clone(),this.id);
    }
}
