package com.nl.mobilesafe;

import com.nl.mobilesafe.ui.SettingItemView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView siv_bind;
	//读取手机卡的信息
	private TelephonyManager tm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		siv_bind = (SettingItemView) findViewById(R.id.siv_bind);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		String sim = sp.getString("sim", null);
//		if(flag){
//			siv_bind.setChecked(true);
//		}else{
//			siv_bind.setChecked(false);
//		}
		if(TextUtils.isEmpty(sim)){
			siv_bind.setChecked(false);
		}else{
			siv_bind.setChecked(true);
		}
		siv_bind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
				//保存sim卡的序列号
				String sim = tm.getSimSerialNumber();
				if(siv_bind.isChecked()){
					siv_bind.setChecked(false);
					edit.putString("sim", null);
				}else{
					siv_bind.setChecked(true);
					edit.putString("sim", sim);
				}
				edit.commit();
			}
		});
	}
	
	
	

	public void showPre() {
		Intent intent = new Intent(Setup2Activity.this,Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_press_in, R.anim.tran_press_out);
	}

	@Override
	public void showNext() {
		//检查sim卡是否绑定
		String sim = sp.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			//没有绑定sim卡
			Toast.makeText(this, "sim卡没有绑定", 1).show();
			return;
		}
		Intent intent = new Intent(Setup2Activity.this,Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);		
	}

	
}
