package com.eltechs.axs.payments.ipc;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.eltechs.axs.helpers.Assert;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public abstract class IabIpc {
    static final String PERMISSION_NAME = "com.eltechs.axs.permission.IAB_IPC";
    static final String REQUEST_EXTRA_REQID = "REQUEST_EXTRA_REQID";
    static final String REQUEST_EXTRA_SOURCE_PKG_NAME = "REQUEST_EXTRA_SOURCE_PKG_NAME";
    static final String RESPONSE_EXTRA_REQID = "RESPONSE_EXTRA_REQID";
    static final String RESPONSE_EXTRA_SKU_LIST = "RESPONSE_EXTRA_SKU_LIST";
    static final String TAG = "IabIpc";
    static Response gResponse;
    static boolean logEnabled;

    /* loaded from: classes.dex */
    public interface Response {
        void received(List<String> list);
    }

    /* loaded from: classes.dex */
    public static class Request {
        public void sendRequest(Context context, String str, Response response) {
            Intent intent = new Intent();
            intent.setClassName(str, IabIpcService.class.getName());
            intent.putExtra(IabIpc.REQUEST_EXTRA_SOURCE_PKG_NAME, context.getPackageName());
            intent.putExtra(IabIpc.REQUEST_EXTRA_REQID, 1);
            Assert.state(IabIpc.gResponse == null, "Only one pending IabIpcRequest supported.");
            IabIpc.gResponse = response;
            IabIpc.logDebug("Starting IabIpcService in " + str + " from " + context.getPackageName() + "...");
            if (context.startService(intent) == null) {
                IabIpc.logDebug("service not started");
                if (IabIpc.gResponse != null) {
                    IabIpc.gResponse.received(Collections.emptyList());
                    IabIpc.gResponse = null;
                }
            }
        }

        public void cleanup() {
            IabIpc.gResponse = null;
        }
    }

    static void logDebug(String str) {
        if (logEnabled) {
            Log.d(TAG, str);
        }
    }
}
