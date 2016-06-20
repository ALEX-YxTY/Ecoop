package com.milai.ecoop.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.pay.PayHelper;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class ActTicketActivity extends Activity implements View.OnClickListener{
    private Button bt_return;
    private EditText et_ticno;
    private TextView tv_validate;
    private PayHelper payHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actticket);
        bt_return= (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
        et_ticno= (EditText) findViewById(R.id.et_ticno);
        tv_validate= (TextView) findViewById(R.id.tv_validate);
        tv_validate.setOnClickListener(this);
        Log.d("zcz",Cookie.session.toString());
        payHelper=PayHelper.getInstance(ActTicketActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_return:
                finish();
                break;
            case R.id.tv_validate:
                payHelper.actTickets(Cookie.session, et_ticno.getText().toString(), new NetCallBack<String>() {
                    @Override
                    public void onSuccess(String data) {
                        Toast.makeText(ActTicketActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ActTicketActivity.this, "券号无效或者过期或者不存在，请检查券号", Toast.LENGTH_SHORT).show();
                    }
                });
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





