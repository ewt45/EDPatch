package com.example.datainsert.exagear.rightdrawer;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.resources.MaterialResources;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ewt45.exagearsupportv7.R;
import com.example.datainsert.exagear.RSIDHelper;

public class RightDrawerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        assert bundle != null;
        TextView tv = new TextView(requireContext());
        tv.setText("临时tv");
        return inflater.inflate(RSIDHelper.rslvID(R.layout.fragment_rightdrawer,0x7f0b0100),container,false);
//                super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewGroup parent = (ViewGroup) view;
        int index = getArguments().getInt("index");
        for(int i=0; i<parent.getChildCount(); i++){
            parent.getChildAt(i).setVisibility(i==index?View.VISIBLE:View.GONE);
        }
//        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setTitle(getArguments().getString("title"));
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(0);

    }
}
