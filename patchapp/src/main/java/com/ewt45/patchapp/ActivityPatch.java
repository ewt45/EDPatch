package com.ewt45.patchapp;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ViewUtils;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ewt45.patchapp.databinding.ActivityPtMainBinding;
import com.ewt45.patchapp.unused.MyAdapter;
import com.ewt45.patchapp.unused.MyService;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ActivityPatch extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPtMainBinding binding;
    private List<String> mDatas = new ArrayList<String>();
    private MyAdapter myAdapter;
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

        setContentView(binding.getRoot());


//        Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
//        intent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
//        intent.putExtra("sender", "str");
//        ResolveInfo resolveInfo = getPackageManager().queryIntentServices(intent, 0).get(0);
//        new Intent(intent).setComponent(new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name));


        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_pt_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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
            Navigation.findNavController(this,R.id.nav_host_fragment_content_pt_main).navigate(R.id.settingPreferences);
            return true;
        }else if(id == R.id.action_help_step){
            Navigation.findNavController(this,R.id.nav_host_fragment_content_pt_main).navigate(R.id.fragmentHelp);
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