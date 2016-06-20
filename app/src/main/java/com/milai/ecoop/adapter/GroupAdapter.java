package com.milai.ecoop.adapter;

import java.util.List;
import java.util.Map;

import com.milai.ecoop.R;
import com.milai.ecoop.activity.GroupDetailActivity;
import com.milai.ecoop.activity.OrderDetailActivity;
import com.milai.ecoop.activity.PayActivity;
import com.milai.ecoop.bean.MyGroup;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.OrderInfo;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.dao.NetDataHelper;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {
    private Context context;
    private List<Order> myGroups;
    private Map<String, Team> map;
    private OnItemClickListener onItemClickListener;

    public GroupAdapter(Context context, List<Order> myGroups, Map<String, Team> map) {
        this.context = context;
        this.myGroups = myGroups;
        this.map = map;
    }

    public interface OnItemClickListener {
        void onClickOrderDetail(Order o);

        void onClickGroupDetail(Order o);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return myGroups.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return myGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        final Order mGroup = myGroups.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.item_group_purchase, null);
            holder.tv_group_state = (TextView) view.findViewById(R.id.tv_group_state);
            holder.tv_goodname = (TextView) view.findViewById(R.id.tv_goodname);
            holder.tv_team_price = (TextView) view.findViewById(R.id.tv_team_price);
            holder.iv_group_good = (ImageView) view.findViewById(R.id.iv_group_good);
            holder.bt_checkteam = (Button) view.findViewById(R.id.bt_checkteam);
            holder.bt_checkorder = (Button) view.findViewById(R.id.bt_checkorder);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_group_state.setText(mGroup.getPin_tip());
        holder.tv_goodname.setText(map.get(mGroup.getTeam_id()).getTitle());
        holder.tv_team_price.setText("合作价: ¥" +mGroup.getPrice()+ "/件");
        Log.d("getmap", mGroup.getTeam_id());
        Picasso.with(context)
                .load(NetDataHelper.imgdomain + map.get(mGroup.getTeam_id()).getImage())
                .into(holder.iv_group_good);
        holder.bt_checkteam.setText("查看社详情");
        holder.bt_checkteam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClickGroupDetail(mGroup);
                }
            }
        });
        if (mGroup.getPin_tip().equals("拼社中.未支付")) {
            holder.bt_checkteam.setText("去支付");
            holder.bt_checkteam.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClickOrderDetail(myGroups.get(position));
                    }
                }
            });
        }else if(mGroup.getPin_tip().equals("拼社已失败.未支付")){
            holder.bt_checkteam.setVisibility(View.GONE);
        }
        else if (mGroup.getPin_tip().equals("拼社已成功.未支付")) {
            holder.bt_checkteam.setText("去支付");
            holder.bt_checkteam.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClickOrderDetail(myGroups.get(position));
                    }
                }
            });
        } else {
            holder.bt_checkteam.setVisibility(View.VISIBLE);
            holder.bt_checkteam.setText("查看社详情");
            holder.bt_checkteam.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClickGroupDetail(mGroup);
                    }
                }
            });
        }

        holder.bt_checkorder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mInfo", myGroups.get(position));

                bundle.putSerializable("team", map.get(mGroup.getTeam_id()));
                intent.putExtras(bundle);
                intent.putExtra("from", "mygroup");
                context.startActivity(intent);
            }
        });
        return view;
    }

    private static class ViewHolder {
        private TextView tv_group_state;
        private TextView tv_goodname;
        private TextView tv_team_price;
        private ImageView iv_group_good;
        private Button bt_checkteam;
        private Button bt_checkorder;
    }

}
