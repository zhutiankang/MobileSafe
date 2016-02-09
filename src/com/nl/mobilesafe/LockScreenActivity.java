package com.nl.mobilesafe;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;

public class LockScreenActivity extends Activity {
	// 设备策略服务
	private DevicePolicyManager dpm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String password = intent.getStringExtra("password");
		dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

		dpm.lockNow();
		dpm.resetPassword(password, 0);
		// 清除sdcard上的数据
		// dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
		// 恢复出厂设置
		// dpm.wipeData(0);
		finish();

	}
}
