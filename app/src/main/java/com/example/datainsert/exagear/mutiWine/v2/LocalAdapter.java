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

import com.eltechs.axs.helpers.SafeFileHelpers;
import com.eltechs.axs.helpers.UiThread;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.mutiWine.WineNameComparator;
import com.example.datainsert.exagear.mutiWine.KronConfig;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private final List<String> mList = new ArrayList<>(); //记录的是tag文件夹名
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
        String tagName = mList.get(position);
        String customName = KronConfig.i.getInfoTxtByTag(tagName).get("name");//如果有info.txt，读取其内容
        viewHolder.mainTv.setText(customName == null ? tagName : customName);
        setSubText(tagName, viewHolder.subTv);
//        viewHolder.subTv.setText(getSubText(new File(Config.getTagParentFolder(), tagName)));
        viewHolder.menuBtn.setOnClickListener(v -> {
            String extractStr = getS(RR.mw_localMenuItem).split("\\$")[0];
            String checkStr = getS(RR.mw_localMenuItem).split("\\$")[1];
            String delDirStr = getS(RR.mw_localMenuItem).split("\\$")[2];
            String delArcStr = getS(RR.mw_localMenuItem).split("\\$")[3];

            File extractedFolder = KronConfig.i.getWineFolderByTag(tagName);
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.getMenu().add(extractStr).setEnabled(extractedFolder == null).setOnMenuItemClickListener(item -> {
                //解压时显示进度对话框
                MyProgressDialog dialog = new MyProgressDialog().init(getS(RR.mw_dialog_extract).split("\\$")[0], true);
                dialog.show();
                onExtract(tagName, message -> {
                    dialog.done(message);
                    notifyItemChanged(position);
                });
                return true;
            });

            popupMenu.getMenu().add(checkStr).setOnMenuItemClickListener(item -> {
                onChecksum(tagName);
                return true;
            });

            //如果存在解压后文件夹，显示删除文件夹选项，否则显示删除压缩包选项
            if (extractedFolder != null) {
                popupMenu.getMenu().add(delDirStr).setOnMenuItemClickListener(item -> {
                    onDeleteExtractedFolder(tagName);
                    notifyItemChanged(position);
                    return true;
                });
            } else {
                boolean canDeleteArchive = "true".equals(KronConfig.i.getInfoTxtByTag(tagName).get("noDeleteArchive"));
                popupMenu.getMenu().add(delArcStr).setEnabled(!canDeleteArchive).setOnMenuItemClickListener(item -> {
                    onDeleteTar(tagName);
                    mList.remove(position);//删除之后该项不再显示
                    notifyDataSetChanged();//用removed 最后一个item会有问题
                    return true;
                });
            }

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 刷新界面。重新读取本地wine压缩包
     *
     * @param first 是否是初次读取。如果不是的话就要notify通知变化
     */
    public void refresh(boolean first) {
        mList.clear();
        //寻找已下载的压缩包
        File parent = KronConfig.i.getHostFolder();
        for (File child : parent.listFiles()) {
            if (!child.isDirectory())
                continue;
            String[] wineTars = child.list((dir, name) -> name.endsWith(".tar.xz"));
            if (wineTars.length > 0)
                mList.add(child.getName());
//            if(child.isFile() && child.getAbsolutePath().endsWith(".tar.xz")){
//                mList.add(child.getName());//.substring(0,child.getName().lastIndexOf(".tar.xz")).replace("-x86","")
//            }
        }

        //按文件名排序
        Collections.sort(mList, new WineNameComparator());
        if (!first) {
            notifyDataSetChanged();
        }
    }

    private void onDeleteExtractedFolder(String tagName) {
        try {
            File wineDir = KronConfig.i.getWineFolderByTag(tagName);
            if (wineDir != null)
                FileUtils.deleteDirectory(wineDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onDeleteTar(String tagName) {
        File tarFile = KronConfig.i.getArchiveByTag(tagName);
        if (tarFile != null)
            tarFile.delete();
    }

    private void onExtract(String tagName, MyProgressDialog.Callback callback) {
        File tagFolder = KronConfig.i.getTagFolder(tagName);
        File wineTarFile = KronConfig.i.getArchiveByTag(tagName);

        new Thread(() -> {
            String extractSucceed = getS(RR.mw_dialog_extract).split("\\$")[1];
            String extractFailed = getS(RR.mw_dialog_extract).split("\\$")[2];
            String finishMsg = extractSucceed;
            //文件->xz->tar
            try (FileInputStream fis = new FileInputStream(wineTarFile);
                 XZCompressorInputStream xzis = new XZCompressorInputStream(fis);
                 TarArchiveInputStream tis = new TarArchiveInputStream(xzis);
            ) {
                //如果已经存在解压后的文件夹（不需要新建文件夹，压缩包解压出来就是一个文件夹
                for (File child : tagFolder.listFiles())
                    if (child.isDirectory())
                        FileUtils.deleteDirectory(child);//删除重新解压

                TarArchiveEntry nextEntry;
                while ((nextEntry = tis.getNextTarEntry()) != null) {

                    String name = nextEntry.getName();
                    File file = new File(tagFolder, name);
                    //如果是目录，创建目录
                    if (nextEntry.isDirectory()) {
                        file.mkdirs();
                        continue;
                    }
                    //文件则写入具体的路径中
                    try (OutputStream os = new FileOutputStream(file);) {
                        IOUtils.copy(tis, os); // FileUtils.copyInputStreamToFile(tis, file); //不能用这个，会自动关闭输入流
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //如果是符号链接，需要手动链接(参考ZipUnpacker）
                    if (nextEntry.isSymbolicLink()) {
                        file.delete();
                        SafeFileHelpers.symlink(nextEntry.getLinkName(), file.getAbsolutePath());
                        Log.d(TAG, String.format("extract: 解压时发现符号链接：链接文件：%s，指向文件：%s", nextEntry.getName(), nextEntry.getLinkName()));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                finishMsg = extractFailed;
            } finally {
                String finalFinishMsg = finishMsg;
                UiThread.post(() -> callback.onFinish(finalFinishMsg));
            }

        }).start();
    }

    private void onChecksum(String tagName) {
        String checkFinish = getS(RR.mw_dialog_checksum).split("\\$")[0];
        String checkNoTxt = getS(RR.mw_dialog_checksum).split("\\$")[1];
        String checkCorrupt = getS(RR.mw_dialog_checksum).split("\\$")[2];

        String msg = checkFinish;
        try {
            File wineTarFile = KronConfig.i.getArchiveByTag(tagName);
            assert wineTarFile != null;

            //尝试获取txt文本中的sh256
            List<String> checksumList = KronConfig.i.getSha256(tagName);
            if (checksumList.size() == 0)
                throw new Exception(checkNoTxt);

            if (!DownloadFileHelper.checkWineTarSum(wineTarFile))
                throw new Exception(checkCorrupt);
        } catch (Exception e) {
            msg = e.getMessage();
            e.printStackTrace();
        }
        new AlertDialog
                .Builder(QH.getCurrentActivity())
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, null)
                .show();
//        AndroidHelpers.toast(msg);

    }

    /**
     * 获取该wine版本的压缩包体积和解压后文件夹体积，用于显示在副文本区域
     * 由于计算文件夹大小比较耗时，所以使用另外线程执行
     * <p/>
     * 原：压缩包: 23.34 MB  |  解压后: 286.54 MB
     * <p/>
     * 结果太长装不下，改成： 304.43 MB | 已启用/未启用
     */
    private void setSubText(String tagName, TextView textView) {
        executor.submit(() -> {
            File tarFile = KronConfig.i.getArchiveByTag(tagName);
            File extractDir = KronConfig.i.getWineFolderByTag(tagName);
            float tarSize = tarFile != null ? tarFile.length() : 0;
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
}
