package com.milai.ecoop.adapter.addressspinneradapter;

import java.util.List;

import com.milai.ecoop.R;
import com.milai.ecoop.bean.zonebean.City;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ZoneAdapter extends BaseAdapter implements SpinnerAdapter{
	private Context context ;  
    private List<City> list;  
      
    public ZoneAdapter(Context context,List<City> list){  
        this.context=context;  
        this.list=list;  
    } 
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position; 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		City city=list.get(position);
	     convertView=LayoutInflater.from(context).inflate(R.layout.item_zoneaddress, null);  
	     TextView tv_zone=(TextView) convertView.findViewById(R.id.tv_zone);  
	     tv_zone.setText(city.getCity_name());  
	     return convertView; 
	}
	public View getDropDownView(int position, View convertView, ViewGroup parent) { 
		City city=list.get(position);
	    convertView=LayoutInflater.from(context).inflate(R.layout.item_zoneaddress, null);  
	    TextView tv_zone=(TextView) convertView.findViewById(R.id.tv_zone);  
	    tv_zone.setText(city.getCity_name());  
        return convertView;  
    }  
}
