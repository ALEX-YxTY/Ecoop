package com.milai.ecoop.activity;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class VoteActivity extends Activity implements OnClickListener {
    private Button bt_return;
    private WebView wb_vote;
    private ProgressBar myProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        bt_return = (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
        myProgressBar = (ProgressBar) findViewById(R.id.myProgressBar);
        wb_vote = (WebView) findViewById(R.id.wb_vote);
        wb_vote.getSettings().setJavaScriptEnabled(true);

        wb_vote.setWebViewClient(new MyWebViewClient());
        wb_vote.setWebChromeClient(new WebChromeClient() {
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
        wb_vote.loadUrl("http://ecoop.szdod.com/active/?uid=" + Cookie.session.getUid());
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

    @Override
    protected void onPause() {
        super.onPause();
        wb_vote.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wb_vote.reload();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wb_vote.loadData("", "text/html", "utf-8");
    }
}
