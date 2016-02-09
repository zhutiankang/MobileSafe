package com.nl.mobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillAllReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//自定义广播接收到了
		
		System.out.println("自定义广播消息接收到了，killthemall");
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : infos) {
			String packname = info.processName;
			am.killBackgroundProcesses(packname);
			
		}
	}

}
