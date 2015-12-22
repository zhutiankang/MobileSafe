package com.nl.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nl.mobilesafe.db.dao.AppLockDAO;
import com.nl.mobilesafe.domain.AppInfo;
import com.nl.mobilesafe.engine.AppInfoProvider;
import com.nl.mobilesafe.utils.DensityUtil;

public class AppManagerActivity extends Activity implements OnClickListener {

	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_app_manager;
	private LinearLayout ll_loading;
	private List<AppInfo> appInfos;

	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	/**
	 * 当前程序信息的状态
	 */
	private TextView tv_status;
	/**
	 * 弹出窗体
	 */
	private PopupWindow popupWindow;

	private LinearLayout ll_uninstall;
	private LinearLayout ll_start;
	private LinearLayout ll_share;

	/**
	 * 被点击的条目
	 */
	private AppInfo appInfo;
	private MyAdapter adapter;

	private AppLockDAO dao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		dao = new AppLockDAO(this);
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		String avail_rom = getAvailSpace(this, Environment.getDataDirectory()
				.getAbsolutePath());
		String avail_sd = getAvailSpace(this, Environment
				.getExternalStorageDirectory().getAbsolutePath());
		tv_avail_rom.setText("内存可用空间：" + avail_rom);
		tv_avail_sd.setText("SD卡可用空间：" + avail_sd);

		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		tv_status = (TextView) findViewById(R.id.tv_status);
		fillData();

		lv_app_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			// 滚动的时候，改变textview
			// firstVisibleItem第一个可见条目在listview中的位置
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userAppInfos != null && systemAppInfos != null) {// 可防止空指针异常
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("系统程序（" + systemAppInfos.size() + "）");
					} else {
						tv_status.setText("用户程序（" + userAppInfos.size() + "）");
					}
				}
			}
		});
		/**
		 * 设置listview的点击事件
		 */
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == 0) {
					return;
				} else if (position == (userAppInfos.size() + 1)) {
					return;
				} else if (position <= userAppInfos.size()) {// 用户程序
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				} else {// 系统程序 新位置就等于 位置减去上面控件的个数
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
				}

				dismissPopupWindow();

				View contentView = View.inflate(getApplicationContext(),
						R.layout.popup_app_item, null);

				ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);
				ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);

				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				popupWindow = new PopupWindow(contentView, -2, -2);// -2包裹内容，-1填充整个窗体
				// 动画效果的播放，必须要求窗体有背景颜色!!!!!!
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));

				int[] location = new int[2];
				view.getLocationInWindow(location);
				// 在代码里面设置的宽高值 都是像素-------》为了适配不同分辨率的手机要用工具转换为dip
				int dip = 60;
				int px = DensityUtil.dip2px(getApplicationContext(), dip);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
						px, location[1]);
				// 缩放
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(300);
				// 透明度
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(300);

				AnimationSet set = new AnimationSet(false);// 独立播放
				set.addAnimation(aa);
				set.addAnimation(sa);
				contentView.startAnimation(set);

			}

		});
		//程序锁 设置条目的长点击事件的监听器 
		lv_app_manager.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return true;//返回true，表示事件到我这里就终止了，处理完了，false没有处理完，别的代码可接着处理，比如松手时变为点击，弹出小气泡
				} else if (position == (userAppInfos.size() + 1)) {
					return true;
				} else if (position <= userAppInfos.size()) {// 用户程序
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				} else {// 系统程序 新位置就等于 位置减去上面控件的个数
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
				}
				
				ViewHolder holder = (ViewHolder) view.getTag();
				//判断条目是否存在在程序锁数据库里面
				if(dao.find(appInfo.getPackname())){
					//被锁定的程序，解除锁定，更新界面为打开的小锁图片
					holder.iv_status.setImageResource(R.drawable.unlock);
					dao.delete(appInfo.getPackname());
				}else{
					//锁定的程序，更新界面为关闭的小锁图片
					holder.iv_status.setImageResource(R.drawable.lock);
					dao.add(appInfo.getPackname());
				}
				
				return true;
			}
		});
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isUserApp()) {
						userAppInfos.add(appInfo);
					} else {
						systemAppInfos.add(appInfo);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);// 查找完数据或者加载完数据，后，进度条不可见
						if (adapter == null) {
							adapter = new MyAdapter();
							lv_app_manager.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
					}
				});

			};
		}.start();
	}

	private class MyAdapter extends BaseAdapter {

		// 控制listView有多少条目
		@Override
		public int getCount() {
			// return appInfos.size();
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;
			if (position == 0) {// 显示的是用户程序有多少个小标签
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setText("用户程序（" + userAppInfos.size() + "）");
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {// 显示的是系统程序有多少个小标签
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setText("系统程序（" + systemAppInfos.size() + "）");
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position <= userAppInfos.size()) {// 这些位置是留给用户程序显示的
				int newposition = position - 1;
				appInfo = userAppInfos.get(newposition);
			} else {
				int newposition = position - userAppInfos.size() - 2;
				appInfo = systemAppInfos.get(newposition);
			}

			View view;
			ViewHolder holder;
			// 复用缓存时出错，1检测view是否为空 2检测view类型是否与之前用的一致
			if (convertView != null && convertView instanceof RelativeLayout) {
				// 不仅需要检查是否为空，还要判断是否为合适的类型去复用
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_app_manager, null);
				holder = new ViewHolder();
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				holder.tv_location = (TextView) view
						.findViewById(R.id.tv_location);
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.iv_status = (ImageView) view.findViewById(R.id.iv_status);
				view.setTag(holder);
			}

			// if(position<=userAppInfos.size()){//这些位置是留给用户程序显示的
			// appInfo = userAppInfos.get(position-1);
			// }else{//这些位置是留给系统显示的
			// int newposition = position - userAppInfos.size()-2;
			// appInfo = systemAppInfos.get(newposition);
			// }
			holder.tv_name.setText(appInfo.getName());
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			if (appInfo.isInRom()) {
				holder.tv_location.setText("内部存储");

			} else {
				holder.tv_location.setText("外部存储");
			}
			if(dao.find(appInfo.getPackname())){
				holder.iv_status.setImageResource(R.drawable.lock);
			}else{
				holder.iv_status.setImageResource(R.drawable.unlock);
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_location;
		ImageView iv_icon;
		ImageView iv_status;
	}

	/**
	 * 获取可用空间
	 * 
	 * @param path
	 *            路径
	 * @param context
	 *            上下文
	 * @return
	 */
	private String getAvailSpace(Context context, String path) {
		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		// long totalBlocks = stat.getBlockCount();
		long availableBlocks = stat.getAvailableBlocks();

		// mSdSize.setSummary(formatSize(totalBlocks * blockSize));
		// mSdAvail.setSummary(formatSize(availableBlocks * blockSize) +
		// readOnly);
		return Formatter.formatFileSize(context, availableBlocks * blockSize);
	}

	private void dismissPopupWindow() {
		// 把旧的弹出窗体关闭掉
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissPopupWindow();
	}

	/**
	 * 布局对应的点击时间
	 */
	@Override
	public void onClick(View v) {
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_start:
			startApplication();
			break;
		case R.id.ll_uninstall:
			if (appInfo.isUserApp()) {
				uninstallApplication();
			} else {
				Toast.makeText(getApplicationContext(), "无root权限系统应用不可卸载", 0)
						.show();
				// Runtime.getRuntime().exec("rm");在java代码中执行linux命令，
				// 前提是手机已获得root权限,应用以获取root权限
			}
			break;
		case R.id.ll_share:
			shareApplication();
			break;
		default:
			break;
		}
	}

	/**
	 * 分享应用
	 */
	private void shareApplication() {
		
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "推荐你使用一款软件，名字叫："+appInfo.getName());
		startActivity(intent);
	}

	/**
	 * 卸载应用
	 */
	private void uninstallApplication() {
		// <intent-filter>
		// <action android:name="android.intent.action.VIEW" />
		// <action android:name="android.intent.action.DELETE" />
		// <category android:name="android.intent.category.DEFAULT" />
		// <data android:scheme="package" />
		// </intent-filter>
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appInfo.getPackname()));//不要有//
		startActivityForResult(intent, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}

	/**
	 * 开启应用
	 */
	private void startApplication() {
		// 查询应用程序的入口activity，把它开启起来
		PackageManager pm = getPackageManager();
		// Intent intent = new Intent();
		// intent.setAction("android.intent.action.MAIN");
		// intent.addCategory("android.intent.category.LAUNCHER");
		// //获取手机上所有有启动能力的activity
		// List<ResolveInfo> activities = pm.queryIntentActivities(intent,
		// PackageManager.GET_INTENT_FILTERS);
		// 根据包名开启应用
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if (intent == null) {
			Toast.makeText(this, "对不起不能启动当前应用", 0).show();
		} else {
			startActivity(intent);
		}
	}
}
