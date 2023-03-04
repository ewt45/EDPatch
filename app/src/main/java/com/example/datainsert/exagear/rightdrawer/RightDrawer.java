package com.example.datainsert.exagear.rightdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.applicationState.CurrentActivityAware;
import com.eltechs.ed.activities.EDMainActivity;
import com.ewt45.exagearsupportv7.R;
import com.example.datainsert.exagear.RSIDHelper;

public class RightDrawer {
    static String TAG = "RightDrawer";
    public static void init() {
        FrameworkActivity c = ((CurrentActivityAware) Globals.getApplicationState()).getCurrentActivity();
//                Globals.getApplicationState() == null ? Globals.getFrameworkActivity() : ((CurrentActivityAware) Globals.getApplicationState()).getCurrentActivity();
        //防止切后台后多次新建
        if(((DrawerLayout) c.findViewById(RSIDHelper.rslvID(R.id.drawer_layout, 0x7f09006f))).getChildCount()>=3){
            return;
        }
        NavigationView navigationView1 = new NavigationView(c);
        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(-2, -1, Gravity.END);
        navigationView1.inflateHeaderView(RSIDHelper.rslvID(R.layout.nav_header_main,0x7f0b0036));

        navigationView1.inflateMenu(RSIDHelper.rslvID(R.menu.right_drawer,0x7f0c0100));
        Menu menu = navigationView1.getMenu();
        navigationView1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int i = 0;
                for (; i < menu.size(); i++) {
                    if (menu.getItem(i).equals(menuItem))
                        break;
                }
                //i是子布局下标
                FrameworkActivity c = ((CurrentActivityAware) Globals.getApplicationState()).getCurrentActivity();

                //启动fragment
                RightDrawerFragment fragment = new RightDrawerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("index", i);
                bundle.putString("title",menuItem.getTitle().toString());
                fragment.setArguments(bundle);

                c.getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(RSIDHelper.rslvID(R.id.ed_main_fragment_container,0x7f090070), fragment, null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
                //启动activity
//                c.startActivity(new Intent(c, RightDrawerAcitvity.class));
                ((DrawerLayout) c.findViewById(RSIDHelper.rslvID(R.id.drawer_layout, 0x7f09006f))).closeDrawers();

                return true;
            }
        });

        ((DrawerLayout) c.findViewById(RSIDHelper.rslvID(R.id.drawer_layout, 0x7f09006f))).addView(navigationView1, params);
    }
}
