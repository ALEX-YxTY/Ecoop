package com.milai.ecoop.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.adapter.ChooseAddressAdapter;
import com.milai.ecoop.bean.Address;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class ChooseAddressActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button bt_return, bt_addaddress;
    private ListView lv_address;
    private NetDataHelper netDataHelper;
    private ProgressDialog progressDialog;
    private ChooseAddressAdapter adapter;
    private ImageView iv_loading;
    private List<Address> data;
    private static final int ADD_ADDRESS = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);
        netDataHelper = NetDataHelper.getInstance(this);
        initView();
    }

    private void initView() {
        bt_return = (Button) findViewById(R.id.bt_return);
        bt_addaddress = (Button) findViewById(R.id.bt_addaddress);
        bt_return.setOnClickListener(this);
        bt_addaddress.setOnClickListener(this);
        lv_address = (ListView) findViewById(R.id.lv_address);
        data = new ArrayList<>();
        adapter = new ChooseAddressAdapter(this, data);
        lv_address.setAdapter(adapter);
        lv_address.setOnItemClickListener(this);
        iv_loading = (ImageView) findViewById(R.id.iv_loading);
        lv_address.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("正在加载,请稍候");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        progressDialog.show();
        netDataHelper.getAddress(Cookie.session, new NetCallBack<List<Address>>() {
            @Override
            public void onSuccess(List<Address> data) {
                ChooseAddressActivity.this.data.clear();
                ChooseAddressActivity.this.data.addAll(data);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
                if (data.size() == 0) {
                    lv_address.setVisibility(View.GONE);
                    iv_loading.setVisibility(View.VISIBLE);
                } else {
                    lv_address.setVisibility(View.VISIBLE);
                    iv_loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_return:
                //Intent intent=new Intent(ChooseAddressActivity.this,PayActivity.class);
                //startActivity(intent);
                setResult(RESULT_CANCELED);
                ChooseAddressActivity.this.finish();

                break;
            case R.id.bt_addaddress:
                Intent i = new Intent(ChooseAddressActivity.this, AddressInfoActivity.class);
                i.putExtra("fromC",true);

                startActivityForResult(i,ADD_ADDRESS);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&requestCode==ADD_ADDRESS){
            progressDialog.show();
            netDataHelper.getAddress(Cookie.session, new NetCallBack<List<Address>>() {
                @Override
                public void onSuccess(List<Address> data) {
                    ChooseAddressActivity.this.data.clear();
                    ChooseAddressActivity.this.data.addAll(data);
                    adapter.notifyDataSetChanged();

                    netDataHelper.editAddress(Cookie.session, ChooseAddressActivity.this.data.get(0), new NetCallBack<List<Address>>() {

                        @Override
                        public void onSuccess(List<Address> data) {
                            progressDialog.dismiss();
                            Intent i=new Intent();
                            i.putExtra("address",ChooseAddressActivity.this.data.get(0));
                            i.putExtra("back",true);
                            //Intent intent=new Intent(ChooseAddressActivity.this,PayActivity.class);
                            //startActivity(intent);
                            setResult(RESULT_OK, i);
                            ChooseAddressActivity.this.finish();
                        }

                        @Override
                        public void onError(String error) {
                            progressDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onError(String error) {

                }
            });


        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
        progressDialog.show();
        netDataHelper.editAddress(Cookie.session, data.get(position), new NetCallBack<List<Address>>() {
            @Override
            public void onSuccess(List<Address> data) {
                progressDialog.dismiss();
                Intent i=new Intent();
                i.putExtra("address",ChooseAddressActivity.this.data.get(position));
                i.putExtra("back",true);
                //Intent intent=new Intent(ChooseAddressActivity.this,PayActivity.class);
                //startActivity(intent);
                setResult(RESULT_OK, i);
                ChooseAddressActivity.this.finish();
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
            }
        });
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
