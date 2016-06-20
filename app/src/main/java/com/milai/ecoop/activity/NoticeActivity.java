package com.milai.ecoop.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.bean.Address;
import com.milai.ecoop.bean.Session;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoticeActivity extends Activity implements View.OnClickListener, AbsListView.OnScrollListener {
    private Button bt_return,clear;
    private ListView lv_notice;
    private List<Map<String, String>> data;
    private SimpleAdapter adapter;
    private NetDataHelper netDataHelper;
    private int page = 1;
    private boolean isLastRow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        netDataHelper = NetDataHelper.getInstance(this);
        data = new ArrayList<>();
        initView();
        getData(1);
    }

    private void initView() {
        bt_return = (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
        clear= (Button) findViewById(R.id.clear);
        clear.setOnClickListener(this);
        adapter = new SimpleAdapter(this, data, R.layout.item_notice, new String[]{"content", "createtime"}, new int[]{R.id.content, R.id.createtime});
        lv_notice = (ListView) findViewById(R.id.lv_notice);
        lv_notice.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_return:
                finish();
                break;
            case R.id.clear:
                netDataHelper.delNotices(Cookie.session, new NetCallBack<String>() {
                    @Override
                    public void onSuccess(String data) {
                        new AlertDialog.Builder(NoticeActivity.this).setTitle("提示")
                                .setMessage("是否清空所有通知").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NoticeActivity.this.data.clear();
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();

                            }
                        }).setNegativeButton("取消",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        }).show();;

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
                break;
        }
    }

    private void getData(int p) {
        netDataHelper.getNotices(Cookie.session, p, 10, new NetCallBack<List<Map<String, String>>>() {
            @Override
            public void onSuccess(List<Map<String, String>> data) {
                NoticeActivity.this.data.addAll(data);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            //加载元素
            getData(++page);
            isLastRow = false;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
            isLastRow = true;
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
}
