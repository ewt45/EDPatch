package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.mutiWine.v2.DownloadParser.PARSER_KRON4EK;
import static com.example.datainsert.exagear.mutiWine.v2.DownloadParser.PARSER_PREF_KEY;
import static com.example.datainsert.exagear.mutiWine.v2.DownloadParser.PARSER_WINEHQ;
import static com.example.datainsert.exagear.mutiWine.v2.HQConfig.PROXY_TSINGHUA;
import static com.example.datainsert.exagear.mutiWine.v2.HQConfig.PROXY_WINEHQ;
import static com.example.datainsert.exagear.mutiWine.v2.HQConfig.PROXY_WINEHQ_PREF_KEY;
import static com.example.datainsert.exagear.mutiWine.v2.KronConfig.PROXY_GHPROXY;
import static com.example.datainsert.exagear.mutiWine.v2.KronConfig.PROXY_GITHUB;
import static com.example.datainsert.exagear.mutiWine.v2.KronConfig.PROXY_KGITHUB;
import static com.example.datainsert.exagear.mutiWine.v2.KronConfig.PROXY_GITHUB_PREF_KEY;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

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

        //已下载的内容
        LinearLayout linearLocal = new LinearLayout(c);
        linearLocal.setOrientation(VERTICAL);

        RecyclerView localVersions = new RecyclerView(c);
        localVersions.setLayoutManager(new LinearLayoutManager(c, LinearLayoutManager.VERTICAL, false));
        localVersions.setAdapter(new LocalAdapter());
        linearLocal.addView(localVersions);
//        addView(linearKron);

        //可下载页面
        LinearLayout linearKron = new LinearLayout(c);
        linearKron.setOrientation(VERTICAL);

        //下载页面的上方选项
        LinearLayout linearOption = new LinearLayout(c);
        linearOption.setOrientation(HORIZONTAL);
        linearOption.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.GRAY);
        gradientDrawable.setSize(AndroidHelpers.dpToPx(2), AndroidHelpers.dpToPx(8));
        linearOption.setDividerDrawable(gradientDrawable);
        linearOption.setDividerPadding(AndroidHelpers.dpToPx(10));

        LinearLayout.LayoutParams badgeParams = new LayoutParams(-2, -2);
        badgeParams.rightMargin = AndroidHelpers.dpToPx(8);
        badgeParams.leftMargin = AndroidHelpers.dpToPx(8);
        //刷新按钮
        Button refreshBtn = new Button(c);
        refreshBtn.setText(RR.getS(RR.mw_refreshBtn));//🔄 🔧
        setupBadgeButtonWidth(refreshBtn);
        linearOption.addView(refreshBtn, badgeParams);

        //下载线路
        Button proxyBtn = new Button(c);
        if ("zh".equals(RR.locale)) {
            proxyBtn.setText("下载线路");
            setupBadgeButtonWidth(proxyBtn);
            setProxyBtnListener(proxyBtn,QH.getPreference().getInt(PARSER_PREF_KEY, PARSER_KRON4EK));
            linearOption.addView(proxyBtn, badgeParams);
        }

        //下载源
        Button dlSrcBtn = new Button(c);
        dlSrcBtn.setText(getS(RR.mw_dlSourceBtn));
        setupBadgeButtonWidth(dlSrcBtn);
        linearOption.addView(dlSrcBtn, badgeParams);

//        Button versionBtn = new Button(c);
//        versionBtn.setText("版本选择");
//        setupBadgeButtonWidth(versionBtn);
//        linearOption.addView(versionBtn,badgeParams);

        HorizontalScrollView scrollOption = new HorizontalScrollView(c);
        scrollOption.addView(linearOption, new LayoutParams(-2, -2));
        linearKron.addView(scrollOption, new LayoutParams(-1, -2));


        //各种版本wine的信息，可以下载
        RecyclerView downloadRecycler = new RecyclerView(c);
        downloadRecycler.setLayoutManager(new LinearLayoutManager(c, LinearLayoutManager.VERTICAL, false));
        downloadRecycler.setAdapter(new DownloadAdapter());
        linearKron.addView(downloadRecycler);
//        addView(linearKron);
        refreshBtn.setOnClickListener(v -> {
            RecyclerView.Adapter<?> adapter = downloadRecycler.getAdapter();
            if (adapter instanceof DownloadAdapter) {
                ((DownloadAdapter) adapter).refresh();
            }
        });

        dlSrcBtn.setOnClickListener(v -> {
            int currParserType = QH.getPreference().getInt(PARSER_PREF_KEY, PARSER_KRON4EK);
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.getMenu().add("Kron4ek").setCheckable(true).setChecked(currParserType == PARSER_KRON4EK);
            popupMenu.getMenu().add("WineHQ").setCheckable(true).setChecked(currParserType == PARSER_WINEHQ);
            popupMenu.setOnMenuItemClickListener(item -> {
                int newParserType = item.getTitle().equals("WineHQ") ? PARSER_WINEHQ : PARSER_KRON4EK;
                QH.getPreference().edit().putInt(PARSER_PREF_KEY, newParserType).apply();
                //通知适配器刷新视图
                RecyclerView.Adapter<?> adapter = downloadRecycler.getAdapter();
                if (adapter instanceof DownloadAdapter) {
                    ((DownloadAdapter) adapter).prepareParser(newParserType);
                }
                //下载线路更新
                setProxyBtnListener(proxyBtn,newParserType);
                return true;
            });
            popupMenu.show();
        });

        //说明页面
        LinearLayout linearTips = new LinearLayout(c);
        linearTips.setOrientation(LinearLayout.VERTICAL);
        TextView tvInfo = new TextView(c);
        tvInfo.setLineSpacing(0,1.2f);
        tvInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP ,16);
        tvInfo.setText(Html.fromHtml(getS(RR.mw_tips)));
//        LinearLayout.LayoutParams tvInfoParams = new LinearLayout.LayoutParams(-2,-2);
//        tvInfoParams.topMargin = 20;
        linearTips.addView(tvInfo);

        //标签页滑动视图
        TabLayout tabLayout = new TabLayout(getContext());
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        ViewPager viewPager = new WineStorePager(getContext(), new ViewGroup[]{linearLocal, linearKron,linearTips});
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


    private void setupBadgeButtonWidth(Button btn) {
        QH.setRippleBackground(btn);
        btn.setMinWidth(0);
        btn.setMinimumWidth(0);
        int padding = AndroidHelpers.dpToPx(4);
        btn.setPadding(padding, 0, padding, 0);
    }

    private void setProxyBtnListener(Button proxyBtn, int dlSrc) {
        if (!"zh".equals(RR.locale)) {
            return;
        }
        proxyBtn.setOnClickListener(dlSrc == DownloadParser.PARSER_KRON4EK
                //Kron4ek构建的下载线路
                ? v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            int proxy = QH.getPreference().getInt(PROXY_GITHUB_PREF_KEY, PROXY_GITHUB);
            popupMenu.getMenu().add("github").setCheckable(true).setChecked(proxy == PROXY_GITHUB).setOnMenuItemClickListener(item -> {
                QH.getPreference().edit().putInt(PROXY_GITHUB_PREF_KEY, PROXY_GITHUB).apply();
                return true;
            });
            popupMenu.getMenu().add("ghproxy").setCheckable(true).setChecked(proxy == PROXY_GHPROXY).setOnMenuItemClickListener(item -> {
                QH.getPreference().edit().putInt(PROXY_GITHUB_PREF_KEY, PROXY_GHPROXY).apply();
                return true;
            });
            popupMenu.getMenu().add("kgithub").setCheckable(true).setChecked(proxy == PROXY_KGITHUB).setOnMenuItemClickListener(item -> {
                QH.getPreference().edit().putInt(PROXY_GITHUB_PREF_KEY, PROXY_KGITHUB).apply();
                return true;
            });
            popupMenu.show();
        }
                //wineHQ 构建的下载线路
                : v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            int proxy = QH.getPreference().getInt(PROXY_WINEHQ_PREF_KEY, PROXY_WINEHQ);
            popupMenu.getMenu().add("WineHQ").setCheckable(true).setChecked(proxy == PROXY_WINEHQ).setOnMenuItemClickListener(item -> {
                QH.getPreference().edit().putInt(PROXY_WINEHQ_PREF_KEY, PROXY_WINEHQ).apply();
                return true;
            });
            popupMenu.getMenu().add("清华").setCheckable(true).setChecked(proxy == PROXY_TSINGHUA).setOnMenuItemClickListener(item -> {
                QH.getPreference().edit().putInt(PROXY_WINEHQ_PREF_KEY, PROXY_TSINGHUA).apply();
                return true;
            });
            popupMenu.show();
        });
    }
}
