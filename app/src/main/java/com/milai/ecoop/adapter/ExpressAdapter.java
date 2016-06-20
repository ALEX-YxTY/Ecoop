package com.milai.ecoop.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.milai.ecoop.R;
import com.milai.ecoop.activity.ExpressActivity;
import com.milai.ecoop.bean.Ordertrack;

import java.util.List;

/**
 * Created by Administrator on 2016/3/11 0011.
 */
public class ExpressAdapter extends BaseAdapter{
    private Context context;
    private List<Ordertrack> data;
    public ExpressAdapter(Context context,List<Ordertrack> data){
        this.context=context;
        this.data=data;
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
        ViewHolder holder;
        Ordertrack ordertrack=data.get(position);
        View view;
        if (convertView == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.item_express, null);
            holder.content= (TextView) view.findViewById(R.id.content);
            holder.createtime= (TextView) view.findViewById(R.id.createtime);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.createtime.setText(ordertrack.getStdate());
        if(position==0){
            holder.content.setText("【正在进行】" + ordertrack.getStremark());

            holder.content.setTextColor(context.getResources().getColor(R.color.green_title));
            holder.createtime.setTextColor(context.getResources().getColor(R.color.green_title));
        }else{
            holder.content.setText(ordertrack.getStremark());

        }

        return view;
    }
    private static class ViewHolder {

        private TextView content;
        private TextView createtime;

    }
}
