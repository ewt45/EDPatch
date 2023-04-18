package com.eltechs.ed.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import com.eltechs.ed.ContainerPackage;
import com.eltechs.ed.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ChoosePackagesDFragment extends DialogFragment {
    OnPackagesSelectedListener mListener;
    List<ContainerPackage> mSelectedItems;

    /* loaded from: classes.dex */
    public interface OnPackagesSelectedListener {
        void onPackagesSelected(List<ContainerPackage> list);
    }

    @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnPackagesSelectedListener) context;
        } catch (ClassCastException unused) {
            throw new ClassCastException(context.toString() + " must implement OnPackagesSelectedListener");
        }
    }

    @Override // android.support.v4.app.DialogFragment
    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        this.mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select packages").setAdapter(new ArrayAdapter(getContext(), (int) R.layout.multichoice_list_item, ContainerPackage.LIST), null).setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.eltechs.ed.fragments.ChoosePackagesDFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ChoosePackagesDFragment.this.mListener.onPackagesSelected(ChoosePackagesDFragment.this.mSelectedItems);
                ChoosePackagesDFragment.this.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.eltechs.ed.fragments.ChoosePackagesDFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ChoosePackagesDFragment.this.dismiss();
            }
        });
        AlertDialog create = builder.create();
        create.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.eltechs.ed.fragments.ChoosePackagesDFragment.3
            @Override // android.content.DialogInterface.OnShowListener
            public void onShow(DialogInterface dialogInterface) {
                ((AlertDialog) ChoosePackagesDFragment.this.getDialog()).getButton(-1).setEnabled(!ChoosePackagesDFragment.this.mSelectedItems.isEmpty());
            }
        });
        create.getListView().setChoiceMode(2);
        create.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.eltechs.ed.fragments.ChoosePackagesDFragment.4
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ContainerPackage containerPackage = (ContainerPackage) adapterView.getItemAtPosition(i);
                if (((CheckedTextView) view).isChecked()) {
                    ChoosePackagesDFragment.this.mSelectedItems.add(containerPackage);
                } else {
                    ChoosePackagesDFragment.this.mSelectedItems.remove(containerPackage);
                }
                ((AlertDialog) ChoosePackagesDFragment.this.getDialog()).getButton(-1).setEnabled(!ChoosePackagesDFragment.this.mSelectedItems.isEmpty());
            }
        });
        return create;
    }
}