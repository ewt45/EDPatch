package com.ewt45.patchapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ewt45.patchapp.ActivityPatch;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.databinding.FragmentPatchMainBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentPatchMain extends Fragment {
    private static final String TAG = "FragmentPatchMain";
    FragmentPatchMainBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPatchMainBinding.inflate(inflater,container,false);

//        FragmentTabHost tabHost = rootView.findViewById(android.R.id.tabhost);
//        //setup, fragmentmanager传自己的，然后挂载到对应的framelayout上 （不行，fragmenttabhost没法持久保存fragment）
//        tabHost.setup(requireActivity(), getChildFragmentManager(), android.R.id.tabcontent);
//
//        //添加tab
//        tabHost.addTab(tabHost.newTabSpec("step1").setIndicator("Step1"),FragmentPatchStep1.class,null);
//        tabHost.addTab(tabHost.newTabSpec("step2").setIndicator("Step2"),FragmentPatchStep2.class,null);
//        tabHost.addTab(tabHost.newTabSpec("step3").setIndicator("Step3"),FragmentPatchStep3.class,null);

//        TabLayout tabLayout = requireActivity().findViewById(R.id.tab_layout);
//        ViewPager viewPager = rootView.findViewById(R.id.pager);
//        PatchMainFragmentAdapter pagerAdapter = new PatchMainFragmentAdapter(getChildFragmentManager());
//        viewPager.setAdapter(pagerAdapter);
//        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
//            @Override
//            public void onPageSelected(int position) {
//                Log.d(TAG, "onPageSelected: "+position);
////                pagerAdapter.notifySelected(position);
//                setTitleAndIconWithPage(position);
//            }
//        });
//
//        tabLayout.setupWithViewPager(viewPager);

        requireActivity().findViewById(R.id.fab).setOnClickListener(v->{});

        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        ((ActivityPatch)requireActivity()).changePatchStepTitleAndFABVisibility(false);

        ((FloatingActionButton)requireActivity().findViewById(R.id.fab)).setImageResource(R.drawable.ic_arrow_forward);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((ActivityPatch)requireActivity()).changePatchStepTitleAndFABVisibility(true);
    }

    private static class PatchMainFragmentAdapter extends FragmentPagerAdapter{
        List<BaseFragmentPatchStep> mFragmentLists = new ArrayList<>();
        public PatchMainFragmentAdapter(FragmentManager fm) {
            super(fm);
            mFragmentLists.add(new FragmentPatchStep1());
            mFragmentLists.add(new FragmentPatchStep2());
            mFragmentLists.add(new FragmentPatchStep3());
        }

//        public void notifySelected(int position){
//            mFragmentLists.get(position).onSelect();
//        }
        @Override
        public Fragment getItem(int i) {
            return mFragmentLists.get(i);
        }

        @Override
        public int getCount() {
            return mFragmentLists.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
//            return mFragmentLists.get(position).getTitle();
            return "Step"+(position+1)  ;
        }
    }
}
