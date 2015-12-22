package com.nl.mobilesafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.ZipFile;

import com.nl.mobilesafe.db.dao.AntiVirusQueryUtils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {
	protected static final int SCANING = 0;
	protected static final int FINISH = 1;
	private ImageView iv_scan;
	private ProgressBar pb_anti;
	private PackageManager pm;
	private TextView tv_scan_status;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCANING:
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				tv_scan_status.setText("����ɨ��.....");
				TextView tv = new TextView(getApplicationContext());
				if(scanInfo.isVirus){
					tv.setTextColor(Color.RED);
					tv.setText("���ֲ�����"+scanInfo.name);
				}else{
					tv.setTextColor(Color.BLACK);
					tv.setText("ɨ�谲ȫ��"+scanInfo.name);
				}
				ll_container.addView(tv, 0);
				break;
			case FINISH:
				tv_scan_status.setText("ɨ�����");
				iv_scan.clearAnimation();
				break;
			default:
				break;
			}
		}
		
	};
	private LinearLayout ll_container;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_anti_virus);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		pb_anti = (ProgressBar) findViewById(R.id.pb_anti);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,  Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan.setAnimation(ra);
		scanVirus();
	}
	
	/**
	 * ɨ�財��
	 */
	private void scanVirus(){
		pm = getPackageManager();
		tv_scan_status.setText("���ڳ�ʼ��ɱ������....");
		new Thread(){
			public void run() {
				SystemClock.sleep(500);//���û��о�רҵ����ʼ��ɱ������ʱ�䲻Ҫ��ô��
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				pb_anti.setMax(infos.size());
				int progress = 0;
				ScanInfo scanInfo;
				for (PackageInfo info : infos) {
					scanInfo = new ScanInfo();
					scanInfo.packname = info.packageName;
					scanInfo.name = info.applicationInfo.loadLabel(pm).toString();//����tostring
					//�õ����ݱ���Ŀ¼��/data/data/����/
					//String dataDir = info.applicationInfo.dataDir;
					//�õ�Դ��Ŀ¼����APKĿ¼,apk�ļ�������·����/system/apk/  /data/apk/,���ڵĲ����������APK����������cд��
					String sourceDir = info.applicationInfo.sourceDir;//apk,Ҳ�ɱ���apkͼƬ���嵥�ļ�
					                    //zip������ѹAPK�ļ��õ�������ļ�
//					                    ZipFile zipFile = new ZipFile(file);
//					                    zipFile.getEntry("AndroidManifest.xml");//��ֹ���˷Ƿ�������Ľڵ�
					String md5 = getFileMd5(sourceDir);
					
					//��ѯmd5��Ϣ�Ƿ��ڲ������д���
					if(AntiVirusQueryUtils.isVirus(md5)){
						//���ֲ���
						scanInfo.isVirus = true;
					}else{
						//ɨ�谲ȫ
						scanInfo.isVirus = false;
					}
					Message msg = Message.obtain();
					msg.what = SCANING;
				    msg.obj = scanInfo;
					handler.sendMessage(msg);
					progress++;
					pb_anti.setProgress(progress);
					SystemClock.sleep(300);//��Ҫ��ô�죬���û��о�����Ŭ������
				}
				
				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	class ScanInfo{
		String packname;
		String name;
		boolean isVirus;
	}
	
	
	/**
	 * ��ȡ�ļ���md5ֵ��������ֵ
	 */
	private String getFileMd5(String path){
		//��ȡһ���ļ���������Ϣ��ǩ����Ϣ
		File file = new File(path);
		//md5
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len=fis.read(buffer))!=-1){
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			StringBuffer sb = new StringBuffer();
			// ��ÿһ��byte��0xff��һ��������
			for (byte b : result) {
				// ������
				int number = b & 0xff;// b&0xfff,���Ǳ�׼�ļ��ܣ���Ϊ���Σ�ֻҪ��֤���Խ���Ϳ�
				// ת��Ϊʮ������
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			//�õ����Ǳ�׼��md5���ܺ�Ľ��
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}
}
