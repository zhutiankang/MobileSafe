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
	// 震动
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address);
		et_phone = (EditText) findViewById(R.id.et_phone);
		tv_address = (TextView) findViewById(R.id.tv_address);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		// 查询效果优化,当号码发生变化的时候，归属地同时变换，动态查找
		et_phone.addTextChangedListener(new TextWatcher() {

			// 当文本变化的时候调用
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() >= 3) {
					String address = NumberAddressQueryUtils.getAddress(s
							.toString());
					tv_address.setText("归属地：      " + address);
				}
			}

			// 当文本变化之前调用
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			// 当文本变化之后调用
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void numberAddressQuery(View view) {
		String phone = et_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "号码为空，请输入", 1).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_phone.startAnimation(shake);

			// 当电话号码为空的时候，振动
			// vibrator.vibrate(2000);振动2秒
			// 振动效果停200毫秒，振动200毫秒，再停300毫秒
			long[] pattern = { 200, 200, 300, 400, 500, 1000 };
			// -1不重复，非-1为从pattern的指定下标开始重复,包括0，1
			vibrator.vibrate(pattern, -1);

			return;
		} else {
			// 去数据库查询归属地
			Log.i(TAG, "您要查询的号码：" + phone);
			// 1.网络查询，2.本地数据库查询
			// 写一个数据库工具类去查询数据库
			String address = NumberAddressQueryUtils.getAddress(phone);
			tv_address.setText("归属地：      " + address);
		}
	}
}
