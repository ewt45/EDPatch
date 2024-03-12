package com.example.datainsert.exagear.controlsV2.edit;

import static com.example.datainsert.exagear.RR.getSArr;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;
import static com.example.datainsert.exagear.RR.getS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.ControlsFragment;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAreaView;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.model.OneProfile;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.example.datainsert.exagear.controlsV2.widget.RecyclerAdapter;
import com.example.datainsert.exagear.RR;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressLint("ViewConstructor")
public class Edit3ProfilesView extends LinearLayout {

    @SuppressLint("NotifyDataSetChanged")
    public Edit3ProfilesView(Context c) {
        super(c);
        setOrientation(VERTICAL);

        //新建 - 空白，复制已有配置，导入本地文件
        // 全部配置 - 删除，重命名，切换至，导出

        ProfileAdapter profileAdapter = new ProfileAdapter(Const.getTouchView());

        //新建
        Button btnAdd = TestHelper.getTextButton(c, getS(RR.global_add));
        btnAdd.setAllCaps(false);
        ControlsFragment.IntentResultCallback importCallback = (requestCode, resultCode, data) -> {
            String[] msgs = getSArr(RR.ctr2_profile_importMsgs);
            try {
                OneProfile oneProfile = ModelProvider.importProfileFromUri(data.getData());
                profileAdapter.refreshDataSet(true); //导入成功后要刷新列表显示
                Toast.makeText(Const.getContext(), /*导入成功*/msgs[0]+oneProfile.name, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Const.getContext(),/*导入失败*/ msgs[1] + e.getCause(), Toast.LENGTH_SHORT).show();
            }
        };


        String[] addTitles = getSArr(RR.ctr2_profile_addOptions);
        PopupMenu popupMenuAdd = new PopupMenu(c, btnAdd, Gravity.FILL_HORIZONTAL);
        popupMenuAdd.getMenu().add(/*空白配置*/addTitles[0]).setOnMenuItemClickListener(item -> {
            showEditNameDialog(c, null, true, profileAdapter);
            return true;
        });
        popupMenuAdd.getMenu().addSubMenu(/*复制现有配置*/addTitles[1]).getItem().setOnMenuItemClickListener(item->{
            popupMenuAdd.dismiss();
            PopupMenu popupMenuCopyCurrent = new PopupMenu(c,btnAdd);
            for (String name : Objects.requireNonNull(ModelProvider.profilesDir.list()))
                popupMenuCopyCurrent.getMenu().add( name).setOnMenuItemClickListener(itemSub -> {
                    showEditNameDialog(c, name, true, profileAdapter);
                    return true;
                });
            popupMenuCopyCurrent.show();
            return true;
        });
        popupMenuAdd.getMenu().add(/*从本地文件导入*/addTitles[2]).setOnMenuItemClickListener(item->{
            Const.getControlFragment().requestImportProfile(importCallback);
            return true;
        });
        btnAdd.setOnClickListener(v -> popupMenuAdd.show());

        RecyclerView recyclerView = new RecyclerView(c);
        recyclerView.setLayoutManager(new LinearLayoutManager(c));
        recyclerView.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(profileAdapter);

        addView(btnAdd);
        addView(recyclerView);
    }

    /**
     * 显示一个对话框，让用户输入配置名称。
     * <br/> 结束时，新建profile或重命名
     *
     * @param refName   若不为null，则使用提供的名字
     * @param createNew 若为true，则新建一个profile。若为false，则修改现有profile的名称
     */
    @SuppressLint("NotifyDataSetChanged")
    public static void showEditNameDialog(Context c, @Nullable String refName, boolean createNew, ProfileAdapter adapter) {
        if (!createNew && refName == null)
            throw new RuntimeException("若为重命名操作，则必须提供配置的当前名称");

        LimitEditText editName = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_TEXT_SINGLE_LINE)
                .setStringValue(refName!=null?refName:"");

        FrameLayout frameRoot = new FrameLayout(c);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -2);
        params.setMargins(dp8, dp8, dp8, dp8);
        frameRoot.addView(editName, params);
        new AlertDialog.Builder(c)
                .setTitle(getS(RR.ctr2_profile_editName))//配置名称
                .setView(frameRoot)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    TestHelper.saveCurrentEditProfileToFile();
                    String finalName = ModelProvider.getNiceProfileName(editName.getStringValue());
                    ModelProvider.createNewProfile(finalName, refName, true); //新建
                    if (!createNew){
                        boolean b = new File(ModelProvider.profilesDir, refName).delete();//重命名也相当于新建一个，然后把旧的删了就行
                    }
                    //创建完了之后，需要刷新回收视图.
                    adapter.refreshDataSet(true);

                    boolean needResetSelected = createNew || adapter.getDataList().get(adapter.currentSelect).equals(refName);
                    if (needResetSelected)
                        adapter.setCheckedItem(finalName); //在这里将新的profile实例设置到touchAreaView上
                })
                .setCancelable(false)
                .show();
    }

    public static class ProfileAdapter extends RecyclerAdapter<String> {
        int currentSelect;
        TouchAreaView mHostView;
        public ProfileAdapter(TouchAreaView hostView) {
            super(reReadAllProfiles());
            mHostView = hostView;
            currentSelect = getDataList().indexOf(ModelProvider.getCurrentProfileCanonicalName());
        }

        /**
         * 从本地重新读取全部profile、
         * <br/> 注意这个函数不会调用adapter.notify方法，请手动调用
         */
        private static List<String> reReadAllProfiles() {
            return Arrays.asList(Objects.requireNonNull(ModelProvider.profilesDir.list()));
        }

        /**
         * 刷新数据列表，设置是否通知
         */
        public void refreshDataSet(boolean notify){
            setDataList(reReadAllProfiles(),notify);
        }

        /**
         * 更换选中的配置。更新列表显示，
         * <br/>并修改currentProfile软链接，
         * <br/> 并修改touchAreaView的profile
         * <br/> 注意，不会将当前选中的配置从内存保存到本地。请手动调用{@link TestHelper#saveCurrentEditProfileToFile()}
         * @param name 配置名
         */
        public void setCheckedItem(String name) {
            int index = getDataList().indexOf(name);
            if (index != -1) {
//            if ((index != -1 && index != currentSelect) || (index==0 && mDataList.size()==1)) { //只剩下两个，第一个已选中，删除第一个，第二个变为第一个，但不会被选中，所以需要加个判断条件
                int oldSelect = currentSelect;
                currentSelect = index;
                ModelProvider.makeCurrent(name);
                notifyItemChanged(oldSelect);
                notifyItemChanged(index);
                //刷新触摸区域显示
                mHostView.setProfile(ModelProvider.readProfile(name));
                mHostView.postInvalidate();
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull RegularViewHolder holder, int pos) {
            String profileName = getDataList().get(pos);


            //不知道为啥click监听里调用线性布局的performClick没用，只能全部触摸事件都传给底下的布局了
            holder.imageView.setImageDrawable(pos == currentSelect ?TestHelper.getAssetsDrawable(holder.root.getContext(),"controls/check.xml"):null);
            holder.imageView.setVisibility(pos == currentSelect ? VISIBLE : INVISIBLE);
            holder.imageView.setOnTouchListener((v, event) -> holder.root.onTouchEvent(event));
            holder.text1.setText(profileName);
            holder.text1.setOnClickListener(v-> holder.root.performClick());
            holder.text2.setVisibility(GONE);
//            holder.tv.setOnTouchListener((v, event) -> holder.root.onTouchEvent(event));
            holder.root.setOnClickListener(v -> {
                TestHelper.saveCurrentEditProfileToFile(); //切换前先保存当前编辑的配置
                setCheckedItem(profileName);
            });

            holder.btnMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                String[] itemNames = getSArr(RR.ctr2_profile_oneOptions);
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 1, itemNames[0]);//导出为文件
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 2, itemNames[1]);//重命名
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 3, itemNames[2]);//删除

                ControlsFragment.IntentResultCallback exportCallback = (requestCode, resultCode, data) -> {
                    String[] exportMsgs = getSArr(RR.ctr2_profile_exportMsgs);
                    try {
                        ModelProvider.exportProfileToUri(data.getData(),profileName);
                        Toast.makeText(Const.getContext(), /*成功*/exportMsgs[0]+profileName, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(Const.getContext(), /*失败*/exportMsgs[1]+ e.getCause(), Toast.LENGTH_SHORT).show();
                    }
                };
                //由于编辑的model放在内存，有修改操作时（导出，复制，切换，重命名，退出编辑）时都应该将当前model同步到本地(saveProfile())，然后再操作
                popupMenu.setOnMenuItemClickListener(item -> {
                    TestHelper.saveCurrentEditProfileToFile();
                    switch (item.getOrder() & 0x0f) {
//                        case 1: //使用该配置
//                            holder.root.performClick();
//                            break;
                        case 1: //导出
                            Const.getControlFragment().requestExportProfile(profileName, exportCallback);
                            break;
                        case 2: //重命名
                            showEditNameDialog(v.getContext(), profileName, false, this);
                            break;
                        case 3: //删除
                            //只剩一个 不删。 被选中的删了，切换选中到第一个
                            if (getDataList().size() == 1)
                                break;
                            TestHelper.showConfirmDialog(v.getContext(), getS(RR.ctr2_profile_delConfirm), (dialog, which) -> {
                                int removedIndex = getDataList().indexOf(profileName);
                                boolean selectAnother = profileName.equals(ModelProvider.getCurrentProfileCanonicalName());
                                new File(ModelProvider.profilesDir, profileName).delete();
                                refreshDataSet(false);
                                notifyItemRemoved(removedIndex);
                                if (selectAnother)
                                    setCheckedItem(getDataList().get(0));
                            });
                            break;
                    }
                    return true;
                });
                popupMenu.show();
            });
        }
    }
}
