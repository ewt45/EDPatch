package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.margin8;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.minTouchSize;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper.getTextButton;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneButton;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneStick;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.colorpicker.ColorPicker;
import com.example.datainsert.exagear.QH;

import java.util.Arrays;
import java.util.List;

/**
 * 显示用于修改按键属性的视图
 */
public class KeyPropertiesView extends LinearLayout {
    private static final String TAG = "KeyPropertiesView";
    private final EditConfigWindow mHost;
    Binding mBinding = new Binding();
    String[] mTypeNames = new String[]{"按钮", "摇杆", "十字键"};
    //直接用mModel来表示当前选择的是哪一个类型就行了
    int[] mTypeInts = new int[]{Const.BtnType.NORMAL, Const.BtnType.STICK, Const.BtnType.DPAD};
    Class[] mTypeClasses = new Class[]{OneButton.class};
    TouchAreaModel mModel = null;
    List<KeySubView<? extends TouchAreaModel>> mSubViews = Arrays.asList(new KeySub1Normal());

    public KeyPropertiesView(EditConfigWindow host) {
        super(host.getContext());
        mHost = host;
        Context c = host.getContext();
        setOrientation(VERTICAL);
        //TODO 调整设置后，再新建的按钮 默认设置也随之更改。（再出一个复制按钮功能吧）
        // onDraw不会自己执行，每个更改属性的选项都要调用view.invalidate()

        //工具栏
        Button btnAddKey = new Button(c);
        btnAddKey.setText("添加");
        btnAddKey.setOnClickListener(v -> onCreateNewTouchArea());
        Button btnDelKey = new Button(c);
        btnDelKey.setText("删除当前");
        LinearLayout linearToolbar = new LinearLayout(c);
        linearToolbar.setOrientation(HORIZONTAL);
        linearToolbar.addView(btnAddKey);

        //类型
        HorizontalScrollView scrollGroupType = buildOptionsGroup(
                mTypeNames,
                mTypeInts,
                (group, btn, intValue) -> {
                    if (getButtonTypeFromModel(mModel) == intValue)
                        return;

                    //TODO 实时从按钮变为摇杆有点难。还是删了当前touchArea再新建一个吧
                    //先备份一下旧model。然后生成新model，替换ui，填充数据。
                    // 然后再看看这个model是否和toucharea已经关联起来了，如果连了的话就把toucharea删了重新建一个
                    TouchAreaModel oldModel = mModel;

                    KeySubView<? extends TouchAreaModel> keySubView = getKeySubView(intValue);
                    mModel = keySubView.adaptModel(oldModel);

                    List<TouchArea<?>> currList = mHost.mHost.getProfile().getTouchAreaList();
                    for (int i = 0; i < currList.size(); i++) {
                        TouchArea<?> area = currList.get(i);
                        if (!area.getModel().equals(oldModel))
                            continue;

                        currList.remove(area);
                        TouchArea<?> newArea = onCreateNewTouchArea();
                        currList.add(i, newArea);
                    }
                });


        //名称
        EditText editName = new EditText(c);
        editName.setSingleLine(true);
        editName.addTextChangedListener((QH.SimpleTextWatcher) s -> {
            mModel.name = s.toString();
            mHost.mHost.invalidate();
        });


        //坐标
        TextView tvCoordinate = getTextButton(c, "");
        tvCoordinate.setText("编辑");
        //TODO 改成dialog？ 添加宽高编辑
        tvCoordinate.setOnClickListener(v -> {
            EditText editPlaceX = new EditText(c);
            editPlaceX.setSingleLine(true);
            editPlaceX.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

            EditText editPlaceY = new EditText(c);
            editPlaceY.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            editPlaceY.setSingleLine(true);

            LinearLayout linearPlace = new LinearLayout(c);
            linearPlace.setOrientation(HORIZONTAL);
            linearPlace.setPadding(margin8, margin8, margin8, margin8);
            linearPlace.addView(editPlaceX, QH.LPLinear.one(0, -2).weight().to());
            linearPlace.addView(editPlaceY, QH.LPLinear.one(0, -2).weight().left().to());

            PopupWindow popupWindow = new PopupWindow(c);
            Button btnConfirm = new Button(c);
            btnConfirm.setText("确定");
            btnConfirm.setOnClickListener(v2 -> {
                String xStr = editPlaceX.getText().toString().trim();
                String yStr = editPlaceY.getText().toString().trim();
                mModel.setLeft(xStr.length() == 0 ? 0 : Integer.parseInt(xStr));
                mModel.setTop(yStr.length() == 0 ? 0 : Integer.parseInt(yStr));
                updateModel(mModel);
                popupWindow.dismiss();
            });
            LinearLayout linearPopupRoot = new LinearLayout(c);
            linearPopupRoot.setOrientation(HORIZONTAL);
            linearPopupRoot.addView(linearPlace, QH.LPLinear.one(0, -2).weight().to());
            linearPopupRoot.addView(btnConfirm, QH.LPLinear.one(-2, -2).to());
            popupWindow.setContentView(linearPopupRoot);
            popupWindow.setWidth(getWidth());
            popupWindow.setHeight(WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new ColorDrawable(TestHelper.getBGColor(c)));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.showAsDropDown((View) v.getParent());
        });

        //颜色
        ImageView imageBgColor = new ImageView(c);
        GradientDrawable colorDrawable = new GradientDrawable();
        imageBgColor.setImageDrawable(ColorPicker.wrapAlphaAlertBg(c, colorDrawable));
        imageBgColor.setMinimumHeight(minTouchSize);
        imageBgColor.setOnClickListener(v -> {
            LinearLayout linearColorRoot = new LinearLayout(c);
            linearColorRoot.setOrientation(VERTICAL);
            linearColorRoot.addView(new ColorPicker(c, mModel.mainColor, argb -> {
                mModel.mainColor = argb;
                colorDrawable.setColor(argb);
                mHost.mHost.invalidate();
            }));
            new AlertDialog.Builder(c)
                    .setView(TestHelper.wrapAsScrollView(linearColorRoot))
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(false)
                    .show();
        });

        //颜色样式
        HorizontalScrollView scrollGroupColorStyle = buildOptionsGroup(
                new String[]{"描边", "填充"},
                new int[]{Const.BtnColorStyle.STROKE, Const.BtnColorStyle.FILL},
                (group, btn, intValue) -> mModel.colorStyle = intValue);

        //每个model单独的属性
        LinearLayout linearSubProps = new LinearLayout(c);
        linearSubProps.setOrientation(VERTICAL);

        addView(linearToolbar);
        //TODO 改为gridview？让标题对齐？
        addView(QH.getOneLineWithTitle(c, "类型", scrollGroupType, false));
        addView(QH.getOneLineWithTitle(c, "颜色", imageBgColor, false));
        addView(QH.getOneLineWithTitle(c, "颜色样式", scrollGroupColorStyle, false));
        addView(QH.getOneLineWithTitle(c, "名称", editName, false));
        addView(QH.getOneLineWithTitle(c, "坐标", tvCoordinate, false));
        addView(linearSubProps);

        mBinding.scrollGroupType = (RadioGroup) scrollGroupType.getChildAt(0);
        mBinding.editName = editName;
        mBinding.imageBgColor = colorDrawable;
        mBinding.groupColorStyle = (RadioGroup) scrollGroupColorStyle.getChildAt(0);
        mBinding.linearSubProps = linearSubProps;

        //初始的时候成员变量为null，然后传入一个实例，这样才会判断不等，调用inflate初次生成视图
        updateModel(OneButton.newInstance(null));

    }

    /**
     * 用新数据填充视图内容。若model与成员变量model不同，则新model变为成员变量
     */
    public void updateModel(TouchAreaModel model) {
        //TODO 要不每个类型都存一个成员变量。视图的话，先分出共有的属性，固定显示，然后再把每个类型特有的属性显示了
        if (mModel != model) {
            mModel = model;
            mBinding.linearSubProps.removeAllViews();
            mBinding.linearSubProps.addView(getKeySubView(model).inflate(this));
        }
        //TODO 目前的设想流程是这样的：修改model->调用updateModel根据model刷新视图的内容；onDraw的时候会自动读取到最新的model内容所以不用手动刷新吧？
//        int index = typeList.indexOf(oneButton.type);
//        mBinding.scrollGroupStyle.setText(typeNameList.get(index));
        ((RadioButton) mBinding.scrollGroupType.getChildAt(getButtonTypeFromModel(model))).setChecked(true);

        mBinding.editName.setText(mModel.name);
        mBinding.imageBgColor.setColor(model.mainColor);
        ((RadioButton) mBinding.groupColorStyle.getChildAt(mModel.colorStyle)).setChecked(true);

        getKeySubView(model).updateUI(model);

    }

    /**
     * 新建一个RadioGroup选项组。
     */
    HorizontalScrollView buildOptionsGroup(String[] optionNames, int[] optionInts, OptionsSelectCallback callback) {
        Context c = getContext();
        RadioGroup group = new RadioGroup(c);
        group.setOrientation(HORIZONTAL);
        for (int i = 0; i < optionInts.length; i++) {
            int csInt = optionInts[i];
            String name = optionNames[i];
            RadioButton btn = new RadioButton(c);
            btn.setText(name);
            btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    callback.onSelect(group, buttonView, csInt);
                    mHost.mHost.invalidate();
                }
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
    private int getButtonTypeFromModel(TouchAreaModel model) {
        if (model instanceof OneStick)
            return Const.BtnType.STICK;
        else if (model instanceof OneButton)
            return Const.BtnType.NORMAL;
        else throw new RuntimeException("无法识别的model类型");
    }


    /**
     * 根据当前编辑界面选择的选项，返回btnType
     */
    private int getButtonTypeCurrentSelect(RadioGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            if (((RadioButton) group.getChildAt(i)).isChecked())
                return i;
        }
        throw new RuntimeException("没有选项被勾选");
    }

    /**
     * 根据给定的class（或int？）返回对应的subview
     */
    private KeySubView getKeySubView(TouchAreaModel model) {
        return mSubViews.get(touchModelToIntValue(model));
    }

    /**
     * 点击添加按钮时新建触摸区域。或者切换类型时删除原有并重新添加触摸区域
     * <br/> 注意该函数会生成新的model实例并赋值到mModel上，因此调用后mModel对象会改变
     */
    private TouchArea<?> onCreateNewTouchArea() {
        //这里不能直接传model，而是应该新建一个model，放到自身并传到toucharea。因为如果新建了但不放到自身，那么toucharea创建之后，model没有和自身联系起来。如果不新建，那么多个area就会共用一个 model
        TouchArea<?> area = TestHelper.newAreaEditable(mHost.mHost, mModel, model -> updateModel(model));
        mModel = area.getModel();
        updateModel(mModel); //新建后应该立刻调用一次，否则无法同步SubView里的model
        mHost.mHost.getProfile().addTouchArea(area);
        mHost.mHost.invalidate();
        return area;
    }

    private KeySubView<? extends TouchAreaModel> getKeySubView(int intValue) {
        return mSubViews.get(intValue);
    }

    private int touchModelToIntValue(TouchAreaModel model) {
        for (int i = 0; i < mTypeClasses.length; i++)
            if (mTypeClasses[i].equals(model.getClass()))
                return i;
        throw new RuntimeException("无法识别model类型");
    }

    interface OptionsSelectCallback {
        void onSelect(RadioGroup group, CompoundButton btn, int intValue);
    }

    private static class Binding {
        public LinearLayout linearSubProps;
        RadioGroup scrollGroupType;
        EditText editName;

        GradientDrawable imageBgColor;

        RadioGroup groupColorStyle;
    }
}
