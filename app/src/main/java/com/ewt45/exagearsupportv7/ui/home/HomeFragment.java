package com.ewt45.exagearsupportv7.ui.home;

import static android.content.ContentValues.TAG;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;

import static com.eltechs.axs.Globals.getApplicationState;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProvider;

import com.eltechs.axs.ExagearImageConfiguration.ExagearImage;
import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.FatalErrorActivity;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.configuration.XServerViewConfiguration;
import com.eltechs.axs.configuration.startup.EnvironmentCustomisationParameters;
import com.eltechs.axs.configuration.startup.StartupAction;
import com.eltechs.axs.configuration.startup.actions.CreateTypicalEnvironmentConfiguration;
import com.eltechs.axs.configuration.startup.actions.StartEnvironmentService;
import com.eltechs.axs.environmentService.AXSEnvironment;
import com.eltechs.axs.environmentService.TrayConfiguration;
import com.eltechs.axs.environmentService.components.XServerComponent;
import com.eltechs.axs.helpers.UiThread;
import com.eltechs.axs.network.SocketPaths;
import com.eltechs.axs.xconnectors.epoll.UnixSocketConfiguration;
import com.eltechs.ed.activities.EDStartupActivity;
import com.eltechs.ed.startupActions.WDesktop;
import com.ewt45.exagearsupportv7.MainActivity;
import com.ewt45.exagearsupportv7.R;
import com.ewt45.exagearsupportv7.databinding.FragmentHomeBinding;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.ToggleButtonHighContrast;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controls.SensitivitySeekBar;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.BtnContainer;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.SpecialPopupMenu;
import com.example.datainsert.exagear.input.SoftInput;
import com.example.datainsert.exagear.obb.ProcessInstallObb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //添加监听输入的区域
//        Log.d(TAG, "onCreate: 能找到button吗"+binding.getRoot());;
//        binding.touchOuter.addView(new TouchScreenControlsInputWidget(requireContext()), 0,
//                new ViewGroup.LayoutParams(-1, 50));


        new ToggleButtonHighContrast(requireContext());


        //点击按钮显示键盘
        binding.toggleInput.setOnClickListener(v -> {
//            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//            Log.d(TAG, "onClick: 让touchcontrol变为focus以接收软键盘输入。是否成功：" + binding.touchOuter.getChildAt(0).requestFocus());
////            binding.editText.requestFocus();
//            Log.d(TAG, "onClick: 当前顶层布局为" + requireActivity().getWindow().getDecorView());

        });

//        binding.testBtn.setBackgroundTintMode(PorterDuff.Mode.SRC);
//        binding.testBtn.setBackgroundColor(Color.BLACK);
        binding.testBtn.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
//        binding.testBtn.setBackgroundColor(Color.WHITE);
        binding.testBtn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

        Log.d(TAG, "onViewCreated: 按钮类=" + binding.testBtn.getClass()
                + "\ngetBackground()=" + binding.testBtn.getBackground()
                + "\ngetBackgroundTintList()=" + binding.testBtn.getBackgroundTintList());
        if (binding.testBtn.getBackground() instanceof RippleDrawable) {
            RippleDrawable drawable = (RippleDrawable) binding.testBtn.getBackground();
            drawable.setColor(ColorStateList.valueOf(Color.BLACK));
        }


        binding.testBtn.setOnClickListener(v -> {
//            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            //试试二级菜单
//            SubMenu subMenu =popupMenu.getMenu().addSubMenu("submenu");
//            subMenu.add("submenu item1");
//            popupMenu.show();

//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("*/*");//仅显示obb类型
//            startActivityForResult(intent, 123);

            //解压obb
            ProcessInstallObb.startest(this);


        });

        binding.startXserveractivityBtn.setOnClickListener(v->{

            //CreateTypicalEnvironmentConfiguration,本来应该在startGuest里写的，没调用，只好在这里写了

//        arrayList.add(new CreateTypicalEnvironmentConfiguration(12, false));
//
            EnvironmentAware environmentAware = (EnvironmentAware) getApplicationState();
            EnvironmentCustomisationParameters environmentCustomisationParameters = new EnvironmentCustomisationParameters();
//                ((SelectedExecutableFileAware) environmentAware).getSelectedExecutableFile().getEnvironmentCustomisationParameters();
            AXSEnvironment aXSEnvironment = new AXSEnvironment(requireContext());
//        aXSEnvironment.addComponent(new SysVIPCEmulatorComponent(ProductIDs.getPackageName(this.productId)));
            aXSEnvironment.addComponent(new XServerComponent(environmentCustomisationParameters.getScreenInfo(), 12,
                    UnixSocketConfiguration.createRegularSocket(requireContext().getFilesDir().getAbsolutePath(), String.format("%s%d", SocketPaths.XSERVER, Integer.valueOf(12)))));
////        aXSEnvironment.addComponent(new ALSAServerComponent(createALSASocketConf()));
////        aXSEnvironment.addComponent(new DirectSoundServerComponent(createDSoundServerSocketConf()));
////        aXSEnvironment.addComponent(new GuestApplicationsTrackerComponent(createGATServerSocketConf()));
            ExagearImage exagearImage = ((ExagearImageAware) environmentAware).getExagearImage();
////        aXSEnvironment.addComponent(new TempDirMaintenanceComponent(exagearImage));
////        aXSEnvironment.addComponent(new EtcHostsFileUpdaterComponent(exagearImage));
            environmentAware.setEnvironment(aXSEnvironment);
            environmentAware.setXServerViewConfiguration(XServerViewConfiguration.DEFAULT);


            ArrayList arrayList = new ArrayList();
            arrayList.add(new StartEnvironmentService(new TrayConfiguration(R.drawable.tray, R.string.ed_host_app_name, R.string.ed_host_app_name)));
            // from class: com.eltechs.ed.startupActions.StartGuest.2
// java.lang.Runnable
            UiThread.post(() -> ((ApplicationStateBase) getApplicationState()).getStartupActionsCollection().addActions(arrayList));

            ((MainActivity)requireActivity()).signalUserInteractionFinished(0,WDesktop.UserRequestedAction.GO_FURTHER);

//            Intent intent = new Intent(requireContext(), XServerDisplayActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            Class<?> cls = null;
//            intent.putExtra("facadeclass", (Serializable) null);
//            startActivity(intent);
        });

        //

        SensitivitySeekBar.create(binding.getRoot());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:fragment requestCode:" + requestCode + ", resultCode" + resultCode);
    }





    /**
     * * 获取Selector
     * * @param normalDraw
     * * @param pressedDraw
     * * @return
     * */
    public static StateListDrawable getSelector(Drawable normalDraw, Drawable pressedDraw) {
        StateListDrawable stateListDrawable  = new StateListDrawable();
        stateListDrawable.addState(new int[]{ android.R.attr.state_pressed }, pressedDraw);
        stateListDrawable.addState(new int[]{ }, normalDraw);
        return stateListDrawable ;
    }
    /**
     * * 设置shape(设置单独圆角)     *
     * @param topLeftCA     *
     * @param topRigthCA     *
     * @param buttomLeftCA     *
     * @param buttomRightCA     *
     * @param bgColor     *
     * @param storkeWidth     *
     * @param strokeColor     *
     * @return
     * */
    public GradientDrawable getDrawable(
            float topLeftCA, float topRigthCA, float buttomLeftCA,
            float buttomRightCA, int bgColor, int storkeWidth, int strokeColor) {
        //把边框值设置成dp对应的px
        storkeWidth = QH.px(requireContext(), storkeWidth);
        float[] circleAngleArr = {topLeftCA, topLeftCA, topRigthCA, topRigthCA,
                buttomLeftCA, buttomLeftCA, buttomRightCA, buttomRightCA};
        //把圆角设置成dp对应的px
        for (int i = 0; i < circleAngleArr.length; i++){
            circleAngleArr[i] = QH.px(requireContext(), circleAngleArr[i]);
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadii(circleAngleArr);
        //圆角
        gradientDrawable.setColor(bgColor);
        //背景色
        gradientDrawable.setStroke(storkeWidth, strokeColor);
        //边框宽度，边框颜色
        return gradientDrawable;
    }
    /**
     * * 设置shape(圆角)     *     *
     * @param bgCircleAngle     *
     * @param bgColor     *
     * @param width     *
     * @param strokeColor     *
     * @return
     * */
    public GradientDrawable getDrawable(int bgCircleAngle, int bgColor, int width, int strokeColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(bgCircleAngle);
        gradientDrawable.setColor(bgColor);
        gradientDrawable.setStroke(width, strokeColor);
        return gradientDrawable;
    }
}