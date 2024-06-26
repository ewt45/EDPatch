package com.example.datainsert.exagear.controlsV2.edit;

import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchArea;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.TouchAreaView;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop0MainColor;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop1Name;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop0Size;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop0Type;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop1Key;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop1Shape;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop1Trigger;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop2Direction;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop2Key;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop3Key;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop5Keys;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop5LenLimit;
import com.example.datainsert.exagear.controlsV2.edit.props.Prop5Vertical;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.model.DeserializerOfModel;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.model.OneButton;
import com.example.datainsert.exagear.controlsV2.model.OneGestureArea;
import com.example.datainsert.exagear.controlsV2.model.OneProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 显示用于修改按键属性的视图
 * <br/> 注意：onDraw不会自己执行，每个更改属性的选项都要调用view.invalidate()
 */
public class Edit1KeyView extends LinearLayout implements Prop.Host<TouchAreaModel> {
    private static final String TAG = "KeyPropertiesView";
    private static final Integer PROP_KEY_UNIVERSAL = 999; //用于HashMap<Integer, Prop<TouchAreaModel>[]>中，记录通用属性的key
    TouchAreaModel mModel = null;
    LinkedHashMap<Integer, Prop<TouchAreaModel>[]> mProps = new LinkedHashMap<>();
    LinearLayout[] mPanels = new LinearLayout[2];
    LinearLayout mPanelsNew;

    public Edit1KeyView(Context c) {
        super(c);
        Const.editKeyView = this;
        setOrientation(VERTICAL);
        //TODO 调整设置后，再新建的按钮 默认设置也随之更改。（再出一个复制按钮功能吧）

        TouchAreaView touchAreaView = Const.getTouchView();
        //工具栏
        Button btnAddKey = new Button(c);
        btnAddKey.setText(getS(RR.global_add)); //添加
        btnAddKey.setOnClickListener(v -> {
            //如果当前的model不属于任何area（当前坐标没有按钮），那么就在当前坐标新建area，否则将新建到0,0
            boolean isRefModelAlreadyInList = false;
            for (TouchArea<?> area : touchAreaView.getProfile().getTouchAreaList())
                if (area.getModel() == mModel) {
                    isRefModelAlreadyInList = true;
                    break;
                }

            TouchAreaModel newModel = TouchAreaModel.newInstance(mModel, mModel.getClass());
            if (isRefModelAlreadyInList) {
                newModel.setLeft(0);
                newModel.setTop(0);
            }

            touchAreaView.getProfile().addModelAndAddArea(newModel);
            mModel = newModel;
            touchAreaView.invalidate();
        });

        Button btnDelKey = new Button(c);
        btnDelKey.setText(getS(RR.global_del)); //删除当前
        btnDelKey.setOnClickListener(v -> {
            touchAreaView.getProfile().removeModelAndArea(mModel);
            touchAreaView.invalidate();
        });

        Button btnTest = new Button(c);
        btnTest.setText("测试宽高");
        btnTest.setOnClickListener(v -> {
            TestHelper.getSystemDisplaySize(v.getContext());
        });

        Button btnTestRead = new Button(c);
        btnTestRead.setText("测试读取state");
        btnTestRead.setOnClickListener(v -> {
            String json = "{\"modelList\":[{\"allStateList\":[{\"id\":1,\"stateType\":2},{\"id\":2,\"stateType\":3},{\"countDownMs\":250,\"fastMoveThreshold\":36.0,\"fingerIndex\":0,\"noMoveThreshold\":12.0,\"id\":3,\"niceName\":\"\",\"stateType\":1}],\"tranActionsList\":[[]],\"tranEventList\":[1],\"tranPostStateList\":[1],\"tranPreStateList\":[2],\"colorStyle\":0,\"height\":1600,\"keycodes\":[0],\"left\":0,\"mMinAreaSize\":80,\"mainColor\":-1286,\"modelType\":3,\"name\":\"None\",\"top\":0,\"width\":2560}],\"name\":\"1\",\"version\":0}\n";
            Gson gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(TouchAreaModel.class, new DeserializerOfModel())
                    .create();
            OneProfile oneProfile = gson.fromJson(json, OneProfile.class);
            System.out.println("state个数=" + oneProfile.getGestureAreaModel().getAllStateList().size());
        });

        LinearLayout linearToolbar = new LinearLayout(c);
        linearToolbar.setOrientation(HORIZONTAL);
        linearToolbar.addView(btnAddKey);
        linearToolbar.addView(btnDelKey);
        if(QH.isTesting()){
            linearToolbar.addView(btnTest);
            linearToolbar.addView(btnTestRead);
        }
        HorizontalScrollView scrollToolbar = new HorizontalScrollView(c);
        scrollToolbar.addView(linearToolbar);


        //仅处理新model生成和更新对应的toucharea显示，调用onModelChanged在prop里调用
        Prop0Type.OnTypeChangeListener typeChangeListener = (newType) -> {
            //先备份一下旧model。然后生成新model，替换ui，填充数据。
            // 然后再看看这个model是否和toucharea已经关联起来了，如果连了的话就把toucharea删了重新建一个
            TouchAreaModel newModelForField = TouchAreaModel.newInstance(mModel, ModelProvider.getModelClass(newType));
            List<TouchArea<?>> currList = Const.getActiveProfile().getTouchAreaList();
            for (int i = 0; i < currList.size(); i++) {
                //如果没进到这个条件，mmodel就不会改变。。。
                if (currList.get(i).getModel().equals(mModel)) {
                    Const.getActiveProfile().removeModelAndArea(i);
                    Const.getActiveProfile().addModelAndAddArea(i, newModelForField);
                    break;
                }
            }
            //如果是没关联touchArea，就用传进来的ref，否则用对应area的新建的model，设置到自身成员变量上
            onModelChanged(newModelForField);
        };

//        for (int i = 0; i < mPanels.length; i++) {
//            mPanels[i] = new LinearLayout(c);
//            mPanels[i].setOrientation(VERTICAL);
//            mPanels[i].setVerticalGravity(Gravity.CENTER_VERTICAL);
//            mPanels[i].setLayoutTransition(new LayoutTransition());
//        }

        mPanelsNew = new LinearLayout(c);
        mPanelsNew.setOrientation(VERTICAL);
        mPanelsNew.setLayoutTransition(new LayoutTransition());

        mProps.put(PROP_KEY_UNIVERSAL, new Prop[]{new Prop0Type(this, c, typeChangeListener), new Prop0MainColor(this, c), new Prop0Size(this, c)});
        mProps.put(TouchAreaModel.TYPE_BUTTON, new Prop[]{new Prop1Name(this, c), new Prop1Key(this, c), new Prop1Shape(this, c), new Prop1Trigger(this, c)});
        mProps.put(TouchAreaModel.TYPE_STICK, new Prop[]{new Prop2Key(this, c), new Prop2Direction(this, c)});
        mProps.put(TouchAreaModel.TYPE_DPAD, new Prop[]{new Prop3Key(this, c)});
        mProps.put(TouchAreaModel.TYPE_COLUMN, new Prop[]{new Prop5LenLimit(this, c), new Prop5Vertical(this, c), new Prop5Keys(this, c), });

        for(Integer typeInt : mProps.keySet()) {
            for (Prop<?> prop : mProps.get(typeInt)) {
                TextView title = QH.getTitleTextView(c, prop.getTitle());
                title.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout linear = new LinearLayout(c);
                linear.setOrientation(VERTICAL);
                linear.setVerticalGravity(Gravity.CENTER_VERTICAL);
                linear.addView(prop.mMainView);

                //没法在外层用滚动视图，否则seekbar之类的没法填满横向

                //如果副视图不为空，添加到linear中并设置切换按钮
                if (prop.mAltView != null) {
                    linear.addView(prop.mAltView);
//                    btn.setText("⭾");  //草了这几个unicode显示不出来
                    TestHelper.setTextViewSwapDrawable(title);
                    title.setOnClickListener(v -> {
                        boolean isMainShowing = prop.mMainView.getVisibility() == VISIBLE;
                        prop.mMainView.setVisibility(isMainShowing ? GONE : VISIBLE);
                        prop.mAltView.setVisibility(isMainShowing ? VISIBLE : GONE);
                    });
                    prop.mAltView.setVisibility(GONE);
                }

                LinearLayout linearPropLine = new LinearLayout(c);
                linearPropLine.setOrientation(HORIZONTAL);
                linearPropLine.setVerticalGravity(Gravity.CENTER_VERTICAL);
                linearPropLine.setBaselineAligned(false);
                linearPropLine.setMinimumHeight(dp8*6);
                linearPropLine.addView(title, QH.LPLinear.one(QH.px(c,80), -2).left().to());
                linearPropLine.addView(linear, QH.LPLinear.one(0, -2).weight().left().right().to());
                mPanelsNew.addView(linearPropLine, QH.LPLinear.one(-1,-2).top().to());
//                mPanels[0].addView(title, QH.LPLinear.one(-1, dp8 * 6).left().top().to());
//                mPanels[1].addView(linear, QH.LPLinear.one(-1, -2).left().right().top().to());
            }
        }

//        LinearLayout linearPanelsWrapper = new LinearLayout(c);
//        linearPanelsWrapper.setOrientation(HORIZONTAL);
//        linearPanelsWrapper.addView(mPanels[0]);
//        linearPanelsWrapper.addView(mPanels[1], QH.LPLinear.one().weight().to());

        addView(scrollToolbar);
        addView(mPanelsNew);

        //初始的时候成员变量为null，然后传入一个实例，这样才会判断不等，调用inflate初次生成视图
        onModelChanged(createDefaultButtonWhenEmpty());

//        if (!QH.isTesting()) {
//            throw new RuntimeException("请勿设置静态实例");
//        }


//        /*
//        每添加一个属性编辑，需要：
//        1. 创建编辑控件
//        2. grid添加一行
//        3. 将编辑控件存入binding中
//        4. updateModel中添加代码 根据model刷新内容
//         */
//        GridLayout gridLines = new GridLayout(c);
//        gridLines.setUseDefaultMargins(true);
//        gridLines.setOrientation(GridLayout.HORIZONTAL);
//        gridLines.setColumnCount(3);
//        gridLines.setClipChildren(false);
//        addOneGridLine(gridLines, "类型", scrollGroupType, null);
//        addView(gridLines);

//        addView(QH.getOneLineWithTitle(c, "类型", scrollGroupType, false));
//        addView(QH.getOneLineWithTitle(c, "颜色", imageBgColor, false));
//        addView(QH.getOneLineWithTitle(c, "颜色样式", scrollGroupColorStyle, false));
//        addView(QH.getOneLineWithTitle(c, "名称", editName, false));
//        addView(QH.getOneLineWithTitle(c, "坐标", tvCoordinate, false));

    }

    /**
     * 新建一个RadioGroup选项组。
     */
    public static HorizontalScrollView buildOptionsGroup(Context c, String[] optionNames, int[] optionInts, OptionsSelectCallback callback) {
        RadioGroup group = new RadioGroup(c);
        group.setOrientation(HORIZONTAL);
        for (int i = 0; i < optionInts.length; i++) {
            int csInt = optionInts[i];
            String name = optionNames[i];
            RadioButton btn = new RadioButton(c);
            btn.setText(name);
            btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) callback.onSelect(group, buttonView, csInt);
            });
            group.addView(btn);
        }
        HorizontalScrollView scroll = new HorizontalScrollView(c);
        scroll.addView(group);
        return scroll;
    }

    /**
     * 根据Model类型，判断btnType
     */
    public static int getButtonTypeFromModel(TouchAreaModel model) {
        int typeIndex = ModelProvider.indexOf(ModelProvider.modelClasses, model.getClass());
        return ModelProvider.modelTypeInts[typeIndex];
    }

    @Override
    public TouchAreaModel getModel() {
        return this.mModel;
    }

    //TODO 这个貌似会引起多次重复调用。要不再改改（isChanging放到这里防止重复调用？）
    @Override
    public void onModelChanged(TouchAreaModel model) {
        //如果是点到gestureArea上了，则应该在对应位置新建按钮
        if (model instanceof OneGestureArea) {
            float[] fingerXY = Const.getGestureContext().getFingerXYByType(FSMR.value.手指位置_最后移动, 0);
            mModel = createDefaultButtonWhenEmpty(); //新建一个没有toucharea关联的model，以当前model的属性为参考
            mModel.setLeft((int) (fingerXY[0] - mModel.getWidth() / 2f));
            mModel.setTop((int) (fingerXY[1] - mModel.getHeight() / 2f));
            Log.d(TAG, "onModelChanged: 点击到空白处，坐标=" + Arrays.toString(fingerXY));
        }
        //切换按钮类型时（不一定，也可能是点击了另一个触摸区域时）
        else if (mModel != model) { //行行行我每次都刷新行了吧
            mModel = model;
            int type = getButtonTypeFromModel(model);
            int line = mProps.get(PROP_KEY_UNIVERSAL).length;
            for(Integer typeInt : mProps.keySet()){
                if(typeInt.equals(PROP_KEY_UNIVERSAL)) continue;
                for(Prop<?> unused : mProps.get(typeInt)){
                    mPanelsNew.getChildAt(line).setVisibility(type == typeInt ? VISIBLE : GONE);
//                    mPanels[0].getChildAt(line).setVisibility(type == typeInt?VISIBLE:GONE);
//                    mPanels[1].getChildAt(line).setVisibility(type == typeInt?VISIBLE:GONE);
                    line ++;
                }
            }
        }

        for(Prop<?> prop : mProps.get(PROP_KEY_UNIVERSAL))
            if(!prop.isChangingSource())
                prop.updateUIFromModel(mModel);

        for(Prop<?> prop : mProps.get(getButtonTypeFromModel(mModel)))
            if(!prop.isChangingSource())
                prop.updateUIFromModel(mModel);

        Const.getTouchView().invalidate();
    }

    /**
     * 新建一个model，用于新建按钮时的选项，由于该按钮尚未添加到profile中，所以也没有对应的toucharea
     */
    private TouchAreaModel createDefaultButtonWhenEmpty() {
        //如果mModel不为null，则尽可能地复制此model的属性
        Class<? extends TouchAreaModel> clz = mModel != null ? mModel.getClass() : OneButton.class;
        return TouchAreaModel.newInstance(mModel, clz);
    }

    public interface OptionsSelectCallback {
        void onSelect(RadioGroup group, CompoundButton btn, int intValue);
    }

}
