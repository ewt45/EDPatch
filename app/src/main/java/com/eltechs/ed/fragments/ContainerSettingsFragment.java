package com.eltechs.ed.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.eltechs.ed.guestContainers.GuestContainerConfig;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.RSIDHelper;
import com.example.datainsert.exagear.RR;

import java.util.Arrays;

public class ContainerSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String ARG_CONT_ID = "CONT_ID";
    /**用于记录本次dialog期间选定的分辨率*/
    String curResolution;
    private static final String TAG = "ContSettingsFragment";
    @Override // android.support.v7.preference.PreferenceFragmentCompat
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

//        assert getArguments() != null;
        long valueOf;
        if(getArguments()!=null){
            valueOf = getArguments().getLong("CONT_ID");
        }else{
            valueOf = 0L;
        }
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(GuestContainerConfig.CONTAINER_CONFIG_FILE_KEY_PREFIX + valueOf);
        setPreferencesFromResource(RSIDHelper.rslvID(R.xml.container_prefs,0x7f100000), rootKey);

    }

    @Override // android.support.v7.preference.PreferenceFragmentCompat, android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(RSIDHelper.rslvID(R.string.wd_title_container_prop,0x7f0d00a0));
    }
    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            updatePreference(preference);
            preference.setSingleLineTitle(false);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        updatePreference(findPreference(str));
    }

    /**
     * 如果自定义preference的话要重写这个
     * @param preference 点击的pref选项
     */
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if(!preference.getKey().equals("SCREEN_SIZE")){
            super.onDisplayPreferenceDialog(preference);
            return;
        }
        //如果是分辨率选项，自定义一下
        //arsc里记录的分辨率键和值

        String[] strEntries = requireContext().getResources().getStringArray(RSIDHelper.rslvID(R.array.cont_pref_screen_size_entries,0x7f030006));
        String[] strValues = requireContext().getResources().getStringArray(RSIDHelper.rslvID(R.array.cont_pref_screen_size_values,0x7f030007));

        //用于记录本次dialog期间选定的分辨率（这个默认返回default不准确，应该是value的第一个值，有可能是Default）
        curResolution = preference.getSharedPreferences().getString("SCREEN_SIZE",strValues[0]);


        ScrollView dialogView = new ScrollView(requireContext());
        LinearLayout linearLayout = new LinearLayout(requireContext());
        dialogView.addView(linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int px = (int) (20*requireContext().getResources().getDisplayMetrics().density+0.5f);
        linearLayout.setPadding(px,px,px,px);
        RadioGroup radioGroup = new RadioGroup(requireContext());

        //添加自定义的选项
        //开关
        Switch switchToCustom = new Switch(requireContext());
        switchToCustom.setText(RR.getS(RR.CstRsl_swtTxt));
        LinearLayout resolutionLinearLayout = new LinearLayout(requireContext());
        resolutionLinearLayout.setOrientation(LinearLayout.VERTICAL);
//            LinearLayout rsSwitchLLayout = new LinearLayout(requireContext());
//            rsSwitchLLayout.setOrientation(LinearLayout.VERTICAL);
//            TextView textView = new TextView(requireContext());
//            textView.setText("宽高用英文逗号分隔。示例：800,600");
//            rsSwitchLLayout.addView(switchToCustom);
//            rsSwitchLLayout.addView(textView);
        //输入文本
        LinearLayout rsEditLLayout = new LinearLayout(requireContext());
        EditText widthEText = new EditText(requireContext());
        widthEText.setHint(RR.getS(RR.CstRsl_editW));
//        widthEText.setInputType(InputType.TYPE_NULL);
//        widthEText.setFocusable(false);
        EditText heightEText = new EditText(requireContext());
        heightEText.setHint(RR.getS(RR.CstRsl_editH));
        TextView commaText = new TextView(requireContext());
        commaText.setText(",");
        rsEditLLayout.addView(widthEText);
        rsEditLLayout.addView(commaText);
        rsEditLLayout.addView(heightEText);
        resolutionLinearLayout.addView(switchToCustom, new ViewGroup.LayoutParams(-2,-2));
        resolutionLinearLayout.addView(rsEditLLayout);

        for(String str:strEntries){
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(str);
            radioGroup.addView(radioButton);
        }

        linearLayout.addView(resolutionLinearLayout);
        linearLayout.addView(radioGroup);

        //设置一些布局的属性


        //设置监听，点击单选项时存到curResolution
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String selectStr =  ((RadioButton)group.findViewById(checkedId)).getText().toString();
                for (int i = 0; i < strEntries.length; i++) {
                    if(strEntries[i].equals(selectStr)){
                        curResolution = strValues[i];
                        break;
                    }
                }
            }
        });
        //设置监听，开关切换时禁用和开启对应项
        switchToCustom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //禁用radiobutton
                    for(int i=0; i<radioGroup.getChildCount(); i++){
                        radioGroup.getChildAt(i).setEnabled(false);
                    }
                    //启用edittext
                    widthEText.setEnabled(true);
                    heightEText.setEnabled(true);

                }else{
                    for(int i=0; i<radioGroup.getChildCount(); i++){
                        radioGroup.getChildAt(i).setEnabled(true);
                    }
                    widthEText.setEnabled(false);
                    heightEText.setEnabled(false);
                }
            }
        });

        //只能输入数字
        widthEText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        heightEText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        //初始时选中pref设置的单选项/自定义分辨率
        for(int i=0; i<strValues.length; i++){
            //如果是预设的，那么选择单选项，禁用自定义分辨率，结束
            if(strValues[i].equals(curResolution)){
                ((RadioButton)radioGroup.getChildAt(i)).setChecked(true);
                widthEText.setEnabled(false);
                heightEText.setEnabled(false);
                break;
            }
            //如果找到最后一个了也不是，说明是自定义，启用自定义分辨率,填写当前宽高，禁用单选项
            else if(i== strEntries.length-1){
                String[] reso = curResolution.split(",");
                widthEText.setText(reso[0]);
                heightEText.setText(reso[1]);
                switchToCustom.setChecked(true);
            }
        }

        Log.d(TAG, "onDisplayPreferenceDialog: 获取到的array为"+ Arrays.toString(strEntries));
        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle(preference.getTitle())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //关闭对话框且点的是确认键，则修改sharePref
                        //如果是自定义分辨率，检查完整性并转为字符串形式
                        if(switchToCustom.isChecked()){
                            //不知道怎么取消退出dialog，如果没填就改成0吧
                            curResolution = (TextUtils.isEmpty(widthEText.getText().toString())
                                    ? "0" : widthEText.getText().toString()) +
                                    "," +
                                    (TextUtils.isEmpty(heightEText.getText().toString())
                                            ? "0" : heightEText.getText().toString());
                        }
                        preference.getSharedPreferences().edit()
                                .putString("SCREEN_SIZE", curResolution).apply();
//                    Toast.makeText(getContext(), "修改设置成功，重启应用生效", Toast.LENGTH_SHORT).show();
                        //更新summary的显示
                        preference.setSummary(curResolution);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();

    }




    private void updatePreference(Preference preference) {
        if ((preference instanceof EditTextPreference)) {

//            preference.dialog
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            editTextPreference.setSummary(editTextPreference.getText());
        }else if("SCREEN_SIZE".equals(preference.getKey())){
            //这里default也改成strValues[0]
            String[] strValues = requireContext().getResources().getStringArray(RSIDHelper.rslvID(R.array.cont_pref_screen_size_values,0x7f030007));
            preference.setSummary(preference.getSharedPreferences().getString("SCREEN_SIZE",strValues[0]));
        }
    }


}
