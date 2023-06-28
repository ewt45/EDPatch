package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.RR.getS;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.mutiWine.KronConfig;

import java.io.File;

public class KronAdapter extends RecyclerView.Adapter<KronAdapter.ViewHolder> {

    private static final int STATE_READY = 0;
    private static final int STATE_REFRESH = 1;
    private static final int STATE_ERROR = 2;
    KronParser mParser;
    private String TAG = "KronBuildAdapter";
    /**
     * 用于标识当前状态。0是显示版本信息。1是显示刷新中，2是显示错误信息
     */
    private int mState = 1;


    public KronAdapter() {

        mParser = new KronParser(wentWrong -> {
            synchronized (this) {
                mState = wentWrong ? STATE_ERROR : STATE_READY;
            }
            UiThread.post(() -> notifyDataSetChanged());
        });

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
        //如果是正在刷新
        if (mState == STATE_REFRESH) {
            viewHolder.menuBtn.setVisibility(View.GONE);
            LinearLayout mainLinear = (LinearLayout) viewHolder.mainTv.getParent();
            for (int i = 0; i < mainLinear.getChildCount(); i++)
                mainLinear.getChildAt(i).setVisibility(View.GONE);//直接移除貌似有问题

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.gravity = Gravity.CENTER;
            mainLinear.addView(new ProgressBar(mainLinear.getContext()), params);
        }
        // 如果无法正常显示版本信息
        else if (mState == STATE_ERROR) {
            viewHolder.menuBtn.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = viewHolder.rootView.getLayoutParams();
            if(params==null)
                params = new ViewGroup.LayoutParams(-1,AndroidHelpers.dpToPx(200));
            else
                params.height = AndroidHelpers.dpToPx(200);
            viewHolder.rootView.setLayoutParams(params);
            viewHolder.mainTv.setText(mParser.infoList.get(0).name);
        }
        //正常显示版本信息
        else {
            KronWineInfo info = mParser.infoList.get(position);
            //去掉首尾空格，中间空格变为横杠，字母小写，结尾加上.tar.xz
            String filename = KronConfig.i.formatTagName(info.name) + "-x86.tar.xz";
            //检查服务器是否存在tag名-x86的文件
            final KronWineInfo.Asset asset = info.getAssetByName(filename);

            viewHolder.mainTv.setText(info.name.trim());

            if(asset!=null){
                viewHolder.subTv.setText(asset.getSizeInMB()+getS(RR.mw_dataSizeMB)+"  |  "+asset.updated_at.substring(0,10));
                viewHolder.menuBtn.setImageResource(QH.rslvID(R.drawable.ic_file_download_24dp, 0x7f0800a1));
                viewHolder.menuBtn.setOnClickListener((v) -> onDownload(info));
            }else{
                viewHolder.menuBtn.setImageResource(0);
                viewHolder.menuBtn.setEnabled(false);
//                AndroidHelpers.toast("该版本没有合适的wine程序，操作取消");
                Log.e(TAG, "onMenuItemClick: 标签为" + info.name + "的release没有该文件：" + filename);

            }

        }

    }

    @Override
    public int getItemCount() {
        return mParser.infoList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        Log.d(TAG, "getItemViewType: " + mState);
        return mState;
    }

    private void onDownload(KronWineInfo info) {
        String filename = KronConfig.i.formatTagName(info.name) + "-x86.tar.xz";

        //是否存在符合命名规则的下载文件，以及是否有其他文件正在下载
        KronWineInfo.Asset tarAsset = info.getAssetByName(filename);
        assert tarAsset!=null; //这个在外部 bind 的时候判断过了，能进入这个函数的都不是null

        MyProgressDialog dialog = new MyProgressDialog().init("", false);
        dialog.show();

        String skipStr = getS(RR.mw_dialog_download).split("\\$")[2];
        String cancelStr = getS(RR.mw_dialog_download).split("\\$")[3];

        //如果本地存在 跳过下载(需要下载sha256sums.txt比较吧，先略过了）
        File wineTarFile = new File(KronConfig.i.getTagFolder(info.name), filename);
        if (wineTarFile.exists()) {
            if (DownloadFileHelper.checkWineTarSum(wineTarFile)) {
                dialog.done(skipStr);
                return;
            } else {
                //如果本地压缩包不完整，删了重下
                wineTarFile.delete();
            }
        }
        //是否有其他文件正在下载
        if (DownloadFileHelper.isDownloading) {
            dialog.fail(cancelStr);
            return;
        }

        //新建线程开始下载
        new Thread(() -> {
            //下载sha256.txt
            KronWineInfo.Asset shaAsset = info.getAssetByName("sha256sums.txt");
            if (shaAsset != null) {
                UiThread.post(() -> dialog.setMessage("sha256sums.txt"));//要在主线程？
                DownloadFileHelper.downloadReleaseAsset(info.name, shaAsset, dialog.defaultCallback);

            }

            //下载压缩包
            UiThread.post(() -> {
                dialog.init(wineTarFile.getName(), false);
                dialog.setMax(tarAsset.size);
            });
            DownloadFileHelper.downloadReleaseAsset(info.name, tarAsset, dialog.defaultCallback);
        }).start();
    }

    /**
     * 外部调用，刷新数据，重新下载
     */
    public void refresh() {
        synchronized (this) {
            mState = STATE_REFRESH;
            mParser.infoList.clear();
//            mParser.infoList.add(new WineKron4ekInfo());
        }
        notifyDataSetChanged();
        mParser.refresh(true);
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
