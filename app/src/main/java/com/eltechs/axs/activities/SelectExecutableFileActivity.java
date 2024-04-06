package com.eltechs.axs.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.iwgang.countdownview.CountdownView;
import com.eltechs.axs.AppConfig;
import com.eltechs.ed.R;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.PurchasableComponentsCollectionAware;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.configuration.startup.AvailableExecutableFiles;
import com.eltechs.axs.configuration.startup.DetectedExecutableFile;
import com.eltechs.axs.configuration.startup.actions.SelectExecutableFile;
import com.eltechs.axs.container.annotations.Autowired;
import com.eltechs.axs.container.annotations.PostAdd;
import com.eltechs.axs.firebase.FAHelper;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.PromoHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections4.collection.CompositeCollection;
import org.apache.commons.lang3.StringEscapeUtils;

/* loaded from: classes.dex */
public class SelectExecutableFileActivity<StateClass extends ApplicationStateBase<StateClass> & PurchasableComponentsCollectionAware & SelectedExecutableFileAware<StateClass>> extends FrameworkActivity<StateClass> {
    private static final int REQUEST_CODE_CHOOSE_CUSTOMIZATION_PACKAGE = 10001;
    private static final int REQUEST_CODE_SET_RUN_OPTIONS = 10002;
    private static final int REQUEST_CODE_ADD_MORE_GAMES = 10003;
    private static final int REQUEST_CODE_SHOW_BUY_WINDOW = 10004;
    private static final int REQUEST_CODE_SHOW_BUY_PROMO_WINDOW = 10005;
    public static final String TAG = "SelectExecutableFileActivity";
    private List<DetectedExecutableFile<StateClass>> otherExecutableFiles;
    private boolean showOthers = false;
    private List<DetectedExecutableFile<StateClass>> supportedExecutableFiles;

    public SelectExecutableFileActivity() {
        enableLogging(false);
    }

    @Autowired
    private void setAvailableExecutableFiles(AvailableExecutableFiles<StateClass> availableExecutableFiles) {
        this.supportedExecutableFiles = availableExecutableFiles.getSupportedFiles();
        this.otherExecutableFiles = availableExecutableFiles.getOtherFiles();
        if (this.supportedExecutableFiles.isEmpty()) {
            this.showOthers = true;
        }
    }

    @PostAdd
    private void checkConsistency() {
        Assert.state(this.supportedExecutableFiles != null && this.otherExecutableFiles != null, "The collection of available executable files must be defined before calling SelectExecutableFileActivity.");
    }

    @SuppressLint("InlinedApi")
    private void processRemindActions() {
        AppConfig appConfig = AppConfig.getInstance(this);
        Date time = Calendar.getInstance().getTime();
        findViewById(R.id.promo_layout).setVisibility(View.GONE);
        if (appConfig.getExeFoundTime().getTime() == 0) {
            appConfig.setExeFoundTime(time);
            FAHelper.logExeFoundEvent(this);
        }
        if (appConfig.getBuyOrSubscribeTime().getTime() != 0) {
            return;
        }
        if (getApplicationState().getPurchasableComponentsCollection().isPrepaidPeriodActive()) {
            appConfig.setBuyOrSubscribeTime(time);
            return;
        }
        if (PromoHelper.isActive(this)) {
            long msecToEnd = PromoHelper.getMsecToEnd(this);
            if (msecToEnd > 5000) {
                findViewById(R.id.promo_layout).setVisibility(View.VISIBLE);
                ((CountdownView) findViewById(R.id.promo_timer)).start(msecToEnd);
                findViewById(R.id.promo_layout).setOnClickListener(view -> startActivityForResult(REQUEST_CODE_SHOW_BUY_PROMO_WINDOW, BuyPromoActivity.class));
                TextView textView = findViewById(R.id.promo_text);
                textView.setText(String.format(getString(R.string.sef_promo_text), PromoHelper.getDiscount(this)));
                TextViewCompat.setAutoSizeTextTypeWithDefaults(textView, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                ((ImageView) findViewById(R.id.promo_image)).setImageResource(PromoHelper.getDiscountImageRes(this));
                if (appConfig.getPromoNextRemindTime().getTime() != 0 && time.compareTo(appConfig.getPromoNextRemindTime()) >= 0) {
                    appConfig.setPromoNextRemindTime(new Date(0L));
                    startActivityForResult(REQUEST_CODE_SHOW_BUY_PROMO_WINDOW, BuyPromoActivity.class);
                }
            }
        }
        if (appConfig.getTrialNextRemindTime().getTime() == 0 || time.compareTo(appConfig.getTrialNextRemindTime()) < 0) {
            return;
        }
        appConfig.setTrialNextRemindTime(new Date(0L));
        startActivityForResult(REQUEST_CODE_SHOW_BUY_WINDOW, CustomizationPackageInformerActivity.class);
    }

    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_executable_file);
        processRemindActions();
    }

    @Override // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        resetList();
    }

    private void resetList() {
        ((ListView) findViewById(R.id.list_of_available_executable_files)).setAdapter(new Adapter());
    }

    private class Adapter extends BaseAdapter {
        private final int commonPathPrefixLength;
        private final View[] myViews = new View[getCount()];

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int i) {
            return -1;
        }

        public Adapter() {
            ArrayList<String> arrayList = new ArrayList<>(supportedExecutableFiles.size() + otherExecutableFiles.size());
            //CompositeCollection.of没这个函数了，改成new的
            for (DetectedExecutableFile<StateClass> detectedExecutableFile : new CompositeCollection<>(supportedExecutableFiles, SelectExecutableFileActivity.this.otherExecutableFiles)) {
                arrayList.add(detectedExecutableFile.getParentDir().getAbsolutePath());
            }
            this.commonPathPrefixLength = calculateCommonPrefixLength(arrayList);
        }

        private int calculateCommonPrefixLength(List<String> list) {
            if (list.isEmpty()) {
                return 0;
            }
            String[] split = list.get(0).split("/");
            int length = split.length - 1;
            for (String str : list) {
                String[] split2 = str.split("/");
                length = Math.min(length, split2.length - 1);
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    } else if (!split[i].equals(split2[i])) {
                        length = i;
                        break;
                    } else {
                        i++;
                    }
                }
            }
            int i2 = 0;
            for (int i3 = 0; i3 < length; i3++) {
                i2 += split[i3].length() + 1;
            }
            return i2;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            int tmp = otherExecutableFiles.isEmpty() ? 0 : 1;
            return showOthers
                    ? (supportedExecutableFiles.size() + otherExecutableFiles.size() + 1) :
                    supportedExecutableFiles.size() + tmp ^ 1; //什么玩意加boolean，先这样吧
//                    supportedExecutableFiles.size() + (!otherExecutableFiles.isEmpty());
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (this.myViews[i] != null) {
                return this.myViews[i];
            }
            boolean enable = false;
            View rootView = getLayoutInflater().inflate(i != supportedExecutableFiles.size() ? R.layout.select_executable_file_elem : R.layout.select_executable_file_separator, viewGroup, false);
            ImageView iconView = rootView.findViewById(R.id.sef_file_icon);
            TextView description = rootView.findViewById(R.id.sef_file_description);
            View menu = rootView.findViewById(R.id.sef_show_configuration_menu);
            if (i == SelectExecutableFileActivity.this.supportedExecutableFiles.size()) {
                description.setText(showOthers ? R.string.hide_files : R.string.more_files);
                installMoreLessOptionButtonListener(rootView);
                if (!supportedExecutableFiles.isEmpty() && !otherExecutableFiles.isEmpty()) {
                    enable = true;
                }
                description.setEnabled(enable);
                rootView.setEnabled(enable);
            } else {
                enable = i < supportedExecutableFiles.size();
                DetectedExecutableFile<StateClass> file = enable ? supportedExecutableFiles.get(i) : otherExecutableFiles.get((i - supportedExecutableFiles.size()) - 1);
                iconView.setImageBitmap(file.getIcon());
                description.setText(generateFileDescription(file));
                if (enable) {
                    installSupportedFileButtonListener(file, iconView, rootView, menu);
                } else {
                    file.setUserSelectedCustomizationPackage(null);
                    installUnsupportedFileButtonListener(file, iconView, rootView, menu);
                }
            }
            this.myViews[i] = rootView;
            return rootView;
        }

        private Spanned generateFileDescription(DetectedExecutableFile<StateClass> detectedExecutableFile) {
            return Html.fromHtml(String.format("<b>%s</b><br>in %s", StringEscapeUtils.escapeHtml4(detectedExecutableFile.getFileName()), StringEscapeUtils.escapeHtml4(detectedExecutableFile.getParentDir().getAbsolutePath().substring(this.commonPathPrefixLength))));
        }

        private void installSupportedFileButtonListener(final DetectedExecutableFile<StateClass> file, View iconView, View rootView, View menu) {
            View.OnClickListener selectListener = view -> {
                getApplicationState().setSelectedExecutableFile(file);
                if (getApplicationState().getPurchasableComponentsCollection().isPrepaidPeriodActive()) {
                    signalUserInteractionFinished(SelectExecutableFile.UserRequestedAction.EXECUTABLE_FILE_SELECTED);
                } else
                    startActivityForResult(REQUEST_CODE_CHOOSE_CUSTOMIZATION_PACKAGE, CustomizationPackageInformerActivity.class);
            };
            View.OnClickListener menuListener = view -> {
                getApplicationState().setSelectedExecutableFile(file);
                startActivityForResult(REQUEST_CODE_SET_RUN_OPTIONS, AdvancedRunOptions.class);
            };
            iconView.setOnClickListener(selectListener);
            rootView.setOnClickListener(selectListener);
            menu.setOnClickListener(menuListener);
        }

        private void installUnsupportedFileButtonListener(final DetectedExecutableFile<StateClass> file, View iconView, View rootView, View menu) {
            View.OnClickListener selectListener = view -> {
                 getApplicationState().setSelectedExecutableFile(file);
                if (getApplicationState().getPurchasableComponentsCollection().isPrepaidPeriodActive()) {
                    if (file.getEffectiveCustomizationPackage() != null)
                        signalUserInteractionFinished(SelectExecutableFile.UserRequestedAction.EXECUTABLE_FILE_SELECTED);
                     else
                        startActivityForResult(REQUEST_CODE_CHOOSE_CUSTOMIZATION_PACKAGE, SelectCustomizationPackageActivity.class);
                    return;
                }
                startActivityForResult(REQUEST_CODE_CHOOSE_CUSTOMIZATION_PACKAGE, CustomizationPackageInformerActivity.class);
            };
            View.OnClickListener menuListener = view -> {
                getApplicationState().setSelectedExecutableFile(file);
                startActivityForResult(REQUEST_CODE_SET_RUN_OPTIONS, AdvancedRunOptions.class);
            };
            iconView.setOnClickListener(selectListener);
            rootView.setOnClickListener(selectListener);
            menu.setOnClickListener(menuListener);
        }

        private void installMoreLessOptionButtonListener(View rootView) {
            rootView.setOnClickListener(view2 -> {
                showOthers = !showOthers;
                resetList();
            });
        }
    }

    @Override // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (getApplicationState().getPurchasableComponentsCollection().isPrepaidPeriodActive()) {
            findViewById(R.id.promo_layout).setVisibility(View.GONE);
        }
        if (requestCode == REQUEST_CODE_CHOOSE_CUSTOMIZATION_PACKAGE && resultCode != 0) {
            signalUserInteractionFinished(SelectExecutableFile.UserRequestedAction.EXECUTABLE_FILE_SELECTED);
        } else if (requestCode == REQUEST_CODE_ADD_MORE_GAMES) {
            resetList();
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    public void onAddMoreGamesClicked(View view) {
        startActivityForResult(REQUEST_CODE_ADD_MORE_GAMES, AddGameWizard.class);
    }

    public void onRequestRescanClicked(View view) {
        signalUserInteractionFinished(SelectExecutableFile.UserRequestedAction.FULL_SCAN_REQUESTED);
    }
}