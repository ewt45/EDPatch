package com.eltechs.axs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.helpers.Assert;

/* loaded from: classes.dex */
public class NetworkStateListener {
    private final BroadcastReceiver broadcastReceiver;
    private final ConnectivityManager connectivityManager;
    private final Context context;
    private final OnNetworkStateChangedListener networkStateChangedListener;
    private final IntentFilter networkStateIntentFilter = new IntentFilter();
    private final WifiManager wifiManager;

    /* loaded from: classes.dex */
    public interface OnNetworkStateChangedListener {
        void onNetworkStateChanged(String str);
    }

    public NetworkStateListener(Context context, OnNetworkStateChangedListener onNetworkStateChangedListener) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.networkStateIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.networkStateChangedListener = onNetworkStateChangedListener;
        this.broadcastReceiver = new BroadcastReceiver() { // from class: com.eltechs.axs.NetworkStateListener.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int ipAddress;
                Assert.isTrue(intent.getAction() == "android.net.conn.CONNECTIVITY_CHANGE");
                String str = "127.0.0.1";
//                NetworkInfo activeNetworkInfo = NetworkStateListener.this.connectivityManager.getActiveNetworkInfo();
//                if (activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == 1 && (ipAddress = NetworkStateListener.this.wifiManager.getConnectionInfo().getIpAddress()) != 0) {
//                    byte[] bArr = {(byte) ipAddress, (byte) (ipAddress >> 8), (byte) (ipAddress >> 16), (byte) (ipAddress >> 24)};
//                    str = String.format("%d.%d.%d.%d", Integer.valueOf(ArithHelpers.extendAsUnsigned(bArr[0])), Integer.valueOf(ArithHelpers.extendAsUnsigned(bArr[1])), Integer.valueOf(ArithHelpers.extendAsUnsigned(bArr[2])), Integer.valueOf(ArithHelpers.extendAsUnsigned(bArr[3])));
//                }
                NetworkStateListener.this.networkStateChangedListener.onNetworkStateChanged(str);
            }
        };
    }

    public void start() {
        this.context.registerReceiver(this.broadcastReceiver, this.networkStateIntentFilter);
    }

    public void stop() {
        this.context.unregisterReceiver(this.broadcastReceiver);
    }
}