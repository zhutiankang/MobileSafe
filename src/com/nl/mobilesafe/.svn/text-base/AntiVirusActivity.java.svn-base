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
				tv_scan_status.setText("正在扫描.....");
				TextView tv = new TextView(getApplicationContext());
				if(scanInfo.isVirus){
					tv.setTextColor(Color.RED);
					tv.setText("发现病毒："+scanInfo.name);
				}else{
					tv.setTextColor(Color.BLACK);
					tv.setText("扫描安全："+scanInfo.name);
				}
				ll_container.addView(tv, 0);
				break;
			case FINISH:
				tv_scan_status.setText("扫描完毕");
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
	 * 扫描病毒
	 */
	private void scanVirus(){
		pm = getPackageManager();
		tv_scan_status.setText("正在初始化杀毒引擎....");
		new Thread(){
			public void run() {
				SystemClock.sleep(500);//让用户感觉专业，初始化杀毒引擎时间不要那么快
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				pb_anti.setMax(infos.size());
				int progress = 0;
				ScanInfo scanInfo;
				for (PackageInfo info : infos) {
					scanInfo = new ScanInfo();
					scanInfo.packname = info.packageName;
					scanInfo.name = info.applicationInfo.loadLabel(pm).toString();//别忘tostring
					//得到数据保存目录，/data/data/包名/
					//String dataDir = info.applicationInfo.dataDir;
					//得到源码目录，即APK目录,apk文件的完整路径，/system/apk/  /data/apk/,现在的病毒大多数是APK，少量是用c写的
					String sourceDir = info.applicationInfo.sourceDir;//apk,也可变量apk图片，清单文件
					                    //zip包，解压APK文件得到里面的文件
//					                    ZipFile zipFile = new ZipFile(file);
//					                    zipFile.getEntry("AndroidManifest.xml");//禁止别人非法更改你的节点
					String md5 = getFileMd5(sourceDir);
					
					//查询md5信息是否在病毒库中存在
					if(AntiVirusQueryUtils.isVirus(md5)){
						//发现病毒
						scanInfo.isVirus = true;
					}else{
						//扫描安全
						scanInfo.isVirus = false;
					}
					Message msg = Message.obtain();
					msg.what = SCANING;
				    msg.obj = scanInfo;
					handler.sendMessage(msg);
					progress++;
					pb_anti.setProgress(progress);
					SystemClock.sleep(300);//不要那么快，让用户感觉你在努力工作
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
	 * 获取文件的md5值，即特征值
	 */
	private String getFileMd5(String path){
		//获取一个文件的特征信息，签名信息
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
			// 把每一个byte与0xff做一个与运算
			for (byte b : result) {
				// 与运算
				int number = b & 0xff;// b&0xfff,不是标准的加密，称为加盐，只要保证可以解出就可
				// 转换为十六进制
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			//得到的是标准的md5加密后的结果
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}
}
