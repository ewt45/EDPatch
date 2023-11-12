//package com.ewt45.patchapp.unused;
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.AppBarLayout;
//import android.support.design.widget.CoordinatorLayout;
//import android.support.transition.TransitionManager;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.navigation.Navigation;
//
//import com.ewt45.patchapp.R;
//import com.ewt45.patchapp.databinding.FragmentPatchLogBinding;
//import com.ewt45.patchapp.fragment.BaseFragmentPatchStep;
//import com.ewt45.patchapp.widget.FAB;
//
//public class FragmentPatchLog extends BaseFragmentPatchStep {
//    FragmentPatchLogBinding binding;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        binding = FragmentPatchLogBinding.inflate(inflater,container,false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        menu.clear();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
////        setStepTitle(R.string.tv_patchstep3);
//        //隐藏actionbar，fab取消anchor，fab设置gravity
//        FAB fab = getFAB();
//
////        AppBarLayout appBarLayout =requireActivity().findViewById(R.id.app_bar);
////        appBarLayout.setVisibility(View.GONE);
//
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
//        params.setAnchorId(-1);
//        params.gravity = Gravity.BOTTOM | Gravity.END;
//        fab.setLayoutParams(params);
//        fab.setImageResource(R.drawable.ic_close);
//
//        fab.setOnClickListener(v -> {
//            getNavController().navigateUp();
//        });
//    }
//
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        FAB fab = getFAB();
//
//
//        CoordinatorLayout.LayoutParams params2 = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
//        params2.setAnchorId(R.id.app_bar);
//        params2.gravity = Gravity.NO_GRAVITY;
//        fab.setLayoutParams(params2);
//    }
//
//    @Override
//    void onSelect() {
//
//    }
//
//    @Override
//    int getTitle() {
//        return 0;
//    }
//}
