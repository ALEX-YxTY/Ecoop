package com.milai.ecoop.activity;

import com.milai.ecoop.Cookie;
import com.milai.ecoop.MyApplication;
import com.milai.ecoop.R;
import com.milai.ecoop.bean.User;
import com.milai.ecoop.bean.VersionInfo;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NetDataHelper;
import com.milai.ecoop.fragment.FragClickListener;
import com.milai.ecoop.fragment.HomeFrag;
import com.milai.ecoop.fragment.MineFrag;
import com.milai.ecoop.utils.CommonUtils;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MineSettingActivty extends Activity implements View.OnClickListener {


    private TextView username, mobile, check_version,version;
    private Button bt_return, bt_log_off;
    private com.milai.ecoop.view.CircleImageView avatar;
    private RelativeLayout rl_bind_tel;
    private Intent i;
    private User user;
    private NetDataHelper netDataHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesetting);
        user=Cookie.user;
        netDataHelper=NetDataHelper.getInstance(this);
        username = (TextView) findViewById(R.id.username);
        username.setText(user.getUsername());
        mobile = (TextView) findViewById(R.id.mobile);
        mobile.setText(user.getMobile());
        avatar = (com.milai.ecoop.view.CircleImageView) findViewById(R.id.avatar);
        Picasso.with(MineSettingActivty.this).load(user.getAvatar()).placeholder(R.drawable.ic_profile_none).into(avatar);
        bt_return = (Button) findViewById(R.id.bt_return);
        bt_return.setOnClickListener(this);
        bt_log_off = (Button) findViewById(R.id.bt_log_off);
        bt_log_off.setOnClickListener(this);
        rl_bind_tel = (RelativeLayout) findViewById(R.id.rl_bind_tel);
        rl_bind_tel.setOnClickListener(this);
        check_version = (TextView) findViewById(R.id.check_version);
        check_version.setOnClickListener(this);
        version= (TextView) findViewById(R.id.version);

        PackageInfo info= CommonUtils.getPackageInfo(this);
        if(info!=null){
           version.setText(info.versionName);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        netDataHelper.updatelogin(Cookie.session, new NetCallBack<User>() {
            @Override
            public void onSuccess(User data) {
                mobile.setText(data.getMobile());
            }

            @Override
            public void onError(String error) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_return:
                finish();
                break;
            case R.id.bt_log_off:

                Cookie.clearCookie(getApplicationContext());
                Cookie.removeAlias(MineSettingActivty.this);
                i = new Intent(MineSettingActivty.this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.rl_bind_tel:
                i = new Intent(MineSettingActivty.this, BindTelActivity.class);
                startActivity(i);
                break;
            case R.id.check_version:
//                UmengUpdateAgent.setUpdateAutoPopup(false);
//                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
//                    @Override
//                    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
//                        switch (updateStatus) {
//                            case UpdateStatus.Yes: // has update
//                                UmengUpdateAgent.showUpdateDialog(MineSettingActivty.this, updateInfo);
//                                break;
//                            case UpdateStatus.No: // has no update
//                                Toast.makeText(MineSettingActivty.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
//                                break;
//                            case UpdateStatus.NoneWifi: // none wifi
//                                Toast.makeText(MineSettingActivty.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
//                                break;
//                            case UpdateStatus.Timeout: // time out
//                                Toast.makeText(MineSettingActivty.this, "超时", Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//                    }
//                });
//                UmengUpdateAgent.forceUpdate(MineSettingActivty.this);
                //更换为自动更新
                checkVerison();
                break;
        }
    }

    /**
     * 检查版本信息，有新版本弹窗提示下载，否则显示最新版本
     */
    private void checkVerison() {
        final Message message = Message.obtain();
        final AlertDialog.Builder builder = new AlertDialog.Builder(MineSettingActivty.this);
        final int versionCode = getVersion();
        netDataHelper.getVersion(new NetCallBack<VersionInfo>() {

            class MyAsy extends AsyncTask<String, Integer, File> {

                ProgressDialog mProgressDialog = new ProgressDialog(MineSettingActivty.this);

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mProgressDialog.setTitle("下载进度");
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                }

                @Override
                protected void onPostExecute(File file) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    if (file.exists()) {
                        installApk(file);
                    }
                }

                private void installApk(File file) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    MineSettingActivty.this.startActivityForResult(intent, 0);
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
                                int len;
                                int total = 0;
                                final int lengthOfFile = conn.getContentLength();
                                /*
                                    设置最大值，必须运行在UI线程，doInBackground运行在工作线程
                                 */
                                MineSettingActivty.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.setMax((int) (lengthOfFile / 1024.0/1024.0));
                                    }
                                });

                                float lenOfFile = lengthOfFile / 1024.0f / 1024.0f;

                                while ((len = is.read(buffer)) != -1) {
                                    fos.write(buffer, 0, len);
                                    fos.flush();
                                    total += len;
                                    mProgressDialog.setProgress(total);
                                    float current = total / 1024.0f / 1024.0f;
                                    mProgressDialog.setProgressNumberFormat(
                                            String.format("%.2fMb/%.2fMb", current, lenOfFile));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fos.close();
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(MineSettingActivty.this, "连接出错", Toast.LENGTH_SHORT).show();
                        }
                        conn.disconnect();

                    } catch (Exception e) {
                        e.printStackTrace();
                        mProgressDialog.dismiss();
                    }
                    return file;
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
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    builder.setMessage("当前已是最新版本");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }

            }

            @Override
            public void onError(String error) {
                Toast.makeText(MineSettingActivty.this, "网络出错", Toast.LENGTH_SHORT).show();
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
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
