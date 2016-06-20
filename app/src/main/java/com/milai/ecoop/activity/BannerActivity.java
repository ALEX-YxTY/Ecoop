package com.milai.ecoop.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.milai.ecoop.R;
import com.milai.ecoop.bean.Banner;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/1/19 0019.
 */
public class BannerActivity extends Activity implements View.OnClickListener{
    private Button bt_return;
    private WebView wb_banner;
    private ProgressBar myProgressBar;
    private Banner banner;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        banner= (Banner) getIntent().getExtras().getSerializable("banner");
        wb_banner= (WebView) findViewById(R.id.wb_banner);
        wb_banner.getSettings().setJavaScriptEnabled(true);
        wb_banner.getSettings().setBuiltInZoomControls(true);
        title= (TextView) findViewById(R.id.title);
        bt_return= (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
        myProgressBar= (ProgressBar) findViewById(R.id.myProgressBar);
        wb_banner.setWebViewClient(new MyWebViewClient());
        wb_banner.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (100 == newProgress) {
                    myProgressBar.setVisibility(View.GONE);
                } else {
                    myProgressBar.setProgress(newProgress);
                }
            }
        });
        if(banner!=null){
            wb_banner.loadUrl(banner.getDetail());
            title.setText(banner.getTitle());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_return:
                finish();
                break;
        }
    }
    private class MyWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
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
