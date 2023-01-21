package com.mittorn.virgloverlay.process;

public class p1 extends android.app.Service
{
	@Override
	public android.os.IBinder onBind(android.content.Intent intent) { return null; }
	@Override
	public void onCreate() {
		super.onCreate();
		com.mittorn.virgloverlay.common.overlay.start(this,1);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		com.mittorn.virgloverlay.common.overlay.restart_services(2);
		System.exit(0);
	}
}
