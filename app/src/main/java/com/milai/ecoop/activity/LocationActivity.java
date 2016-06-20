package com.milai.ecoop.activity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.dao.NetCallBack2;
import com.milai.ecoop.dao.NetDataHelper;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private NetDataHelper netDataHelper;
    private TextView tv_current_city;
    private ListView lv_city;
    private Map<String, String> map;
    private List<String> cities;
    private ArrayAdapter<String> adapter;
    private Button bt_return;
    private boolean isLocated;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        mLocationClient.start();

        netDataHelper = NetDataHelper.getInstance(this);
        tv_current_city = (TextView) findViewById(R.id.tv_current_city);
        tv_current_city.setOnClickListener(this);

        cities = new ArrayList<>();
        map = new HashMap<>();
        lv_city = (ListView) findViewById(R.id.lv_city);
        adapter = new ArrayAdapter(this, R.layout.item_city, R.id.tv_city);
        lv_city.setAdapter(adapter);
        lv_city.setOnItemClickListener(this);
        bt_return = (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
        getSupportCities();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(true);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    private void getSupportCities() {
        netDataHelper.getCities(new NetCallBack2<List<String>, Map<String, String>>() {
            @Override
            public void onSuccess(List<String> list, Map<String, String> map) {
                adapter.addAll(list);
                adapter.notifyDataSetChanged();
                cities.addAll(list);
                LocationActivity.this.map.putAll(map);
                Log.d("zzzzz list", list + "");
                Log.d("zzzzz map", map + "");
            }

            @Override
            public void onError(String error) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_current_city:
                if (tv_current_city.getText().equals("正在定位")) {
                } else if (map.get(tv_current_city.getText().toString()) != null) {
                    Intent i = new Intent();
                    i.putExtra("city", tv_current_city.getText().toString());
                    i.putExtra("id", map.get(tv_current_city.getText().toString()));
                    setResult(RESULT_OK, i);
                    Cookie.saveCityId(this, map.get(tv_current_city.getText().toString()));
                    Cookie.saveCityName(this, tv_current_city.getText().toString());
                    finish();
                } else {
                    Toast.makeText(LocationActivity.this, "当前城市未开通", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_return:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent();
        i.putExtra("city", cities.get(position));
        i.putExtra("id", map.get(cities.get(position)));
        setResult(RESULT_OK, i);
        Cookie.saveCityId(this, map.get(cities.get(position)));
        Cookie.saveCityName(this, cities.get(position));
        Log.d("save id", map.get(cities.get(position)));
        Log.d("save named", map.get(cities.get(position)));
        finish();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
                tv_current_city.setText(location.getCity().replace("市", ""));
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                tv_current_city.setText(location.getCity().replace("市", ""));
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                tv_current_city.setText(location.getCity().replace("市", ""));
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
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


