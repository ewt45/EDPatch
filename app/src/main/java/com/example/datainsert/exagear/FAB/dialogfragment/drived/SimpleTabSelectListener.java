package com.example.datainsert.exagear.FAB.dialogfragment.drived;

import android.support.design.widget.TabLayout;

public abstract class SimpleTabSelectListener implements TabLayout.OnTabSelectedListener {
    public abstract void onTabSelectedOrReSel(TabLayout.Tab tab);

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        onTabSelectedOrReSel(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelectedOrReSel(tab);
    }
}
