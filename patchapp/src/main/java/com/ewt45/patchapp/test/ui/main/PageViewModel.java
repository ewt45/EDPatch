package com.ewt45.patchapp.test.ui.main;

//import static androidx.lifecycle.Transformations.map;

import android.arch.core.util.Function;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = new LiveData<String>(){

    };

//    map(mIndex, input -> "Hello world from section: " + input)


    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
}