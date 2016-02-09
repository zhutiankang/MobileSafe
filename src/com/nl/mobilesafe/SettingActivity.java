package com.nl.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nl.mobilesafe.service.AddressService;
import com.nl.mobilesafe.service.CallSmsSafeService;
import com.nl.mobilesafe.service.WatchDogService;
import com.nl.mobilesafe.ui.SettingClickView;
import com.nl.mobilesafe.ui.SettingItemView;
import com.nl.mobilesafe.utils.MD5Utils;
import com.nl.mobilesafe.utils.ServiceStatusUtils;

public class SettingActivity extends Activity {
	private SettingItemView siv_update;
	private SharedPreferences sp;
	private SettingItemView siv_show_address;
	private Intent showAddressIntent;

	private SettingClickView scv_changebg;

	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;

	// ���Ź�����
	private SettingItemView siv_watchdog;
	private Intent watchDogIntent;
	private AlertDialog dialog;
	private EditText et_setup_pwd;
	private EditText et_setup_confirm;
	@Override
	protected void onResume() {//�ɼ������»�ȡ����ʱ�����ԣ���С��������ջ�����������
		super.onResume();
		boolean isRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.nl.mobilesafe.service.AddressService");
		siv_show_address.setChecked(isRunning);
		boolean Running = ServiceStatusUtils.isServiceRunning(this,
				"com.nl.mobilesafe.service.CallSmsSafeService");
		siv_callsms_safe.setChecked(Running);
		// ����һ�������Ƿ����еĹ�����ȽϺ�
		boolean dogRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.nl.mobilesafe.service.WatchDogService");
		siv_watchdog.setChecked(dogRunning);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		/**
		 * ���ø���
		 */
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean flag = sp.getBoolean("update", false);
		if (flag) {
			// �Զ������Ѿ�����
			siv_update.setChecked(true);
			// siv_update.setText("�Զ������Ѿ�����");
		} else {
			// �Զ������Ѿ��ر�
			siv_update.setChecked(false);
			// siv_update.setText("�Զ������Ѿ��ر�");
		}
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
				// �Ѿ����Զ�����
				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					// siv_update.setText("�Զ������Ѿ��ر�");
					edit.putBoolean("update", false);
				} else {
					// û�д��Զ�����
					siv_update.setChecked(true);
					// siv_update.setText("�Զ������Ѿ�����");
					edit.putBoolean("update", true);
				}
				edit.commit();
			}
		});

		/**
		 * ������ʾ��ַ
		 */
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		// sharedPreference�����б׶ˣ�����ں�̨ɱ������show_address��Ϊtrue���Ի����¿�������
		// �û����鲻��
		// boolean show_address = sp.getBoolean("show_address", false);
		// siv_show_address.setChecked(show_address);
		// if(address){
		// //�Զ������Ѿ�����
		// siv_show_address.setChecked(true);
		// // siv_update.setText("�Զ������Ѿ�����");
		// }else{
		// //�Զ������Ѿ��ر�
		// siv_show_address.setChecked(false);
		// // siv_update.setText("�Զ������Ѿ��ر�");
		// }

		// ����һ�������Ƿ����еĹ�����ȽϺ�
		boolean isRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.nl.mobilesafe.service.AddressService");
		siv_show_address.setChecked(isRunning);
		showAddressIntent = new Intent(this, AddressService.class);
		siv_show_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Editor edit = sp.edit();
				// �Ѿ����Զ�����
				if (siv_show_address.isChecked()) {
					siv_show_address.setChecked(false);
					// siv_update.setText("�Զ������Ѿ��ر�");
					// edit.putBoolean("show_address", false);
					stopService(showAddressIntent);
				} else {
					// û�д��Զ�����
					siv_show_address.setChecked(true);
					// siv_update.setText("�Զ������Ѿ�����");
					// edit.putBoolean("show_address", true);
					startService(showAddressIntent);
				}
				// edit.commit();
			}
		});

		/**
		 * ���ù�������ʾ����
		 */
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		final String[] items = { "��͸��", "������", "��ʿ��", "������", "ƻ����" };
		int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);
		scv_changebg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("�����ر߿���");
				// ��Ҫ�ٵõ�һ��which�����Ի���ǰһ�Σ����Ի������Ҫһ�Σ���ȻĬ��Ϊ0
				int which = sp.getInt("which", 0);
				builder.setSingleChoiceItems(items, which,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								Editor edit = sp.edit();
								edit.putInt("which", which);
								scv_changebg.setDesc(items[which]);
								edit.commit();

								dialog.dismiss();
							}
						});
				builder.setNegativeButton("cancel", null);
				builder.show();
			}
		});

		/**
		 * ���ú���������
		 */
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);

		// ����һ�������Ƿ����еĹ�����ȽϺ�
		boolean Running = ServiceStatusUtils.isServiceRunning(this,
				"com.nl.mobilesafe.service.CallSmsSafeService");
		siv_callsms_safe.setChecked(Running);
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (siv_callsms_safe.isChecked()) {
					siv_callsms_safe.setChecked(false);
					stopService(callSmsSafeIntent);
				} else {
					siv_callsms_safe.setChecked(true);
					startService(callSmsSafeIntent);
				}

			}
		});

		/**
		 * ���ó�����
		 */
		siv_watchdog = (SettingItemView) findViewById(R.id.siv_watchdog);

		// ����һ�������Ƿ����еĹ�����ȽϺ�
		boolean dogRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.nl.mobilesafe.service.WatchDogService");
		siv_watchdog.setChecked(dogRunning);
		watchDogIntent = new Intent(this, WatchDogService.class);
		siv_watchdog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (siv_watchdog.isChecked()) {
					siv_watchdog.setChecked(false);
					stopService(watchDogIntent);
				} else {
					siv_watchdog.setChecked(true);
					startService(watchDogIntent);
					showSetupPwdDialog();
				}

			}
		});

	}
	/**
	 * ��������Ի���
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(SettingActivity.this);
		// �Զ���һ�������ļ�
		View view = View.inflate(SettingActivity.this,
				R.layout.dialog_setup_password, null);
		// builder.setView(view);
		// builder.show();
		// �������д�����Ϊ������Ͱ汾��ģ������Ϊ�����ۣ�һ��Сϸ��
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
		Button ok = (Button) view.findViewById(R.id.ok);
		Button cancel = (Button) view.findViewById(R.id.cancel);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �ѶԻ�������
				dialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ȡ������
				String password = et_setup_pwd.getText().toString().trim();
				String password_confirm = et_setup_confirm.getText().toString()
						.trim();
				if (TextUtils.isEmpty(password)
						|| TextUtils.isEmpty(password_confirm)) {
					Toast.makeText(SettingActivity.this, "���ǣ�����Ϊ��", 0).show();
					return;
				}
				// �ж��Ƿ�һ�£���ȥ����
				if (password.equals(password_confirm)) {
					// һ�µĻ�
					/**
					 * �������� �����Ի��� �����ֻ���������
					 */
					Editor edit = sp.edit();
					edit.putString("apkpwd", MD5Utils.md5Password(password));
					edit.commit();

					dialog.dismiss();
					
				} else {
					Toast.makeText(SettingActivity.this, "���ǣ����벻һ��", 0).show();
					return;
				}
			}
		});
	}

}