package com.example.datainsert.exagear.mutiWine.v2;

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
import com.example.datainsert.exagear.mutiWine.Config;

import java.io.File;
import java.util.Locale;

public class KronBuildAdapter extends RecyclerView.Adapter<KronBuildAdapter.ViewHolder> {

    private static final int STATE_READY = 0;
    private static final int STATE_REFRESH = 1;
    private static final int STATE_ERROR = 2;
    KronPackagesParser mParser;
    private String TAG = "KronBuildAdapter";
    /**
     * 用于标识当前状态。0是显示版本信息。1是显示刷新中，2是显示错误信息
     */
    private int mState = 1;


    public KronBuildAdapter() {

        mParser = new KronPackagesParser(wentWrong -> {
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
            viewHolder.mainTv.setText(mParser.infoList.get(0).name);
        }
        //正常显示版本信息
        else {
            WineKron4ekInfo info = mParser.infoList.get(position);
            viewHolder.mainTv.setText(info.name);
            viewHolder.menuBtn.setImageResource(QH.rslvID(R.drawable.ic_file_download_24dp,0x7f0800a1));
            viewHolder.menuBtn.setOnClickListener((v) -> {
                //去掉首尾空格，中间空格变为横杠，字母小写，结尾加上.tar.xz
                String filename = info.name.trim().replace(" ", "-").toLowerCase(Locale.ROOT) + "-x86.tar.xz";
                //检查服务器是否存在tag名-x86的文件
                final WineKron4ekInfo.Asset asset = info.getAssetByName(filename);
                if (asset == null) {
                    AndroidHelpers.toast("该版本没有合适的wine程序，操作取消");
                    Log.e(TAG, "onMenuItemClick: 标签为" + info.name + "的release没有该文件：" + filename);
                    return;
                }
                onDownload(info);


            });
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

    private void onDownload(WineKron4ekInfo info) {
        String filename = info.name.trim().replace(" ", "-").toLowerCase(Locale.ROOT) + "-x86.tar.xz";

        //如果本地存在 跳过下载(需要下载sha256sums.txt比较吧，先略过了）
        File wineTarFile = new File(Config.getTagFolder(info.name),  filename);
        if (wineTarFile.exists()){
            boolean result = GithubDownload.checkWineTarSum(wineTarFile);
            if(result){
                AndroidHelpers.toast("本地文件已存在，跳过下载");
                return;
            }else{
                //如果本地压缩包不完整，删了重下
                wineTarFile.delete();
            }
        }

        //检查是否能连接github
        //新建线程开始下载
        WineKron4ekInfo.Asset tarAsset = info.getAssetByName(filename);
        if(tarAsset==null ){
            AndroidHelpers.toast("未找到合适的下载链接");
            return;
        }
        new Thread(()->{
            //下载sha256.txt
            if( info.getAssetByName("sha256sums.txt")!=null)
                GithubDownload.download(new File(wineTarFile.getParentFile(),"sha256sums.txt"),info.getAssetByName("sha256sums.txt").browser_download_url);
            //下载压缩包
            GithubDownload.download(wineTarFile,tarAsset.browser_download_url);
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
            subTv = (TextView) linear.getChildAt(1);
            menuBtn = (ImageButton) ((LinearLayout) itemView).getChildAt(2);
        }
    }
}
