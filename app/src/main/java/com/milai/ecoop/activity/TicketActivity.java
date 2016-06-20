package com.milai.ecoop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.adapter.CoupunAdapter;
import com.milai.ecoop.bean.Ticket;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.pay.PayHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class TicketActivity extends Activity implements AbsListView.OnScrollListener {
    private ListView lv_coupun;
    private RadioGroup radioGroup;
    private PayHelper payHelper;
    private List<Ticket> data;
    private CoupunAdapter adapter;
    private int page = 1;
    private boolean isLastRow = false;
    private int state = 1;
    private Button bt_return,activited;
    private TextView title_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        bt_return = (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activited= (Button) findViewById(R.id.activited);
        activited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.activited:
                        Intent intent=new Intent(TicketActivity.this,ActTicketActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        lv_coupun = (ListView) findViewById(R.id.lv_coupun);
        payHelper = PayHelper.getInstance(this);
        data = new ArrayList<>();
        adapter = new CoupunAdapter(this, data);
        lv_coupun.setAdapter(adapter);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_coupun);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_wait_use:
                        state = 1;
                        adapter.setState(1);
                        break;
                    case R.id.rb_unactivited:
                        state = 2;
                        adapter.setState(2);
                        break;
                    case R.id.rb_overdue:
                        state = 3;
                        adapter.setState(2);
                        break;
                }
                data.clear();
                adapter.notifyDataSetChanged();
                page = 1;
                getData(page, state);
            }
        });
        getData(page, state);
    }



    public void getData(final int p, int state) {
        payHelper.getTickets(Cookie.session, p, 10, state, new NetCallBack<List<Ticket>>() {

            @Override
            public void onSuccess(List<Ticket> data) {
                TicketActivity.this.data.addAll(data);
                adapter.notifyDataSetChanged();
                lv_coupun.setBackgroundResource(R.color.normal_bg);

            }

            @Override
            public void onError(String error) {
                Log.d("TickcetActivity", error);
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            //加载元素
            getData(++page, state);
            isLastRow = false;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
            isLastRow = true;
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
