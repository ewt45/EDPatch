package com.eltechs.axs.widgets.helpers;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class ButtonHelpers {
    public static ImageButton createRegularImageButton(Activity activity, int height, int width, int imgRes) {
        ImageButton imageButton = new ImageButton(activity);
        imageButton.setImageResource(imgRes);
        imageButton.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        imageButton.setMinimumHeight(height);
        imageButton.setMaxWidth(width);
        imageButton.setMaxHeight(height);
        imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageButton;
    }
}