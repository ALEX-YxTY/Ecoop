package com.milai.ecoop.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.milai.ecoop.Cookie;
import com.milai.ecoop.MyApplication;
import com.milai.ecoop.R;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.bean.Ticket;
import com.milai.ecoop.bean.User;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetCallBack2;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.pay.PayHelper;
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

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/8 0008.
 */
public class GroupBuyActivity extends Activity  implements View.OnClickListener {
    private Button btn_return,bt_creatteam;
    private ImageButton share;
    private ShareAction mShareAction;
    private ImageView bg_1, bg_2, bg_3, bg_4;
    private CircleImageView avatar1, avatar2, avatar3, avatar4, avatar5;
    private ProgressDialog progressDialog,loadingDialog;
    private List<CircleImageView> avatars;
    private String id,end_time,url,pin_id;
    private UMShareAPI mShareAPI = null;
    private Team team;
    private List<User> list;
    private NetDataHelper netDataHelper;
    private Gson gson;
    private ImageView iv_order_good;
    private TextView tv_order_name,tv_team,tv_team_price;
    private LinearLayout ll_avatar_vertical,ll_play_intro;
    private Order order;
    private PayHelper payHelper;
    private CountDownView countDownView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_buy);
        mShareAPI = MyApplication.mShareAPI;
        mShareAction = new ShareAction(this);
        netDataHelper = NetDataHelper.getInstance(this);
        gson = new Gson();
        bt_creatteam= (Button) findViewById(R.id.bt_creatteam);
        bt_creatteam.setOnClickListener(this);
        loadingDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        loadingDialog.setMessage("正在加载，请稍候");
        loadingDialog.setCancelable(false);
        iv_order_good = (ImageView) findViewById(R.id.iv_order_good);
        tv_order_name = (TextView) findViewById(R.id.tv_order_name);
        tv_team = (TextView) findViewById(R.id.tv_team);
        tv_team_price = (TextView) findViewById(R.id.tv_team_price);
        ll_avatar_vertical = (LinearLayout) findViewById(R.id.ll_avatar_vertical);
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("正在加载,请稍候");
        progressDialog.setCancelable(false);
        btn_return = (Button) findViewById(R.id.bt_return);
        btn_return.setOnClickListener(this);

        bg_1 = (ImageView) findViewById(R.id.bg_1);
        bg_2 = (ImageView) findViewById(R.id.bg_2);
        bg_3 = (ImageView) findViewById(R.id.bg_3);
        bg_4 = (ImageView) findViewById(R.id.bg_4);
        bg_1.setImageResource(R.drawable.write_1);
        bg_2.setImageResource(R.drawable.bg_2);
        bg_3.setImageResource(R.drawable.write_3);
        bg_4.setImageResource(R.drawable.write_4);
        ll_play_intro = (LinearLayout) findViewById(R.id.ll_play_intro);
        ll_play_intro.setOnClickListener(this);
        share = (ImageButton) findViewById(R.id.share);
        share.setOnClickListener(this);
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
        id=getIntent().getStringExtra("id");
        url = "http://ecoop.szdod.com/account/group.php?id=" + id;
        pin_id=getIntent().getStringExtra("pin_id");
        payHelper = PayHelper.getInstance(this);
        countDownView = (CountDownView) findViewById(R.id.count_down_view);
        getData(id);

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
                    Picasso.with(GroupBuyActivity.this).load(NetDataHelper.imgdomain + team.getImage())
                            .into(iv_order_good);

                    list = gson.fromJson(data.getJSONArray("buy_user").toString(),
                            new TypeToken<List<User>>() {
                            }.getType());

                    end_time = data.getString("pin_endtime");


                } catch (Exception e) {
                    e.printStackTrace();
                }
                long time = Long.parseLong(end_time);
                countDownView.setEndTime(time);
                if (team == null || list == null) return;
                tv_order_name.setText(team.getTitle()+" "+team.getSummary());
                tv_team.setText(team.getMin_number() + "人社 ");
                tv_team_price.setText(" ￥" + team.getPin_price() +"元");
                for (int i = 0; i < 5; i++) {
                    if (i == list.size())
                        break;
                    Picasso.with(GroupBuyActivity.this).load(list.get(i).getAvatar()).placeholder(R.drawable.ic_profile_none).into(avatars.get(i));
                    avatars.get(i).setVisibility(View.VISIBLE);
                    View v = getLayoutInflater().inflate(R.layout.item_tz, null);
                    CircleImageView cv = (CircleImageView) v.findViewById(R.id.avatar);
                    TextView username = (TextView) v.findViewById(R.id.tv_username);
                    Picasso.with(GroupBuyActivity.this).load(list.get(i).getAvatar()).placeholder(R.drawable.ic_profile_none).into(cv);
                    if (i == 0) {
                        username.setText("团长 " + list.get(i).getUsername());
                    } else {
                        username.setText(list.get(i).getUsername());
                    }
                    if (i == 0) {
                        View v1 = new View(GroupBuyActivity.this);
                        v1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(GroupBuyActivity.this, 4)));
                        v1.setBackgroundResource(R.color.green_title);
                        View v2 = new View(GroupBuyActivity.this);
                        v2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(GroupBuyActivity.this, 6)));
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
                        params.setMargins(0, CommonUtils.dip2px(GroupBuyActivity.this, 8), 0, 0);
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
    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {


        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            if (share_media == null) {
                if (snsPlatform.mKeyword.equals("11")) {
                    Toast.makeText(GroupBuyActivity.this, "add button success", Toast.LENGTH_LONG).show();
                }

            } else {
                if((Integer.parseInt(team.getMin_number()) - list.size())==0){
                    UMImage image = new UMImage(GroupBuyActivity.this, NetDataHelper.imgdomain + team.getImage());

                    new ShareAction(GroupBuyActivity.this).setPlatform(share_media).setCallback(umShareListener)
                            .withTitle("这里的水果,宝宝最爱吃,e起来吧!")
                            .withText("这里的水果宝宝最爱吃，e起来吧!").withMedia(image).withTargetUrl(url)
                            .share();
                }else {
                    UMImage image = new UMImage(GroupBuyActivity.this, NetDataHelper.imgdomain + team.getImage());

                    new ShareAction(GroupBuyActivity.this).setPlatform(share_media).setCallback(umShareListener)
                            .withTitle("这里的水果,宝宝最爱吃,e起来吧!")
                            .withText(team.getTitle() +team.getSummary()+ "," + team.getPin_price() + "元/件,还差" + (Integer.parseInt(team.getMin_number()) - list.size()) + "人，赶紧来合作").withMedia(image).withTargetUrl(url)
                            .share();

                }
            }
        }

    };
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(GroupBuyActivity.this, platform + " 分享成功啦",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(GroupBuyActivity.this, platform + " 分享失败啦",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(GroupBuyActivity.this, platform + " 分享取消了",
                    Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.bt_return:
                finish();
                break;
            case R.id.share:
                MobclickAgent.onEvent(GroupBuyActivity.this, "wx_share");
                SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE};
                mShareAction.setDisplayList(displaylist).setShareboardclickCallback(shareBoardlistener).open();
                break;
            case R.id.ll_play_intro:
                Intent intent = new Intent(GroupBuyActivity.this, PuzzleActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_creatteam:
                if (Cookie.session != null) {
                    loadingDialog.show();
                    teamBuy(true, pin_id);
                } else {
                    showDialog();
                }
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
    private void teamBuy(final boolean isPin, final String pin_id) {
        payHelper.buyTeam(Cookie.session, team.getId(), isPin, pin_id, new NetCallBack<String>() {

            @Override
            public void onSuccess(String data) {
                netDataHelper.checkOrder(Cookie.session, data, new NetCallBack2<List<Ticket>, Order>() {
                    @Override
                    public void onSuccess(List<Ticket> data1, Order data2) {
                        loadingDialog.dismiss();
                        Log.d("TeamDetail/ticket size", data1.size() + "");
                        Intent i = new Intent(GroupBuyActivity.this, PayActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("tickets", (Serializable) data1);
                        bundle.putSerializable("team", team);
                        bundle.putSerializable("order", data2);
                        i.putExtra("pin_id", pin_id);
                        i.putExtras(bundle);
                        startActivity(i);
                    }

                    @Override
                    public void onError(String error) {
                        loadingDialog.dismiss();
                        Toast.makeText(GroupBuyActivity.this, "团购失败,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onError(String error) {
                loadingDialog.dismiss();
                Toast.makeText(GroupBuyActivity.this, "你已经参加过这个团了", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showDialog() {
        AlertDialog.Builder builder = new Builder(GroupBuyActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage("请先登录");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent i = new Intent(GroupBuyActivity.this, SigninActivity.class);
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
}
