package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getOneLineWithTitle;
import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.TooltipCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.JoyStickBtn;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.ArrayList;
import java.util.List;

//https://elixir.bootlin.com/linux/v4.9/source/include/uapi/linux/input-event-codes.h#L74
//ex里kecode最大到111
//一个纵向滚动，宽高match_parent，，在里面放一个横向滚动，宽高wrap_content,再里面都是wrap。就可以横向纵向都可以滑动了
public class AvailableKeysView extends ScrollView implements CompoundButton.OnCheckedChangeListener {

    //存储对应keycode的显示名称
    public static String[] names = {
            "esc", "1/!", "2/@", "3/#", "4/$", "5/%", "6/^", "7/&", "8/*", "9/(", "0/)", "-/_", "=/+", "BackSpace",     //14个
            "Tab", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[/{", "]/}", "Enter",         //14个
            "LCtrl", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";/:", "'/\"", "`/~",             //13
            "LShift", "\\", "Z", "X", "C", "V", "B", "N", "M", ",/<", "./>", "/(?)", "RShift", "*",    //14
            "LAlt", "Space", "CapsLock", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "NumLock", "ScrollLock", //15
            "KP7", "KP8", "KP9", "KP-",                     //4
            "KP4", "KP5", "KKP6", "KP+",                    //4
            "KP1", "KP2", "KP3", "KP0",                     //4
            "KP.",                                          //1
            "F11", "F12",                                   //2
            "KPEnter", "RCtrl",                             //2
            "KP/", "SysRq", "RAlt", "LineFeed", "Home",     //5
            "↑", "PageUp", "←", "→", "End",                 //5
            "↓", "PageDn", "Insert", "Delete",              //4
            "Left","Middle","Right", "SCROLL_UP","SCROLL_DOWN",
            "SCROLL_CLICK_LEFT","SCROLL_CLICK_RIGHT",
    };
    public static int[] codes = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14
            , 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28
            , 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41
            , 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55
            , 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70
            , 71, 72, 73, 74
            , 75, 76, 77, 78
            , 79, 80, 81, 82
            , 83
            , 87, 88
            , 96, 97
            , 98, 99, 100, 101, 102
            , 103, 104, 105, 106, 107
            , 108, 109, 110, 111
            , 256+1,256+2,256+3,256+4,256+5
            ,256+6,256+7
    };//存储对应keycode的值
    //这三者的相同下标应该对应同一个key，但是下标并不能看做keycode
    public boolean[] keySelect = new boolean[names.length];//存储对应keycode选中情况
    private int mBtnSize = -2;
    private int mBtnWidth;
    private int mBtnHeight;
    private boolean mSelectOnlyOne=false; //是否最多只允许选择一个按钮
    CompoundButton mLastCheckedButton; //直接设置button行嘛，会不会赋值的时候乱套
    /**
     * 最外层的线性布局
     */
    private final LinearLayout rootLinear ;
    /**
     * 是否显示摇杆按钮
     */
    public int joyStickNum;

//    /**
//     * 两侧按键的构建
//     * @param context
//     */
//    public AvailableKeysView(Context context){
//        this(context,null,-1);
//    }

    /**
     * 自定义位置按键的构建
     */
    public AvailableKeysView(Context context, boolean[] preSelect,int joyNum) {
        super(context);
        this.joyStickNum=joyNum;
        mBtnSize = AndroidHelpers.dpToPx(50);
        mBtnWidth = AndroidHelpers.dpToPx(60);
        mBtnHeight = AndroidHelpers.dpToPx(50);
        if (preSelect != null && preSelect.length == this.keySelect.length)
            this.keySelect = preSelect;
//        setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
        addView(horizontalScrollView, new ViewGroup.LayoutParams(-2, -2));
        rootLinear = new LinearLayout(getContext());
        rootLinear.setOrientation(HORIZONTAL);
        int padding8dp = QH.px(context,8);
        rootLinear.setPadding(padding8dp,padding8dp,padding8dp,padding8dp);
        horizontalScrollView.addView(rootLinear);
        //用于装很多行的垂直线性布局
        LinearLayout verticalLinear1 = new LinearLayout(getContext());
        verticalLinear1.setOrientation(VERTICAL);
        rootLinear.addView(verticalLinear1, new ViewGroup.LayoutParams(-2, -2));
        //第1行 数字
        verticalLinear1.addView(getOneKeysLine(0, 14));
        //第234行 字母
        verticalLinear1.addView(getOneKeysLine(14, 28));
        verticalLinear1.addView(getOneKeysLine(28, 41));
        verticalLinear1.addView(getOneKeysLine(41, 55));
        verticalLinear1.addView(getOneKeysLine(55, 70));

        //又一个垂直线性布局，小键盘相关
        LinearLayout verticalLinear2 = new LinearLayout(getContext());
        verticalLinear2.setOrientation(VERTICAL);
        rootLinear.addView(verticalLinear2, new ViewGroup.LayoutParams(-2, -2));
        verticalLinear2.addView(getOneKeysLine(70, 74));
        verticalLinear2.addView(getOneKeysLine(74, 78));
        verticalLinear2.addView(getOneKeysLine(78, 82));
        verticalLinear2.addView(getOneKeysLine(82, 83));
        verticalLinear2.addView(getOneKeysLine(83, 85));
        //第三个垂直线性布局，小键盘和其他键。这个真的应该放在小键盘右边吗
        LinearLayout verticalLinear3 = new LinearLayout(getContext());
        verticalLinear3.setOrientation(VERTICAL);
        rootLinear.addView(verticalLinear3, new ViewGroup.LayoutParams(-2, -2));
        verticalLinear3.addView(getOneKeysLine(85, 87));
        verticalLinear3.addView(getOneKeysLine(87, 92));
        verticalLinear3.addView(getOneKeysLine(92, 97));
        verticalLinear3.addView(getOneKeysLine(97, 101));

        //摇杆按钮(记得在构造函数传入当前个数）
        //(要不还是都显示出来，然后禁用掉？点击的话就显示该按键仅在xxx中可以使用）
        if(joyStickNum>=0){
            LinearLayout joyBtnFrame = new LinearLayout(context);
            joyBtnFrame.setOrientation(VERTICAL);
            JoyStickBtn joyStickBtn = JoyStickBtn.getSample(context);
            int btnPx = QH.px(getContext(), 100);
            LinearLayout.LayoutParams joyParams = new LinearLayout.LayoutParams(btnPx,btnPx);
            joyParams.gravity= Gravity.CENTER;
            joyBtnFrame.addView(joyStickBtn,joyParams);

            LinearLayout linearSetJoyNum = new LinearLayout(context);
            Button btnAddJoyNum = new Button(context);
            btnAddJoyNum.setText("+");
            Button btnSubJoyNum = new Button(context);
            btnSubJoyNum.setText("-");
            TextView tvJoyNum = new TextView(context);
            tvJoyNum.setText(Integer.toString(joyStickNum));
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(-2,-2);
            tvParams.gravity=Gravity.CENTER;
            tvJoyNum.setPadding(padding8dp,0,padding8dp,0);
            tvJoyNum.setText(Integer.toString(joyStickNum));
            btnAddJoyNum.setOnClickListener(vBtnAdd-> {
                joyStickNum++;
                tvJoyNum.setText(Integer.toString(joyStickNum));
            });
            btnSubJoyNum.setOnClickListener(vBtnSub-> {
                joyStickNum=Math.max(0,joyStickNum-1);
                tvJoyNum.setText(Integer.toString(joyStickNum));
            });
            linearSetJoyNum.addView(btnSubJoyNum,new ViewGroup.LayoutParams(mBtnSize,mBtnSize));
            linearSetJoyNum.addView(tvJoyNum,tvParams);
            linearSetJoyNum.addView(btnAddJoyNum,new ViewGroup.LayoutParams(mBtnSize,mBtnSize));
            LinearLayout.LayoutParams linearSetJoyNumParams = new LinearLayout.LayoutParams(-2,-2);
            linearSetJoyNumParams.gravity= Gravity.CENTER;
            joyBtnFrame.addView(linearSetJoyNum,linearSetJoyNumParams);

            rootLinear.addView(getOneLineWithTitle(getContext(),RR.getS(RR.cmCtrl_allKeysJoyTitle),joyBtnFrame,true));
        }

    }

    /**
     * 默认没显示鼠标按键，调用这个会显示
     */
    public void showMouseBtn(){
        LinearLayout mouseLinear = new LinearLayout(getContext());
        mouseLinear.setOrientation(VERTICAL);
        LinearLayout line1 = getOneKeysLine(101,104);
        for(int i=0; i<line1.getChildCount(); i++){
            line1.getChildAt(i).getLayoutParams().width=-2;
            line1.getChildAt(i).getLayoutParams().height=-2;
            line1.getChildAt(i).setLayoutParams(line1.getChildAt(i).getLayoutParams());
        }
        mouseLinear.addView(line1);
        LinearLayout line2 = getOneKeysLine(104,106);
        for(int i=0; i<line2.getChildCount(); i++){
            line2.getChildAt(i).getLayoutParams().width=-2;
            line2.getChildAt(i).getLayoutParams().height=-2;
            line2.getChildAt(i).setLayoutParams(line2.getChildAt(i).getLayoutParams());
        }
        mouseLinear.addView(line2);
        LinearLayout line3 = getOneKeysLine(106,108);
        for(int i=0; i<line3.getChildCount(); i++){
            line3.getChildAt(i).getLayoutParams().width=-2;
            line3.getChildAt(i).getLayoutParams().height=-2;
            line3.getChildAt(i).setLayoutParams(line3.getChildAt(i).getLayoutParams());
        }
        mouseLinear.addView(line3);
        rootLinear.addView(getOneLineWithTitle(getContext(),RR.getS(RR.cmCtrl_allKeysMouseTitle),mouseLinear,true));
//        rootLinear.addView(BaseFragment.getOneLineWithTitle(getContext(),"鼠标",));
    }


    /**
     * 此视图一般都是显示在一个对话框中。使用该方法显示一个包含该视图的对话框，以及关闭对话框时的回调
     * @param listener 点击确定按钮时的监听
     */
    public void showWithinDialog(DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(getContext()).setView(this)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }

    /**
     * 生成布局时，生成一行按键，返回包含这行按键的一个线性布局
     */
    private LinearLayout getOneKeysLine(int start, int end) {
        LinearLayout linear = new LinearLayout(getContext());
        for (int i = start; i < end; i++) {
            ToggleButton button = new ToggleButton(getContext());
            button.setText(names[i]);
            button.setTextOn(names[i]);
            button.setTextOff(names[i]);
            button.setTag(i);//不如直接存下标吧，不存keycode了
            button.setChecked(keySelect[i]);
            button.setSingleLine();
            button.setOnCheckedChangeListener(this);
            TooltipCompat.setTooltipText(button, names[i]);
//            linear.addView(button, new ViewGroup.LayoutParams(mBtnWidth , mBtnHeight));
            linear.addView(button, new ViewGroup.LayoutParams(-2 , -2));

        }
        return linear;
    }

    /**
     * 获取当前选中的Keys
     *
     * @return OneCol，adapter中的一项
     */
    public OneKey[] getSelectedKeys() {
        List<OneKey> keyList = new ArrayList<>();
        for (int i = 0; i < keySelect.length; i++) {
            if (keySelect[i]) {
                keyList.add(new OneKey(codes[i], names[i]));
            }
        }
        Log.d("TAG", "getSelectedKeys: 选中的key为" + keyList.toString());
        return keyList.toArray(new OneKey[0]);
    }


    /**
     * 只允许选择一个按键。选择第二个按键时第一个按键会取消选择。
     * 用于摇杆设置按键的时候
     */
    public void selectOnlyOne() {
        mSelectOnlyOne=true;
    }

    /**
     * 获取最后一次选中的按钮。null说明没有选中任何按钮
     * 用于摇杆设置按键的时候
     */
    @Nullable
    public CompoundButton getLastCheckedButton() {
        return mLastCheckedButton;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int selfIndex = (int) buttonView.getTag();
        this.keySelect[selfIndex] = isChecked;
        //如果只允许选择一个按键，取消选择上一个按键
        if(mSelectOnlyOne && mLastCheckedButton !=null ){
            mLastCheckedButton.setChecked(false);
        }
        //如果本次是取消选择，不记录该按钮
        mLastCheckedButton = isChecked?buttonView:null;
    }

    /**
     * hugo的勾选了和没勾选看不出来，想个办法改一下高对比度的样式
     */
    private void setToggleButtonStyle(CompoundButton button, boolean isChecked){

    }
}
