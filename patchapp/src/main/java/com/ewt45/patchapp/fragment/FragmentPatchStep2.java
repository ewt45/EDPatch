package com.ewt45.patchapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ewt45.patchapp.AndroidUtils;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.databinding.FragmentPatchStep2Binding;
import com.ewt45.patchapp.model.FunctionInfo;
import com.ewt45.patchapp.thread.Action;
import com.ewt45.patchapp.thread.BuildApk;
import com.ewt45.patchapp.thread.DecodeApk;
import com.ewt45.patchapp.thread.Func;
import com.ewt45.patchapp.thread.SignApk;
import com.ewt45.patchapp.thread.SignalDone;
import com.ewt45.patchapp.widget.ActionProgressDialog;
import com.ewt45.patchapp.widget.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class FragmentPatchStep2 extends BaseFragmentPatchStep {
    private static final String TAG = "FragmentPatchStep2";
    private final FunctionInfo[] mFuncList = FunctionInfo.ALL_FUNCTIONS;
    FragmentPatchStep2Binding binding;
    private int type = 0;
    private static final String PREF_KEY_STT_ONELINE="statistic_one_line";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPatchStep2Binding.inflate(inflater, container, false);

//        binding.funcRecycler.setLayoutManager(getResources().getBoolean(R.bool.recyclerview_grid)
//                ? new GridLayoutManager(requireContext(), 2) : new LinearLayoutManager(requireContext()));
        binding.funcRecycler.setAdapter(new FuncAdapter(mFuncList));
        binding.funcRecycler.addItemDecoration(new DividerGridItemDecoration(requireContext()));
        binding.cardInfo.setOnClickListener(v-> {
            boolean currIsOneLine = AndroidUtils.getPrefs().getBoolean(PREF_KEY_STT_ONELINE,false);
            AndroidUtils.getPrefs().edit().putBoolean(PREF_KEY_STT_ONELINE,!currIsOneLine).commit();
            TransitionManager.beginDelayedTransition(binding.getRoot());
            updateCountInfo();
        });
        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: step2");
        setStepTitle(R.string.patchstep2_title);

        //刷新funcList的版本号统计
        FunctionInfo.refreshVersionsStatistic();
        updateCountInfo();

        getFAB().setOnClickListener(v -> {
            //执行操作，执行完如果没错误，跳转到step3，如果有错误，留在step2
            List<Action> torunList = new ArrayList<>();
            for (FunctionInfo info : mFuncList)
                if (info.newlyChecked == 1 && info.instVer != info.latestVer)
                    torunList.add(info.func);

            if (torunList.size() > 0) {
                torunList.add(0, new DecodeApk(DecodeApk.PATCHER));
                torunList.add(new BuildApk());
                boolean useDefaultKey = AndroidUtils.getPrefs().getBoolean("use_default_signature", true);
                torunList.add(new SignApk(requireContext().getAssets(), useDefaultKey));//签名
            }
            torunList.add(new SignalDone());
            Log.d(TAG, "setOnClickListener: 点击fab后，执行action：" + torunList);

            ActionProgressDialog.startActionsWithDialog(requireContext(), noError -> {
                AndroidUtils.showSnack(requireActivity(), noError ? R.string.tips_backup : R.string.tips_action_failed);

                if (noError) {
                    getNavController().navigate(R.id.action_patch_step2_to_step3);
                } else {
                    updateCountInfo();
                }
            }, torunList.toArray(new Action[0]));

        });
        //切换图标时，要先hide再show
        getFAB().hide();
        getFAB().setImageResource(R.drawable.ic_arrow_forward);
        getFAB().show();

    }


    /**
     * 勾选或取消勾选功能时，更新上方显示文字.
     * 是否是第一次（每个功能都获取一遍版本，还是只是改变了一个checkbox
     */
    void updateCountInfo() {
        //统计数据放到解包和回包apk之后吧，这里只计算勾选的
        int nonExist = 0, updatable = 0, latest = 0, newlyChecked = 0;
        for (FunctionInfo info : mFuncList) {
            if (info.newlyChecked == 1 && info.instVer != info.latestVer) newlyChecked++;
            if (info.instVer == Func.INVALID_VERSION) nonExist++;
            else if (info.instVer != info.latestVer) updatable++;
            else latest++;
        }

        String stt = getString(R.string.patchstep2_tv_statistic, (updatable + latest), mFuncList.length, newlyChecked, (nonExist + updatable));
        boolean isOneLine = AndroidUtils.getPrefs().getBoolean(PREF_KEY_STT_ONELINE,false);
        binding.tvInfo.setMaxLines(isOneLine?1:99);
        binding.tvInfo.setText(isOneLine?stt.replace("\n","   "):stt);
    }

    private static class FuncViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        CheckBox check;
        TextView version;

        public FuncViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            check = itemView.findViewById(R.id.check);
            version = itemView.findViewById(R.id.tv_version);
        }
    }

    private class FuncAdapter extends RecyclerView.Adapter<FuncViewHolder> {
        private static final String TAG = "FuncAdapter";
        FunctionInfo[] mInfoList;

        public FuncAdapter(FunctionInfo[] infoList) {
            mInfoList = infoList;
        }


        @NonNull
        @Override
        public FuncViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_func_select, viewGroup, false);
            return new FuncViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull FuncViewHolder holder, int position) {
            Context c = holder.name.getContext();
            FunctionInfo info = mInfoList[position];
            holder.name.setText(info.name);
            holder.icon.setImageDrawable(info.iconId == 0 ? null : c.getDrawable(info.iconId));

            int installedVersion = info.func.getInstalledVersion();
            Log.d(TAG, String.format("changeView: 功能%s, 已安装版本 %d，最新版本 %d", info.func.getClass().getSimpleName(), installedVersion, info.func.getLatestVersion()));
            // 如果已安装功能版本号和最新的相等则自动勾选，否则不勾选
            boolean added = installedVersion == info.func.getLatestVersion();
            holder.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                info.newlyChecked = isChecked ? 1 : 0;
                updateCountInfo();
            });
            holder.check.setChecked(added);

            holder.version.setText(c.getString(R.string.patchstep2_recy_item, info.instVer, info.latestVer));

            //如果已经是最新版本， 设置为禁用
            holder.check.setEnabled(!added);
//            holder.name.setEnabled(!added);
//            holder.icon.setEnabled(!added);

            //设置点击标题和图标显示介绍
            View.OnClickListener clickListener = v -> {
                BottomSheetDialog dialog = new BottomSheetDialog(v.getContext());
                View dialogRootView = LayoutInflater.from(v.getContext()).inflate(R.layout.item_func_help_descp, (ViewGroup) v.getParent(), false);
                ((TextView) dialogRootView.findViewById(R.id.title)).setText(info.name);
                ((TextView) dialogRootView.findViewById(R.id.description)).setText(info.description);
                ((ImageView) dialogRootView.findViewById(R.id.gif_image)).setImageResource(info.descpImgId);
                dialog.setContentView(dialogRootView);
                dialog.show();
            };
            holder.name.setOnClickListener(clickListener);
            holder.icon.setOnClickListener(clickListener);

        }


        @Override
        public int getItemCount() {
            return mInfoList.length;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }
    }
}
