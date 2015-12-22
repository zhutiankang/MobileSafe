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
		
//		//计时器，倒计时
//		CountDownTimer cd = new CountDownTimer(3000, 1000) {//该计时器方法执行3秒,3秒后finish，每1秒执行一次onTick
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
				//锁屏的广播事件是一种特殊的广播事件，在清单文件配置广播接收者是不会生效的
				//只能在代码中注册才会生效
				
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
