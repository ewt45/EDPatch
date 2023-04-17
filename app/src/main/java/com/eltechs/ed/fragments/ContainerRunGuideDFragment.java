package com.eltechs.ed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.eltechs.ed.AppRunGuide;
import com.eltechs.ed.R_original;
import com.eltechs.ed.guestContainers.GuestContainer;
import com.eltechs.ed.guestContainers.GuestContainersManager;

/* loaded from: classes.dex */
public class ContainerRunGuideDFragment extends DialogFragment {
    public static final String ARG_CONT_ID = "CONT_ID";
    public static final String ARG_IS_SHOW_ONLY = "IS_SHOW_ONLY";
    private GuestContainer mContainer;
    private AppRunGuide mGuide;
    private boolean mIsShowOnly;
    private OnContRunGuideResListener mListener;

    /* loaded from: classes.dex */
    public interface OnContRunGuideResListener {
        void onContRunGuideRes(boolean z);
    }

    @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContainer = GuestContainersManager.getInstance(getContext()).getContainerById(Long.valueOf(getArguments().getLong("CONT_ID")));
        this.mGuide = AppRunGuide.guidesMap.get(this.mContainer.mConfig.getRunGuide());
        this.mIsShowOnly = getArguments().getBoolean(ARG_IS_SHOW_ONLY);
        if (this.mIsShowOnly) {
            return;
        }
        try {
            this.mListener = (OnContRunGuideResListener) context;
        } catch (ClassCastException unused) {
            throw new ClassCastException(context.toString() + " must implement OnContRunGuideResListener");
        }
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R_original.layout.container_run_guide, viewGroup, false);
        if (this.mIsShowOnly) {
            linearLayout.findViewById(R_original.id.btn_dontshow).setVisibility(View.GONE);
        }
        return linearLayout;
    }

    @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ((TextView) getView().findViewById(R_original.id.header)).setText(getResources().getString(this.mGuide.mHeaderRes));
        ((TextView) getView().findViewById(R_original.id.body)).setText(Html.fromHtml(getResources().getString(this.mGuide.mBodyRes)));
        ((Button) getView().findViewById(R_original.id.btn_ok)).setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.fragments.ContainerRunGuideDFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ContainerRunGuideDFragment.this.dismiss();
                if (ContainerRunGuideDFragment.this.mIsShowOnly) {
                    return;
                }
                ContainerRunGuideDFragment.this.mListener.onContRunGuideRes(true);
            }
        });
        ((Button) getView().findViewById(R_original.id.btn_dontshow)).setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.fragments.ContainerRunGuideDFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ContainerRunGuideDFragment.this.mContainer.mConfig.setRunGuideShown(true);
                ContainerRunGuideDFragment.this.dismiss();
                if (ContainerRunGuideDFragment.this.mIsShowOnly) {
                    return;
                }
                ContainerRunGuideDFragment.this.mListener.onContRunGuideRes(false);
            }
        });
    }

    public static DialogFragment createDialog(GuestContainer guestContainer, boolean z) {
        ContainerRunGuideDFragment containerRunGuideDFragment = new ContainerRunGuideDFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("CONT_ID", guestContainer.mId.longValue());
        bundle.putBoolean(ARG_IS_SHOW_ONLY, z);
        containerRunGuideDFragment.setArguments(bundle);
        return containerRunGuideDFragment;
    }
}