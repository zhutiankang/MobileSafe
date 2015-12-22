package com.nl.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {
	private ListView list_select_contact;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		list_select_contact = (ListView) findViewById(R.id.list_select_contact);
		final List<Map<String, String>> data = getContactInfo();
		list_select_contact.setAdapter(new SimpleAdapter(this, data,
				R.layout.cotact_item_view, new String[] { "name", "phone" },
				new int[] { R.id.tv_name, R.id.tv_phone }));
		list_select_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = data.get(position).get("phone");
				
				Intent data = new Intent();
				data.putExtra("phone", phone);
				setResult(0, data);
				
				//当前页面关闭掉
				finish();
			}
			
			
		});
	}
	
	
	
	/**
	 * 读取手机联系人
	 * 
	 * @return
	 */
	private List<Map<String, String>> getContactInfo() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		//Map<String, String> map = new HashMap<String, String>();
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");//对应数据库下的raw_contacts表
		Uri dataUri = Uri.parse("content://com.android.contacts/data");//不仅对应contacts2.db中的data表还对应与
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },//data相关的视图view_data
				null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			String contact_id;
			while (cursor.moveToNext()) {
				contact_id = cursor.getString(0);
				Cursor c = getContentResolver().query(dataUri,
						new String[] { "data1", "mimetype" }, "contact_id = ?",
						new String[] { contact_id }, null);//每一个contact_id 就能找到一组数据
				map = new HashMap<String, String>();//每遇到一组数据，就要new一个对象
				if (c != null && c.getCount() > 0) {
					String data1;
					String mimetype;
					while (c.moveToNext()) {
						data1 = c.getString(0);
						mimetype = c.getString(1);
						System.out.println("data1=" + data1 + "==mimetype=="
								+ mimetype);
						if ("vnd.android.cursor.item/name"
								.equals(mimetype)) {
							map.put("name", data1);
						} else if ("vnd.android.cursor.item/phone_v2"
								.equals(mimetype)) {
							map.put("phone", data1);
						}
					}
					c.close();
				}
				data.add(map);
			}

			cursor.close();
		}
		
		return data;
	}
}
