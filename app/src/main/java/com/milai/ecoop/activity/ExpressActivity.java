package com.milai.ecoop.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.adapter.ExpressAdapter;
import com.milai.ecoop.bean.Address;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.Ordertrack;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2016/3/9 0009.
 */
public class ExpressActivity extends Activity implements View.OnClickListener{
    private Button bt_return;
    private Order order;
    private ImageView iv_goods;
    private TextView tv_express_state,tv_express_no;
    private NetDataHelper netDataHelper;
    private String imageurl;
    private Gson gson;
    private List<Ordertrack> list;
    private ListView lv_express_info;
    private View view;
    private ExpressAdapter adapter;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express);
        gson=new Gson();
        Bundle bundle = getIntent().getExtras();
        order = (Order) (bundle.get("morder"));
        netDataHelper=NetDataHelper.getInstance(this);

        initview();
        getData();
        bt_return= (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
    }
    private void initview(){
        iv_goods= (ImageView) findViewById(R.id.iv_goods);
        tv_express_state= (TextView) findViewById(R.id.tv_express_state);
        tv_express_no= (TextView) findViewById(R.id.tv_express_no);
        lv_express_info= (ListView) findViewById(R.id.lv_express_info);
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("正在请求物流数据,请稍候");

    }
    private void getData(){
        progressDialog.show();
        netDataHelper.getLogistics(Cookie.session, order.getId(), new NetCallBack<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                try {
                    progressDialog.dismiss();
                    imageurl = data.getString("teampic");
                    Log.d("zcz", imageurl);
                    Picasso.with(ExpressActivity.this).load(NetDataHelper.imgdomain + imageurl).into(iv_goods);
                    list = gson.fromJson(data.getJSONArray("ordertrackinfo").toString(),
                            new TypeToken<List<Ordertrack>>() {
                            }.getType());
                    tv_express_state.setText(list.get(0).getStatusinfo());
                    tv_express_no.setText(list.get(0).getFkorderid());
                    adapter=new ExpressAdapter(ExpressActivity.this,list);
                    lv_express_info.setAdapter(adapter);
                    /*String[] info=new String[list.size()];


                    for(int i=0;i<list.size();i++){
                        if(i==0){
                            info[i]="【当前流程】"+list.get(i).getStdate()+"  "+list.get(i).getStremark();
                        }else{
                            info[i]=list.get(i).getStdate()+"  "+list.get(i).getStremark();
                        }
                    }
                    madapter=new ArrayAdapter(ExpressActivity.this,R.layout.item_express,info);

                    lv_express_info.setAdapter(madapter);*/



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                tv_express_state.setText("暂无物流信息");
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_return:
                finish();
                break;
        }
    }
}
