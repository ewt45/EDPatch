package com.example.datainsert.exagear.controlsV2.edit.props;

import static com.example.datainsert.exagear.controlsV2.TouchAreaModel.TYPE_BUTTON;
import static com.example.datainsert.exagear.controlsV2.TouchAreaModel.TYPE_COLUMN;
import static com.example.datainsert.exagear.controlsV2.TouchAreaModel.TYPE_DPAD;
import static com.example.datainsert.exagear.controlsV2.TouchAreaModel.TYPE_STICK;
import static com.example.datainsert.exagear.controlsV2.edit.Edit1KeyView.buildOptionsGroup;
import static com.example.datainsert.exagear.controlsV2.edit.Edit1KeyView.getButtonTypeFromModel;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;

public class Prop0Type extends Prop<TouchAreaModel>{
    RadioGroup groupType;
    static String[] mTypeNames = RR.getSArr(RR.ctr2_prop_type_names); //按钮 摇杆 十字键
    //直接用mModel来表示当前选择的是哪一个类型就行了
    static int[] mTypeInts = new int[]{TYPE_BUTTON, TYPE_STICK, TYPE_DPAD, TYPE_COLUMN};
    OnTypeChangeListener mHostListener;
    public Prop0Type(Host<TouchAreaModel> host, Context c, OnTypeChangeListener listener) {
        super(host, c);
        mHostListener = listener;
//        this. mMainView = mainView;;
//        groupType = (RadioGroup) mainView.getChildAt(0);
    }

    @Override
    public String getTitle() {
        return RR.getS(RR.global_type);
    }

    @Override
    protected View createMainEditView(Context c) {
        HorizontalScrollView scrollGroupType = buildOptionsGroup(
                c,
                mTypeNames,
                mTypeInts,
                (group, btn, selectType) -> {
                    int oldType = getButtonTypeFromModel(mHost.getModel());
                    if (oldType == selectType)
                        return;

//                    完全不对，现在切换类型的时候专有prop不会跟着切换了，
//                    而且点击一个area，切换类型，没有将这个area的model改变而是新建了一个model，
//                    emmm刚说完又没这个问题了，时有时无
                    Log.d("Prop0Type", "Prop0Type: 回调");

                    //不调用onWidgetListener()来调用onModelChanged了，直接让onTypeChanged里调用吧
                    this.mIsChangingSource=true;
                    mHostListener.onTypeChanged(selectType);
                    this.mIsChangingSource=false;

                });
        groupType = (RadioGroup) scrollGroupType.getChildAt(0);
        return scrollGroupType;
    }

    @Override
    protected View createAltEditView(Context c) {
        return null;
    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        int checkedType = -1;
        for(int i=0; i<groupType.getChildCount(); i++){
            RadioButton btn = (RadioButton) groupType.getChildAt(i);
            if(btn.isChecked()){
                checkedType = mTypeInts[i];
                break;
            }
        }

        int modelType = getButtonTypeFromModel(model);
        if(checkedType!=modelType){
            for(int i=0; i<mTypeInts.length; i++){
                if(modelType == mTypeInts[i]){
                    ((RadioButton) groupType.getChildAt(i)).setChecked(true);
                    Log.d("Prop0Type", "updateUIFromModel: 有变化");
                    break;
                }
            }
        }
    }
    /**
     * 处理新model生成和更新对应的toucharea显示，调用onModelChanged在prop里调用
     */
    public interface OnTypeChangeListener{
        /**
         * 处理新model生成和更新对应的toucharea显示，调用onModelChanged在prop里调用
         */
        public void onTypeChanged(@TouchAreaModel.ModelType int newType);
    }
}
