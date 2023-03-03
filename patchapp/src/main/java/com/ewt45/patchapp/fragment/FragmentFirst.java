package com.ewt45.patchapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import androidx.navigation.fragment.NavHostFragment;

import com.ewt45.patchapp.R;
import com.ewt45.patchapp.databinding.FragmentFirstBinding;

public class FragmentFirst extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(view1 -> NavHostFragment.findNavController(FragmentFirst.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));
        binding.btnTest.setOnClickListener(v->{
            NavHostFragment.findNavController(FragmentFirst.this)
                    .navigate(R.id.action_FirstFragment_to_fragmentChoosePatch);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}