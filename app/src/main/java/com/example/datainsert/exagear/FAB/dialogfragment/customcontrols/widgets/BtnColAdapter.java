package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import static android.view.Display.DEFAULT_DISPLAY;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getTextViewWithText;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.AvailableKeysView.codes;
import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.drawable.RippleDrawable;
import android.hardware.display.DisplayManager;
import android.os.Build;
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
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.OneCol;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BtnColAdapter extends ListAdapter<OneCol, BtnColAdapter.ViewHolder> {
    private final static String TAG= "BtnColAdapter";
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
            //??????areItemsTheSame??????true????????????????????????
            if (oldUser.getAllKeys().length != newUser.getAllKeys().length)
                return false;
            for (int i = 0; i < oldUser.getAllKeys().length; i++) {
                if (oldUser.getAllKeys()[i].getCode() != newUser.getAllKeys()[i].getCode())
                    return false;
            }
            return true;
        }
    };
    public static final int TYPE_ADD_BTN = 2;
    public static final int TYPE_NORMAL_COL = 1;
    final KeyCodes2 mKeyCodes2;
    final boolean mIsLeft;

    public BtnColAdapter(KeyCodes2 keyCodes2, boolean isLeft) {
        super(DIFF_CALLBACK);
        mKeyCodes2 = keyCodes2;
        mIsLeft = isLeft;

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

        //??????btn??????OneCol?????????tag???
        viewHolder.getmBtn().setText(String.valueOf(getItem(position).getId() + 1));
        viewHolder.getmBtn().setTag(getItem(position));
        viewHolder.getmBtn().setOnClickListener(v -> {
            Context c = v.getContext();
            PopupMenu popupMenu = new PopupMenu(c, v);
            Menu menu = popupMenu.getMenu();
            menu.add(getS(RR.cmCtrl_s2_popEdit)).setOnMenuItemClickListener(item -> {
                showEditDialog(viewHolder);
                return true;
            });
            menu.add(getS(RR.cmCtrl_s2_popDel)).setOnMenuItemClickListener(item -> {
                OneCol selfCol = (OneCol) v.getTag();
                List<OneCol> newList = getCurrentList();
                newList.remove(getIndexOfItem(newList, selfCol));
                submitList(newList);
                return true;
            });
            popupMenu.show();
        });
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void showEditDialog(ViewHolder viewHolder) {
        Context c = viewHolder.getmBtn().getContext();
        //?????????????????????????????????selfCol?????????KeyAdapter?????????key submit??????????????????????????????selfCol
        OneCol selfCol = (OneCol) viewHolder.getmBtn().getTag();
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setPadding(QH.px(c, RR.attr.dialogPaddingDp), QH.px(c, RR.attr.dialogPaddingDp), QH.px(c, RR.attr.dialogPaddingDp), QH.px(c, RR.attr.dialogPaddingDp));
        linearRoot.setOrientation(LinearLayout.VERTICAL);

        //???????????????????????????????????????
        DisplayManager mDisplayManager = (DisplayManager) c.getSystemService(Context.DISPLAY_SERVICE);
        Rect rect = new Rect();
        mDisplayManager.getDisplay(DEFAULT_DISPLAY).getRectSize(rect);
        boolean isLandScape = (rect.right - rect.left) > (rect.bottom - rect.top);
        //????????????
        BtnKeyRecyclerView btnKeyRecyclerView = new BtnKeyRecyclerView(c, selfCol,isLandScape);
        //????????????
        Button selectBtn = new Button(c);
        selectBtn.setText(getS(RR.cmCtrl_s2_selectBtn));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            selectBtn.setTextAppearance(android.R.style.TextAppearance_Material_Widget_Button_Borderless_Colored);
            selectBtn.setBackground(new RippleDrawable(ColorStateList.valueOf(0x44444444), null, selectBtn.getBackground()));
        }
        selectBtn.setOnClickListener(clickedSelectBtn -> {
            boolean[] preSelected = new boolean[codes.length];
            for (OneKey oneKey : btnKeyRecyclerView.getAdapter().getCurrentList())
                for (int i = 0; i < codes.length; i++)
                    if (oneKey.getCode() == codes[i])
                        preSelected[i] = true; //??????oneKey???code?????????????????????????????????????????????????????????????????????boolean?????????true

            //???????????????????????????????????????fragment????????????recyclerview??????submitList
            AvailableKeysView dialogView = new AvailableKeysView(clickedSelectBtn.getContext(), preSelected, -1);
            dialogView.showMouseBtn();
            dialogView.showWithinDialog((dialog, which) -> {
                OneKey[] newKeys = dialogView.getSelectedKeys();
                //????????????key?????????
                for(OneKey newKey:newKeys){
                    for(OneKey oldKey:selfCol.getAllKeys())
                        if(newKey.getCode()==oldKey.getCode()){
                            newKey.setName(oldKey.getName());
                            break;
                        }
                }
                //submit??????????????????????????????????????????
                btnKeyRecyclerView.getAdapter().submitList(Arrays.asList(newKeys));
            });
        });
        LinearLayout.LayoutParams btnViewParams = new LinearLayout.LayoutParams(-2, -2);
        btnViewParams.gravity = Gravity.CENTER_HORIZONTAL;
        linearRoot.addView(selectBtn, btnViewParams);
        //??????
        linearRoot.addView(getTextViewWithText(c, getS(RR.cmCtrl_s2_ColEditTip)));
        //???????????????????????????????????? ????????????????????????????????????
        linearRoot.addView(btnKeyRecyclerView);
        new AlertDialog.Builder(c)
                .setView(linearRoot)
                //?????????????????????????????????submitList????????????model????????????keycodes2
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    OneCol newSelfCol = selfCol.clone();
//                    //?????????KeyAdapter???????????????OneKey??????
//                    List<OneKey> newKeyList = btnKeyRecyclerView.getAdapter().getCurrentList();
//                    newSelfCol.setmAllKeys(newKeyList.toArray(new OneKey[0]));
                    viewHolder.getmBtn().setTag(newSelfCol);
                    List<OneCol> newList = getCurrentList();
                    //OneCol?????????????????????????????????, ?????????getAdapterPosition?????????
                    newList.remove(viewHolder.getAdapterPosition());
                    newList.add(viewHolder.getAdapterPosition(), newSelfCol);
                    submitList(newList);
                })
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }


    public List<OneCol> getCurrentList() {
        List<OneCol> list = new ArrayList<>();
        int length = getItemCount();
        for (int i = 0; i < length; i++)
            list.add(getItem(i).clone());
        return list;
    }

    public int getIndexOfItem(List<OneCol> newList, OneCol item) {
        int i = 0;//????????????btn??????col????????????
        for (; i < newList.size(); i++)
            if (newList.get(i).getId() == item.getId())
                break;
        return i;
    }

    /**
     * ??????????????????????????????????????????KeyCodes2
     *
     * @param list
     */
    @Override
    public void submitList(@Nullable List<OneCol> list) {
        super.submitList(list);
        if (mIsLeft)
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
