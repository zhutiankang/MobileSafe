package com.nl.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.nl.mobilesafe.db.dao.BlackNumberDAO;

public class CallSmsSafeService extends Service {
	private InnerSmsReceiver receiver;
	private BlackNumberDAO dao;
	private TelephonyManager tm;
	private MyListener listener;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		dao = new BlackNumberDAO(this);
		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver,filter );
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
		
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}
	
	private class InnerSmsReceiver extends BroadcastReceiver{


		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("短信来了");
			//短信到来了
			//检查发件人是否是黑名单号码，设置短信拦截，全部拦截
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
				//得到短信发件人
				String sender = sms.getDisplayOriginatingAddress();
				String result = dao.findMode(sender);
				if("2".equals(result)||"3".equals(result)){
					abortBroadcast();
				}
				
				//智能短信拦截，查找短信里面的关键字,需要用到语言分词技术，开源框架有lucene
				String body = sms.getMessageBody();
				if(body.contains("fapiao")){
					abortBroadcast();
				}
			}
			
		}
		
	}
	
	private class MyListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING://铃响状态
				String mode = dao.findMode(incomingNumber);
				if("1".equals(mode)||"3".equals(mode)){
//					System.out.println("电话拦截开启");
					
					
					//观察呼叫记录数据库内容的变化
					Uri url = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(url, true, new CallLogObserver(incomingNumber,new Handler()));
					
					
					endCall();//另外一个进程里面运行的，远程服务的方法，与下面并不是顺序执行
							//方法调用后，呼叫记录可能还没有生成
					// //删除呼叫记录
					// //访问另外一个应用程序，联系人应用私有的数据库
					// deleteCallLog(incomingNumber);
					
				}
				break;

			default:
				break;
			}
			
			
			
		}
		
	}
	
	private class CallLogObserver extends ContentObserver{
		private String incomingNumber;
		public CallLogObserver(String incomingNumber, Handler handler) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			System.out.println("数据库变化了，产生了呼叫记录");
			getContentResolver().unregisterContentObserver(this);//取消注册，只需要观察一次
			//删除呼叫记录
			//访问另外一个应用程序，联系人应用私有的数据库
			deleteCallLog(incomingNumber);
			
		}
		
	}

	public void endCall() {
		
//		IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
		//底层隐藏的api不可以被直接调用，可用反射原理
		try {
			//加载ServiceManager的字节码，获得里面的方法
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);//静态的null
			ITelephony.Stub.asInterface(iBinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	
	/**利用内容提供者删除通话记录
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		//呼叫记录uri的路径   
		Uri url = Uri.parse("content://call_log/calls");
		//Uri uri  = CallLog.CONTENT_URI;该方式与上面一样，该文档不如自己找容易看
		resolver.delete(url , "number = ?", new String[]{incomingNumber});
		
	}

}
