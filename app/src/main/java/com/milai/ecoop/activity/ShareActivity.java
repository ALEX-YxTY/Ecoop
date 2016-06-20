package com.milai.ecoop.activity;

import android.app.Activity;
import android.os.Bundle;

import com.milai.ecoop.R;
import com.milai.ecoop.dao.NetDataHelper;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/1/4 0004.
 */
public class ShareActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
