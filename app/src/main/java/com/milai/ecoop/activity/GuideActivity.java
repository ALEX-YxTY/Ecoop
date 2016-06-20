package com.milai.ecoop.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.milai.ecoop.R;
import com.milai.ecoop.adapter.GuidePageAdapter;
import com.milai.ecoop.fragment.FragClickListener;
import com.milai.ecoop.fragment.guidefragment.Fragpage1;
import com.milai.ecoop.fragment.guidefragment.Fragpage2;
import com.milai.ecoop.fragment.guidefragment.Fragpage3;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/1/4 0004.
 */
public class GuideActivity extends FragmentActivity {
    private ViewPager vp_guide;

    private GuidePageAdapter adapter;
    private FragClickListener l=new FragClickListener() {

        @Override
        public void onViewSelected(View v) {
            // TODO Auto-generated method stub

        }

        @Override
        public void enableMenuDrag(boolean b) {
            // TODO Auto-generated method stub

        }
    };
    private SharedPreferences.Editor editor;
    private Fragment[] fragments=new Fragment[]{Fragpage1.createInstance(l), Fragpage2.createInstance(l),
            Fragpage3.createInstance(l)};
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_guide);
        vp_guide=(ViewPager) findViewById(R.id.vp_guide);

        adapter=new GuidePageAdapter(getSupportFragmentManager(), fragments);
        vp_guide.setAdapter(adapter);

        SharedPreferences preferences = getSharedPreferences(
                "first_pref", MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("mFirst", false);
        editor.commit();
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}
