package com.milai.ecoop.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;

import com.milai.ecoop.activity.MainActivity;
import com.milai.ecoop.activity.GroupDetailActivity;
import com.milai.ecoop.activity.PayActivity;
import com.milai.ecoop.adapter.GroupAdapter;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.bean.Ticket;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetCallBack2;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.utils.CommonUtils;
import com.milai.ecoop.view.XListView.XListView;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MyGroupFrag extends Fragment implements GroupAdapter.OnItemClickListener, XListView.IXListViewListener {
    private View view;
    private FragClickListener mFraglistener;
    private XListView lv_group_purchase;
    private GroupAdapter adapter;
    private List<Order> myGroups;
    private Map<String, Team> map;
    private Gson gson;
    private NetDataHelper netDataHelper;
    private ProgressDialog progressDialog;
    private int page = 1;
    private boolean isLastPage = false;
    private LinearLayout ll_empty, ll_no_network;
    private ImageView iv_loading;
    private Button bt_return_main;
    public final static int REQUEST_GROUP_DETAIL = 201;
    private final static int STATUS_NO_NETWORK = 101;
    private final static int STATUS_EMPTY_CONTENT = 102;
    private final static int STATUS_LOADING = 103;
    private final static int STATUS_NORMAL = 104;

    public static MyGroupFrag createInstance(FragClickListener l) {
        MyGroupFrag f = new MyGroupFrag();
        f.mFraglistener = l;
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("正在加载,请稍候");
        progressDialog.setCancelable(false);
        view = inflater.inflate(R.layout.frag_group_purchase, null);
        ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);
        ll_no_network = (LinearLayout) view.findViewById(R.id.ll_no_network);
        iv_loading = (ImageView) view.findViewById(R.id.iv_loading);
        bt_return_main = (Button) view.findViewById(R.id.bt_return_main);
        bt_return_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_return_main:
                        // MainActivity ————false  MainIntoActivity——true
                        final Boolean flag = getArguments().getBoolean("flag", false);
                        Log.i("test", "flag:" + flag);
                        if (!flag) {
                            ((MainActivity) getActivity()).check(R.id.rb_home);
                        } else {
                            getActivity().finish();
                        }
                        break;
                }
            }
        });
        gson = new Gson();
        netDataHelper = NetDataHelper.getInstance(getActivity());
        lv_group_purchase = (XListView) view.findViewById(R.id.lv_group_purchase);
        myGroups = new ArrayList<>();
        map = new HashMap<>();
        adapter = new GroupAdapter(getActivity(), myGroups, map);

        adapter.setOnItemClickListener(this);
        lv_group_purchase.setAdapter(adapter);
        lv_group_purchase.setXListViewListener(this);
        lv_group_purchase.setPullLoadEnable(false);
        if (!CommonUtils.isConn(getActivity())) {
            showContent(STATUS_NO_NETWORK);
        } else {
            showContent(STATUS_LOADING);
        }
        return view;
    }

    public void getData(int p) {
        if (Cookie.session == null)
            return;
        netDataHelper.getMyGroups(Cookie.session, p, 10, new NetCallBack<JSONObject>() {

            @Override
            public void onSuccess(JSONObject data) {
                try {

                    if (page == 1) {
                        MyGroupFrag.this.myGroups.clear();
                        MyGroupFrag.this.map.clear();
                        lv_group_purchase.stopRefresh();
                        lv_group_purchase.setRefreshTime("刚刚");
                        lv_group_purchase.setPullLoadEnable(true);
                        isLastPage = false;
                    } else {
                        lv_group_purchase.stopLoadMore();

                    }
                    List<Order> orderList = gson.fromJson(data.getJSONArray("orders").toString(),
                            new TypeToken<List<Order>>() {
                            }.getType());

                    MyGroupFrag.this.myGroups.addAll(orderList);

                    List<Team> teamList = gson.fromJson(data.getJSONArray("teams").toString(),
                            new TypeToken<List<Team>>() {
                            }.getType());
                    for (Team t : teamList) {
                        MyGroupFrag.this.map.put(t.getId(), t);
                        Log.d("teams", t.getId());
                    }
                    Log.d("zcz", orderList.size() + ":" + teamList.size() + ":" + page);
                    if (orderList.size() == 0) {
                        isLastPage = true;
                        lv_group_purchase.setPullLoadEnable(false);
                        if (page == 1) {
                            showContent(STATUS_EMPTY_CONTENT);
                        } else {
                            showContent(STATUS_NORMAL);
                        }
                    } else {
                        showContent(STATUS_NORMAL);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onRefresh() {
        page = 1;
        getData(page);
    }

    @Override
    public void onLoadMore() {
        if (!isLastPage) {
            ++page;
            getData(page);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        getData(page);
        MobclickAgent.onPageStart("我的团界面"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    @Override
    public void onClickOrderDetail(Order o) {
        progressDialog.show();
        netDataHelper.checkOrder(Cookie.session, o.getId(), new NetCallBack2<List<Ticket>, Order>() {
            @Override
            public void onSuccess(List<Ticket> data1, Order data2) {
                progressDialog.dismiss();
                Intent intent = new Intent(getActivity(), PayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("tickets", (Serializable) data1);
                bundle.putSerializable("order", data2);

                bundle.putSerializable("team", map.get(data2.getTeam_id()));
                intent.putExtras(bundle);
                intent.putExtra("allowEdit", false);
                getActivity().startActivity(intent);
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClickGroupDetail(Order o) {
        Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
        intent.putExtra("id", o.getId());
        intent.putExtra("state", o.getPin_tip());
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", o);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, REQUEST_GROUP_DETAIL);
    }

    private void showContent(int status) {
        switch (status) {
            case STATUS_NO_NETWORK:
                ll_no_network.setVisibility(View.VISIBLE);
                ll_empty.setVisibility(View.GONE);
                lv_group_purchase.setVisibility(View.GONE);
                iv_loading.setVisibility(View.GONE);
                break;
            case STATUS_LOADING:
                ll_no_network.setVisibility(View.GONE);
                ll_empty.setVisibility(View.GONE);
                lv_group_purchase.setVisibility(View.GONE);
                iv_loading.setVisibility(View.VISIBLE);
                break;
            case STATUS_EMPTY_CONTENT:
                ll_no_network.setVisibility(View.GONE);
                ll_empty.setVisibility(View.VISIBLE);
                lv_group_purchase.setVisibility(View.GONE);
                iv_loading.setVisibility(View.GONE);
                break;
            case STATUS_NORMAL:
                ll_no_network.setVisibility(View.GONE);
                ll_empty.setVisibility(View.GONE);
                lv_group_purchase.setVisibility(View.VISIBLE);
                iv_loading.setVisibility(View.GONE);
                break;
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("我的团界面");
    }
}