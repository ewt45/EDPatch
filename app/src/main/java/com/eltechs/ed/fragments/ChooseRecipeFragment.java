package com.eltechs.ed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.eltechs.ed.InstallRecipe;
import com.eltechs.ed.R;
import java.util.List;

/* loaded from: classes.dex */
public class ChooseRecipeFragment extends Fragment {
    private OnRecipeSelectedListener mListener;
    private RecyclerView mRecyclerView;

    /* loaded from: classes.dex */
    public interface OnRecipeSelectedListener {
        void onRecipeSelected(InstallRecipe installRecipe);
    }

    @Override // android.support.v4.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnRecipeSelectedListener) context;
        } catch (ClassCastException unused) {
            throw new ClassCastException(context.toString() + " must implement OnRecipeSelectedListener");
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.ex_basic_list, viewGroup, false);
        this.mRecyclerView = (RecyclerView) frameLayout.findViewById(R.id.list);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mRecyclerView.getContext()));
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(this.mRecyclerView.getContext(), 1));
        return frameLayout;
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mRecyclerView.setAdapter(new RecipeAdapter(InstallRecipe.LIST));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.wd_title_select_install_recipe);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    private class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
        private final List<InstallRecipe> mItems;

        public RecipeAdapter(List<InstallRecipe> list) {
            this.mItems = list;
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public final ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ex_basic_list_item, viewGroup, false));
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            viewHolder.mItem = this.mItems.get(i);
            if (i == getItemCount() - 1) {
                viewHolder.mImage.setImageResource(R.drawable.ic_help_24dp);
            } else {
                viewHolder.mImage.setImageResource(R.drawable.ic_description_24dp);
            }
            viewHolder.mText.setText(viewHolder.mItem.toString());
            viewHolder.mView.setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.fragments.ChooseRecipeFragment.RecipeAdapter.1


                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ChooseRecipeFragment.this.mListener.onRecipeSelected((InstallRecipe) RecipeAdapter.this.mItems.get(viewHolder.getAdapterPosition()));
                }
            });
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public final int getItemCount() {
            return this.mItems.size();
        }

        /* loaded from: classes.dex */
        private class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImage;
            public InstallRecipe mItem;
            public TextView mText;
            public final View mView;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public ViewHolder(View view) {
                super(view);
                this.mView = view;
                this.mImage = (ImageView) view.findViewById(R.id.image);
                this.mText = (TextView) view.findViewById(R.id.text);
            }
        }
    }
}