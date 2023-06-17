package com.example.datainsert.exagear.mutiWine;


import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;


import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.ed.R;
import com.eltechs.ed.fragments.ManageContainersFragment;
import com.eltechs.ed.guestContainers.GuestContainer;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.mutiWine.v2.WineStoreFragment;

class MyItemClickListener implements MenuItem.OnMenuItemClickListener {
    private final String TAG = "MyItemClickListener";
    ManageContainersFragment fragment;
    AsyncTask<GuestContainer, Void, Void> task;

    MyItemClickListener(ManageContainersFragment fragmentInstance){
        this.fragment = fragmentInstance;
    }

    //显示消息的那个，activity类型在smali要改成v4包的
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick: 点击了菜单项 "+item.getTitle());
        //管理wine版本界面
        if(item.getGroupId() == 3){
            FragmentManager supportFragmentManager = ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity().getSupportFragmentManager();
            for (int i = 0; i < supportFragmentManager.getBackStackEntryCount(); i++) {
                supportFragmentManager.popBackStack();
            }
            WineStoreFragment wineStoreFragment = new WineStoreFragment();
            supportFragmentManager.beginTransaction()
                    .replace(QH.rslvID(R.id.ed_main_fragment_container,0x7f090070), wineStoreFragment, "CONTAINER_PROP")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null)
                    .commit();
            //为什么Builder里传Global.getAppContext()就会闪退？？
//            new AlertDialog.Builder(((ApplicationStateBase)Globals.getApplicationState()).getCurrentActivity())
//                    .setTitle("添加/删除wine版本")
//                    .setView(new WineStoreView(Globals.getAppContext()))
//                    .setPositiveButton(android.R.string.yes,null)
//                    .setCancelable(false)
//                    .show();
            return true;
        }
        //如果点击的是是使用说明,显示对话框
        else if(item.getGroupId()==2 && fragment!=null){

            TextView usageTv = new TextView(fragment.getActivity());
            SpannableString spanText = new SpannableString("多版本wine共存功能 修改by 补补23456");
            spanText.setSpan(new URLSpan("https://ewt45.github.io/blogs/2022/autumn/exagearMultiWine/"), 0, spanText.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            usageTv.setPadding(20,20,20,20);
            usageTv.setText(WineVersionConfig.usage);//添加txt里的使用说明
            usageTv.append("\n");
            usageTv.append(spanText);//添加作者链接（居然不能和换行加在一起不然没法跳转了）
            usageTv.setMovementMethod(LinkMovementMethod.getInstance());//让URLSpan可以点击
            //这个activity在我这里是androidx包的，smali要从Landroidx/fragment/app/FragmentActivity;改成Landroid/support/v4/app/FragmentActivity;
            new AlertDialog.Builder(fragment.getActivity()).setView(usageTv).show();
            return true;
        }
        //如果点击的是wine版本，新建容器.通过itemId作为list的下标获取wine信息
        if(item.getGroupId()==1 && item.getItemId()<WineVersionConfig.wineList.size()){
            //将创建容器的wine版本写入临时pref
            WineVersion wine = WineVersionConfig.wineList.get(item.getItemId());
            MutiWine.writeTmpWineVerPref(wine);
            //执行原本的创建容器task
            if(fragment!=null){
                fragment.callToCreateNewContainer();
//                task.execute();
            }else{
                Log.e(TAG, "onMenuItemClick: fragment为null，无法调用task");
            }
            return true;
        }
        return false;
    }
}
