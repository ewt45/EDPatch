package com.example.datainsert.exagear.mutiWine.v2;

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

import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.SafeFileHelpers;
import com.eltechs.axs.helpers.UiThread;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.mutiWine.Config;
import com.example.datainsert.exagear.mutiWine.WineNameComparator;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用于显示本地已存在的wine的回收视图适配器
 */
public class LocalWineAdapter extends RecyclerView.Adapter<LocalWineAdapter.ViewHolder> {
    private static final String TAG = "LocalWineAdapter";
    private final List<String> mList = new ArrayList<>(); //记录的是tag文件夹名

    public LocalWineAdapter() {
        refresh(true);
    }

    @NonNull
    @Override
    public LocalWineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(QH.rslvID(R.layout.ex_basic_list_item_with_button, 0x7f0b001f), viewGroup, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalWineAdapter.ViewHolder viewHolder, int position) {
        String tagName = mList.get(position);
        viewHolder.mainTv.setText(tagName);
        viewHolder.subTv.setText("压缩包：xxx.MB | 解压后：未解压");
        viewHolder.menuBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.getMenu().add("解压").setOnMenuItemClickListener(item -> {
                onExtract(tagName);
                return true;
            });
            popupMenu.getMenu().add("删除解压后文件夹").setOnMenuItemClickListener(item -> {
                onDeleteExtractedFolder(tagName);
                return true;
            });
            popupMenu.getMenu().add("删除压缩包").setOnMenuItemClickListener(item -> {
                onDeleteTar(tagName);
                return true;
            });
            popupMenu.getMenu().add("校验压缩包完整性").setOnMenuItemClickListener(item -> {
                onChecksum(tagName);
                return true;
            });
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
        File parent = Config.getTagParentFolder();
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
            File wineDir = Config.getWineFolderFromTagFolder(new File(Config.getTagParentFolder(), tagName));
            if (wineDir != null)
                FileUtils.deleteDirectory(wineDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onDeleteTar(String tagName) {
        File tarFile = Config.getTarFileFromTagFolder(new File(Config.getTagParentFolder(), tagName));
        if (tarFile != null)
            tarFile.delete();
    }

    private void onExtract(String tagName) {
        File tagFolder = new File(Config.getTagParentFolder(), tagName);
        File wineTarFile = Config.getTarFileFromTagFolder(tagFolder);
        //如果压缩包不存在
        if (wineTarFile == null) {
            Log.e(TAG, "onExtract: 压缩包未下载，无法解压");
            UiThread.post(() -> AndroidHelpers.toast("压缩包未下载，无法解压"));
            return;
        }

        new Thread(() -> {
            UiThread.post(() -> AndroidHelpers.toast("开始解压"));
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
                UiThread.post(() -> AndroidHelpers.toast("解压成功"));
            } catch (IOException e) {
                e.printStackTrace();
                UiThread.post(() -> AndroidHelpers.toast("解压失败"));

            }
        }).start();
    }

    private void onChecksum(String tagName) {
        try {
            File tagFolder = new File(Config.getTagParentFolder(), tagName);
            File wineTarFile = Config.getTarFileFromTagFolder(tagFolder);
            if (wineTarFile == null)
                throw new Exception("压缩包不存在");

            File shaTxt = Config.getShaTxtFromTagFolder(tagFolder);
            if (shaTxt == null)
                throw new Exception("校验码文本不存在");

            if (GithubDownload.checkWineTarSum(wineTarFile))
                AndroidHelpers.toast("压缩包校验结束，没有发现问题。");
            else
                throw new Exception("压缩包损坏或校验码文本不存在，请尝试删除并重新下载");
        } catch (Exception e) {
            e.printStackTrace();
            AndroidHelpers.toast(e.getMessage());
        }
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
