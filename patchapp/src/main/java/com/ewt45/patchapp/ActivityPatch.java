package com.ewt45.patchapp;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ewt45.patchapp.databinding.ActivityPtMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ActivityPatch extends AppCompatActivity {
    private static final String TAG = "ActivityPatch";

    private AppBarConfiguration appBarConfiguration;
    private ActivityPtMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPtMainBinding.inflate(getLayoutInflater());


        //设置强制竖屏？
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
//        int widthPixels = outMetrics.widthPixels;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            widthPixels=Math.abs(getWindowManager().getCurrentWindowMetrics().getBounds().width());
//        }
//        int maxWidth = AndroidUtils.toPx(this,500);
//        if(widthPixels>maxWidth){
//            binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(maxWidth,-1));
//        }

        //设置内部路径
        PatchUtils.setExternalFilesDir(getExternalFilesDir(null).getAbsolutePath());

        //写入版本号
        getSharedPreferences("config", MODE_PRIVATE).edit().putInt("versionCode", BuildConfig.VERSION_CODE).apply();

        //通过检查patcher是否是最新的，如果不是就重新解压

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_pt_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //include的话要再用一个binding 或者给include 设置id
//        binding.appbarPatchStepMain

    }

    /**
     * 非修改界面（帮助和设置）应该隐藏这个。onStart里调用吧
     */
    public void changePatchStepTitleAndFABVisibility(boolean hide) {
        binding.appbarPatchStepMain.getRoot().setVisibility(hide?GONE:VISIBLE);
        ((View) binding.fab).setVisibility(hide?GONE:VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(!MyApplication.data.isShowingLog)
            super.onBackPressed();
        else{
//            Toast.makeText(this, "阻止fragment切换", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pt_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_pt_main).navigate(R.id.action_fragmentPatchMain_to_settingPreferences);
            return true;
        } else if (id == R.id.action_help_step) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_pt_main).navigate(R.id.action_fragmentPatchMain_to_fragmentHelp);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_pt_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}