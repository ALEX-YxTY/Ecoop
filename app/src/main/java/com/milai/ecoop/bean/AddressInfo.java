package com.milai.ecoop.bean;
/**
 * 用户地址信息
 * */
public class AddressInfo {
	private String client;
	private String phonenumber;
	private String address;
	public AddressInfo(String client,String phonenumber,String address){
		this.client=client;
		this.phonenumber=phonenumber;
		this.address=address;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
