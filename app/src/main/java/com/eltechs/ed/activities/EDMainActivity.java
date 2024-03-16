package com.eltechs.ed.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.eltechs.axs.AppConfig;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.ed.ContainerPackage;
import com.eltechs.ed.InstallRecipe;
import com.eltechs.ed.R;
import com.eltechs.ed.XDGLink;
import com.eltechs.ed.fragments.ChooseFileFragment;
import com.eltechs.ed.fragments.ChoosePackagesDFragment;
import com.eltechs.ed.fragments.ChooseRecipeFragment;
import com.eltechs.ed.fragments.ChooseXDGLinkFragment;
import com.eltechs.ed.fragments.ContainerRunGuideDFragment;
import com.eltechs.ed.fragments.ContainerSettingsFragment;
import com.eltechs.ed.fragments.ManageContainersFragment;
import com.eltechs.ed.guestContainers.GuestContainer;
import com.eltechs.ed.startupActions.StartGuest;
import com.eltechs.ed.startupActions.WDesktop;
import com.example.datainsert.exagear.FAB.FabMenu;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.CustomControls;
import com.example.datainsert.exagear.virgloverlay.OverlayBuildUI;

import java.io.File;
import java.util.List;

/* loaded from: classes.dex */
public class EDMainActivity<StateClass extends ApplicationStateBase<StateClass>> extends FrameworkActivity<StateClass> implements ChooseRecipeFragment.OnRecipeSelectedListener, ChooseFileFragment.OnFileSelectedListener, ChooseXDGLinkFragment.OnXDGLinkSelectedListener, ManageContainersFragment.OnManageContainersActionListener, ChoosePackagesDFragment.OnPackagesSelectedListener, ContainerRunGuideDFragment.OnContRunGuideResListener {
    private static final String FRAGMENT_TAG_CHOOSE_FILE = "CHOOSE_FILE";
    private static final String FRAGMENT_TAG_CONTAINER_PROP = "CONTAINER_PROP";
    private static final String FRAGMENT_TAG_DESKTOP = "DESKTOP";
    private static final String FRAGMENT_TAG_INSTALL_NEW = "INSTALL_NEW";
    private static final String FRAGMENT_TAG_MANAGE_CONTAINERS = "MANAGE_CONTAINERS";
    private static final String FRAGMENT_TAG_START_MENU = "START_MENU";
    private static final int ON_START_ACTION_SHOW_MANAGE_CONTAINERS = 0;
    private static final String TAG = "EDMainActivity";
    private static final File mUserAreaDir = new File(AndroidHelpers.getMainSDCard(), "Exagear");
    private AppConfig mAppCfg = AppConfig.getInstance(this);
    private GuestContainer mChoosenCont;
    private XDGLink mChoosenXDGLink;
    private InstallRecipe mChosenRecipe;
    private DrawerLayout mDrawerLayout;
    private boolean mIsHomeActionBack;
    private NavigationView mNavigationView;


    @Override
    // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ed_main);
        this.mDrawerLayout = findViewById(R.id.ed_main_drawer);
        this.mNavigationView = findViewById(R.id.ed_main_nav_view);
        NavigationItemSelectedListener navigationItemSelectedListener = new NavigationItemSelectedListener();
        this.mNavigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        setSupportActionBar(findViewById(R.id.ed_main_toolbar));
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        getSupportFragmentManager().addOnBackStackChangedListener(new BackStackChangedListener());
        if (bundle == null) {
            Integer eDMainOnStartAction = this.mAppCfg.getEDMainOnStartAction();
            navigationItemSelectedListener.onNavigationItemSelected(this.mNavigationView.getMenu().findItem(R.id.ed_main_menu_desktop));
            if (eDMainOnStartAction.intValue() == 0) {
                navigationItemSelectedListener.onNavigationItemSelected(this.mNavigationView.getMenu().findItem(R.id.ed_main_menu_manage_containers));
            }
            this.mAppCfg.setEDMainOnStartAction(-1);

//            UiThread.postDelayed(1250L, () -> RateAppDialog.checkCondAndShow(EDMainActivity.this));
        }
        new OverlayBuildUI(this);
        new FabMenu(this);
    }

    @Override
    // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        changeUIByCurFragment();
    }

    private void setHomeIsActionBack(boolean z) {
        this.mIsHomeActionBack = z;
        getSupportActionBar().setHomeAsUpIndicator(this.mIsHomeActionBack ? 0 : R.drawable.ic_menu_24dp);
    }

    /* JADX WARN: Code restructure failed: missing block: B:78:0x006d, code lost:
        if (r0.equals(com.eltechs.ed.activities.EDMainActivity.FRAGMENT_TAG_DESKTOP) != false) goto L22;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void changeUIByCurFragment() {
        String tag = getSupportFragmentManager().findFragmentById(R.id.ed_main_fragment_container).getTag();
        if (tag == null) {
            setHomeIsActionBack(false);
            return;
        }
        setHomeIsActionBack(tag.equals(FRAGMENT_TAG_CONTAINER_PROP) || tag.equals(FRAGMENT_TAG_CHOOSE_FILE));

        switch (tag) {
            case FRAGMENT_TAG_DESKTOP:
                this.mNavigationView.setCheckedItem(R.id.ed_main_menu_desktop);
                return;
            case FRAGMENT_TAG_START_MENU:
                this.mNavigationView.setCheckedItem(R.id.ed_main_menu_start_menu);
                return;
            case FRAGMENT_TAG_INSTALL_NEW:
                this.mNavigationView.setCheckedItem(R.id.ed_main_menu_install_new);
                return;
            case FRAGMENT_TAG_MANAGE_CONTAINERS:
                this.mNavigationView.setCheckedItem(R.id.ed_main_menu_manage_containers);
                return;
            default:
                return;
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            if (this.mIsHomeActionBack) {
                getSupportFragmentManager().popBackStack();
                return true;
            }
            this.mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.eltechs.ed.fragments.ChooseRecipeFragment.OnRecipeSelectedListener
    public void onRecipeSelected(InstallRecipe installRecipe) {
        this.mChosenRecipe = installRecipe;
        ChooseFileFragment chooseFileFragment = new ChooseFileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ChooseFileFragment.ARG_ROOT_PATH, mUserAreaDir.getAbsolutePath());
        bundle.putString(ChooseFileFragment.ARG_DOWNLOAD_URL, this.mChosenRecipe.getDownloadURL());
        chooseFileFragment.setArguments(bundle);
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.ed_main_fragment_container, chooseFileFragment, FRAGMENT_TAG_CHOOSE_FILE);
        beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        beginTransaction.addToBackStack(null);
        beginTransaction.commit();
    }

    /* JADX WARN: Type inference failed for: r4v1, types: [com.eltechs.axs.applicationState.ApplicationStateBase] */
    @Override // com.eltechs.ed.fragments.ChooseFileFragment.OnFileSelectedListener
    public void onFileSelected(String str) {
        getApplicationState().getStartupActionsCollection().addAction(new StartGuest(new StartGuest.InstallApp(null, str, this.mChosenRecipe)));
        signalUserInteractionFinished(WDesktop.UserRequestedAction.GO_FURTHER);
    }

    @Override // com.eltechs.ed.fragments.ChooseXDGLinkFragment.OnXDGLinkSelectedListener
    public void onXDGLinkSelected(XDGLink xDGLink) {
        this.mChoosenXDGLink = xDGLink;
        GuestContainer guestContainer = xDGLink.guestCont;
        if (guestContainer != null && guestContainer.mConfig.getRunGuide() != null && !guestContainer.mConfig.getRunGuide().isEmpty() && !guestContainer.mConfig.getRunGuideShown().booleanValue()) {
            ContainerRunGuideDFragment.createDialog(guestContainer, false).show(getSupportFragmentManager(), "CONT_RUN_GUIDE");
        } else {
            startXDGLink(xDGLink);
        }
    }

    @Override // com.eltechs.ed.fragments.ContainerRunGuideDFragment.OnContRunGuideResListener
    public void onContRunGuideRes(boolean z) {
        if (this.mChoosenXDGLink != null) {
            startXDGLink(this.mChoosenXDGLink);
        }
    }

    /* JADX WARN: Type inference failed for: r3v1, types: [com.eltechs.axs.applicationState.ApplicationStateBase] */
    private void startXDGLink(XDGLink xDGLink) {
        getApplicationState().getStartupActionsCollection().addAction(new StartGuest(new StartGuest.RunXDGLink(xDGLink)));
        signalUserInteractionFinished(WDesktop.UserRequestedAction.GO_FURTHER);
    }

    /* JADX WARN: Type inference failed for: r3v1, types: [com.eltechs.axs.applicationState.ApplicationStateBase] */
    @Override // com.eltechs.ed.fragments.ManageContainersFragment.OnManageContainersActionListener
    public void onManageContainersRunExplorer(GuestContainer guestContainer) {
        getApplicationState().getStartupActionsCollection().addAction(new StartGuest(new StartGuest.RunExplorer(guestContainer)));
        signalUserInteractionFinished(WDesktop.UserRequestedAction.GO_FURTHER);
    }

    @Override // com.eltechs.ed.fragments.ManageContainersFragment.OnManageContainersActionListener
    public void onManageContainersInstallPackages(GuestContainer guestContainer) {
        this.mChoosenCont = guestContainer;
        new ChoosePackagesDFragment().show(getSupportFragmentManager(), "CHOOSE_PACKAGES");
    }

    /* JADX WARN: Type inference failed for: r3v1, types: [com.eltechs.axs.applicationState.ApplicationStateBase] */
    @Override // com.eltechs.ed.fragments.ChoosePackagesDFragment.OnPackagesSelectedListener
    public void onPackagesSelected(List<ContainerPackage> list) {
        getApplicationState().getStartupActionsCollection().addAction(new StartGuest(new StartGuest.InstallPackage(this.mChoosenCont, list)));
        this.mAppCfg.setEDMainOnStartAction(0);
        signalUserInteractionFinished(WDesktop.UserRequestedAction.GO_FURTHER);
    }

    @Override // com.eltechs.ed.fragments.ManageContainersFragment.OnManageContainersActionListener
    public void onManageContainerSettingsClick(GuestContainer guestContainer) {
        this.mChoosenCont = guestContainer;
        ContainerSettingsFragment containerSettingsFragment = new ContainerSettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("CONT_ID", guestContainer.mId);
        containerSettingsFragment.setArguments(bundle);
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.ed_main_fragment_container, containerSettingsFragment, FRAGMENT_TAG_CONTAINER_PROP);
        beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        beginTransaction.addToBackStack(null);
        beginTransaction.commit();
    }

    /* loaded from: classes.dex */
    private class NavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @SuppressLint("NonConstantResourceId")
        @Override // android.support.design.widget.NavigationView.OnNavigationItemSelectedListener
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            Fragment fragment;
            String str;
            boolean z = false;
            switch (menuItem.getItemId()) {
                case R.id.ed_main_menu_desktop /* 2131296369 */:
                    menuItem.setChecked(true);
                    fragment = new ChooseXDGLinkFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(ChooseXDGLinkFragment.ARG_IS_START_MENU, false);
                    fragment.setArguments(bundle);
                    str = EDMainActivity.FRAGMENT_TAG_DESKTOP;
                    break;
                case R.id.ed_main_menu_help /* 2131296370 */:
                    EDMainActivity.this.startActivity(EDHelpActivity.class);
                    fragment = null;
                    str = null;
                    break;
                case R.id.ed_main_menu_install_new /* 2131296371 */:
                    menuItem.setChecked(true);
                    fragment = new ChooseRecipeFragment();
                    str = EDMainActivity.FRAGMENT_TAG_INSTALL_NEW;
                    break;
                case R.id.ed_main_menu_manage_containers /* 2131296372 */:
                    menuItem.setChecked(true);
                    fragment = new ManageContainersFragment();
                    str = EDMainActivity.FRAGMENT_TAG_MANAGE_CONTAINERS;
                    break;
                case R.id.ed_main_menu_start_menu /* 2131296373 */:
                    menuItem.setChecked(true);
                    fragment = new ChooseXDGLinkFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putBoolean(ChooseXDGLinkFragment.ARG_IS_START_MENU, true);
                    fragment.setArguments(bundle2);
                    str = EDMainActivity.FRAGMENT_TAG_START_MENU;
                    break;
                default:
                    fragment = null;
                    str = null;
                    break;
            }

            fragment = new ControlsFragment();

            if (fragment != null) {
                FragmentManager supportFragmentManager = EDMainActivity.this.getSupportFragmentManager();
                for (int i = 0; i < supportFragmentManager.getBackStackEntryCount(); i++) {
                    supportFragmentManager.popBackStack();
                }
                FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.replace(R.id.ed_main_fragment_container, fragment, str);
                beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                if (menuItem.getItemId() != R.id.ed_main_menu_desktop) {
                    beginTransaction.addToBackStack(null);
                }
                beginTransaction.commit();
                z = true;
            }
            mDrawerLayout.closeDrawers();
            return z;
        }
    }

    /* loaded from: classes.dex */
    private class BackStackChangedListener implements FragmentManager.OnBackStackChangedListener {
        @Override // android.support.v4.app.FragmentManager.OnBackStackChangedListener
        public void onBackStackChanged() {
            EDMainActivity.this.changeUIByCurFragment();
        }
    }
}