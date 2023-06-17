package com.example.datainsert.exagear.mutiWine.v2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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
        setPadding(padding, padding, padding, padding);
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
        localVersions.setAdapter(new LocalWineAdapter());
        linearLocal.addView(localVersions);
//        addView(linearKron);

        //负责kron4ek构建的最外层布局
        LinearLayout linearKron = new LinearLayout(c);
        linearKron.setOrientation(VERTICAL);

        //刷新按钮
        Button refreshBtn = new Button(c);
        refreshBtn.setText("↻ 刷新列表");//🔄

        LinearLayout.LayoutParams refreshParams = new LayoutParams(-2, -2);
        refreshParams.bottomMargin = 20;
        linearKron.addView(refreshBtn, refreshParams);

        //kron4ek构建的版本信息，可以下载或删除
        RecyclerView kronVersions = new RecyclerView(c);
        kronVersions.setLayoutManager(new LinearLayoutManager(c, LinearLayoutManager.VERTICAL, false));
        kronVersions.setAdapter(new KronBuildAdapter());
        linearKron.addView(kronVersions);
//        addView(linearKron);
        refreshBtn.setOnClickListener(v -> {
            RecyclerView.Adapter<?> adapter = kronVersions.getAdapter();
            if(adapter instanceof  KronBuildAdapter){
                ((KronBuildAdapter) adapter).refresh();
            }
        });

        //标签页滑动视图
        TabLayout tabLayout = new TabLayout(getContext());
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        ViewPager viewPager = new ViewPager(getContext());
        LinearLayout.LayoutParams viewPagerParams = new LinearLayout.LayoutParams(-1, -1);
        viewPagerParams.setMargins(0, 20, 0, 20);
//        viewPager.setLabelFor(View.NO_ID);
//        viewPager.setId(VIEWPAGER_RESOURCE_ID);
        //设置适配器，显示两个标签对应的布局
        viewPager.setAdapter(new PagerAdapter() {

            private final String[] mTabTitles = new String[]{"本地", "可下载"};
            private final View[] mViewPages = new View[]{linearLocal, linearKron};

            @Override
            public int getCount() {
                return mTabTitles.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(mViewPages[position]);
                return mViewPages[position];
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(mViewPages[position]);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTabTitles[position];
            }
        });
        //切换到“本地”页面显示的时候，刷新
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    RecyclerView.Adapter adapter = localVersions.getAdapter();
                    if(adapter instanceof LocalWineAdapter)
                        ((LocalWineAdapter) adapter).refresh(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.setupWithViewPager(viewPager, false);
        addView(tabLayout, new ViewGroup.LayoutParams(-1, -2));
        addView(viewPager, viewPagerParams);


    }
}
