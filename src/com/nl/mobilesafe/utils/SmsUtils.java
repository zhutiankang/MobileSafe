package com.nl.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

/**短信备份还原工具类
 * @author 追梦
 *
 */
public class SmsUtils {
	
	/**备份短信的回调接口
	 * @author 追梦
	 *代码解耦：解除耦合
            暴露一个接口，提供一个回调函数、callBack
	 */
	public interface BackUpCallBack{
		
		/**开始备份的时候，设置进度的最大值
		 * @param max最大值
		 */
		public void beforeBackup(int max);
		
		/**备份过程中，增加的进度
		 * @param process 进度
		 */
		public void onSmsBackup(int process);
	}
	/**还原短信的回调接口
	 * @author 追梦
	 *代码解耦：解除耦合
            暴露一个接口，提供一个回调函数、callBack
	 */
	public interface RestoreCallBack{
		
		/**开始还原的时候，设置进度的最大值
		 * @param max最大值
		 */
		public void beforeRestore(int max);
		
		/**还原过程中，增加的进度
		 * @param process 进度
		 */
		public void onSmsRestore(int process);
	}
	/**备份短信
	 * @param context 上下文
	 */
	public static void backupSms(Context context,BackUpCallBack callBack) throws Exception{
		File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		
		serializer.startTag(null, "smss");
		
		ContentResolver resolver = context.getContentResolver();
		
		Uri uri = Uri.parse("content://sms/");//对应数据库下,与sms相关的所有的表都可操作，收件箱，发件箱等等
		Cursor cursor = resolver.query(uri , new String[]{"address","body","type","date"}, null, null, null);
		//设置进度条的最大值
		int max = cursor.getCount();
		callBack.beforeBackup(max);
		int process = 0;
		serializer.attribute(null, "max", max+"");
		if(cursor!=null&&cursor.getCount()>0){
			String address;
			String body;
			String type;
			String date;
			while(cursor.moveToNext()){
//				Thread.sleep(500);
				address = cursor.getString(0);
				body = cursor.getString(1);
				type = cursor.getString(2);
				date = cursor.getString(3);
				
				serializer.startTag(null, "sms");
				
				serializer.startTag(null, "address");
				serializer.text(address);
				serializer.endTag(null, "address");
				
				serializer.startTag(null, "body");
				serializer.text(body);
				serializer.endTag(null, "body");
				
				serializer.startTag(null, "type");
				serializer.text(type);
				serializer.endTag(null, "type");
				
				serializer.startTag(null, "date");
				serializer.text(date);
				serializer.endTag(null, "date");
				
				serializer.endTag(null, "sms");
				//每备份一条，设置进度条的进度
				process++;
				callBack.onSmsBackup(process);
			}
		}
		cursor.close();
		
		
		serializer.endTag(null, "smss");
		
		
		serializer.endDocument();
		fos.close();
	}
	public static void restoreSms(Context context,RestoreCallBack callBack) throws Exception{
		
		Uri url = Uri.parse("content://sms/");
		//清除现在短信数据
		ContentResolver resolver = context.getContentResolver();
		resolver.delete(url, null, null);
		
		
		//1.读取xml文件
		File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
		FileInputStream fis = new FileInputStream(file);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(fis, "utf-8");
		int eventType = parser.getEventType();
		
		ContentValues values = null;
		int process = 0;
		while(eventType!=XmlPullParser.END_DOCUMENT){
			String tagName = parser.getName();
			switch (eventType) {
			case XmlPullParser.START_TAG:
				//2.读取最大值
				if("smss".equals(tagName)){
					String max = parser.getAttributeValue(null, "max");
					callBack.beforeRestore(Integer.parseInt(max));
					//3.读取每一条短信的信息
				}else if("sms".equals(tagName)){
					values = new ContentValues();//每遇到一组数据，就要new一个对象
				}else if("address".equals(tagName)){
					values.put("address", parser.nextText());
				}else if("body".equals(tagName)){
					values.put("body", parser.nextText());
				}else if("type".equals(tagName)){
					values.put("type", parser.nextText());
				}else if("date".equals(tagName)){
					values.put("date", parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if("sms".equals(tagName)){
					//4.把短信插入到系统短信应用
					resolver.insert(url, values);
					process++;
					callBack.onSmsRestore(process);
				}
				break;
			default:
				break;
			}
			eventType = parser.next();
			
			
		}
		
		
	    
		
		fis.close();
		
		
	}
}
