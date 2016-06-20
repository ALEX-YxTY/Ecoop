package com.milai.ecoop.app;

import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

public class MyApplication extends Application{
	public static int screenWidth;
	//用于存放倒计时时间
	public static Map<String,Long> map;

	@Override
	public void onCreate() {
		super.onCreate();
		WindowManager wm=(WindowManager) getSystemService(Context.WINDOW_SERVICE);
		screenWidth=wm.getDefaultDisplay().getWidth();

	}
	
}
