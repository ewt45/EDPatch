package com.example.datainsert.exagear.FAB.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinArrayAdapterSmSize extends ArrayAdapter<String> {
    private final static String TAG="SpinArrayAdapterSmSize";

    public SpinArrayAdapterSmSize(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //改一下文字大小
        View oriView = super.getView(position,convertView,parent);
        TextView textView = new TextView(parent.getContext());
        textView.setText(getItem(position));
        textView.setTextColor(((TextView)oriView).getCurrentTextColor());
        return textView;
//        return oriView;

    }
}
