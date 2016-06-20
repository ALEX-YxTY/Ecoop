package com.milai.ecoop.adapter;

import com.milai.ecoop.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingAdapter extends BaseAdapter{
	private Context context;
	private String names[];
	private GridView gv_menu;
	private int icons[]={R.drawable.icon_my_order,R.drawable.icon_my_regiment,R.drawable.icon_activity,
			R.drawable.icon_address,R.drawable.icon_after_sale_service,R.drawable.icon_set_up};
	public SettingAdapter(GridView gv_menu,Context context) {
		this.context=context;
		names=context.getResources().getStringArray(R.array.setting_item_name);
		this.gv_menu=gv_menu;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=View.inflate(context, R.layout.item_setting, null);
		ImageView iv_icon=(ImageView) view.findViewById(R.id.iv_icon);
		TextView tv_iconname=(TextView) view.findViewById(R.id.tv_iconname);
		tv_iconname.setText(names[position]);
		iv_icon.setImageResource(icons[position]);
		int height = gv_menu.getHeight();
		int width = gv_menu.getWidth();

//得到GridView每一项的高度与宽度
		GridView.LayoutParams params = new GridView.LayoutParams(width / 3,
				height /2);
//设置每一行的高度和宽度
		view.setLayoutParams(params);
		return view;
	}

}
