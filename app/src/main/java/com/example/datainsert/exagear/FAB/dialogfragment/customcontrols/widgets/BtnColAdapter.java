package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.OneCol;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.ArrayList;
import java.util.List;

public class BtnColAdapter extends ListAdapter<OneCol, BtnColAdapter.ViewHolder> {
    final KeyCodes2 mKeyCodes2;
    final boolean mIsLeft;
    public static final DiffUtil.ItemCallback<OneCol> DIFF_CALLBACK = new DiffUtil.ItemCallback<OneCol>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull OneCol oldKeys, @NonNull OneCol newKeys) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldKeys.getId() == newKeys.getId();
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull OneCol oldUser, @NonNull OneCol newUser) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            //只有areItemsTheSame返回true才会调用这个方法
            if (oldUser.getmAllKeys().length != newUser.getmAllKeys().length)
                return false;
            for (int i = 0; i < oldUser.getmAllKeys().length; i++) {
                if (oldUser.getmAllKeys()[i].getCode() != newUser.getmAllKeys()[i].getCode())
                    return false;
            }
            return true;
        }
    };
    public static final int TYPE_ADD_BTN = 2;
    public static final int TYPE_NORMAL_COL = 1;

    public BtnColAdapter(KeyCodes2 keyCodes2, boolean isLeft) {
        super(DIFF_CALLBACK);
        mKeyCodes2=keyCodes2;
        mIsLeft=isLeft;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Button btn = new Button(viewGroup.getContext());
        int width = AndroidHelpers.dpToPx(50);
        btn.setLayoutParams(new ViewGroup.LayoutParams(width, width));
        return new ViewHolder(btn);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        //每个btn带个OneCol，放到tag里
        viewHolder.getmBtn().setText(String.valueOf(getItem(position).getId() + 1));
        viewHolder.getmBtn().setTag(getItem(position));
        viewHolder.getmBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                PopupMenu popupMenu = new PopupMenu(c, v);
                Menu menu = popupMenu.getMenu();
                menu.add(getS(RR.cmCtrl_s2_popEdit)).setOnMenuItemClickListener(item->{
                    OneCol selfCol = (OneCol) v.getTag();
                    LinearLayout linearRoot = new LinearLayout(c);
                    linearRoot.setPadding(QH.px(c, RR.attr.dialogPaddingDp),QH.px(c, RR.attr.dialogPaddingDp),QH.px(c, RR.attr.dialogPaddingDp),QH.px(c, RR.attr.dialogPaddingDp));
                    linearRoot.setOrientation(LinearLayout.VERTICAL);
                    //提示
                    linearRoot.addView(BaseFragment.getTextViewWithText(c,getS(RR.cmCtrl_s2_ColEditTip)));
                    //一列按键
                    BtnKeyRecyclerView recyclerView = new BtnKeyRecyclerView(c,selfCol.getmAllKeys());
                    LinearLayout.LayoutParams recyclerViewParams = new LinearLayout.LayoutParams(-2,-1);
                    recyclerViewParams.gravity= Gravity.CENTER_HORIZONTAL;
                    linearRoot.addView(recyclerView,recyclerViewParams);
                    new AlertDialog.Builder(c)
                            .setView(linearRoot)
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                OneCol newSelfCol = selfCol.clone();
                                List<OneKey> newKeyList = recyclerView.getAdapter().getCurrentList();
                                newSelfCol.setmAllKeys(newKeyList.toArray(new OneKey[0]));
                                v.setTag(newSelfCol);
                                List<OneCol> newList = getCurrentList();
                                int i=getIndexOfItem(newList,newSelfCol);//找到当前btn对应col所在位置
                                newList.remove(i);
                                newList.add(i,newSelfCol);
                                submitList(newList);
                            })
                            .setNegativeButton(android.R.string.cancel,null)
                            .create().show();
                    return true;
                });
                menu.add(getS(RR.cmCtrl_s2_popDel)).setOnMenuItemClickListener(item -> {
                    OneCol selfCol = (OneCol) v.getTag();
                    List<OneCol> newList = getCurrentList();
                    newList.remove(getIndexOfItem(newList,selfCol));
                    submitList(newList);
                    return true;
                });
                popupMenu.show();
            }
        });
    }


    public List<OneCol> getCurrentList() {
        List<OneCol> list = new ArrayList<>();
        int length = getItemCount();
        for (int i = 0; i < length; i++)
            list.add(getItem(i).clone());
        return list;
    }

    public int getIndexOfItem(List<OneCol> newList, OneCol item){
        int i=0;//找到当前btn对应col所在位置
        for(; i<newList.size();i++)
            if(newList.get(i).getId()== item.getId())
                break;
        return i;
    }

    /**
     * 每次更新数据时，顺带实时更新KeyCodes2
     * @param list
     */
    @Override
    public void submitList(@Nullable List<OneCol> list) {
        super.submitList(list);
        if(mIsLeft)
            mKeyCodes2.setLeftSide(list);
        else
            mKeyCodes2.setRightSide(list);
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
    }

}
