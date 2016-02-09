package com.nl.mobilesafe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
public class CleanCacheActivity extends Activity {
	private TextView tv_scan_status;
	private ProgressBar pb;
	private PackageManager pm;
	private ListView lv_container;
	private List<ApplicationInfo> appInfos;
	private List<Long> cacheSizes;
	private MyDataAdapter adapter;
	
	private long total=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		pb = (ProgressBar) findViewById(R.id.pb);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		lv_container = (ListView) findViewById(R.id.lv_container);
		scanCache();
	}
	/**
	 * ɨ���ֻ���������Ӧ�ó���Ļ�����Ϣ
	 */
	private void scanCache(){
		pm = getPackageManager();
		new Thread(){
			public void run() {
				Method getPackageSizeInfo = null;
				Method[] methods = PackageManager.class.getMethods();//��д�������forѭ���У���Ӱ��ʱ��Ч�ʣ���Ϊÿ��info��Ҫ����ȫ���ķ�����
				for (Method method : methods) {                      //Ϊ���ʱ��Ч�ʣ���ʼ��Ҫ�Ȱѷ����ҵģ������ҷ������˷�ʱ��
					if("getPackageSizeInfo".equals(method.getName())){
						getPackageSizeInfo = method;
						break;//���ҵ�֮�󣬲���ѭ���������ˣ���Լʱ��
					}
				}
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				pb.setMax(infos.size());
				int progress = 0;
				appInfos = new ArrayList<ApplicationInfo>();
				cacheSizes = new ArrayList<Long>();
				for (PackageInfo info : infos) {
					try {
						getPackageSizeInfo.invoke(pm, info.packageName,new MyStatsObserver());
					} catch (Exception e) {
						e.printStackTrace();
					}
					progress++;
					pb.setProgress(progress);
					SystemClock.sleep(50);//��Ҫ̫�����û��о���
				}
				//�����߳��и���ui,tv_scan_status��״̬,runonuiThread
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tv_scan_status.setText("ɨ�����...");
						
					}
				});
			};
		}.start();
	}
	private class MyStatsObserver extends IPackageStatsObserver.Stub{

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			final long cacheSize = pStats.cacheSize;
//			System.out.println("cache:"+Formatter.formatFileSize(getApplicationContext(), cacheSize));
//			System.out.println(pStats.packageName);
//			System.out.println("--------------");
			
			String packageName = pStats.packageName;
			final ApplicationInfo appInfo;
			try {
				appInfo = pm.getApplicationInfo(packageName, 0);
				//�����߳��и���ui,tv_scan_status��״̬
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv_scan_status.setText("����ɨ�裺"+ appInfo.loadLabel(pm));
						if(cacheSize>0){
							appInfos.add(appInfo);
							cacheSizes.add(cacheSize);
							total+=cacheSize;
							if(adapter==null){
								adapter = new MyDataAdapter();
								lv_container.setAdapter(adapter);
							}else{
								adapter.notifyDataSetChanged();
							}
						}
						
					}
				});
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	private class MyDataAdapter extends BaseAdapter{
	//	private ApplicationInfo appInfo;
		@Override
		public int getCount() {
			return appInfos.size();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ApplicationInfo appInfo = appInfos.get(position);//����ԭ��
			View view;
			ViewHolder holder;
			if(convertView!=null&&convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(getApplicationContext(), R.layout.list_item_clean_cache, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				holder.tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(appInfo.loadIcon(pm));
			holder.tv_name.setText(appInfo.loadLabel(pm));
			holder.tv_cache_size.setText("�����С��"+Formatter.formatFileSize(getApplicationContext(), cacheSizes.get(position)));
//			<action android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
//            <category android:name="android.intent.category.DEFAULT" />
//            <data android:scheme="package" />
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:"+appInfo.packageName));
					startActivity(intent);
				}
			});
//			holder.iv_delete.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					try {//������ֱ�������棬��ҪȨ�ޣ�����Ȩ�ޱ�����ϵͳ�����Ӧ�òſ��������Ȩ�ޣ���ͨӦ�û����
//						Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class,IPackageDataObserver.class);
//						method.invoke(pm, appInfo.packageName,new MyDataObserver());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
			return view;
		}
		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		
	}
	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_cache_size;
		ImageView iv_delete;
	}
	public void cleanCache(View view){
		
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if("freeStorageAndNotify".equals(method.getName())){
				try {
					//receiver�����ߣ������ߣ�����һ������Ҫ���ø÷�����PackageManager pm����
					method.invoke(pm, Integer.MAX_VALUE,new MyDataObserver());
				} catch (Exception e) {
					e.printStackTrace();
				}
				//ˢ�½���
				appInfos.clear();
				cacheSizes.clear();
				adapter.notifyDataSetChanged();
				Toast.makeText(this, "�ͷ���"+Formatter.formatFileSize(this, total)+"�ռ�", 1).show();
				
				return;//ִ���귽���ͷ��ز�����ִ����,�ټ������ң�Ҳ������break;
			}
		}
		
		
	}
	
	private class MyDataObserver extends IPackageDataObserver.Stub{

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			System.out.println(packageName+succeeded);
		}
		
	}
}
