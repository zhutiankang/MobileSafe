package com.nl.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.nl.mobilesafe.R;
import com.nl.mobilesafe.domain.TaskInfo;

/**提供手机里面的进程信息
 * @author 追梦
 *
 */
public class TaskInfoProvider {
	/**获取所有的进程信息
	 * @param context 上下文
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		//获取正在运行的所有进程
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		TaskInfo taskInfo;
		
		for (RunningAppProcessInfo processInfo : processInfos) {
			taskInfo = new TaskInfo();
			//进程名就为包名
			String packname = processInfo.processName;
			
			MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{processInfo.pid});
			
			long memsize = memoryInfos[0].getTotalPrivateDirty()*1024l;
			taskInfo.setMemsize(memsize);
			//根据包名得到清单文件
			try {
				PackageInfo packInfo = pm.getPackageInfo(packname, 0);
				Drawable icon = packInfo.applicationInfo.loadIcon(pm);
				String name = packInfo.applicationInfo.loadLabel(pm).toString();
				
				int flags = packInfo.applicationInfo.flags;
				if((flags&ApplicationInfo.FLAG_SYSTEM)==0){
					//不是系统进程，用户进程
					taskInfo.setUserTask(true);
				}else{
					taskInfo.setUserTask(false);
				}
				taskInfo.setName(name);
				taskInfo.setPackname(packname);
				taskInfo.setIcon(icon);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				taskInfo.setName(packname);
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
			}
			taskInfos.add(taskInfo);
			
			
		}
		
		return taskInfos;
	}
}
