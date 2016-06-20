package com.milai.ecoop.fragment;

import com.milai.ecoop.R;
import com.milai.ecoop.adapter.OrderAdapter;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderInfoFrag extends Fragment{
	private View view;
	private Order info;
	private Team team;
	private ImageView iv_order_good;
	private TextView tv_order_content;
	private TextView tv_order_price;
	private TextView tv_market_price;
	private TextView tv_order_number;
	private TextView tv_order_no;
	private TextView tv_order_time;
	private FragClickListener mFraglistener;
	public static OrderInfoFrag createInstance(FragClickListener l) {
		OrderInfoFrag f = new OrderInfoFrag();
			f.mFraglistener = l;
	        return f;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.frag_orderinfo, null);
		iv_order_good=(ImageView) view.findViewById(R.id.iv_order_good);
		tv_order_content=(TextView) view.findViewById(R.id.tv_order_content);
		tv_order_price=(TextView) view.findViewById(R.id.tv_order_price);
		tv_market_price=(TextView) view.findViewById(R.id.tv_market_price);
		tv_order_number=(TextView) view.findViewById(R.id.tv_order_number);
		tv_order_no=(TextView) view.findViewById(R.id.tv_order_no);
		tv_order_time=(TextView) view.findViewById(R.id.tv_order_time);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(getArguments()!=null){
			info=(Order) getArguments().getSerializable("mInfo");
			team=(Team) getArguments().getSerializable("team");	
			
	        int max = getActivity().getResources().getDimensionPixelOffset(
					R.dimen.margin_127);
	        Picasso.with(getActivity()).load(NetDataHelper.imgdomain + team.getImage())
	        .resize(max,max).into(iv_order_good);
	        tv_order_content.setText(team.getSummary());
	        tv_order_price.setText("¥"+team.getPin_price());
	        tv_market_price.setText("¥"+team.getMarket_price());
	        tv_order_number.setText("×" + info.getQuantity());
	        tv_order_no.setText(info.getTrade_no());
	        tv_order_time.setText(CommonUtils.DateUtilSecond(info.getCreate_time()));
		}
	}
}
