package com.mittorn.virgloverlay.common;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.*;
import android.graphics.*;
import android.os.Build;
import android.view.*;
import android.app.*;
import android.util.Log;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class overlay {
	private static final String T = "virgl-java";

	private static native void nativeRunOld(int fd);
	private static native int nativeAcceptOld(int fd);
	private static native int nativeOpenOld();
	private static native int nativeInitOld(String settings);
	private static native void nativeSettingsOld(String settings);
	private static native void nativeUnlinkOld();

	private static native void nativeRun(int fd);
	private static native int nativeAccept(int fd);
	private static native int nativeOpen();
	private static native int nativeInit(String settings);
	private static native void nativeSettings(String settings);
	private static native void nativeUnlink();

	private static Handler handler;
	@SuppressLint("StaticFieldLeak")
	private static Context ctx;
    private static WindowManager wm;

	public static int overlay_pos = 0, restart_var = 0, protocol_type = 0;

    final static Handler hh = new Handler();

	private static void start_next(int svc_id)
	{
		java.util.List<ActivityManager.RunningServiceInfo> services =
		((ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE)).
		getRunningServices(Integer.MAX_VALUE);

		for(int i = 1; i < 32; i++)
		{
			boolean free = true;

			if( i == svc_id )
				continue;
			for(ActivityManager.RunningServiceInfo s :services)
			{

				if(s.service.getClassName().equals(ctx.getPackageName() + ".process.p"+i))
				{
					free = false;
					break;
				}
			}
			if(free)
			{
				Log.d(T,"starting instance "+i);
				ctx.startService( new Intent().setClassName(ctx, ctx.getPackageName() + ".process.p"+i));
				return;
			}
		}
	}

	private static void run_mt()
	{

		new Thread(){
			@Override
			public void run()
			{
				Log.d(T, "Mode MT run!");

				int sock;
				if (protocol_type == 1)
					sock = nativeOpen();
				else
					sock = nativeOpenOld();

				if( sock < 0)
				{
					Log.d(T, "Failed to open socket " + sock);
					ctx.stopService( new Intent().setClassName(ctx, ctx.getPackageName() + ".process.p1"));
					return;
				}

				int fd;
				if (protocol_type == 1) {
					while ((fd = nativeAccept(sock)) >= 0) {
						final int fd1 = fd;
						Thread t = new Thread() {
							@Override
							public void run() {
								nativeRun(fd1);
							}
						};
						t.start();
					}
				}
				else
				{
					while ((fd = nativeAcceptOld(sock)) >= 0) {
						final int fd1 = fd;
						Thread t = new Thread() {
							@Override
							public void run() {
								nativeRunOld(fd1);
							}
						};
						t.start();
					}
				}
			}
		}.start();
	}
	private static void run_mp(final int svc_id)
	{
		new Thread()
		{
			public void run() {

				Log.d(T, "Mode MP run!");

				int fd;
				if (protocol_type == 1)
					fd = nativeOpen();
				else
					fd = nativeOpenOld();

				if (fd < 0) {
					Log.d(T, "Failed to open socket " + fd);
					ctx.stopService(new Intent().setClassName(ctx, ctx.getPackageName() + ".process.p" + svc_id));
					return;
				}
				if (protocol_type == 1) {
					fd = nativeAccept(fd);
					nativeUnlink();
					start_next(svc_id);
					nativeRun(fd);
				} else {
					fd = nativeAcceptOld(fd);
					nativeUnlinkOld();
					start_next(svc_id);
					nativeRunOld(fd);
				}
				ctx.stopService(new Intent().setClassName(ctx, ctx.getPackageName() + ".process.p" + svc_id));
			}
		}.start();
	}

	public static void start(Context ctx1, int svc_id)
	{
		ctx = ctx1;

		// Считываем
		try {
			FileReader settings_reader = new FileReader(ctx.getFilesDir().getPath() + "/settings2");
			BufferedReader reader = new BufferedReader(settings_reader);
			String[] parts0 = reader.readLine().split(" ");
			overlay_pos = Integer.valueOf(parts0[0]);
			restart_var = Integer.valueOf(parts0[1]);
			protocol_type = Integer.valueOf(parts0[2]);
			reader.close();
			settings_reader.close();

		}catch(Exception e){}

		wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		byte[] settings = new byte[65];
		int thread_mode;
		System.loadLibrary("virgl-lib");

		if (protocol_type == 1) {
			thread_mode = nativeInit(ctx.getFilesDir().getPath() + "/settings");
			nativeSettings(ctx.getFilesDir().getPath() + "/settings2");
		}
		else
		{
			thread_mode = nativeInitOld(ctx.getFilesDir().getPath() + "/settings");
			nativeSettingsOld(ctx.getFilesDir().getPath() + "/settings2");
		}

		handler = new Handler();
		if( thread_mode == 1 )
			run_mt();
		else
			run_mp(svc_id);
	}

    private static SurfaceView create(final int x, final int y, final int width, final int height) {
	    //resize(x,y,width, height);
		final Thread t = Thread.currentThread();
		final SurfaceView surf[] = new SurfaceView[1];
		try
		{
			int LAYOUT_FLAG;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
			} else {
				LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
			}
			//WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY

			Log.d(T, "post");

			handler.postDelayed(new Runnable(){
			//@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			public void run()
			{
				surf[0] = new SurfaceView(ctx);
				WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.OPAQUE);

				params.x = x;
				params.y = y;
				params.width = width;
				params.height = height;
				if( (width == 0) || (height == 0) )
				{
					params.width = params.height = 32;
				}

				// Overlay position
				if (overlay_pos == 0) {
					params.gravity = Gravity.LEFT | Gravity.TOP;
				} else if (overlay_pos == 1) {
					params.gravity = Gravity.CENTER;
				} else if (overlay_pos == 2) {
					params.gravity = Gravity.LEFT | Gravity.TOP;
					params.width = params.height = 1;
				}
				//

				wm.addView(surf[0], params);
				Log.d(T, "notify");
				synchronized(t)
				{
				t.notify();
				}
			}
		},100);
		synchronized(t)
		{
		t.wait();
		t.sleep(1000);
		}
		Log.d(T, "resume");
		}
		catch(Exception e)
		{
			e.printStackTrace();

			Log.d(T, "int");
			//return null;
		}
		return surf[0];
    }

	private static void set_rect(final SurfaceView surface, final int x, final int y, final int width, final int height, final int visible)
	{

		Log.d(T,"Resize " + x + " " + y + " " + width + " x " + height);
		handler.post(new Runnable()
		{
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			public void run()
			{
				try
				{
					WindowManager.LayoutParams params = (WindowManager.LayoutParams)surface.getLayoutParams();

					if( params == null )
						return;
					if( visible != 0 )
					{
						params.x = x;
						params.y = y;
						params.width = width;
						params.height = height;

						if (overlay_pos == 2) {
							params.gravity = Gravity.LEFT | Gravity.TOP;
							params.width = params.height = 1;
						}
					}
					else
					{
						//params.x = params.y = -33;
						//params.width = params.height = 32;
						Log.d(T,"Invisible! ");
					}
					wm.updateViewLayout(surface, params);
				}
				catch(Exception e)
				{}
			}
		});
	}

    public static void destroy(final SurfaceView surface) {
		handler.post(new Runnable(){
			public void run()
			{
				wm.removeView(surface);
			}
		});
    }
	public static Surface get_surface(SurfaceView surf)
	{
		return surf.getHolder().getSurface();
	}

	public static int readStop() {
		int stop = 0;
		try {
			FileReader reader3 = new FileReader(ctx.getFilesDir().getPath() + "/stop");
			BufferedReader reader = new BufferedReader(reader3);
			String[] parts0 = reader.readLine().split(" ");
			stop = Integer.valueOf(parts0[0]);
			reader.close();
			reader3.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stop;
	}

	public static void restart_services(int svc_id) {
		if (restart_var == 1 ) {
			if (readStop() == 0) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ctx.startService(new Intent().setClassName(ctx, ctx.getPackageName() + ".process.p" + svc_id));
				Log.d(T, "Restart service " + svc_id);
			}
		}
	}
}
