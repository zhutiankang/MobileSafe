package com.nl.mobilesafe;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.nl.mobilesafe.receiver.MyAdmin;

public class Setup4Activity extends BaseSetupActivity {
	private SharedPreferences sp;
	private CheckBox cb_protected;
	private ComponentName mDeviceAdminSample;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_protected = (CheckBox) findViewById(R.id.cb_protected);
		boolean configed = sp.getBoolean("configed", false);
		cb_protected.setChecked(configed);
		cb_protected.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Editor edit = sp.edit();
				edit.putBoolean("configed", isChecked);
				edit.commit();
				if(isChecked){
					openAdmin();
				}
			}
		});
	}

	@Override
	public void showNext() {

		// ����Ƿ�����������
		if (cb_protected.isChecked() == false) {
			Toast.makeText(this, "û�п�����������", 1).show();
			return;
		}

		Intent intent = new Intent(Setup4Activity.this, LostFindActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(Setup4Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_press_in, R.anim.tran_press_out);

	}
	/**
	 * �ô���ȥ��������Ա
	 */
	private void openAdmin() {
		// ����һ����ͼ��Ŀ���ǿ����豸�ĳ�������Ա
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		//��Ҫ����˭
		//��Ҫ����˭��---��������Ա
		mDeviceAdminSample = new ComponentName(this,MyAdmin.class);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		// Ȱ˵�û���������ԱȨ�޵ĺô�
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"���ǣ�������һ����������İ����Ͳ���ʧ����");
		startActivity(intent);
	}
}
