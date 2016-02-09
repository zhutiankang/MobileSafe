package com.nl.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AutoCleanService extends Service {
	
	private ScreenOffReceiver receiver;
	private ActivityManager am;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		receiver = new ScreenOffReceiver();
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("������");
			List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
			for (RunningAppProcessInfo info : infos) {
				String packname = info.processName;
				am.killBackgroundProcesses(packname);
				
			}
			
		}
		
	}

}
