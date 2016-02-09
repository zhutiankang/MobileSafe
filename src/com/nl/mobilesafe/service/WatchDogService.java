package com.nl.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

import com.nl.mobilesafe.EnterPwdActivity;
import com.nl.mobilesafe.db.dao.AppLockDAO;
/**
 * 看门狗代码，监视系统程序的运行状态
 * @author 追梦
 *
 */
public class WatchDogService extends Service {
	private ActivityManager am;
	private boolean flag;
	private AppLockDAO dao;
	private InnerReceiver receiver;
	private String tempStopProtectPackname;
	
	private ScreenOffReceiver offreceiver;//自定义锁屏广播接收者
	
	private List<String> protectPacknames;
	private Intent intent;
	
	private DataChangeReceiver dataChangeReceiver;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		dataChangeReceiver = new DataChangeReceiver();
		registerReceiver(dataChangeReceiver, new IntentFilter("com.nl.mobilesafe.db.dao.datachanged"));
		
		receiver = new InnerReceiver();
		registerReceiver(receiver, new IntentFilter("com.nl.mobilesafe.tempstop"));
		
		offreceiver = new ScreenOffReceiver();
		registerReceiver(offreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		dao = new AppLockDAO(this);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
		protectPacknames = dao.findAll();//存在内存中
		flag = true;
		
		//优化三  不在子线程中连续创建意图，事先准备好，节约时间
		intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		new Thread(){
			public void run() {
				while(flag){//看门狗，一直要监视，所以要有个死循环，不过为不太影响cpu，要有睡眠50毫秒，一般精确的监视系统都要一个死循环
					//不过要想关闭它（死循环）要有一个flag标志,
					//优化一    得到1个任务栈就行，不需要100个，尽可能优化每一个环节，写优秀代码
					List<RunningTaskInfo> tasks = am.getRunningTasks(1);
					
					String packname = tasks.get(0).topActivity.getPackageName();//通过得到正在运行的任务栈，找出正在运行的activity的包名
					//System.out.println("packname"+packname);//狗培训好了，开始看家
					//查询数据库太慢了，消耗资源，改成查询内存，时间效率与成本都是从牙缝里省出来的，比如淘宝服务器禁用5vUSB接口一年省200万
					
					//优化二 开始就把锁定的包名从数据库中查找出来，存在内存中，不用在每次查数据库，直接从内存中查找，将大大优化开启速度，
					if(protectPacknames.contains(packname)){
						
						//判断这个应用程序是否需要临时停止保护
						if(packname.equals(tempStopProtectPackname)){
							//停止保护,不执行代码
						}else{
							//开启保护
							
							//当前应用程序需要保护，蹦出来，弹出一个输入密码的界面
							
							intent.putExtra("packname", packname);
							startActivity(intent);
							System.out.println("kaiqikaiqikaiqi");
						}
						
					}
					SystemClock.sleep(20);//最后休息50毫秒让狗休息一下,20毫秒就可以
				}
			};
		}.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
		unregisterReceiver(receiver);
		receiver = null;
		
		unregisterReceiver(offreceiver);
		offreceiver = null;
		
		unregisterReceiver(dataChangeReceiver);
		dataChangeReceiver = null;
	}
	
	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("接收到了临时停止保护的应用程序");
			tempStopProtectPackname = intent.getStringExtra("packname");
		}
		
	}
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("锁屏了");//锁屏清空tempStopProtectPackname，解锁后需要密码才能进入应用
			tempStopProtectPackname = null;//将它置为空，锁屏之前因为，tempStopProtectPackname在内存中有保留，连续开启不需要输入密码
			
		}
		
	}
	
	//数据优化的结果，数据已经查找过了，不会在自动查找了，但数据会新添加或者删除，改变，这时要重新查找，最好的方法就是自定义广播接收者
	private class DataChangeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//数据被新添加或者删除，数据改变，要重新查找
			protectPacknames = dao.findAll();
		}
		
	}
	
}
