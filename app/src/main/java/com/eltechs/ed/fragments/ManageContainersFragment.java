package com.eltechs.ed.fragments;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.eltechs.ed.guestContainers.GuestContainer;
import com.example.datainsert.exagear.mutiWine.MutiWine;

public class ManageContainersFragment extends Fragment {
    private boolean mIsAsyncTaskRun;

    public void createContainerNew(){
        Log.d("TAG", "createContainerNew: 新建容器，执行task。不知道为啥复制过来反编译有问题");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = new LinearLayout(getContext());
        TextView tv = new TextView(getContext());
        tv.setText("ManageContainersFragment");
        layout.addView(tv);
        setHasOptionsMenu(true);//调用这个，fragment里处理menu才有效
        return layout;
    }

    //在这里创建右上角的菜单，需要先调用setHasOptionsMenu.才能进到这个函数里
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MutiWine.setOptionMenu(menu,this);
//        getMenuInflater().inflate(R.menu.toobar_menu, menu);

    }


    /**
     * 准备添加到ex中的方法，用于从外部调用，执行创建容器task
     */
    public void callToCreateNewContainer(){
        new ContAsyncTask(0).execute();
    }
    /**
     * 仿照ex写的私有类，用于创建容器的task
     */
    private class ContAsyncTask extends AsyncTask<GuestContainer, Void, Void> {
        ContAsyncTask(int i){
            Log.d(TAG, "ContAsyncTask: 构造函数"+this);
        }
        @Override
        protected Void doInBackground(GuestContainer... objects) {
            Log.d(TAG, "doInBackground: ContAsyncTask执行");
            return null;
        }

        @Override
        protected void onPreExecute() {
            ManageContainersFragment.this.mIsAsyncTaskRun=true;
            Log.d(TAG, "onPreExecute: 此时smali中会调用this$0");
            super.onPreExecute();
        }
    }

}
