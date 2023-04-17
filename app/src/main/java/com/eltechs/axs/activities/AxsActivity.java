package com.eltechs.axs.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.eltechs.axs.activities.AxsDataFragment;
import com.eltechs.axs.helpers.Assert;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class AxsActivity extends AppCompatActivity {
    private static final String DATA_FRAGMENT_TAG = "data";
    private static final String RESULT_VALUE_NAME = "AxsActivityResult";
    private static final Map<Class<? extends AxsActivity>, Map<String, BufferedListenerInvoker<?>>> bufferedListenerInvokers = new HashMap();
    private String TAG;
    private AxsDataFragment dataFragment;
    private boolean wasRecreated;
    private boolean wasRecreatedInited;
    private boolean logEnabled = true;
    private boolean resumed = false;
    private final List<ActivityResultHandler> activityResultHandlers = new ArrayList();
    private int firstFreeRequestCode = 7666;

    /* loaded from: classes.dex */
    public interface ActivityResultHandler {
        boolean handleActivityResult(int i, int i2, Intent intent);
    }

    protected Dialog onCreateRetainedDialog(String str) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected AxsActivity() {
        this.TAG = "AxsActivity";
        this.TAG = getClass().getSimpleName();
        try {
            String str = (String) getClass().getField("TAG").get(this);
            if (str != null) {
                this.TAG = str;
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
//            e.printStackTrace();
        }
    }

    public final void enableLogging(boolean z) {
        this.logEnabled = z;
    }

    public final void logDebug(String str) {
        if (this.logEnabled) {
            Log.d(this.TAG, str);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        logDebug("onCreate() called");
        super.onCreate(bundle);
        logDebug(getIntent().toString());
        if (bundle != null) {
            logDebug(bundle.toString());
        }
        logDebug(getPackageName());
        logDebug("task id: " + getTaskId());
        logDebug("is root activity: " + isTaskRoot());
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        this.dataFragment = (AxsDataFragment) supportFragmentManager.findFragmentByTag(DATA_FRAGMENT_TAG);
        if (this.dataFragment == null) {
            this.dataFragment = new AxsDataFragment();
            FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
            beginTransaction.add(this.dataFragment, DATA_FRAGMENT_TAG);
            beginTransaction.commit();
            logDebug("first time activity creation");
            return;
        }
        this.wasRecreated = true;
        this.wasRecreatedInited = true;
        logDebug("activity recreation");
    }

    @Override // android.app.Activity
    protected void onRestart() {
        logDebug("onRestart() called");
        super.onRestart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        logDebug("onStart() called");
        super.onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        logDebug("onResume() called");
        super.onResume();
        this.resumed = true;
        for (Map.Entry<String, AxsDataFragment.DialogInfo> entry : this.dataFragment.tag2dialogInfo.entrySet()) {
            String key = entry.getKey();
            AxsDataFragment.DialogInfo value = entry.getValue();
            if (value.dialog == null && value.isShown) {
                showRetainedDialog(key);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        logDebug("onPause() called");
        super.onPause();
        this.resumed = false;
        for (BufferedListenerInvoker<?> bufferedListenerInvoker : getMyBufferedListenerInvokers().values()) {
            bufferedListenerInvoker.clearRealListener();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        logDebug("onStop() called");
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        logDebug("onDestroy() called");
        super.onDestroy();
        for (AxsDataFragment.DialogInfo dialogInfo : this.dataFragment.tag2dialogInfo.values()) {
            if (dialogInfo.dialog != null) {
                dialogInfo.dialog.dismiss();
                dialogInfo.dialog = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        logDebug("onActivityResult(" + i + "," + i2 + "," + intent + ") in AxsActivity called");
        for (ActivityResultHandler activityResultHandler : this.activityResultHandlers) {
            if (activityResultHandler.handleActivityResult(i, i2, intent)) {
                return;
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    public int registerActivityResultHandler(ActivityResultHandler activityResultHandler, int i) {
        int i2;
        if (i > 0) {
            i2 = this.firstFreeRequestCode;
            this.firstFreeRequestCode += i;
        } else {
            i2 = 0;
        }
        this.activityResultHandlers.add(activityResultHandler);
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected final void logDebug(String str, Object... objArr) {
        logDebug(String.format(str, objArr));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected final void logInfo(String str) {
        if (this.logEnabled) {
            Log.i(this.TAG, str);
        }
    }

    protected final void logInfo(String str, Object... objArr) {
        logInfo(String.format(str, objArr));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected final void checkUiThread() {
        Assert.state(Thread.currentThread() == Looper.getMainLooper().getThread());
    }

    public final boolean isActivityResumed() {
        return this.resumed;
    }

    protected final boolean wasRecreated() {
        Assert.state(this.wasRecreatedInited);
        return this.wasRecreated;
    }

    protected final void alert(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str);
        builder.setNeutralButton("OK", (DialogInterface.OnClickListener) null);
        String str2 = this.TAG;
        Log.d(str2, "Showing alert dialog: " + str);
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected final <T extends Serializable> void setResultEx(int i, T t) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_VALUE_NAME, t);
        setResult(i, intent);
    }

    public static <T extends Serializable> T getResultEx(Intent intent) {
        if (intent != null) {
            return (T) intent.getSerializableExtra(RESULT_VALUE_NAME);
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected <ListenerType> ListenerType createBufferedListener(String str, ListenerType listenertype) {
        Map<String, BufferedListenerInvoker<?>> myBufferedListenerInvokers = getMyBufferedListenerInvokers();
        BufferedListenerInvoker<ListenerType> bufferedListenerInvoker = (BufferedListenerInvoker<ListenerType>) myBufferedListenerInvokers.get(str);
        if (bufferedListenerInvoker != null) {
            bufferedListenerInvoker.setRealListener(listenertype);
            return null;
        }
        BufferedListenerInvoker<ListenerType> bufferedListenerInvoker2 = new BufferedListenerInvoker<>(inferListenerClass(listenertype));
        bufferedListenerInvoker2.setRealListener(listenertype);
        myBufferedListenerInvokers.put(str, bufferedListenerInvoker2);
        return (ListenerType) bufferedListenerInvoker2.getProxy();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Map<String, BufferedListenerInvoker<?>> getMyBufferedListenerInvokers() {
        Map<String, BufferedListenerInvoker<?>> map = bufferedListenerInvokers.get(getClass());
        if (map == null) {
            HashMap hashMap = new HashMap();
            bufferedListenerInvokers.put(getClass(), hashMap);
            return hashMap;
        }
        return map;
    }

    private <ListenerType> Class<ListenerType> inferListenerClass(ListenerType listenertype) {
        Class<?>[] interfaces = listenertype.getClass().getInterfaces();
        Assert.isTrue(interfaces.length == 1, String.format("The class %s is used as a listener and must implement exactly one interface.", listenertype.getClass()));
        return (Class<ListenerType>) interfaces[0];
    }

    protected final void showRetainedDialog(String str) {
        AxsDataFragment.DialogInfo dialogInfo = this.dataFragment.tag2dialogInfo.get(str);
        if (dialogInfo == null) {
            dialogInfo = new AxsDataFragment.DialogInfo();
            this.dataFragment.tag2dialogInfo.put(str, dialogInfo);
        }
        if (dialogInfo.dialog == null) {
            dialogInfo.dialog = onCreateRetainedDialog(str);
        }
        if (dialogInfo.isShown) {
            return;
        }
        dialogInfo.dialog.show();
        dialogInfo.isShown = true;
    }

    protected final void hideRetainedDialog(String str) {
        AxsDataFragment.DialogInfo dialogInfo = this.dataFragment.tag2dialogInfo.get(str);
        if (dialogInfo == null || dialogInfo.dialog == null) {
            return;
        }
        dialogInfo.dialog.hide();
        dialogInfo.isShown = false;
    }

    protected final void dismissRetainedDialog(String str) {
        AxsDataFragment.DialogInfo dialogInfo = this.dataFragment.tag2dialogInfo.get(str);
        if (dialogInfo == null || dialogInfo.dialog == null) {
            return;
        }
        dialogInfo.dialog.dismiss();
        dialogInfo.dialog = null;
        dialogInfo.isShown = false;
    }
}