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
 * ���������ݿ����ɾ�Ĳ�ҵ����
 * 
 * @author ׷��
 * 
 */
public class BlackNumberDAO {

	private BlackNumberDBOpenHelper helper;

	public BlackNumberDAO(Context context) {
		helper = new BlackNumberDBOpenHelper(context);// �κ���newһ��dao����ͬʱnewһ�����ݿ������
	}

	/**
	 * ��ѯ�����������Ƿ����
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
	 * ��ѯ���������������ģʽ
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
	 * ��ѯȫ������������
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
	 * ��ѯ���ֺ��������룬�����������ݣ�����û��������⣬��ҳ�������ݽ���ڴ�ռ�����
	 * @param offset ����һ��λ�ÿ�ʼ��ȡ����
	 * @param maxnumber һ�λ�ȡ��������
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
	 * ������� ����ģʽ��1�绰���� 2�������� 3 ȫ������
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
	 * �޸�����
	 */
	public void update(String number, String newmode) {

		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[] { number });
		db.close();
	}

	/**
	 * ɾ������
	 */
	public void delete(String number) {

		SQLiteDatabase db = helper.getWritableDatabase();

		db.delete("blacknumber", "number=?", new String[] { number });
		db.close();
	}
}
