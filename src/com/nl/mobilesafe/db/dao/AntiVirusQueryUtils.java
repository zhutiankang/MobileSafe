package com.nl.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class AntiVirusQueryUtils {

	public static String path = "/data/data/com.nl.mobilesafe/files/antivirus.db";

	
	public static boolean isVirus(String md5) {
		boolean result = false;//若直接return true;游标数据库容易没有关闭掉
		//打开数据库
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
