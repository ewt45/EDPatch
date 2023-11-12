package com.ewt45.patchapp.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ewt45.patchapp.ActivityPatch;
import com.ewt45.patchapp.R;

import pl.droidsonroids.gif.GifImageView;

public class FragmentHelp extends Fragment {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        for(int i=0; i<menu.size(); i++){
            menu.getItem(i).setVisible(false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //隐藏右上角菜单。必须先设置为true，待会onCreateOptionsMenu才会被调用，在那里给每个menuitem设置隐藏。
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.func_recycler);
//        if (requireActivity().getWindowManager().getDefaultDisplay().getWidth() > AndroidUtils.toPx(requireContext(), 800)) {
//            GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false);
//            recyclerView.setLayoutManager(layoutManager);
//        }


        recyclerView.setAdapter(new FuncdescpAdapter(
                getResources().getStringArray(R.array.func_name),
                getResources().getStringArray(R.array.func_description),
                new int[]{
                        R.drawable.drived,
                        R.drawable.cstmctrl,
                        R.drawable.pulseaudio,
                        R.drawable.showcursor,
                        R.drawable.customresl,
                        R.drawable.softinput,
                        R.drawable.selectobb,
                        R.drawable.shortcut,
                        R.drawable.multiwine,
                        R.drawable.renderer,
                        R.drawable.otherargv
                }
        ));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((ActivityPatch)requireActivity()).changePatchStepTitleAndFABVisibility(true);
    }


    public static class FuncdescpAdapter extends RecyclerView.Adapter<FuncdescpAdapter.ViewHolder> {
        final String[] titles;
        final String[] descriptions;
        final @DrawableRes int[] images;

        public FuncdescpAdapter(String[] titles, String[] strings, int[] images) {
            this.titles = titles;
            this.descriptions = strings;
            this.images = images;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_func_help_descp, viewGroup, false);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.title.setText(titles[i]);
            viewHolder.description.setText(descriptions[i]);
            if (images[i] != 0)
                viewHolder.image.setImageResource(images[i]);


        }

        @Override
        public int getItemCount() {
            return titles.length;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final CardView root;
            private final TextView title;
            private final TextView description;
            private final GifImageView image;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                root = (CardView) itemView;
                title = itemView.findViewById(R.id.title);
                description = itemView.findViewById(R.id.description);
                image = itemView.findViewById(R.id.gif_image);
            }
        }
    }
}
