package com.nl.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

import com.nl.mobilesafe.EnterPwdActivity;
import com.nl.mobilesafe.db.dao.AppLockDAO;
/**
 * ���Ź����룬����ϵͳ���������״̬
 * @author ׷��
 *
 */
public class WatchDogService extends Service {
	private ActivityManager am;
	private boolean flag;
	private AppLockDAO dao;
	private InnerReceiver receiver;
	private String tempStopProtectPackname;
	
	private ScreenOffReceiver offreceiver;//�Զ��������㲥������
	
	private List<String> protectPacknames;
	private Intent intent;
	
	private DataChangeReceiver dataChangeReceiver;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		dataChangeReceiver = new DataChangeReceiver();
		registerReceiver(dataChangeReceiver, new IntentFilter("com.nl.mobilesafe.db.dao.datachanged"));
		
		receiver = new InnerReceiver();
		registerReceiver(receiver, new IntentFilter("com.nl.mobilesafe.tempstop"));
		
		offreceiver = new ScreenOffReceiver();
		registerReceiver(offreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		dao = new AppLockDAO(this);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
		protectPacknames = dao.findAll();//�����ڴ���
		flag = true;
		
		//�Ż���  �������߳�������������ͼ������׼���ã���Լʱ��
		intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		new Thread(){
			public void run() {
				while(flag){//���Ź���һֱҪ���ӣ�����Ҫ�и���ѭ��������Ϊ��̫Ӱ��cpu��Ҫ��˯��50���룬һ�㾫ȷ�ļ���ϵͳ��Ҫһ����ѭ��
					//����Ҫ��ر�������ѭ����Ҫ��һ��flag��־,
					//�Ż�һ    �õ�1������ջ���У�����Ҫ100�����������Ż�ÿһ�����ڣ�д�������
					List<RunningTaskInfo> tasks = am.getRunningTasks(1);
					
					String packname = tasks.get(0).topActivity.getPackageName();//ͨ���õ��������е�����ջ���ҳ��������е�activity�İ���
					//System.out.println("packname"+packname);//����ѵ���ˣ���ʼ����
					//��ѯ���ݿ�̫���ˣ�������Դ���ĳɲ�ѯ�ڴ棬ʱ��Ч����ɱ����Ǵ�������ʡ�����ģ������Ա�����������5vUSB�ӿ�һ��ʡ200��
					
					//�Ż��� ��ʼ�Ͱ������İ��������ݿ��в��ҳ����������ڴ��У�������ÿ�β����ݿ⣬ֱ�Ӵ��ڴ��в��ң�������Ż������ٶȣ�
					if(protectPacknames.contains(packname)){
						
						//�ж����Ӧ�ó����Ƿ���Ҫ��ʱֹͣ����
						if(packname.equals(tempStopProtectPackname)){
							//ֹͣ����,��ִ�д���
						}else{
							//��������
							
							//��ǰӦ�ó�����Ҫ�������ĳ���������һ����������Ľ���
							
							intent.putExtra("packname", packname);
							startActivity(intent);
							System.out.println("kaiqikaiqikaiqi");
						}
						
					}
					SystemClock.sleep(20);//�����Ϣ50�����ù���Ϣһ��,20����Ϳ���
				}
			};
		}.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
		unregisterReceiver(receiver);
		receiver = null;
		
		unregisterReceiver(offreceiver);
		offreceiver = null;
		
		unregisterReceiver(dataChangeReceiver);
		dataChangeReceiver = null;
	}
	
	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("���յ�����ʱֹͣ������Ӧ�ó���");
			tempStopProtectPackname = intent.getStringExtra("packname");
		}
		
	}
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("������");//�������tempStopProtectPackname����������Ҫ������ܽ���Ӧ��
			tempStopProtectPackname = null;//������Ϊ�գ�����֮ǰ��Ϊ��tempStopProtectPackname���ڴ����б�����������������Ҫ��������
			
		}
		
	}
	
	//�����Ż��Ľ���������Ѿ����ҹ��ˣ��������Զ������ˣ������ݻ�����ӻ���ɾ�����ı䣬��ʱҪ���²��ң���õķ��������Զ���㲥������
	private class DataChangeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//���ݱ�����ӻ���ɾ�������ݸı䣬Ҫ���²���
			protectPacknames = dao.findAll();
		}
		
	}
	
}
