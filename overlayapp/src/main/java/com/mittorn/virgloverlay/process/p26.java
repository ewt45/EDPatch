package com.mittorn.virgloverlay.process;

public class p26 extends android.app.Service
{
    @Override
    public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
    }
	@Override
    public void onCreate() {
		super.onCreate();
		com.mittorn.virgloverlay.common.overlay.start(this,26);
		}
	@Override
    public void onDestroy() {
		super.onDestroy();
		System.exit(0);
		}
}
