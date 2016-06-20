package com.milai.ecoop.bean;
/**
 * 订单信息
 * */
public class OrderInfo {
	private String orderdate;
	private String orderstate;
	private String ordercontent;
	private double orderprice;
	private double marketprice;
	private int ordernumber;
	private double paymoney;
	public OrderInfo(String orderdate,String orderstate,
			String ordercontent,double orderprice,double marketprice,
			int ordernumber
			){
		this.orderdate=orderdate;
		this.orderstate=orderstate;
		this.ordercontent=ordercontent;
		this.orderprice=orderprice;
		this.marketprice=marketprice;
		this.ordernumber=ordernumber;
		this.paymoney=orderprice*ordernumber;
	}
	public String getOrderdata() {
		return orderdate;
	}
	public void setOrderdate(String orderdate) {
		this.orderdate = orderdate;
	}
	public String getOrderstate() {
		return orderstate;
	}
	public void setOrderstate(String orderstate) {
		this.orderstate = orderstate;
	}
	public String getOrdercontent() {
		return ordercontent;
	}
	public void setOrdercontent(String ordercontent) {
		this.ordercontent = ordercontent;
	}
	public double getOrderprice() {
		return orderprice;
	}
	public void setOrderprice(double orderprice) {
		this.orderprice = orderprice;
	}
	public double getMarketprice() {
		return marketprice;
	}
	public void setMarketprice(double marketprice) {
		this.marketprice = marketprice;
	}
	public int getOrdernumber() {
		return ordernumber;
	}
	public void setOrdernumber(int ordernumber) {
		this.ordernumber = ordernumber;
	}
	public double getPaymoney() {
		return paymoney=getOrdernumber()*getOrderprice();
	}

	
}
