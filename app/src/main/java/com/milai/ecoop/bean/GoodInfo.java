package com.milai.ecoop.bean;

public class GoodInfo {
	private String goodsUrl;
	private String goodsTitle;
	private String goodsContent;
	private double teamPrice;
	private String teamWay;
	private double marketPrice;
	public GoodInfo(String goodsUrl,String goodsTitle,String goodsContent,
			double teamPrice,double marketPrice,String teamWay
			){
		this.goodsUrl=goodsUrl;
		this.goodsTitle=goodsTitle;
		this.goodsContent=goodsContent;
		this.teamPrice=teamPrice;
		this.marketPrice=marketPrice;
		this.teamPrice=teamPrice;
	}
	public String getGoodsUrl() {
		return goodsUrl;
	}
	public void setGoodsUrl(String goodsUrl) {
		this.goodsUrl = goodsUrl;
	}
	public String getGoodsTitle() {
		return goodsTitle;
	}
	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}
	public String getGoodsContent() {
		return goodsContent;
	}
	public void setGoodsContent(String goodsContent) {
		this.goodsContent = goodsContent;
	}
	public double getTeamPrice() {
		return teamPrice;
	}
	public void setTeamPrice(double teamPrice) {
		this.teamPrice = teamPrice;
	}
	public String getTeamWay() {
		return teamWay;
	}
	public void setTeamWay(String teamWay) {
		this.teamWay = teamWay;
	}
	public double getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(double marketPrice) {
		this.marketPrice = marketPrice;
	}
	
}
