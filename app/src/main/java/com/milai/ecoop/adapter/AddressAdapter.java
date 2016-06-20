package com.milai.ecoop.adapter;

import java.util.List;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.activity.AddressEditActivity;
import com.milai.ecoop.activity.PayActivity;
import com.milai.ecoop.bean.Address;
import com.milai.ecoop.bean.AddressInfo;
import com.milai.ecoop.bean.GoodInfo;
import com.milai.ecoop.bean.GroupInfo;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AddressAdapter extends BaseAdapter{
	private Context context;
	private List<Address> data;
	private OnItemClickListener onItemClickListener;
	private NetDataHelper mNetDataHelper;
	private  ViewHolder holder;
	private View view;
	private NetDataHelper netDataHelper;
	ProgressDialog progressDialog;
	public AddressAdapter(Context context,List<Address> data) {
		this.context=context;
		this.data=data;
		mNetDataHelper=NetDataHelper.getInstance(context);
	}
	public interface OnItemClickListener {
		void onClickAddress(Address a);
		void onDelAddress(Address a);

	}
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {



		final Address info = data.get(position);
		if(convertView==null){
			holder = new ViewHolder();
			view=View.inflate(context, R.layout.item_address, null);
			holder.tv_default=(TextView) view.findViewById(R.id.tv_default);
			holder.cb_defult_address= (RadioButton) view.findViewById(R.id.cb_defult_address);

			holder.tv_username=(TextView) view.findViewById(R.id.tv_username);
			holder.tv_phonenumber=(TextView) view.findViewById(R.id.tv_phonenumber);
			holder.tv_address=(TextView) view.findViewById(R.id.tv_address);

			holder.edit_address= (Button) view.findViewById(R.id.edit_address);
			holder.del_address= (Button) view.findViewById(R.id.del_address);
			view.setTag(holder);
		}else{
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		if(data.get(position).getDefaultA().equals("Y")){
			holder.tv_default.setVisibility(View.VISIBLE);
			holder.cb_defult_address.setChecked(true);
		}else{
			holder.tv_default.setVisibility(View.GONE);
			holder.cb_defult_address.setChecked(false);
		}
		holder.tv_username.setText(info.getName());
		holder.tv_phonenumber.setText(info.getMobile());
		holder.tv_address.setText(info.getProvince() + info.getCity() + info.getArea() + info.getStreet());
		holder.edit_address.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.edit_address:
						Intent intent = new Intent(context, AddressEditActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("address", data.get(position));
						intent.putExtras(bundle);
						context.startActivity(intent);

						break;
				}
			}
		});
		holder.del_address.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onItemClickListener != null){
					onItemClickListener.onDelAddress(data.get(position));
				}
			}
		});
		holder.cb_defult_address.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onItemClickListener != null) {
					onItemClickListener.onClickAddress(data.get(position));

				}

			}
		});

		return view;
	}
	private static class ViewHolder {
		private TextView tv_username;
		private TextView tv_phonenumber;
		private TextView tv_address;
		private TextView tv_default;
		private RadioButton cb_defult_address;
		private Button edit_address,del_address;
	}


}

