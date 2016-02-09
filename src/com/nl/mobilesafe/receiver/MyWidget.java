package com.nl.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.nl.mobilesafe.service.UpdateWidgetService;
//特殊的广播接收者
public class MyWidget extends AppWidgetProvider {
	
	//接收到widget，创建,删除，移动，添加widget等,执行多次,一操作widget就执行Onreceiver方法,紧接着开启服务
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
//		Intent i = new Intent(context, UpdateWidgetService.class);
//		context.startService(i);
	}
	//更新，内容
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {//第一次创建widget时执行，并且只执行一次，不管创建几个widget
		super.onEnabled(context);
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}

	@Override
	public void onDisabled(Context context) {//移除最后一个widget时执行，并且只执行一次
		super.onDisabled(context);
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
	}
	
}
