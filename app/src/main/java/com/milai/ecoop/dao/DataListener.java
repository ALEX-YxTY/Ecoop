package com.milai.ecoop.dao;

public interface DataListener<T> {
	void onSuccess(T t);

	void onError(String error);
}
