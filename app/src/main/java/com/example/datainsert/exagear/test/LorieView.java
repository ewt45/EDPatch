package com.example.datainsert.exagear.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.SurfaceView;

import java.util.regex.PatternSyntaxException;

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
//            SharedPreferences sp = getContext().getSharedPreferences("com.eltechs.ed.CONTAINER_CONFIG_0", Context.MODE_PRIVATE);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean shouldResize = sp.getBoolean("should_full_screen_with_ratio", true); //full_screen_with_ratio是字符串类型了

            float ratio; //宽除以高
            String[] res = null;
            switch (sp.getString("displayResolutionMode", "native")) {
                case "exact": {
                    res = sp.getString("displayResolutionExact", "1280x1024").split("x");
                    break;
                }
                case "custom": {
                    try {
                        res = sp.getString("displayResolutionCustom", "1280x1024").split("x");
                    } catch (NumberFormatException | PatternSyntaxException ignored) {
                    }
                    break;
                }
                default:
                    break;
            }

            ratio = res != null ? Integer.parseInt(res[0]) * 1.0f / Integer.parseInt(res[1]) : 0f;

            if (ratio != 0 && shouldResize) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                //左右黑边
                if (width * 1f / height > ratio) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (height * ratio), MeasureSpec.EXACTLY);
                }
                //上下黑边
                else {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width / ratio), MeasureSpec.EXACTLY);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
