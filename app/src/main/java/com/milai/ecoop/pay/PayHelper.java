package com.milai.ecoop.pay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.Session;
import com.milai.ecoop.bean.Ticket;
import com.milai.ecoop.dao.NetCallBack;
import com.milai.ecoop.dao.NormalPostRequest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class  PayHelper {
    private static PayHelper payHelper;
    private static RequestQueue mQueue;
    private static Gson gson;
    private static String domain = "http://ecoop.szdod.com/rest/";
    private Context context;

    private PayHelper(Context context) {
        mQueue = Volley.newRequestQueue(context);
        gson = new Gson();

    }

    public static synchronized PayHelper getInstance(Context context) {
        if (payHelper == null) {
            payHelper = new PayHelper(context.getApplicationContext());
        }
        context=context;
        return payHelper;
    }

    public void buyTeam(Session session, String id, boolean isPin, String pin_id, final NetCallBack<String> callback) {
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
            obj.put("id", id);
            if (isPin) {
                obj.put("act", "pin");
            }
            if (!TextUtils.isEmpty(pin_id)) {
                obj.put("pin_id", pin_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e.getMessage());
        }
        System.out.println(obj.toString());
        Map<String, String> map = new HashMap<>();
        map.put("json", obj.toString());
        Log.i("buy team request", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "team/buy.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("buy team response", arg0.toString());
                int succeed;
                try {
                    succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    callback.onSuccess(arg0.getJSONObject("data").getString("order_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onError(arg0.toString());
            }

        }, map);
        mQueue.add(request);
    }

    public void getTickets(Session session, final int page, int count, int state, final NetCallBack<List<Ticket>> callback) {
        Map<String, String> map = new HashMap<>();
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        JSONObject p = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            p.put("page", String.valueOf(page));
            p.put("count", String.valueOf(count));
            obj.put("session", s);
            obj.put("pagination", p);
            if (state != 0)
                obj.put("state", String.valueOf(state));

        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        System.out.println(obj.toString());
        map.put("json", obj.toString());
        Log.d("get ticket request", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "account/cards.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                int succeed;
                Log.d("get ticket response", arg0.toString());
                try {
                    succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    List<Ticket> tickets = gson.fromJson(arg0.getJSONArray("data").toString(),
                            new TypeToken<List<Ticket>>() {
                            }.getType());
                    callback.onSuccess(tickets);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onError(arg0.toString());
            }

        }, map);
        mQueue.add(request);
    }
    public void actTickets(Session session,String cardid, final NetCallBack<String> callback) {
        Map<String, String> map = new HashMap<>();
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();

        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("cardid", cardid);
            obj.put("act", "verify");
            obj.put("session", s);
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        System.out.println(obj.toString());
        map.put("json", obj.toString());
        Log.d("zcz", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "card.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                int succeed;
                Log.d("zcz", arg0.toString());
                try {
                    succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    callback.onSuccess("");
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onError(arg0.toString());
            }

        }, map);
        mQueue.add(request);
    }
    public void PayState(Session session,String order_id, String card_id,String address_id,int paytype,final NetCallBack<JSONObject> callback){
        Map<String, String> map = new HashMap<String, String>();
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
            obj.put("order_id", order_id);
            obj.put("address_id",address_id);
            if(paytype==1){
                obj.put("paytype", "wxpay_app");
            }else{
                obj.put("paytype", "alipay_app");
            }

            if (!TextUtils.isEmpty(card_id)) {
                obj.put("card_id", card_id);
            }



        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }

        map.put("json", obj.toString());

        Log.i("payRequest", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "order/pay.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                int succeed;
                Log.d("payreponse", arg0.toString());
                try {
                    succeed = arg0.getJSONObject("status").getInt("succeed");

                    callback.onSuccess(arg0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onError(arg0.toString());
            }

        }, map);
        mQueue.add(request);
    }
    public void PayWX(Session session, String order_id, String card_id,String address_id, final NetCallBack<JSONObject> callback) {
        Map<String, String> map = new HashMap<String, String>();
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
            obj.put("order_id", order_id);
            obj.put("address_id",address_id);
            obj.put("paytype", "wxpay_app");
            if (!TextUtils.isEmpty(card_id)) {
                obj.put("card_id", card_id);
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }

        map.put("json", obj.toString());
        Log.d("payWXrequest", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "order/pay.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                int succeed;
                Log.d("payWXresponse", arg0.toString());
                try {
                    succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                       
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    callback.onSuccess(arg0.getJSONObject("data").getJSONObject("signparams"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onError(arg0.toString());
            }

        }, map);
        mQueue.add(request);
    }

    public void PayAli(Session session, String order_id, String card_id,String address_id, final NetCallBack<JSONObject> callback) {
        Map<String, String> map = new HashMap<>();
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
            obj.put("order_id", order_id);
            obj.put("paytype", "alipay_app");
            obj.put("address_id",address_id);
            if (!TextUtils.isEmpty(card_id)) {
                obj.put("card_id", card_id);
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }

        map.put("json", obj.toString());
        Log.i("pay ali request", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "order/pay.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                int succeed;
                Log.i("payaliresponse", arg0.toString());
                try {
                    succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {


                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    callback.onSuccess(arg0.getJSONObject("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onError(arg0.toString());
            }

        }, map);
        mQueue.add(request);
    }
}
