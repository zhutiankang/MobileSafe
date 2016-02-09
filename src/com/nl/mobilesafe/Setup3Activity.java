package com.nl.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_phone;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_phone  = (EditText) findViewById(R.id.et_phone);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		
		et_phone.setText(sp.getString("safeNumber", ""));
	}

	

	@Override
	public void showNext() {
		//�ж��Ƿ����ð�ȫ����
		String phone = et_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "��ȫ����û������", 1).show();
			return;
		}
		
		//Ӧ�ñ��氲ȫ����
		Editor edit = sp.edit();
		edit.putString("safeNumber", phone);
		edit.commit();
		
		Intent intent = new Intent(Setup3Activity.this, Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(Setup3Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_press_in, R.anim.tran_press_out);
		
	}
	public void selectContact(View view){
		Intent intent = new Intent(Setup3Activity.this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(data == null){
			return;
		}
		String phone = data.getStringExtra("phone").replace("-", "");
		et_phone.setText(phone);
		
	}
}
