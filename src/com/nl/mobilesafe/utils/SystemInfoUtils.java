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
 * ϵͳ��Ϣ�Ĺ�����
 * 
 * @author ׷��
 * 
 */
public class SystemInfoUtils {

	/**
	 * ��ȡ�������еĽ�������
	 * 
	 * @return
	 */
	public static int getRunningProcessCount(Context context) {
		// PackageManager �����������൱�ڳ������������̬�ģ�����window�Ŀ������ж�س���
		// ActivityManager�൱��window�Ľ��̹�����,���������
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		return infos.size();
	}

	/**
	 * ��ȡ�ֻ����õ��ڴ�
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
	 * ��ȡ�ֻ����õ����ڴ�
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
