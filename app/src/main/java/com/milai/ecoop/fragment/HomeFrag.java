package com.milai.ecoop.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONObject;


import com.bigkoo.convenientbanner.ConvenientBanner;

import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;

import com.milai.ecoop.activity.BannerActivity;
import com.milai.ecoop.activity.MainActivity;
import com.milai.ecoop.activity.MainIntoActivity;
import com.milai.ecoop.activity.SigninActivity;
import com.milai.ecoop.activity.TeamDetailActivity;
import com.milai.ecoop.adapter.GoodsAdapter;
import com.milai.ecoop.bean.Banner;
import com.milai.ecoop.bean.Category;
import com.milai.ecoop.bean.SharedTeam;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.utils.CommonUtils;
import com.milai.ecoop.view.ImageHolderView;
import com.milai.ecoop.view.RefreshLayout;
import com.milai.ecoop.view.SlideShowView;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class HomeFrag extends Fragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {
    private View view;
    private View headview;
    private RefreshLayout swipeRefreshLayout;
    private ListView lv_goodsinfo;
    private GoodsAdapter adapter;
    private FragClickListener mFraglistener;
    private Intent intent,i;
    private List<Team> data;
    private NetDataHelper netDataHelper;
    private Gson gson;
    private RelativeLayout rl_not_network;
    private ConvenientBanner banner;
    private Button goods1,goods2,goods3,goods4;
    private Button[] finalgoods=new Button[4];
    private List<Category> categorys;
    private String city_id;
    public static HomeFrag createInstance(FragClickListener l) {
        HomeFrag f = new HomeFrag();
        f.mFraglistener = l;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_home, null);
        city_id=getArguments().getString("mcity_id");

        netDataHelper = NetDataHelper.getInstance(getActivity());
        gson = new Gson();
        swipeRefreshLayout = (RefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        headview = inflater.inflate(R.layout.layout_topgoodsnews, null);
        data = new ArrayList<>();
        adapter = new GoodsAdapter(getActivity(), data);
        lv_goodsinfo = (ListView) view.findViewById(R.id.lv_goodsinfo);
        //sv_photo = (SlideShowView) headview.findViewById(R.id.sv_photo);
        banner= (ConvenientBanner) headview.findViewById(R.id.convenientBanner);
        goods1= (Button) headview.findViewById(R.id.goods1);
        goods2= (Button) headview.findViewById(R.id.goods2);
        goods3= (Button) headview.findViewById(R.id.goods3);
        goods4= (Button) headview.findViewById(R.id.goods4);
        goods1.setOnClickListener(this);
        goods2.setOnClickListener(this);
        goods3.setOnClickListener(this);
        goods4.setOnClickListener(this);
        finalgoods=new Button[4];
        finalgoods=new Button[]{goods1,goods2,goods3,goods4};

        netDataHelper.getCategory(new NetCallBack<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                categorys=data;
                for(int i=0;i<data.size();i++){
                    finalgoods[i].setText(data.get(i).getName());
                    if(finalgoods[i].getText().toString().equals("品质鲜果")){
                        Drawable img=getActivity().getResources().getDrawable(R.drawable.icon_cm);
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        finalgoods[i].setCompoundDrawables(null,img,null,null);
                    }
                    if(finalgoods[i].getText().toString().equals("坚果小吃")){
                        Drawable img=getActivity().getResources().getDrawable(R.drawable.icon_jg);
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        finalgoods[i].setCompoundDrawables(null,img,null,null);
                    }
                    if(finalgoods[i].getText().toString().equals("精品红酒")){
                        Drawable img=getActivity().getResources().getDrawable(R.drawable.icon_hj);
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        finalgoods[i].setCompoundDrawables(null,img,null,null);
                    }
                    if(finalgoods[i].getText().toString().equals("团购礼盒")){
                        Drawable img=getActivity().getResources().getDrawable(R.drawable.icon_hj);
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        finalgoods[i].setCompoundDrawables(null,img,null,null);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
        getBanner();
        lv_goodsinfo.addHeaderView(headview);
        lv_goodsinfo.setAdapter(adapter);
        if (Cookie.loadHomePage(getActivity()) != null) {
            data.addAll(Cookie.loadHomePage(getActivity()));
            adapter.notifyDataSetChanged();
        }
        onRefresh();
        lv_goodsinfo.setOnItemClickListener(this);
        rl_not_network = (RelativeLayout) view.findViewById(R.id.ll_no_network);
        if (!CommonUtils.isConn(getActivity())) {
            rl_not_network.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        }
        return view;
    }

    private void getBanner() {
        netDataHelper.getBanners(new NetCallBack<List<Banner>>() {

            @Override
            public void onSuccess(final List<Banner> data) {
                List<String> imageUrls = new ArrayList<>();
                String urls[] = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    imageUrls.add(NetDataHelper.imgdomain + data.get(i).getImage());

                }

                //sv_photo.setView(urls);
                banner.setPages(new CBViewHolderCreator<ImageHolderView>() {

                    public ImageHolderView createHolder() {
                        return new ImageHolderView();
                    }
                }, imageUrls).setPageIndicator(new int[]{R.drawable.dot_blur, R.drawable.dot_focus});

                banner.setOnItemClickListener(new com.bigkoo.convenientbanner.listener.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (Cookie.session == null) {
                            showDialog();
                        } else if (data.get(position).getDetail().contains("http://")) {
                            intent = new Intent(getActivity(), BannerActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("banner", data.get(position));
                            intent.putExtras(bundle);
                            startActivity(intent);

                        } else {
                            netDataHelper.getTeamDetail(Cookie.session, data.get(position).getDetail(), new NetCallBack<JSONObject>() {

                                @Override
                                public void onSuccess(JSONObject data) {
                                    try {
                                        List<SharedTeam> list = gson.fromJson(data.getJSONArray("pin_tz_list").toString(),
                                                new TypeToken<List<SharedTeam>>() {
                                                }.getType());
                                        System.out.println("homefrag list size:" + list.size());
                                        Team team = gson.fromJson(data.getJSONObject("team").toString(), Team.class);
                                        intent = new Intent(getActivity(), TeamDetailActivity.class);
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
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getData() {
        netDataHelper.getHomePageTeams(((MainActivity) getActivity()).city_id, new NetCallBack<List<Team>>() {

            @Override
            public void onSuccess(List<Team> data) {
                HomeFrag.this.data.clear();
                HomeFrag.this.data.addAll(data);
                Log.d("zcz",NetDataHelper.imgdomain+data.get(0).getImage());
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                Cookie.saveHomePage(getActivity(), data);
            }

            @Override
            public void onError(String error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage("请先登录");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getActivity(), SigninActivity.class);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (Cookie.session == null) {
            showDialog();
        } else {
            netDataHelper.getTeamDetail(Cookie.session, data.get(position - 1).getId(), new NetCallBack<JSONObject>() {

                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        List<SharedTeam> list = gson.fromJson(data.getJSONArray("pin_tz_list").toString(),
                                new TypeToken<List<SharedTeam>>() {
                                }.getType());
                        System.out.println("homefrag list size:" + list.size());
                        Team team = gson.fromJson(data.getJSONObject("team").toString(), Team.class);
                        intent = new Intent(getActivity(), TeamDetailActivity.class);
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        banner.startTurning(6000);
        MobclickAgent.onPageStart("商品主页面"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("商品主页面");
    }
    @Override
    public void onRefresh() {
//        swipeRefreshLayout.setRefreshing(true);
        getBanner();
        getData();
    }
    private Bundle bundle;
    @Override
    public void onClick(View v) {
        i=new Intent(getActivity(), MainIntoActivity.class);
        i.putExtra("city_id",city_id);
        bundle=new Bundle();
        switch (v.getId()){
            case R.id.goods1:
                if (categorys != null && categorys.get(0) != null) {
                    bundle.putSerializable("category", categorys.get(0));
                    i.putExtras(bundle);
                    startActivity(i);
                }
                break;
            case R.id.goods2:
                if (categorys != null && categorys.get(1) != null) {
                    bundle.putSerializable("category", categorys.get(1));
                    i.putExtras(bundle);
                    startActivity(i);
                }
                break;
            case R.id.goods3:
                if (categorys != null && categorys.get(2) != null) {
                    bundle.putSerializable("category", categorys.get(2));
                    i.putExtras(bundle);
                    startActivity(i);
                }
                break;
            case R.id.goods4:
                if (categorys != null && categorys.get(3) != null) {
                    bundle.putSerializable("category", categorys.get(3));
                    i.putExtras(bundle);
                    startActivity(i);
                }
                break;
        }
    }
}
