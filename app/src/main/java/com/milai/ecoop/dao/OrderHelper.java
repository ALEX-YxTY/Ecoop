package com.milai.ecoop.dao;

public class OrderHelper {
	private static OrderHelper orderhelper;
	private OrderHelper() {

	}
	public static synchronized OrderHelper OrderHelper() {
		if (orderhelper == null) {
			orderhelper = new OrderHelper();
		}
		return orderhelper;
	}	
}
