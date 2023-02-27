package com.ewt45.exagearsupportv7;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.support.design.widget.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.applicationState.ApplicationStateObject;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.ed.EDApplicationState;
import com.ewt45.exagearsupportv7.databinding.ActivityMainBinding;
import com.example.datainsert.SymlinkLib;
import com.example.datainsert.exagear.FAB.FabMenu;
import com.example.datainsert.exagear.rightdrawer.RightDrawer;
import com.example.datainsert.exagear.virgloverlay.OverlayBuildUI;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainActivity extends FrameworkActivity {
    public MainActivity(){
        if (Globals.getApplicationState() == null) {
            Globals.setApplicationState(new ApplicationStateObject(EDApplicationState.class));
        }

    }

    String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals.setAppContext(this.getApplicationContext());
        Globals.setFrameworkActivity(this);

//        Globals.setApplicationState(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setBackgroundResource(R.drawable.someimg);

        Toast.makeText(this, Environment.getExternalStorageState(), Toast.LENGTH_SHORT).show();

//        binding.appBarMain.fab.setOnClickListener(view -> {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
//                .setOpenableLayout(drawer)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.ed_main_fragment_container);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        new FabMenu(this);
//        Toast.makeText(this, "初始化d盘路径为"+DriveD.getDriveDDir().getAbsolutePath(), Toast.LENGTH_SHORT).show();
//        SymlinkLib.create(this);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
//        new OverlayBuildUI(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RightDrawer.init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Globals.setAppContext(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.ed_main_fragment_container);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        Log.d(TAG, "onActivityResult:activity requestCode:"+requestCode+", resultCode"+resultCode);

    }
}