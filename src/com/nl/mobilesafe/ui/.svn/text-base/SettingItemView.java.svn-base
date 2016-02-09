package com.nl.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nl.mobilesafe.R;

/**
 * @author 追梦
 *我们自定义的组合控件，有两个TextView，有一个Checkbox,一个View
 */
public class SettingItemView extends RelativeLayout {
	
	private CheckBox check_status;
	private TextView tv_title;
	private TextView tv_desc;
	private String desc_off;
	private String desc_on;
	/**初始化布局文件
	 * @param context
	 */
	private void initView(Context context) {
		//把一个布局文件转换成一个View，并且加载在SettingItemView中，此SettingItemView为父类
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
	/**校验组合控件是否选中
	 * @return
	 */
	public boolean isChecked(){
		return check_status.isChecked();
	}
	/**设置组合控件的状态
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
	/**设置组合控件的描述信息
	 * @param text
	 */
	public void setText(String text){
		tv_desc.setText(text);
	}
}
