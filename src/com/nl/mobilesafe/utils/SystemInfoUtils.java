package com.nl.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

/**
 * 系统信息的工具类
 * 
 * @author 追梦
 * 
 */
public class SystemInfoUtils {

	/**
	 * 获取正在运行的进程数量
	 * 
	 * @return
	 */
	public static int getRunningProcessCount(Context context) {
		// PackageManager 包管理器，相当于程序管理器，静态的，类似window的控制面板卸载程序
		// ActivityManager相当于window的进程管理器,任务管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		return infos.size();
	}

	/**
	 * 获取手机可用的内存
	 * 
	 * @param context
	 * @return
	 */
	public static long getAvailMem(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	/**
	 * 获取手机可用的总内存
	 * 
	 * @param context
	 * @return
	 */
	public static long getTotalMem() {

		StringBuilder sb = new StringBuilder();
		try {
			FileInputStream is = new FileInputStream("/proc/meminfo");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			for (char c : line.toCharArray()) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			br.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Long.valueOf(sb.toString()) * 1024;
	}
}
