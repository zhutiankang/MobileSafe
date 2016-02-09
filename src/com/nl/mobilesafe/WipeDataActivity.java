package com.nl.mobilesafe;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.os.Bundle;

public class WipeDataActivity extends Activity {
	// �豸���Է���
	private DevicePolicyManager dpm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

		
		// ���sdcard�ϵ�����
		dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
		// �ָ���������
		dpm.wipeData(0);
		finish();

	}
}
