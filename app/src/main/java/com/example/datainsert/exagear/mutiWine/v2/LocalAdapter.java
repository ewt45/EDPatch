package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.RR.getS;

import android.app.AlertDialog;
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
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.mutiWine.WineNameComparator;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用于显示本地已存在的wine的回收视图适配器
 */
public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.ViewHolder> {
    private static final String TAG = "LocalWineAdapter";
    private final List<TagWithConfig> mInfoList = new ArrayList<>();//记录tag名和对应config类型。对应文件操作交给对应的config处理
    private final ConfigAbstract[] configArr = new ConfigAbstract[]{KronConfig.i, HQConfig.i, CustomConfig.i};
    ExecutorService executor = Executors.newFixedThreadPool(5); //用于计算文件夹体积的另一线程

    public LocalAdapter() {
        refresh(true);
    }

    @NonNull
    @Override
    public LocalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(QH.rslvID(R.layout.ex_basic_list_item_with_button, 0x7f0b001f), viewGroup, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalAdapter.ViewHolder viewHolder, int position) {
        TagWithConfig tagWithConfig = mInfoList.get(position);
        String tagName = tagWithConfig.tagName;
        ConfigAbstract config = tagWithConfig.config;
        String customName = config.getInfoTxtByTag(tagName).get("name");//如果有info.txt，读取其内容
        viewHolder.mainTv.setText(customName == null ? tagName : customName);
        setSubText(tagWithConfig, viewHolder.subTv);
//        viewHolder.subTv.setText(getSubText(new File(Config.getTagParentFolder(), tagName)));
        viewHolder.menuBtn.setOnClickListener(v -> {
            String extractStr = getS(RR.mw_localMenuItem).split("\\$")[0];
            String checkStr = getS(RR.mw_localMenuItem).split("\\$")[1];
            String delDirStr = getS(RR.mw_localMenuItem).split("\\$")[2];
            String delArcStr = getS(RR.mw_localMenuItem).split("\\$")[3];

            File extractedFolder = config.getWineFolderByTag(tagName);
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.getMenu().add(extractStr).setEnabled(extractedFolder == null).setOnMenuItemClickListener(item -> {
                //解压时显示进度对话框
                MyProgressDialog dialog = new MyProgressDialog().init(getS(RR.mw_dialog_extract).split("\\$")[0], true);
                dialog.show();
                onExtract(tagWithConfig, message -> {
                    dialog.done(message);
                    notifyItemChanged(position);
                });
                return true;
            });

            //校验功能，预置wine暂不支持
            popupMenu.getMenu().add(checkStr).setEnabled(!(tagWithConfig.config instanceof CustomConfig)).setOnMenuItemClickListener(item -> {
                onChecksum(tagWithConfig);
                return true;
            });

            //如果存在解压后文件夹，显示删除文件夹选项，否则显示删除压缩包选项
            if (extractedFolder != null) {
                popupMenu.getMenu().add(delDirStr).setOnMenuItemClickListener(item -> {
                    onDeleteExtractedFolder(tagWithConfig);
                    notifyItemChanged(position);
                    return true;
                });
            } else {
                //属于customConfig（custom文件夹里）的，禁止删除
//                boolean canDeleteArchive = "true".equals(config.getInfoTxtByTag(tagName).get("noDeleteArchive"));
                Log.d(TAG, "onBindViewHolder: config类型为："+tagWithConfig.config);
                popupMenu.getMenu().add(delArcStr).setEnabled(!(tagWithConfig.config instanceof CustomConfig)).setOnMenuItemClickListener(item -> {
                    onDeleteTar(tagWithConfig);
                    mInfoList.remove(position);//删除之后该项不再显示
                    notifyDataSetChanged();//用removed 最后一个item会有问题
                    return true;
                });
            }

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

    /**
     * 刷新界面。重新读取本地wine压缩包
     *
     * @param first 是否是初次读取。如果不是的话就要notify通知变化
     */
    public void refresh(boolean first) {
        mInfoList.clear();
        //寻找已下载的压缩包
        for (ConfigAbstract config : configArr) {
            File parent = config.getHostFolder();
            for (File child : parent.listFiles()) {
                if (!child.isDirectory())
                    continue;
                //拿到该tag文件夹中的压缩包，如果长度不为0则说明可以显示出来
                List<File> archives = config.getLocalArchivesByTag(child.getName());
                if (archives.size() > 0)
                    mInfoList.add(new TagWithConfig(child.getName(), config));
            }
        }

        //按文件名排序
        Collections.sort(mInfoList, new WineNameComparator());
        if (!first) {
            notifyDataSetChanged();
        }
    }

    private void onDeleteExtractedFolder(TagWithConfig tagWithConfig) {
        try {
            File wineDir = tagWithConfig.config.getWineFolderByTag(tagWithConfig.tagName);
            if (wineDir != null)
                FileUtils.deleteDirectory(wineDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onDeleteTar(TagWithConfig tagWithConfig) {
        for (File file : tagWithConfig.config.getLocalArchivesByTag(tagWithConfig.tagName)) {
            if (file != null)
                file.delete();
        }
    }

    private void onExtract(TagWithConfig tagWithConfig, MyProgressDialog.Callback callback) {
        new Thread(() -> {
            String extractSucceed = getS(RR.mw_dialog_extract).split("\\$")[1];
            String extractFailed = getS(RR.mw_dialog_extract).split("\\$")[2];
            String finishMsg = extractSucceed;

            try {
                tagWithConfig.config.unpackArchive(tagWithConfig.tagName);
            } catch (IOException e) {
                e.printStackTrace();
                finishMsg = extractFailed;
            } finally {
                String finalFinishMsg = finishMsg;
                UiThread.post(() -> callback.onFinish(finalFinishMsg));
            }

        }).start();
    }

    private void onChecksum(TagWithConfig tagCfg) {
        String doneMsg = getS(RR.mw_dialog_checksum).split("\\$")[0]; //压缩包校验结束，没有发现问题
        try {
            tagCfg.config.checkSha256(tagCfg.tagName);
        } catch (Exception e) {
            doneMsg = e.getMessage();
        }
        new AlertDialog
                .Builder(QH.getCurrentActivity())
                .setMessage(doneMsg)
                .setPositiveButton(android.R.string.yes, null)
                .show();

    }

    /**
     * 获取该wine版本的压缩包体积和解压后文件夹体积，用于显示在副文本区域
     * 由于计算文件夹大小比较耗时，所以使用另外线程执行
     * <p/>
     * 原：压缩包: 23.34 MB  |  解压后: 286.54 MB
     * <p/>
     * 结果太长装不下，改成： 304.43 MB | 已启用/未启用
     */
    private void setSubText(TagWithConfig tagWithConfig, TextView textView) {

        executor.submit(() -> {
            String tagName = tagWithConfig.tagName;
            ConfigAbstract config = tagWithConfig.config;
            float tarSize = 0;
            for (File file : config.getLocalArchivesByTag(tagName))
                tarSize += file.length();
            File extractDir = config.getWineFolderByTag(tagName);
            float dirSize = extractDir != null ? FileUtils.sizeOfDirectory(extractDir) : 0f;
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String activeStr = getS(RR.mw_localState).split("\\$")[0];//已启用
            String inactiveStr = getS(RR.mw_localState).split("\\$")[1];//未启用

            String s = decimalFormat.format((tarSize + dirSize) / 1024f / 1024) + getS(RR.mw_dataSizeMB) +
                    "  |  " +
                    (dirSize == 0 ? inactiveStr : activeStr);
//                    archiveStr + decimalFormat.format(tarSize) + getS(RR.mw_dataSizeMB) + "  |  "+
//                    folderStr + (dirSize == 0 ? nofoldereStr : (decimalFormat.format(dirSize)) + getS(RR.mw_dataSizeMB));
            if (textView != null)
                textView.setText(s);
//            Log.d(TAG, "setSubText: " + tagFolder.getName() + " " + s);
        });
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        executor.shutdown();
        Log.d(TAG, "onDetachedFromRecyclerView: 线程池关闭");//貌似这个函数从来没有被执行过？？
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
            mainTv.setMaxLines(2);
            subTv = (TextView) linear.getChildAt(1);
            subTv.setMaxLines(2);
            menuBtn = (ImageButton) ((LinearLayout) itemView).getChildAt(2);
        }
    }

    private static class TagWithConfig {
        String tagName;
        ConfigAbstract config;

        public TagWithConfig(String s, ConfigAbstract c) {
            tagName = s;
            config = c;
        }
    }
}
