package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;
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
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.ModelFileSaver;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressLint("ViewConstructor")
public class Edit3ProfilesView extends LinearLayout {
    private static final int MENU_CREATE_NEW = 9999;
    private static final int MENU_COPY = 9998;
    private static final int MENU_IMPORT = 9997;
    Edit0Main mHost;


    public Edit3ProfilesView(Edit0Main host) {
        super(host.getContext());
        mHost = host;
        Context c = host.getContext();
        setOrientation(VERTICAL);

        //新建 - 空白，复制已有配置，导入本地文件
        // 全部配置 - 删除，重命名，切换至，导出

        ProfileAdapter profileAdapter = new ProfileAdapter(mHost.mHost);
        //新建
        Button btnAdd = TestHelper.getTextButton(c, getS(RR.global_add));
        btnAdd.setAllCaps(false);
        btnAdd.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v, Gravity.FILL_HORIZONTAL);
            popupMenu.getMenu().add(Menu.NONE, MENU_CREATE_NEW, Menu.NONE, "空白配置");
            SubMenu subMenu = popupMenu.getMenu().addSubMenu(Menu.NONE, MENU_COPY, Menu.NONE, "复制现有配置");
            popupMenu.getMenu().add(Menu.NONE, MENU_IMPORT, Menu.NONE, "从本地文件导入");
            for (String name : Objects.requireNonNull(ModelFileSaver.profilesDir.list()))
                subMenu.add(2, Menu.NONE, Menu.NONE, name);

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {

                    case MENU_IMPORT:
                        Const.fragmentRef.get().requestImportProfile();
                        return true;
                    case MENU_CREATE_NEW:
                    case Menu.NONE:
                        showEditNameDialog(
                                v.getContext(),
                                item.getItemId() == MENU_CREATE_NEW ? null : item.getTitle().toString(),
                                true,
                                profileAdapter);
                        return true;
                    case MENU_COPY:
                    default:
                        return true;
                }
            });
            popupMenu.show();
        });

        RecyclerView recyclerView = new RecyclerView(c);
        recyclerView.setLayoutManager(new LinearLayoutManager(c));
        recyclerView.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(profileAdapter);

        addView(btnAdd);
        addView(recyclerView);


        //选择
        TextView btnProfileName = TestHelper.getTextButton(c, "当前存档");
        QH.setRippleBackground(btnProfileName);
        btnProfileName.setOnClickListener(v -> {
            String[] names = ModelFileSaver.profilesDir.list();
            if (names == null)
                return;
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            for (String fileName : names) {
                popupMenu.getMenu().add(fileName).setOnMenuItemClickListener(item -> {
                    ModelFileSaver.makeCurrent(fileName);
                    return true;
                });
            }
            popupMenu.show();
        });

        Const.profilesAdapterRef = new WeakReference<>(profileAdapter);
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

        EditText editText = new EditText(c);
        editText.setSingleLine(true);

        FrameLayout frameRoot = new FrameLayout(c);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -2);
        params.setMargins(dp8, dp8, dp8, dp8);
        frameRoot.addView(editText, params);
        if (refName != null)
            editText.setText(refName);
        new AlertDialog.Builder(c)
                .setTitle("配置名称")
                .setView(frameRoot)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    TestHelper.saveCurrentEditProfileToFile();
                    String finalName = ModelFileSaver.getNiceProfileName(editText.getText().toString());
                    if (createNew)
                        ModelFileSaver.createNewProfile(finalName, refName, true); //新建
                    else {
                        ModelFileSaver.createNewProfile(finalName, refName, false); //重命名也相当于新建一个，然后把旧的删了就行
                        boolean b = new File(ModelFileSaver.profilesDir, refName).delete();
                    }
                    //创建完了之后，需要刷新回收视图.
                    boolean needResetSelected = createNew || adapter.mDataList.get(adapter.currentSelect).equals(refName);
                    adapter.refreshDataSet();
                    if (needResetSelected)
                        adapter.setCheckedItem(finalName);
                    adapter.notifyDataSetChanged();
                })
                .setCancelable(false)
                .show();
    }

    public static class ProfileAdapter extends RecyclerView.Adapter<Edit3ProfilesView.ViewHolder> {
        List<String> mDataList = new ArrayList<>();
        int currentSelect = 0;
        TouchAreaView mHostView;

        public ProfileAdapter(TouchAreaView hostView) {
            super();
            mHostView = hostView;
            refreshDataSet();
            currentSelect = mDataList.indexOf(ModelFileSaver.getCurrentProfileCanonicalName());
        }

        /**
         * 设置current配置为选中
         */
        public void setCheckedItemByCurrent() {
            try {
                String currentName = ModelFileSaver.currentProfile.getCanonicalFile().getName();
                setCheckedItem(currentName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 更换选中的配置。更新列表显示，
         * <br/>并修改currentProfile软链接，
         * <br/> 并修改touchAreaView的profile
         * <br/> 注意，不会将当前选中的配置从内存保存到本地。请手动调用{@link TestHelper#saveCurrentEditProfileToFile()}
         * @param name 配置名
         */
        public void setCheckedItem(String name) {
            int index = mDataList.indexOf(name);
            if (index != -1) {
//            if ((index != -1 && index != currentSelect) || (index==0 && mDataList.size()==1)) { //只剩下两个，第一个已选中，删除第一个，第二个变为第一个，但不会被选中，所以需要加个判断条件
                int oldSelect = currentSelect;
                currentSelect = index;
                ModelFileSaver.makeCurrent(name);
                notifyItemChanged(oldSelect);
                notifyItemChanged(index);
                //刷新触摸区域显示
                mHostView.setProfile(ModelFileSaver.readProfile(name));
                mHostView.postInvalidate();
            }
        }

        /**
         * 刷新自身数据列表，从本地重新读取全部profile、
         * <br/> 注意这个函数不会调用adapter.notify方法，请手动调用
         */
        public void refreshDataSet() {
            mDataList.clear();
            String[] names = ModelFileSaver.profilesDir.list();
            if (names != null)
                mDataList.addAll(Arrays.asList(names));
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//            android.R.attr.selectableItemBackground
            Context c = viewGroup.getContext();

            //勾选
            ImageView iconCheck = new ImageView(c);
            iconCheck.setId(android.R.id.checkbox);
            iconCheck.setImageDrawable(c.getDrawable(R.drawable.aaa_check));
            iconCheck.setPadding(dp8 / 2, dp8 / 2, dp8 / 2, dp8 / 2);

            //配置名
            TextView tvName = new TextView(c);//(TextView) LayoutInflater.from(c).inflate(android.R.layout.simple_list_item_1, linearRoot, false);
            tvName.setGravity(Gravity.CENTER_VERTICAL);
            tvName.setTextColor(RR.attr.textColorPrimary(c));
            tvName.setSingleLine();
//            tvName.setMovementMethod(new ScrollingMovementMethod());
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvName.setId(android.R.id.text1);

            //菜单按钮
            ImageButton btnMenu = new ImageButton(c);
            btnMenu.setId(android.R.id.button1);
            btnMenu.setImageResource(RR.drawable.ic_more_vert_24dp());
            btnMenu.setBackground(RR.attr.selectableItemBackground(c));

            LinearLayout linearRoot = new LinearLayout(c);
            linearRoot.setOrientation(LinearLayout.HORIZONTAL);
            linearRoot.setBackground(TestHelper.getAttrDrawable(c, android.R.attr.selectableItemBackground));
            linearRoot.addView(iconCheck, QH.LPLinear.one(dp8 * 6, dp8 * 6).left().to());
            linearRoot.addView(tvName, QH.LPLinear.one(0, dp8 * 6).weight().left().to());
            linearRoot.addView(btnMenu, QH.LPLinear.one(dp8 * 6, dp8 * 6).left().to());

            linearRoot.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
//            linearRoot.setLayoutTransition(new LayoutTransition());
            return new ViewHolder(linearRoot);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
            String profileName = mDataList.get(pos);


            //不知道为啥click监听里调用线性布局的performClick没用，只能全部触摸事件都传给底下的布局了
            holder.ck.setVisibility(pos == currentSelect ? VISIBLE : INVISIBLE);
            holder.ck.setOnTouchListener((v, event) -> holder.root.onTouchEvent(event));
            holder.tv.setText(profileName);
            holder.tv.setOnClickListener(v->{holder.root.performClick();});
//            holder.tv.setOnTouchListener((v, event) -> holder.root.onTouchEvent(event));
            holder.root.setOnClickListener(v -> {
                TestHelper.saveCurrentEditProfileToFile(); //切换前先保存当前编辑的配置
                setCheckedItem(profileName);
            });

            holder.btnMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 1, "使用该配置");
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 2, "导出为文件");
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 3, "重命名");
                popupMenu.getMenu().add(Menu.NONE, Menu.NONE, 4, "删除");

                //由于编辑的model放在内存，有修改操作时（导出，复制，切换，重命名，退出编辑）时都应该将当前model同步到本地(saveProfile())，然后再操作
                popupMenu.setOnMenuItemClickListener(item -> {
                    TestHelper.saveCurrentEditProfileToFile();
                    switch (item.getOrder() & 0x0f) {
                        case 1: //使用该配置
                            holder.root.performClick();
                            break;
                        case 2: //导出
                            Const.fragmentRef.get().requestExportProfile(profileName);
                            break;
                        case 3: //重命名
                            showEditNameDialog(v.getContext(), profileName, false, this);
                            break;
                        case 4: //删除
                            //只剩一个 不删。 被选中的删了，切换选中到第一个
                            if (mDataList.size() == 1)
                                break;
                            TestHelper.showConfirmDialog(v.getContext(), "确定要删除吗?", (DialogInterface.OnClickListener) (dialog, which) -> {
                                int removedIndex = mDataList.indexOf(profileName);
                                boolean selectAnother = profileName.equals(ModelFileSaver.getCurrentProfileCanonicalName());
                                new File(ModelFileSaver.profilesDir, profileName).delete();
                                refreshDataSet();
                                notifyItemRemoved(removedIndex);
                                if (selectAnother)
                                    setCheckedItem(mDataList.get(0));
                            });
                            break;
                    }
                    return true;
                });
                popupMenu.show();
            });
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout root;
        View ck;
        TextView tv;
        ImageButton btnMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = (LinearLayout) itemView;
            ck = root.findViewById(android.R.id.checkbox);
            tv = root.findViewById(android.R.id.text1);
            btnMenu = root.findViewById(android.R.id.button1);
        }
    }


}
