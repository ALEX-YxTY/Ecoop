package com.milai.ecoop.activity;


import com.milai.ecoop.Cookie;
import com.milai.ecoop.MyApplication;
import com.milai.ecoop.R;

import com.milai.ecoop.fragment.FragClickListener;
import com.milai.ecoop.fragment.MyGroupFrag;
import com.milai.ecoop.fragment.HomeFrag;
import com.milai.ecoop.fragment.MineFrag;
import com.milai.ecoop.fragment.OrderFrag;
import com.milai.ecoop.utils.CommonUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.umeng.update.UmengUpdateAgent;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private Intent intent;
    public static TextView title_main;
    public static TextView tv_choose_city;
    public static Button bt_return;
    public String city_id = "92";
    private FragmentManager fragmentManager;
    private RadioGroup radioGroup;
    private int currentPage = 0;
    private ImageButton share, news;
    private UMShareAPI mShareAPI = null;
    private ShareAction mShareAction;
    private Fragment homeFrag, mineFrag, myGroupFrag, orderFrag;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (!CommonUtils.isConn(this)) {
            CommonUtils.setNetworkMethod(this);
        }
        SharedPreferences spf = getApplicationContext().getSharedPreferences("city", MODE_APPEND);
        setContentView(R.layout.activity_main);
//        UmengUpdateAgent.update(this);
        mShareAPI = MyApplication.mShareAPI;
        mShareAction = new ShareAction(this);
        share = (ImageButton) findViewById(R.id.share);
        news = (ImageButton) findViewById(R.id.news);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.share:
                        MobclickAgent.onEvent(MainActivity.this,"wx_share");
                        SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE};
                        mShareAction.setDisplayList(displaylist).setShareboardclickCallback(shareBoardlistener).open();
                        break;
                }
            }
        });

        title_main = (TextView) findViewById(R.id.title_main);
        tv_choose_city = (TextView) findViewById(R.id.tv_choose_city);
        if (TextUtils.isEmpty(Cookie.getCityId(this)) || TextUtils.isEmpty(Cookie.getCityName(this))) {
            Intent i = new Intent(this, LocationActivity.class);
            startActivityForResult(i, 1);
        } else {
            city_id = Cookie.getCityId(this);
            tv_choose_city.setText(Cookie.getCityName(this));
        }
        bt_return = (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        homeFrag = HomeFrag.createInstance(fragClickListener);
        mineFrag = MineFrag.createInstance(fragClickListener);
        myGroupFrag = MyGroupFrag.createInstance(fragClickListener);
        orderFrag = OrderFrag.createInstance(fragClickListener);
        radioGroup = (RadioGroup) findViewById(R.id.rg_tab);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (currentPage == 2) {
                    if (((MineFrag) mineFrag).isRefreshing()) {
                        check(R.id.rb_mine);
                        return;
                    }
                }
                if (checkedId == R.id.rb_home) {
                    showFrag("home");
                } else if (checkedId == R.id.rb_mine) {
                    if (Cookie.session != null)
                        showFrag("mine");
                    else {
                        radioGroup.check(R.id.rb_home);
                        showLoginDialog();
                    }
                } else if (checkedId == R.id.rb_group_purchase) {
                    if (Cookie.session != null)
                        showFrag("grouppurchase");
                    else {
                        radioGroup.check(R.id.rb_home);
                        showLoginDialog();
                    }
                } else if (checkedId == R.id.rb_order) {
                    if (Cookie.session != null)
                        showFrag("order");
                    else {
                        radioGroup.check(R.id.rb_home);
                        showLoginDialog();
                    }
                }
            }
        });


        if (getIntent().getBooleanExtra("notification", false)) {
            radioGroup.check(R.id.rb_order);
        } else {
            radioGroup.check(R.id.rb_home);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            AlertDialog.Builder builder = new Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setMessage("是否退出程序？");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.finish();
                    System.exit(0);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            builder.create().show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {


        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            if (share_media == null) {
                if (snsPlatform.mKeyword.equals("11")) {
                    Toast.makeText(MainActivity.this, "add button success", Toast.LENGTH_LONG).show();
                }

            } else {
                UMImage image = new UMImage(MainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon));
                new ShareAction(MainActivity.this).setPlatform(share_media).setCallback(umShareListener)
                        .withTitle("e起来合作社，我们只做有品质的水果")
                        .withText("来来来，e合作社为大家提供品质，健康鲜果，帮助百万果农致富，一起来点赞吧").withMedia(image)
                        .withTargetUrl("http://ecoop.szdod.com")
                        .share();
            }
        }

    };
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(MainActivity.this, platform + " 分享成功啦",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MainActivity.this, platform + " 分享失败啦",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MainActivity.this, platform + " 分享取消了",
                    Toast.LENGTH_SHORT).show();
        }
    };

    public void check(int id) {
        radioGroup.check(id);
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage("请先登录");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MainActivity.this, SigninActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                radioGroup.check(findViewById(R.id.rb_home).getId());
            }
        });

        builder.create().show();
    }

    private void showVoteDialog() {
        AlertDialog.Builder builder = new Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage("新注册用户可参与调查获取优惠券，是否前往？(已参与调查的用户除外");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(MainActivity.this, VoteActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.create().show();
    }


    public void click(View v) {

        switch (v.getId()) {
            case R.id.tv_choose_city:

                intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivityForResult(intent, 1);
                break;

        }

    }

    public void showFrag(String frag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (frag.equals("home")) {
            if (currentPage != 1) {
                currentPage = 1;
                Bundle bundle=new Bundle();
                bundle.putString("mcity_id", city_id);
                homeFrag.setArguments(bundle);
                title_main.setText(R.string.app_name);
                tv_choose_city.setVisibility(View.VISIBLE);
                bt_return.setVisibility(View.GONE);
                share.setVisibility(View.VISIBLE);
                news.setVisibility(View.GONE);
                transaction.replace(R.id.fl_content, homeFrag);
                transaction.commit();
            }
        } else if (frag.equals("mine")) {
            if (currentPage != 2) {
                currentPage = 2;
                Bundle bundle=new Bundle();
                Boolean flag=false;
                bundle.putBoolean("flag", flag);
                mineFrag.setArguments(bundle);
                title_main.setText(R.string.mine_title);
                tv_choose_city.setVisibility(View.GONE);
                bt_return.setVisibility(View.GONE);
                share.setVisibility(View.GONE);
                news.setVisibility(View.VISIBLE);
                if(Cookie.getNotice(MainActivity.this)){
                    news.setImageResource(R.drawable.btn_news_dot);
                }else{
                    news.setImageResource(R.drawable.btn_news);
                }
                news.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.news:
                                news.setImageResource(R.drawable.btn_news);
                                Cookie.saveNotice(MainActivity.this,false);
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                transaction.replace(R.id.fl_content, mineFrag);
                transaction.commit();
            }
        } else if (frag.equals("grouppurchase")) {
            if (currentPage != 3) {
                currentPage = 3;
                Bundle bundle=new Bundle();
                Boolean flag=false;
                bundle.putBoolean("flag", flag);
                myGroupFrag.setArguments(bundle);
                title_main.setText(R.string.group_purchase);
                tv_choose_city.setVisibility(View.GONE);
                bt_return.setVisibility(View.GONE);
                share.setVisibility(View.VISIBLE);
                news.setVisibility(View.GONE);
                transaction.replace(R.id.fl_content, myGroupFrag);
                transaction.commit();
            }
        } else if (frag.equals("order")) {
            if (currentPage != 4) {
                currentPage = 4;
                Bundle bundle=new Bundle();
                Boolean flag=false;
                bundle.putBoolean("flag", flag);
                orderFrag.setArguments(bundle);
                title_main.setText(R.string.order);
                tv_choose_city.setVisibility(View.GONE);
                bt_return.setVisibility(View.GONE);
                share.setVisibility(View.VISIBLE);
                news.setVisibility(View.GONE);
                transaction.replace(R.id.fl_content, orderFrag);
                transaction.commit();
            }
        }

    }

    private FragClickListener fragClickListener = new FragClickListener() {

        @Override
        public void onViewSelected(View v) {
        }

        @Override
        public void enableMenuDrag(boolean b) {
        }
    };

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!CommonUtils.isConn(this)) {
            CommonUtils.setNetworkMethod(this);

        }
        if (Cookie.session == null) {
            check(R.id.rb_home);
        }else{
            MobclickAgent.onResume(this);
            if (Cookie.getLoginTimes(this) == 1) {
                showVoteDialog();
            }
        }



    }
    public void onPause() {
        super.onPause();
        if (Cookie.session != null) {

            MobclickAgent.onPause(this);

        }
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MyGroupFrag.REQUEST_GROUP_DETAIL) {
            check(R.id.rb_home);
        } else if (resultCode == RESULT_OK) {
            tv_choose_city.setText(data.getStringExtra("city"));
            city_id = data.getStringExtra("id");
            ((HomeFrag) homeFrag).onRefresh();

        }
        mShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}
