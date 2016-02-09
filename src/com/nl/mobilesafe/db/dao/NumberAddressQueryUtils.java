package com.nl.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {

	// 知识点：手机号码11位，开头13x 、14x、15x、18x;
	//
	// 手机号码正则表达式：^1[3458]\\d{9}$
	//
	// 3位的号码：120、119、110、999（香港急救电话） 特殊号码：
	// 4位的号码：5554 模拟器
	// 5位的号码：10086 、10010客服电话
	// 7位号码：3543243 本地号码
	// 8位号码：83551234 本地号码
	//
	//
	// 4、长途电话的查询
	// 021 12345678 010-59790386
	// select location from data2 where area = "21"

	public static String path = "/data/data/com.nl.mobilesafe/files/address.db";

	/**
	 * 传一个号码进去，返回一个地址
	 * 
	 * @param number
	 * @return
	 */
	public static String getAddress(String number) {
		// assets下的数据库，无法直接访问，只需要把address.db拷贝到我们的/data/data/包名/files/address.db

		String address = number;
		SQLiteDatabase sdb = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		if (number.matches("^1[3458]\\d{9}$")) {

			Cursor cursor = sdb
					.rawQuery(
							"select location from data2 where id = (select outkey from data1 where id = ?)",
							new String[] { number.substring(0, 7) });

			if (cursor.moveToFirst()) {
				address = cursor.getString(0);
			}
			cursor.close();
		} else {
			switch (number.length()) {
			case 3:
				address = "特殊号码";
				break;
			case 4:
				address = "android模拟器";
				break;
			case 5:
				address = "客服电话";
				break;
			case 7:
				address = "本地电话";
				break;
			case 8:
				address = "本地电话";
				break;
			default:
				//长途电话
				if (number.length() > 10 && number.startsWith("0")) {
					// 010 12345678 area=10
					Cursor cursor = sdb.rawQuery(
							"select location from data2 where area = ?",
							new String[] { number.substring(1, 3) });

					if (cursor.moveToFirst()) {
						String location = cursor.getString(0);
						address = location.substring(0, location.length() - 2);
					}
					cursor.close();
					
					
					// 0855 12345678 area=855
					cursor = sdb.rawQuery(
							"select location from data2 where area = ?",
							new String[] { number.substring(1, 4) });

					if (cursor.moveToFirst()) {
						String location = cursor.getString(0);
						address = location.substring(0, location.length() - 2);
					}
					cursor.close();
					
				}
				break;

			}

		}

		sdb.close();// 只要是与数据库有关的，以用起来就写上.close();
		return address;

	}
}
