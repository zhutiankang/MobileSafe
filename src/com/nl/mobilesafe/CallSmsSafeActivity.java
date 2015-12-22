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

		// ��listviewע��һ�������¼��ļ�����
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			// ��������״̬�����ı��ʱ�����
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // ����״̬���κι�����󶼻�ص�����״̬
					// �жϵ�ǰlistview�Ĺ���λ��
					// ��ȡ���һ���ɼ���Ŀ���ڼ��������λ��
					//��������ҳ����һҳ����
					int lastPosition = lv_callsms_safe.getLastVisiblePosition();
					// ����������20����Ŀ��λ�ô�0��19
					if (lastPosition == (infos.size() - 1)) {
						// �ƶ�������λ�ã����ظ��������
						if (lastPosition == (dao.findAll().size() - 1)) {
							Toast.makeText(getApplicationContext(), "�������", 0).show();
							return;
						}
						offset += maxnumber;
						fillData();
					}
					
//					// ��ȡ��һ���ɼ���Ŀ���ڼ��������λ��
//					//��ҳ����һҳ����
//					int firstPosition = lv_callsms_safe.getFirstVisiblePosition();
//					if(firstPosition==0){
//						// �ƶ����������λ�ã����ظ��������
//						
//						offset-=maxnumber;
//						if(offset<0){
//							Toast.makeText(getApplicationContext(), "�������", 0).show();
//							return;
//						}
//						fillData();
//					}
					
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// ��ָ��������
					break;
				case OnScrollListener.SCROLL_STATE_FLING: // ���Ի���״̬
					break;
				default:
					break;
				}
			}

			// ������ʱ�����
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);// ��ʼ�������ݿɼ������½���֮ǰ
		new Thread() {
			public void run() {
				//��ҳ
				// infos = dao.findPart(offset,maxnumber);�µ����ݽ�����ԭ�����������ڣ���ҳ���������ݣ�һҳֻ��ʾ20������
				
				//����
				if (infos == null) {
					infos = dao.findPart(offset, maxnumber);
				} else {
					// ԭ���Ѿ����ع�������.���ԭ�����ص�������ʧ���������ڣ��������������ݣ�ʹ��������������ʾ��һ�����棬һҳ
					infos.addAll(dao.findPart(offset, maxnumber));

				}
				// ���̲߳�����ֱ�Ӹ���ui
				runOnUiThread(new Runnable() {
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);// ���������ݻ��߼��������ݣ��󣬽��������ɼ�
						// ��ҳ���ص�ҳ��Ķ��ˣ��ص�ԭ���ݿ�ʼ
						// adapter = new MyAdapter();ÿ�ζ�newһ������Ļ�����Ҫ���¸���һ���µĽ��棬��������Ҫ��ͷ��ʼ
						// lv_callsms_safe.setAdapter(adapter);

						// ����
						if (adapter == null) {
							adapter = new MyAdapter();
							lv_callsms_safe.setAdapter(adapter);
						} else {// ������ع����ݣ��ص�ԭ���ݿ�ʼ������
							adapter.notifyDataSetChanged();
						}
					}
				});

			};

		}.start();
	}

	// listView�Ժ�д��ģ�棬���Ż�50%
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		// �ж�����Ŀ����Ӧ��������ִ�ж��ٴ�
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// System.out.println("position:"+ position);
			View view;
			// ������±�
			ViewHolder holder;
			// 1.�����ڴ���view���󴴽��Ĵ��������þɵ�view���Ż��ڴ�
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_callsms, null);
				// 2.�����Ӻ��Ӳ��ҵĴ������ڴ��ж���ĵ�ַ��tv_black_number.toString()
				holder = new ViewHolder();
				holder.tv_number = (TextView) view
						.findViewById(R.id.tv_black_number);
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_black_mode);
				holder.iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);
				// ��������������ʱ���ҵ����ǵ����ã���¼�ڼ��±��У�������ڸ���view�Ŀڴ���
				view.setTag(holder);
			} else {
				view = convertView;
				// ���õ�ʱ����Ϊ֮ǰ���ҹ��ˣ��Ͳ���Ҫ�ڲ�����findviewByid,ֱ����holder�еļ�¼������id
				holder = (ViewHolder) view.getTag();
			}
			holder.tv_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("�绰����");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("��������");
			} else {
				holder.tv_mode.setText("ȫ������");
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(
							CallSmsSafeActivity.this);
					builder.setTitle("����");
					builder.setMessage("ȷ��Ҫɾ���ú�����");
					builder.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// ɾ�����ݿ������
									dao.delete(infos.get(position).getNumber());
									// ���½���
									infos.remove(position);
									// ֪ͨlistview�������������ݸ�����
									adapter.notifyDataSetChanged();
								}
							});
					builder.setNegativeButton("ȡ��", null);
					builder.show();

				}
			});
			return view;// �ײ���뽫���ص�view��ֵ��convertView
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
	 * view����������� ��¼���ӵ��ڴ��ַ �൱��һ�����±�
	 */
	static class ViewHolder {// ��̬��ֻ����һ���ֽ���
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
					Toast.makeText(getApplicationContext(), "���������", 0).show();
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
					Toast.makeText(getApplicationContext(), "��ѡ������ģʽ", 0)
							.show();
					return;
				}
				// ���ݱ���ӵ����ݿ�
				dao.add(black_number, mode);
				// ����listview�������������,���õ���ǰ��
				infos.add(0, new BlackNumberInfo(black_number, mode));
				// ֪ͨlistview�������������ݸ�����
				adapter.notifyDataSetChanged();
				// �����������Ի���
				dialog.dismiss();
			}
		});

	}
}
