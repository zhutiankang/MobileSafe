package com.nl.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.nl.mobilesafe.service.UpdateWidgetService;
//����Ĺ㲥������
public class MyWidget extends AppWidgetProvider {
	
	//���յ�widget������,ɾ�����ƶ������widget��,ִ�ж��,һ����widget��ִ��Onreceiver����,�����ſ�������
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
//		Intent i = new Intent(context, UpdateWidgetService.class);
//		context.startService(i);
	}
	//���£�����
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {//��һ�δ���widgetʱִ�У�����ִֻ��һ�Σ����ܴ�������widget
		super.onEnabled(context);
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}

	@Override
	public void onDisabled(Context context) {//�Ƴ����һ��widgetʱִ�У�����ִֻ��һ��
		super.onDisabled(context);
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
	}
	
}
