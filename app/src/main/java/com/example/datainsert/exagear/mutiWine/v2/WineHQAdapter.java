package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.mutiWine.v2.WineHQAdapter.Branch.STABLE;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.eltechs.axs.helpers.UiThread;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.QH;


class WineHQAdapter extends RecyclerView.Adapter<WineHQAdapter.ViewHolder> {
    private static final String TAG ="OfficialBuildAdapter";

    /**
     * 记录当前读取到的Package的行数
     */

    private Branch currentBranch = STABLE;
    private final WineHQParser mParser;
    public WineHQAdapter() {
        mParser = new WineHQParser(new WineHQParser.Callback() {
            @Override
            public void ready() {
                UiThread.post(()->{
                    WineHQAdapter.this.
                    notifyItemRangeInserted(0,0);
                });
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
//        Context c = viewGroup.getContext()  ;
//        LinearLayout rootView = new LinearLayout(c);
//        rootView.setOrientation(LinearLayout.HORIZONTAL);
//        TextView textView = new TextView(c);
//        rootView.addView(textView);
//        Button btn = new Button(c);
//        btn.setBackgroundResource(RSIDHelper.rslvID(R.drawable.ic_more_vert_24dp,0x7f0800a9));
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2,-2  );
//        params.gravity = Gravity.END;
//        btn.setLayoutParams(params);
//
//        rootView.addView(btn);
//        return new ViewHolder(rootView);

        //ex有个线程的布局，就用这个吧
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(QH.rslvID(R.layout.ex_basic_list_item_with_button,0x7f0b001f), viewGroup, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: ");

        WineHQWineInfo info = mParser.getListByBranch(currentBranch).get(position);
        viewHolder.mainTv.setText(info.Version.split("~")[0]);
        viewHolder.menuBtn.setOnClickListener((v)->{
            PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
            popupMenu.getMenu().add("下载");
            popupMenu.getMenu().add("解压");
            popupMenu.getMenu().add("删除");
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return mParser.getListByBranch(currentBranch).size();
    }


    public void setBranch(Branch branch){
        currentBranch = branch;
        notifyDataSetChanged();
    }

    enum Branch {
        STABLE,
        DEVEL,
        STAGING;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mainTv;
        TextView subTv;
        ImageButton menuBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            LinearLayout linear = (LinearLayout) ((LinearLayout)itemView).getChildAt(1);
            mainTv = (TextView) linear.getChildAt(0);
            subTv = (TextView) linear.getChildAt(1);
            menuBtn  = (ImageButton) ((LinearLayout)itemView).getChildAt(2);
        }
    }
}
