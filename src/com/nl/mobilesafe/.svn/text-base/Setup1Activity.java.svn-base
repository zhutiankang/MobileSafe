package com.nl.mobilesafe;

import android.content.Intent;
import android.os.Bundle;


public class Setup1Activity extends BaseSetupActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
	}
	
	public void showNext() {
		Intent intent = new Intent(Setup1Activity.this,Setup2Activity.class);
		startActivity(intent);
		//关闭当前界面
		finish();
		//要求在finish()或者是startActivity(intent)后面执行
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	

	@Override
	public void showPre() {
		
	}
	
}
