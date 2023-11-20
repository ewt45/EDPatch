package com.example.datainsert.exagear.FAB.widget;

import android.text.Editable;
import android.text.TextWatcher;


/**
 * edittext的监听，只需重写afterTextChanged
 * @deprecated 请使用QH.SimpleTextWatcher
 */
@Deprecated
public interface SimpleTextWatcher extends TextWatcher {

    @Override
    default void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    default void onTextChanged(CharSequence s, int start, int before, int count) {

    }

}
