package com.milai.ecoop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.milai.ecoop.R;
import com.umeng.analytics.MobclickAgent;

import java.text.BreakIterator;

/**
 * Created by Administrator on 2016/1/9 0009.
 */
public class PuzzleActivity extends Activity implements View.OnClickListener{
    private Button bt_return;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        bt_return= (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_return:
                finish();
                break;

        }
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
