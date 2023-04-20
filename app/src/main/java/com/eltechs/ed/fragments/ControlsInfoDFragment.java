package com.eltechs.ed.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.eltechs.axs.helpers.AndroidHelpers;
//import com.eltechs.ed.R;
import com.eltechs.ed.controls.Controls;
import com.eltechs.ed.controls.ControlsInfoElem;
import com.eltechs.ed.R;

import java.util.List;

/* loaded from: classes.dex */
public class ControlsInfoDFragment extends DialogFragment {
    public static final String ARG_CONTROLS_ID = "CONTROLS_ID";
    private static final int VIEW_TYPE_NO_IMAGE = 1;
    private static final int VIEW_TYPE_REGULAR = 0;
    private Controls mControls;
    private int mImageRes;
    private List<ControlsInfoElem> mItems;
    private RecyclerView mRecyclerView;

    @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mControls = Controls.find(getArguments().getString(ARG_CONTROLS_ID));
        this.mItems = this.mControls.getInfoElems();
        this.mImageRes = this.mControls.getInfoImage();
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (this.mItems.size() > 1) {
            FrameLayout frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.ex_basic_list, viewGroup, false);
            this.mRecyclerView = (RecyclerView) frameLayout.findViewById(R.id.list);
            this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mRecyclerView.getContext()));
            return frameLayout;
        }
        return (ScrollView) layoutInflater.inflate(R.layout.ex_controls_info_image, viewGroup, false);
    }

    @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mItems.size() > 1) {
            this.mRecyclerView.setAdapter(new ControlsInfoAdapter(this.mItems));
            return;
        }
        ((TextView) getView().findViewById(R.id.header)).setText(this.mItems.get(0).mHeaderText);
        ((TextView) getView().findViewById(R.id.description)).setText(this.mItems.get(0).mDescrText);
        ((ImageView) getView().findViewById(R.id.image)).setImageResource(this.mImageRes);
    }

    /* loaded from: classes.dex */
    private class ControlsInfoAdapter extends RecyclerView.Adapter<ControlsInfoAdapter.ViewHolder> {
        private final List<ControlsInfoElem> mItems;

        public ControlsInfoAdapter(List<ControlsInfoElem> list) {
            this.mItems = list;
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public final ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ex_controls_info_list_item, viewGroup, false), i);
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.mItem = this.mItems.get(i);
//            if (viewHolder.mItem.mImageRes != 0) {
//                viewHolder.mImage.setImageResource(viewHolder.mItem.mImageRes);
//            }
            viewHolder.mHeader.setText(viewHolder.mItem.mHeaderText);
            viewHolder.mBodyText.setText(viewHolder.mItem.mDescrText);
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return this.mItems.get(i).mImageRes == 0 ? 1 : 0;
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public final int getItemCount() {
            return this.mItems.size();
        }

        /* loaded from: classes.dex */
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mBodyText;
            public TextView mHeader;
            public ImageView mImage;
            public ControlsInfoElem mItem;
            public final View mView;

            public ViewHolder(View view, int i) {
                super(view);
                this.mView = view;
                this.mImage = (ImageView) view.findViewById(R.id.image);
                this.mHeader = (TextView) view.findViewById(R.id.header);
                this.mBodyText = (TextView) view.findViewById(R.id.description);
                if (i == 1) {
                    this.mView.setPadding(this.mView.getPaddingLeft(), AndroidHelpers.dpToPx(8), this.mView.getPaddingRight(), AndroidHelpers.dpToPx(15));
                    this.mImage.setVisibility(View.GONE);
                    this.mHeader.setPadding(0, 0, 0, 0);
                    this.mBodyText.setPadding(0, 0, 0, 0);
                }
            }
        }
    }
}