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
 * 若安全卫士没有完全退出，按小房子变为后台进程等会引起界面切换混乱问题，解决界面切换混乱问题，是任务栈的问题，只需要把，EnterPwdActivity做成单例模式就解决了
 * 也可以解决将安全卫士锁定后，出现无数个EnterPwdActivity的问题，因为看门狗发现加锁后，又运行起来了，弹出密码界面，又因为密码界面是安全卫士的，检测到安全卫士又在运行
 * 又弹出一个密码界面，这样死循环下去
 * @author 追梦
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
			Toast.makeText(this, "密码不能为空", 0).show();
			return;
		}
		String apkpwd = sp.getString("apkpwd", "");
		//假设正确的密码为123
		if(apkpwd.equals(MD5Utils.md5Password(pwd))){
			//告诉看门狗这个程序密码输入正确了，临时停止保护 
			//经常用自定义的广播来完成两个不同组件的通信，四大组件，或者是两个不同的activity等，还可以用绑定服务等方式完成通信
			Intent intent = new Intent();
			intent.setAction("com.nl.mobilesafe.tempstop");
			intent.putExtra("packname", packname);
			sendBroadcast(intent);
			finish();
		}else{
			Toast.makeText(this, "密码错误", 0).show();
		}
		
	}
	//按返回键回到桌面
	@Override
	public void onBackPressed() {
		//回到桌面
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
		//会桌面，表示当前所有的activity最小化，不会执行ondestory,所以onCreate不会重新执行，只执行onstop方法
	}
	@Override
	protected void onStop() {
		super.onStop();
		//界面不可见时finish掉界面，清掉缓存，避免包名图标不对应
		finish();
		am.killBackgroundProcesses(packname);
		
	}
}
