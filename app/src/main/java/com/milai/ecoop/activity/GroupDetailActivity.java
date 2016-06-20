package com.milai.ecoop.activity;

import java.io.Serializable;
import java.security.acl.Group;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.milai.ecoop.Cookie;
import com.milai.ecoop.MyApplication;
import com.milai.ecoop.R;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.SharedTeam;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.bean.User;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.utils.CommonUtils;
import com.milai.ecoop.view.CircleImageView;
import com.milai.ecoop.view.CountDownView;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class GroupDetailActivity extends Activity implements OnClickListener {
    private View footview;
    private ImageView iv_order_good, iv_order_good_failure, iv_order_good_ecome;
    private TextView tv_order_name, tv_team, tv_team_price, tv_order_name_failure, tv_team_failure, tv_team_price_failure, tv_remain,
            tv_order_name_ecome, tv_team_ecome, tv_team_price_ecome,remaining;
    private CircleImageView avatar1, avatar2, avatar3, avatar4, avatar5;
    private List<CircleImageView> avatars;
    private CountDownView countDownView;
    private NetDataHelper netDataHelper;
    private Order order;
    private String id;
    private String time;
    private Gson gson;
    private LinearLayout ll_buy_success, Rl_buy_failure, ll_counttime, ll_avatar_horizon, ll_avatar_vertical, ll_buy_e_come, ll_play_intro;
    private RelativeLayout rl_buy_success, rl_buy_failure, rl_buy_ecome;
    private Button btn_return, bt_creatteam;
    private ProgressDialog progressDialog;
    private ImageButton share;
    private ImageView bg_1, bg_2, bg_3, bg_4;
    private UMShareAPI mShareAPI = null;
    private ShareAction mShareAction;
    private Team team;
    private List<User> list;
    private String end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("正在加载,请稍候");
        progressDialog.setCancelable(false);
        boolean flag = getIntent().getExtras().getBoolean("flag", false);
        if (flag) {

            AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetailActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setMessage("支付成功，赶快分享给您的好友e起来吧!");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                            {
                                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE
                            };

                    mShareAction.setDisplayList(displaylist)
                            .setShareboardclickCallback(shareBoardlistener)
                            .open();

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

        bg_1 = (ImageView) findViewById(R.id.bg_1);
        bg_2 = (ImageView) findViewById(R.id.bg_2);
        bg_3 = (ImageView) findViewById(R.id.bg_3);
        bg_4 = (ImageView) findViewById(R.id.bg_4);
        order = (Order) getIntent().getExtras().getSerializable("order");
        id = order.getId();
        ll_buy_success = (LinearLayout) findViewById(R.id.ll_buy_success);
        Rl_buy_failure = (LinearLayout) findViewById(R.id.Rl_buy_failure);
        rl_buy_success = (RelativeLayout) findViewById(R.id.rl_buy_success);
        rl_buy_failure = (RelativeLayout) findViewById(R.id.rl_buy_failure);
        ll_counttime = (LinearLayout) findViewById(R.id.ll_counttime);
        ll_avatar_horizon = (LinearLayout) findViewById(R.id.ll_avatar_horizon);
        ll_avatar_vertical = (LinearLayout) findViewById(R.id.ll_avatar_vertical);
        ll_buy_e_come = (LinearLayout) findViewById(R.id.ll_buy_e_come);
        rl_buy_ecome = (RelativeLayout) findViewById(R.id.rl_buy_ecome);
        avatar1 = (CircleImageView) findViewById(R.id.avatar1);
        avatar2 = (CircleImageView) findViewById(R.id.avatar2);
        avatar3 = (CircleImageView) findViewById(R.id.avatar3);
        avatar4 = (CircleImageView) findViewById(R.id.avatar4);
        avatar5 = (CircleImageView) findViewById(R.id.avatar5);
        avatars = new ArrayList<>();
        avatars.add(avatar1);
        avatars.add(avatar2);
        avatars.add(avatar3);
        avatars.add(avatar4);
        avatars.add(avatar5);
        btn_return = (Button) findViewById(R.id.bt_return);
        btn_return.setOnClickListener(this);
        bt_creatteam = (Button) findViewById(R.id.bt_creatteam);
        bt_creatteam.setOnClickListener(this);
        remaining= (TextView) findViewById(R.id.remaining);
        netDataHelper = NetDataHelper.getInstance(this);
        gson = new Gson();
        iv_order_good = (ImageView) findViewById(R.id.iv_order_good);
        tv_order_name = (TextView) findViewById(R.id.tv_order_name);
        tv_team = (TextView) findViewById(R.id.tv_team);
        tv_team_price = (TextView) findViewById(R.id.tv_team_price);
        iv_order_good_failure = (ImageView) findViewById(R.id.iv_order_good_failure);
        tv_order_name_failure = (TextView) findViewById(R.id.tv_order_name_failure);
        tv_team_failure = (TextView) findViewById(R.id.tv_team_failure);
        tv_team_price_failure = (TextView) findViewById(R.id.tv_team_price_failure);
        tv_remain = (TextView) findViewById(R.id.tv_remain);
        iv_order_good_ecome = (ImageView) findViewById(R.id.iv_order_good_ecome);
        tv_order_name_ecome = (TextView) findViewById(R.id.tv_order_name_ecome);
        tv_team_ecome = (TextView) findViewById(R.id.tv_team_ecome);
        tv_team_price_ecome = (TextView) findViewById(R.id.tv_team_price_ecome);
        footview = getLayoutInflater().inflate(R.layout.item_play_introduction, null);
        ll_play_intro = (LinearLayout) findViewById(R.id.ll_play_intro);
        ll_play_intro.setOnClickListener(this);
        share = (ImageButton) findViewById(R.id.share);
        share.setOnClickListener(this);
        mShareAPI = MyApplication.mShareAPI;
        mShareAction = new ShareAction(this);
        getData(id);
        countDownView = (CountDownView) findViewById(R.id.count_down_view);
    }

    private void getData(String id) {
        progressDialog.show();
        netDataHelper.getGroupDetail(Cookie.session, id, new NetCallBack<JSONObject>() {

            @Override
            public void onSuccess(JSONObject data) {
                team = null;
                list = null;
                try {
                    team = gson.fromJson(data.getJSONObject("team").toString(), Team.class);
                    Picasso.with(GroupDetailActivity.this).load(NetDataHelper.imgdomain + team.getImage())
                            .into(iv_order_good);

                    list = gson.fromJson(data.getJSONArray("buy_user").toString(),
                            new TypeToken<List<User>>() {
                            }.getType());
                    tv_remain.setText(data.getString("remainnum"));
                    end_time = data.getString("pin_endtime");
                    remaining.setText("成社时间："+data.getString("pin_finish_time"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
               /* String create_time = order.getCreate_time();
                if (create_time.contains("-")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                    try {
                        create_time = String.valueOf(sdf.parse(create_time).getTime() / 1000);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                long time = Long.parseLong(create_time) + (Long.parseLong(team.getPin_day()) * (long) 86400);*/
                long time = Long.parseLong(end_time);


                long currentSecond = System.currentTimeMillis() / 1000;
                Log.d("zcz", currentSecond + ":" + time);
                if (Integer.parseInt(team.getMin_number()) == list.size()) {
                    ll_buy_success.setVisibility(View.VISIBLE);
                    Rl_buy_failure.setVisibility(View.GONE);
                    rl_buy_success.setVisibility(View.VISIBLE);
                    rl_buy_failure.setVisibility(View.GONE);
                    ll_counttime.setVisibility(View.GONE);
                    ll_buy_e_come.setVisibility(View.GONE);
                    rl_buy_ecome.setVisibility(View.GONE);
                    bg_1.setImageResource(R.drawable.write_1);
                    bg_2.setImageResource(R.drawable.write_2);
                    bg_3.setImageResource(R.drawable.write_3);
                    bg_4.setImageResource(R.drawable.bg_4);
                    remaining.setVisibility(View.VISIBLE);
                } else if (currentSecond < time) {
                    ll_buy_success.setVisibility(View.GONE);
                    Rl_buy_failure.setVisibility(View.GONE);
                    rl_buy_success.setVisibility(View.GONE);
                    rl_buy_failure.setVisibility(View.GONE);
                    ll_counttime.setVisibility(View.VISIBLE);
                    ll_buy_e_come.setVisibility(View.VISIBLE);
                    rl_buy_ecome.setVisibility(View.VISIBLE);
                    bg_1.setImageResource(R.drawable.write_1);
                    bg_2.setImageResource(R.drawable.write_2);
                    bg_3.setImageResource(R.drawable.bg_3);
                    bg_4.setImageResource(R.drawable.write_4);
                    remaining.setVisibility(View.GONE);
                } else {
                    ll_buy_success.setVisibility(View.GONE);
                    Rl_buy_failure.setVisibility(View.VISIBLE);
                    rl_buy_success.setVisibility(View.GONE);
                    rl_buy_failure.setVisibility(View.VISIBLE);
                    ll_counttime.setVisibility(View.GONE);
                    ll_buy_e_come.setVisibility(View.GONE);
                    rl_buy_ecome.setVisibility(View.GONE);
                    remaining.setVisibility(View.GONE);
                }

                if (team == null || list == null) return;
                tv_order_name.setText(team.getTitle()+" "+team.getSummary());
                tv_team.setText(team.getMin_number() + "人社 ");
                tv_team_price.setText(" ￥" + team.getPin_price() +"元");
                Picasso.with(GroupDetailActivity.this).load(NetDataHelper.imgdomain + team.getImage())
                        .into(iv_order_good_failure);
                tv_order_name_failure.setText(team.getTitle() + " " + team.getSummary());
                tv_team_failure.setText(team.getMin_number() + "人社 ");
                tv_team_price_failure.setText(" ￥" + team.getPin_price() +"元");
                Picasso.with(GroupDetailActivity.this).load(NetDataHelper.imgdomain + team.getImage())
                        .into(iv_order_good_ecome);
                tv_order_name_ecome.setText(team.getTitle() + " " + team.getSummary());
                tv_team_ecome.setText(team.getMin_number() + "人社 ");
                tv_team_price_ecome.setText(" ￥" + team.getPin_price() +"元");
                Log.d("DDD", team.getReach_time());


                countDownView.setEndTime(time);
                for (int i = 0; i < 5; i++) {
                    if (i == list.size())
                        break;
                    Picasso.with(GroupDetailActivity.this).load(list.get(i).getAvatar()).placeholder(R.drawable.ic_profile_none).into(avatars.get(i));
                    avatars.get(i).setVisibility(View.VISIBLE);
                    View v = getLayoutInflater().inflate(R.layout.item_tz, null);
                    CircleImageView cv = (CircleImageView) v.findViewById(R.id.avatar);
                    TextView username = (TextView) v.findViewById(R.id.tv_username);
                    Picasso.with(GroupDetailActivity.this).load(list.get(i).getAvatar()).placeholder(R.drawable.ic_profile_none).into(cv);
                    if (i == 0) {
                        username.setText("社长 " + list.get(i).getUsername());
                    } else {
                        username.setText(list.get(i).getUsername());
                    }
                    if (i == 0) {
                        View v1 = new View(GroupDetailActivity.this);
                        v1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(GroupDetailActivity.this, 4)));
                        v1.setBackgroundResource(R.color.green_title);
                        View v2 = new View(GroupDetailActivity.this);
                        v2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(GroupDetailActivity.this, 6)));
                        v2.setBackgroundResource(R.color.green_title);
                        ll_avatar_vertical.addView(v1);
                        ll_avatar_vertical.addView(v);
                        ll_avatar_vertical.addView(v2);
                    } else if (i == 1) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 0);
                        v.setLayoutParams(params);
                        ll_avatar_vertical.addView(v);
                    } else {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, CommonUtils.dip2px(GroupDetailActivity.this, 8), 0, 0);
                        v.setLayoutParams(params);
                        ll_avatar_vertical.addView(v);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.bt_return:
                i = new Intent(GroupDetailActivity.this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.bt_creatteam:
                netDataHelper.getTeamDetail(Cookie.session, team.getId(), new NetCallBack<JSONObject>() {

                    @Override
                    public void onSuccess(JSONObject data) {
                        try {
                            List<SharedTeam> list = gson.fromJson(data.getJSONArray("pin_tz_list").toString(),
                                    new TypeToken<List<SharedTeam>>() {
                                    }.getType());
                            System.out.println("homefrag list size:" + list.size());
                            Team team = gson.fromJson(data.getJSONObject("team").toString(), Team.class);
                            Intent intent = new Intent(GroupDetailActivity.this, TeamDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("sharedTeams", (Serializable) list);
                            bundle.putSerializable("team", team);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String error) {

                    }
                });

                break;
            case R.id.ll_play_intro:
                Intent intent = new Intent(GroupDetailActivity.this, PuzzleActivity.class);
                startActivity(intent);
                break;
            case R.id.share:
                MobclickAgent.onEvent(GroupDetailActivity.this,"wx_share");
                SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE};
                mShareAction.setDisplayList(displaylist).setShareboardclickCallback(shareBoardlistener).open();
                break;
        }
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {


        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            if (share_media == null) {
                if (snsPlatform.mKeyword.equals("11")) {
                    Toast.makeText(GroupDetailActivity.this, "add button success", Toast.LENGTH_LONG).show();
                }

            } else {
                if((Integer.parseInt(team.getMin_number()) - list.size())==0){
                    UMImage image = new UMImage(GroupDetailActivity.this, NetDataHelper.imgdomain + team.getImage());
                    String url = "http://ecoop.szdod.com/account/group.php?id=" + order.getId();
                    new ShareAction(GroupDetailActivity.this).setPlatform(share_media).setCallback(umShareListener)
                            .withTitle("这里的水果,宝宝最爱吃,e起来吧!")
                            .withText("这里的水果宝宝最爱吃，e起来吧").withMedia(image).withTargetUrl(url)
                            .share();
                }else {
                    UMImage image = new UMImage(GroupDetailActivity.this, NetDataHelper.imgdomain + team.getImage());
                    String url = "http://ecoop.szdod.com/account/group.php?id=" + order.getId();
                    new ShareAction(GroupDetailActivity.this).setPlatform(share_media).setCallback(umShareListener)
                            .withTitle("这里的水果,宝宝最爱吃,e起来吧!")
                            .withText(team.getTitle() +team.getSummary()+ "," + team.getPin_price() + "元/件，还差" + (Integer.parseInt(team.getMin_number()) - list.size()) + "人，赶紧来合作").withMedia(image).withTargetUrl(url)
                            .share();

                }
            }
        }

    };
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(GroupDetailActivity.this, platform + " 分享成功啦",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(GroupDetailActivity.this, platform + " 分享失败啦",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(GroupDetailActivity.this, platform + " 分享取消了",
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(GroupDetailActivity.this, MainActivity.class);
        startActivity(i);
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
