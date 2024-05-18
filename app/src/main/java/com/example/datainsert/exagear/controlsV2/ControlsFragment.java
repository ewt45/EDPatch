package com.example.datainsert.exagear.controlsV2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.eltechs.ed.activities.EDMainActivity;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.model.OneProfile;

public class ControlsFragment extends Fragment {
    public static final String ARGV_START_EDIT_ON_SHOW = "ARGV_START_EDIT_ON_SHOW";
    private static final String TAG = "ControlsFragment";
    private static final int REQUEST_IMPORT_PROFILE = 124;
    private static final int REQUEST_EXPORT_PROFILE = 125;
    private SparseArray<IntentResultCallback> intentCallbackList = new SparseArray<>();
    Handler uiHandler = new Handler(Looper.getMainLooper());
    private TouchAreaView touchAreaView;

    //完蛋，fragment必须要用无参构造
    public ControlsFragment() {

    }

    /**
     * 在选择本地配置返回时，fragment会被重建，此时需要正确处理
     * @param ref 旧fragment，可能为null
     */
    public static Fragment newInstance(Fragment ref) {
        ControlsFragment fragment = new ControlsFragment();
        if (ref instanceof ControlsFragment) {
            ControlsFragment ref1 = (ControlsFragment) ref;
            fragment.touchAreaView = ref1.touchAreaView;
            fragment.intentCallbackList = ref1.intentCallbackList;
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        //为什么全屏改小窗时，走到这里但是holder还是null？
        //当全屏切小窗时，会走到activity的onStart此时fragment的onCreateView被重新调用，
        // 但由于InterfaceOverlay.attach是在onResume才被创建，所以此时还没调用Const.init()初始化
        return new FrameLayout(requireContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, this+"onResume: ");

        Context c = requireContext();

        //在resume中新建TouchAreaView，此时保证holder不为null
        assert Const.isInitiated();
        if(touchAreaView==null){
            touchAreaView = new TouchAreaView(c);
            Const.setTouchView(touchAreaView);

            OneProfile profile = ModelProvider.readCurrentProfile();
            touchAreaView.setProfile(profile);

            if (getArguments() != null && getArguments().getBoolean(ARGV_START_EDIT_ON_SHOW))
                touchAreaView.startEdit();

            if (requireActivity() instanceof EDMainActivity){
//                touchAreaView.setBackgroundResource(R.drawable.someimg);
                touchAreaView.setBackgroundColor(0xff8D6F64);
            }

        }

        //将touchView放入容器视图中。
        assert touchAreaView!=null;
        if(touchAreaView.getParent()!=null)
            ((ViewGroup)touchAreaView.getParent()).removeView(touchAreaView);
        ViewGroup rootView = getRootView();
        rootView.removeAllViews();
        rootView.addView(touchAreaView);

        if (Const.getTouchView() != touchAreaView)
            Const.setTouchView(touchAreaView);
        //必须要在resume这里调用一下requestFocus，否则返回键拦截不到会直接退出Fragment
        touchAreaView.requireFocus();
        touchAreaView.invalidate();
    }

    private ViewGroup getRootView(){
        return (ViewGroup) getView();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, this+"onAttach: ");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, this+"onDetach: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, this+"onPause: ");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, this+"onDestroy: ");
        //TODO 意外退出时，也应该保存配置
        super.onDestroy();
        touchAreaView=null;
        intentCallbackList=null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResultCallback callback = intentCallbackList.get(requestCode);

        if (data == null || data.getData() == null || callback == null)
            return;


        if (requestCode == REQUEST_IMPORT_PROFILE) {
            handleRequestAfterContextHasBeenSet(() -> callback.onReceive(requestCode, resultCode, data));
        } else if (requestCode == REQUEST_EXPORT_PROFILE) {
            handleRequestAfterContextHasBeenSet(() -> callback.onReceive(requestCode, resultCode, data));
        }
    }

    /**
     * 选择完文件后回到应用，此时Const已经clear但还没init，context用不了，所以需要等一等
     */
    private void handleRequestAfterContextHasBeenSet(Runnable runnable) {
        if (Const.isInitiated())
            runnable.run();
        else
            uiHandler.postDelayed(() -> handleRequestAfterContextHasBeenSet(runnable), 300);
    }

    /**
     * 从本地文件导入配置
     */
    public void requestImportProfile(IntentResultCallback callback) {
        intentCallbackList.put(REQUEST_IMPORT_PROFILE, callback);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_IMPORT_PROFILE);
    }

    /**
     * 将某个配置保存到本地，调用此函数前应确保该model数据是最新的，比如 如果导出的是当前编辑的model，那么应该先保存一下
     */
    public void requestExportProfile(@NonNull String profileName, IntentResultCallback callback) {
        intentCallbackList.put(REQUEST_EXPORT_PROFILE, callback);

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, profileName);
        startActivityForResult(intent, REQUEST_EXPORT_PROFILE);
    }

    /**
     * 用于外部注册 发送intent后 接收到结果时的回调
     */
    public interface IntentResultCallback {
        public void onReceive(int requestCode, int resultCode, Intent data);
    }
}
