package com.nl.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.nl.mobilesafe.service.AutoCleanService;
import com.nl.mobilesafe.utils.ServiceStatusUtils;

public class TaskSettingActivity extends Activity {
	
	private CheckBox cb_show_system;
	private CheckBox cb_auto_clean;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		cb_show_system =(CheckBox) findViewById(R.id.cb_show_system);
		cb_auto_clean =(CheckBox) findViewById(R.id.cb_auto_clean);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean flag = sp.getBoolean("showsystem", false);
		cb_show_system.setChecked(flag);
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor edit = sp.edit();
				edit.putBoolean("showsystem", isChecked);
				edit.commit();
			}
		});
		
//		//��ʱ��������ʱ
//		CountDownTimer cd = new CountDownTimer(3000, 1000) {//�ü�ʱ������ִ��3��,3���finish��ÿ1��ִ��һ��onTick
//			
//			@Override
//			public void onTick(long millisUntilFinished) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onFinish() {
//				// TODO Auto-generated method stub
//				
//			}
//		};
		
		cb_auto_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//�����Ĺ㲥�¼���һ������Ĺ㲥�¼������嵥�ļ����ù㲥�������ǲ�����Ч��
				//ֻ���ڴ�����ע��Ż���Ч
				
				Intent intent = new Intent(getApplicationContext(), AutoCleanService.class);
				if(isChecked){
					startService(intent);
				}else{
					stopService(intent);
				}
			}
		});
	}
	@Override
	protected void onStart() {
		super.onStart();
		boolean running = ServiceStatusUtils.isServiceRunning(this, "com.nl.mobilesafe.service.AutoCleanService");
		cb_auto_clean.setChecked(running);
		
	}
}
