package com.example.datainsert.exagear.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class LorieView extends SurfaceView {
    public LorieView(Context context) {
        super(context);
    }

    public LorieView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LorieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LorieView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
//            SharedPreferences sp = getContext().getSharedPreferences("com.eltechs.ed.CONTAINER_CONFIG_0",Context.MODE_PRIVATE);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            String ratioStr = sp.getString("full_screen_with_ratio","");
            if(ratioStr!=null && ratioStr.contains(":") ){
                String[] strings1 = ratioStr.split(":");
                float ratio = Integer.parseInt(strings1[0])*1f/Integer.parseInt(strings1[1]);
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                //左右黑边
                if(width*1f/height > ratio){
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width/ratio), MeasureSpec.EXACTLY);
                }
                //上下黑边
                else{
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (height*ratio), MeasureSpec.EXACTLY);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
