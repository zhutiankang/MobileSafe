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
	 * 扫描手机里面所有应用程序的缓存信息
	 */
	private void scanCache(){
		pm = getPackageManager();
		new Thread(){
			public void run() {
				Method getPackageSizeInfo = null;
				Method[] methods = PackageManager.class.getMethods();//若写在下面的for循环中，会影响时间效率，因为每个info都要查找全部的方法，
				for (Method method : methods) {                      //为提高时间效率，开始就要先把方法找的，不再找方法上浪费时间
					if("getPackageSizeInfo".equals(method.getName())){
						getPackageSizeInfo = method;
						break;//查找到之后，不再循环想下找了，节约时间
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
					SystemClock.sleep(50);//不要太快让用户感觉到
				}
				//在子线程中更新ui,tv_scan_status的状态,runonuiThread
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tv_scan_status.setText("扫描完毕...");
						
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
				//在子线程中更新ui,tv_scan_status的状态
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv_scan_status.setText("正在扫描："+ appInfo.loadLabel(pm));
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
			final ApplicationInfo appInfo = appInfos.get(position);//单个原因
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
			holder.tv_cache_size.setText("缓存大小："+Formatter.formatFileSize(getApplicationContext(), cacheSizes.get(position)));
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
//					try {//不可以直接清理缓存，需要权限，但该权限必须是系统级别的应用才可以申请该权限，普通应用会出错
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
					//receiver接收者，调用者，是那一个对象要调用该方法，PackageManager pm对象
					method.invoke(pm, Integer.MAX_VALUE,new MyDataObserver());
				} catch (Exception e) {
					e.printStackTrace();
				}
				//刷新界面
				appInfos.clear();
				cacheSizes.clear();
				adapter.notifyDataSetChanged();
				Toast.makeText(this, "释放了"+Formatter.formatFileSize(this, total)+"空间", 1).show();
				
				return;//执行完方法就返回不往下执行了,再继续查找，也可以用break;
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
