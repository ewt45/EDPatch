package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getOneLineWithTitle;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_FILE_NAME_SETTING;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_HEIGHT;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_WIDTH;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.UnmovableBtn;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.ArrayList;
import java.util.List;

public class BtnKeyAdapter extends ListAdapter<OneKey, BtnKeyAdapter.ViewHolder> {
    private static final String TAG="BtnKeyAdapter";
    private final boolean isLandScape;
    public static final DiffUtil.ItemCallback<OneKey> DIFF_CALLBACK = new DiffUtil.ItemCallback<OneKey>() {
        @Override
        public boolean areItemsTheSame(@NonNull OneKey oldKey, @NonNull OneKey newKeys) {
            return oldKey.getCode() == newKeys.getCode();
        }

        @Override
        public boolean areContentsTheSame(@NonNull OneKey oldKey, @NonNull OneKey newKey) {
            return oldKey.equals(newKey);
        }
    };

    protected BtnKeyAdapter(boolean isLandScape) {
        super(DIFF_CALLBACK);
        this.isLandScape = isLandScape;
    }

    @NonNull
    @Override
    public BtnKeyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Button btn = UnmovableBtn.getSample(viewGroup.getContext());
        SharedPreferences sp = viewGroup.getContext().getSharedPreferences(PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE);
        //当高=-2且横向布局的时候不知道为什么回收视图会坍缩，只好手动强制设定高度了
        int height = sp.getInt(PREF_KEY_BTN_HEIGHT, -2);
        btn.setLayoutParams(new ViewGroup.LayoutParams(
                sp.getInt(PREF_KEY_BTN_WIDTH, -2),
                height==-2&&isLandScape?QH.px(viewGroup.getContext(),50):height
                ));
        return new BtnKeyAdapter.ViewHolder(btn);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.getmBtn().setText(getItem(position).getName());
        viewHolder.getmBtn().setTag(getItem(position));
        viewHolder.getmBtn().setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: 点击没反应？");
            Context c = v.getContext();
            EditText editText = new EditText(c);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            editText.setSingleLine();
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editText.setText(((OneKey) v.getTag()).getName());
            editText.setLayoutParams(new ViewGroup.LayoutParams(QH.px(c, 100),-2));
            LinearLayout renameRootView = getOneLineWithTitle(c,getS(RR.cmCtrl_BtnEditReName),editText,false);
            int padding = QH.px(c, RR.attr.dialogPaddingDp);
            renameRootView.setPadding(padding,0,padding,0);
            new AlertDialog.Builder(c)
                    .setView(renameRootView)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        OneKey newSelfKey = ((OneKey) v.getTag()).clone();
                        newSelfKey.setName(editText.getText().toString());
                        //更新按钮的tag和文字显示
                        v.setTag(newSelfKey);
                        ((Button)v).setText(newSelfKey.getName());
                        //更新adapter的数据列表
                        List<OneKey> newList = getCurrentList();
                        int index=getIndexOfItem(newList,newSelfKey);
                        newList.remove(index);
                        newList.add(index,newSelfKey);
                        submitList(newList);
                    })
                    .setNegativeButton(android.R.string.cancel,null)
                    .create().show();
//            PopupMenu popupMenu = new PopupMenu(c,v);
//            popupMenu.getMenu().add(getS(RR.cmCtrl_s2_popEdit)).setOnMenuItemClickListener(item -> {
//
//                return true;
//            });
////            MenuItem itemFixTop=popupMenu.getMenu().add("固定置顶");
//            popupMenu.getMenu().add(getS(RR.cmCtrl_s2_popDel)).setOnMenuItemClickListener(item->{
//                OneKey selfKey = (OneKey) v.getTag();
//                List<OneKey> newList = getCurrentList();
//                newList.remove(getIndexOfItem(newList,selfKey));
//                submitList(newList);
//                return true;
//            });
//            popupMenu.show();


        });


    }

    public List<OneKey> getCurrentList() {
        List<OneKey> list = new ArrayList<>();
        int length = getItemCount();
        for (int i = 0; i < length; i++)
            list.add(getItem(i).clone());
        return list;
    }

    public int getIndexOfItem(List<OneKey> newList, OneKey item){
        int i=0;//找到当前btn对应col所在位置
        for(; i<newList.size();i++)
            if(newList.get(i).getCode()== item.getCode())
                break;
        return i;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button mBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBtn = (Button) itemView;
        }

        public Button getmBtn() {
            return mBtn;
        }

        public void bind(){

        }
    }
}
