package com.eltechs.axs.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.eltechs.ed.R;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.PurchasableComponentsCollectionAware;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.payments.PurchasableComponent;

/* loaded from: classes.dex */
public class SelectCustomizationPackageActivity<StateClass extends ApplicationStateBase<StateClass> & PurchasableComponentsCollectionAware & SelectedExecutableFileAware<StateClass>> extends FrameworkActivity<StateClass> {
    private static final int REQUEST_CODE_CP_DRAWABLE = 10001;

    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.select_customization_package);
        resizeRootViewToStandardDialogueSize();
    }

    @Override // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        ((ListView) findViewById(R.id.list_of_all_customization_packages)).setAdapter(new MyAdapter());
    }

    /* loaded from: classes.dex */
    private class MyAdapter extends BaseAdapter {
        private final View[] myViews;

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int i) {
            return -1;
        }

        private MyAdapter() {
            this.myViews = new View[getCount()];
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return getApplicationState().getPurchasableComponentsCollection().getPurchasableComponentsCount();
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (this.myViews[i] != null) {
                return this.myViews[i];
            }
            final PurchasableComponent purchasableComponent = getApplicationState().getPurchasableComponentsCollection().getPurchasableComponent(i);
            @SuppressLint("ViewHolder") LinearLayout linearLayout = (LinearLayout) ((LayoutInflater) SelectCustomizationPackageActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.select_customization_package_elem, viewGroup, false);
            Button button = linearLayout.findViewById(R.id.select_cp_button_elem);
            button.setText(String.format(SelectCustomizationPackageActivity.this.getString(R.string.like_textelem), purchasableComponent.getName()));
            button.setOnClickListener(view2 -> {
                getApplicationState().getSelectedExecutableFile().setUserSelectedCustomizationPackage(purchasableComponent);
                setResult(-1);
                finish();
            });
            linearLayout.findViewById(R.id.select_cp_button_info).setOnClickListener(view2 -> startActivityForResult(REQUEST_CODE_CP_DRAWABLE, UsageActivity.class, purchasableComponent.getInfoResId()));
            this.myViews[i] = linearLayout;
            return linearLayout;
        }
    }

    @Override // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        logDebug("onActivityResult(" + requestCode + "," + resultCode + "," + intent + ")");
        if (requestCode == REQUEST_CODE_CP_DRAWABLE) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}