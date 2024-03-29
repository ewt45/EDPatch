package com.example.datainsert.exagear.mutiWine.v2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.datainsert.exagear.RR;

public class WineStoreFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return new WineStoreView(inflater.getContext());
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(RR.getS(RR.mw_fragTitle));
    }

    @Override
    public void onResume() {
        super.onResume();
        //设置左上角为返回箭头
        ((AppCompatActivity)requireActivity()).getSupportActionBar().setHomeAsUpIndicator(0);

    }
}
