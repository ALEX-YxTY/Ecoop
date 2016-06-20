package com.milai.ecoop.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.milai.ecoop.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/1/14 0014.
 */
public class CallmeActivity extends Activity implements View.OnClickListener {
    private LinearLayout rl_phone,rl_email,rl_myweb;
    private Button bt_return;
    private  Intent intent;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callme);
        rl_phone = (LinearLayout) findViewById(R.id.rl_phone);
        rl_phone.setOnClickListener(this);
        bt_return= (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
        rl_email= (LinearLayout) findViewById(R.id.rl_email);
        rl_email.setOnClickListener(this);
        rl_myweb= (LinearLayout) findViewById(R.id.rl_myweb);
        rl_myweb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_phone:
                /*intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "051255253827"));

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);*/
                // 只进入拨号界面，不拨打
                uri = Uri.parse("tel:" + "051255253827");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
                break;
            case R.id.rl_email:
                Intent data=new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:d2d_home@szdod.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
                data.putExtra(Intent.EXTRA_TEXT, "这是内容");
                startActivity(data);
                break;
            case R.id.rl_myweb:
                uri = Uri.parse("http://www.myeco-op.com/");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
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
