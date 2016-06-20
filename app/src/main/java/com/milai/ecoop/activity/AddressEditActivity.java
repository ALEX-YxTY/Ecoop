package com.milai.ecoop.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.adapter.addressspinneradapter.ZoneAdapter;
import com.milai.ecoop.bean.Address;
import com.milai.ecoop.bean.zonebean.City;
import com.milai.ecoop.dao.AddressJSONParserHelper;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.utils.CommonUtils;
import com.milai.ecoop.utils.FileUtil;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddressEditActivity extends Activity implements View.OnLongClickListener{
    private List<City> province_list = new ArrayList<City>();
    private HashMap<String, List<City>> city_map = new HashMap<String, List<City>>();
    private HashMap<String, List<City>> couny_map = new HashMap<String, List<City>>();
    private Spinner sp_province, sp_city, sp_county;
    private ZoneAdapter province_adapter, city_adapter, county_adapter;
    private List<City> citylist;
    private AddressJSONParserHelper parser = new AddressJSONParserHelper();
    private String area_str;
    private EditText et_client, et_phonenumber, et_myaddress, et_zipzone;
    private NetDataHelper netDataHelper;
    private Address address;
    private ContentResolver resolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressinfo);
        resolver=getContentResolver();
        address = (Address) getIntent().getSerializableExtra("address");
        Log.d("zcz", address.toString());
        netDataHelper = NetDataHelper.getInstance(AddressEditActivity.this);
        sp_province = (Spinner) findViewById(R.id.sp_province);
        sp_city = (Spinner) findViewById(R.id.sp_city);
        sp_county = (Spinner) findViewById(R.id.sp_county);
        area_str = FileUtil.readAssets(AddressEditActivity.this, "area.json");
        province_adapter = new ZoneAdapter(AddressEditActivity.this, getProvinceData());
        city_map = parser.getJSONParserResultArray(area_str, "area1");
        couny_map = parser.getJSONParserResultArray(area_str, "area2");
        sp_province.setAdapter(province_adapter);
        et_client = (EditText) findViewById(R.id.et_client);
        et_phonenumber = (EditText) findViewById(R.id.et_phonenumber);
        et_myaddress = (EditText) findViewById(R.id.et_myaddress);
        et_zipzone = (EditText) findViewById(R.id.et_zipzone);
        et_client.setText(address.getName());
        et_phonenumber.setText(address.getMobile());
        et_phonenumber.setOnLongClickListener(this);
        et_myaddress.setText(address.getStreet());
        et_zipzone.setText(address.getZipcode());
        Log.d("zcz", "sss" + address.getId() + address.getDefaultA());
        for (int i = 0; i < province_list.size(); i++) {
            if (address.getProvince().equals(province_list.get(i).getCity_name())) {
                sp_province.setSelection(i);
            }
        }

        sp_province.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City province = province_list.get(position);
                String cityId = province.getId();
                final List<City> rightCity = city_map.get(cityId);
                city_adapter = new ZoneAdapter(AddressEditActivity.this, rightCity);
                sp_city.setAdapter(city_adapter);
                for (int i = 0; i < rightCity.size(); i++) {
                    if (address.getCity().equals(rightCity.get(i).getCity_name())) {
                        sp_city.setSelection(i);
                    }
                }
                sp_city.setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        City county = rightCity.get(position);
                        String countyId = county.getId();
                        List<City> rightCounty = couny_map.get(countyId);
                        county_adapter = new ZoneAdapter(AddressEditActivity.this, rightCounty);
                        sp_county.setAdapter(county_adapter);
                        for (int i = 0; i < rightCounty.size(); i++) {
                            if (address.getArea().equals(rightCounty.get(i).getCity_name())) {
                                sp_county.setSelection(i);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

    }

    private List<City> getProvinceData() {
        province_list = parser.getJSONParserResult(area_str, "area0");
        return province_list;

    }

    public void click(View v) {
        switch (v.getId()) {
            case R.id.bt_return:
                finish();
                break;
            case R.id.bt_saveaddress:
                String client = et_client.getText().toString();
                String phonenumber = et_phonenumber.getText().toString();
                String address = et_myaddress.getText().toString();
                String zipcode = et_zipzone.getText().toString();
                String province = ((City) sp_province.getSelectedItem()).getCity_name();
                String city = ((City) sp_city.getSelectedItem()).getCity_name();
                String county = ((City) sp_county.getSelectedItem()).getCity_name();
                if ("".equals(client.trim())) {
                    Toast.makeText(AddressEditActivity.this, "还没输入您的姓名哦", Toast.LENGTH_SHORT).show();
                } else if ("".equals(phonenumber.trim())) {
                    Toast.makeText(AddressEditActivity.this, "还没输入您的联系电话哦", Toast.LENGTH_SHORT).show();
                } else if(!CommonUtils.isPhoneNumber(phonenumber.trim())){
                    Toast.makeText(AddressEditActivity.this, "您输入的手机号或者固话不正确", Toast.LENGTH_SHORT).show();
                }else if ("".equals(address.trim())) {
                    Toast.makeText(AddressEditActivity.this, "还没输入您的具体地址哦", Toast.LENGTH_SHORT).show();
                } else {

                    AddressEditActivity.this.address.setName(client);
                    AddressEditActivity.this.address.setMobile(phonenumber);
                    AddressEditActivity.this.address.setStreet(address);
                    AddressEditActivity.this.address.setProvince(province);
                    AddressEditActivity.this.address.setCity(city);
                    AddressEditActivity.this.address.setArea(county);
                    AddressEditActivity.this.address.setZipcode(zipcode);
                    Log.d("zcz", AddressEditActivity.this.address.getName());
                    netDataHelper.editAddress(Cookie.session, AddressEditActivity.this.address,
                            new NetCallBack<List<Address>>() {

                                @Override
                                public void onSuccess(List<Address> data) {
                                    Toast.makeText(AddressEditActivity.this, "地址修改成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onError(String error) {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(AddressEditActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.et_phonenumber:
                //获取通讯录界面
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
                break;
        }
        return false;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK)  {
                    Uri contactData = data.getData();
                    Cursor cursor = resolver.query(contactData, null, null, null, null);
                    cursor.moveToFirst();
                    String safeNumber=this.getContactPhone(cursor);
                    char[] ch = safeNumber.toCharArray();
                    StringBuilder builder = new StringBuilder();
                    for (char c : ch) {
                        if (c >= 48 && c <= 57) {
                            builder.append(c);
                        }
                    }
                    safeNumber = builder.toString();

                    et_phonenumber.setText(safeNumber);



                }
                break;


        }
    }
    private String getContactPhone(Cursor cursor) {
        int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String phoneResult="";
        if (phoneNum > 0)
        {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人的电话号码的cursor;
            Cursor phones = resolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId,
                    null, null);
            //int phoneCount = phones.getCount();
            //allPhoneNum = new ArrayList<String>(phoneCount);
            if (phones.moveToFirst())
            {
                // 遍历所有的电话号码
                for (;!phones.isAfterLast();phones.moveToNext())
                {
                    int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int phone_type = phones.getInt(typeindex);
                    String phoneNumber = phones.getString(index);
                    switch(phone_type)
                    {
                        case 2:
                            phoneResult=phoneNumber;
                            break;
                    }
                    //allPhoneNum.add(phoneNumber);
                }
                if (!phones.isClosed())
                {
                    phones.close();
                }
            }
        }

        return phoneResult;
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
