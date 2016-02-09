package com.nl.mobilesafe;

import com.nl.mobilesafe.utils.MD5Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * ����ȫ��ʿû����ȫ�˳�����С���ӱ�Ϊ��̨���̵Ȼ���������л��������⣬��������л��������⣬������ջ�����⣬ֻ��Ҫ�ѣ�EnterPwdActivity���ɵ���ģʽ�ͽ����
 * Ҳ���Խ������ȫ��ʿ�����󣬳���������EnterPwdActivity�����⣬��Ϊ���Ź����ּ����������������ˣ�����������棬����Ϊ��������ǰ�ȫ��ʿ�ģ���⵽��ȫ��ʿ��������
 * �ֵ���һ��������棬������ѭ����ȥ
 * @author ׷��
 *
 */
public class EnterPwdActivity extends Activity {
	private EditText et_password;
	private TextView tv_name;
	private ImageView iv_icon;
	private PackageManager pm;
	private String packname;
	private ActivityManager am;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		setContentView(R.layout.activity_enter_pwd);
		et_password = (EditText) findViewById(R.id.et_password);
		tv_name = (TextView) findViewById(R.id.tv_name);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		Intent intent = getIntent();
		packname = intent.getStringExtra("packname");
		pm = getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packname, 0);
			tv_name.setText(info.loadLabel(pm));
			iv_icon.setImageDrawable(info.loadIcon(pm));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void click(View view){
		String pwd = et_password.getText().toString().trim();
		if(TextUtils.isEmpty(pwd)){
			Toast.makeText(this, "���벻��Ϊ��", 0).show();
			return;
		}
		String apkpwd = sp.getString("apkpwd", "");
		//������ȷ������Ϊ123
		if(apkpwd.equals(MD5Utils.md5Password(pwd))){
			//���߿��Ź������������������ȷ�ˣ���ʱֹͣ���� 
			//�������Զ���Ĺ㲥�����������ͬ�����ͨ�ţ��Ĵ������������������ͬ��activity�ȣ��������ð󶨷���ȷ�ʽ���ͨ��
			Intent intent = new Intent();
			intent.setAction("com.nl.mobilesafe.tempstop");
			intent.putExtra("packname", packname);
			sendBroadcast(intent);
			finish();
		}else{
			Toast.makeText(this, "�������", 0).show();
		}
		
	}
	//�����ؼ��ص�����
	@Override
	public void onBackPressed() {
		//�ص�����
//		<intent-filter>
//        <action android:name="android.intent.action.MAIN" />
//        <category android:name="android.intent.category.HOME" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <category android:name="android.intent.category.MONKEY"/>
//    </intent-filter>
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		//�����棬��ʾ��ǰ���е�activity��С��������ִ��ondestory,����onCreate��������ִ�У�ִֻ��onstop����
	}
	@Override
	protected void onStop() {
		super.onStop();
		//���治�ɼ�ʱfinish�����棬������棬�������ͼ�겻��Ӧ
		finish();
		am.killBackgroundProcesses(packname);
		
	}
}
