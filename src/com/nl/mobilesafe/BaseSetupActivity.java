package com.nl.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity{
	// 1.定义手势识别器
	private GestureDetector detector;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 2.实例化手势识别器
		detector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {

					// 当我们的手指在上面滑动的时候回调
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						//屏蔽斜着滑动这种情况
						if(Math.abs((e2.getRawY() - e1.getRawY()))>100){
							Toast.makeText(BaseSetupActivity.this, "哥们，不能这样滑", Toast.LENGTH_SHORT).show();
							return true;
						}
						//屏蔽在X上滑动很慢的情况
						if(Math.abs(velocityX)<100){
							return true;
						}
						
						if (e2.getRawX() - e1.getRawX() > 200) {
							// 显示上一个界面e1, ---->e2 指向e2
							System.out.println("显示上一个界面，从左向右滑动");
							showPre();
							return true;
						}
						if (e1.getRawX() - e2.getRawX() > 200) {
							// 显示下一个界面e2, <----e1指向e2
							System.out.println("显示下一个界面，从右向左滑动");
							showNext();
							return true;
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});
	}

	public abstract void showNext();

	public abstract void showPre();

	/**
	 * 下一步点击事件
	 * 
	 * @param v
	 */
	public void next(View v) {
		showNext();
	}

	/**
	 * 上一步点击事件
	 * 
	 * @param v
	 */
	public void pre(View view) {
		showPre();
	}

	// 3.使用手势识别器
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
