package com.milai.ecoop.adapter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.activity.PayActivity;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.bean.Ticket;
import com.milai.ecoop.dao.NetCallBack2;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.fragment.FragClickListener;
import com.milai.ecoop.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private Map<String, Team> map;
    private List<Order> data;
    private Team team;
    private OnPayListener onPayListener;
    private OnDelOrderListener onDelOrderListener;
    private OnExpressListener onExpressListener;
    public OrderAdapter(Context context, List<Order> data, Map<String, Team> map) {
        this.context = context;
        this.data = data;
        this.map = map;

    }

    public interface OnPayListener {
        void onClick(int position);
    }

    public interface OnDelOrderListener {
        void onDel(Order o, String message);
    }
    public interface OnExpressListener{
        void onExp(Order o);
    }
    public void setOnPayListener(OnPayListener onPayListener) {
        this.onPayListener = onPayListener;
    }

    public void setOnDelOrderListener(OnDelOrderListener onDelOrderListener) {
        this.onDelOrderListener = onDelOrderListener;
    }
    public void setOnExpressListener(OnExpressListener onExpressListener){
        this.onExpressListener=onExpressListener;
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
        ViewHolder holder;
        final Order info = data.get(position);
        OrderAdapter.this.team = map.get(info.getTeam_id());
        Log.d("zcz", data.toString());
        View view;
        if (convertView == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.item_order, null);
            holder.iv_order_good = (ImageView) view
                    .findViewById(R.id.iv_order_good);
            holder.tv_order_date = (TextView) view
                    .findViewById(R.id.tv_order_date);
            holder.tv_order_state = (TextView) view
                    .findViewById(R.id.tv_order_state);
            holder.tv_order_content = (TextView) view
                    .findViewById(R.id.tv_order_content);
            holder.tv_order_price = (TextView) view
                    .findViewById(R.id.tv_order_price);
            holder.tv_market_price = (TextView) view
                    .findViewById(R.id.tv_market_price);
            holder.tv_order_number = (TextView) view
                    .findViewById(R.id.tv_order_number);
            holder.tv_pay_money = (TextView) view
                    .findViewById(R.id.tv_pay_money);
            holder.ll_order_good = (LinearLayout) view
                    .findViewById(R.id.ll_order_good);
            holder.bt1 = (Button) view.findViewById(R.id.bt_logistics);
            holder.bt2 = (Button) view.findViewById(R.id.bt_confirm_goods);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        Picasso.with(context).load(NetDataHelper.imgdomain + team.getImage()).into(holder.iv_order_good);
        holder.tv_order_content.setText(team.getTitle());
        holder.tv_order_price.setText("¥" + team.getPin_price());
        holder.tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tv_market_price.setText("¥" + team.getMarket_price());
        holder.tv_order_number.setText("×" + info.getQuantity());
        holder.tv_order_date
                .setText(info.getEstablish_time());
        holder.tv_order_state.setText(info.getOrder_state());

        if(info.getOrder_info_type().equals("0")){
            holder.bt1.setVisibility(View.GONE);
            holder.bt2.setVisibility(View.VISIBLE);
            holder.bt2.setText("取消订单");
            holder.bt2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDelOrderListener != null) {
                        onDelOrderListener.onDel(info, "确定取消吗?");
                    }
                }
            });
        }else if(info.getOrder_info_type().equals("1")){
            holder.bt1.setVisibility(View.VISIBLE);
            holder.bt2.setVisibility(View.GONE);
            holder.bt1.setText("删除订单");
            holder.bt1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDelOrderListener != null) {
                        onDelOrderListener.onDel(info, "确定删除吗?");
                    }
                }
            });

        }else if(info.getOrder_info_type().equals("2")){
            holder.bt1.setVisibility(View.VISIBLE);
            holder.bt2.setVisibility(View.VISIBLE);
            holder.bt1.setText("去支付");
            holder.bt1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog dialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    dialog.setMessage("正在加载,请稍候");
                    dialog.setCancelable(false);
                    dialog.show();
                    NetDataHelper.getInstance(context).checkOrder(Cookie.session, info.getId(), new NetCallBack2<List<Ticket>, Order>() {
                        @Override
                        public void onSuccess(List<Ticket> data1, Order data2) {
                            dialog.dismiss();
                            Intent intent = new Intent(context, PayActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("tickets", (Serializable) data1);
                            bundle.putSerializable("order", data2);
                            bundle.putSerializable("team", map.get(data2.getTeam_id()));
                            intent.putExtras(bundle);
                            intent.putExtra("allowEdit", false);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onError(String error) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            holder.bt2.setText("取消订单");
            holder.bt2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDelOrderListener != null) {
                        onDelOrderListener.onDel(info, "确定取消吗?");
                    }
                }
            });
        }else if(info.getOrder_info_type().equals("10")){
            holder.bt1.setVisibility(View.GONE);
            holder.bt2.setVisibility(View.GONE);
        }else if(info.getOrder_info_type().equals("11")){
            holder.bt1.setVisibility(View.VISIBLE);
            holder.bt2.setVisibility(View.GONE);
            holder.bt1.setText("查看物流");
            holder.bt1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onExpressListener != null) {
                        onExpressListener.onExp(info);
                    }
                }
            });
        }else if(info.getOrder_info_type().equals("12")){
            holder.bt1.setVisibility(View.VISIBLE);
            holder.bt2.setVisibility(View.GONE);
            holder.bt1.setText("查看物流");
            holder.bt2.setText("确认收货");
            holder.bt1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onExpressListener != null) {
                        onExpressListener.onExp(info);
                    }
                }
            });
        }else{
            holder.bt1.setVisibility(View.GONE);
            holder.bt2.setVisibility(View.GONE);
        }
        /*if (info.getOrder_state().equals("已退款")) {
            holder.bt1.setVisibility(View.GONE);
            holder.bt2.setVisibility(View.GONE);
        } else if (info.getOrder_state().equals("待支付")) {
            holder.bt1.setVisibility(View.VISIBLE);
            holder.bt2.setVisibility(View.VISIBLE);
            holder.bt1.setText("去支付");
            holder.bt1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog dialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    dialog.setMessage("正在加载,请稍候");
                    dialog.setCancelable(false);
                    dialog.show();
                    NetDataHelper.getInstance(context).checkOrder(Cookie.session, info.getId(), new NetCallBack2<List<Ticket>, Order>() {
                        @Override
                        public void onSuccess(List<Ticket> data1, Order data2) {
                            dialog.dismiss();
                            Intent intent = new Intent(context, PayActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("tickets", (Serializable) data1);
                            bundle.putSerializable("order", data2);
                            bundle.putSerializable("team", map.get(data2.getTeam_id()));
                            intent.putExtras(bundle);
                            intent.putExtra("allowEdit", false);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onError(String error) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            holder.bt2.setText("取消订单");
            holder.bt2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDelOrderListener != null) {
                        onDelOrderListener.onDel(info, "确定取消吗?");
                    }
                }
            });
        } else if (info.getOrder_state().equals("已收货")) {
            holder.bt1.setVisibility(View.GONE);
            holder.bt2.setVisibility(View.GONE);
        } else if (info.getOrder_type().equals("12")) {
            holder.bt1.setVisibility(View.VISIBLE);
            holder.bt2.setVisibility(View.VISIBLE);
            holder.bt1.setText("查看物流");
            holder.bt2.setText("确认收货");
        } else if (info.getOrder_state().equals("等待发货")) {
            holder.bt1.setVisibility(View.GONE);
            holder.bt2.setVisibility(View.GONE);
        } else if (info.getOrder_type().equals("1")) {
            holder.bt1.setVisibility(View.VISIBLE);
            holder.bt1.setText("删除订单");
            holder.bt1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDelOrderListener != null) {
                        onDelOrderListener.onDel(info, "确定删除吗?");
                    }
                }
            });
            holder.bt2.setVisibility(View.GONE);
        }*/
        holder.tv_pay_money.setText("实付:¥" + info.getMoney());
        holder.ll_order_good.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (onPayListener != null) {
                    onPayListener.onClick(position);
                }
            }
        });
        return view;
    }

    private static class ViewHolder {
        private ImageView iv_order_good;
        private TextView tv_order_date;
        private TextView tv_order_state;
        private TextView tv_order_content;
        private TextView tv_order_price;
        private TextView tv_market_price;
        private TextView tv_order_number;
        private TextView tv_pay_money;
        private LinearLayout ll_order_good;
        private Button bt1, bt2;
    }

    private FragClickListener fragClickListener = new FragClickListener() {

        @Override
        public void onViewSelected(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {

            }
        }

        @Override
        public void enableMenuDrag(boolean b) {
        }
    };

}
