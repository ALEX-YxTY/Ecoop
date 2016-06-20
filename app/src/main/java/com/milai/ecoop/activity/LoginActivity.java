package com.milai.ecoop.activity;

import java.io.File;

import com.android.volley.toolbox.DiskBasedCache;
import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.bean.User;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.utils.CommonUtils;
import com.milai.ecoop.view.TimeButton;
import com.umeng.analytics.MobclickAgent;

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

/**
 * 手机登录界面
 */
public class LoginActivity extends Activity implements View.OnClickListener,View.OnLongClickListener {
	// 判断是否进入城市选择界面
	private Boolean choosecity;
	private Intent intent;
	private CheckBox cb_protocol;
	private Button  bt_return, bt_login,bt_send_verify;
	//private TimeButton bt_send_verify;
	private NetDataHelper netDataHelper;
	private EditText et_phonenumber, et_identifyingcode;
	private int time = 60;
	private boolean terminateCount = false;
	private Handler mHandler;
	private HandlerThread mHandlerThread;
	private ProgressDialog dialog;
	private ContentResolver resolver;
	Thread oneSecondThread = new Thread(new Runnable() {

		@Override
		public void run() {
			System.out.println("oneSecondThread running");
			try {
				if (time > 0 && !terminateCount) {
					Message msg = new Message();
					msg.arg1 = time;
					uiHandler.sendMessage(msg);
                    time--;
                    Thread.sleep(1000);
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
		setContentView(R.layout.activity_tel_login);
		netDataHelper = NetDataHelper.getInstance(this);
		resolver=getContentResolver();
		cb_protocol = (CheckBox) findViewById(R.id.cb_protocol);
		cb_protocol.isChecked();
		choosecity = false;
		bt_return = (Button) findViewById(R.id.bt_return);
		bt_return.setOnClickListener(this);
		bt_send_verify = (Button) findViewById(R.id.bt_send_verify);
		bt_send_verify.setText("发送验证码");
		bt_send_verify.setOnClickListener(this);
		et_phonenumber = (EditText) findViewById(R.id.et_phonenumber);
		et_phonenumber.setOnLongClickListener(this);
		et_identifyingcode = (EditText) findViewById(R.id.et_identifyingcode);
		bt_login = (Button) findViewById(R.id.bt_login);
		bt_login.setOnClickListener(this);
		dialog = new ProgressDialog(LoginActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
		dialog.setMessage("正在登录");

		mHandlerThread = new HandlerThread("count",0);
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_return:
			finish();
			break;

		case R.id.bt_login:
			login();
			break;
		case R.id.bt_send_verify:


			sendVerify();
			break;
		}
	}

	private void sendVerify() {
		bt_send_verify.setEnabled(false);
		if (!CommonUtils.isPhoneNumber(et_phonenumber.getText().toString().trim())) {
			Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            bt_send_verify.setEnabled(true);
			return;
		}
		time = 30;
		terminateCount = false;
		mHandler.post(oneSecondThread);
		netDataHelper.sendVerify(et_phonenumber.getText().toString().trim(), "92", new NetCallBack<String>() {

			@Override
			public void onSuccess(String data) {
				//测试用
//				Toast.makeText(LoginActivity.this, data, Toast.LENGTH_SHORT).show();
//				et_identifyingcode.setText(data);
			}

			@Override
			public void onError(String error) {
				bt_send_verify.setEnabled(true);
			}
		});
	}

	private void login() {
		dialog.show();
		netDataHelper.login(et_phonenumber.getText().toString().trim(), et_identifyingcode.getText().toString().trim(),
				new NetCallBack<User>() {

					@Override
					public void onSuccess(User data) {
						dialog.dismiss();
						MobclickAgent.onProfileSignIn(Cookie.session.getUid());
						Intent i = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(i);
					}

					@Override
					public void onError(String error) {
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
