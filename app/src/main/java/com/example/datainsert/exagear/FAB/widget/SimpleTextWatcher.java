package com.example.datainsert.exagear.FAB.widget;

import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;


/**
 * edittext的监听，只需重写afterTextChanged
 */
public class SimpleTextWatcher implements TextWatcher {
    public interface Callback{
        public void afterTextChanged(Editable s);
    }
    Callback mCallback;
    public SimpleTextWatcher(Callback callback){
        mCallback=callback;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
       mCallback.afterTextChanged(s);
    }
}
