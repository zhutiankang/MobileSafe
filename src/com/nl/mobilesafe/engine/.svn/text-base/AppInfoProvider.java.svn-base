package com.nl.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

import com.nl.mobilesafe.domain.AppInfo;

/**业务方法，提供手机里面安装的所有应用程序信息
 * @author 追梦
 *
 */
public class AppInfoProvider {
	
	/**获取所有安装的应用程序信息
	 * @param context 上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		SystemClock.sleep(500);
		//得到包管理者
		PackageManager pm = context.getPackageManager();
		//获得所有安装在系统上的应用程序的包信息
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		AppInfo appInfo;
		for (PackageInfo packInfo : packInfos) {
			//packInfo，相当于一个应用程序APK包的清单文件
			String packageName = packInfo.packageName;
			Drawable icon = packInfo.applicationInfo.loadIcon(pm);
			String name = packInfo.applicationInfo.loadLabel(pm).toString();
			appInfo = new AppInfo();
			int flags = packInfo.applicationInfo.flags;//应用程序的标记
			//操作系统分配给应用程序的一个固定编号，一旦应用程序被装到手机上id就固定不变
			int uid = packInfo.applicationInfo.uid;//应用程序的用户id,
			//用inputstream读取统计的上传下载的流量，不过google有专门的api进行流量统计
//			File rcvfile = new File("/proc/uid_stat/"+uid+"/tcp_rcv");
//			File sndfile = new File("/proc/uid_stat/"+uid+"/tcp_snd");

			if((flags&ApplicationInfo.FLAG_SYSTEM)==0){
				//不是系统程序，所以是用户程序
				appInfo.setUserApp(true);
			}else{
				//系统程序
				appInfo.setUserApp(false);
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
				//不是外部存储，所以是内部存储
				appInfo.setInRom(true);
			}else{
				//外部存储
				appInfo.setInRom(false);
			}
			
			appInfo.setPackname(packageName);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfos.add(appInfo);
		}
		
		
		return appInfos;
	}
	

}
