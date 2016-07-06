package com.milai.ecoop.activity;

/**
 * Created by Administrator on 2016/1/4 0004.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.milai.ecoop.R;
import com.milai.ecoop.bean.VersionInfo;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.utils.CommonUtils;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity implements View.OnClickListener {
    private Intent intent;
    boolean mFirst;
    private NetDataHelper netDataHelper;
    private ImageView iv_startpage;
    private String url;
    private Button step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        SharedPreferences preferences = getSharedPreferences("first_pref", MODE_APPEND);
        mFirst = preferences.getBoolean("mFirst", true);
        step= (Button) findViewById(R.id.step);
        step.setOnClickListener(this);
        netDataHelper = NetDataHelper.getInstance(this);

//        if (mFirst){
//            mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY, 6000);
//        }
//        else{
//            mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY, 6000);
//        }
        iv_startpage = (ImageView) findViewById(R.id.iv_startpage);
        /*File f = new File(Environment.getExternalStorageDirectory() + File.separator + "startPage.jpg");
        if (f.exists()) {
            Picasso.with(this).load(f).into(iv_startpage);
        } else {
            Picasso.with(this).load(R.drawable.start_page).into(iv_startpage);
        }*/

        netDataHelper.getStartpage(new NetCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                url = data;
                Picasso.with(SplashActivity.this).load(url)
                        .placeholder(getResources().getDrawable(R.drawable.start_page))
                        .error(getResources().getDrawable(R.drawable.start_page)).into(iv_startpage);

                /*SharedPreferences sp = getPreferences(MODE_APPEND);
                if (!TextUtils.equals(data, sp.getString("startPage", ""))) {
                    Thread t = new Thread(SplashActivity.this);
                    t.start();
                }*/
            }

            @Override
            public void onError(String error) {
                //无网络情况下无图片显示，是否需要默认图片或提示
            }
        });

        checkVersion();
    }

    /**
     * 检查版本信息，有新版本弹窗提示下载，否则进入主页，此页面至少保持2秒
     */
    private void checkVersion() {
        final Long startTime = System.currentTimeMillis();    //获取当前时间，用于判断splash页面的整体停留时间
        final Message message = Message.obtain();

        final AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        final int versionCode = getVersion();
        netDataHelper.getVersion(new NetCallBack<VersionInfo>() {

            class MyAsy extends AsyncTask<String,Integer,File>{

                ProgressDialog mProgressDialog = new ProgressDialog(SplashActivity.this);

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mProgressDialog.setTitle("下载进度");
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);
//                    mProgressDialog.setProgressNumberFormat("%1d Mb/%2d Mb");
                    mProgressDialog.show();
                }

                @Override
                protected void onPostExecute(File file) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    if(file.exists()){
                        installApk(file);
                    }
                }

                private void installApk(File file) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    SplashActivity.this.startActivityForResult(intent,0);
                }


                @Override
                protected File doInBackground(String... params) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/ecoop.apk");

                    try {
                        URL url = new URL(params[0]);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(1500);
                        conn.setReadTimeout(1500);

                        if (conn.getResponseCode() == 200) {
                            InputStream is = conn.getInputStream();
                            FileOutputStream fos = new FileOutputStream(file);
                            try {
                                byte[] buffer = new byte[1024];
                                int len ;
                                int total = 0;
                                int lengthOfFile = conn.getContentLength();
                                //设置最大值
                                mProgressDialog.setMax(lengthOfFile );
                                final float length = lengthOfFile / 1024.0f / 1024.0f;

                                while ((len = is.read(buffer)) != -1) {
                                    fos.write(buffer, 0, len);
                                    fos.flush();
                                    total += len;
                                    mProgressDialog.setProgress(total);
                                    final float current = total / 1024.0f / 1024.0f;
                                    //直接设置ProgressDialog右下角文字显示
                                    SplashActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProgressDialog.setProgressNumberFormat(
                                                    String.format("%.2fMb/%.2fMb", current, length));
                                        }
                                    });

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            finally {
                                try {
                                    fos.close();
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else {
                            SplashActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SplashActivity.this,"连接出错",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        conn.disconnect();

                    } catch (Exception e) {
                        e.printStackTrace();
                        mProgressDialog.dismiss();
                    }
                    return  file;
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                }

            }

            @Override
            public void onSuccess(final VersionInfo data) {

                if (Integer.parseInt(data.getApp_version()) > versionCode) {

                    builder.setTitle("发现新版本");
                    builder.setMessage("最新版本：" + data.getApp_version_name()
                            + "\n更新内容：" + data.getApp_update_desc());
                    builder.setPositiveButton("现在下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            new MyAsy().execute(data.getApp_file());
                        }
                    });
                    builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Long endTime = System.currentTimeMillis();
                            message.what = (mFirst == true) ? SWITCH_GUIDACTIVITY : SWITCH_MAINACTIVITY;
                            mHandler.sendMessageDelayed(message, 5000 - endTime + startTime);
                        }
                    });
                    builder.show();
                }else {
                    nextPage();
                }

            }

            private void nextPage() {
                Long endTime = System.currentTimeMillis();
                message.what = (mFirst == true) ? SWITCH_GUIDACTIVITY : SWITCH_MAINACTIVITY;
                mHandler.sendMessageDelayed(message, 5000 - endTime + startTime);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();
                nextPage();
            }
        });

    }

    private int getVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            int versionCode = pi.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mFirst)
            mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY, 5000);
        else
            mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY, 5000);
    }

    //*************************************************
    // Handler:跳转至不同页面
    //*************************************************
    private final static int SWITCH_MAINACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SWITCH_MAINACTIVITY:
                    intent = new Intent();
                    intent.setClass(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                    break;
                case SWITCH_GUIDACTIVITY:
                    intent = new Intent();
                    intent.setClass(SplashActivity.this, GuideActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

//    @Override
//    public void run() {
//        URL url;
//        try {
//            Log.d("zzz", "zzzzzz");
//            url = new URL(SplashActivity.this.url);
//            InputStream is = url.openStream();
//            Bitmap bitmap = BitmapFactory.decodeStream(is);
//            File myCaptureFile = new File(Environment.getExternalStorageDirectory() + File.separator + "startPage.jpg");
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
//            bos.flush();
//            bos.close();
//            is.close();
//            SharedPreferences sp = getPreferences(MODE_APPEND);
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString("startPage", SplashActivity.this.url);
//            editor.apply();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.step:
                if(mFirst){
                    intent = new Intent();
                    intent.setClass(SplashActivity.this, GuideActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                }else{
                    intent = new Intent();
                    intent.setClass(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                }
                break;
        }
    }
}