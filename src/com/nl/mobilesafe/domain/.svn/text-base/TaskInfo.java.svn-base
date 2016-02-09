package com.nl.mobilesafe.domain;

import android.graphics.drawable.Drawable;
/**
 * 进程信息的业务bean
 * @author 追梦
 * 参考 金山卫士 的进程管理
 */
public class TaskInfo {
	private String packname;//唯一标识
	private String name; //名字
	private Drawable icon;//图标
	private long memsize;//内存所用的大小
	private boolean userTask;//用户进程，系统进程
	private boolean Checked;//记录勾选状态
	public boolean isChecked() {
		return Checked;
	}
	public void setChecked(boolean isChecked) {
		this.Checked = isChecked;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public long getMemsize() {
		return memsize;
	}
	public void setMemsize(long memsize) {
		this.memsize = memsize;
	}
	public boolean isUserTask() {
		return userTask;
	}
	public void setUserTask(boolean userTask) {
		this.userTask = userTask;
	}
	@Override
	public String toString() {
		return "TaskInfo [packname=" + packname + ", name=" + name + ", icon="
				+ icon + ", memsize=" + memsize + ", userTask=" + userTask
				+ "]";
	}
	
	
}
