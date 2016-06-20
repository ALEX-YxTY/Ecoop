package com.milai.ecoop.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;
import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.bean.Address;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.Session;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.bean.Ticket;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetCallBack2;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.pay.PayHelper;
import com.milai.ecoop.pay.PayResult;
import com.milai.ecoop.pay.SignUtils;
import com.milai.ecoop.wxapi.WXPayEntryActivity;
import com.squareup.picasso.Picasso;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;

public class
        PayActivity extends Activity implements OnClickListener {
    private Order order;
    private Team team;
    private String pin_id;
    private List<Ticket> tickets;
    private TextView tv_username, tv_phonenumber, tv_address, tv_order_content, tv_order_price, tv_fare, tv_total_price, tv_ticket, tv_ticket_price;
    private LinearLayout ll_addaddress, ll_address, ll_choose_ticket, ll_use_ticket;
    private ImageView iv_order_good, iv_arrow;
    private RadioGroup rg_pay_type;
    private Button btn_pay, btn_return;
    private PayHelper payHelper;
    private NetDataHelper netDataHelper;
    private IWXAPI api;
    private int payType = 0;
    private Ticket selectedTicket = null;
    private ProgressDialog progressDialog;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;
    private static final int ADD_ADDRESS = 1001;
    private static final int EDIT_ADDRESS = 1002;
    private static final int CHOOSE_TICKET = 1003;
    private boolean firstIn = false;
    private boolean allowEdit = false;
    private ShareAction mShareAction;
    private String card_id ="";
    private String address_id=null;
    private boolean back=false;
    private Address address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        payHelper = PayHelper.getInstance(this);
        netDataHelper = NetDataHelper.getInstance(this);
        Intent i = getIntent();
        order = (Order) i.getExtras().getSerializable("order");
        team = (Team) i.getExtras().getSerializable("team");
        pin_id = i.getStringExtra("pin_id");
        allowEdit = i.getBooleanExtra("allowEdit", true);
        if (!allowEdit) {
            iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
            iv_arrow.setVisibility(View.GONE);
        }
        tickets = (List<Ticket>) i.getExtras().getSerializable("tickets");
        if (order == null || team == null || tickets == null) {
            finish();
        }
        mShareAction = new ShareAction(this);
        initView();
    }

    private void initView() {
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("正在加载,请稍候");
        ll_address = (LinearLayout) findViewById(R.id.ll_address);
        ll_addaddress = (LinearLayout) findViewById(R.id.ll_addaddress);
        ll_addaddress.setOnClickListener(this);
        rg_pay_type = (RadioGroup) findViewById(R.id.rg_pay_type);
        rg_pay_type.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_wxpay:
                        payType = 1;
                        break;

                    case R.id.rb_alipay:
                        payType = 2;
                        break;
                }
            }
        });
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_phonenumber = (TextView) findViewById(R.id.tv_phonenumber);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_order_content = (TextView) findViewById(R.id.tv_order_content);
        tv_order_price = (TextView) findViewById(R.id.tv_order_price);
        tv_fare = (TextView) findViewById(R.id.tv_fare);
        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        btn_return = (Button) findViewById(R.id.bt_return);
        iv_order_good = (ImageView) findViewById(R.id.iv_order_good);
        ll_choose_ticket = (LinearLayout) findViewById(R.id.ll_choose_ticket);
        tv_ticket = (TextView) findViewById(R.id.tv_ticket);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        ll_use_ticket = (LinearLayout) findViewById(R.id.ll_use_ticket);
        tv_ticket_price = (TextView) findViewById(R.id.tv_ticket_price);

        if (TextUtils.isEmpty(order.getRealname()) || TextUtils.isEmpty(order.getMobile()) || TextUtils.isEmpty(order.getAddress())) {
            ll_addaddress.setVisibility(View.VISIBLE);
            ll_address.setVisibility(View.GONE);
        } else {
            ll_addaddress.setVisibility(View.GONE);
            ll_address.setVisibility(View.VISIBLE);
            if(back){

                tv_username.setText(address.getName());
                tv_phonenumber.setText(address.getMobile());
                tv_address.setText(address.getProvince() + address.getCity() + address.getArea() + address.getStreet());
            }else{
                address_id=order.getAid();
                tv_username.setText(order.getRealname());
                tv_phonenumber.setText(order.getMobile());
                tv_address.setText(order.getAddress());
            }

        }
        tv_order_content.setText(team.getTitle());
        tv_order_price.setText(order.getPrice());
        tv_fare.setText(order.getFare());
        tv_total_price.setText(String.valueOf(Float.parseFloat(order.getPrice()) + Float.parseFloat(order.getFare())));
        if (tickets.size() == 0) {
            tv_ticket.setText("暂无可用优代金券");
        } else {
            tv_ticket.setText(tickets.size() + "张可用代金券");
        }
        ll_address.setOnClickListener(this);
        if (!allowEdit) {
            ll_address.setClickable(false);
        }
        btn_return.setOnClickListener(this);
        ll_choose_ticket.setOnClickListener(this);
        Picasso.with(this).load(NetDataHelper.imgdomain + team.getImage()).into(iv_order_good);
        btn_pay.setOnClickListener(this);
    }

    private void pay() {

        final String myAid=address_id;

        if (ll_addaddress.getVisibility() == View.VISIBLE) {
            Toast.makeText(PayActivity.this, "请添加收货地址", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTicket != null) {
            card_id = selectedTicket.getId();
        }
        payHelper.PayState(Cookie.session, order.getId(), card_id,myAid, payType, new NetCallBack<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                try {

                    int succeed = data.getJSONObject("status").getInt("succeed");
                    Log.d("zcz", "succed:" + succeed);

                    if (succeed == 1) {
                        progressDialog.show();
                        switch (payType) {
                            case 1:
                                btn_pay.setEnabled(false);
                                api = WXAPIFactory.createWXAPI(getApplicationContext(), "wx8ea0c0bc19258654");
                                if (api.registerApp("wx8ea0c0bc19258654")) {

                                    payHelper.PayWX(Cookie.session, order.getId(), card_id,myAid, new NetCallBack<JSONObject>() {

                                        @Override
                                        public void onSuccess(JSONObject signParams) {

                                            btn_pay.setEnabled(true);
                                            progressDialog.dismiss();
                                            PayReq req = new PayReq();
                                            try {
                                                req.appId = signParams.getString("appid");
                                                req.partnerId = signParams.getString("partnerid");
                                                req.prepayId = signParams.getString("prepayid");
                                                req.nonceStr = signParams.getString("noncestr");
                                                req.timeStamp = String.valueOf(signParams.getLong("timestamp"));
                                                req.packageValue = signParams.getString("package");
                                                req.sign = signParams.getString("sign");
                                                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                                System.out.println("appId:" + req.appId);
                                                System.out.println("partnerId:" + req.partnerId);
                                                System.out.println("prepayId:" + req.prepayId);
                                                System.out.println("nonceStr:" + req.nonceStr);
                                                System.out.println("timeStamp:" + req.timeStamp);
                                                System.out.println("packageValue:" + req.packageValue);
                                                System.out.println("sign:" + req.sign);
                                                if (api.sendReq(req)) {
                                                    WXPayEntryActivity.order = order;
                                                    Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(PayActivity.this, "调起支付失败", Toast.LENGTH_SHORT).show();
                                            }
                                            MobclickAgent.onEvent(PayActivity.this, "pay_wx");
                                        }

                                        @Override
                                        public void onError(String error) {

                                            Toast.makeText(PayActivity.this, "调起支付失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                break;
                            case 2:
                                btn_pay.setEnabled(false);

                                payHelper.PayAli(Cookie.session, order.getId(), card_id,myAid, new NetCallBack<JSONObject>() {
                                    @Override
                                    public void onSuccess(JSONObject data) {

                                        Log.d("zcz", data.toString());
                                        progressDialog.dismiss();
                                        btn_pay.setEnabled(true);
                                        try {
                                            String orderInfo = data.getString("orderInfo");
                                            Log.d("orderInfo", orderInfo);
                                            String sign = data.getString("sign").replace("\\/", "/");
                                            Log.d("sign_unencode", sign);
                                            //sign = URLEncoder.encode(sign);
                                            Log.d("sign_encode", sign);
                                            final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
                                            Log.d("payInfo", payInfo);
                                            Runnable payRunnable = new Runnable() {

                                                @Override
                                                public void run() {
                                                    // 构造PayTask 对象
                                                    PayTask alipay = new PayTask(PayActivity.this);
                                                    // 调用支付接口，获取支付结果
                                                    String result = alipay.pay(payInfo);

                                                    Message msg = new Message();
                                                    msg.what = SDK_PAY_FLAG;
                                                    msg.obj = result;
                                                    mHandler.sendMessage(msg);
                                                }
                                            };
                                            MobclickAgent.onEvent(PayActivity.this, "pay_ali");
                                            // 必须异步调用
                                            Thread payThread = new Thread(payRunnable);
                                            payThread.start();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void onError(String error) {
                                        btn_pay.setEnabled(true);
                                        progressDialog.dismiss();
                                        Toast.makeText(PayActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            default:
                                btn_pay.setEnabled(true);
                                Toast.makeText(PayActivity.this, "请选择付款方式", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                break;
                        }
                    } else {



                       new AlertDialog.Builder(PayActivity.this).setTitle("提示")
                                .setMessage("该社已经合作成功，您可以选择重新开社或选择其他商品！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();






                                    }
                                }).show();;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });


    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    Log.d("payresult", payResult.toString());
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(PayActivity.this, "支付成功",
                                Toast.LENGTH_SHORT).show();
                        if (order.getIs_pin().equals("1")) {
                            Intent i = new Intent(PayActivity.this, GroupDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("order", order);
                            bundle.putSerializable("flag", true);
                            i.putExtras(bundle);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(PayActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayActivity.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(PayActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.btn_pay:

                pay();
                break;
            case R.id.bt_return:
                finish();
                break;
            case R.id.ll_addaddress:
                i = new Intent(this, ChooseAddressActivity.class);
                startActivityForResult(i, EDIT_ADDRESS);
                break;
            case R.id.ll_address:
                i = new Intent(this, ChooseAddressActivity.class);
                startActivityForResult(i, EDIT_ADDRESS);
                break;
            case R.id.ll_choose_ticket:
                i = new Intent(this, ChooseTicketActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("tickets", (Serializable) tickets);
                i.putExtras(bundle);
                startActivityForResult(i, CHOOSE_TICKET);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) return;
        if (resultCode == RESULT_OK && requestCode == ADD_ADDRESS) {
            teamBuy();
        } else if (resultCode == RESULT_OK && requestCode == EDIT_ADDRESS) {
            address= (Address) data.getSerializableExtra("address");
            address_id=address.getId();
            back=data.getBooleanExtra("back",false);
            teamBuy();
        } else if (resultCode == RESULT_OK && requestCode == CHOOSE_TICKET) {
            int pos = data.getIntExtra("position", -1);
            tv_ticket.setVisibility(View.GONE);
            ll_use_ticket.setVisibility(View.VISIBLE);
            tv_ticket_price.setText(tickets.get(pos).getCredit());
            selectedTicket = tickets.get(pos);
        } else if (resultCode == RESULT_CANCELED && requestCode == EDIT_ADDRESS) {
            tv_ticket.setVisibility(View.VISIBLE);
            ll_use_ticket.setVisibility(View.GONE);
            selectedTicket = null;
        } else if(resultCode==RESULT_CANCELED&&requestCode==CHOOSE_TICKET){
            tv_ticket.setVisibility(View.VISIBLE);
            ll_use_ticket.setVisibility(View.GONE);
            selectedTicket = null;
            card_id="";
        }
    }

    private void teamBuy() {
        boolean isPin;
        if (order.getIs_pin().equals("1")) {
            isPin = true;
        } else {
            isPin = false;
        }
        progressDialog.show();
        payHelper.buyTeam(Cookie.session, team.getId(), isPin, pin_id, new NetCallBack<String>() {

            @Override
            public void onSuccess(String data) {
                netDataHelper.checkOrder(Cookie.session, data, new NetCallBack2<List<Ticket>, Order>() {
                    @Override
                    public void onSuccess(List<Ticket> data1, Order data2) {
                        tickets.clear();
                        tickets.addAll(data1);
                        order = data2;

                        Log.d("zcz",data2.getAddress()+data2.getRealname());
                        if (TextUtils.isEmpty(order.getRealname()) || TextUtils.isEmpty(order.getMobile()) || TextUtils.isEmpty(order.getAddress())) {
                            ll_addaddress.setVisibility(View.VISIBLE);
                            ll_address.setVisibility(View.GONE);
                        } else {

                            ll_addaddress.setVisibility(View.GONE);
                            ll_address.setVisibility(View.VISIBLE);
                            if(back){
                                tv_username.setText(address.getName());
                                tv_phonenumber.setText(address.getMobile());
                                tv_address.setText(address.getProvince() + address.getCity() + address.getArea() + address.getStreet());
                            }else{
                                tv_username.setText(order.getRealname());
                                tv_phonenumber.setText(order.getMobile());
                                tv_address.setText(order.getAddress());
                            }

                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(PayActivity.this, "团购失败,请稍后再试", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Toast.makeText(PayActivity.this, "团购失败,请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstIn) {
            firstIn = false;
        } else if (allowEdit) {
            //teamBuy();
        }
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
