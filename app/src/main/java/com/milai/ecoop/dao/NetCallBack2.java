package com.milai.ecoop.dao;

/**
 * Created by xiong on 2015/12/30.
 */
public interface NetCallBack2<K, V> {
    void onSuccess(K data1, V data2);

    void onError(String error);
}
