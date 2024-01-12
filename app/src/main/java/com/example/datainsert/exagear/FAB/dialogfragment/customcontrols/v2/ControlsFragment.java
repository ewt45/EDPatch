package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.ModelFileSaver;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneProfile;
import com.example.datainsert.exagear.QH;

import java.lang.ref.WeakReference;

public class ControlsFragment extends Fragment {
    static final int REQUEST_IMPORT_PROFILE = 124;
    static final int REQUEST_EXPORT_PROFILE = 125;

    public ControlsFragment() {
        Const.fragmentRef = new WeakReference<>(this);
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

        if (QH.isTesting())
            requireActivity().findViewById(R.id.ed_main_toolbar).setVisibility(View.GONE);

        Context c = requireContext();
        //TODO 必须在任何操作前初始化一次
        Const.init(c);
        int frameRootId = View.generateViewId();
        FrameLayout frameRoot = new FrameLayout(c);
        frameRoot.setId(frameRootId);
        frameRoot.setBackgroundResource(R.drawable.someimg);

        TouchAreaView touchAreaView = new TouchAreaView(c);
        OneProfile profile = ModelFileSaver.readCurrentProfile();
        profile.syncAreaList(touchAreaView);
        touchAreaView.setProfile(profile);
        touchAreaView.startEdit();
        frameRoot.addView(touchAreaView);


        return frameRoot;
//        return QH.wrapAsDialogScrollView(buildUI());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || data.getData() == null )
            return;
        String endMsg;
        if (requestCode == REQUEST_IMPORT_PROFILE) {
            try {
                OneProfile oneProfile = ModelFileSaver.importProfileFromUri(data.getData());
                Const.profilesAdapterRef.get().refreshDataSet(); //导入成功后要刷新列表显示
                Const.profilesAdapterRef.get().notifyDataSetChanged();
                endMsg = "导入成功: "+oneProfile.name;
            } catch (Exception e) {
                endMsg = "导入失败: " + e.getCause();
            }
            Toast.makeText(requireContext(), endMsg, Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == REQUEST_EXPORT_PROFILE){
            try {
                ModelFileSaver.exportProfileToUri(data.getData(),profileExporting);
                endMsg = "导出成功: "+profileExporting;
            } catch (Exception e) {
                endMsg = "导出失败: " + e.getCause();
            }
            profileExporting = null;
            Toast.makeText(requireContext(), endMsg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从本地文件导入配置
     */
    public void requestImportProfile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_IMPORT_PROFILE);
    }

    private String profileExporting; //用于被导出的配置，只能用一次，导出后置为null
    /**
     * 将某个配置保存到本地，调用此函数前应确保该model数据是最新的，比如 如果导出的是当前编辑的model，那么应该先保存一下
     */
    public void requestExportProfile(@NonNull String profileName) {
        profileExporting = profileName;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, profileName);
        startActivityForResult(intent, REQUEST_EXPORT_PROFILE);
    }

}
