package com.ewt45.patchapp.unused;

import android.graphics.drawable.Drawable;

public class DataBean {

    Drawable imageId;
    String appName;

    public DataBean() {

    }

    public DataBean(Drawable imageId, String appName) {
        this.imageId = imageId;
        this.appName = appName;
    }

    public Drawable getImageId() {
        return imageId;
    }

    public void setImageId(Drawable imageId) {
        this.imageId = imageId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}


