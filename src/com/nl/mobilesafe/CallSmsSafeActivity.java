package com.nl.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nl.mobilesafe.db.dao.BlackNumberDAO;
import com.nl.mobilesafe.domain.BlackNumberInfo;

public class CallSmsSafeActivity extends Activity {
	private ListView lv_callsms_safe;
	private BlackNumberDAO dao;
	private List<BlackNumberInfo> infos;
	private MyAdapter adapter;
	private LinearLayout ll_loading;
	private int offset = 0;
	private int maxnumber = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		dao = new BlackNumberDAO(this);
		fillData();

		// 给listview注册一个滚动事件的监听器
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			// 当滚动的状态发生改变的时候调用
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // 空闲状态，任何滚动最后都会回到空闲状态
					// 判断当前listview的滚动位置
					// 获取最后一个可见条目，在集合里面的位置
					//分批，分页的下一页代码
					int lastPosition = lv_callsms_safe.getLastVisiblePosition();
					// 集合里面有20个条目，位置从0到19
					if (lastPosition == (infos.size() - 1)) {
						// 移动到最后的位置，加载更多的数据
						if (lastPosition == (dao.findAll().size() - 1)) {
							Toast.makeText(getApplicationContext(), "加载完毕", 0).show();
							return;
						}
						offset += maxnumber;
						fillData();
					}
					
//					// 获取第一个可见条目，在集合里面的位置
//					//分页的上一页代码
//					int firstPosition = lv_callsms_safe.getFirstVisiblePosition();
//					if(firstPosition==0){
//						// 移动到最上面的位置，加载更多的数据
//						
//						offset-=maxnumber;
//						if(offset<0){
//							Toast.makeText(getApplicationContext(), "加载完毕", 0).show();
//							return;
//						}
//						fillData();
//					}
					
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 手指触摸滚动
					break;
				case OnScrollListener.SCROLL_STATE_FLING: // 惯性滑行状态
					break;
				default:
					break;
				}
			}

			// 滚动的时候调用
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);// 开始加载数据可见，更新界面之前
		new Thread() {
			public void run() {
				//分页
				// infos = dao.findPart(offset,maxnumber);新的数据将覆盖原来的数据属于，分页，加载数据，一页只显示20个数据
				
				//分批
				if (infos == null) {
					infos = dao.findPart(offset, maxnumber);
				} else {
					// 原来已经加载过数据了.解决原来加载的数据消失的问题属于，分批，加载数据，使所有数据最终显示在一个界面，一页
					infos.addAll(dao.findPart(offset, maxnumber));

				}
				// 子线程不可以直接更新ui
				runOnUiThread(new Runnable() {
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);// 查找完数据或者加载完数据，后，进度条不可见
						// 分页，回到页面的顶端，回到原数据开始
						// adapter = new MyAdapter();每次都new一个对象的话，就要重新更新一个新的界面，所以数据要重头开始
						// lv_callsms_safe.setAdapter(adapter);

						// 分批
						if (adapter == null) {
							adapter = new MyAdapter();
							lv_callsms_safe.setAdapter(adapter);
						} else {// 解决加载过数据，回到原数据开始的问题
							adapter.notifyDataSetChanged();
						}
					}
				});

			};

		}.start();
	}

	// listView以后写的模版，能优化50%
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		// 有多少条目被响应，方法就执行多少次
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// System.out.println("position:"+ position);
			View view;
			// 定义记事本
			ViewHolder holder;
			// 1.减少内存中view对象创建的次数，复用旧的view，优化内存
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_callsms, null);
				// 2.减少子孩子查找的次数，内存中对象的地址，tv_black_number.toString()
				holder = new ViewHolder();
				holder.tv_number = (TextView) view
						.findViewById(R.id.tv_black_number);
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_black_mode);
				holder.iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);
				// 当孩子生出来的时候找到他们的引用，记录在记事本中，并存放在父亲view的口袋里
				view.setTag(holder);
			} else {
				view = convertView;
				// 复用的时候因为之前查找过了，就不需要在查找了findviewByid,直接用holder中的记录的引用id
				holder = (ViewHolder) view.getTag();
			}
			holder.tv_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("电话拦截");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("短信拦截");
			} else {
				holder.tv_mode.setText("全部拦截");
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(
							CallSmsSafeActivity.this);
					builder.setTitle("警告");
					builder.setMessage("确定要删除该号码吗？");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 删除数据库的内容
									dao.delete(infos.get(position).getNumber());
									// 更新界面
									infos.remove(position);
									// 通知listview数据适配器数据更新了
									adapter.notifyDataSetChanged();
								}
							});
					builder.setNegativeButton("取消", null);
					builder.show();

				}
			});
			return view;// 底层代码将返回的view赋值给convertView
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	/**
	 * view对象的容器， 记录孩子的内存地址 相当于一个记事本
	 */
	static class ViewHolder {// 静态的只加载一次字节码
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}

	private EditText et_black_number;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button ok;
	private Button cancel;

	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = view.inflate(this, R.layout.dialog_black_number,
				null);
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
		et_black_number = (EditText) contentView
				.findViewById(R.id.et_black_number);
		cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
		ok = (Button) contentView.findViewById(R.id.ok);
		cancel = (Button) contentView.findViewById(R.id.cancel);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String black_number = et_black_number.getText().toString()
						.trim();
				if (TextUtils.isEmpty(black_number)) {
					Toast.makeText(getApplicationContext(), "请输入号码", 0).show();
					return;
				}
				String mode;
				if (cb_phone.isChecked() && cb_sms.isChecked()) {
					mode = "3";
				} else if (cb_phone.isChecked()) {
					mode = "1";
				} else if (cb_sms.isChecked()) {
					mode = "2";
				} else {
					Toast.makeText(getApplicationContext(), "请选中拦截模式", 0)
							.show();
					return;
				}
				// 数据被添加到数据库
				dao.add(black_number, mode);
				// 更新listview集合里面的内容,放置到最前面
				infos.add(0, new BlackNumberInfo(black_number, mode));
				// 通知listview数据适配器数据更新了
				adapter.notifyDataSetChanged();
				// 别忘记消掉对话框
				dialog.dismiss();
			}
		});

	}
}
