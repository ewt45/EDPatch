package com.example.datainsert.exagear.controlsV2;

import static com.example.datainsert.exagear.controlsV2.Const.dp8;
import static com.example.datainsert.exagear.controlsV2.Const.minTouchSize;

import android.support.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 宽高和坐标xy设置为8dp的倍数
 */
public abstract class TouchAreaModel {
    private static final String TAG = "TouchAreaModel";
    public static final int TYPE_BUTTON = 0;
    public static final int TYPE_STICK = 1;
    public static final int TYPE_DPAD = 2;
    public static final int TYPE_GESTURE = 3;
    public static final int TYPE_COLUMN = 4;
    public static final int TYPE_NONE=-1;
    //保证编辑完成时该值不为空， keycodes至少存在一个按键
    private final List<Integer> keycodes;
    private int mainColor = Const.defaultTouchAreaBgColor;
    @Const.BtnColorStyle
    private int colorStyle = Const.BtnColorStyle.STROKE;
    @SerializedName(value = Const.GsonField.md_ModelType)
    @ModelType
    protected int modelType = TYPE_NONE;//这个要在每个子类里构造函数的时候设置成对应的
    protected int left = 0;
    protected int top = 0;
    protected int width;
    protected int height;
    transient protected int mMinAreaSize = minTouchSize;
    transient protected boolean isPressed = false;
    protected TouchAreaModel(@ModelType int type) {
        modelType = type;
        //保证初始时至少有一个keycode，以及其对应的名字
        keycodes = new ArrayList<>();
        keycodes.add(0);
//        name = getKeycodesString();
        //是先走到构造函数还是先走到变量的直接赋值？ （先变量直接赋值，再走到这里）
        width = mMinAreaSize;
        height = mMinAreaSize;
    }

    /**
     * 基于现有ref，新建一个同类型model的实例。
     */
    public static <T extends TouchAreaModel> T newInstance(TouchAreaModel ref,Class<T> tClass) {
        try {
            T one = tClass.newInstance();
            if(ref!=null)
                one.cloneOf(ref);
            return one;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getKeycodes() {
        //保证该值不为空， keycodes至少存在一个按键
        if(keycodes.isEmpty())
            keycodes.add(0);
        return keycodes;
    }

    /**
     * 将keycode改为新传入的，
     * <br/> 顺序由KeyOnBoardView那边排好。这里按传入的列表中的顺序原封不动设置到自身
     * <br/> 如果当前别名与默认keycode转换的别名相同，说明没有额外设置别名，那么更新keycode之后别名也应该刷新,因此重新设置keycode后，应注意name是否变化
     * <br/> 应至少存在一个按键，若传入按键为空，则keycodes列表元素只保留一个0
     *
     * @param newKeys 该按钮全部包括的按键。
     */
    public void setKeycodes(List<Integer> newKeys) {
//        boolean shouldUpdateName = name.equals(getKeycodesString());

//        for (int i = 0; i < keycodes.size(); i++)
//            if (!newKeys.contains(keycodes.get(i))) {
//                keycodes.remove(i);
//                i--;
//            }
//
//        for (int i = 0; i < newKeys.size(); i++)
//            if (!keycodes.contains(newKeys.get(i))) {
//                keycodes.add(newKeys.get(i));
//                i--;
//            }
        keycodes.clear();
        keycodes.addAll(newKeys);

        if (keycodes.isEmpty())
            keycodes.add(0);

//        if (shouldUpdateName)
//            name = getKeycodesString();
    }

    /**
     * 将keycode转为用户友好的按键名称，返回字符串。
     * <br/> 用于编辑界面显示当前已选择的按键码，以及未手动设置别名时获根据按键码获取默认别名
     */
    public String getKeycodesString() {
        return getKeycodesString(this.keycodes);
    }

    /**
     * 与 {@link #getKeycodesString()} 相同，但是是静态方法，需要传入指定的keycodes列表
     */
    public static String  getKeycodesString(List<Integer> keycodes){
        StringBuilder builder = new StringBuilder();
        for (Integer key : keycodes)
            builder.append(Const.getKeyOrPointerButtonName(key)).append(", ");
        if (builder.length() > 2)
            builder.delete(builder.length() - 2, builder.length());
        return builder.toString().replace('\n',' ');
    }

    public int getLeft() {
        return left;
    }

    /**
     * 设置为8dp的倍数
     */
    public void setLeft(int left) {
        this.left = Math.max(0, floorDivToSmallestUnit(left));
    }

    public int getTop() {
        return top;
    }

    /**
     * 设置为8dp的倍数
     */
    public void setTop(int top) {
        this.top = Math.max(0, floorDivToSmallestUnit(top));
    }

    public int getWidth() {
        return width;
    }

    /**
     * 设置为8dp的倍数
     */
    public void setWidth(int width) {
        this.width = Math.max(mMinAreaSize, floorDivToSmallestUnit(width));
    }

    public int getHeight() {
        return height;
    }

    /**
     * 设置为8dp的倍数
     */
    public void setHeight(int height) {
        this.height = Math.max(mMinAreaSize, floorDivToSmallestUnit(height));
    }

    public int getMainColor() {
        return mainColor;
    }

    public void setMainColor(int mainColor) {
        this.mainColor = mainColor;
    }

    public int getColorStyle() {
        return colorStyle;
    }

    public void setColorStyle(int colorStyle) {
        this.colorStyle = colorStyle;
    }

    /**
     * 为方便对齐，需要将left，top，width，height全部转换为dp8(对应的px值)的倍数，向下取整
     */
    public static int floorDivToSmallestUnit(int value){
        return Math.floorDiv(value, dp8) * dp8;
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
        if(ref==null)
            return;

//        modelType = ref.modelType; //草，这个自身类型不要复制啊，不然就变身了
        mainColor = ref.mainColor;
        setColorStyle(ref.getColorStyle());
        setLeft(ref.getLeft());
        setTop(ref.getTop());
        setWidth(ref.getWidth());
        setHeight(ref.getHeight()); //通过setter函数，这样能处理对应的限制
        setKeycodes(ref.keycodes);

        cloneSelfFields(ref);
    }

    abstract protected void cloneSelfFields(TouchAreaModel ref);

    @IntDef({TYPE_BUTTON, TYPE_STICK, TYPE_DPAD, TYPE_GESTURE, TYPE_COLUMN, TYPE_NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ModelType {
    }
}
