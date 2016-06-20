package com.milai.ecoop.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.bean.User;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.utils.CommonUtils;
import com.umeng.analytics.MobclickAgent;


public class BindTelActivity extends Activity implements View.OnClickListener,View.OnLongClickListener{
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private int time = 60;
    private boolean terminateCount = false;
    private Button bt_send_verify,bt_return,bt_binding;
    private EditText et_phonenumber,et_identifyingcode;
    private NetDataHelper netDataHelper;
    private ProgressDialog dialog;
    private ContentResolver resolver;
    Thread oneSecondThread = new Thread(new Runnable() {

        @Override
        public void run() {
            System.out.println("oneSecondThread running");
            try {
                if (time > 0 && !terminateCount) {
                    System.out.println("time = " + time);
                    time--;
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.arg1 = time;
                    uiHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    });
    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (time > 0 && !terminateCount) {
                bt_send_verify.setText(msg.arg1 + "s");
                mHandler.post(oneSecondThread);
            } else {
                bt_send_verify.setText("发送验证码");
                bt_send_verify.setEnabled(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tel_bind);
        resolver=getContentResolver();
        bt_send_verify = (Button) findViewById(R.id.bt_send_verify);
        bt_send_verify.setText("发送验证码");
        bt_send_verify.setOnClickListener(this);
        et_phonenumber= (EditText) findViewById(R.id.et_phonenumber);
        et_phonenumber.setOnLongClickListener(this);
        et_identifyingcode= (EditText) findViewById(R.id.et_identifyingcode);
        netDataHelper = NetDataHelper.getInstance(this);
        mHandlerThread = new HandlerThread("count", 0);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        bt_return= (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
        bt_binding= (Button) findViewById(R.id.bt_binding);
        bt_binding.setOnClickListener(this);
        dialog = new ProgressDialog(BindTelActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("正在绑定");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_return:
                finish();
                break;
            case R.id.bt_send_verify:
                sendVerify();
                break;
            case R.id.bt_binding:
                bind();

                break;
        }
    }
    private void sendVerify() {
        if (!CommonUtils.isPhoneNumber(et_phonenumber.getText().toString().trim())) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        bt_send_verify.setEnabled(false);
        netDataHelper.sendVerify(et_phonenumber.getText().toString().trim(), "92", new NetCallBack<String>() {

            @Override
            public void onSuccess(String data) {
                //Toast.makeText(BindTelActivity.this, data, Toast.LENGTH_SHORT).show();
                //et_identifyingcode.setText(data);
                time = 45;
                terminateCount = false;
                mHandler.post(oneSecondThread);
            }

            @Override
            public void onError(String error) {
                bt_send_verify.setEnabled(true);
            }
        });
    }
    private void bind(){
        dialog.show();
        netDataHelper.bindMobile(Cookie.session, et_phonenumber.getText().toString().trim(), et_identifyingcode.getText().toString().trim(), "92",

        new NetCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                dialog.dismiss();
                Cookie.user.setMobile(data);
                Toast.makeText(BindTelActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(BindTelActivity.this,MainActivity.class);
                startActivity(i);



            }

            @Override
            public void onError(String error) {
                Toast.makeText(BindTelActivity.this, "绑定失败，请检查网络是否正常连接", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
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
