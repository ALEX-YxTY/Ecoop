package com.milai.ecoop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.milai.ecoop.R;
import com.milai.ecoop.activity.AddressEditActivity;
import com.milai.ecoop.bean.Address;

import java.util.List;

/**
 * Created by xiong on 2016/1/5.
 */
public class ChooseAddressAdapter extends BaseAdapter {
    private Context context;
    private List<Address> data;

    public ChooseAddressAdapter(Context context, List<Address> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        Address info = data.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.item_choose_address, null);
            holder.tv_username = (TextView) view.findViewById(R.id.tv_username);
            holder.tv_mobile = (TextView) view.findViewById(R.id.tv_mobile);
            holder.tv_address = (TextView) view.findViewById(R.id.tv_address);
            holder.iv_edit = (ImageView) view.findViewById(R.id.iv_edit);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_username.setText(info.getName());
        holder.tv_mobile.setText(info.getMobile());
        holder.tv_address.setText(info.getProvince() + info.getCity() + info.getArea() + info.getStreet());
        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddressEditActivity.class);
                intent.putExtra("address", data.get(position));
                context.startActivity(intent);
            }
        });
        return view;
    }

    private static class ViewHolder {
        private TextView tv_username;
        private TextView tv_mobile;
        private TextView tv_address;
        private ImageView iv_edit;
    }
}
