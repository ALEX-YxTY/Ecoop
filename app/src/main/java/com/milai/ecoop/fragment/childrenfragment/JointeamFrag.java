package com.milai.ecoop.fragment.childrenfragment;

import java.util.ArrayList;
import java.util.List;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.milai.ecoop.activity.GroupDetailActivity;
import com.milai.ecoop.activity.TeamDetailActivity;
import com.milai.ecoop.bean.SharedTeam;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.fragment.FragClickListener;
import com.milai.ecoop.pay.PayHelper;
import com.milai.ecoop.view.CircleImageView;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JointeamFrag extends Fragment implements View.OnClickListener {
    private View view;
    private LinearLayout ll_pin_tz;
    private List<SharedTeam> list;
    private OnItemClickListener onItemClickListener;

    public static JointeamFrag createInstance(FragClickListener l) {
        JointeamFrag f = new JointeamFrag();
        return f;
    }

    public interface OnItemClickListener {
        void onItemClick(SharedTeam st);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_jointeam, null);
        ll_pin_tz = (LinearLayout) view.findViewById(R.id.ll_pin_tz);
        list = new ArrayList<>();
        list.addAll(((TeamDetailActivity) getActivity()).getSharedTeams());

        if (list.size() > 0) {
            for (final SharedTeam st : list) {
                View v = inflater.inflate(R.layout.item_shared_team, null);
                CircleImageView avatar = (CircleImageView) v.findViewById(R.id.avatar);
                TextView username = (TextView) v.findViewById(R.id.username);
                TextView time = (TextView) v.findViewById(R.id.time);
                TextView tv_price = (TextView) v.findViewById(R.id.tv_price);
                TextView tv_reamin = (TextView) v.findViewById(R.id.tv_remain);
                Picasso.with(getActivity()).load(st.getAvatar()).placeholder(R.drawable.ic_profile_none).into(avatar);
                if(st.getUsername()==null){
                    v.setVisibility(View.GONE);
                }else{
                    username.setText(st.getUsername());
                    time.setText(st.getCreatetime() + "开社");
                    tv_price.setText("￥" + st.getPrice() + "/件");
                    tv_reamin.setText("差" + st.getRemain_count() + "人成社，火速参社");
                    tv_reamin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(st);
                            }

                        }
                    });
                }



                ll_pin_tz.addView(v);
            }
        } else {
            TextView tv_join_team = (TextView) view.findViewById(R.id.tv_join_team);
            tv_join_team.setText("暂无分享社");
        }
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
