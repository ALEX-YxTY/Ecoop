package com.milai.ecoop.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;


import com.bigkoo.convenientbanner.ConvenientBanner;

import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.milai.ecoop.Cookie;
import com.milai.ecoop.MyApplication;
import com.milai.ecoop.R;
import com.milai.ecoop.bean.Address;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.SharedTeam;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.bean.Ticket;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetCallBack2;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.fragment.FragClickListener;
import com.milai.ecoop.fragment.childrenfragment.GraphicdetailsFrag;
import com.milai.ecoop.fragment.childrenfragment.JointeamFrag;
import com.milai.ecoop.fragment.childrenfragment.PinintroFrag;
import com.milai.ecoop.pay.PayHelper;
import com.milai.ecoop.view.ImageHolderView;
import com.milai.ecoop.view.SlideShowView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import static com.umeng.socialize.bean.SHARE_MEDIA.*;

public class TeamDetailActivity extends FragmentActivity implements View.OnClickListener, JointeamFrag.OnItemClickListener {
    private FragmentManager fragmentManager;
    //private SlideShowView sv_team;
    private LinearLayout ll_teambuy, ll_onlybuy;
    private TextView tv_goods_title, tv_team_price, tv_team_way, tv_chearbuy_price, tv_group, tv_onlybuy_price,
            tv_market_price;
    private Button bt_return;
    private Team team;
    private List<SharedTeam> list;
    private ProgressDialog loadingDialog;
    private Intent intent;
    private PayHelper payHelper;
    private NetDataHelper netDataHelper;
    private ImageButton share;
    private UMShareAPI mShareAPI = null;
    private ShareAction mShareAction;
    private ConvenientBanner banner;
    private List<Address> addresserlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_buy);
        banner= (ConvenientBanner) findViewById(R.id.convenientBanner);
        mShareAPI = MyApplication.mShareAPI;
        loadingDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        loadingDialog.setMessage("正在加载，请稍候");
        loadingDialog.setCancelable(false);
        payHelper = PayHelper.getInstance(this);
        netDataHelper = NetDataHelper.getInstance(this);
        team = (Team) getIntent().getExtras().getSerializable("team");
        list = (List<SharedTeam>) getIntent().getExtras().getSerializable("sharedTeams");
        String[] teamUrls={team.getImage(),team.getImage1(),team.getImage2()};
        List<String> urls=new ArrayList<>();
        for(int i=0;i<3;i++){
            if(teamUrls[i]!=null){
                urls.add(NetDataHelper.imgdomain+teamUrls[i]);
                Log.i("zcz",NetDataHelper.imgdomain+teamUrls[i]);
            }
        }
        for(int i=0;i<urls.size();i++){
            Log.i("zcz",urls.get(i));
        }
        String[] imageUrls = {NetDataHelper.imgdomain + team.getImage(),
                NetDataHelper.imgdomain + team.getImage1(),
                NetDataHelper.imgdomain + team.getImage2()};




        /*List<String> mulrs=new ArrayList<>();
        //List<String> imageUrls=new ArrayList<>();
        for(int i=0;i<urls.length-1;i++){

                mulrs.add(urls[i]);

        }
        for(int i=0;i<mulrs.size();i++){
            imageUrls.add(i,NetDataHelper.imgdomain+mulrs.get(i));
        }*/
        fragmentManager = getSupportFragmentManager();
        //sv_team = (SlideShowView) findViewById(R.id.sv_team);
        //sv_team.setView(imageUrls);
        if(urls.size()>1){
            banner.setPages(new CBViewHolderCreator<ImageHolderView>() {

                public ImageHolderView createHolder() {
                    return new ImageHolderView();
                }
            },urls).setPageIndicator(new int[]{R.drawable.dot_blur, R.drawable.dot_focus});
        }else{
            banner.setPages(new CBViewHolderCreator<ImageHolderView>() {

                public ImageHolderView createHolder() {
                    return new ImageHolderView();
                }
            },urls);
        }

        ll_teambuy = (LinearLayout) findViewById(R.id.ll_teambuy);
        ll_onlybuy = (LinearLayout) findViewById(R.id.ll_onlybuy);
        ll_teambuy.setOnClickListener(this);
        ll_onlybuy.setOnClickListener(this);

        bt_return = (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
        tv_goods_title = (TextView) findViewById(R.id.tv_goods_title);
        tv_team_price = (TextView) findViewById(R.id.tv_team_price);
        tv_team_way = (TextView) findViewById(R.id.tv_team_way);
        tv_chearbuy_price = (TextView) findViewById(R.id.tv_chearbuy_price);
        tv_group = (TextView) findViewById(R.id.tv_group);
        tv_onlybuy_price = (TextView) findViewById(R.id.tv_onlybuy_price);
        tv_market_price = (TextView) findViewById(R.id.tv_market_price);

        tv_goods_title.setText(team.getTitle());
        tv_team_price.setText("合作购买:￥" + team.getPin_price());
        tv_team_way.setText(team.getMin_number() + "人成社");
        tv_chearbuy_price.setText("￥" + team.getPin_price() + "/件");
        tv_group.setText(team.getMin_number() + "人社");
        tv_onlybuy_price.setText("￥" + team.getTeam_price() + "/件");
        tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tv_market_price.setText("市场价:￥" + team.getMarket_price());

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_team);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == findViewById(R.id.rb_details).getId()) {
                    showFrag("details");
                } else if (checkedId == findViewById(R.id.rb_jointeam).getId()) {
                    showFrag("jointeam");
                } else if(checkedId == findViewById(R.id.rb_pinintro).getId()){
                    showFrag("pinintro");
                }
            }
        });
        radioGroup.check(findViewById(R.id.rb_details).getId());
        share = (ImageButton) findViewById(R.id.share);
        share.setOnClickListener(this);
    }


    public Team getTeam() {
        return team;
    }

    private void showFrag(String frag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        if (frag.equals("details")) {
            fragment = GraphicdetailsFrag.createInstance(fragClickListener);
        } else if (frag.equals("jointeam")) {

            fragment = JointeamFrag.createInstance(fragClickListener);
            ((JointeamFrag) fragment).setOnItemClickListener(this);
        }else if(frag.equals("pinintro")){
            fragment = PinintroFrag.createInstance(fragClickListener);
        }
        transaction.replace(R.id.fl_team, fragment);
        transaction.commit();
    }

    private FragClickListener fragClickListener = new FragClickListener() {

        @Override
        public void onViewSelected(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {

            }
        }

        @Override
        public void enableMenuDrag(boolean b) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ll_onlybuy:
                if (Cookie.session != null) {
                    loadingDialog.show();
                    teamBuy(false, "");
                } else {
                    showDialog();
                }
                break;
            case R.id.ll_teambuy:
                if (Cookie.session != null) {
                    loadingDialog.show();
                    teamBuy(true, "");
                } else {
                    showDialog();
                }
                break;
            case R.id.bt_return:
                finish();
                break;
            case R.id.share:
                MobclickAgent.onEvent(TeamDetailActivity.this,"wx_share");
                SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                        {
                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE
                        };
                mShareAction = new ShareAction(this);
                mShareAction.setDisplayList(displaylist)
                        .setShareboardclickCallback(shareBoardlistener)
                        .open();

                break;
        }

    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {


        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            if (share_media == null) {
                if (snsPlatform.mKeyword.equals("11")) {
                    Toast.makeText(TeamDetailActivity.this, "add button success", Toast.LENGTH_LONG).show();
                }

            } else {
                UMImage image = new UMImage(TeamDetailActivity.this,NetDataHelper.imgdomain + team.getImage());
                String url="http://ecoop.szdod.com/team.php?id="+team.getId();
                new ShareAction(TeamDetailActivity.this).setPlatform(share_media).setCallback(umShareListener)
                        .withTitle("e起来合作社，我们只做有品质的水果")
                        .withText("这个商品很超值哦，加入我吧" + team.getTitle()).withMedia(image).withTargetUrl(url)
                        .share();

            }
        }

    };
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(TeamDetailActivity.this, platform + " 分享成功啦",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(TeamDetailActivity.this, platform + " 分享失败啦",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(TeamDetailActivity.this, platform + " 分享取消了",
                    Toast.LENGTH_SHORT).show();
        }
    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new Builder(TeamDetailActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage("请先登录");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent i = new Intent(TeamDetailActivity.this, SigninActivity.class);
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

    public List<SharedTeam> getSharedTeams() {
        return list;
    }

    private void teamBuy(final boolean isPin, final String pin_id) {
        netDataHelper.getAddress(Cookie.session, new NetCallBack<List<Address>>() {
            @Override
            public void onSuccess(List<Address> data) {
                if (data.size() == 0) {
                    Toast.makeText(TeamDetailActivity.this, "地址还未设置，先去设置地址吧", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    Intent i = new Intent(TeamDetailActivity.this, AddressActivity.class);
                    startActivity(i);

                } else {
                    payHelper.buyTeam(Cookie.session, team.getId(), isPin, pin_id, new NetCallBack<String>() {

                        @Override
                        public void onSuccess(String data) {
                            netDataHelper.checkOrder(Cookie.session, data, new NetCallBack2<List<Ticket>, Order>() {
                                @Override
                                public void onSuccess(List<Ticket> data1, Order data2) {
                                    loadingDialog.dismiss();
                                    Log.i("TeamDetail/ticket size", data1.size() + "");
                                    Intent i = new Intent(TeamDetailActivity.this, PayActivity.class);
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
                                    Toast.makeText(TeamDetailActivity.this, "团购失败,请稍后再试", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onError(String error) {
                            loadingDialog.dismiss();
                            Toast.makeText(TeamDetailActivity.this, "团购失败,请稍后再试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {

            }
        });

    }

    @Override
    public void onItemClick(final SharedTeam st) {
        if (Cookie.session != null) {
            loadingDialog.show();
;            netDataHelper.getAddress(Cookie.session, new NetCallBack<List<Address>>() {
                @Override
                public void onSuccess(List<Address> data) {
                    if (data.size() == 0) {
                        Toast.makeText(TeamDetailActivity.this, "地址还未设置，先去设置地址吧", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                        Intent i = new Intent(TeamDetailActivity.this, AddressActivity.class);
                        startActivity(i);

                    }else{
                        loadingDialog.dismiss();
                        Intent i = new Intent(TeamDetailActivity.this,GroupBuyActivity.class);
                        i.putExtra("id", st.getOrder_id());
                        i.putExtra("pin_id",st.getPin_id());
                        startActivity(i);
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        } else {
            showDialog();
        }
    }
    public void onResume() {
        super.onResume();
        banner.startTurning(6000);
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

