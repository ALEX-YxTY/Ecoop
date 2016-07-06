package com.milai.ecoop.bean;

public class Session {
	private String sid;
	private String uid;

	public Session() {
	}

	public String getSid() {
		if (sid != null) {
			return sid;
		}else {
			return "";
		}
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getUid() {
		if (uid != null) {
			return uid;
		}else {
			return "";
		}
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

}
