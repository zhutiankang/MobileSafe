package com.nl.mobilesafe;

import com.nl.mobilesafe.db.dao.NumberAddressQueryUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressActivity extends Activity {

	private static final String TAG = "NumberAddressActivity";
	private EditText et_phone;
	private TextView tv_address;
	// ��
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address);
		et_phone = (EditText) findViewById(R.id.et_phone);
		tv_address = (TextView) findViewById(R.id.tv_address);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		// ��ѯЧ���Ż�,�����뷢���仯��ʱ�򣬹�����ͬʱ�任����̬����
		et_phone.addTextChangedListener(new TextWatcher() {

			// ���ı��仯��ʱ�����
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() >= 3) {
					String address = NumberAddressQueryUtils.getAddress(s
							.toString());
					tv_address.setText("�����أ�      " + address);
				}
			}

			// ���ı��仯֮ǰ����
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			// ���ı��仯֮�����
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void numberAddressQuery(View view) {
		String phone = et_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "����Ϊ�գ�������", 1).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_phone.startAnimation(shake);

			// ���绰����Ϊ�յ�ʱ����
			// vibrator.vibrate(2000);��2��
			// ��Ч��ͣ200���룬��200���룬��ͣ300����
			long[] pattern = { 200, 200, 300, 400, 500, 1000 };
			// -1���ظ�����-1Ϊ��pattern��ָ���±꿪ʼ�ظ�,����0��1
			vibrator.vibrate(pattern, -1);

			return;
		} else {
			// ȥ���ݿ��ѯ������
			Log.i(TAG, "��Ҫ��ѯ�ĺ��룺" + phone);
			// 1.�����ѯ��2.�������ݿ��ѯ
			// дһ�����ݿ⹤����ȥ��ѯ���ݿ�
			String address = NumberAddressQueryUtils.getAddress(phone);
			tv_address.setText("�����أ�      " + address);
		}
	}
}
