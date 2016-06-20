package com.milai.ecoop.fragment.childrenfragment;

import com.milai.ecoop.R;
import com.milai.ecoop.activity.TeamDetailActivity;
import com.milai.ecoop.fragment.FragClickListener;
import com.milai.ecoop.fragment.HomeFrag;
import com.milai.ecoop.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 商品详情页的图文详情,预留网址接口需要集中处理
 */
public class GraphicdetailsFrag extends Fragment {
    private View view;
    private FragClickListener mFraglistener;
    private WebView wb_graphicdetails;


    public static GraphicdetailsFrag createInstance(FragClickListener l) {
        GraphicdetailsFrag f = new GraphicdetailsFrag();
        f.mFraglistener = l;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_graphicdetails, null);
        wb_graphicdetails = (WebView) view.findViewById(R.id.wb_graphicdetails);

        wb_graphicdetails.getSettings().setJavaScriptEnabled(true);//设置使用够执行JS脚本
        wb_graphicdetails.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        String html = ((TeamDetailActivity) getActivity()).getTeam().getDetail();

        int width= (int) (CommonUtils.px2dip(getActivity(),display.getWidth())*0.95);

        String mhtml="<html><head><style>img{max-width:"+width+"px !important;}</style></head><body>"+html+"</body></html> ";
        //wb_graphicdetails.loadUrl("http://www.baidu.com");
        Log.d("html", html);
        wb_graphicdetails.loadDataWithBaseURL(null, mhtml, "text/html", "UTF-8", null);
        //Picasso.with(getActivity()).load("http://ecoop.szdod.com//static/team/2016/0215/2016021513372232130.jpg").into(iv_details);
        //如果希望点击链接由自己处理，而不是新开Android的系统browser中响应该链接。  
        //需要给WebView添加一个事件监听对象（WebViewClient)，并重写shouldOverrideUrlLoading方法        
        wb_graphicdetails.setWebViewClient(new MyWebViewClient());
        return view;
    }

    private class MyWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
