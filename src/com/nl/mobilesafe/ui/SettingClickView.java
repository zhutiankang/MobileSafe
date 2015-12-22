package com.nl.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nl.mobilesafe.R;

/**
 * @author 追梦
 *我们自定义的组合控件，有两个TextView，有一个ImageView,一个View
 */
public class SettingClickView extends RelativeLayout {
	
	private TextView tv_title;
	private TextView tv_desc;
	
	/**初始化布局文件
	 * @param context
	 */
	private void initView(Context context) {
		//把一个布局文件转换成一个View，并且加载在SettingItemView中，此SettingItemView为父类
		View.inflate(context, R.layout.setting_click_view, SettingClickView.this);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		
	}
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);

	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.nl.mobilesafe", "title_name");
		tv_title.setText(title);
		

	}

	public SettingClickView(Context context) {
		super(context);
		initView(context);
	}
	
	/**设置组合控件的描述信息
	 * @param text
	 */
	public void setDesc(String text){
		tv_desc.setText(text);
	}
	
}
