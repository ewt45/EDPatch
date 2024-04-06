//package com.eltechs.axs.firebase;
//
//import android.support.annotation.NonNull;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
//import java.util.HashMap;
//import org.json.JSONException;
//import org.json.JSONObject;
//
///* loaded from: classes.dex */
//public class FRCHelper {
//    private static final String KEY_REMIND_ACTIONS = "remind_actions";
//
//    public static void init() {
//        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//        firebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(false).build());
//        HashMap hashMap = new HashMap();
//        hashMap.put(KEY_REMIND_ACTIONS, "");
//        firebaseRemoteConfig.setDefaults(hashMap);
//        firebaseRemoteConfig.activateFetched();
//        fetch();
//    }
//
//    public static void fetch() {
//        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//        firebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() { // from class: com.eltechs.axs.firebase.FRCHelper.1
//            @Override // com.google.android.gms.tasks.OnCompleteListener
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    FirebaseRemoteConfig.this.activateFetched();
//                }
//            }
//        });
//    }
//
//    public static JSONObject getRemindActionsConfigJson() {
//        try {
//            return new JSONObject(FirebaseRemoteConfig.getInstance().getString(KEY_REMIND_ACTIONS));
//        } catch (JSONException unused) {
//            return null;
//        }
//    }
//}