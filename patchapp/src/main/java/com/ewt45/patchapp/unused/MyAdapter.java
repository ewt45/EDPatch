package com.ewt45.patchapp.unused;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ewt45.patchapp.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    Context context;
    List<DataBean> mDatas;

    public void setDatas(List<DataBean> datas) {
        mDatas = datas;
    }

    public MyAdapter(Context context, List<DataBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_app_name, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drawable icon = mDatas.get(position).getImageId();
        holder.imageView.setImageDrawable(icon);
        String appName = mDatas.get(position).getAppName();
        holder.textView.setText(appName);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.app_icon);
            textView = itemView.findViewById(R.id.app_name);
        }
    }
}

