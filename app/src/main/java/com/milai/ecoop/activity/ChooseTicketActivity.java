package com.milai.ecoop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.milai.ecoop.R;
import com.milai.ecoop.adapter.CoupunAdapter;
import com.milai.ecoop.bean.Ticket;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class ChooseTicketActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView lv_coupun;
    private Button bt_return,clear;
    private CoupunAdapter adapter;
    private List<Ticket> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ticket);
        data = (List<Ticket>) (getIntent().getExtras().getSerializable("tickets"));
        Log.d("chooseticket size", data.size() + "");
        if (data == null) return;
        lv_coupun = (ListView) findViewById(R.id.lv_coupun);
        adapter = new CoupunAdapter(this, data);
        lv_coupun.setAdapter(adapter);
        lv_coupun.setOnItemClickListener(this);
        bt_return = (Button) findViewById(R.id.bt_return);
        clear= (Button) findViewById(R.id.clear);
        clear.setOnClickListener(this);
        bt_return.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent();
        i.putExtra("position", position);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_return:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.clear:
                setResult(RESULT_CANCELED);
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
