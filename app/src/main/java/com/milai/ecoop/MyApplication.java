package com.milai.ecoop;

import java.util.Map;

import cn.jpush.android.api.JPushInterface;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.WindowManager;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

public class MyApplication extends Application {
    public static int screenWidth;
    //用于存放倒计时时间
    public static Map<String, Long> map;
    public static UMShareAPI mShareAPI =  null;;
    @Override
    public void onCreate() {
        super.onCreate();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        Cookie.loadSession(this);
        Cookie.loadUser(this);
        mShareAPI =  UMShareAPI.get(this);
    }

    {
        PlatformConfig.setWeixin("wx8ea0c0bc19258654", "d8c9c732cfb545ea9554922926c61d6b");
    }


}
