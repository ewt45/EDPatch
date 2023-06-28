package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.mutiWine.KronConfig.PROXY_GHPROXY;
import static com.example.datainsert.exagear.mutiWine.KronConfig.PROXY_GITHUB;
import static com.example.datainsert.exagear.mutiWine.KronConfig.PROXY_KGITHUB;
import static com.example.datainsert.exagear.mutiWine.KronConfig.PROXY_PREF_KEY;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;


/**
 * 像googleplay的设置界面那样，一个源是一个大项，点开之后里面是各版本下载
 */
public class WineStoreView extends LinearLayout {
    public WineStoreView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        int padding = QH.px(context, RR.attr.dialogPaddingDp);
//        setPadding(padding, padding, padding, padding);
        initUI(context);
    }

    private void initUI(Context c) {
//        //负责官方构建的最外层布局
//        LinearLayout officialLinear = new LinearLayout(c);
//        officialLinear.setOrientation(VERTICAL);
//
//        //官方构建的标题。点击会显示版本信息
//        TextView titleOfficial = new TextView(c);
//        titleOfficial.setText("官方（WineHQ）");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            titleOfficial.setTextColor(Globals.getAppContext().getColor(QH.rslvID(R.color.primary_text,0x7f060061)));
//        }
//        titleOfficial.setClickable(true);
//
//
//        //官方构建的版本信息，可以下载或删除
//        RecyclerView officialVersions = new RecyclerView(c);
//        officialVersions.setLayoutManager(new LinearLayoutManager(c,LinearLayoutManager.VERTICAL,false));
//        officialVersions.setAdapter(new OfficialBuildAdapter());
//        officialVersions.setVisibility(GONE);
////        officialVersions.addView(new Button(c));
//
//        titleOfficial.setOnClickListener(v -> {
//            boolean isVisible = officialVersions.getVisibility()==VISIBLE;
//            officialVersions.setVisibility(isVisible?GONE:VISIBLE);
//        });
//        LayoutParams titleOfficialParams = new LayoutParams(-1,-2);
//        titleOfficialParams.bottomMargin=40;
//        officialLinear.addView(titleOfficial,titleOfficialParams);
//        officialLinear.addView(officialVersions);
//        addView(officialLinear);

        //已下载的内容
        LinearLayout linearLocal = new LinearLayout(c);
        linearLocal.setOrientation(VERTICAL);

        RecyclerView localVersions = new RecyclerView(c);
        localVersions.setLayoutManager(new LinearLayoutManager(c, LinearLayoutManager.VERTICAL, false));
        localVersions.setAdapter(new LocalAdapter());
        linearLocal.addView(localVersions);
//        addView(linearKron);

        //负责kron4ek构建的最外层布局
        LinearLayout linearKron = new LinearLayout(c);
        linearKron.setOrientation(VERTICAL);

        //下载页面的上方选项
        LinearLayout linearOption = new LinearLayout(c);
        linearOption.setOrientation(HORIZONTAL);
        linearOption.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.GRAY);
        gradientDrawable.setSize(AndroidHelpers.dpToPx(2),AndroidHelpers.dpToPx(8));
        linearOption.setDividerDrawable(gradientDrawable);
        linearOption.setDividerPadding(AndroidHelpers.dpToPx(10));

        LinearLayout.LayoutParams badgeParams = new LayoutParams(-2, -2);
        badgeParams.rightMargin = AndroidHelpers.dpToPx(8);
        badgeParams.leftMargin = AndroidHelpers.dpToPx(8);
        //刷新按钮
        Button refreshBtn = new Button(c);
        refreshBtn.setText(RR.getS(RR.mw_refreshBtn));//🔄 🔧
        setupBadgeButtonWidth(refreshBtn);
        linearOption.addView(refreshBtn,badgeParams);

        //下载线路
        if("zh".equals(RR.locale)){
            Button proxyBtn = new Button(c);
            proxyBtn.setText("下载线路");
            proxyBtn.setOnClickListener(v->{
                PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
                int proxy = QH.getPreference().getInt(PROXY_PREF_KEY,PROXY_GITHUB);
                popupMenu.getMenu().add("github").setCheckable(true).setChecked(proxy== PROXY_GITHUB).setOnMenuItemClickListener(item->{
                    QH.getPreference().edit().putInt(PROXY_PREF_KEY,PROXY_GITHUB).apply();
                    return true;
                });
                popupMenu.getMenu().add("ghproxy").setCheckable(true).setChecked(proxy==PROXY_GHPROXY).setOnMenuItemClickListener(item->{
                    QH.getPreference().edit().putInt(PROXY_PREF_KEY,PROXY_GHPROXY).apply();
                    return true;
                });
                popupMenu.getMenu().add("kgithub").setCheckable(true).setChecked(proxy==PROXY_KGITHUB).setOnMenuItemClickListener(item->{
                    QH.getPreference().edit().putInt(PROXY_PREF_KEY,PROXY_KGITHUB).apply();
                    return true;
                });
                popupMenu.show();
            });
            setupBadgeButtonWidth(proxyBtn);
            linearOption.addView(proxyBtn,badgeParams);
        }


//        Button versionBtn = new Button(c);
//        versionBtn.setText("版本选择");
//        setupBadgeButtonWidth(versionBtn);
//        linearOption.addView(versionBtn,badgeParams);

        HorizontalScrollView scrollOption = new HorizontalScrollView(c);
        scrollOption.addView(linearOption,new LayoutParams(-2,-2));
        linearKron.addView(scrollOption,new LayoutParams(-1,-2));


        //kron4ek构建的版本信息，可以下载或删除
        RecyclerView kronVersions = new RecyclerView(c);
        kronVersions.setLayoutManager(new LinearLayoutManager(c, LinearLayoutManager.VERTICAL, false));
        kronVersions.setAdapter(new KronAdapter());
        linearKron.addView(kronVersions);
//        addView(linearKron);
        refreshBtn.setOnClickListener(v -> {
            RecyclerView.Adapter<?> adapter = kronVersions.getAdapter();
            if(adapter instanceof KronAdapter){
                ((KronAdapter) adapter).refresh();
            }
        });

        //标签页滑动视图
        TabLayout tabLayout = new TabLayout(getContext());
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        ViewPager viewPager = new WineStorePager(getContext(),new ViewGroup[]{linearLocal,linearKron});
        LinearLayout.LayoutParams viewPagerParams = new LinearLayout.LayoutParams(-1, -1);
        int margin = AndroidHelpers.dpToPx(8);
        viewPagerParams.setMargins(margin, margin, margin, 0);
//        viewPager.setLabelFor(View.NO_ID);
//        viewPager.setId(VIEWPAGER_RESOURCE_ID);
        //设置适配器，显示两个标签对应的布局

        //切换到“本地”页面显示的时候，刷新
        tabLayout.setupWithViewPager(viewPager, false);
        addView(tabLayout, new ViewGroup.LayoutParams(-1, -2));
        addView(viewPager, viewPagerParams);


    }


    private void setupBadgeButtonWidth(Button btn){
        QH.setRippleBackground(btn);
        btn.setMinWidth(0);
        btn.setMinimumWidth(0);
        int padding = AndroidHelpers.dpToPx(4);
        btn.setPadding(padding,0,padding,0);
    }
}
