package com.eltechs.axs.payments.ipc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;

public class IabIpcBroadcastReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        IabIpc.logDebug("IabIpcBroadcastReceiver.onReceive() in " + context.getPackageName() + " intent " + intent);
        ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("RESPONSE_EXTRA_SKU_LIST");
        String sb = "sku list = " +
                stringArrayListExtra;
        IabIpc.logDebug(sb);
        if (IabIpc.gResponse != null) {
            IabIpc.gResponse.received(stringArrayListExtra);
            IabIpc.gResponse = null;
        }
    }
}
