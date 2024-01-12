package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props;

import android.content.Context;
import android.view.View;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.EditConfigWindow;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;

/**
 * 用户编辑ui
 * ->updateUIFromModel:同步内容到model，调用mHost.onModelChanged刷新全部ui
 * -> 调用到自己的updateUIFromModel，此时再根据需要刷新ui
 * （但是一般来说如果ui是通过用户编辑而更新的话，此时就不需要再根据model刷新ui了，注意一下别死循环了）
 */
public abstract class Prop<ModelType> {
    public View mMainView;
    final public View mAltView;
    final protected Host<ModelType> mHost;
    boolean mIsHide = false;
    /**
     * onModelChanged调用是否是由这个属性的改变而引起的。如果返回true，则host不应再调用prop的changeUI以防循环
     * <br/>例如颜色编辑edittext，如果用户在输入时，输入少于8个数字时，会触发updateModel，然后触发host.onModelChanged,然后触发updateUI，然后在updateUI里会把补全到8个的数字填到自身上，导致永远是8个数字没法正常输入。
     * <br/> 所以需要在调用host.onModelChanged前将此flag置为true，调用后置为false。在调用期间会走到updateUI，发现这个flag为true的话就不改了
     * <br/> 才发现这个应该是双向的，updateUI里也要设置这个，否则如果是先updateUI，也会触发视图的监听（比如seekbar这种）
     */
    protected boolean mIsChangingSource=false;

    public Prop(Host<ModelType> host, Context c) {
        mHost = host;
        mMainView = createMainEditView(c);
        mAltView = createAltEditView(c);
    }


//    /**
//     * 仅对model赋值，其他ui刷新放在updateUIFromModel
//     */
//    abstract void updateModelFromUI(boolean isAltView);
//
//    // ui被用户编辑时，修改model，并通知host刷新其他ui
//     protected void onUserEdited(boolean isAltView) {
//         updateModelFromUI(isAltView);
//         mHost.onModelChanged();
//     };

    abstract public String getTitle();

    abstract protected View createMainEditView(Context c);

    abstract protected View createAltEditView(Context c);

    // model被修改时，更新ui （emm应该没有这个需求吧？初始化的时候吗）
    // 不对这个是需要的，下面那个是不需要的，ui被编辑时，回调里先设置到model上，然后调用这个函数就行
    //仅当ui数据与model数据不同时才刷新，防止循环
    // 只修改自己的ui，如果有联动，也让上级，循环update，总会循坏的别的ui
    // 只应该由外部调用，自身不应该调用这个函数
    abstract public void updateUIFromModel(TouchAreaModel model);
    /**
     * onModelChanged调用是否是由这个属性的改变而引起的。如果返回true，则host不应再调用prop的changeUI以防循环
     */
    public boolean isChangingSource() {
        return mIsChangingSource;
    }

    /**
     * 返回的编辑控件，被修改时的回调，应该在修改model属性后调用此方法。
     */
    protected void onWidgetListener(){
        onWidgetListener(mHost.getModel());
    }

    protected void onWidgetListener(ModelType model){
        mIsChangingSource=true;
        mHost.onModelChanged(model);
        mIsChangingSource=false;
    }

    public boolean isHide() {
        return mIsHide;
    }

    public void setHide(boolean isHide) {
        mIsHide = isHide;
    }

    public interface Host<Model> {
        /**
         * 用户修改ui后，同步到model时用来获取model
         */
        Model getModel();

        /**
         * 获取编辑选项最顶层的窗口
         *
         * @return
         */
        EditConfigWindow getWindow();

        /**
         * 修改model属性后通知host修改其他prop的ui。仅用于修改ui
         */
        void onModelChanged(Model model);

        /**
         * 和onModelChanged(TouchAreaModel model);一样，不过model沿用旧的host自己的model
         */
        default void onModelChanged() {
            onModelChanged(getModel());
        }

        ;
    }
}
