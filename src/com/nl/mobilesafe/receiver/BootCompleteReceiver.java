package com.nl.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver {
	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// ��ȡ֮ǰ�����sim����Ϣ
		String saveSim = sp.getString("sim", "");
		// ��ȡ��ǰ��sim����Ϣ
		String realSim = tm.getSimSerialNumber();
		// �Ƚ��Ƿ�һ��
		if (saveSim.equals(realSim)) {
			// sim��һ�»���ͬһ������
		} else {
			// sim���������һ�����Ÿ���ȫ����
			System.out.println("sim�����,��Ҫ͵͵������");
			// Toast.makeText(context, "sim�����", 1).show();
			String safeNumber = sp.getString("safeNumber", "");
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(safeNumber, null, "sim card change !",
					null, null);
		}
	}

}
