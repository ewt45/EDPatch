package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.minTouchSize;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public abstract class TouchAreaModel {
    private static final String TAG = "TouchAreaModel";
    public static final int TYPE_BUTTON = 0;
    public static final int TYPE_STICK = 1;
    public static final int TYPE_DPAD = 2;
    public static final int TYPE_GESTURE = 3;
    public static final int TYPE_NONE=-1;
    protected final List<Integer> keycodes;
    public int mainColor = Const.defaultBgColor;
    @Const.BtnColorStyle
    public int colorStyle = Const.BtnColorStyle.STROKE;
    @SerializedName(value = Const.GsonField.md_ModelType)
    @ModelType
    protected int modelType = TYPE_NONE;//这个要在每个子类里构造函数的时候设置成对应的
    //TODO 保证编辑完成时改值不为空， keycodes至少存在一个按键
    protected String name;
    protected int left = 0;
    protected int top = 0;
    protected int width;
    protected int height;
    protected int mMinAreaSize = minTouchSize;
    transient protected boolean isPressed = false;
    protected TouchAreaModel(@ModelType int type) {
        modelType = type;
        //保证初始时至少有一个keycode，以及其对应的名字
        keycodes = new ArrayList<>();
        keycodes.add(0);
        name = getKeycodesString();
        //TODO 像这种初始化用到别的地方的变量的，构造函数不为空的，会出问题吗
        width = mMinAreaSize;
        height = mMinAreaSize;
    }

    /**
     * 基于现有ref，新建一个同类型model的实例。
     */
    public static <T extends TouchAreaModel> T newInstance(TouchAreaModel ref,Class<T> tClass) {
        try {
            T one = tClass.newInstance();
            one.cloneOf(ref);
            return one;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getKeycodes() {
        return keycodes;
    }

    /**
     * 将keycode改为新传入的，会尽力保留已有keycode的顺序
     * <br/> 如果当前别名与默认keycode转换的别名相同，说明没有额外设置别名，那么更新keycode之后别名也应该刷新,因此重新设置keycode后，应注意name是否变化
     * <br/> 应至少存在一个按键，若传入按键为空，则keycodes列表元素只保留一个0
     *
     * @param newKeys 该按钮全部包括的按键。
     */
    public void setKeycodes(List<Integer> newKeys) {
        boolean shouldUpdateName = name.equals(getKeycodesString());

        for (int i = 0; i < keycodes.size(); i++)
            if (!newKeys.contains(keycodes.get(i))) {
                keycodes.remove(i);
                i--;
            }

        for (int i = 0; i < newKeys.size(); i++)
            if (!keycodes.contains(newKeys.get(i))) {
                keycodes.add(newKeys.get(i));
                i--;
            }

        if (keycodes.size() == 0)
            keycodes.add(0);

        if (shouldUpdateName)
            name = getKeycodesString();
    }

    /**
     * 将keycode转为用户友好的按键名称，返回字符串。
     * <br/> 用于编辑界面显示当前已选择的按键码，以及未手动设置别名时获根据按键码获取默认别名
     */
    public String getKeycodesString() {
        StringBuilder builder = new StringBuilder();
        for (Integer key : keycodes)
            builder.append(Const.keyNames[key]).append(", ");
        if (builder.length() > 2)
            builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }

    /**
     * 返回用户友好的别名。若用户没有设置，则该别名为该按钮所有按键码的别名（getKeycodesString()）
     */
    public String getName() {
        return name;
    }

    /**
     * 更新该按钮的别名。若别名trim后为长度为0，则变为所有按键码的别名（getKeycodesString()）
     */
    public void setName(String newName) {
        this.name = (newName.trim().length() == 0)
                ? getKeycodesString() : newName;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = Math.max(0, left);
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = Math.max(0, top);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = Math.max(mMinAreaSize, width);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = Math.max(mMinAreaSize, height);
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }


    /**
     * 深拷贝。子类应该重写这个函数，完善 拷贝自身独有的变量
     */
    public void cloneOf(TouchAreaModel ref){
        keycodes.clear();
        keycodes.addAll(ref.keycodes);
        mainColor = ref.mainColor;
        colorStyle = ref.colorStyle;
        name = ref.name;
        left = ref.left;
//        modelType = ref.modelType; //草，这个自身类型不要复制啊，不然就变身了

        setLeft(ref.getLeft());
        setTop(ref.getTop());
        setWidth(ref.getWidth());
        setHeight(ref.getHeight()); //通过setter函数，这样能处理对应的限制
        colorStyle = ref.colorStyle;
        mainColor = ref.mainColor;
        setKeycodes(ref.keycodes);

        cloneSelfFields(ref);
    }

    abstract protected void cloneSelfFields(TouchAreaModel ref);

    @IntDef({TYPE_BUTTON, TYPE_STICK, TYPE_DPAD, TYPE_GESTURE,TYPE_NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ModelType {
    }
}
