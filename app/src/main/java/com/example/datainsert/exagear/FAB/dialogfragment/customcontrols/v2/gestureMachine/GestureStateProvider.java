package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State.StateCountDownMeasureSpeed;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.BoolEditable;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.IntRangeEditable;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.RangeSeekbar;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 记录全部State，其tag，全部可编辑的属性。
 * <br/> 用于用户自定义手势操作
 */
public class GestureStateProvider {
    public final static Map<Integer, StateInfo> allStatesEditableList = new HashMap<>();
    public final static Map<Class<? extends AbstractFSMState2>,StateInfo> allStateEditableList2 = new HashMap<>();

    public static abstract class FSMStateFactory<FSMStateClass>{
        abstract public AbstractFSMState2 createState();
        abstract public View createDetailEditView();
        abstract public View createShortInfoView();
    }
    public final static SparseArray<FSMStateFactory<? extends AbstractFSMState2>> stateArray = new SparseArray<>();

    static {
        addState(StateCountDownMeasureSpeed.class);
    }

    private static void addState(Class<? extends AbstractFSMState2> clz) {
        allStatesEditableList.put(AbstractFSMState2.getClassTag(clz),new StateInfo(clz));
        allStateEditableList2.put(clz,new StateInfo(clz));
    }


    public static class StateInfo {
        private static final String TAG = "StateInfo";
        final Class<? extends AbstractFSMState2> clz;
        final Map<Integer, EditableField<?,?>> fieldMap;

        public StateInfo(Class<? extends AbstractFSMState2> clz) {
            this.clz = clz;
            fieldMap = new HashMap<>();
            for (Field field : this.clz.getDeclaredFields()) {
                for (Annotation ant : field.getAnnotations()){
//                    Log.d(TAG, "test: class=" + ant);
                    if (ant instanceof IntRangeEditable) {
                        EditableField<?,?> editableField = new IntRangeEditableField((IntRangeEditable) ant, field);
                        fieldMap.put(editableField.getTag(), editableField);
                        break;
                    } else if(ant instanceof BoolEditable){

                    }
                }
            }
        }


    }

    static class IntRangeEditableField extends EditableField<IntRangeEditable,Integer> {
        int defValue;
        int[] range;
        private int tag;

        public IntRangeEditableField(IntRangeEditable ant, Field field) {
            super(ant, field);
        }

        @Override
        protected void saveAntInfo(IntRangeEditable ant) {
            tag = ant.tag();
            defValue = ant.defVal();
            range = ant.range();
        }

        @Override
        public int getTag() {
            return tag;
        }

        @Override
        public View getEditView(Context c, @Nullable Integer initValue) {
            RangeSeekbar seekbar = new RangeSeekbar(c,range[0],range[1]) {
                @Override
                protected int rawToFinal(int rawValue) {
                    return rawValue;
                }

                @Override
                protected int finalToRaw(int finalValue) {
                    return finalValue;
                }
            };
            if(initValue!=null)
                seekbar.setValue(initValue);
            return seekbar;
        }
    }

    static abstract class EditableField<AntType,ValueType> {
        final Field field;

        public EditableField(AntType ant, Field field) {
            this.field = field;
            field.setAccessible(true);
            saveAntInfo(ant);
        }

        abstract protected void saveAntInfo(AntType ant);

        abstract public int getTag();

        abstract public View getEditView(Context c, @Nullable ValueType initValue);

        public View getEditView(Context c) {
            return getEditView(c, null);
        }
    }
}
