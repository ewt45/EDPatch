package com.eltechs.axs.payments.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.UiThread;
import com.eltechs.axs.helpers.iab.IabHelper;
import com.eltechs.axs.helpers.iab.IabResult;
import com.eltechs.axs.helpers.iab.Inventory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class IabIpcService extends Service {
    private Intent intent;
    private int startId;
    private IabHelper iabHelper = null;
    IabHelper.QueryInventoryFinishedListener gotInventoryListener = (iabResult, inventory) -> {
        UiThread.check();
        Assert.notNull(IabIpcService.this.iabHelper);
        sendResponseAndStopSelf(iabResult.isFailure()
                ? Collections.emptyList()
                : inventory.getAllOwnedSkus());
    };

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        IabIpc.logDebug("IabIpcService.onStartCommand() in " + getBaseContext().getPackageName());
        this.startId = startId;
        this.intent = intent;
        Assert.state(this.iabHelper == null, "Only one processing IabIpcRequest supported");
        UiThread.check();
        this.iabHelper = new IabHelper(getBaseContext(), "");
        this.iabHelper.enableDebugLogging(false);
        this.iabHelper.startSetup(iabResult -> {
            UiThread.check();
            Assert.notNull(IabIpcService.this.iabHelper);
            if (!iabResult.isSuccess()) {
                cleanup();
                sendResponseAndStopSelf(Collections.emptyList());
                return;
            }
            iabHelper.queryInventoryAsync(false, IabIpcService.this.gotInventoryListener);
        });
        return Service.START_REDELIVER_INTENT;
    }

    private void cleanup() {
        if (this.iabHelper != null) {
            this.iabHelper.dispose();
            this.iabHelper = null;
        }
    }

    private void sendResponseAndStopSelf(List<String> list) {
        String stringExtra = this.intent.getStringExtra("REQUEST_EXTRA_SOURCE_PKG_NAME");
        int intExtra = this.intent.getIntExtra("REQUEST_EXTRA_REQID", 0);
        Intent intent = new Intent();
        intent.setClassName(stringExtra, IabIpcBroadcastReceiver.class.getName());
        intent.putStringArrayListExtra(IabIpc.RESPONSE_EXTRA_SKU_LIST, new ArrayList<>(list));
        intent.putExtra(IabIpc.RESPONSE_EXTRA_REQID, intExtra);
        sendBroadcast(intent, IabIpc.PERMISSION_NAME);
        stopSelf(this.startId);
    }

    @Override // android.app.Service
    public void onCreate() {
        IabIpc.logDebug("IabIpcService.onCreate() in " + getBaseContext().getPackageName());
    }

    @Override // android.app.Service
    public void onDestroy() {
        IabIpc.logDebug("IabIpcService.onDestroy() in " + getBaseContext().getPackageName());
        cleanup();
    }
}
