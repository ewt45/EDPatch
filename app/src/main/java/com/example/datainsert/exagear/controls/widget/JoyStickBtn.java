package com.example.datainsert.exagear.controls.widget;

import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getPreference;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.setDialogTooltip;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_ALPHA;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_BG_COLOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_HEIGHT;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_WIDTH;
import static com.example.datainsert.exagear.controls.model.JoyParams.PresetKey.ARROWS;
import static com.example.datainsert.exagear.controls.model.JoyParams.PresetKey.CUSTOM;
import static com.example.datainsert.exagear.controls.model.JoyParams.PresetKey.MOUSE_LEFT_CLICK;
import static com.example.datainsert.exagear.controls.model.JoyParams.PresetKey.MOUSE_MOVE;
import static com.example.datainsert.exagear.controls.model.JoyParams.PresetKey.MOUSE_RIGHT_CLICK;
import static com.example.datainsert.exagear.controls.model.JoyParams.PresetKey.WASD;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
import com.eltechs.axs.PointerEventReporter;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.AvailableKeysView;
import com.example.datainsert.exagear.FAB.widget.SimpleItemSelectedListener;
import com.example.datainsert.exagear.FAB.widget.SpinArrayAdapterSmSize;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters.MouseViewportAdapter;
import com.example.datainsert.exagear.controls.model.JoyParams;

import java.util.ArrayList;
import java.util.List;

/**
 * up,cancel other, 使用状态下松手回到起始位置
 * <p>
 * 需要设置按键请通过setViewfacade传入viewfacade
 */
public class JoyStickBtn extends BaseMoveBtn {
    private static final String TAG = "JoyStickBtn";
    private static final float tanPiDiv8 = 0.414213562f;
    private static final float cotPiDiv8 = 2.414213562f;
    private final static int BTN_INVALID_DIAM = -100;
    /**
     * 外部轮廓的宽度是内部按钮宽度的多少倍
     */
    private static final float outerDivInnerWidthRatio = 1.85f;
    /**
     * 摇杆内部按钮宽度是普通按钮宽度的多少倍
     */
    private static final float innerDivBtnWidthRatio = 0.9f;
    private final JoyParams mParams;
    /**
     * 布局圆心在父布局的xy,固定不变。
     */
    private final PointF mOriginXYPoint = new PointF();
    /**
     * 布局圆心在父布局的xy。用于绘制按钮样式时确定位置. 手指按下时该数据会变化
     */
    private final PointF mCenterXYPoint = new PointF();
    /**
     * 当前按钮样式应该设置到的圆心位置,相对于按钮的父布局
     */
    private final PointF mLastTouchXYPoint = new PointF();
    //    /**
//     * 手指按下时的xy，使用状态下，用来比较当前手指移动到的位置是哪个方向。
//     */
//    private final PointF mLastXYPoint = new PointF();
    private final List<Integer> lastMovingDirections = new ArrayList<>();
    private final Paint mOutLinePaint = new Paint();//外轮廓
    private final Paint mBtnPaint = new Paint(); //按钮样式
    //    private ViewFacade mViewFacade;
    private ViewOfXServer mViewOfXServer;
    private PointerEventReporter mPointReporter;
    private MouseMoveAdapter mMousePosInject;
    /**
     * 类型为鼠标移动时，两次发送鼠标移动事件的时间间隔（毫秒）。之后应该移到JoyParams中
     * 先固定50吧。
     */
    public static int mTimerRepeatMs=25;
    /**
     * 类型为鼠标移动时，每次发送鼠标移动事件 移动的距离。之后应该移到JoyParams中
     */
    private static int mMouseMoveLength = 20;


    /**
     * 按钮和圆形背景直径
     */
    private int btnDiam;
    private int outerDiam;

    public JoyStickBtn(Context context, JoyParams params) {
        super(context);
        mParams = params;
        setupStyle();


    }

    public static JoyStickBtn getSample(Context c) {
        final int btnDiam = QH.px(c, 50);
        final int outerDiam = btnDiam * 2;
        JoyStickBtn sample = new JoyStickBtn(c, new JoyParams()) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return true;
            }

            @Override
            public void injectMove(Finger finger) {

            }

            @Override
            public void injectRelease(Finger finger) {

            }

            @Override
            public void injectPress(Finger finger) {

            }
        };
        //设置宽高
        sample.btnDiam = QH.px(c, 50);
        sample.outerDiam = sample.btnDiam * 2;
        //设置颜色为黑白
        sample.mBtnPaint.setColor(0xff848484);
        sample.mBtnPaint.setStrokeWidth(QH.px(c, btnDiam / 30f));
        sample.mOutLinePaint.setColor(0xffc9c9c9);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(btnDiam * 2, btnDiam * 2);
        sample.setLayoutParams(params);
        return sample;
    }

    public void setViewFacade(ViewOfXServer viewOfXServer) {
        mViewOfXServer = viewOfXServer;
        if (mViewOfXServer != null){
            mPointReporter = new PointerEventReporter(viewOfXServer);
            mMousePosInject = new MouseViewportAdapter(mViewOfXServer,mPointReporter);
        }
    }

    @Override
    protected void updateModelMargins(int marginLeft, int marginTop) {
        mParams.setMarginLeft(marginLeft); //getleft()+mLastX
        mParams.setMarginTop(marginTop);
    }

    @Override
    public void injectPress(Finger finger) {
        //设置整体偏移
        setTranslationX(finger.getX() - mCenterXYPoint.x);
        setTranslationY(finger.getY() - mCenterXYPoint.y);
        //记录本次移动时的起始位置
        mLastTouchXYPoint.x = finger.getX();
        mLastTouchXYPoint.y = finger.getY();
        //将手指初次按下的位置设置为按钮圆心位置，之后的移动都以这个为基准
        mCenterXYPoint.x = finger.getX();
        mCenterXYPoint.y = finger.getY();


        lastMovingDirections.clear();

        if (mParams == null || mViewOfXServer == null)
            return;

        if (mParams.getPresetKey() == MOUSE_MOVE) {
            Log.d(TAG, String.format("handleInjectStart: 中央？传入的坐标是viewofxserver宽高的一半？ %d, %d", mViewOfXServer.getWidth() / 2, mViewOfXServer.getHeight() / 2));
            //设置位置到中央
//            mPointReporter.pointerMove(mViewOfXServer.getWidth() / 2f, mViewOfXServer.getHeight() / 2f);
        } else if (mParams.getPresetKey() == MOUSE_LEFT_CLICK || mParams.getPresetKey() == MOUSE_RIGHT_CLICK) {
            //设置位置到中央并按下
            mPointReporter.pointerMove(mViewOfXServer.getWidth() / 2f, mViewOfXServer.getHeight() / 2f);
            mPointReporter.buttonPressed(mParams.getPresetKey().getKeys()[0]);
        } else {
//            injectMultiKeysByDirections(mLastTouchXYPoint.x - mCenterXYPoint.x, mLastTouchXYPoint.y - mCenterXYPoint.y);
        }
        //重新绘制按钮样式
        invalidate();
    }

    @Override
    public void injectMove(Finger finger) {
        //使用模式下，限制移动距离
        float dx = finger.getX() - mCenterXYPoint.x;//+mDownLeftTopPoint.x
        float dy = finger.getY() - mCenterXYPoint.y;//+mDownLeftTopPoint.y

        if ((dx == 0 && dy == 0) || mParams == null) { //|| mViewOfXServer==null
            return;
        }
        injectMultiKeysByDirections(dx, dy);
        mLastTouchXYPoint.x = finger.getX();
        mLastTouchXYPoint.y = finger.getY();
        invalidate();
    }

    @Override
    public void injectRelease(Finger finger) {
        setTranslationX(0);
        setTranslationY(0);
        //重置触摸位置
        //设置中心位置
        if (mParams != null) {
            mCenterXYPoint.x = mParams.getMarginLeft() + outerDiam / 2f;
            mCenterXYPoint.y = mParams.getMarginTop() + outerDiam / 2f;
        } else {
            mCenterXYPoint.x = outerDiam / 2f;
            mCenterXYPoint.y = outerDiam / 2f;
        }
        mLastTouchXYPoint.x = mCenterXYPoint.x;
        mLastTouchXYPoint.y = mCenterXYPoint.y;
        invalidate();

        if (mParams == null || mViewOfXServer == null)
            return;
        if (mParams.getPresetKey() == MOUSE_MOVE) {
            //设置位置到中央
//            mPointReporter.pointerMove(mViewOfXServer.getWidth() / 2f, mViewOfXServer.getHeight() / 2f);

        } else if (mParams.getPresetKey() == MOUSE_LEFT_CLICK || mParams.getPresetKey() == MOUSE_RIGHT_CLICK) {
            //松开按键,设置位置到中央
            mPointReporter.buttonReleased(mParams.getPresetKey().getKeys()[0]);
            mPointReporter.pointerMove(mViewOfXServer.getWidth() / 2f, mViewOfXServer.getHeight() / 2f);
        } else {
            //松开按键
            for (int i : lastMovingDirections) {
                mViewOfXServer.getXServerFacade().injectKeyRelease(mParams.getPresetKey().equals(CUSTOM)
                        ? (byte) (mParams.getKey4Directions()[i] + 8)
                        : (byte) (mParams.getPresetKey().getKeys()[i] + 8));
            }
            lastMovingDirections.clear();
        }

    }


    /**
     * 根据偏移量，注入对应方向的按键
     *
     * @param dx 手指当前位置距摇杆中心点位置 x
     * @param dy 手指当前位置距摇杆中心点位置 y
     */
    private void injectMultiKeysByDirections(float dx, float dy) {
        /*
         * 判断移动方向，注入对应按键
         * 先判断是否在0-3/8π范围内，确定上下，然后再判断是否在1/8π-1/2π范围内，叠加左右
         * 注意dy是向下的大小，如果大于0说明是手指向下移动。。
         */

        if (dx == 0 && dy == 0)
            return;

        if (mParams.getPresetKey() == MOUSE_MOVE || mParams.getPresetKey() == MOUSE_LEFT_CLICK || mParams.getPresetKey() == MOUSE_RIGHT_CLICK) {
            //计算光标坐标。计算方法：按下点为圆心，半径为一个设定值（比如100），然后求出倍率 100 / 根号dx2+dy2， dx和dy乘以倍率就是光标相对圆心偏移的距离
            //然后怎么办，是移动到位置就不管了，还是只要没松手就每30/50毫秒传送一次坐标？
            mMousePosInject.moveTo(dx,dy);
        } else {
            //先判断是否在0-3/8π范围内，确定上下，然后再判断是否在1/8π-1/2π范围内，叠加左右
            float tanCurrent = Math.abs(dx / dy);

            List<Integer> thisMovingDirections = new ArrayList<>();

            //不允许斜向
            if (mParams.isFourDirections()) {
                if (tanCurrent <= 1 && dy < 0) {
                    thisMovingDirections.add(0);
                } else if (tanCurrent <= 1) {
                    thisMovingDirections.add(1);
                } else if (tanCurrent > 1 && dx < 0) {
                    thisMovingDirections.add(2);
                } else if (tanCurrent > 1) {
                    thisMovingDirections.add(3);
                }
            }
            //允许斜向
            else {
                //注意dy是向下的大小，如果大于0说明是手指向下移动。。。然后上下和左右之间不要用else，否则没法斜向了
                if (tanCurrent < cotPiDiv8 && dy < 0) {
                    thisMovingDirections.add(0);//上
                } else if (tanCurrent < cotPiDiv8 && dy > 0) {
                    thisMovingDirections.add(1);//下
                }
                if (tanCurrent > tanPiDiv8 && dx < 0) {
                    thisMovingDirections.add(2);//左
                } else if (tanCurrent > tanPiDiv8 && dx > 0) {
                    thisMovingDirections.add(3);//右
                }
            }
            if (thisMovingDirections.size() == 0) {
                Log.d(TAG, "onTouchMove: 应该没有漏掉的方向了吧? tanCurrent=" + tanCurrent + ", dx=" + dx + ", dy=" + dy);
//                assert !isMoved();
            }
            //如果原来有 现在没有，松开
            for (int i : lastMovingDirections)
                if (!thisMovingDirections.contains(i) && mViewOfXServer != null) {
                    mViewOfXServer.getXServerFacade().injectKeyRelease((byte) (mParams.getKey4Directions()[i] + 8));
                }

            //如果原来没有 现在有，按下
            for (int i : thisMovingDirections) {
                if (!lastMovingDirections.contains(i) && mViewOfXServer != null) {
                    mViewOfXServer.getXServerFacade().injectKeyPress((byte) (mParams.getKey4Directions()[i] + 8));
                }
            }
//            if(pressingKeys.length!=0 || releasingKeys.length!=0){
//                Log.d(TAG, "injectMove: 方向：上次"+lastMovingDirections+", 本次"+thisMovingDirections+"。按键：松开"+ Arrays.toString(releasingKeys) +", 按下"+Arrays.toString(pressingKeys));
//            }
            //更新当前方向
            lastMovingDirections.clear();
            lastMovingDirections.addAll(thisMovingDirections);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "handleEditingClick: ");
        Context c = getContext();
        int padding8dp = QH.px(c, 8);
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.VERTICAL);
        linearRoot.setPadding(padding8dp, padding8dp, padding8dp, padding8dp);

        //禁止触发斜向按键
        CheckBox check4Direct = new CheckBox(c);
        check4Direct.setText(getS(RR.cmCtrl_JoyEdit4Ways));
        check4Direct.setChecked(mParams.isFourDirections());
        setDialogTooltip(check4Direct, getS(RR.cmCtrl_JoyEdit4WaysTip));
        check4Direct.setOnCheckedChangeListener((buttonView, isChecked) -> mParams.setFourDirections(isChecked));
        linearRoot.addView(check4Direct);

        //编辑位置状态下点击，设置四个方向的按键
        LinearLayout linearCustomKeys = new LinearLayout(c); //用于控制自定义按键显隐的布局
        linearCustomKeys.setOrientation(LinearLayout.VERTICAL);
        int btnSize = QH.px(c, 50);
        LinearLayout linearLine1 = new LinearLayout(c);
        linearLine1.addView(get1SetKeyBtn(false, -1), new ViewGroup.LayoutParams(btnSize, btnSize));
        linearLine1.addView(get1SetKeyBtn(true, 0), new ViewGroup.LayoutParams(btnSize, btnSize));
        linearCustomKeys.addView(linearLine1, new ViewGroup.LayoutParams(-2, -2));
        LinearLayout linearLine2 = new LinearLayout(c);
        linearLine2.addView(get1SetKeyBtn(true, 2), new ViewGroup.LayoutParams(btnSize, btnSize));
        linearLine2.addView(get1SetKeyBtn(false, -1), new ViewGroup.LayoutParams(btnSize, btnSize));
        linearLine2.addView(get1SetKeyBtn(true, 3), new ViewGroup.LayoutParams(btnSize, btnSize));
        linearCustomKeys.addView(linearLine2, new ViewGroup.LayoutParams(-2, -2));
        LinearLayout linearLine3 = new LinearLayout(c);
        linearLine3.addView(get1SetKeyBtn(false, -1), new ViewGroup.LayoutParams(btnSize, btnSize));
        linearLine3.addView(get1SetKeyBtn(true, 1), new ViewGroup.LayoutParams(btnSize, btnSize));
        linearCustomKeys.addView(linearLine3, new ViewGroup.LayoutParams(-2, -2));

        //选择预设按键，或者自定义
        Spinner spinKeys = new Spinner(c,Spinner.MODE_DIALOG);
        final String[] spinOptions = new String[]{WASD.getName(), ARROWS.getName(), MOUSE_MOVE.getName(),CUSTOM.getName()};
        final JoyParams.PresetKey[] spinValues = new JoyParams.PresetKey[]{WASD, ARROWS,MOUSE_MOVE, CUSTOM};
        ArrayAdapter<String> spinKeyPosAdapter = new SpinArrayAdapterSmSize(c, android.R.layout.simple_spinner_item, spinOptions);
        spinKeyPosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinKeys.setAdapter(spinKeyPosAdapter);
        spinKeys.setOnItemSelectedListener(new SimpleItemSelectedListener((parent, view, position, id) -> {
            mParams.setPresetKey(spinValues[position]);
            linearCustomKeys.setVisibility(mParams.getPresetKey().equals(CUSTOM) ? VISIBLE : GONE);
        }));
        //设置当前选中
        for (int i = 0; i < spinValues.length; i++) {
            if (mParams.getPresetKey().equals(spinValues[i]))
                spinKeys.setSelection(i);
        }
        spinKeys.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        LinearLayout oneLineSpinKeyPos = QH.getOneLineWithTitle(c, getS(RR.cmCtrl_JoyEditKeys), spinKeys, false);
//        setDialogTooltip(oneLineSpinKeyPos.getChildAt(0),"");
        linearRoot.addView(oneLineSpinKeyPos);

        linearRoot.addView(linearCustomKeys);

        Log.d(TAG, "handleEditingClick: 显示对话框");
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, null)
                .setView(linearRoot)
                .create().show();
    }

    private Button get1SetKeyBtn(boolean visible, int direction) {
        Button btn = new Button(getContext());
        btn.setText("…");
        btn.setVisibility(visible ? VISIBLE : INVISIBLE);
        btn.setTag(direction);
        if (direction != -1) {
            //选择该方向的按键，选择后设置到自身
            btn.setOnClickListener(v -> {
                boolean[] preSel = new boolean[AvailableKeysView.codes.length];
                for (int i = 0; i < preSel.length; i++) {
                    if (AvailableKeysView.codes[i] == mParams.getKey4Directions()[direction])
                        preSel[i] = true;
                }
                AvailableKeysView availableKeysView = new AvailableKeysView(getContext(), preSel, -1);
                availableKeysView.selectOnlyOne();
                availableKeysView.showWithinDialog((dialog, which) -> {
                    Button checkedBtn = availableKeysView.getLastCheckedButton();
                    if (checkedBtn != null) {
                        mParams.getKey4Directions()[direction] = AvailableKeysView.codes[(int) checkedBtn.getTag()];
                        Log.d(TAG, String.format("onClick: 设置mParams.key4Directions[%d]=%s", direction, AvailableKeysView.names[(int) checkedBtn.getTag()]));
                    }
                });
            });
        }
        return btn;
    }

    /**
     * 初始化时设置样式
     */
    private void setupStyle() {
        //标准按钮背景颜色（带透明度）
        int bgColorARGB = (getPreference().getInt(PREF_KEY_BTN_BG_COLOR, Color.BLACK) & 0x00ffffff)
                | (getPreference().getInt(PREF_KEY_BTN_ALPHA, 255) << 24);
        //按钮直径
        btnDiam = (int) (innerDivBtnWidthRatio*Math.max(getPreference().getInt(PREF_KEY_BTN_WIDTH, -2), getPreference().getInt(PREF_KEY_BTN_HEIGHT, -2)));
        if (btnDiam <= 0)
            btnDiam = (int) (innerDivBtnWidthRatio*QH.px(getContext(), (float) (50 * Math.sqrt(2))));
        outerDiam = (int) (btnDiam * outerDivInnerWidthRatio);

//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setShape(OVAL);
//        drawable.setColor(bgColorARGB);
//        //设置按钮边框，降低明度到0.5f
//        float[] tmpHSV = new float[3];
//        Color.colorToHSV(bgColorARGB, tmpHSV);
//        tmpHSV[2] -= 0.2f;
//        if (tmpHSV[2] < 0) tmpHSV[2] += 1.0f;
//        //边框稍微透明一点吧
//        drawable.setStroke(QH.px(getContext(), btnDiam / 30f), Color.HSVToColor((bgColorARGB & 0xff000000) >> 24, tmpHSV));//描边
//        setBackground(drawable);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(outerDiam, outerDiam);
        if (mParams != null)
            params.setMargins(mParams.getMarginLeft(), mParams.getMarginTop(), 0, 0);
        setLayoutParams(params);
        //设置中心位置
        if (mParams != null) {
            mCenterXYPoint.x = mParams.getMarginLeft() + outerDiam / 2f;
            mCenterXYPoint.y = mParams.getMarginTop() + outerDiam / 2f;
        } else {
            mCenterXYPoint.x = outerDiam / 2f;
            mCenterXYPoint.y = outerDiam / 2f;
        }
        mLastTouchXYPoint.x = mCenterXYPoint.x;
        mLastTouchXYPoint.y = mCenterXYPoint.y;

        mOriginXYPoint.x = mCenterXYPoint.x;
        mOriginXYPoint.y = mCenterXYPoint.y;
        //要不外层先不要了
//        FrameLayout joyFrame = new FrameLayout(getContext());
//        GradientDrawable joyFrameDrawable = new GradientDrawable();
//        joyFrameDrawable.setShape(OVAL);
//        //设置底板颜色，提高明度？好像不太行，还是降低吧
//        tmpHSV[2]+=0.4f;
//        if(tmpHSV[2]>1) tmpHSV[2]-=1.0f;
//        joyFrameDrawable.setColor(Color.HSVToColor((bgColorARGB&0xff000000)>>24,tmpHSV));
//        joyFrame.setBackground(joyFrameDrawable);

//        joyFrame.addView(btn);
//        int frameDiam = btnDiam*5/4;
//        FrameLayout.LayoutParams joFramParams = new FrameLayout.LayoutParams(frameDiam,frameDiam);
//        joFramParams.gravity=Gravity.CENTER ;
//        joyFrame.setLayoutParams(joFramParams);
//        binding.fakeTscMainframe.addView(joyFrame);

        //设置自定义画笔
        float[] btnHSV = new float[3];
        Color.colorToHSV(bgColorARGB, btnHSV);
        btnHSV[2] -= 0.2f;
        if (btnHSV[2] < 0) btnHSV[2] += 1.0f;

        mBtnPaint.setStyle(STROKE);
        mBtnPaint.setStrokeWidth(QH.px(getContext(), btnDiam / 30f));
        mBtnPaint.setColor(Color.HSVToColor((bgColorARGB & 0xff000000) >> 24, btnHSV));

//        mOutLinePaint.setStrokeWidth(QH.px(getContext(), btnDiam / 40f));
        mOutLinePaint.setStyle(FILL);
        mOutLinePaint.setColor(bgColorARGB);

        setBackground(null);

    }


    /**
     * 手动绘制样式吧，不然外部轮廓不好搞
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        //x和y是以自己为基准的
        float outerRadius = outerDiam / 2f;
        canvas.drawCircle(outerRadius, outerRadius, outerRadius - outerDiam / 35f, mOutLinePaint);
        //移动时改变按钮样式的位置
        float dx = mLastTouchXYPoint.x - mCenterXYPoint.x;
        float dy = mLastTouchXYPoint.y - mCenterXYPoint.y;

        float temptDistance = (float) Math.sqrt(dx * dx + dy * dy);
        float maxDistance = (outerDiam-btnDiam)/2f;
        if (temptDistance > maxDistance) {
            float ratio = maxDistance/temptDistance;
            dx = dx * ratio;
            dy = dy * ratio;
        }
//            setTranslationX(dx);
//            setTranslationY(dy);
        canvas.drawCircle(outerRadius + dx, outerRadius + dy, btnDiam / 2f - btnDiam / 25f, mBtnPaint);
    }


}
