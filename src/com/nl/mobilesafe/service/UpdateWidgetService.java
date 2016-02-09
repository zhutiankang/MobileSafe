package com.nl.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.nl.mobilesafe.R;
import com.nl.mobilesafe.receiver.MyWidget;
import com.nl.mobilesafe.utils.SystemInfoUtils;

public class UpdateWidgetService extends Service {

	private Timer timer;
	private TimerTask task;
	private ScreenOffReceiver offreceiver;
	private ScreenOnReceiver onreceiver;
	/**
	 * widget管理器
	 */
	private AppWidgetManager awm;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		offreceiver = new ScreenOffReceiver();
		onreceiver = new ScreenOnReceiver();
		registerReceiver(offreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(onreceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		startTime();

	}

	private void startTime() {
		awm = AppWidgetManager.getInstance(this);
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				System.out.println("更新widget");
				// 设置更新的组件是谁，更新MyWidget组件
				ComponentName provider = new ComponentName(
						getApplicationContext(), MyWidget.class);
				// 中间人，远程view对象，让桌面程序通过它调用手机卫士的内存空间中的Mywidget布局
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);

				views.setTextViewText(
						R.id.process_count,
						"正在运行的进程："
								+ SystemInfoUtils
										.getRunningProcessCount(getApplicationContext())
								+ "个");
				views.setTextViewText(
						R.id.process_memory,
						"可用内存："
								+ Formatter
										.formatFileSize(
												getApplicationContext(),
												SystemInfoUtils
														.getAvailMem(getApplicationContext())));
				// 延期意图，描述一个动作，这个动作是由另外一个应用程序执行的，，非延期意图，是让本程序本事执行的

				// 自定义一个广播事件，杀死后台进程的事件，大声叫一声，被清理进程的广播接收者收到
				Intent intent = new Intent();
				intent.setAction("com.nl.mobilesafe.killall");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent,//Intent已经通过PengIntent发送出去，不需要在sendBroadcast(intent)
						PendingIntent.FLAG_UPDATE_CURRENT);// 第二次广播把第一次广播覆盖掉
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 0, 3000);// 计时器，计时器任务，计时器延迟0毫秒（即立刻）执行task任务,并没3秒执行一次
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopTime();
		unregisterReceiver(offreceiver);
		unregisterReceiver(onreceiver);
		offreceiver = null;
		onreceiver = null;
	}

	private void stopTime() {
		if(timer!=null&&task!=null){//用if(xx!=null)可防止空指针异常
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("锁屏了");
			stopTime();
			
		}
		
	}
	private class ScreenOnReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("屏幕解锁了");
			startTime();
		}
		
	}
}
