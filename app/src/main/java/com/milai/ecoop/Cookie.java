package com.milai.ecoop;

import com.milai.ecoop.bean.Session;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.bean.User;
import com.milai.ecoop.utils.ExampleUtil;
import com.umeng.socialize.net.utils.Base64;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class Cookie {
    public static Session session;
    public static User user;
    private static Context context;
    private static final String TAG = "Cookie";
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_REMOMVE_ALIAS = 1002;
    private static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(context.getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;
                case MSG_REMOMVE_ALIAS:
                    JPushInterface.setAliasAndTags(context.getApplicationContext(), "", null, null);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

    private static final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("alias", Context.MODE_APPEND);
                    Editor editor = preferences.edit();
                    editor.putBoolean("isSet", true);
                    editor.apply();
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
        }
    };

    public static void setAlias(Context c, String alias) {
        context = c;
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("alias", Context.MODE_APPEND);
        if (preferences.getBoolean("isSet", false))
            return;
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    public static void removeAlias(Context c) {
        context = c;
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("alias", Context.MODE_APPEND);
        Editor editor = preferences.edit();
        editor.putBoolean("isSet", false);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_REMOMVE_ALIAS));
    }

    public static void saveSession(Context context, Session s) {
        String sid = s.getSid();
        String uid = s.getUid();
        if (sid.equals("") || uid.equals("")) {
            session = null;
            return;
        } else {
            if (session == null)
                session = new Session();
        }
        session.setSid(sid);
        session.setUid(uid);
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("session", Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.putString("sid", sid);
        editor.putString("uid", uid);
        editor.commit();

    }

    public static void loadSession(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("session", Context.MODE_APPEND);
        String sid = pref.getString("sid", "");
        String uid = pref.getString("uid", "");
        if (sid.equals("") || uid.equals("")) {
            session = null;
        } else {
            if (session == null)
                session = new Session();
            session.setSid(sid);
            session.setUid(uid);
        }
    }

    public static void clearCookie(Context context) {
        session = null;
        user = null;
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("session", Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        SharedPreferences pref1 = context.getApplicationContext().getSharedPreferences("user", Context.MODE_APPEND);
        Editor editor1 = pref1.edit();
        editor1.clear();
        editor1.apply();

//        SharedPreferences pref2 = context.getApplicationContext().getSharedPreferences("login", Context.MODE_APPEND);
//        Editor editor2 = pref2.edit();
//        editor2.putInt("times", 0);
//        editor2.apply();
    }

    public static String getCityId(Context context) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("city", Context.MODE_APPEND);
        return preferences.getString("city_id", "");
    }

    public static String getCityName(Context context) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("city", Context.MODE_APPEND);
        return preferences.getString("city_name", "");
    }

    public static void saveCityId(Context context, String city_id) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("city", Context.MODE_APPEND);
        Editor editor = preferences.edit();
        editor.putString("city_id", city_id);
        editor.apply();
    }

    public static void saveCityName(Context context, String cityName) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("city", Context.MODE_APPEND);
        Editor editor = preferences.edit();
        editor.putString("city_name", cityName);
        editor.apply();
    }
    public static boolean getNotice(Context context){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("notice", Context.MODE_APPEND);
        return preferences.getBoolean("noticeflag", false);
    }
    public static void saveNotice(Context context,boolean notice){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("notice", Context.MODE_APPEND);
        Editor editor = preferences.edit();
        editor.putBoolean("noticeflag", notice);
        editor.apply();
    }
    public static void saveHomePage(Context context, List<Team> data) {
        Log.d("session", session.getUid());
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("home_page", Context.MODE_APPEND);
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(data);
            // 将字节流编码成base64的字符窜
            String oAuth_Base64 = Base64.encodeBase64String(baos.toByteArray());
            Editor editor = preferences.edit();
            editor.putString("data", oAuth_Base64);
            editor.apply();
        } catch (IOException e) {
        }
        Log.i("ok", "存储成功");
    }

    public static List<Team> loadHomePage(Context context) {
        List<Team> data = null;
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("home_page", Context.MODE_APPEND);
        String productBase64 = preferences.getString("data", "");

        //读取字节
        byte[] base64 = Base64.decodeBase64(productBase64);
        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            try {
                //读取对象
                data = (List<Team>) bis.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


    public static void saveUser(Context context, User u) {
        Log.d("session", session.getUid());
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_APPEND);
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(u);
            // 将字节流编码成base64的字符窜
            String oAuth_Base64 = Base64.encodeBase64String(baos.toByteArray());
            Editor editor = preferences.edit();
            editor.putString("user", oAuth_Base64);
            editor.apply();
        } catch (IOException e) {
            // TODO Auto-generated
        }
        user = u;
        Log.i("ok", "存储成功");
    }


    public static void loadUser(Context context) {
        User u = null;
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_APPEND);
        String productBase64 = preferences.getString("user", "");

        //读取字节
        byte[] base64 = Base64.decodeBase64(productBase64);
        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            try {
                //读取对象
                u = (User) bis.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        user = u;
    }

    public static int getLoginTimes(Context context) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("login", Context.MODE_APPEND);
        int times = preferences.getInt("times",0);
        return times;
    }

    public static void addLoginTimes(Context context,int times) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("login", Context.MODE_APPEND);
        Editor editor = preferences.edit();
        editor.putInt("times", getLoginTimes(context));
        editor.apply();
    }

}
