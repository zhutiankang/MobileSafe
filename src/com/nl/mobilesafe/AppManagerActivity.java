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
	 * ��ǰ������Ϣ��״̬
	 */
	private TextView tv_status;
	/**
	 * ��������
	 */
	private PopupWindow popupWindow;

	private LinearLayout ll_uninstall;
	private LinearLayout ll_start;
	private LinearLayout ll_share;

	/**
	 * ���������Ŀ
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
		tv_avail_rom.setText("�ڴ���ÿռ䣺" + avail_rom);
		tv_avail_sd.setText("SD�����ÿռ䣺" + avail_sd);

		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		tv_status = (TextView) findViewById(R.id.tv_status);
		fillData();

		lv_app_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			// ������ʱ�򣬸ı�textview
			// firstVisibleItem��һ���ɼ���Ŀ��listview�е�λ��
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userAppInfos != null && systemAppInfos != null) {// �ɷ�ֹ��ָ���쳣
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("ϵͳ����" + systemAppInfos.size() + "��");
					} else {
						tv_status.setText("�û�����" + userAppInfos.size() + "��");
					}
				}
			}
		});
		/**
		 * ����listview�ĵ���¼�
		 */
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == 0) {
					return;
				} else if (position == (userAppInfos.size() + 1)) {
					return;
				} else if (position <= userAppInfos.size()) {// �û�����
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				} else {// ϵͳ���� ��λ�þ͵��� λ�ü�ȥ����ؼ��ĸ���
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
				popupWindow = new PopupWindow(contentView, -2, -2);// -2�������ݣ�-1�����������
				// ����Ч���Ĳ��ţ�����Ҫ�����б�����ɫ!!!!!!
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));

				int[] location = new int[2];
				view.getLocationInWindow(location);
				// �ڴ����������õĿ��ֵ ��������-------��Ϊ�����䲻ͬ�ֱ��ʵ��ֻ�Ҫ�ù���ת��Ϊdip
				int dip = 60;
				int px = DensityUtil.dip2px(getApplicationContext(), dip);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
						px, location[1]);
				// ����
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(300);
				// ͸����
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(300);

				AnimationSet set = new AnimationSet(false);// ��������
				set.addAnimation(aa);
				set.addAnimation(sa);
				contentView.startAnimation(set);

			}

		});
		//������ ������Ŀ�ĳ�����¼��ļ����� 
		lv_app_manager.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return true;//����true����ʾ�¼������������ֹ�ˣ��������ˣ�falseû�д����꣬��Ĵ���ɽ��Ŵ�����������ʱ��Ϊ���������С����
				} else if (position == (userAppInfos.size() + 1)) {
					return true;
				} else if (position <= userAppInfos.size()) {// �û�����
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				} else {// ϵͳ���� ��λ�þ͵��� λ�ü�ȥ����ؼ��ĸ���
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
				}
				
				ViewHolder holder = (ViewHolder) view.getTag();
				//�ж���Ŀ�Ƿ�����ڳ��������ݿ�����
				if(dao.find(appInfo.getPackname())){
					//�������ĳ��򣬽�����������½���Ϊ�򿪵�С��ͼƬ
					holder.iv_status.setImageResource(R.drawable.unlock);
					dao.delete(appInfo.getPackname());
				}else{
					//�����ĳ��򣬸��½���Ϊ�رյ�С��ͼƬ
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
						ll_loading.setVisibility(View.INVISIBLE);// ���������ݻ��߼��������ݣ��󣬽��������ɼ�
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

		// ����listView�ж�����Ŀ
		@Override
		public int getCount() {
			// return appInfos.size();
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;
			if (position == 0) {// ��ʾ�����û������ж��ٸ�С��ǩ
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setText("�û�����" + userAppInfos.size() + "��");
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {// ��ʾ����ϵͳ�����ж��ٸ�С��ǩ
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setText("ϵͳ����" + systemAppInfos.size() + "��");
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position <= userAppInfos.size()) {// ��Щλ���������û�������ʾ��
				int newposition = position - 1;
				appInfo = userAppInfos.get(newposition);
			} else {
				int newposition = position - userAppInfos.size() - 2;
				appInfo = systemAppInfos.get(newposition);
			}

			View view;
			ViewHolder holder;
			// ���û���ʱ����1���view�Ƿ�Ϊ�� 2���view�����Ƿ���֮ǰ�õ�һ��
			if (convertView != null && convertView instanceof RelativeLayout) {
				// ������Ҫ����Ƿ�Ϊ�գ���Ҫ�ж��Ƿ�Ϊ���ʵ�����ȥ����
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

			// if(position<=userAppInfos.size()){//��Щλ���������û�������ʾ��
			// appInfo = userAppInfos.get(position-1);
			// }else{//��Щλ��������ϵͳ��ʾ��
			// int newposition = position - userAppInfos.size()-2;
			// appInfo = systemAppInfos.get(newposition);
			// }
			holder.tv_name.setText(appInfo.getName());
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			if (appInfo.isInRom()) {
				holder.tv_location.setText("�ڲ��洢");

			} else {
				holder.tv_location.setText("�ⲿ�洢");
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
	 * ��ȡ���ÿռ�
	 * 
	 * @param path
	 *            ·��
	 * @param context
	 *            ������
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
		// �Ѿɵĵ�������رյ�
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
	 * ���ֶ�Ӧ�ĵ��ʱ��
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
				Toast.makeText(getApplicationContext(), "��rootȨ��ϵͳӦ�ò���ж��", 0)
						.show();
				// Runtime.getRuntime().exec("rm");��java������ִ��linux���
				// ǰ�����ֻ��ѻ��rootȨ��,Ӧ���Ի�ȡrootȨ��
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
	 * ����Ӧ��
	 */
	private void shareApplication() {
		
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "�Ƽ���ʹ��һ����������ֽУ�"+appInfo.getName());
		startActivity(intent);
	}

	/**
	 * ж��Ӧ��
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
		intent.setData(Uri.parse("package:" + appInfo.getPackname()));//��Ҫ��//
		startActivityForResult(intent, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}

	/**
	 * ����Ӧ��
	 */
	private void startApplication() {
		// ��ѯӦ�ó�������activity��������������
		PackageManager pm = getPackageManager();
		// Intent intent = new Intent();
		// intent.setAction("android.intent.action.MAIN");
		// intent.addCategory("android.intent.category.LAUNCHER");
		// //��ȡ�ֻ�������������������activity
		// List<ResolveInfo> activities = pm.queryIntentActivities(intent,
		// PackageManager.GET_INTENT_FILTERS);
		// ���ݰ�������Ӧ��
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if (intent == null) {
			Toast.makeText(this, "�Բ�����������ǰӦ��", 0).show();
		} else {
			startActivity(intent);
		}
	}
}
