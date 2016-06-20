package com.milai.ecoop.adapter;

import java.util.List;

import com.milai.ecoop.R;
import com.milai.ecoop.bean.CoupunInfo;
import com.milai.ecoop.bean.Ticket;
import com.milai.ecoop.utils.CommonUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CoupunAdapter extends BaseAdapter {
    private Context context;
    private List<Ticket> data;
    private View view;
    private int state = 1;

    public void setState(int s) {
        state = s;
    }

    public CoupunAdapter(Context context, List<Ticket> data) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Ticket ticket = data.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.item_coupun, null);
            holder.iv_coupun = (ImageView) view.findViewById(R.id.iv_coupun);
            holder.tv_coupun_price = (TextView) view.findViewById(R.id.tv_coupun_price);
            holder.tv_coupun_name = (TextView) view.findViewById(R.id.tv_coupun_name);
            holder.tv_coupun_condition = (TextView) view.findViewById(R.id.tv_coupun_condition);
            holder.tv_validity = (TextView) view.findViewById(R.id.tv_validity);
            holder.tv_price_yuan = (TextView) view.findViewById(R.id.tv_price_yuan);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        switch (state) {
            case 1:
                holder.iv_coupun.setImageResource(R.drawable.bg_green);
                break;

            case 2:
                holder.iv_coupun.setImageResource(R.drawable.bg_gray);
                break;
        }
        holder.tv_coupun_price.setText(ticket.getCredit() + "元");
        holder.tv_coupun_name.setText(ticket.getName());
        if (ticket.getBegin_time().contains("-")) {
            holder.tv_validity.setText("有效期:" + ticket.getBegin_time().split(" ")[0] + "至" + ticket.getEnd_time().split(" ")[0] + "可用");
        } else {
            holder.tv_validity.setText("有效期:" + CommonUtils.DateUtilSecondShort(ticket.getBegin_time()) + "至" + CommonUtils.DateUtilSecondShort(ticket.getEnd_time()) + "可用");
        }
        if (ticket.getMin_price().equals("0")) {
            holder.tv_coupun_condition.setText("无限制");
        } else {
            holder.tv_coupun_condition.setText("满" + ticket.getMin_price() + "元可用");
        }

        return view;
    }

    private static class ViewHolder {
        private TextView tv_coupun_price;
        private TextView tv_coupun_name;
        private TextView tv_coupun_condition;
        private TextView tv_validity;
        private TextView tv_price_yuan;
        private ImageView iv_coupun;
    }

}
