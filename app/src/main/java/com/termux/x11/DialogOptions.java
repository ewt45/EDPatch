package com.termux.x11;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.QH;

class DialogOptions {
      static LinearLayout getView(BaseFragment baseFragment){
         Context c = baseFragment.getContext();
         LinearLayout linearRoot = new LinearLayout(c);
         linearRoot.setOrientation(LinearLayout.VERTICAL);
         LinearLayout.LayoutParams paddingParams = new LinearLayout.LayoutParams(-2, -2);
         paddingParams.topMargin = AndroidHelpers.dpToPx(20);

         //https://4pda.to/forum/index.php?showtopic=992239&st=25880#entry121709333
         TextView tv1 = new TextView(c);
         tv1.setText("x11服务端用于显示图形画面，由于exa实现的x11服务端比较简陋，无法支持dxvk等，所以将termux:x11实现的x11服务端移植到exa内，以期实现更好的渲染效果。感谢termux:x11作者twaik的帮助。");
         linearRoot.addView(tv1);

         CheckBox checkLegacyDraw = new CheckBox(c);
         checkLegacyDraw.setText("-legacy-drawing");
         checkLegacyDraw.setOnCheckedChangeListener((buttonView, isChecked) -> QH.getPreference().edit().putBoolean(CmdEntryPoint.PREF_KEY_LEGACY_DRAW,isChecked).apply());
         linearRoot.addView(checkLegacyDraw,paddingParams);
         linearRoot.addView(getDescriptionTextView(c,"如果启动容器后只显示黑屏和一个箭头鼠标，勾选此选项可以解决问题，但是渲染效率会变低。"));

         return linearRoot;
     }

    /**
     * 创建一个对应某个选项的说明文字
     *
     * @param text 文字
     * @return textview
     */
     static TextView getDescriptionTextView(Context c, String text) {
        TextView textView = new TextView(c);
        textView.setText(text);
        textView.setTextIsSelectable(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        params.setMarginStart(AndroidHelpers.dpToPx(16));
        textView.setLayoutParams(params);
        return textView;
    }
}
