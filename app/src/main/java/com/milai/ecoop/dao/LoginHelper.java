package com.milai.ecoop.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.milai.ecoop.bean.GoodInfo;

import android.content.Context;

public class LoginHelper {
	private static LoginHelper signUpHelper;

	private LoginHelper() {

	}

	public static synchronized LoginHelper getSignUpHelper() {
		if (signUpHelper == null) {
			signUpHelper = new LoginHelper();
		}
		return signUpHelper;
	}

	public void getVerifyCode(Context context, String cityId, String mobile, final DataListener<String> listener) {
		RequestQueue mQueue = Volley.newRequestQueue(context);
		Map<String, String> map = new HashMap<String, String>();
		map.put("city_id", cityId);
		map.put("mobile", mobile);
		NormalPostRequest request = new NormalPostRequest("http://ecoop.szdod.com/rest/account/signup.php",
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject arg0) {
						List<GoodInfo> list = new ArrayList<GoodInfo>();
						try {
							int succeed = arg0.getJSONObject("status").getInt("succeed");
							if (succeed == 0) {
								listener.onError(arg0.getJSONObject("status").getString("error_desc"));
								return;
							}
							listener.onSuccess(arg0.getJSONObject("data").getString("verify"));
						} catch (Exception e) {
							listener.onError(arg0.toString());
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						listener.onError(arg0.toString());
					}
				}, map);
		mQueue.add(request);
	}
}
