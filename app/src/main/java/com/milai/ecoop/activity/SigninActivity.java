package com.milai.ecoop.activity;

import cn.jpush.android.api.JPushInterface;


import com.milai.ecoop.Cookie;
import com.milai.ecoop.MyApplication;
import com.milai.ecoop.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.milai.ecoop.bean.User;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;


import java.util.Map;

/**
 * 注册界面，第一次splash后，或者退出登录状态时调用
 */
public class SigninActivity extends Activity {
    private Intent intent;
    private UMShareAPI mShareAPI = null;
    private SHARE_MEDIA platform = null;
    private NetDataHelper mNetDataHelper;
    private ProgressDialog progressDialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mShareAPI = MyApplication.mShareAPI;
        mNetDataHelper=NetDataHelper.getInstance(SigninActivity.this);
        progressDialog=new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在加载,请稍候");
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(SigninActivity.this);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(SigninActivity.this);
        MobclickAgent.onPause(this);
    }

    public void signout(View v) {
        switch (v.getId()) {
            case R.id.bt_close:
                finish();
                break;

            default:
                break;
        }
    }

    public void signin(View v) {
        switch (v.getId()) {
            case R.id.iv_tel_sign_in://电话登录界面
                intent = new Intent(SigninActivity.this, LoginActivity.class);
                startActivity(intent);
                break;


            case R.id.iv_wechat_sign_in:
                progressDialog.setMessage("正在登录,请稍候");
                progressDialog.show();
                //微信登录,进入授权界面，成功进入首页
                platform = SHARE_MEDIA.WEIXIN;
                mShareAPI.doOauthVerify(SigninActivity.this, platform, umAuthListener);
                progressDialog.dismiss();
                break;
        }

    }


    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            progressDialog.show();
            mShareAPI.getPlatformInfo(SigninActivity.this, platform, new UMAuthListener() {

                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                    Log.d("zcz", "map:"+map);
                    mNetDataHelper.loginSSO("wx", map.get("nickname"), map.get("sex"), map.get("unionid"), map.get("headimgurl"), map.get("openid"),
                            "91", new NetCallBack<User>() {
                                @Override
                                public void onSuccess(User data) {
                                    progressDialog.dismiss();
                                    MobclickAgent.onProfileSignIn("WX",Cookie.session.getUid());
                                    intent=new Intent(SigninActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onError(String error) {
                                    progressDialog.dismiss();
                                    Log.d("zcz",error);
                                }
                            });

                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                    progressDialog.dismiss();
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {
                    progressDialog.dismiss();
                }
            });
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mShareAPI.onActivityResult(requestCode, resultCode, data);

    }

}

