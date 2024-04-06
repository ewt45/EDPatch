package com.eltechs.axs.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import com.eltechs.axs.Locales;
import com.eltechs.ed.R;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.PurchasableComponentsCollectionAware;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.configuration.startup.EnvironmentCustomisationParameters;
import com.eltechs.axs.configuration.startup.PerApplicationSettingsStore;
import com.eltechs.axs.payments.PurchasableComponent;
import com.eltechs.axs.payments.PurchasableComponentsCollection;
import com.eltechs.axs.widgets.actions.AbstractAction;
import com.eltechs.axs.widgets.actions.ActionGroup;
import com.eltechs.axs.widgets.popupMenu.AXSPopupMenu;
import com.eltechs.axs.xserver.ScreenInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AdvancedRunOptions<StateClass extends ApplicationStateBase<StateClass> & SelectedExecutableFileAware<StateClass> & PurchasableComponentsCollectionAware> extends FrameworkActivity<StateClass> {
    private ImageButton changeColourDepthButton;
    private ImageButton changeDefaultControlsNameButton;
    private ImageButton changeLocaleButton;
    private ImageButton changeScreenResolutionButton;
    private TextView colourDepthDisplay;
    private AXSPopupMenu colourDepthsPopupMenu;
    private TextView defaultControlsNameDisplay;
    private AXSPopupMenu defaultControlsNamesPopupMenu;
    private EnvironmentCustomisationParameters environmentCustomisationParameters;
    private TextView localeDisplay;
    private AXSPopupMenu localesPopupMenu;
    private TextView screenResolutionDisplay;
    private AXSPopupMenu screenResolutionsPopupMenu;
    private static final ScreenResolution[] TYPICAL_SCREEN_RESOLUTIONS = {
            new ScreenResolution(640, 480),
            new ScreenResolution(800, 600),
            new ScreenResolution(1024, 768),
            new ScreenResolution(1280, 720)};
    private static final int[] SUPPORTED_COLOUR_DEPTHS = {15, 16, 32};

    private String formatDefaultControlsName(String name) {
        return name;
    }

    private String formatLocale(String str) {
        return str;
    }

    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.advanced_run_options);
        resizeRootViewToStandardDialogueSize();
        this.environmentCustomisationParameters = getApplicationState().getSelectedExecutableFile().getEnvironmentCustomisationParameters();
        lookupWidgets();
        createPopupMenus();
    }

    @Override // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        fillData();
    }

    private void lookupWidgets() {
        this.screenResolutionDisplay = findViewById(R.id.aro_screen_resolution);
        this.changeScreenResolutionButton = findViewById(R.id.aro_change_screen_resolution);
        this.colourDepthDisplay = findViewById(R.id.aro_colour_depth);
        this.changeColourDepthButton = findViewById(R.id.aro_change_colour_depth);
        this.localeDisplay = findViewById(R.id.aro_locale);
        this.changeLocaleButton = findViewById(R.id.aro_change_locale);
        this.defaultControlsNameDisplay = findViewById(R.id.aro_default_controls_name);
        this.changeDefaultControlsNameButton = findViewById(R.id.aro_change_default_controls_name);
    }

    private void createPopupMenus() {
        createScreenResolutionsPopupMenu();
        createColourDepthsPopupMenu();
        createLocalesPopupMenu();
        createDefaultControlsNamePopupMenu();
    }

    private void createScreenResolutionsPopupMenu() {
        ActionGroup actionGroup = new ActionGroup();
        actionGroup.setExclusive(true);
        for (ScreenResolution screenResolution : TYPICAL_SCREEN_RESOLUTIONS) {
            actionGroup.add(new ChangeScreenResolution(screenResolution));
        }
        this.screenResolutionsPopupMenu = new AXSPopupMenu(this, this.changeScreenResolutionButton, Gravity.RIGHT);
        this.screenResolutionsPopupMenu.add(actionGroup);
    }

    private void createColourDepthsPopupMenu() {
        ActionGroup actionGroup = new ActionGroup();
        actionGroup.setExclusive(true);
        for (int i : SUPPORTED_COLOUR_DEPTHS) {
            actionGroup.add(new ChangeColourDepth(i));
        }
        this.colourDepthsPopupMenu = new AXSPopupMenu(this, this.changeColourDepthButton, Gravity.RIGHT);
        this.colourDepthsPopupMenu.add(actionGroup);
    }

    private void createLocalesPopupMenu() {
        ActionGroup actionGroup = new ActionGroup();
        actionGroup.setExclusive(true);
        for (String str : Locales.getSupportedLocales()) {
            actionGroup.add(new ChangeLocale(str));
        }
        this.localesPopupMenu = new AXSPopupMenu(this, this.changeLocaleButton, 5);
        this.localesPopupMenu.add(actionGroup);
    }

    private List<String> getSupportedDefaultControlsNames() {
        PurchasableComponentsCollection coll = getApplicationState().getPurchasableComponentsCollection();
        ArrayList<String> arrayList = new ArrayList<>();
        for (PurchasableComponent purchasableComponent : coll.getPurchasableComponents()) {
            arrayList.add(purchasableComponent.getName());
        }
        return arrayList;
    }

    private void createDefaultControlsNamePopupMenu() {
        ActionGroup actionGroup = new ActionGroup();
        actionGroup.setExclusive(true);
        for (String str : getSupportedDefaultControlsNames()) {
            actionGroup.add(new ChangeDefaultControlsName(str));
        }
        this.defaultControlsNamesPopupMenu = new AXSPopupMenu(this, this.changeDefaultControlsNameButton, Gravity.RIGHT);
        this.defaultControlsNamesPopupMenu.add(actionGroup);
    }

    private void fillData() {
        EnvironmentCustomisationParameters envParams = getApplicationState().getSelectedExecutableFile().getEnvironmentCustomisationParameters();
        ScreenInfo screenInfo = envParams.getScreenInfo();
        this.screenResolutionDisplay.setText(formatScreenResolution(screenInfo.widthInPixels, screenInfo.heightInPixels));
        this.colourDepthDisplay.setText(formatBpp(screenInfo.depth));
        this.localeDisplay.setText(envParams.getLocaleName());
        this.defaultControlsNameDisplay.setText(envParams.getDefaultControlsName());
    }

    public void onChangeScreenResolutionClicked(View view) {
        this.screenResolutionsPopupMenu.show();
    }

    public void onChangeColourDepthClicked(View view) {
        this.colourDepthsPopupMenu.show();
    }

    public void onChangeLocaleClicked(View view) {
        this.localesPopupMenu.show();
    }

    public void onChangeDefaultControlsNameClicked(View view) {
        this.defaultControlsNamesPopupMenu.show();
    }

    public void onResetToDefaultsClicked(View view) {
        this.environmentCustomisationParameters.copyFrom(getApplicationState().getSelectedExecutableFile().getDefaultEnvironmentCustomisationParameters());
        fillData();
    }

    public void onOKClicked(View view) {
        try {
            PerApplicationSettingsStore.get(getApplicationState().getSelectedExecutableFile()).storeDetectedExecutableFileConfiguration();
        } catch (IOException ignored) {
        }
        finish();
    }

    private String formatScreenResolution(int w, int h) {
        return String.format("%dx%d", w, h);
    }

    private String formatBpp(int i) {
        return String.format("%d bpp", i);
    }

    private static class ScreenResolution {
        public final int height;
        public final int width;

        ScreenResolution(int i, int i2) {
            this.width = i;
            this.height = i2;
        }
    }

    private class ChangeScreenResolution extends AbstractAction {
        private final ScreenResolution resolution;

        ChangeScreenResolution(ScreenResolution screenResolution) {
            super(formatScreenResolution(screenResolution.width, screenResolution.height), true);
            this.resolution = screenResolution;
        }

        @Override // com.eltechs.axs.widgets.actions.AbstractAction, com.eltechs.axs.widgets.actions.Action
        public boolean isChecked() {
            ScreenInfo screenInfo = environmentCustomisationParameters.getScreenInfo();
            return this.resolution.width == screenInfo.widthInPixels && this.resolution.height == screenInfo.heightInPixels;
        }

        @Override // com.eltechs.axs.widgets.actions.Action
        public void run() {
            environmentCustomisationParameters.setScreenInfo(new ScreenInfo(this.resolution.width, this.resolution.height, this.resolution.width / 10, this.resolution.height / 10, AdvancedRunOptions.this.environmentCustomisationParameters.getScreenInfo().depth));
            fillData();
        }
    }

    private class ChangeColourDepth extends AbstractAction {
        private final int bpp;

        ChangeColourDepth(int bpp) {
            super(formatBpp(bpp), true);
            this.bpp = bpp;
        }

        @Override // com.eltechs.axs.widgets.actions.AbstractAction, com.eltechs.axs.widgets.actions.Action
        public boolean isChecked() {
            return this.bpp == environmentCustomisationParameters.getScreenInfo().depth;
        }

        @Override // com.eltechs.axs.widgets.actions.Action
        public void run() {
            ScreenInfo screenInfo = environmentCustomisationParameters.getScreenInfo();
            environmentCustomisationParameters.setScreenInfo(new ScreenInfo(screenInfo.widthInPixels, screenInfo.heightInPixels, screenInfo.widthInMillimeters, screenInfo.heightInMillimeters, this.bpp));
            fillData();
        }
    }

    private class ChangeLocale extends AbstractAction {
        private final String locale;

        ChangeLocale(String locale) {
            super(formatLocale(locale), true);
            this.locale = locale;
        }

        @Override // com.eltechs.axs.widgets.actions.AbstractAction, com.eltechs.axs.widgets.actions.Action
        public boolean isChecked() {
            return this.locale.equals(environmentCustomisationParameters.getLocaleName());
        }

        @Override // com.eltechs.axs.widgets.actions.Action
        public void run() {
            environmentCustomisationParameters.setLocaleName(this.locale);
            fillData();
        }
    }

    private class ChangeDefaultControlsName extends AbstractAction {
        private final String defaultControlsName;

        ChangeDefaultControlsName(String controlsName) {
            super(formatDefaultControlsName(controlsName), true);
            this.defaultControlsName = controlsName;
        }

        @Override // com.eltechs.axs.widgets.actions.AbstractAction, com.eltechs.axs.widgets.actions.Action
        public boolean isChecked() {
            return this.defaultControlsName.equals(environmentCustomisationParameters.getDefaultControlsName());
        }

        @Override // com.eltechs.axs.widgets.actions.Action
        public void run() {
            environmentCustomisationParameters.setDefaultControlsName(this.defaultControlsName);
            fillData();
        }
    }
}