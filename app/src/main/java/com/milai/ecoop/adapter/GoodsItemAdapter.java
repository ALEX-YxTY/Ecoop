package com.milai.ecoop.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.milai.ecoop.R;
import com.milai.ecoop.protocol.GoodsItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class GoodsItemAdapter extends BaseAdapter {
	private List<GoodsItem> data;
	private Context context;

	public GoodsItemAdapter(List<GoodsItem> data, Context context) {
		this.data = data;
		this.context = context;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			view = View.inflate(context, R.layout.item_goods, null);
			holder.iv_goods = (ImageView) view
					.findViewById(R.id.iv_goods);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		return view;
	}

	private static class ViewHolder {
		private ImageView iv_goods;

	}
}
