package com.milai.ecoop.fragment;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.activity.AddressActivity;
import com.milai.ecoop.activity.CallmeActivity;
import com.milai.ecoop.activity.MainActivity;
import com.milai.ecoop.activity.MainIntoActivity;
import com.milai.ecoop.activity.MineSettingActivty;
import com.milai.ecoop.activity.NoticeActivity;
import com.milai.ecoop.activity.TicketActivity;
import com.milai.ecoop.activity.VoteActivity;
import com.milai.ecoop.adapter.SettingAdapter;
import com.milai.ecoop.bean.Ticket;
import com.milai.ecoop.bean.User;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.pay.PayHelper;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MineFrag extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private GridView gv_menu;
    private com.milai.ecoop.view.CircleImageView avatar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FragmentTransaction transaction;
    private FragmentManager manager;
    private FragClickListener mFraglistener;
    private Intent intent;
    private LinearLayout ll_myconpun;
    private TextView username, phonenumber, tv_mypoint, tv_myconpun, tv_account;
    private NetDataHelper netDataHelper;
    private boolean isRefreshing = false;
    private User userInfo;

    public static MineFrag createInstance(FragClickListener l) {
        MineFrag f = new MineFrag();
        f.mFraglistener = l;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("test", "page started");
        view = inflater.inflate(R.layout.frag_mine, null);
        Log.i("test", "view is create");
        try {
            User user = Cookie.user;
            gv_menu = (GridView) view.findViewById(R.id.gv_menu);
            gv_menu.setAdapter(new SettingAdapter(gv_menu, getActivity()));
            final Boolean flag = getArguments().getBoolean("flag", false);
            gv_menu.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            if (flag) {
                                ((MainIntoActivity) getActivity()).check(R.id.rb_order);
                            } else {
                                ((MainActivity) getActivity()).check(R.id.rb_order);
                            }
                            break;
                        case 1:
                            if (flag) {
                                ((MainIntoActivity) getActivity()).check(R.id.rb_group_purchase);
                            } else {
                                ((MainActivity) getActivity()).check(R.id.rb_group_purchase);
                            }
                            break;
                        case 2:
                            intent = new Intent(getActivity(), VoteActivity.class);
                            startActivity(intent);
                            break;
                        case 3:
                            intent = new Intent(getActivity(), AddressActivity.class);
                            startActivity(intent);
                            break;
                        case 4:
                            intent = new Intent(getActivity(), CallmeActivity.class);
                            startActivity(intent);
                            break;
                        case 5:
                            intent = new Intent(getActivity(), MineSettingActivty.class);
                            startActivity(intent);
                            break;
                        case 6:
                            intent = new Intent(getActivity(), NoticeActivity.class);
                            startActivity(intent);
                            break;
                    }

                }
            });
            avatar = (com.milai.ecoop.view.CircleImageView) view.findViewById(R.id.iv_icon);
//        if (user.getAvatar() != null) {
//            Picasso.with(getActivity()).load(user.getAvatar()).error(R.drawable.ic_profile_none).into(avatar);
//        }

            avatar.setOnClickListener(this);
            ll_myconpun = (LinearLayout) view.findViewById(R.id.ll_myconpun);
            ll_myconpun.setOnClickListener(this);
            username = (TextView) view.findViewById(R.id.username);
//        if (Cookie.user.getUsername() != null) {
//            username.setText(Cookie.user.getUsername());
//        }
            phonenumber = (TextView) view.findViewById(R.id.phonenumber);
            netDataHelper = NetDataHelper.getInstance(getActivity());

            userInfo = Cookie.user;
//        Log.i("test", "userInfo:" + userInfo.toString());
            if (userInfo != null) {
                if (TextUtils.isEmpty(Cookie.user.getMobile())) {
                    phonenumber.setText("未绑定手机");
                } else {
                    phonenumber.setText(Cookie.user.getMobile());
                }
            }

            tv_mypoint = (TextView) view.findViewById(R.id.tv_mypoint);
            tv_myconpun = (TextView) view.findViewById(R.id.tv_myconpun);
            tv_account = (TextView) view.findViewById(R.id.tv_account);

            getTickets(1);
            manager = getFragmentManager();
            transaction = manager.beginTransaction();
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
            updateLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;

    }

    private void updateLogin() {
        try {
            if (Cookie.session != null) {
                Log.i("test", "session.sid:" + Cookie.session.getSid());
                Log.i("test", "session.uid:" + Cookie.session.getUid());
                netDataHelper.updatelogin(Cookie.session, new NetCallBack<User>() {
                    @Override
                    public void onSuccess(User data) {

                        swipeRefreshLayout.setRefreshing(false);
                        username.setText(data.getUsername());
                        if (data.getAvatar() != null) {
                            Picasso.with(getActivity()).load(data.getAvatar()).placeholder(R.drawable.ic_profile_none).into(avatar);
                        } else {
                            Picasso.with(getActivity()).load(data.getAvatar()).placeholder(R.drawable.ic_profile_none).into(avatar);
                        }
                        if (TextUtils.isEmpty(Cookie.user.getMobile())) {
                            phonenumber.setText("未绑定手机");
                        } else {
                            phonenumber.setText(data.getMobile());
                        }
                        tv_mypoint.setText(data.getScore());
                        tv_account.setText(data.getMoney());
                        isRefreshing = false;
                    }

                    @Override
                    public void onError(String error) {

                        swipeRefreshLayout.setRefreshing(false);
                        isRefreshing = false;
                    }
                });
            } else {
                phonenumber.setText("未绑定手机");
                swipeRefreshLayout.setRefreshing(false);
                isRefreshing = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isRefreshing() {
        return swipeRefreshLayout.isRefreshing();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Cookie.session != null && Cookie.user != null) {
//            if (TextUtils.isEmpty(Cookie.user.getMobile())) {
            if (Cookie.user.mobile != null && Cookie.user.mobile.length() > 0) {
                phonenumber.setText("未绑定手机");
            } else {
                phonenumber.setText(Cookie.user.getMobile());
            }

            updateLogin();
            getTickets(1);
        }
        MobclickAgent.onPageStart("个人信息界面"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        netDataHelper = NetDataHelper.getInstance(getActivity());
        if (Cookie.session != null ) {
            netDataHelper.updatelogin(Cookie.session, new NetCallBack<User>() {
                @Override
                public void onSuccess(User data) {
                    phonenumber.setText(data.getMobile());
                }

                @Override
                public void onError(String error) {

                }
            });
        }
    }

    private void getTickets(int page) {
        PayHelper.getInstance(getActivity()).getTickets(Cookie.session, page, 10, 1, new NetCallBack<List<Ticket>>() {

            @Override
            public void onSuccess(List<Ticket> data) {
                tv_myconpun.setText("" + data.size());
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.iv_icon:
                i = new Intent(getActivity(), MineSettingActivty.class);
                startActivity(i);
                break;
            case R.id.ll_myconpun:
                i = new Intent(getActivity(), TicketActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        if (Cookie.user != null) {
            updateLogin();
            getTickets(1);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            isRefreshing=false;
        }
    }


    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("个人信息界面");
    }
}
