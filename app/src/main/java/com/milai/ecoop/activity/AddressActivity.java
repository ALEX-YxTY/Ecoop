package com.milai.ecoop.activity;

import java.util.ArrayList;
import java.util.List;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.adapter.AddressAdapter;
import com.milai.ecoop.adapter.OrderAdapter;
import com.milai.ecoop.bean.Address;
import com.milai.ecoop.bean.AddressInfo;
import com.milai.ecoop.bean.GroupInfo;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddressActivity extends Activity implements View.OnClickListener ,AddressAdapter.OnItemClickListener{
    private ListView lv_address;
    private Intent intent;
    private List<Address> list;
    private AddressAdapter adapter;
    private NetDataHelper netDataHelper;
    private Button bt_return, bt_addaddress;
    private ProgressDialog progressDialog,mprogressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("正在加载,请稍候");
        mprogressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        mprogressDialog.setMessage("正在删除，请稍候");
        netDataHelper = NetDataHelper.getInstance(AddressActivity.this);
        list = new ArrayList<>();
        adapter = new AddressAdapter(AddressActivity.this, list);
        bt_return = (Button) findViewById(R.id.bt_return);
        bt_addaddress = (Button) findViewById(R.id.bt_addaddress);
        bt_return.setOnClickListener(this);
        bt_addaddress.setOnClickListener(this);
        lv_address = (ListView) findViewById(R.id.lv_address);

        lv_address.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        getData();

        /*lv_address.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(AddressActivity.this, AddressEditActivity.class);
                Address data = AddressActivity.this.list.get(position);
                intent.putExtra("address", data);
                startActivity(intent);
            }
        });*/
    }
    public void getData() {
        netDataHelper.getAddress(Cookie.session, new NetCallBack<List<Address>>() {

            @Override
            public void onSuccess(List<Address> data) {
                if (AddressActivity.this.list != null)
                    AddressActivity.this.list.clear();
                AddressActivity.this.list.addAll(data);
                Log.d("zcz", "s" + AddressActivity.this.list.size());
                if(AddressActivity.this.list.size()==0){
                    lv_address.setBackgroundResource(R.drawable.bg_download);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_return:
                finish();
                break;

            case R.id.bt_addaddress:
                intent = new Intent(AddressActivity.this, AddressInfoActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClickAddress(Address a) {
        progressDialog.show();
        netDataHelper.setDefultAddress(Cookie.session, a, new NetCallBack<List<Address>>() {
            @Override
            public void onSuccess(List<Address> data) {
                progressDialog.dismiss();
                getData();
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onDelAddress(Address a) {
        mprogressDialog.show();
        netDataHelper.delAddress(Cookie.session, a, new NetCallBack<List<Address>>() {
            @Override
            public void onSuccess(List<Address> data) {
                mprogressDialog.dismiss();
                getData();
            }

            @Override
            public void onError(String error) {

            }
        });
    }
}
