package com.milai.ecoop.activity;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.milai.ecoop.R;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.utils.CommonUtils;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

public class OrderDetailActivity extends Activity implements View.OnClickListener {
    private Order order;
    private Team team;
    private ImageView iv_order_good,iv_print_ps,iv_print_zs,iv_print_wc;
    private TextView tv_order_content, tv_order_price, tv_market_price, tv_order_number, tv_order_no,
            tv_order_time, tv_username, tv_phonenumber, tv_address, tv_order_state_name,tv_order_time1
            ,color_zs,color_ps,color_wc;
    private Button bt_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Bundle bundle = getIntent().getExtras();
        order = (Order) (bundle.get("mInfo"));
        team = (Team) (bundle.get("team"));
        String from = getIntent().getStringExtra("from");
        String is_get = getIntent().getStringExtra("is_get");//
        String state = getIntent().getStringExtra("state");//20160606
        String express_no = getIntent().getStringExtra("express_no");
        iv_order_good = (ImageView) findViewById(R.id.iv_order_good);//商品图标
        iv_print_zs = (ImageView) findViewById(R.id.iv_print_zs);//组社图标
        iv_print_ps = (ImageView) findViewById(R.id.iv_print_ps);//配送图标
        iv_print_wc = (ImageView) findViewById(R.id.iv_print_wc);//完成图标
        color_zs = (TextView) findViewById(R.id.color_zs);//组社彩色
        color_ps = (TextView) findViewById(R.id.color_ps);//配送彩色
        color_wc = (TextView) findViewById(R.id.color_wc);//完成彩色
        tv_order_content = (TextView) findViewById(R.id.tv_order_content);//商品名称
        tv_order_price = (TextView) findViewById(R.id.tv_order_price);//现价
        tv_market_price = (TextView) findViewById(R.id.tv_market_price);//原价
        tv_order_number = (TextView) findViewById(R.id.tv_order_number);
        tv_order_no = (TextView) findViewById(R.id.tv_order_no);//订单编号
        tv_order_time = (TextView) findViewById(R.id.tv_order_time);//发货时间
        tv_order_time1= (TextView) findViewById(R.id.tv_order_time1);//预售时间
        tv_username = (TextView) findViewById(R.id.tv_username);//收货人
        tv_phonenumber = (TextView) findViewById(R.id.tv_phonenumber);//手机号码
        tv_address = (TextView) findViewById(R.id.tv_address);//地址
        tv_order_state_name = (TextView) findViewById(R.id.tv_order_state_name);//订单状态
        bt_return = (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        Picasso.with(this).load(NetDataHelper.imgdomain + team.getImage()).into(iv_order_good);
        tv_order_content.setText(team.getSummary());
        tv_order_price.setText("¥" + team.getPin_price());
        tv_market_price.setText("¥" + team.getMarket_price());

        tv_order_number.setText(order.getQuantity());
        tv_order_no.setText(order.getTrade_no());

        if (from != null) {
            tv_order_state_name.setText(order.getPin_tip());
            tv_order_time.setText(order.getCreate_time());

            Log.i("test", "from:" + from);
                Log.i("test", "from1:" + is_get);
                Log.i("test", "createTime:" + order.getCreate_time());
                Log.i("test", "createTime1:" + order.getDelivery_time());
        } else {
            Log.i("test", "all null");
            Log.i("test", "pay:" + is_get);
            Log.i("test", "createTime:" + order.getCreate_time());
            Log.i("test", "createTime1:" + order.getDelivery_time());

            tv_order_state_name.setText(order.getOrder_state());
            tv_order_time.setText(CommonUtils.DateUtilSecond(order.getCreate_time()));

        }
        Log.i("test", ".getState()" + order.getState());
        Log.i("test", "express_no" + order.getExpress_no());
        Log.i("test", "is_get" +order.getIs_get());
        if(order.getState().equals("pay")){
            iv_print_zs .setBackgroundResource(R.drawable.submit_orders);
            color_zs.setTextColor(getResources().getColor(R.color.btn_bg_green));//返回彩色
        }
        if(order.getExpress_no()!=null){
            iv_print_ps .setBackgroundResource(R.drawable.distribution);
            color_ps.setTextColor(getResources().getColor(R.color.btn_bg_green));//颜色返回彩色
        }
        if(order.getIs_get().equals("1")){
            iv_print_wc .setBackgroundResource(R.drawable.sign_for);
            color_wc.setTextColor(getResources().getColor(R.color.btn_bg_green));//返回彩色
        }

//        if
        tv_order_time1.setText(order.getDelivery_time());
        tv_username.setText(order.getRealname());
        tv_phonenumber.setText(order.getMobile());
        tv_address.setText(order.getAddress());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
