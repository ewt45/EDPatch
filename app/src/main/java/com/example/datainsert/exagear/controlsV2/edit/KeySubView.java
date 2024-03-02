package com.example.datainsert.exagear.controlsV2.edit;

import android.widget.GridLayout;

import com.example.datainsert.exagear.controlsV2.TouchAreaModel;

public interface KeySubView<T extends TouchAreaModel> {

    /**
     * 根据数据刷新UI。请在调用inflate之后再调用此函数。
     * <br/> 若model与成员变量的model不等，则成员变量变为新model
     */
    void updateUI(T model);
    void inflate(Edit1KeyView host, GridLayout gridLayout);

    //TODO 每个Model都有各自的newInstance方法。这个方法和其类似。（是否是多余的？）
    /**
     * 用于切换类型时
     * <br/> 根据别的类型的model，将通用的属性同步到自身model，并返回自身model
     * <br/> 是否需要new一个model返回而非直接返回自身model？不用，原因：
     * 1. 切换类型时，若model不属于任何touchArea，那么直接设置到host的mModel上
     * 2. 如model属于一个touchArea，那么会调用onCreateNewTouchArea，host的mModel变化，但在updateUI时会将host的mModel同步到Sub自身。
     * @param reference 非T 类型的model
     * @return T 类型的model
     */
    T adaptModel(TouchAreaModel reference);
}
