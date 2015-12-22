package com.nl.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nl.mobilesafe.R;

/**
 * @author ׷��
 *�����Զ������Ͽؼ���������TextView����һ��Checkbox,һ��View
 */
public class SettingItemView extends RelativeLayout {
	
	private CheckBox check_status;
	private TextView tv_title;
	private TextView tv_desc;
	private String desc_off;
	private String desc_on;
	/**��ʼ�������ļ�
	 * @param context
	 */
	private void initView(Context context) {
		//��һ�������ļ�ת����һ��View�����Ҽ�����SettingItemView�У���SettingItemViewΪ����
		View.inflate(context, R.layout.setting_item_view, SettingItemView.this);
		check_status = (CheckBox) this.findViewById(R.id.check_status);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		
	}
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);

	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.nl.mobilesafe", "title_name");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.nl.mobilesafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.nl.mobilesafe", "desc_off");
		tv_title.setText(title);

	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}
	/**У����Ͽؼ��Ƿ�ѡ��
	 * @return
	 */
	public boolean isChecked(){
		return check_status.isChecked();
	}
	/**������Ͽؼ���״̬
	 * @param isChecked
	 */
	public void setChecked(boolean isChecked){
		if(isChecked){
			setText(desc_on);
		}else{
			setText(desc_off);
		}
		check_status.setChecked(isChecked);
	}
	/**������Ͽؼ���������Ϣ
	 * @param text
	 */
	public void setText(String text){
		tv_desc.setText(text);
	}
}
