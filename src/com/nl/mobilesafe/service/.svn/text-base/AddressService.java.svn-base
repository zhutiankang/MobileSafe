package com.nl.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.nl.mobilesafe.R;
import com.nl.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {
	// 自定义吐司，窗体管理者
	private WindowManager wm;

	private TelephonyManager tm;
	private MyPhoneStateListener listener;

	// 代码注册一个广播接收者。OutCallReceiver
	private OutCallReceiver receiver;

	private View view;
	private SharedPreferences sp;

	private WindowManager.LayoutParams params;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 代码注册一个广播接收者。OutCallReceiver
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);

		// 实例化窗体管理者
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;

		// 代码取消一个广播接收者
		unregisterReceiver(receiver);
		receiver = null;
	}

	private class MyPhoneStateListener extends PhoneStateListener {
		/**
		 * 当呼叫状态发生改变的时候回调该方法
		 */

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String address = NumberAddressQueryUtils
						.getAddress(incomingNumber);

				// Toast.makeText(getApplicationContext(), address, 1).show();
				// 自定义一个吐司，比原先好看
				showMyToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:// 电话的空闲状态，处理电话挂断，来电拒绝
				if (view != null) {
					wm.removeView(view);
					view = null;
				}
				break;
			default:
				break;
			}

		}

	}

	/**
	 * 服务里面的内部类，广播接收者的生命周期与服务一样
	 * 
	 * @author 追梦
	 * 
	 */
	class OutCallReceiver extends BroadcastReceiver {

		private static final String TAG = "OutCallReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.i(TAG, "有电话打出去");
			String phone = getResultData();
			String address = NumberAddressQueryUtils.getAddress(phone);
			showMyToast(address);
		}
	}

	/**
	 * 自定义吐司
	 * 
	 * @param address
	 */
	public void showMyToast(String address) {
	
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int which = sp.getInt("which", 0);

		view = View.inflate(this, R.layout.toast_address, null);
		//给view设置一个点击事件
		final long[] mHits = new long[2];
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					params.x = (wm.getDefaultDisplay().getWidth()-view.getWidth())/2;
					params.y = (wm.getDefaultDisplay().getHeight()-view.getHeight())/2;
					wm.updateViewLayout(v, params);
					Editor edit = sp.edit();
					edit.putInt("lastX", params.x);
					edit.putInt("lastY", params.y);
					edit.commit();
				}
			}
		});
		// 给view对象设置一个触摸监听器
		view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;
			

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 定义手指的初始化位置

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 手指按下屏幕
					// 开始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动
					// 新位置
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					// 偏移量
					int dx = newX - startX;
					int dy = newY - startY;
					// 更新view的位置,移动(dx,dy)
					
					params.x += dx;
					params.y += dy;
					//移动过之后，考虑边界问题
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.x > (wm.getDefaultDisplay().getWidth() - view
							.getWidth())) {
						params.x = wm.getDefaultDisplay().getWidth() - view
								.getWidth();
					}
					if (params.y > (wm.getDefaultDisplay().getHeight() - view
							.getHeight())) {
						params.y = wm.getDefaultDisplay().getHeight() - view
								.getHeight();
					}
					wm.updateViewLayout(view, params);
					// 重新初始化开始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:// 手指离开屏幕的一瞬间
					
					Editor edit = sp.edit();
					edit.putInt("lastX", params.x);
					edit.putInt("lastY", params.y);
					edit.commit();
					break;
				default:
					break;
				}

				return false;//若为true表示事件处理完毕了,不要让父控件，父布局再响应触摸事件了,
							//若将之移动到挂断按钮上，不会响应挂断电话，包括挂断电话，按钮的点击
				            //应为false
			}
		});

		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] ids = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		view.setBackgroundResource(ids[which]);
		TextView tv_location = (TextView) view.findViewById(R.id.tv_location);
		tv_location.setText(address);
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		// 吐司的位置,左上位置
		params.gravity = Gravity.TOP + Gravity.LEFT;

		// params.x = 50;// 距离左边50dip
		// params.y = 50;// 距离顶部50dip
		// 吐司的位置坐标
		params.x = sp.getInt("lastX", 0);
		params.y = sp.getInt("lastY", 0);
		params.format = PixelFormat.TRANSLUCENT;
		// TYPE_PRIORITY_PHONE，，android里面具有电话优先级的一种窗体类型，记得添加权限
		// 以后要想定义一个原本不响应，要使它响应触摸事件的窗体，记得用TYPE_PRIORITY_PHONE
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

		wm.addView(view, params);
		
		
		new Thread(){
			public void run() {
				SystemClock.sleep(8000);
				if (view != null) {//该句子十分重要，判断view不为空，为通话状态，没有会空指针异常
					wm.removeView(view);
					view = null;
				}
			};
		}.start();
	}
}
