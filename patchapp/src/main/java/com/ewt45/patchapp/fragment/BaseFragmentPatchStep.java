package com.ewt45.patchapp.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ewt45.patchapp.R;
import com.ewt45.patchapp.widget.FAB;

public abstract class BaseFragmentPatchStep extends Fragment {

    protected FAB getFAB(){
        return requireActivity().findViewById(R.id.fab);
    }
    protected void setStepTitle(String str){
        ((TextView)requireActivity().findViewById(R.id.step_title)).setText(str);
    }
    protected void setStepTitle(int resId){
        ((TextView)requireActivity().findViewById(R.id.step_title)).setText(resId);
    }

    @Override
    public void onStart() {
        super.onStart();

//        //切换小窗的时候会重新进入，这里判断一下日志是否显示吧
//        if(!MyApplication.data.isShowingLog)
//            return;
//        CoordinatorLayout coordinatorLayout = requireActivity().findViewById(R.id.root_coordinator);
//        AppBarLayout appBarLayout = requireActivity().findViewById(R.id.app_bar);
//        ViewGroup.LayoutParams params = appBarLayout.getLayoutParams();
//        if(!(params instanceof CoordinatorLayout.LayoutParams))
//            return;
//        CoordinatorLayout.Behavior behavior= ((CoordinatorLayout.LayoutParams) params).getBehavior();
//        if(!(behavior instanceof AppbarSwipeBehavior2))
//            return;
//        ((AppbarSwipeBehavior2)behavior).switchLogViewFullScreen(coordinatorLayout,appBarLayout);
    }

    protected NavController getNavController(){
        return   Navigation.findNavController(requireActivity(),R.id.patch_step_nav_host);
    }
    protected Toolbar getToolbar(){
        return requireActivity().findViewById(R.id.toolbar);
    }
}
