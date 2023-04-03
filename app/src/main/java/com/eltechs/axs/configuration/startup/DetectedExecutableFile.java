package com.eltechs.axs.configuration.startup;

import android.graphics.Bitmap;
import android.support.v4.app.DialogFragment;
import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.applicationState.PurchasableComponentsCollectionAware;
import com.eltechs.axs.payments.PurchasableComponent;
import com.eltechs.axs.payments.PurchasableComponentsCollection;
import java.io.File;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class DetectedExecutableFile<StateClass> {
    private String controlsId;
    private DialogFragment controlsInfoDialog;
    private final EnvironmentCustomisationParameters defaultEnvironmentCustomisationParameters;
    private final XServerDisplayActivityInterfaceOverlay defaultUiOverlay;
    private final String description;
    private final EnvironmentCustomisationParameters environmentCustomisationParameters;
    private final String fileName;
    private final List<StartupAction<StateClass>> furtherStartupActions;
    private final Bitmap icon;
    private final File parentDir;
    private final PurchasableComponent recommendedCustomizationPackage;
    private PurchasableComponent userSelectedCustomizationPackage;

    public DetectedExecutableFile(File file, String str, Bitmap bitmap, String str2, PurchasableComponent purchasableComponent, XServerDisplayActivityInterfaceOverlay xServerDisplayActivityInterfaceOverlay, EnvironmentCustomisationParameters environmentCustomisationParameters, List<StartupAction<StateClass>> list) {
        this.parentDir = file;
        this.fileName = str;
        this.icon = bitmap;
        this.description = str2;
        this.defaultUiOverlay = xServerDisplayActivityInterfaceOverlay;
        this.recommendedCustomizationPackage = purchasableComponent;
        if (purchasableComponent != null) {
            environmentCustomisationParameters.setDefaultControlsName(purchasableComponent.getName());
        }
        this.environmentCustomisationParameters = environmentCustomisationParameters;
        this.defaultEnvironmentCustomisationParameters = new EnvironmentCustomisationParameters();
        this.defaultEnvironmentCustomisationParameters.copyFrom(environmentCustomisationParameters);
        this.furtherStartupActions = list;
        this.controlsInfoDialog = null;
    }

    public DetectedExecutableFile(EnvironmentCustomisationParameters environmentCustomisationParameters, String str, DialogFragment dialogFragment) {
        this(new File("dummyParentDir"), "dummyFileName", null, null, null, null, environmentCustomisationParameters, Collections.EMPTY_LIST);
        this.controlsId = str;
        this.controlsInfoDialog = dialogFragment;
    }

    public File getParentDir() {
        return this.parentDir;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Bitmap getIcon() {
        return this.icon;
    }

    public String getDescription() {
        return this.description;
    }

    public PurchasableComponent getRecommendedCustomizationPackage() {
        return this.recommendedCustomizationPackage;
    }

    public PurchasableComponent getUserSelectedCustomizationPackage() {
        return this.userSelectedCustomizationPackage;
    }

    public void setUserSelectedCustomizationPackage(PurchasableComponent purchasableComponent) {
        this.userSelectedCustomizationPackage = purchasableComponent;
    }

    public List<StartupAction<StateClass>> getFurtherStartupActions() {
        return this.furtherStartupActions;
    }

    public String getControlsId() {
        return this.controlsId;
    }

    public DialogFragment getControlsInfoDialog() {
        return this.controlsInfoDialog;
    }

    public PurchasableComponent getEffectiveCustomizationPackage() {
        PurchasableComponent userSelectedCustomizationPackage = getUserSelectedCustomizationPackage();
        if (userSelectedCustomizationPackage == null) {
            userSelectedCustomizationPackage = findSettingsDefaultCustomizationPackage();
        }
        return userSelectedCustomizationPackage == null ? getRecommendedCustomizationPackage() : userSelectedCustomizationPackage;
    }

    public EnvironmentCustomisationParameters getDefaultEnvironmentCustomisationParameters() {
        return this.defaultEnvironmentCustomisationParameters;
    }

    public EnvironmentCustomisationParameters getEnvironmentCustomisationParameters() {
        return this.environmentCustomisationParameters;
    }

    public XServerDisplayActivityInterfaceOverlay getDefaultUiOverlay() {
        return this.defaultUiOverlay;
    }

    private PurchasableComponent findSettingsDefaultCustomizationPackage() {
        PurchasableComponentsCollection purchasableComponentsCollection = ((PurchasableComponentsCollectionAware) Globals.getApplicationState()).getPurchasableComponentsCollection();
        String defaultControlsName = this.environmentCustomisationParameters.getDefaultControlsName();
        if (purchasableComponentsCollection != null) {
            return purchasableComponentsCollection.getPurchasableComponent(defaultControlsName);
        }
        return null;
    }
}