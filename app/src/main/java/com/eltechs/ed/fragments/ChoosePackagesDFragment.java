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
        this.mSelectedItems = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select packages")
                .setAdapter(new ArrayAdapter<>(requireContext(), R.layout.multichoice_list_item, ContainerPackage.LIST), null)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    mListener.onPackagesSelected(ChoosePackagesDFragment.this.mSelectedItems);
                    dismiss();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dismiss());
        AlertDialog create = builder.create();
        create.setOnShowListener(dialogInterface -> ((AlertDialog) getDialog()).getButton(-1).setEnabled(!mSelectedItems.isEmpty()));
        create.getListView().setChoiceMode(2);
        create.getListView().setOnItemClickListener((adapterView, view, position, id) -> {
            ContainerPackage containerPackage = (ContainerPackage) adapterView.getItemAtPosition(position);
            if (((CheckedTextView) view).isChecked()) {
                ChoosePackagesDFragment.this.mSelectedItems.add(containerPackage);
            } else {
                ChoosePackagesDFragment.this.mSelectedItems.remove(containerPackage);
            }
            ((AlertDialog) ChoosePackagesDFragment.this.getDialog()).getButton(-1).setEnabled(!ChoosePackagesDFragment.this.mSelectedItems.isEmpty());
        });
        return create;
    }

    /* loaded from: classes.dex */
    public interface OnPackagesSelectedListener {
        void onPackagesSelected(List<ContainerPackage> list);
    }
}