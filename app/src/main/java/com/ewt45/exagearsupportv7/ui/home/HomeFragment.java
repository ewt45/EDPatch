package com.ewt45.exagearsupportv7.ui.home;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProvider;
import android.widget.Toast;

import com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsInputWidget;
import com.ewt45.exagearsupportv7.R;
import com.ewt45.exagearsupportv7.databinding.FragmentHomeBinding;
import com.example.datainsert.exagear.RSIDHelper;
import com.example.datainsert.exagear.controls.SensitivitySeekBar;
import com.example.datainsert.exagear.input.SoftInput;
import com.example.datainsert.exagear.obb.ProcessInstallObb;
import com.example.datainsert.exagear.obb.SelectObbFragment;
import com.example.test;

import java.io.File;
import java.nio.file.Files;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //添加监听输入的区域
//        Log.d(TAG, "onCreate: 能找到button吗"+binding.getRoot());;
        binding.touchOuter.addView(new TouchScreenControlsInputWidget(requireContext()),0,
                new ViewGroup.LayoutParams(-1,50));

        //点击按钮显示键盘
        binding.toggleInput.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager)requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            Log.d(TAG, "onClick: 让touchcontrol变为focus以接收软键盘输入。是否成功："+ binding.touchOuter.getChildAt(0).requestFocus());
//            binding.editText.requestFocus();
            Log.d(TAG, "onClick: 当前顶层布局为"+requireActivity().getWindow().getDecorView());
        });

//        binding.testBtn.setBackgroundTintMode(PorterDuff.Mode.SRC);
//        binding.testBtn.setBackgroundColor(Color.BLACK);
        binding.testBtn.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
//        binding.testBtn.setBackgroundColor(Color.WHITE);
        binding.testBtn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

        Log.d(TAG, "onViewCreated: 按钮类="+binding.testBtn.getClass()
                +"\ngetBackground()="+binding.testBtn.getBackground()
                +"\ngetBackgroundTintList()="+binding.testBtn.getBackgroundTintList());
        if(binding.testBtn.getBackground() instanceof RippleDrawable){
            RippleDrawable drawable = (RippleDrawable) binding.testBtn.getBackground();
            drawable.setColor(ColorStateList.valueOf(Color.BLACK));
        }

        binding.testBtn.setOnClickListener(v->{
//            PopupMenu popupMenu = new PopupMenu(requireContext(),v);
//            popupMenu.getMenu().add("显示/隐藏键盘").setOnMenuItemClickListener(item -> {
//                SoftInput.toggleTest((AppCompatActivity) requireActivity());
//                return true;
//            });
//            popupMenu.show();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");//仅显示obb类型
            startActivityForResult(intent, 123);

        });
        SensitivitySeekBar.create(binding.getRoot());

        Log.d(TAG, "onViewCreated: 新建一个小的fragment");
//        Fragment fragment = new SelectObbFragment();
//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .add(RSIDHelper.rslvID(R.id.startupAdButtons,0x7f0900f2),fragment).addToBackStack(null).commit();
//        requireActivity().getSupportFragmentManager().beginTransaction().show(fragment).addToBackStack(null).commit();
//        ViewGroup linear = requireActivity().findViewById(RSIDHelper.rslvID(R.id.startupAdButtons,0x7f0900f2));
//        linear.setVisibility(View.VISIBLE);
        //移除原先的子布局


        //防止多次添加
        ProcessInstallObb.startest(this);




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:fragment requestCode:"+requestCode+", resultCode"+resultCode);
    }
}