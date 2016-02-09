package com.nl.mobilesafe;

import com.nl.mobilesafe.utils.SmsUtils;
import com.nl.mobilesafe.utils.SmsUtils.BackUpCallBack;
import com.nl.mobilesafe.utils.SmsUtils.RestoreCallBack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AtoolActivity extends Activity {
	private ProgressDialog pd;
	private ProgressDialog pdr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	
	/**����¼��������������ز�ѯҳ��
	 * @param view
	 */
	public void numberAddressQuery(View view){
		Intent intent = new Intent(this,NumberAddressActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**���ű���
	 * @param view
	 */
	public void smsBackup(View view){
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("���ڱ���");
		pd.show();
		new Thread(){
			
			public void run() {
				try {
					SmsUtils.backupSms(AtoolActivity.this,new BackUpCallBack() {
						
						@Override
						public void onSmsBackup(int process) {
							pd.setProgress(process);
						}
						
						@Override
						public void beforeBackup(int max) {
							pd.setMax(max);
						}
					});
					
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolActivity.this, "���ݳɹ�", 0).show();
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolActivity.this, "����ʧ��", 0).show();
						}
					});
				} finally{
					pd.dismiss();
				}
				
			};
		}.start();
		
		
	}
	
	/**���Żָ�
	 * @param view
	 */
	public void smsRestore(View view){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("����");
		builder.setMessage("ȷ��Ҫ����������ݣ��ָ�����������");
		
		builder.setNegativeButton("ȡ��", null);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				pdr = new ProgressDialog(AtoolActivity.this);
				pdr.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pdr.setMessage("���ڻָ�����...");
				pdr.show();
				
				new Thread(){
					public void run() {
						try {
							SmsUtils.restoreSms(getApplicationContext(), new RestoreCallBack() {
								
								@Override
								public void onSmsRestore(int process) {
									pdr.setProgress(process);
								}
								
								@Override
								public void beforeRestore(int max) {
									pdr.setMax(max);
								}
							});
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(AtoolActivity.this, "�ָ��ɹ�", 0).show();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(AtoolActivity.this, "�ָ�ʧ��", 0).show();
								}
							});
							
						}finally{
							pdr.dismiss();
						}
						
					};
				}.start();
			}
		});
		builder.show();
		
		
	}
}
