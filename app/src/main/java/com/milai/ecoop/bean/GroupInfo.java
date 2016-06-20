package com.milai.ecoop.bean;
/**
 * 我的团信息
 * */
public class GroupInfo {
	private String goodname;
	private double teamprice;
	private String groupstate;
	public GroupInfo(String goodname,double teamprice,String groupstate){
		this.goodname=goodname;
		this.teamprice=teamprice;
		this.groupstate=groupstate;
	}
	public String getGoodname() {
		return goodname;
	}
	public void setGoodname(String goodname) {
		this.goodname = goodname;
	}

	public double getTeamprice() {
		return teamprice;
	}
	public void setTeamprice(double teamprice) {
		this.teamprice = teamprice;
	}
	public String getGroupstate() {
		return groupstate;
	}
	public void setGroupstate(String groupstate) {
		this.groupstate = groupstate;
	}

}
