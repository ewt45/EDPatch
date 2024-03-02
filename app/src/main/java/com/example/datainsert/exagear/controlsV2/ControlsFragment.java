package com.example.datainsert.exagear.controlsV2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.ed.R;
import com.eltechs.ed.activities.EDMainActivity;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.model.OneProfile;

public class ControlsFragment extends Fragment {
    public static final String ARGV_START_EDIT_ON_SHOW = "ARGV_START_EDIT_ON_SHOW";
    private static final int REQUEST_IMPORT_PROFILE = 124;
    private static final int REQUEST_EXPORT_PROFILE = 125;
    private SparseArray<IntentResultCallback> intentCallbackList = new SparseArray<>();


    //完蛋，fragment必须要用无参构造
    public ControlsFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        XServerDisplayActivityConfigurationAware aware = Globals.getApplicationState();
//        if (aware != null)
//            mUiOverlay = ((FalloutInterfaceOverlay2) aware.getXServerDisplayActivityInterfaceOverlay());
//
//        if (mUiOverlay != null) {
//            mKeyCodes2 = mUiOverlay.getControlsFactory().getKeyCodes2();
//            mKeyCodes3 = mUiOverlay.getControlsFactory().getKeyCodes3();
//        } else {
//            mKeyCodes2 = KeyCodes2.read(requireContext());
//            mKeyCodes3 = KeyCodes3.read(requireContext());
//        }

        Context c = requireContext();

        TouchAreaView touchAreaView = new TouchAreaView(c);
        OneProfile profile = ModelProvider.readCurrentProfile();
        touchAreaView.setProfile(profile);

        if(getArguments()!=null && getArguments().getBoolean(ARGV_START_EDIT_ON_SHOW))
            touchAreaView.startEdit();

        if(requireActivity() instanceof EDMainActivity)
            touchAreaView.setBackgroundResource(R.drawable.someimg);

        return touchAreaView;
//        return QH.wrapAsDialogScrollView(buildUI());
    }

    @Override
    public void onResume() {
        super.onResume();
        //必须要在resume这里调用一下requestFocus，否则返回键拦截不到会直接退出Fragment
        Const.getTouchView().requireFocus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResultCallback callback = intentCallbackList.get(requestCode);

        if (data == null || data.getData() == null || callback==null)
            return;

        if (requestCode == REQUEST_IMPORT_PROFILE) {
            callback.onReceive(requestCode, resultCode, data);
        } else if (requestCode == REQUEST_EXPORT_PROFILE) {
            callback.onReceive(requestCode, resultCode, data);
        }
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
