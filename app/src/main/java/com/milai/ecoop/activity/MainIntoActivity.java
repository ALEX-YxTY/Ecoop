package com.milai.ecoop.activity;

import android.app.AlertDialog;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.MyApplication;
import com.milai.ecoop.R;
import com.milai.ecoop.bean.Category;
import com.milai.ecoop.fragment.FragClickListener;
import com.milai.ecoop.fragment.HomeFrag;
import com.milai.ecoop.fragment.HomeIntoFrag;
import com.milai.ecoop.fragment.MineFrag;
import com.milai.ecoop.fragment.MyGroupFrag;
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

/**
 * Created by Administrator on 2016/4/22 0022.
 */
public class MainIntoActivity extends FragmentActivity implements View.OnClickListener {

    private Intent intent;
    public static TextView title_main;

    public static Button bt_return;
    public String city_id = "92";
    private FragmentManager fragmentManager;
    private RadioGroup radioGroup;
    private int currentPage = 0;
    private ImageButton share, news;
    private UMShareAPI mShareAPI = null;
    private ShareAction mShareAction;
    private Category category;
    private Fragment homeintoFrag, mineFrag, myGroupFrag, orderFrag;

    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);
        if (!CommonUtils.isConn(this)) {
            CommonUtils.setNetworkMethod(this);
        }
        SharedPreferences spf = getApplicationContext().getSharedPreferences("city", MODE_APPEND);
        setContentView(R.layout.activity_main_into);
        UmengUpdateAgent.update(this);
        mShareAPI = MyApplication.mShareAPI;
        mShareAction = new ShareAction(this);
        city_id=getIntent().getStringExtra("city_id");
        category= (Category) getIntent().getSerializableExtra("category");
        share = (ImageButton) findViewById(R.id.share);

        news = (ImageButton) findViewById(R.id.news);
        share.setVisibility(View.GONE);
        news.setVisibility(View.GONE);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.share:
                        MobclickAgent.onEvent(MainIntoActivity.this, "wx_share");
                        SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE};
                        mShareAction.setDisplayList(displaylist).setShareboardclickCallback(shareBoardlistener).open();
                        break;
                }
            }
        });

        title_main = (TextView) findViewById(R.id.title_main);


        bt_return = (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fragmentManager = getSupportFragmentManager();
        homeintoFrag = HomeIntoFrag.createInstance(fragClickListener);
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
                    showFrag("homeinto");
                } else if (checkedId == R.id.rb_mine) {

                        showFrag("mine");

                } else if (checkedId == R.id.rb_group_purchase) {

                        showFrag("grouppurchase");

                } else if (checkedId == R.id.rb_order) {

                        showFrag("order");

                }
            }
        });

        radioGroup.check(R.id.rb_home);

    }



    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {


        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            if (share_media == null) {
                if (snsPlatform.mKeyword.equals("11")) {
                    Toast.makeText(MainIntoActivity.this, "add button success", Toast.LENGTH_LONG).show();
                }

            } else {
                UMImage image = new UMImage(MainIntoActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon));
                new ShareAction(MainIntoActivity.this).setPlatform(share_media).setCallback(umShareListener)
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
            Toast.makeText(MainIntoActivity.this, platform + " 分享成功啦",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MainIntoActivity.this, platform + " 分享失败啦",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MainIntoActivity.this, platform + " 分享取消了",
                    Toast.LENGTH_SHORT).show();
        }
    };

    public void check(int id) {
        radioGroup.check(id);
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainIntoActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage("请先登录");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MainIntoActivity.this, SigninActivity.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainIntoActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage("新注册用户可参与调查获取优惠券，是否前往？(已参与调查的用户除外");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(MainIntoActivity.this, VoteActivity.class);
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

                intent = new Intent(MainIntoActivity.this, LocationActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.bt_return:
                finish();
                break;
        }

    }

    public void showFrag(String frag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (frag.equals("homeinto")) {
            if (currentPage != 1) {
                currentPage = 1;
                Bundle bundle=new Bundle();
                bundle.putSerializable("category",category);
                bundle.putString("city_id",city_id);
                homeintoFrag.setArguments(bundle);
                //title_main.setText(R.string.app_name);
                title_main.setText(category.getName());
                bt_return.setVisibility(View.VISIBLE);
                share.setVisibility(View.GONE);
                news.setVisibility(View.GONE);
                transaction.replace(R.id.fl_content, homeintoFrag);
                transaction.commit();
            }
        } else if (frag.equals("mine")) {
            if (currentPage != 2) {
                currentPage = 2;
                Bundle bundle=new Bundle();
                Boolean flag=true;
                bundle.putBoolean("flag", flag);
                mineFrag.setArguments(bundle);
                title_main.setText(R.string.mine_title);
                bt_return.setVisibility(View.GONE);
                share.setVisibility(View.GONE);
                news.setVisibility(View.VISIBLE);
                if(Cookie.getNotice(MainIntoActivity.this)){
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
                                Cookie.saveNotice(MainIntoActivity.this,false);
                                intent = new Intent(MainIntoActivity.this, NoticeActivity.class);
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
                title_main.setText(R.string.group_purchase);
                bt_return.setVisibility(View.GONE);
                share.setVisibility(View.VISIBLE);
                news.setVisibility(View.GONE);
                transaction.replace(R.id.fl_content, myGroupFrag);
                transaction.commit();
            }
        } else if (frag.equals("order")) {
            if (currentPage != 4) {
                currentPage = 4;

                title_main.setText(R.string.order);
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



}

