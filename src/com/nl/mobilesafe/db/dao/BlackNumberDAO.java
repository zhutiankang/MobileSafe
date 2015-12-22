package com.nl.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.nl.mobilesafe.db.BlackNumberDBOpenHelper;
import com.nl.mobilesafe.domain.BlackNumberInfo;

/**
 * 黑名单数据库的增删改查业务类
 * 
 * @author 追梦
 * 
 */
public class BlackNumberDAO {

	private BlackNumberDBOpenHelper helper;

	public BlackNumberDAO(Context context) {
		helper = new BlackNumberDBOpenHelper(context);// 任何人new一个dao对象，同时new一个数据库帮助类
	}

	/**
	 * 查询黑名单号码是否存在
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber where number=?",
				new String[] { number });
		if (cursor != null && cursor.moveToFirst()) {
			result = true;
		}
		cursor.close();
		db.close();

		return result;
	}
	/**
	 * 查询黑名单号码的拦截模式
	 */
	public String findMode(String number) {

		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacknumber where number=?",
				new String[] { number });
		if (cursor != null && cursor.moveToFirst()) {
			return cursor.getString(0);
		}
		cursor.close();
		db.close();

		return null;
	}
	/**
	 * 查询全部黑名单号码
	 */
	public List<BlackNumberInfo> findAll() {

		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery("select number,mode from blacknumber order by _id desc", null);
		if (cursor != null && cursor.getCount() > 0) {
			BlackNumberInfo info;
			String number;
			String mode;
			while (cursor.moveToNext()) {
				info = new BlackNumberInfo();
				number = cursor.getString(0);
				mode = cursor.getString(1);
				info.setNumber(number);
				info.setMode(mode);
				infos.add(info);
			}
		}
		cursor.close();
		db.close();

		return infos;
	}
	/**
	 * 查询部分黑名单号码，分批加载数据，解决用户体验问题，分页加载数据解决内存空间问题
	 * @param offset 从那一个位置开始获取数据
	 * @param maxnumber 一次获取多少数据
	 */
	public List<BlackNumberInfo> findPart(int offset,int maxnumber) {
		SystemClock.sleep(500);
		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset ?", 
						new String[]{String.valueOf(maxnumber),String.valueOf(offset)});
		if (cursor != null && cursor.getCount() > 0) {
			BlackNumberInfo info;
			String number;
			String mode;
			while (cursor.moveToNext()) {
				info = new BlackNumberInfo();
				number = cursor.getString(0);
				mode = cursor.getString(1);
				info.setNumber(number);
				info.setMode(mode);
				infos.add(info);
			}
		}
		cursor.close();
		db.close();

		return infos;
	}
	/**
	 * 添加数据 拦截模式：1电话拦截 2短信拦截 3 全部拦截
	 */
	public void add(String number, String mode) {

		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}

	/**
	 * 修改数据
	 */
	public void update(String number, String newmode) {

		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[] { number });
		db.close();
	}

	/**
	 * 删除数据
	 */
	public void delete(String number) {

		SQLiteDatabase db = helper.getWritableDatabase();

		db.delete("blacknumber", "number=?", new String[] { number });
		db.close();
	}
}
