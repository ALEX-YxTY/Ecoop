package com.milai.ecoop.wxapi;

import com.milai.ecoop.activity.GroupDetailActivity;
import com.milai.ecoop.activity.MainActivity;
import com.milai.ecoop.bean.Order;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    private IWXAPI api;
    public static Order order;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_pay_result);
        //tv = (TextView) findViewById(R.id.tv);
        api = WXAPIFactory.createWXAPI(this, "wx8ea0c0bc19258654");
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        //tv.setText("type:" + resp.getType() + "\nerrCode:" + resp.errCode + "\nerrStr:" + resp.errStr + "\nopenId:" + resp.openId + "\ntransaction:" + resp.transaction);
        Log.d("zzzzz", "type:" + resp.getType() + "\nerrCode:" + resp.errCode + "\nerrStr:" + resp.errStr + "\nopenId:" + resp.openId + "\ntransaction:" + resp.transaction);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX && resp.errCode == 0) {
            if (order.getIs_pin().equals("1")) {
                Intent i = new Intent(WXPayEntryActivity.this, GroupDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                bundle.putSerializable("flag", true);
                i.putExtras(bundle);
                startActivity(i);
            } else {
                Intent i = new Intent(WXPayEntryActivity.this, MainActivity.class);
                startActivity(i);
            }
        } else {
            finish();
        }
    }

}
