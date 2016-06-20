package com.milai.ecoop.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeIntoFrag extends Fragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
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

    private ConvenientBanner banner;
    private LinearLayout mdzz;
    private List<Category> categorys;
    private Category category;
    private String city_id;
    private RelativeLayout ll_empty;
    private Button bt_return_main;
    public static HomeIntoFrag createInstance(FragClickListener l) {
        HomeIntoFrag f = new HomeIntoFrag();
        f.mFraglistener = l;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_home_into, null);
        city_id=getArguments().getString("city_id");
        category= (Category) getArguments().getSerializable("category");
        ll_empty= (RelativeLayout) view.findViewById(R.id.ll_empty);
        bt_return_main= (Button) view.findViewById(R.id.bt_return_main);
        bt_return_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
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
        mdzz= (LinearLayout) headview.findViewById(R.id.mdzz);
        mdzz.setVisibility(View.GONE);


        getBanner();
        lv_goodsinfo.addHeaderView(headview);
        lv_goodsinfo.setAdapter(adapter);
        if (Cookie.loadHomePage(getActivity()) != null) {
            data.addAll(Cookie.loadHomePage(getActivity()));
            adapter.notifyDataSetChanged();
        }
        onRefresh();
        lv_goodsinfo.setOnItemClickListener(this);

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


                banner.setPages(new CBViewHolderCreator<ImageHolderView>() {

                    public ImageHolderView createHolder() {
                        return new ImageHolderView();
                    }
                }, imageUrls).setPageIndicator(new int[]{R.drawable.dot_blur, R.drawable.dot_focus});
                ;
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
        netDataHelper.getHomeIntoPageTeams(city_id,category.getId(), new NetCallBack<List<Team>>() {

            @Override
            public void onSuccess(List<Team> data) {
                HomeIntoFrag.this.data.clear();
                HomeIntoFrag.this.data.addAll(data);
                Log.d("zcz", NetDataHelper.imgdomain + data.get(0).getImage());




                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onError(String error) {
                ll_empty.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
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
        swipeRefreshLayout.setRefreshing(true);
        getBanner();
        getData();
    }

}
