package com.eltechs.axs;

import android.content.SharedPreferences;

/* loaded from: classes.dex */
public class ShadowApplicationConfigurationAccessor extends ApplicationConfigurationAccessor {
    private static final String SHADOW_PARAM_USAGE_PREFIX = "axs_shadow_usage_";
    private final String keyName;

    public ShadowApplicationConfigurationAccessor(String str) {
        this.keyName = SHADOW_PARAM_USAGE_PREFIX + str;
    }

    public boolean isUsageShown() {
        return this.prefs.getBoolean(this.keyName, false);
    }

    public void setUsageShown(boolean shown) {
        SharedPreferences.Editor edit = this.prefs.edit();
        edit.putBoolean(this.keyName, shown);
        edit.commit();
    }
}