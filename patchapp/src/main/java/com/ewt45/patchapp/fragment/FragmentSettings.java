package com.ewt45.patchapp.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ewt45.patchapp.ActivityPatch;
import com.ewt45.patchapp.BuildConfig;
import com.ewt45.patchapp.MyApplication;
import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;

import java.io.File;

public class FragmentSettings extends PreferenceFragmentCompat {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置 数据存到的文件名
        getPreferenceManager().setSharedPreferencesName(MyApplication.PREFERENCE);
        setHasOptionsMenu(true); //隐藏右上角菜单。必须先设置为true，待会onCreateOptionsMenu才会被调用，在那里给每个menuitem设置隐藏。

    }

    @Override
    public void onStart() {
        super.onStart();
        ((ActivityPatch) requireActivity()).changePatchStepTitleAndFABVisibility(true);
    }

    //如果想自定义点击一个preference的操作：
    // xml中新建checkbox，widgetLayout设置一个空的布局。这里通过key获取对应preference，设置setOnPreferenceClickListener
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        setPreferencesFromResource(R.xml.pref_settings, s);
        //通过key寻找preference findPreference(key)
        Preference delPatcherPref = findPreference("clear_patcher_apk");
        //删除patcher
        delPatcherPref.setOnPreferenceClickListener(p -> {
            File patcher = PatchUtils.getLocalPatcherApk();
            String msg;
            if (patcher.exists()) {
                boolean b = patcher.delete();
                msg = b ? "patcher.apk删除成功" : "patcher.apk删除失败";
            } else
                msg = "patcher.apk不存在";
//            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            return true;
        });

        //设置版本号
        Preference versionPref = findPreference("version");
        versionPref.setSummary(BuildConfig.VERSION_NAME);
        versionPref.setOnPreferenceClickListener(preference -> true);

        //设置第三方项目依赖
        Preference creditPref = findPreference("credits");
        creditPref.setOnPreferenceClickListener(p -> {
            ListView listView = new ListView(requireContext());
            String[] creditsName = getResources().getStringArray(R.array.credits_name);
            String[] creditsLink = getResources().getStringArray(R.array.credits_link);
            listView.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, creditsName));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(creditsLink[position]));
                    if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                        startActivity(intent);
                    }
//                    Toast.makeText(requireContext(),creditsLink[position],Toast.LENGTH_SHORT).show();
                }
            });
            new AlertDialog.Builder(requireContext())
                    .setView(listView).create().show();

            return true;
        });

        //更新地址
        findPreference("update").setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ewt45/EDPatch/releases"));
            if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        });
    }


    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        super.onDisplayPreferenceDialog(preference);
    }

}
