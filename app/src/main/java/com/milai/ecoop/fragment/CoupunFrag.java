package com.milai.ecoop.fragment;

import java.util.ArrayList;
import java.util.List;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.activity.MainActivity;
import com.milai.ecoop.activity.MineSettingActivty;
import com.milai.ecoop.activity.NoticeActivity;
import com.milai.ecoop.adapter.CoupunAdapter;
import com.milai.ecoop.adapter.GroupAdapter;
import com.milai.ecoop.adapter.OrderAdapter;
import com.milai.ecoop.bean.CoupunInfo;
import com.milai.ecoop.bean.GroupInfo;
import com.milai.ecoop.bean.OrderInfo;
import com.milai.ecoop.bean.Session;
import com.milai.ecoop.bean.Ticket;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.pay.PayHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CoupunFrag extends Fragment implements AbsListView.OnScrollListener {
    private View view;
    private FragClickListener mFraglistener;
    private ListView lv_coupun;
    private RadioGroup radioGroup;
    private PayHelper payHelper;
    private List<Ticket> data;
    private CoupunAdapter adapter;
    private int page = 1;
    private boolean isLastRow = false;
    private int state = 1;
    private Button bt_return;
    private ImageButton share;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Fragment fragment = null;
    private TextView title_main;
    public static CoupunFrag createInstance(FragClickListener l) {
        CoupunFrag f = new CoupunFrag();
        f.mFraglistener = l;
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_coupun, null);
        lv_coupun = (ListView) view.findViewById(R.id.lv_coupun);
        payHelper = PayHelper.getInstance(getActivity());
        data = new ArrayList<>();
        adapter = new CoupunAdapter(getActivity(), data);
        lv_coupun.setAdapter(adapter);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_coupun);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_wait_use:
                        state = 1;
                        adapter.setState(1);
                        break;
                    case R.id.rb_unactivited:
                        state = 2;
                        adapter.setState(2);
                        break;
                    case R.id.rb_overdue:
                        state = 3;
                        adapter.setState(2);
                        break;
                }
                data.clear();
                adapter.notifyDataSetChanged();
                page = 1;
                getData(page, state);
            }
        });
        getData(page, state);
        manager = getFragmentManager();
        transaction = manager.beginTransaction();
        share= (ImageButton) getActivity().findViewById(R.id.share);
        share.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bt_return= (Button) getActivity().findViewById(R.id.bt_return);
        bt_return.setVisibility(View.VISIBLE);

        bt_return.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_return:
                        showFrag("mine");
                        break;
                }
            }
        });
        title_main= (TextView) getActivity().findViewById(R.id.title_main);
        title_main.setText("优惠券");
    }



    private void showFrag(String frag) {
        fragment = null;
         if (frag.equals("mine")) {
             bt_return.setVisibility(View.GONE);
             title_main.setText("我的");
            fragment = MineFrag.createInstance(fragClickListener);
            transaction.replace(R.id.fl_content, fragment);
            transaction.commit();
        }
    }
    private FragClickListener fragClickListener = new FragClickListener() {

        @Override
        public void onViewSelected(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                // case R.id.btn_list:
                // showFrag(ConstUtil.FRAG.MESEUM_LIST);
                // break;
            }
        }

        @Override
        public void enableMenuDrag(boolean b) {
        }
    };
    public void getData(int p, int state) {
        payHelper.getTickets(Cookie.session, p, 10, state, new NetCallBack<List<Ticket>>() {

            @Override
            public void onSuccess(List<Ticket> data) {
                CoupunFrag.this.data.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Log.d("CoupunFrag", error);
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            //加载元素
            getData(++page, state);
            isLastRow = false;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
            isLastRow = true;
        }
    }
}
