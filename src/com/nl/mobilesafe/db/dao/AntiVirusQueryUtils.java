package com.nl.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class AntiVirusQueryUtils {

	public static String path = "/data/data/com.nl.mobilesafe/files/antivirus.db";

	
	public static boolean isVirus(String md5) {
		boolean result = false;//��ֱ��return true;�α����ݿ�����û�йرյ�
		//�����ݿ�
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5 = ?", new String[]{md5});
		if(cursor!=null&&cursor.moveToFirst()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
}
