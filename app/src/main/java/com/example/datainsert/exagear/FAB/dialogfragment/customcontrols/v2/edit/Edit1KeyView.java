package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.content.Context;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props.Prop;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props.Prop0MainColor;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props.Prop0Name;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props.Prop0Size;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props.Prop0Type;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props.Prop1Key;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props.Prop1Shape;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props.Prop1Trigger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props.Prop2Direction;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props.Prop2Key;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props.Prop3Key;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.DeserializerOfModel;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.ModelProvider;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneButton;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneProfile;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;
import com.example.datainsert.exagear.QH;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 显示用于修改按键属性的视图
 */
public class Edit1KeyView extends LinearLayout implements Prop.Host<TouchAreaModel> {
    private static final String TAG = "KeyPropertiesView";

//    private static final List<Class<? extends TouchAreaModel>> mTypeClasses =
//            Arrays.asList(OneButton.class, OneStick.class, OneDpad.class);

    final Edit0Main mHost;
    TouchAreaModel mModel = null;
    Prop<TouchAreaModel>[][] mProps;
    LinearLayout[] mPanels = new LinearLayout[2];


    public Edit1KeyView(Edit0Main host) {
        super(host.getContext());
        mHost = host;
        Context c = host.getContext();
        setOrientation(VERTICAL);
        //TODO 调整设置后，再新建的按钮 默认设置也随之更改。（再出一个复制按钮功能吧）
        // onDraw不会自己执行，每个更改属性的选项都要调用view.invalidate()

        //工具栏
        Button btnAddKey = new Button(c);
        btnAddKey.setText("添加");
        btnAddKey.setOnClickListener(v -> {
            TouchAreaModel newModel = TouchAreaModel.newInstance(mModel,mModel.getClass());
            mHost.mHost.getProfile().addModelAndAddArea( newModel,true);
            mModel = newModel;
            mModel.setLeft(0);
            mModel.setTop(0);
            mHost.mHost.invalidate();
        });

        Button btnDelKey = new Button(c);
        btnDelKey.setText("删除当前");
        btnDelKey.setOnClickListener(v-> {
            mHost.mHost.getProfile().removeModelAndArea(mModel);
            mHost.mHost.invalidate();
        });

        Button btnTest = new Button(c);
        btnTest.setText("测试保存");
        btnTest.setOnClickListener(v -> ModelProvider.saveProfile(Const.touchAreaViewRef.get().getProfile()));

        Button btnTestRead  = new Button(c);
        btnTestRead.setText("测试读取state");
        btnTestRead.setOnClickListener(v->{
            String json = "{\"modelList\":[{\"allStateList\":[{\"id\":1,\"stateType\":2},{\"id\":2,\"stateType\":3},{\"countDownMs\":250,\"fastMoveThreshold\":36.0,\"fingerIndex\":0,\"noMoveThreshold\":12.0,\"id\":3,\"niceName\":\"\",\"stateType\":1}],\"tranActionsList\":[[]],\"tranEventList\":[1],\"tranPostStateList\":[1],\"tranPreStateList\":[2],\"colorStyle\":0,\"height\":1600,\"keycodes\":[0],\"left\":0,\"mMinAreaSize\":80,\"mainColor\":-1286,\"modelType\":3,\"name\":\"None\",\"top\":0,\"width\":2560}],\"name\":\"1\",\"version\":0}\n";
            Gson gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(TouchAreaModel.class, new DeserializerOfModel())
                    .create();
            OneProfile oneProfile = gson.fromJson(json, OneProfile.class);
            System.out.println("state个数="+oneProfile.getGestureAreaModel().getAllStateList().size());
        });

        LinearLayout linearToolbar = new LinearLayout(c);
        linearToolbar.setOrientation(HORIZONTAL);
        linearToolbar.addView(btnAddKey);
        linearToolbar.addView(btnDelKey);
        linearToolbar.addView(btnTest);
        linearToolbar.addView(btnTestRead);
        HorizontalScrollView scrollToolbar = new HorizontalScrollView(c);
        scrollToolbar.addView(linearToolbar);


        //仅处理新model生成和更新对应的toucharea显示，调用onModelChanged在prop里调用
        Prop0Type.OnTypeChangeListener typeChangeListener = (newType) -> {
            //先备份一下旧model。然后生成新model，替换ui，填充数据。
            // 然后再看看这个model是否和toucharea已经关联起来了，如果连了的话就把toucharea删了重新建一个
            TouchAreaModel newModelForField= TouchAreaModel.newInstance(mModel,ModelProvider.getModelClass(newType));
            List<TouchArea<?>> currList = mHost.mHost.getProfile().getTouchAreaList();
            for (int i = 0; i < currList.size(); i++) {
                //如果没进到这个条件，mmodel就不会改变。。。
                if (currList.get(i).getModel().equals(mModel)) {
                    mHost.mHost.getProfile().removeModelAndArea(i);
                    mHost.mHost.getProfile().addModelAndAddArea( i, newModelForField,true);
                    break;
                }
            }
            //如果是没关联touchArea，就用传进来的ref，否则用对应area的新建的model，设置到自身成员变量上
            onModelChanged(newModelForField);
        };


        for (int i = 0; i < mPanels.length; i++) {
            mPanels[i] = new LinearLayout(c);
            mPanels[i].setOrientation(VERTICAL);
            mPanels[i].setVerticalGravity(Gravity.CENTER_VERTICAL);
        }

        mProps = new Prop[][]{
                {new Prop0Type(this, c, typeChangeListener), new Prop0MainColor(this, c), new Prop0Name(this, c), new Prop0Size(this, c)},
                {new Prop1Key(this, c), new Prop1Shape(this, c), new Prop1Trigger(this, c)},
                {new Prop2Key(this, c), new Prop2Direction(this, c)},
                {new Prop3Key(this, c)}};

        LayoutParams h48Params = new LayoutParams(-1, dp8 * 6);
        h48Params.setMarginStart(dp8);
        h48Params.topMargin = dp8;

        for (Prop[] propGroup : mProps) {
            boolean hide = propGroup != mProps[0] && propGroup != mProps[1];
            for (Prop prop : propGroup) {
                TextView title = QH.getTitleTextView(c, prop.getTitle());
                title.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout linear = new LinearLayout(c);
                linear.setOrientation(VERTICAL);
                linear.setVerticalGravity(Gravity.CENTER_VERTICAL);
                linear.addView(prop.mMainView);

                //没法在外层用滚动视图，否则seekbar之类的没法填满横向
//                HorizontalScrollView scroll = new HorizontalScrollView(c);
//                HorizontalScrollView.LayoutParams paramsLinear = new FrameLayout.LayoutParams(-2, -1);
////                paramsLinear.gravity = Gravity.CENTER_VERTICAL; //setForegroundGravity行吗
//                scroll.addView(linear, paramsLinear);

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

                mPanels[0].addView(title, h48Params);
                mPanels[1].addView(linear, h48Params);
            }
        }

        LinearLayout linearPanelsWrapper = new LinearLayout(c);
        linearPanelsWrapper.setOrientation(HORIZONTAL);
        linearPanelsWrapper.addView(mPanels[0]);
        linearPanelsWrapper.addView(mPanels[1], QH.LPLinear.one().weight().to());

        addView(scrollToolbar);
        addView(linearPanelsWrapper,QH.LPLinear.one(-2,-2).to());
        addView(LayoutInflater.from(getContext()).inflate(R.layout.aaa_test_relative, this, false));

        //初始的时候成员变量为null，然后传入一个实例，这样才会判断不等，调用inflate初次生成视图
        onModelChanged(new OneButton());

        if(!QH.isTesting()){
            throw new RuntimeException("请勿设置静态实例");
        }

        Const.editKeyViewRef = null;
        Const.editKeyViewRef = new WeakReference<>(this);

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
    //TODO 这样做并不是很安全，因为是拿int当下标用了，现在只有三个类型的按钮，正好对应int是0 1 2，以后要再加的话可能有问题
    public static int getButtonTypeFromModel(TouchAreaModel model) {
        int typeIndex = 0;
        for(; typeIndex<ModelProvider.modelClasses.length; typeIndex++)
            if(ModelProvider.modelClasses[typeIndex].equals(model.getClass()))
                break;

        return ModelProvider.modelTypeInts[typeIndex];
//        int index = Const.modelTypeArray.indexOfValue(model.getClass());
//        if (index < 0)
//            throw new RuntimeException("无法识别的model类型");
//        return Const.modelTypeArray.keyAt(index);
    }

    /**
     * 根据当前编辑界面选择的选项，返回btnType
     */
    public static int getButtonTypeCurrentSelect(RadioGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            if (((RadioButton) group.getChildAt(i)).isChecked())
                return i;
        }
        throw new RuntimeException("没有选项被勾选");
    }


    @Override
    public TouchAreaModel getModel() {
        return this.mModel;
    }


    @Override
    public void onModelChanged(TouchAreaModel model) {
        //切换按钮类型时（不一定，也可能是点击了另一个触摸区域时）
        if (mModel != model) { //行行行我每次都刷新行了吧
            mModel = model;
            int type = getButtonTypeFromModel(model);
            int line = mProps[0].length;
            for (int i = 1; i < mProps.length; i++)
                for (Prop unused : mProps[i]) {
                    mPanels[0].getChildAt(line).setVisibility(type == (i - 1) ? VISIBLE : GONE);
                    mPanels[1].getChildAt(line).setVisibility(type == (i - 1) ? VISIBLE : GONE);
                    line++;
                }
        }

        for (Prop prop : mProps[0])
            if (!prop.isChangingSource())
                prop.updateUIFromModel(mModel);

        for (Prop prop : mProps[getButtonTypeFromModel(mModel) + 1])
            if (!prop.isChangingSource())
                prop.updateUIFromModel(mModel);

        mHost.mHost.invalidate();
    }

    public interface OptionsSelectCallback {
        void onSelect(RadioGroup group, CompoundButton btn, int intValue);
    }

}
