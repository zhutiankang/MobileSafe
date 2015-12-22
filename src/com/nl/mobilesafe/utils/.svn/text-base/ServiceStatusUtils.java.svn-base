package com.nl.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtils {
	
	
	/**
	 * 检验一个服务是否还运行,系统所有的
	 * serviceName服务的全路径名
	 */
	public static boolean isServiceRunning(Context context,String serviceName){
		
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		List<RunningServiceInfo> infos = am.getRunningServices(100);
		for (RunningServiceInfo info : infos) {
			
			String className = info.service.getClassName();
			if(className.equals(serviceName)){
				return true;
			}
		}
		
		return false;
	}
}
