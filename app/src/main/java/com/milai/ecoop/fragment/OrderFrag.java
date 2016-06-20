package com.milai.ecoop.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.activity.ExpressActivity;
import com.milai.ecoop.activity.MainActivity;
import com.milai.ecoop.activity.OrderDetailActivity;
import com.milai.ecoop.adapter.OrderAdapter;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.utils.CommonUtils;
import com.milai.ecoop.view.XListView.XListView;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class OrderFrag extends Fragment implements XListView.IXListViewListener, OrderAdapter.OnPayListener, OrderAdapter.OnDelOrderListener,OrderAdapter.OnExpressListener {
    private View view;
    private XListView lv_order;
    private NetDataHelper netDataHelper;
    private List<Order> data;
    private Map<String, Team> map;
    private OrderAdapter adapter;
    private Gson gson;
    private boolean isLastPage = false;
    private int page = 1;
    private int s = 1;
    private LinearLayout ll_empty, ll_no_network;
    private ImageView iv_loading;
    private Button bt_return_main;
    private final static int STATUS_NO_NETWORK = 101;
    private final static int STATUS_EMPTY_CONTENT = 102;
    private final static int STATUS_LOADING = 103;
    private final static int STATUS_NORMAL = 104;

    public static OrderFrag createInstance(FragClickListener l) {
        OrderFrag f = new OrderFrag();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_order, null);
        ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);
        ll_no_network = (LinearLayout) view.findViewById(R.id.ll_no_network);
        iv_loading = (ImageView) view.findViewById(R.id.iv_loading);
        bt_return_main = (Button) view.findViewById(R.id.bt_return_main);
        bt_return_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_return_main:
                        ((MainActivity) getActivity()).check(R.id.rb_home);
                        break;
                }
            }
        });
        lv_order = (XListView) view.findViewById(R.id.lv_order);
        netDataHelper = NetDataHelper.getInstance(getActivity());
        gson = new Gson();
        map = new HashMap<>();
        data = new ArrayList<>();
        adapter = new OrderAdapter(getActivity(), data, map);
        adapter.setOnPayListener(this);
        adapter.setOnDelOrderListener(this);
        adapter.setOnExpressListener(this);
        lv_order.setAdapter(adapter);
        lv_order.setXListViewListener(this);
        if (!CommonUtils.isConn(getActivity())) {
            showContent(STATUS_NO_NETWORK);
        } else {
            showContent(STATUS_LOADING);
        }
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_order);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showContent(STATUS_LOADING);
                switch (checkedId) {
                    case R.id.rb_allorder:
                        s = 1;
                        break;
                    case R.id.rb_nopay:
                        s = 2;
                        break;
                    case R.id.rb_onway:
                        s = 3;
                        break;
                }
                data.clear();
                map.clear();
                adapter.notifyDataSetChanged();
                page = 1;
                Log.d("DD", "AAAAA");
                getOrders(page);
            }
        });
        return view;
    }

    private void getOrders(int p) {
        if (Cookie.session == null) {
            return;
        }
        String state = "index";
        switch (s) {
            case 1:
                state = "index";
                break;
            case 2:
                state = "unpay";
                break;
            case 3:
                state = "pay";
                break;
        }

        final String finalState = state;
        netDataHelper.getOrders(Cookie.session, p, 10, state, new NetCallBack<JSONObject>() {

            @Override
            public void onSuccess(JSONObject data) {

                try {
                    if (page == 1) {
                        OrderFrag.this.data.clear();
                        OrderFrag.this.map.clear();
                        isLastPage = false;
                        lv_order.setPullLoadEnable(true);
                        lv_order.setRefreshTime("刚刚");
                        lv_order.stopRefresh();
                    } else {
                        lv_order.stopLoadMore();
                    }
                    List<Order> list = gson.fromJson(data.getJSONArray("orders").toString(),
                            new TypeToken<List<Order>>() {
                            }.getType());
                    if (TextUtils.equals(finalState, "unpay")) {
                        Iterator<Order> iterator=list.iterator();
                        while(iterator.hasNext()){
                            Order o=iterator.next();
                            Log.d("zcz","unpay"+o);

                                OrderFrag.this.data.add(o);

                        }
                    } else if (TextUtils.equals(finalState, "pay")) {
                        Iterator<Order> iterator=list.iterator();
                        while(iterator.hasNext()){
                            Order o=iterator.next();
                            Log.d("zcz","pay"+o);

                                OrderFrag.this.data.add(o);

                        }
                    } else {
                        OrderFrag.this.data.addAll(list);
                    }
                    List<Team> teamList = gson.fromJson(data.getJSONArray("teams").toString(),
                            new TypeToken<List<Team>>() {
                            }.getType());
                    for (Team t : teamList) {
                        OrderFrag.this.map.put(t.getId(), t);
                    }
                    if (list.size() == 0) {
                        isLastPage = true;
                        lv_order.setPullLoadEnable(false);
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
        getOrders(page);
    }

    @Override
    public void onLoadMore() {
        if (!isLastPage) {
            ++page;
            getOrders(page);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        getOrders(page);
        MobclickAgent.onPageStart("商品订单界面"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    @Override
    public void onClick(int position) {
        Intent i = new Intent(getActivity(), OrderDetailActivity.class);
        Order mInfo = data.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mInfo", mInfo);
        bundle.putSerializable("team", map.get(mInfo.getTeam_id()));
        i.putExtras(bundle);
        startActivity(i);
    }

    @Override
    public void onDel(final Order o, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                netDataHelper.deleteOrder(Cookie.session, o.getId(), new NetCallBack<String>() {
                    @Override
                    public void onSuccess(String data) {
                        onRefresh();
                    }

                    @Override
                    public void onError(String error) {
                        onRefresh();
                    }
                });
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

    private void showContent(int status) {
        switch (status) {
            case STATUS_NO_NETWORK:
                ll_no_network.setVisibility(View.VISIBLE);
                ll_empty.setVisibility(View.GONE);
                lv_order.setVisibility(View.GONE);
                iv_loading.setVisibility(View.GONE);
                break;
            case STATUS_LOADING:
                ll_no_network.setVisibility(View.GONE);
                ll_empty.setVisibility(View.GONE);
                lv_order.setVisibility(View.GONE);
                iv_loading.setVisibility(View.VISIBLE);
                break;
            case STATUS_EMPTY_CONTENT:
                ll_no_network.setVisibility(View.GONE);
                ll_empty.setVisibility(View.VISIBLE);
                lv_order.setVisibility(View.GONE);
                iv_loading.setVisibility(View.GONE);
                break;
            case STATUS_NORMAL:
                ll_no_network.setVisibility(View.GONE);
                ll_empty.setVisibility(View.GONE);
                lv_order.setVisibility(View.VISIBLE);
                iv_loading.setVisibility(View.GONE);
                break;
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("商品订单界面");
    }

    @Override
    public void onExp(Order o) {
        Intent i=new Intent(getActivity(), ExpressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("morder",o);

        i.putExtras(bundle);
        startActivity(i);
    }
}
