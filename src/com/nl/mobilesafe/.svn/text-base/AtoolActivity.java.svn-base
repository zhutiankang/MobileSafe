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
	
	
	/**点击事件，进入号码归属地查询页面
	 * @param view
	 */
	public void numberAddressQuery(View view){
		Intent intent = new Intent(this,NumberAddressActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**短信备份
	 * @param view
	 */
	public void smsBackup(View view){
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在备份");
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
							Toast.makeText(AtoolActivity.this, "备份成功", 0).show();
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolActivity.this, "备份失败", 0).show();
						}
					});
				} finally{
					pd.dismiss();
				}
				
			};
		}.start();
		
		
	}
	
	/**短信恢复
	 * @param view
	 */
	public void smsRestore(View view){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("警告");
		builder.setMessage("确定要清除现在数据，恢复备份数据吗");
		
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				pdr = new ProgressDialog(AtoolActivity.this);
				pdr.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pdr.setMessage("正在恢复数据...");
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
									Toast.makeText(AtoolActivity.this, "恢复成功", 0).show();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(AtoolActivity.this, "恢复失败", 0).show();
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
