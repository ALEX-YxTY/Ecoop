package com.milai.ecoop.fragment.guidefragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.milai.ecoop.R;
import com.milai.ecoop.activity.MainActivity;
import com.milai.ecoop.fragment.FragClickListener;


public class Fragpage3 extends Fragment implements View.OnClickListener {
    private View view;
    private FragClickListener mFraglistener;
    private ImageView ib_guide_3;

    public static Fragpage3 createInstance(FragClickListener l) {
        Fragpage3 f = new Fragpage3();
        f.mFraglistener = l;
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_page3, null);

        ib_guide_3 = (ImageView) view.findViewById(R.id.ib_guide_3);

        ib_guide_3.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_guide_3:
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().finish();
                break;

            default:
                break;
        }
    }
}
