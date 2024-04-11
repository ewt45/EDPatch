package com.example.datainsert.exagear.controlsV2.edit;

import static com.example.datainsert.exagear.RR.getSArr;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controlsV2.model.ModelProvider.getNiceProfileName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorSpace;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datainsert.exagear.QH;
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
import java.io.IOException;
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
                //先把要导入的配置替换合理名称导入到本地。然后显示编辑名称的dialog，当做是在对现有配置重命名
                OneProfile profile = ModelProvider.readProfileFromUri(data.getData());
                profile.setName(getNiceProfileName(profile.getName()));
                profile.adjustProfileToFullscreen(); //适配当前设备的分辨率
                ModelProvider.saveProfile(profile);
                Toast.makeText(Const.getContext(), /*导入成功*/msgs[0]+profile.getName(), Toast.LENGTH_SHORT).show();
                showEditNameDialog(c,profile.getName(),false,profileAdapter);
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
            for (String name : Objects.requireNonNull(Const.Files.profilesDir.list()))
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
     * <br/> 新建空白profile/复制现有profile/为现有profile重命名/为导入profile重命名
     * @param refName   若不为null，则使用此profile作为参考/编辑
     * @param createNew 若为true，则新建一个profile且不删掉ref。若为false，则修改现有profile的名称（删掉ref对应文件）
     */
    @SuppressLint("NotifyDataSetChanged")
    private static void showEditNameDialog(Context c, @Nullable String refName, boolean createNew, ProfileAdapter adapter) {
        if (!createNew && refName == null)
            throw new RuntimeException("若为重命名操作，则必须提供配置的当前名称");

        TextView tvWarn = new TextView(c);
        tvWarn.setTextColor(0xffF56C6C);
        tvWarn.setText(getS(RR.ctr2_profile_editNameWarn)); //某些情况下允许重复

        //关闭dialog时回调. 初始enable在下面的editName，先设置监听再设置初始值，触发监听就行了
        Button btnYes = QH.getBorderlessColoredButton(c,c.getString(android.R.string.yes));

        LimitEditText editName = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_TEXT_SINGLE_LINE)
                .setUpdateListener(editText -> {
                    String inputName = editText.getStringValue();
                    //如果输入名称与修正后名称不等，说明该名称重复或包含特殊字符。但是如果是在重命名，那么允许输入名称与原名称重复
                    boolean allowed = (!createNew && refName.equals(inputName)) || getNiceProfileName(inputName).equals(inputName);
                    tvWarn.setVisibility(allowed ? GONE : VISIBLE);
                    btnYes.setEnabled(allowed);
                 })
                .setStringValue(refName!=null?refName:"");

        LinearLayout linearEditNameRoot = new LinearLayout(c);
        linearEditNameRoot.setOrientation(VERTICAL);
        linearEditNameRoot.addView(editName,QH.LPLinear.one(-1,-2).left().right().top().to());
        linearEditNameRoot.addView(tvWarn,QH.LPLinear.one(-1,-2).left().right().top().to());
        linearEditNameRoot.addView(btnYes,QH.LPLinear.one(-2,-2).gravity(Gravity.END).margin(0,dp8,dp8*2,dp8).to());
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(getS(RR.ctr2_profile_editName))//配置名称
                .setView(linearEditNameRoot)
                .setCancelable(true)
                .create();
        btnYes.setOnClickListener(v-> {
            TestHelper.saveCurrentEditProfileToFile();
            String finalName = editName.getStringValue();
            if(!finalName.equals(refName)){
                ModelProvider.createNewProfile(finalName, refName, true); //新建
                if (!createNew){
                    boolean b = new File(Const.Files.profilesDir, refName).delete();//重命名也相当于新建一个，然后把旧的删了就行
                }
            }
            //创建完了之后，需要刷新回收视图.
            adapter.refreshDataSet(true);
            adapter.setCheckedItem(finalName); //在这里将新的profile实例设置到touchAreaView上

            dialog.dismiss();
        });
        dialog.show();



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
            return Arrays.asList(Objects.requireNonNull(Const.Files.profilesDir.list()));
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
                ModelProvider.makeCurrentForContainerAndGlobal(name);
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
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 1, itemNames[0])//设置为新建容器的默认配置
                        .setCheckable(true).setChecked(isDefaultForNewContainer(profileName));
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 2, itemNames[1]);//导出为文件
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 3, itemNames[2]);//复制
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 4, itemNames[3]);//重命名
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 5, itemNames[4]);//删除

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
                        case 1: //设置为新建容器的默认配置
                            ModelProvider.makeDefaultForNewContainer(profileName);
                            break;
                        case 2: //导出
                            Const.getControlFragment().requestExportProfile(profileName, exportCallback);
                            break;
                        case 3: //复制
                            showEditNameDialog(v.getContext(), profileName, true, this);
                            break;
                        case 4: //重命名 内置配置禁止
                            if(Const.profileBundledNames.contains(profileName)) {
                                TestHelper.showConfirmDialog(v.getContext(), //"该配置为内置配置，无法进行此操作。"
                                        getS(RR.ctr2_profile_bundleProfNotAllow), null);
                                break;
                            }
                            showEditNameDialog(v.getContext(), profileName, false, this);
                            break;
                        case 5: //删除 内置配置禁止
                            if(Const.profileBundledNames.contains(profileName)) {
                                TestHelper.showConfirmDialog(v.getContext(), //"该配置为内置配置，无法进行此操作。"
                                        getS(RR.ctr2_profile_bundleProfNotAllow), null);
                                break;
                            }
                            //只剩一个 不删。 被选中的删了，切换选中到第一个
                            if (getDataList().size() == 1)
                                break;
                            TestHelper.showConfirmDialog(v.getContext(), getS(RR.ctr2_profile_delConfirm), (dialog, which) -> {
                                int removedIndex = getDataList().indexOf(profileName);
                                boolean selectAnother = profileName.equals(ModelProvider.getCurrentProfileCanonicalName());
                                new File(Const.Files.profilesDir, profileName).delete();
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
        private boolean isDefaultForNewContainer(String profileName){
            boolean isDefaultForNewCont = false;
            try {
                isDefaultForNewCont = Const.Files.defaultProfileForNewContainer.getCanonicalFile().getName().equals(profileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return isDefaultForNewCont;
        }
    }

}
