package com.termux.x11;

import static android.Manifest.permission.WRITE_SECURE_SETTINGS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.view.InputDevice.KEYBOARD_TYPE_ALPHABETIC;
import static android.view.KeyEvent.*;
import static android.view.WindowManager.LayoutParams.*;
import static com.termux.x11.CmdEntryPoint.ACTION_START;
//import static com.termux.x11.LoriePreferences.ACTION_PREFERENCES_CHANGED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.DragEvent;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.NotificationCompat;
//import androidx.core.math.MathUtils;
//import androidx.viewpager.widget.ViewPager;

import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.ed.EDApplicationState;
import com.example.datainsert.exagear.QH;
//import com.termux.x11.input.InputEventSender;
//import com.termux.x11.input.InputStub;
//import com.termux.x11.input.TouchInputHandler.RenderStub;
//import com.termux.x11.input.TouchInputHandler;
//import com.termux.x11.utils.FullscreenWorkaround;
//import com.termux.x11.utils.KeyInterceptor;
//import com.termux.x11.utils.SamsungDexUtils;
//import com.termux.x11.utils.TermuxX11ExtraKeys;
//import com.termux.x11.utils.X11ToolbarViewPager;

import java.util.Map;
import java.util.Objects;

@SuppressLint("ApplySharedPref")
@SuppressWarnings({"deprecation", "unused"})
public class MainActivity extends XServerDisplayActivity<EDApplicationState> implements View.OnApplyWindowInsetsListener {
    private static final String TAG = "MainActivity";
    static final String ACTION_STOP = "com.eltechs.ed.ACTION_STOP";
    static final String REQUEST_LAUNCH_EXTERNAL_DISPLAY = "request_launch_external_display";

    public static Handler handler = new Handler();
    FrameLayout frm;
//    private TouchInputHandler mInputHandler;
    private ICmdEntryInterface service = null;
//    public TermuxX11ExtraKeys mExtraKeys;
    private Notification mNotification;
    private final int mNotificationId = 7892;
    NotificationManager mNotificationManager;
    private boolean mClientConnected = false;
    private View.OnKeyListener mLorieKeyListener;
    private boolean filterOutWinKey = false;
    private boolean hideEKOnVolDown = false;
    private boolean toggleIMEUsingBackKey = false;
    private static final int KEY_BACK = 158;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_START.equals(intent.getAction())) {
                try {
                    Log.v("LorieBroadcastReceiver", "Got new ACTION_START intent");
                    IBinder b = Objects.requireNonNull(intent.getBundleExtra("")).getBinder("");
                    service = ICmdEntryInterface.Stub.asInterface(b);
                    Objects.requireNonNull(service).asBinder().linkToDeath(() -> {
                        service = null;
                        CmdEntryPoint.requestConnection();

                        Log.v("Lorie", "Disconnected");
                        runOnUiThread(() -> clientConnectedStateChanged(false)); //recreate()); //onPreferencesChanged(""));
                    }, 0);

                    onReceiveConnection();
                } catch (Exception e) {
                    Log.e("MainActivity", "Something went wrong while we extracted connection details from binder.", e);
                }
            } else if (ACTION_STOP.equals(intent.getAction())) {
                finishAffinity();
            } //else if (ACTION_PREFERENCES_CHANGED.equals(intent.getAction())) {
//                Log.d("MainActivity", "preference: " + intent.getStringExtra("key"));
//                if (!"additionalKbdVisible".equals(intent.getStringExtra("key")))
//                    onPreferencesChanged("");
//            }
        }
    };

//    @SuppressLint("StaticFieldLeak")
//    private static MainActivity instance;

    public MainActivity() {
//        instance = this;
    }

    public static MainActivity getInstance() {
        return (MainActivity) QH.getCurrentActivity();
    }

    @Override
    @SuppressLint({"NewApi", "WrongConstant", "AppCompatMethod", "ObsoleteSdkInt", "ClickableViewAccessibility", "WrongConstant", "UnspecifiedRegisterReceiverFlag"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        int modeValue = Integer.parseInt(preferences.getString("touchMode", "1")) - 1;
//        if (modeValue > 2) {
//            SharedPreferences.Editor e = Objects.requireNonNull(preferences).edit();
//            e.putString("touchMode", "1");
//            e.apply();
//        }
//
//        preferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> onPreferencesChanged(key));
//
//        getWindow().setFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | FLAG_KEEP_SCREEN_ON | FLAG_TRANSLUCENT_STATUS, 0);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.main_activity);
//
//        frm = findViewById(R.id.frame);
//        findViewById(R.id.preferences_button).setOnClickListener((l) -> startActivity(new Intent(this, LoriePreferences.class) {{ setAction(Intent.ACTION_MAIN); }}));
//        findViewById(R.id.help_button).setOnClickListener((l) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/termux/termux-x11/blob/master/README.md#running-graphical-applications"))));

        LorieView lorieView = viewOfXServer.getViewForRendering();//findViewById(R.id.lorieView);
        View lorieParent = (View) lorieView.getParent();

//        mInputHandler = new TouchInputHandler(this, new RenderStub.NullStub() {
//            @Override
//            public void swipeDown() {
//                toggleExtraKeys();
//            }
//        }, new InputEventSender(lorieView));
//        mLorieKeyListener = (v, k, e) -> {
//            if (hideEKOnVolDown && k == KEYCODE_VOLUME_DOWN) {
//                if (e.getAction() == ACTION_UP)
//                    toggleExtraKeys();
//                return true;
//            }
//
//            if (k == KEYCODE_BACK) {
//                if (e.isFromSource(InputDevice.SOURCE_MOUSE) || e.isFromSource(InputDevice.SOURCE_MOUSE_RELATIVE)) {
//                    if (e.getRepeatCount() != 0) // ignore auto-repeat
//                        return true;
//                    if (e.getAction() == KeyEvent.ACTION_UP || e.getAction() == KeyEvent.ACTION_DOWN)
//                        lorieView.sendMouseEvent(-1, -1, InputStub.BUTTON_RIGHT, e.getAction() == KeyEvent.ACTION_DOWN, true);
//                    return true;
//                }
//
//                if (e.getScanCode() == KEY_BACK && e.getDevice().getKeyboardType() != KEYBOARD_TYPE_ALPHABETIC || e.getScanCode() == 0) {
//                    if (toggleIMEUsingBackKey && e.getAction() == ACTION_UP)
//                        toggleKeyboardVisibility(MainActivity.this);
//
//                    return true;
//                }
//            }
//
//            return mInputHandler.sendKeyEvent(v, e);
//        };
//
//        lorieParent.setOnTouchListener((v, e) -> mInputHandler.handleTouchEvent(lorieParent, lorieView, e));
//        lorieParent.setOnHoverListener((v, e) -> mInputHandler.handleTouchEvent(lorieParent, lorieView, e));
//        lorieParent.setOnGenericMotionListener((v, e) -> mInputHandler.handleTouchEvent(lorieParent, lorieView, e));
//        lorieView.setOnCapturedPointerListener((v, e) -> mInputHandler.handleTouchEvent(lorieView, lorieView, e));
//        lorieParent.setOnCapturedPointerListener((v, e) -> mInputHandler.handleTouchEvent(lorieView, lorieView, e));
//        lorieView.setOnKeyListener(mLorieKeyListener);
//TODO 会不会在activity重建时 lorieView发生变化而没有重新设置回调？
        lorieView.setCallback((sfc, surfaceWidth, surfaceHeight, screenWidth, screenHeight) -> {
            int framerate = (int) ((lorieView.getDisplay() != null) ? lorieView.getDisplay().getRefreshRate() : 30);

//            mInputHandler.handleHostSizeChanged(surfaceWidth, surfaceHeight);
//            mInputHandler.handleClientSizeChanged(screenWidth, screenHeight);
            LorieView.sendWindowChange(screenWidth, screenHeight, framerate);

            if (service != null) {
                try {
                    service.windowChanged(sfc);
                } catch (RemoteException e) {
                    Log.e("MainActivity", "failed to send windowChanged request", e);
                }
            }
        });

        registerReceiver(receiver, new IntentFilter(ACTION_START) {{
//            addAction(ACTION_PREFERENCES_CHANGED);
            addAction(ACTION_STOP);
        }}, SDK_INT >= 33 ? 4 : 0);

        // Taken from Stackoverflow answer https://stackoverflow.com/questions/7417123/android-how-to-adjust-layout-in-full-screen-mode-when-softkeyboard-is-visible/7509285#
//        FullscreenWorkaround.assistActivity(this);
//        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotification = buildNotification();
//        mNotificationManager.notify(mNotificationId, mNotification);

        CmdEntryPoint.requestConnection();
        LorieView.setClipboardSyncEnabled(true);//onPreferencesChanged("");

//        toggleExtraKeys(false, false);
//        checkXEvents();

//        initStylusAuxButtons();
//        initMouseAuxButtons();
//
//        if (SDK_INT >= VERSION_CODES.TIRAMISU
//                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED
//                && !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//            requestPermissions(new String[] { Manifest.permission.POST_NOTIFICATIONS }, 0);
//        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    //Register the needed events to handle stylus as left, middle and right click
//    @SuppressLint("ClickableViewAccessibility")
//    private void initStylusAuxButtons() {
//        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean stylusMenuEnabled = p.getBoolean("showStylusClickOverride", false);
//        final float menuUnselectedTrasparency = 0.66f;
//        final float menuSelectedTrasparency = 1.0f;
//        Button left = findViewById(R.id.button_left_click);
//        Button right = findViewById(R.id.button_right_click);
//        Button middle = findViewById(R.id.button_middle_click);
//        Button visibility = findViewById(R.id.button_visibility);
//        LinearLayout overlay = findViewById(R.id.mouse_helper_visibility);
//        LinearLayout buttons = findViewById(R.id.mouse_helper_secondary_layer);
//        overlay.setOnTouchListener((v, e) -> true);
//        overlay.setOnHoverListener((v, e) -> true);
//        overlay.setOnGenericMotionListener((v, e) -> true);
//        overlay.setOnCapturedPointerListener((v, e) -> true);
//        overlay.setVisibility(stylusMenuEnabled ? View.VISIBLE : View.GONE);
//        View.OnClickListener listener = view -> {
//            TouchInputHandler.STYLUS_INPUT_HELPER_MODE = (view.equals(left) ? 1 : (view.equals(middle) ? 2 : (view.equals(right) ? 3 : 0)));
//            left.setAlpha((TouchInputHandler.STYLUS_INPUT_HELPER_MODE == 1) ? menuSelectedTrasparency : menuUnselectedTrasparency);
//            middle.setAlpha((TouchInputHandler.STYLUS_INPUT_HELPER_MODE == 2) ? menuSelectedTrasparency : menuUnselectedTrasparency);
//            right.setAlpha((TouchInputHandler.STYLUS_INPUT_HELPER_MODE == 3) ? menuSelectedTrasparency : menuUnselectedTrasparency);
//            visibility.setAlpha(menuUnselectedTrasparency);
//        };
//
//        left.setOnClickListener(listener);
//        middle.setOnClickListener(listener);
//        right.setOnClickListener(listener);
//
//        visibility.setOnClickListener(view -> {
//            if (buttons.getVisibility() == View.VISIBLE) {
//                buttons.setVisibility(View.GONE);
//                visibility.setAlpha(menuUnselectedTrasparency);
//                int m = TouchInputHandler.STYLUS_INPUT_HELPER_MODE;
//                visibility.setText(m == 1 ? "L" : (m == 2 ? "M" : (m == 3 ? "R" : "U")));
//            } else {
//                buttons.setVisibility(View.VISIBLE);
//                visibility.setAlpha(menuUnselectedTrasparency);
//                visibility.setText("X");
//
//                //Calculate screen border making sure btn is fully inside the view
//                float maxX = frm.getWidth() - 4 * left.getWidth();
//                float maxY = frm.getHeight() - 4 * left.getHeight();
//
//                //Make sure the Stylus menu is fully inside the screen
//                overlay.setX(MathUtils.clamp(overlay.getX(), 0, maxX));
//                overlay.setY(MathUtils.clamp(overlay.getY(), 0, maxY));
//
//                int m = TouchInputHandler.STYLUS_INPUT_HELPER_MODE;
//                listener.onClick(m == 1 ? left : (m == 2 ? middle : (m == 3 ? right : left)));
//            }
//        });
//        //Simulated mouse click 1 = left , 2 = middle , 3 = right
//        TouchInputHandler.STYLUS_INPUT_HELPER_MODE = 1;
//        listener.onClick(left);
//
//        visibility.setOnLongClickListener(v -> {
//            v.startDragAndDrop(ClipData.newPlainText("", ""), new View.DragShadowBuilder(visibility) {
//                public void onDrawShadow(@NonNull Canvas canvas) {}
//            }, null, View.DRAG_FLAG_GLOBAL);
//
//            frm.setOnDragListener((v2, event) -> {
//                //Calculate screen border making sure btn is fully inside the view
//                float maxX = frm.getWidth() - visibility.getWidth();
//                float maxY = frm.getHeight() - visibility.getHeight();
//
//                switch (event.getAction()) {
//                    case DragEvent.ACTION_DRAG_LOCATION:
//                        //Center touch location with btn icon
//                        float dX = event.getX() - visibility.getWidth() / 2.0f;
//                        float dY = event.getY() - visibility.getHeight() / 2.0f;
//
//                        //Make sure the dragged btn is inside the view with clamp
//                        overlay.setX(MathUtils.clamp(dX, 0, maxX));
//                        overlay.setY(MathUtils.clamp(dY, 0, maxY));
//                        break;
//                    case DragEvent.ACTION_DRAG_ENDED:
//                        //Make sure the dragged btn is inside the view
//                        overlay.setX(MathUtils.clamp(overlay.getX(), 0, maxX));
//                        overlay.setY(MathUtils.clamp(overlay.getY(), 0, maxY));
//                        break;
//                }
//                return true;
//            });
//
//            return true;
//        });
//    }

//    void setSize(View v, int width, int height) {
//        ViewGroup.LayoutParams p = v.getLayoutParams();
//        p.width = (int) (width * getResources().getDisplayMetrics().density);
//        p.height = (int) (height * getResources().getDisplayMetrics().density);
//        v.setLayoutParams(p);
//        v.setMinimumWidth((int) (width * getResources().getDisplayMetrics().density));
//        v.setMinimumHeight((int) (height * getResources().getDisplayMetrics().density));
//    }

//    @SuppressLint("ClickableViewAccessibility")
//    void initMouseAuxButtons() {
//        Button left = findViewById(R.id.mouse_button_left_click);
//        Button right = findViewById(R.id.mouse_button_right_click);
//        Button middle = findViewById(R.id.mouse_button_middle_click);
//        ImageButton pos = findViewById(R.id.mouse_buttons_position);
//        LinearLayout primaryLayer = findViewById(R.id.mouse_buttons);
//        LinearLayout secondaryLayer = findViewById(R.id.mouse_buttons_secondary_layer);
//
//        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean mouseHelperEnabled = p.getBoolean("showMouseHelper", false) && "1".equals(p.getString("touchMode", "1"));
//        primaryLayer.setVisibility(mouseHelperEnabled ? View.VISIBLE : View.GONE);
//
//        pos.setOnClickListener((v) -> {
//            if (secondaryLayer.getOrientation() == LinearLayout.HORIZONTAL) {
//                setSize(left, 48, 96);
//                setSize(right, 48, 96);
//                secondaryLayer.setOrientation(LinearLayout.VERTICAL);
//            } else {
//                setSize(left, 96, 48);
//                setSize(right, 96, 48);
//                secondaryLayer.setOrientation(LinearLayout.HORIZONTAL);
//            }
//            handler.postDelayed(() -> {
//                int[] offset = new int[2];
//                frm.getLocationOnScreen(offset);
//                primaryLayer.setX(MathUtils.clamp(primaryLayer.getX(), offset[0], offset[0] + frm.getWidth() - primaryLayer.getWidth()));
//                primaryLayer.setY(MathUtils.clamp(primaryLayer.getY(), offset[1], offset[1] + frm.getHeight() - primaryLayer.getHeight()));
//            }, 10);
//        });
//
//        Map.of(left, InputStub.BUTTON_LEFT, middle, InputStub.BUTTON_MIDDLE, right, InputStub.BUTTON_RIGHT)
//                .forEach((v, b) -> v.setOnTouchListener((__, e) -> {
//                    switch(e.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                        case MotionEvent.ACTION_POINTER_DOWN:
//                            getLorieView().sendMouseEvent(0, 0, b, true, true);
//                            v.setPressed(true);
//                            break;
//                        case MotionEvent.ACTION_UP:
//                        case MotionEvent.ACTION_POINTER_UP:
//                            getLorieView().sendMouseEvent(0, 0, b, false, true);
//                            v.setPressed(false);
//                            break;
//                    }
//                    return true;
//                }));
//
//        pos.setOnTouchListener(new View.OnTouchListener() {
//            final int touchSlop = (int) Math.pow(ViewConfiguration.get(MainActivity.this).getScaledTouchSlop(), 2);
//            final int tapTimeout = ViewConfiguration.getTapTimeout();
//            final float[] startOffset = new float[2];
//            final int[] startPosition = new int[2];
//            long startTime;
//            @Override
//            public boolean onTouch(View v, MotionEvent e) {
//                switch(e.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        primaryLayer.getLocationOnScreen(startPosition);
//                        startOffset[0] = e.getX();
//                        startOffset[1] = e.getY();
//                        startTime = SystemClock.uptimeMillis();
//                        pos.setPressed(true);
//                        break;
//                    case MotionEvent.ACTION_MOVE: {
//                        int[] offset = new int[2];
//                        int[] offset2 = new int[2];
//                        primaryLayer.getLocationOnScreen(offset);
//                        frm.getLocationOnScreen(offset2);
//                        primaryLayer.setX(MathUtils.clamp(offset[0] - startOffset[0] + e.getX(), offset2[0], offset2[0] + frm.getWidth() - primaryLayer.getWidth()));
//                        primaryLayer.setY(MathUtils.clamp(offset[1] - startOffset[1] + e.getY(), offset2[1], offset2[1] + frm.getHeight() - primaryLayer.getHeight()));
//                        break;
//                    }
//                    case MotionEvent.ACTION_UP: {
//                        final int[] _pos = new int[2];
//                        primaryLayer.getLocationOnScreen(_pos);
//                        int deltaX = (int) (startOffset[0] - e.getX()) + (startPosition[0] - _pos[0]);
//                        int deltaY = (int) (startOffset[1] - e.getY()) + (startPosition[1] - _pos[1]);
//                        pos.setPressed(false);
//
//                        if (deltaX * deltaX + deltaY * deltaY < touchSlop && SystemClock.uptimeMillis() - startTime <= tapTimeout) {
//                            v.performClick();
//                            return true;
//                        }
//                        break;
//                    }
//                }
//                return true;
//            }
//        });
//    }

    void onReceiveConnection() {
        try {
            if (service != null && service.asBinder().isBinderAlive()) {
                Log.v("LorieBroadcastReceiver", "Extracting logcat fd.");
                ParcelFileDescriptor logcatOutput = service.getLogcatOutput();
                if (logcatOutput != null)
                    LorieView.startLogcat(logcatOutput.detachFd());

                tryConnect();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Something went wrong while we were establishing connection", e);
        }
    }

    void tryConnect() {
        if (mClientConnected)
            return;
        try {
            Log.v("LorieBroadcastReceiver", "Extracting X connection socket.");
            ParcelFileDescriptor fd = service == null ? null : service.getXConnection();
            if (fd != null) {
                LorieView.connect(fd.detachFd());
                getLorieView().triggerCallback();
                clientConnectedStateChanged(true);
                LorieView.setClipboardSyncEnabled(true);//(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("clipboardSync", false));
            } else
                handler.postDelayed(this::tryConnect, 500);
        } catch (Exception e) {
            Log.e("MainActivity", "Something went wrong while we were establishing connection", e);
            service = null;

            // We should reset the View for the case if we have sent it's surface to the client.
            getLorieView().regenerate();
        }
    }

//    void onPreferencesChanged(String key) {
//        if ("additionalKbdVisible".equals(key))
//            return;
//
//        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
//        LorieView lorieView = getLorieView();
//
//        int mode = Integer.parseInt(p.getString("touchMode", "1"));
//        mInputHandler.setInputMode(mode);
//        mInputHandler.setTapToMove(p.getBoolean("tapToMove", false));
//        mInputHandler.setPreferScancodes(p.getBoolean("preferScancodes", false));
//        mInputHandler.setPointerCaptureEnabled(p.getBoolean("pointerCapture", false));
//        mInputHandler.setApplyDisplayScaleFactorToTouchpad(p.getBoolean("scaleTouchpad", true));
//        if (!p.getBoolean("pointerCapture", false) && lorieView.hasPointerCapture())
//            lorieView.releasePointerCapture();
//
//        SamsungDexUtils.dexMetaKeyCapture(this, p.getBoolean("dexMetaKeyCapture", false));
//
//        setTerminalToolbarView();
//        onWindowFocusChanged(true);
//        LorieView.setClipboardSyncEnabled(p.getBoolean("clipboardEnable", false));
//
//        lorieView.triggerCallback();
//
//        filterOutWinKey = p.getBoolean("filterOutWinkey", false);
//        if (p.getBoolean("enableAccessibilityServiceAutomatically", false)) {
//            try {
//                Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "com.termux.x11/.utils.KeyInterceptor");
//                Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, "1");
//            } catch (SecurityException e) {
//                new AlertDialog.Builder(this)
//                        .setTitle("Permission denied")
//                        .setMessage("Android requires WRITE_SECURE_SETTINGS permission to start accessibility service automatically.\n" +
//                                "Please, launch this command using ADB:\n" +
//                                "adb shell pm grant com.termux.x11 android.permission.WRITE_SECURE_SETTINGS")
//                        .setNegativeButton("OK", null)
//                        .create()
//                        .show();
//
//                SharedPreferences.Editor edit = p.edit();
//                edit.putBoolean("enableAccessibilityServiceAutomatically", false);
//                edit.commit();
//            }
//        } else if (checkSelfPermission(WRITE_SECURE_SETTINGS) == PERMISSION_GRANTED)
//            KeyInterceptor.shutdown();
//
//        hideEKOnVolDown = p.getBoolean("hideEKOnVolDown", false);
//        toggleIMEUsingBackKey = p.getBoolean("toggleIMEUsingBackKey", true);
//
//        int requestedOrientation = p.getBoolean("forceLandscape", false) ?
//                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
//        if (getRequestedOrientation() != requestedOrientation)
//            setRequestedOrientation(requestedOrientation);
//
//        findViewById(R.id.mouse_buttons).setVisibility(p.getBoolean("showMouseHelper", false) && "1".equals(p.getString("touchMode", "1")) && mClientConnected ? View.VISIBLE : View.GONE);
//        LinearLayout buttons = findViewById(R.id.mouse_helper_visibility);
//        if (p.getBoolean("showStylusClickOverride", false)) {
//            buttons.setVisibility(View.VISIBLE);
//        } else {
//            //Reset default input back to normal
//            TouchInputHandler.STYLUS_INPUT_HELPER_MODE = 1;
//            final float menuUnselectedTrasparency = 0.66f;
//            final float menuSelectedTrasparency = 1.0f;
//            findViewById(R.id.button_left_click).setAlpha(menuSelectedTrasparency);
//            findViewById(R.id.button_right_click).setAlpha(menuUnselectedTrasparency);
//            findViewById(R.id.button_middle_click).setAlpha(menuUnselectedTrasparency);
//            findViewById(R.id.button_visibility).setAlpha(menuUnselectedTrasparency);
//            buttons.setVisibility(View.GONE);
//        }
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        mNotificationManager.notify(mNotificationId, mNotification);
//
//        setTerminalToolbarView();
//        getLorieView().requestFocus();
//    }

//    @Override
//    public void onPause() {
//        for (StatusBarNotification notification: mNotificationManager.getActiveNotifications())
//            if (notification.getId() == mNotificationId)
//                mNotificationManager.cancel(mNotificationId);
//        super.onPause();
//    }

    public LorieView getLorieView() {
        return viewOfXServer.getViewForRendering();
    }

//    public ViewPager getTerminalToolbarViewPager() {
//        return findViewById(R.id.terminal_toolbar_view_pager);
//    }

//    private void setTerminalToolbarView() {
//        final ViewPager terminalToolbarViewPager = getTerminalToolbarViewPager();
//
//        terminalToolbarViewPager.setAdapter(new X11ToolbarViewPager.PageAdapter(this, (v, k, e) -> mInputHandler.sendKeyEvent(getLorieView(), e)));
//        terminalToolbarViewPager.addOnPageChangeListener(new X11ToolbarViewPager.OnPageChangeListener(this, terminalToolbarViewPager));
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean enabled = preferences.getBoolean("showAdditionalKbd", true);
//        boolean showNow = enabled && preferences.getBoolean("additionalKbdVisible", true);
//
//        terminalToolbarViewPager.setVisibility(showNow ? View.VISIBLE : View.GONE);
//        findViewById(R.id.terminal_toolbar_view_pager).requestFocus();
//
//        handler.postDelayed(() -> {
//            if (mExtraKeys != null) {
//                ViewGroup.LayoutParams layoutParams = terminalToolbarViewPager.getLayoutParams();
//                layoutParams.height = Math.round(37.5f * getResources().getDisplayMetrics().density *
//                        (mExtraKeys.getExtraKeysInfo() == null ? 0 : mExtraKeys.getExtraKeysInfo().getMatrix().length));
//                terminalToolbarViewPager.setLayoutParams(layoutParams);
//            }
//        }, 200);
//    }

//    public void toggleExtraKeys(boolean visible, boolean saveState) {
//        runOnUiThread(() -> {
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//            boolean enabled = preferences.getBoolean("showAdditionalKbd", true);
//            ViewPager pager = getTerminalToolbarViewPager();
//            ViewGroup parent = (ViewGroup) pager.getParent();
//            boolean show = enabled && mClientConnected && visible;
//
//            if (show) {
//                setTerminalToolbarView();
//                getTerminalToolbarViewPager().bringToFront();
//            } else {
//                parent.removeView(pager);
//                parent.addView(pager, 0);
//            }
//
//            if (enabled && saveState) {
//                SharedPreferences.Editor edit = preferences.edit();
//                edit.putBoolean("additionalKbdVisible", show);
//                edit.commit();
//            }
//
//            pager.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//
//            getLorieView().requestFocus();
//        });
//    }

//    public void toggleExtraKeys() {
//        int visibility = getTerminalToolbarViewPager().getVisibility();
//        toggleExtraKeys(visibility != View.VISIBLE, true);
//        getLorieView().requestFocus();
//    }

//    public boolean handleKey(KeyEvent e) {
//        if (filterOutWinKey && (e.getKeyCode() == KEYCODE_META_LEFT || e.getKeyCode() == KEYCODE_META_RIGHT || e.isMetaPressed()))
//            return false;
//        mLorieKeyListener.onKey(getLorieView(), e.getKeyCode(), e);
//        return true;
//    }

//    @SuppressLint("ObsoleteSdkInt")
//    Notification buildNotification() {
//        Intent preferencesIntent = new Intent(this, LoriePreferences.class);
//        preferencesIntent.putExtra("key", "value");
//        preferencesIntent.setAction(Intent.ACTION_MAIN);
//
//        Intent exitIntent = new Intent(ACTION_STOP);
//        exitIntent.setPackage(getPackageName());
//
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, preferencesIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//        PendingIntent pExitIntent = PendingIntent.getBroadcast(this, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        return new NotificationCompat.Builder(this, getNotificationChannel(notificationManager))
//                .setContentTitle("Termux:X11")
//                .setSmallIcon(R.drawable.ic_x11_icon)
//                .setContentText("Pull down to show options")
//                .setContentIntent(pIntent)
//                .setOngoing(true)
//                .setPriority(Notification.PRIORITY_MAX)
//                .setSilent(true)
//                .setShowWhen(false)
//                .setColor(0xFF607D8B)
//                .addAction(0, "Exit", pExitIntent)
//                .addAction(0, "Preferences", pIntent)
//                .build();
//    }

//    private String getNotificationChannel(NotificationManager notificationManager){
//        String channelId = getResources().getString(R.string.app_name);
//        String channelName = getResources().getString(R.string.app_name);
//        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
//        channel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
//        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//        if (SDK_INT >= VERSION_CODES.Q)
//            channel.setAllowBubbles(false);
//        notificationManager.createNotificationChannel(channel);
//        return channelId;
//    }

//    int orientation;
//
//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        if (newConfig.orientation != orientation) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//            View view = getCurrentFocus();
//            if (view == null) {
//                view = getLorieView();
//                view.requestFocus();
//            }
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//
//        orientation = newConfig.orientation;
//        setTerminalToolbarView();
//    }

//    @SuppressLint("WrongConstant")
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
//        Window window = getWindow();
//        View decorView = window.getDecorView();
//        boolean fullscreen = p.getBoolean("fullscreen", false);
//        boolean reseed = p.getBoolean("Reseed", true);
//
//        fullscreen = fullscreen || getIntent().getBooleanExtra(REQUEST_LAUNCH_EXTERNAL_DISPLAY, false);
//
//        int requestedOrientation = p.getBoolean("forceLandscape", false) ?
//                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
//        if (getRequestedOrientation() != requestedOrientation)
//            setRequestedOrientation(requestedOrientation);
//
//        if (hasFocus) {
//            if (SDK_INT >= VERSION_CODES.P) {
//                if (p.getBoolean("hideCutout", false))
//                    getWindow().getAttributes().layoutInDisplayCutoutMode = (SDK_INT >= VERSION_CODES.R) ?
//                            LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS :
//                            LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//                else
//                    getWindow().getAttributes().layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
//            }
//
//            window.setStatusBarColor(Color.BLACK);
//            window.setNavigationBarColor(Color.BLACK);
//        }
//
//        window.setFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | FLAG_KEEP_SCREEN_ON | FLAG_TRANSLUCENT_STATUS, 0);
//        if (hasFocus) {
//            if (fullscreen) {
//                window.addFlags(FLAG_FULLSCREEN);
//                decorView.setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//            } else {
//                window.clearFlags(FLAG_FULLSCREEN);
//                decorView.setSystemUiVisibility(0);
//            }
//        }
//
//        if (p.getBoolean("keepScreenOn", true))
//            window.addFlags(FLAG_KEEP_SCREEN_ON);
//        else
//            window.clearFlags(FLAG_KEEP_SCREEN_ON);
//
//        window.setSoftInputMode((reseed ? SOFT_INPUT_ADJUST_RESIZE : SOFT_INPUT_ADJUST_PAN) | SOFT_INPUT_STATE_HIDDEN);
//
//        ((FrameLayout) findViewById(android.R.id.content)).getChildAt(0).setFitsSystemWindows(!fullscreen);
//        SamsungDexUtils.dexMetaKeyCapture(this, hasFocus && p.getBoolean("dexMetaKeyCapture", false));
//    }

//    @Override
//    public void onBackPressed() {
//    }

//    public static boolean hasPipPermission(@NonNull Context context) {
//        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//        if (appOpsManager == null)
//            return false;
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//            return appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), context.getPackageName()) == AppOpsManager.MODE_ALLOWED;
//        else
//            return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), context.getPackageName()) == AppOpsManager.MODE_ALLOWED;
//    }

//    @Override
//    public void onUserLeaveHint() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        if (preferences.getBoolean("PIP", false) && hasPipPermission(this)) {
//            enterPictureInPictureMode();
//        }
//    }

//    @Override
//    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
//        toggleExtraKeys(!isInPictureInPictureMode, false);
//
//        frm.setPadding(0, 0, 0, 0);
//        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
//    }

    /** @noinspection NullableProblems*/
    @SuppressLint("WrongConstant")
    @Override
    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
        handler.postDelayed(() -> getLorieView().triggerCallback(), 100);
        return insets;
    }

//    /**
//     * Manually toggle soft keyboard visibility
//     * @param context calling context
//     */
//    public static void toggleKeyboardVisibility(Context context) {
//        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        Log.d("MainActivity", "Toggling keyboard visibility");
//        if(inputMethodManager != null)
//            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//    }

    @SuppressLint("NewApi")
    @SuppressWarnings("SameParameterValue")
    void clientConnectedStateChanged(boolean connected) {
        runOnUiThread(()-> {
//            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
            mClientConnected = connected;
//            toggleExtraKeys(connected && p.getBoolean("additionalKbdVisible", true), true);
//            findViewById(R.id.mouse_buttons).setVisibility(p.getBoolean("showMouseHelper", false) && "1".equals(p.getString("touchMode", "1")) && mClientConnected ? View.VISIBLE : View.GONE);
//            findViewById(R.id.stub).setVisibility(connected?View.INVISIBLE:View.VISIBLE);
//            getLorieView().setVisibility(connected?View.VISIBLE:View.INVISIBLE);
            if(connected) getLorieView().regenerate(); //加个判断吧

            // We should recover connection in the case if file descriptor for some reason was broken...
            if (!connected)
                tryConnect();

            if (connected)
                getLorieView().setPointerIcon(PointerIcon.getSystemIcon(this, PointerIcon.TYPE_NULL));
        });
    }
    /** 好像只用于处理剪切板 */
    private void checkXEvents() {
        if(viewOfXServer==null)
            return;
        if(getLorieView()!=null)
            getLorieView().handleXEvents();
        handler.postDelayed(this::checkXEvents, 300);
    }
}