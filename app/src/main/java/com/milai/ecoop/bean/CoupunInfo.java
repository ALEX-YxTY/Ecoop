package com.milai.ecoop.bean;
/**
 * 定义Coupun
 * */
public class CoupunInfo {
	private int coupunprice;
	private String coupunname;
	private String coupuncondition;
	private String validity;
	public CoupunInfo(int coupunprice,String coupunname,String coupuncondition,String validity) {
		this.coupunprice=coupunprice;
		this.coupunname=coupunname;
		this.coupuncondition=coupuncondition;
		this.validity=validity;
	}
	public int getCoupunprice() {
		return coupunprice;
	}
	public void setCoupunprice(int coupunprice) {
		this.coupunprice = coupunprice;
	}
	public String getCoupunname() {
		return coupunname;
	}
	public void setCoupunname(String coupunname) {
		this.coupunname = coupunname;
	}
	public String getCoupuncondition() {
		return coupuncondition;
	}
	public void setCoupuncondition(String coupuncondition) {
		this.coupuncondition = coupuncondition;
	}
	public String getValidity() {
		return validity;
	}
	public void setValidity(String validity) {
		this.validity = validity;
	}

	
}
