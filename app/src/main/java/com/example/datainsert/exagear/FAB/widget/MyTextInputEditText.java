package com.example.datainsert.exagear.FAB.widget;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.datainsert.exagear.FAB.dialogfragment.DriveD;

import java.util.ArrayList;
import java.util.Arrays;


public class MyTextInputEditText extends TextInputEditText {

//    long lastPopCloseTime; //设一个这个，解决ex的popupMenu在clearFocus也弹出来
    private PopupMenu popupMenu;
    public MyTextInputEditText(Context context,String[] keys,String[] values,String hint) {
        super(context);
        setHint(hint);
        //设置第一次点击提示填充
        if(keys!=null && values!=null){
            popupMenu = new PopupMenu(context,this);
            for (int i = 0; i < keys.length; i++) {
                popupMenu.getMenu().add(Menu.NONE, i, Menu.NONE, keys[i]).setOnMenuItemClickListener(item -> {
                    this.setText(values[item.getItemId()]);
                    return true;
                });
            }
        }
        setOnClickListener(v-> {
            if(popupMenu!=null) popupMenu.show();
        });
        setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus && popupMenu!=null)
                popupMenu.show();
        });
//        setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line,entries));
//        setThreshold(0);

    }

    public void setPopupMenu(PopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }


//    /**
//     * 安卓11模拟器上怎么还出来语法纠错了，禁用掉（好像不行啊）
//     * @return
//     */
//    @Override
//    public int getAutofillType() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //禁止EditText自动填充
//            return AUTOFILL_TYPE_NONE;
//        } else {
//            return super.getAutofillType();
//        }
//    }
    public void setTextChangeListener(){

    }

    public void setTextChangeListener(Object updateCurrentParDir) {
    }

    //    public static class AnyArrayAdapter<T> extends BaseAdapter implements Filterable {
//        private final T[] autoList;
//        public AnyArrayAdapter(@NonNull Context context, int resource, @NonNull T[] objects) {
//            super();
//            autoList = objects;
//
//        }
//
//        @NonNull
//        @Override
//        public Filter getFilter() {
//            return new Filter() {
//                @Override
//                protected FilterResults performFiltering(CharSequence constraint) {
//                    FilterResults results = new FilterResults();
//                    results.values = Arrays.asList(autoList);
//                    results.count = autoList.length;
//                    return results;
//                }
//
//                @Override
//                protected void publishResults(CharSequence constraint, FilterResults results) {
////                    data = (ArrayList)results.values;
////                    notifyDataSetChanged();
//                }
//            };
//        }
//
//
//        @Override
//        public int getCount() {
//            return autoList.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return autoList[position];
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView tv = new TextView(parent.getContext());
//            tv.setText((CharSequence) autoList[position]);
//            return tv;
//        }
//    }



}
