package com.milai.ecoop.protocol;

public class GoodsItem {
	private String icon;
	private String title;
	private String date;

	public GoodsItem(String icon, String title, String date) {
		this.icon = icon;
		this.title = title;
		this.date = date;
	}
	public GoodsItem(){}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
