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
		// 读取之前保存的sim卡信息
		String saveSim = sp.getString("sim", "");
		// 读取当前的sim卡信息
		String realSim = tm.getSimSerialNumber();
		// 比较是否一致
		if (saveSim.equals(realSim)) {
			// sim卡一致还是同一个哥们
		} else {
			// sim卡变更，发一个短信给安全号码
			System.out.println("sim卡变更,需要偷偷发短信");
			// Toast.makeText(context, "sim卡变更", 1).show();
			String safeNumber = sp.getString("safeNumber", "");
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(safeNumber, null, "sim card change !",
					null, null);
		}
	}

}
