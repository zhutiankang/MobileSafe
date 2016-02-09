package com.nl.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nl.mobilesafe.domain.TaskInfo;
import com.nl.mobilesafe.engine.TaskInfoProvider;
import com.nl.mobilesafe.utils.SystemInfoUtils;

public class TaskManagerActivity extends Activity {
	private TextView tv_process_count;
	private TextView tv_mem_info;

	private LinearLayout ll_loading;
	private ListView lv_task_manager;
	private List<TaskInfo> allTaskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;
	private TaskManagerAdapter adapter;

	private TextView tv_status;
	private int process;
	private long totalMem;
	private long availMem;

	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
		setTitle();

		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
		fillData();

		tv_status = (TextView) findViewById(R.id.tv_status);
		lv_task_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userTaskInfos != null && systemTaskInfos != null) {

					if (firstVisibleItem > userTaskInfos.size()) {
						tv_status.setText("系统进程：（" + systemTaskInfos.size()
								+ "）");
					} else {
						tv_status.setText("用户进程：（" + userTaskInfos.size() + "）");
					}
				}
			}
		});

		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo taskInfo;
				if (position == 0) {
					return;
				} else if (position == (userTaskInfos.size() + 1)) {
					return;
				} else if (position <= userTaskInfos.size()) {
					taskInfo = userTaskInfos.get(position - 1);
				} else {
					taskInfo = systemTaskInfos.get(position
							- userTaskInfos.size() - 2);
				}
//				System.out.println("------------------"+taskInfo.toString());
				if(getPackageName().equals(taskInfo.getPackname())){
					return;
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if (taskInfo.isChecked()) {
					taskInfo.setChecked(false);
					holder.cb_check.setChecked(false);
				} else {
					taskInfo.setChecked(true);
					holder.cb_check.setChecked(true);
				}

			}
		});
	}

	private void setTitle() {
		process = SystemInfoUtils.getRunningProcessCount(this);
		tv_process_count.setText("运行中的进程（" + process + "）");

		totalMem = SystemInfoUtils.getTotalMem();
		availMem = SystemInfoUtils.getAvailMem(this);

		tv_mem_info.setText("剩余/总内存："
				+ Formatter.formatFileSize(this, availMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));
	}

	/**
	 * 填充数据
	 */
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				allTaskInfos = TaskInfoProvider
						.getTaskInfos(getApplicationContext());
				for (TaskInfo taskInfo : allTaskInfos) {
					if (taskInfo.isUserTask()) {
						userTaskInfos.add(taskInfo);
					} else {
						systemTaskInfos.add(taskInfo);
					}

				}
				// 更新界面
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new TaskManagerAdapter();
							lv_task_manager.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						setTitle();//更新完界面，重新查询进程数与内存
					}
				});
				
			};
		}.start();
	}

	private class TaskManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean flag = sp.getBoolean("showsystem", false);
			if(flag){
				return 1 + userTaskInfos.size() + 1 + systemTaskInfos.size();
			}else{
				return 1 + userTaskInfos.size();
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskInfo taskInfo;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("用户进程（" + userTaskInfos.size() + "）");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				return tv;
			} else if (position == (userTaskInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("系统进程（" + systemTaskInfos.size() + "）");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				return tv;
			} else if (position <= userTaskInfos.size()) {
				taskInfo = userTaskInfos.get(position - 1);
			} else {
				taskInfo = systemTaskInfos.get(position - userTaskInfos.size()
						- 2);
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {

				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_taskinfo, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_task_name = (TextView) view
						.findViewById(R.id.tv_task_name);
				holder.tv_task_memsize = (TextView) view
						.findViewById(R.id.tv_task_memsize);
				holder.cb_check = (CheckBox) view.findViewById(R.id.cb_check);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(taskInfo.getIcon());
			holder.tv_task_name.setText(taskInfo.getName());
			holder.tv_task_memsize.setText("内存占用："
					+ Formatter.formatFileSize(getApplicationContext(),
							taskInfo.getMemsize()));
			holder.cb_check.setChecked(taskInfo.isChecked());// 用taskInfo记录下复选框的状态，防止复用，或者新建的时候出现勾选错误
			if(getPackageName().equals(taskInfo.getPackname())){
				holder.cb_check.setVisibility(View.INVISIBLE);
			}else{
				holder.cb_check.setVisibility(View.VISIBLE);
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
		ImageView iv_icon;
		TextView tv_task_name;
		TextView tv_task_memsize;
		CheckBox cb_check;
	}

	/**
	 * 全选
	 * 
	 * @param view
	 */
	public void selectAll(View view) {
		for (TaskInfo taskInfo : allTaskInfos) {
			if(getPackageName().equals(taskInfo.getPackname())){
				continue;
			}
			taskInfo.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 反选
	 * 
	 * @param view
	 */
	public void selectOppo(View view) {
		for (TaskInfo taskInfo : allTaskInfos) {
			if(getPackageName().equals(taskInfo.getPackname())){
				continue;
			}
			if (taskInfo.isChecked()) {
				taskInfo.setChecked(false);
			} else {
				taskInfo.setChecked(true);
			}
		}
		adapter.notifyDataSetChanged();//通知listview数据改变，
	}

	/**
	 * 一键清理
	 * 
	 * @param view
	 */
	public void killAll(View view) {
		int count = 0;
		int saveMem = 0;
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<TaskInfo> killedTaskInfos = new ArrayList<TaskInfo>();
		for (TaskInfo taskInfo : allTaskInfos) {
			if(taskInfo.isChecked()){//被勾选的进程
				am.killBackgroundProcesses(taskInfo.getPackname());
				
				if(taskInfo.isUserTask()){
					userTaskInfos.remove(taskInfo);
				}else{
					systemTaskInfos.remove(taskInfo); 
				}
				count++;
				saveMem+=taskInfo.getMemsize();
				killedTaskInfos.add(taskInfo);
			}
		}
		allTaskInfos.removeAll(killedTaskInfos);//已经杀死的进程，要从需要遍历的总进程中去除，不然会重复
//		//刷新界面,杀死进程，卸载软件，不仅仅是数据改变，最好重新刷新一下
//		fillData();
		adapter.notifyDataSetChanged();
		Toast.makeText(this, "杀死了"+count+"个进程，释放了"+Formatter.formatFileSize(this, saveMem)+"内存", 1).show();
		process-=count;
		availMem+=saveMem;
		tv_process_count.setText("运行中的进程（" + process + "）");
		tv_mem_info.setText("剩余/总内存："
				+ Formatter.formatFileSize(this, availMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));
	}

	/**
	 * 设置
	 * 
	 * @param view
	 */
	public void enterSetting(View view) {
		Intent intent = new Intent(this,TaskSettingActivity.class);
		startActivityForResult(intent, 0);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		adapter.notifyDataSetChanged();
	}
}
