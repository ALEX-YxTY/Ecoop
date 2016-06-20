package com.milai.ecoop.adapter;

import java.util.List;

import com.milai.ecoop.R;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.dao.NetDataHelper;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GoodsAdapter extends BaseAdapter {
    private Context context;
    private List<Team> data;

    public GoodsAdapter(Context context, List<Team> data) {
        this.context = context;
        this.data = data;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        // 通过列表项位置从集合中获取要显示的对象
        Team team = data.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.item_goods, null);
            holder.iv_goods = (ImageView) view.findViewById(R.id.iv_goods);
            holder.tv_goods_title = (TextView) view.findViewById(R.id.tv_goods_title);
            holder.tv_goods_content = (TextView) view.findViewById(R.id.tv_goods_content);
            holder.tv_team_price = (TextView) view.findViewById(R.id.tv_team_price);
            holder.tv_market_price = (TextView) view.findViewById(R.id.tv_market_price);
            holder.tv_team_way = (TextView) view.findViewById(R.id.tv_team_way);
            holder.bt_team_buy = (TextView) view.findViewById(R.id.bt_team_buy);
            holder.tv_team_intro= (TextView) view.findViewById(R.id.tv_team_intro);
            holder.tv_good_info= (TextView) view.findViewById(R.id.tv_good_info);
            holder.tv_reminder_content= (TextView) view.findViewById(R.id.tv_reminder_content);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        Picasso.with(context).load(NetDataHelper.imgdomain + team.getImage()).into(holder.iv_goods);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        holder.iv_goods.setMaxWidth(wm.getDefaultDisplay().getWidth());
        holder.iv_goods.setMaxHeight(wm.getDefaultDisplay().getHeight());
        holder.tv_goods_title.setText("品  名：" + team.getTitle());
        holder.tv_goods_content.setText(team.getSummary());
        holder.tv_team_price.setText(team.getPin_price()+"   ");
        holder.tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tv_good_info.setText("【"+team.getAttr()+"】");
        holder.tv_market_price.setText("市场价: ¥" + team.getMarket_price());
        holder.tv_team_intro.setText(team.getProduct());
        holder.tv_team_way.setText(team.getMin_number() + "人社");
        holder.tv_reminder_content.setText(team.getWarmprompt());
        return view;
    }

    private static class ViewHolder {
        private ImageView iv_goods;
        private TextView tv_goods_title;
        private TextView tv_goods_content;
        private TextView tv_team_price;
        private TextView tv_market_price;
        private TextView tv_team_way;
        private TextView bt_team_buy;
        private TextView tv_team_intro;
        private TextView tv_good_info;
        private TextView tv_reminder_content;
    }
}
