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
		//�رյ�ǰ����
		finish();
		//Ҫ����finish()������startActivity(intent)����ִ��
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	

	@Override
	public void showPre() {
		
	}
	
}
