package com.milai.ecoop.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.activity.MainActivity;
import com.milai.ecoop.activity.TeamDetailActivity;
import com.milai.ecoop.utils.ExampleUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
        if (intent.getAction().equals("cn.jpush.android.intent.NOTIFICATION_OPENED")) {
            JPushInterface.clearNotificationById(context,notificationId);
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("notification", true);
            i.putExtras(bundle);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            context.startActivity(i);

        }
        if (intent.getAction().equals("cn.jpush.android.intent.NOTIFICATION_RECEIVED")) {
            Cookie.saveNotice(context,true);
        }
    }

}
