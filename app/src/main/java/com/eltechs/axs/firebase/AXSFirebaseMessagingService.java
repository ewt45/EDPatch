//package com.eltechs.axs.firebase;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.media.RingtoneManager;
//import android.os.Build;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//import com.eltechs.axs.R;
//import com.eltechs.axs.activities.SwitchToAxsFromSystemTrayActivity;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
///* loaded from: classes.dex */
//public class AXSFirebaseMessagingService extends FirebaseMessagingService {
//    private static final String TAG = "AXSFirebaseMsgService";
//
//    @Override // com.google.firebase.messaging.FirebaseMessagingService
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        if (remoteMessage.getData().containsKey("axs_title") || remoteMessage.getData().containsKey("axs_body")) {
//            sendNotification(remoteMessage.getData().get("axs_title"), remoteMessage.getData().get("axs_body"));
//        } else {
//            Log.d(TAG, "No required field found");
//        }
//    }
//
//    private void sendNotification(String str, String str2) {
//        PendingIntent activity = PendingIntent.getActivity(this, 0, new Intent(this, SwitchToAxsFromSystemTrayActivity.class), 0);
//        String string = getString(R.string.default_notification_channel_id);
//        NotificationCompat.Builder contentIntent = new NotificationCompat.Builder(this, string).setSmallIcon(R.drawable.tray).setContentTitle(str).setContentText(str2).setAutoCancel(true).setSound(RingtoneManager.getDefaultUri(2)).setContentIntent(activity);
//        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
//        if (Build.VERSION.SDK_INT >= 26) {
//            notificationManager.createNotificationChannel(new NotificationChannel(string, "ExaGear", 3));
//        }
//        notificationManager.notify(0, contentIntent.build());
//    }
//}