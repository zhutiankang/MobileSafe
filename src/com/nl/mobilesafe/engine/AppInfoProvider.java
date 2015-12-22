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

/**ҵ�񷽷����ṩ�ֻ����氲װ������Ӧ�ó�����Ϣ
 * @author ׷��
 *
 */
public class AppInfoProvider {
	
	/**��ȡ���а�װ��Ӧ�ó�����Ϣ
	 * @param context ������
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		SystemClock.sleep(500);
		//�õ���������
		PackageManager pm = context.getPackageManager();
		//������а�װ��ϵͳ�ϵ�Ӧ�ó���İ���Ϣ
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		AppInfo appInfo;
		for (PackageInfo packInfo : packInfos) {
			//packInfo���൱��һ��Ӧ�ó���APK�����嵥�ļ�
			String packageName = packInfo.packageName;
			Drawable icon = packInfo.applicationInfo.loadIcon(pm);
			String name = packInfo.applicationInfo.loadLabel(pm).toString();
			appInfo = new AppInfo();
			int flags = packInfo.applicationInfo.flags;//Ӧ�ó���ı��
			//����ϵͳ�����Ӧ�ó����һ���̶���ţ�һ��Ӧ�ó���װ���ֻ���id�͹̶�����
			int uid = packInfo.applicationInfo.uid;//Ӧ�ó�����û�id,
			//��inputstream��ȡͳ�Ƶ��ϴ����ص�����������google��ר�ŵ�api��������ͳ��
//			File rcvfile = new File("/proc/uid_stat/"+uid+"/tcp_rcv");
//			File sndfile = new File("/proc/uid_stat/"+uid+"/tcp_snd");

			if((flags&ApplicationInfo.FLAG_SYSTEM)==0){
				//����ϵͳ�����������û�����
				appInfo.setUserApp(true);
			}else{
				//ϵͳ����
				appInfo.setUserApp(false);
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
				//�����ⲿ�洢���������ڲ��洢
				appInfo.setInRom(true);
			}else{
				//�ⲿ�洢
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
