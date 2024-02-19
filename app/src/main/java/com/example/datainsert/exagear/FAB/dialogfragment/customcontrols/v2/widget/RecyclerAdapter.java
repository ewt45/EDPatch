package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.util.List;

public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerAdapter.RegularViewHolder> {
    private List<T> dataList;
    public RecyclerAdapter(List<T> dataList){
        this.dataList = dataList;
    }


    /**
     * 设置新的数据列表，并notifyDataSetChange
     */
    public void setDataList(List<T> dataList) {
        setDataList(dataList,true);
    }

    /**
     * 设置新的数据列表，并设置是否notifyDataSetChange
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setDataList(List<T> dataList, boolean notify){
        this.dataList = dataList;
        if(notify)
            notifyDataSetChanged();
    }

    /**
     * 获取adapter对应的数据列表
     */
    public List<T> getDataList() {
        return dataList;
    }

    @NonNull
    @Override
    public RegularViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new RegularViewHolder(viewGroup.getContext());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    abstract public void onBindViewHolder(@NonNull RegularViewHolder holder, int position) ;

    public static class RegularViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout root;
        public final ImageView imageView;
        public final TextView text1;
        public final TextView text2;
        public final ImageButton btnMenu;
        public final LinearLayout linearPart2;
        public RegularViewHolder(Context c) {
            super(new LinearLayout(c));

            root = (LinearLayout) this.itemView;
            root.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(dp8*6, dp8*6);
            checkParams.gravity = Gravity.CENTER;
            LinearLayout.LayoutParams linearTextsParams = new LinearLayout.LayoutParams(0, -2);
            linearTextsParams.gravity = Gravity.CENTER_VERTICAL;
            linearTextsParams.weight = 1;
            linearTextsParams.topMargin = dp8;
            linearTextsParams.bottomMargin = dp8;

            //勾选框
            imageView = new ImageView(c);
            imageView.setId(android.R.id.checkbox);
    //        imageView.setImageDrawable(c.getDrawable(R.drawable.aaa_check));
            imageView.setPadding(dp8 / 2, dp8 / 2, dp8 / 2, dp8 / 2);

    //        CheckBox imageView = new CheckBox(c);
    //        imageView.setId(android.R.id.checkbox);
    //        RelativeLayout relativeCheck = new RelativeLayout(c);
    //        relativeCheck.setGravity(Gravity.CENTER);
    //        relativeCheck.addView(imageView, new ViewGroup.LayoutParams(-2, -2));
    //        relativeCheck.setOnClickListener(v -> imageView.performClick());

            //两行文本
            text1 = new TextView(c);//(TextView) LayoutInflater.from(c).inflate(android.R.layout.simple_list_item_1, root, false);
            text1.setTextColor(RR.attr.textColorPrimary(c));
            text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            text1.setId(android.R.id.text1);

            text2 = new TextView(c);
            text2.setId(android.R.id.text2);
            text2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            LinearLayout linearTexts = new LinearLayout(c);
            linearTexts.setOrientation(LinearLayout.VERTICAL);
            linearTexts.addView(text1);
            linearTexts.addView(text2);

            //菜单按钮
            btnMenu = new ImageButton(c);
            btnMenu.setId(android.R.id.button1);
            btnMenu.setImageResource(RR.drawable.ic_more_vert_24dp());
            btnMenu.setBackground(RR.attr.selectableItemBackground(c));

            LinearLayout linearPart1 = new LinearLayout(c);
            linearPart1.setOrientation(LinearLayout.HORIZONTAL);
            linearPart1.addView(imageView, QH.LPLinear.one(dp8 * 6, dp8 * 6).left().to());
            linearPart1.addView(linearTexts, QH.LPLinear.one(0, dp8 * 6).weight().gravity(Gravity.CENTER_VERTICAL).left().to());
            linearPart1.addView(btnMenu, QH.LPLinear.one(dp8 * 6, dp8 * 6).left().to());

            linearPart2 = new LinearLayout(c);
            linearPart2.setOrientation(LinearLayout.VERTICAL);
            linearPart2.setPadding(dp8*3, 0, 0, 0);
    //        linearPart2.setScaleX(0.9f);
    //        linearPart2.setScaleY(0.9f);

            root.addView(linearPart1);
            root.addView(linearPart2);
            root.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
            root.setLayoutTransition(new LayoutTransition());
        }
    }
}
