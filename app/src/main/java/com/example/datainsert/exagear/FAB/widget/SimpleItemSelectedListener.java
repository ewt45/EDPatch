package com.example.datainsert.exagear.FAB.widget;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
    public interface Callback{
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id);
    }
    private final Callback mCallback;
    public SimpleItemSelectedListener(Callback callback){
        mCallback=callback;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mCallback.onItemSelected(parent,view,position,id);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
