package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.mutiWine.v2.DownloadParser.PARSER_KRON4EK;
import static com.example.datainsert.exagear.mutiWine.v2.DownloadParser.PARSER_PREF_KEY;
import static com.example.datainsert.exagear.mutiWine.v2.DownloadParser.PARSER_WINEHQ;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.UiThread;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.QH;
//import com.example.datainsert.exagear.mutiWine.v2.KronConfig;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {

    /**
     * 仅通过parser获取对应的config，进行对应操作
     */
    private DownloadParser mParser;
    private final DownloadParser[] parserArr; //0是kron, 1是winehq
    private String TAG = "KronBuildAdapter";
    /**
     * 用于标识当前状态。0是显示版本信息。1是显示刷新中，2是显示错误信息
     */


    public DownloadAdapter() {
        DownloadParser.Callback callback = errMsg -> {

            UiThread.post(() -> {
                //有错误的话直接对话框显示了
                if (errMsg != null)
                    new AlertDialog.Builder(QH.getCurrentActivity())
                            .setMessage(errMsg)
                            .setPositiveButton(android.R.string.yes, null)
                            .show();
                notifyDataSetChanged();
            });
        };
        //先初始化俩parser吧
        parserArr = new DownloadParser[]{new KronParser(callback), new HQParser(callback)};
        //根据选项选择对应的parser
        //构造函数中就调用这个的话，会产生context null报错。因为会新建dialog，而此时通过Global获取的acitivity为null，因为这时是onStart，onResume的时候才设置activity
        prepareParser(QH.getPreference().getInt(PARSER_PREF_KEY,PARSER_KRON4EK));
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(QH.rslvID(R.layout.ex_basic_list_item_with_button, 0x7f0b001f), viewGroup, false);
        return new ViewHolder(rootView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
//        //如果是正在刷新
//        if (mState == STATE_REFRESH) {
//            viewHolder.menuBtn.setVisibility(View.GONE);
//            LinearLayout mainLinear = (LinearLayout) viewHolder.mainTv.getParent();
//            for (int i = 0; i < mainLinear.getChildCount(); i++)
//                mainLinear.getChildAt(i).setVisibility(View.GONE);//直接移除貌似有问题
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
//            params.gravity = Gravity.CENTER;
//            mainLinear.addView(new ProgressBar(mainLinear.getContext()), params);
//        }
        //正常显示版本信息
        WineInfo info = mParser.infoList().get(position);
        String tagName = info.getTagName();
        //检查服务器是否存在tag名-x86的文件 (这个放到parser里 获取infoList的时候检查吧，没有的直接删掉）
//            final KronWineInfo.Asset asset = info.getAssetByName( mParser.config().formatTagName(tagName) + "-x86.tar.xz");

        viewHolder.mainTv.setText(tagName.trim());
        viewHolder.subTv.setText(info.getDescription());
        viewHolder.menuBtn.setImageResource(QH.rslvID(R.drawable.ic_file_download_24dp, 0x7f0800a1));
        viewHolder.menuBtn.setOnClickListener((v) -> mParser.downloadWine(info));
//            if (!isDownloaded(tagName)) {
//
//            } else {
//                viewHolder.menuBtn.setImageResource(0);
//                viewHolder.menuBtn.setEnabled(false);
////                AndroidHelpers.toast("该版本没有合适的wine程序，操作取消");
////                Log.e(TAG, "onMenuItemClick: 标签为" + tagName + "的release没有该文件：" + filename);
//
//            }

    }

    @Override
    public int getItemCount() {
        return mParser.infoList().size();
    }



    /**
     * 外部调用，刷新数据，重新下载release信息并读取
     */
    public void refresh() {
        mParser.infoList().clear();
        notifyDataSetChanged();
        mParser.syncRelease(true);
    }

    /**
     * 根据设置 使用对应的parser，并让parser读取infoList
     * @param parserType PARSER_KRON4EK或PARSER_WINEHQ
     */
    public void prepareParser(int parserType){
        switch (parserType){
            case PARSER_KRON4EK:
                mParser = parserArr[0];
                break;
            case PARSER_WINEHQ:
                mParser = parserArr[1];
                break;
            default:
                throw new RuntimeException("无法找到合适的parser");
        }

        mParser.infoList().clear();
        notifyDataSetChanged();
        mParser.syncRelease(false);
    }


    private boolean isDownloaded(String tagName) {

        boolean downloaded = true;
        try {
            mParser.config().checkSha256(tagName);
        } catch (Exception e) {
            downloaded = false;
        }
        return downloaded;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewGroup rootView;

        TextView mainTv;
        TextView subTv;
        ImageButton menuBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = (ViewGroup) itemView;
            //图标隐藏了吧，应该用不上
            ((ViewGroup) itemView).getChildAt(0).setVisibility(View.GONE);
            LinearLayout linear = (LinearLayout) ((LinearLayout) itemView).getChildAt(1);
            mainTv = (TextView) linear.getChildAt(0);
            mainTv.setMaxLines(10);
            subTv = (TextView) linear.getChildAt(1);
            subTv.setMaxLines(10);
            menuBtn = (ImageButton) ((LinearLayout) itemView).getChildAt(2);
        }
    }
}
