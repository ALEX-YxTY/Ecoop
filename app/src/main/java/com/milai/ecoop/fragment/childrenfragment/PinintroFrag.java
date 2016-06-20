package com.milai.ecoop.fragment.childrenfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.milai.ecoop.R;
import com.milai.ecoop.activity.PuzzleActivity;
import com.milai.ecoop.activity.TeamDetailActivity;
import com.milai.ecoop.fragment.FragClickListener;

/**
 * Created by Administrator on 2016/2/18 0018.
 */
public class PinintroFrag extends Fragment implements View.OnClickListener{
    private View view;
    private FragClickListener mFraglistener;
    private WebView wb_graphicdetails;
    private LinearLayout ll_play_intro;
    private ImageView bg_1, bg_2, bg_3, bg_4;
    public static PinintroFrag createInstance(FragClickListener l) {
        PinintroFrag f = new PinintroFrag();
        f.mFraglistener = l;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_pinintro, null);
        ll_play_intro = (LinearLayout) view.findViewById(R.id.ll_play_intro);
        ll_play_intro.setOnClickListener(this);
        bg_1 = (ImageView) view.findViewById(R.id.bg_1);
        bg_2 = (ImageView) view.findViewById(R.id.bg_2);
        bg_3 = (ImageView) view.findViewById(R.id.bg_3);
        bg_4 = (ImageView) view.findViewById(R.id.bg_4);
        bg_1.setImageResource(R.drawable.bg_1);
        bg_2.setImageResource(R.drawable.write_2);
        bg_3.setImageResource(R.drawable.write_3);
        bg_4.setImageResource(R.drawable.write_4);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_play_intro:
                Intent intent = new Intent(getActivity(), PuzzleActivity.class);
                startActivity(intent);
                break;
        }
    }
}