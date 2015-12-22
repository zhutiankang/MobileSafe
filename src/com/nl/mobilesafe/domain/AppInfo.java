package com.nl.mobilesafe.domain;

import android.graphics.drawable.Drawable;
/**
 * 应用程序信息的业务bean
 * @author 追梦
 *
 */
public class AppInfo {
	
	
	private String packname;
	private String name;
	private Drawable icon;
	private boolean inRom;
	private boolean userApp;
	private int uid;
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
	public boolean isInRom() {
		return inRom;
	}
	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}
	public boolean isUserApp() {
		return userApp;
	}
	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}
	@Override
	public String toString() {
		return "AppInfo [packname=" + packname + ", name=" + name + ", icon="
				+ icon + ", inRom=" + inRom + ", userApp=" + userApp + "]";
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	
}
