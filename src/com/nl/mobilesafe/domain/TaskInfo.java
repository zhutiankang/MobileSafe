package com.nl.mobilesafe.domain;

import android.graphics.drawable.Drawable;
/**
 * ������Ϣ��ҵ��bean
 * @author ׷��
 * �ο� ��ɽ��ʿ �Ľ��̹���
 */
public class TaskInfo {
	private String packname;//Ψһ��ʶ
	private String name; //����
	private Drawable icon;//ͼ��
	private long memsize;//�ڴ����õĴ�С
	private boolean userTask;//�û����̣�ϵͳ����
	private boolean Checked;//��¼��ѡ״̬
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
