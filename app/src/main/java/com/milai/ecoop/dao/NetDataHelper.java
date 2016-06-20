package com.milai.ecoop.dao;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.milai.ecoop.Cookie;
import com.milai.ecoop.bean.Address;
import com.milai.ecoop.bean.Banner;
import com.milai.ecoop.bean.Category;
import com.milai.ecoop.bean.GoodInfo;
import com.milai.ecoop.bean.Order;
import com.milai.ecoop.bean.Session;
import com.milai.ecoop.bean.Team;
import com.milai.ecoop.bean.Ticket;
import com.milai.ecoop.bean.User;
import com.milai.ecoop.bean.VersionInfo;
import com.milai.ecoop.utils.CommonUtils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class NetDataHelper {
    private static NetDataHelper dataHelper;
    private static RequestQueue mQueue;
    private static Gson gson;
    private Context context;
    public static String domain = "http://ecoop.szdod.com/rest/";
    public static String imgdomain = "http://ecoop.szdod.com/static/";

    private NetDataHelper(Context context) {
        mQueue = Volley.newRequestQueue(context);
        gson = new Gson();
        this.context = context;
    }

    public static synchronized NetDataHelper getInstance(Context context) {
        if (dataHelper == null)
            dataHelper = new NetDataHelper(context.getApplicationContext());
        return dataHelper;
    }

    public void getCities(final NetCallBack2<List<String>, Map<String, String>> callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(domain + "city.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("get cities response", arg0.toString());
                try {
                    int succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    Map<String, String> map = new HashMap<>();
                    List<String> list = new ArrayList<>();
                    JSONArray array = arg0.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        list.add(obj.getString("name"));
                        map.put(obj.getString("name"), obj.getString("id"));
                    }
                    callback.onSuccess(list, map);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onError(arg0.toString());
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    public void getStartpage(final NetCallBack<String> callback) {

        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        NormalPostRequest request = new NormalPostRequest(domain + "startpage.php",
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject arg0) {
                        Log.d("startpage", arg0.toString());
                        try {
                            int succeed = arg0.getJSONObject("status").getInt("succeed");
                            if (succeed == 0) {
                                callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                                return;
                            }
                            callback.onSuccess(imgdomain + arg0.getJSONObject("data").getString("image"));
                        } catch (Exception e) {
                            callback.onError(arg0.toString());
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

    public void getCategory(final NetCallBack<List<Category>> callback){
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        NormalPostRequest request=new NormalPostRequest(domain + "category.php", new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("categoryresponse", response.toString());
                try {
                    int succeed = response.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(response.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    List<Category> list = gson.fromJson(response.getJSONArray("data").toString(),
                            new TypeToken<List<Category>>() {
                            }.getType());
                    callback.onSuccess(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        },map);
        mQueue.add(request);
    }

    public void sendVerify(String mobile, String city_id, final NetCallBack<String> callback) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("city_id", city_id);
        map.put("mobile", mobile);
        Log.d("sendverifyrequest", map + "");
        NormalPostRequest request = new NormalPostRequest(domain + "account/sendverify.php",
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject arg0) {
                        Log.d("send verify response", arg0.toString());
                        try {
                            int succeed = arg0.getJSONObject("status").getInt("succeed");
                            if (succeed == 0) {
                                callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                                return;
                            }
                            callback.onSuccess(arg0.getJSONObject("data").getString("verify"));
                        } catch (Exception e) {
                            callback.onError(arg0.toString());
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

    public void login(String mobile, String verify, final NetCallBack<User> callback) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("verify", verify);
        map.put("mobile", mobile);
        Log.d("login request", map + "");
        NormalPostRequest request = new NormalPostRequest(domain + "account/login.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("login response", arg0.toString());
                try {
                    int succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    User user = gson.fromJson(arg0.getJSONObject("data").getJSONObject("user").toString(), User.class);
                    Session session = gson.fromJson(arg0.getJSONObject("data").getJSONObject("session").toString(),
                            Session.class);
                    Cookie.saveSession(context, session);
                    Cookie.saveUser(context, user);
                    Cookie.user = user;
                    Cookie.addLoginTimes(context,arg0.getJSONObject("data").getInt("isfirst"));
                    Cookie.setAlias(context, session.getUid());
                    callback.onSuccess(user);
                } catch (Exception e) {
                    callback.onError(arg0.toString());
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

    public void loginSSO(String type, String name, String gender, String mkey, String avatar, String open_id, String city_id, final NetCallBack<User> callback) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("type", type);
        map.put("name", name);
        if (gender.equals("1")) {
            gender = "M";
        } else if (gender.equals("2")) {
            gender = "F";
        }
        map.put("gender", gender);
        map.put("mkey", mkey);
        map.put("avatar", avatar);
        map.put("open_id", open_id);
        map.put("city_id", city_id);
        Log.d("login sso request", "" + map);
        NormalPostRequest request = new NormalPostRequest(domain + "account/loginsso.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("login sso response", arg0.toString());
                try {
                    int succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        Log.d("zcz", "失败");
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    Log.d("zcz", "成功");
                    User user = gson.fromJson(arg0.getJSONObject("data").getJSONObject("user").toString(), User.class);
                    Session session = gson.fromJson(arg0.getJSONObject("data").getJSONObject("session").toString(),
                            Session.class);
                    Cookie.saveSession(context, session);
                    Cookie.saveUser(context, user);
                    Cookie.user = user;
                    Cookie.setAlias(context, session.getUid());
                    Cookie.addLoginTimes(context, arg0.getJSONObject("data").getInt("isfirst"));;
                    JPushInterface.setAlias(context, session.getUid(), new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {

                        }
                    });
                    callback.onSuccess(user);
                } catch (Exception e) {
                    callback.onError(arg0.toString());
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

    public void bindMobile(Session session, String mobile, String verify, String city_id, final NetCallBack<String> callback) {
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
            obj.put("mobile", mobile);
            obj.put("verify", verify);
            obj.put("city_id", city_id);
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        Map<String, String> map = new HashMap<>();
        map.put("json", obj.toString());
        Log.d("bind mobile request", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "account/bindmobile.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("bind mobile response", arg0.toString());
                try {
                    int succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    String phonenum = arg0.getJSONObject("data").getString("mobile");
                    callback.onSuccess(phonenum);
                    Log.d("zcz", phonenum);
                } catch (Exception e) {
                    callback.onError(arg0.toString());
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

    public void updatelogin(final Session session, final NetCallBack<User> callback) {
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }

        Map<String, String> map = new HashMap<>();
        map.put("json", obj.toString());
        Log.d("json",obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "account/mycenter.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("mycenter", arg0.toString());
                try {
                    int succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        Log.d("mycenter", "error");
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    Log.d("mycenter", "succeed");
                    User user = gson.fromJson(arg0.getJSONObject("data").getJSONObject("user").toString(), User.class);

                    callback.onSuccess(user);

                    Cookie.saveSession(context, session);
                    Cookie.saveUser(context, user);
                    Cookie.user = user;

                    Cookie.addLoginTimes(context, arg0.getJSONObject("data").getInt("isfirst"));
                    Cookie.setAlias(context, session.getUid());
                    JPushInterface.setAlias(context, session.getUid(), new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {

                        }
                    });

                } catch (Exception e) {

                    callback.onError(arg0.toString());
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

    public void getBanners(final NetCallBack<List<Banner>> callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(domain + "news.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("get banners response", arg0.toString());
                try {
                    int succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }

                    List<Banner> list = gson.fromJson(arg0.getJSONArray("data").toString(),
                            new TypeToken<List<Banner>>() {
                            }.getType());
                    callback.onSuccess(list);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                callback.onError(arg0.toString());
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    public void getHomePageTeams(String cityi_id, final NetCallBack<List<Team>> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("city_id", cityi_id);
        Log.d("get homepage request", "" + map);
        NormalPostRequest request = new NormalPostRequest(domain + "index.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("get homepage response", arg0.toString());
                try {
                    int succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    List<Team> list = gson.fromJson(arg0.getJSONArray("data").toString(), new TypeToken<List<Team>>() {
                    }.getType());
                    callback.onSuccess(list);
                } catch (Exception e) {
                    callback.onError(arg0.toString());
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
    public void getHomeIntoPageTeams(String city_id, String group_id,final NetCallBack<List<Team>> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("city_id", city_id);
        map.put("group_id",group_id);
        Log.d("get homepage request", "" + map);
        NormalPostRequest request = new NormalPostRequest(domain + "index.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("get homepage response", arg0.toString());
                try {
                    int succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    List<Team> list = gson.fromJson(arg0.getJSONArray("data").toString(), new TypeToken<List<Team>>() {
                    }.getType());
                    callback.onSuccess(list);
                } catch (Exception e) {
                    callback.onError(arg0.toString());
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

    public void getTeamDetail(Session session, String id, final NetCallBack<JSONObject> callback) {
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
            obj.put("id", id);
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        Map<String, String> map = new HashMap<>();
        map.put("json", obj.toString());
        Log.d("getteamdetailrequest", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "team.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("team detail response", arg0.toString());
                try {
                    int succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    callback.onSuccess(arg0.getJSONObject("data"));
                } catch (Exception e) {
                    callback.onError(arg0.toString());
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
    public void getLogistics(Session session,String id,final NetCallBack<JSONObject> callback){
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
            obj.put("id", id);
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        Map<String, String> map = new HashMap<>();
        map.put("json", obj.toString());
        Log.d("getLogistics", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "order/logistics.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("logistics", arg0.toString());
                try {
                    int succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    callback.onSuccess(arg0.getJSONObject("data"));
                } catch (Exception e) {
                    callback.onError(arg0.toString());
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
    public void getOrders(Session session, int page, int count, String state, final NetCallBack<JSONObject> callback) {
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
            obj.put("s", state);
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("json", obj.toString());
        Log.d("getorderrequest", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "order/index.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("get orders response", arg0.toString());
                int succeed;
                try {
                    succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    callback.onSuccess(arg0.getJSONObject("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                callback.onError(arg0.toString());
            }

        }, map);
        mQueue.add(request);
    }


    public void getMyGroups(Session session, int page, int count, final NetCallBack<JSONObject> callback) {
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
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("json", obj.toString());
        Log.d("my groups request", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "account/groups.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("my groups response", arg0.toString());
                int succeed;
                System.out.println(arg0.toString());
                try {
                    succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    System.out.println(arg0.getJSONObject("data").toString());
                    callback.onSuccess(arg0.getJSONObject("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
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

    public void getGroupDetail(Session session, String id, final NetCallBack<JSONObject> callback) {
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
            obj.put("id", id);
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("json", obj.toString());
        Log.d("group detail request", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "account/group.php", new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                Log.d("group detail response", arg0.toString());
                int succeed;
                try {
                    succeed = arg0.getJSONObject("status").getInt("succeed");
                    if (succeed == 0) {
                        callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                        return;
                    }
                    System.out.println(arg0.getJSONObject("data").toString());
                    callback.onSuccess(arg0.getJSONObject("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
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

    public void getAddress(Session session, final NetCallBack<List<Address>> callback) {
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        if (session != null) {
            try {
                s.put("sid", session.getSid());
                s.put("uid", session.getUid());
                obj.put("session", s);
            } catch (JSONException e1) {
                e1.printStackTrace();
                callback.onError(e1.getMessage());
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("json", obj.toString());
            Log.d("get address request", obj.toString());
            NormalPostRequest request = new NormalPostRequest(domain + "account/setaddress.php",
                    new Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject arg0) {
                            Log.d("get address response", arg0.toString());
                            int succeed;
                            try {
                                succeed = arg0.getJSONObject("status").getInt("succeed");
                                if (succeed == 0) {
                                    callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                                    return;
                                }
                                List<Address> list = gson.fromJson(arg0.getJSONArray("data").toString(),
                                        new TypeToken<List<Address>>() {
                                        }.getType());
                                callback.onSuccess(list);
                            } catch (JSONException e) {
                                e.printStackTrace();
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

    public void addAddress(final Session session, final Address info, final NetCallBack<List<Address>> callback) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, domain + "account/setaddress.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("add address response", response);
                        callback.onSuccess(null);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callback.onError(error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject object = new JSONObject();
                JSONObject s = new JSONObject();
                try {
                    s.put("sid", session.getSid());
                    s.put("uid", session.getUid());
                    object.put("act", "add");
                    object.put("session", s);
                    object.put("provinceId", info.getProvince());
                    object.put("cityId", info.getCity());
                    object.put("areaId", info.getArea());
                    object.put("adinfo", info.getStreet());
                    object.put("realname", info.getName());
                    object.put("mobile", info.getMobile());
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }
                map.put("json", object.toString());
                Log.d("add address request", object.toString());
                return map;
            }
        };
        mQueue.add(stringRequest);
    }

    public void editAddress(final Session session, final Address info, final NetCallBack<List<Address>> callback) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, domain + "account/setaddress.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("add address response", response);
                        callback.onSuccess(null);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callback.onError(error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject object = new JSONObject();
                JSONObject s = new JSONObject();
                try {
                    s.put("sid", session.getSid());
                    s.put("uid", session.getUid());
                    object.put("act", "edit");
                    object.put("session", s);
                    object.put("id", info.getId());
                    object.put("provinceId", info.getProvince());
                    object.put("cityId", info.getCity());
                    object.put("areaId", info.getArea());
                    object.put("adinfo", info.getStreet());
                    object.put("realname", info.getName());
                    object.put("mobile", info.getMobile());
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }
                Log.d("edit address request", object.toString());
                map.put("json", object.toString());
                return map;
            }
        };
        mQueue.add(stringRequest);
    }
    public void delAddress(final Session session, final Address info, final NetCallBack<List<Address>> callback) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, domain + "account/setaddress.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("add address response", response);
                        callback.onSuccess(null);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callback.onError(error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject object = new JSONObject();
                JSONObject s = new JSONObject();
                try {
                    s.put("sid", session.getSid());
                    s.put("uid", session.getUid());
                    object.put("act", "delete");
                    object.put("session", s);
                    object.put("id", info.getId());
                    object.put("provinceId", info.getProvince());
                    object.put("cityId", info.getCity());
                    object.put("areaId", info.getArea());
                    object.put("adinfo", info.getStreet());
                    object.put("realname", info.getName());
                    object.put("mobile", info.getMobile());
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }
                Log.d("edit address request", object.toString());
                map.put("json", object.toString());
                return map;
            }
        };
        mQueue.add(stringRequest);
    }
    public void setDefultAddress(final Session session, final Address info, final NetCallBack<List<Address>> callback) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, domain + "account/setaddress.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("add address response", response);
                        callback.onSuccess(null);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callback.onError(error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject object = new JSONObject();
                JSONObject s = new JSONObject();
                try {
                    s.put("sid", session.getSid());
                    s.put("uid", session.getUid());
                    object.put("act", "default");
                    object.put("session", s);
                    object.put("id", info.getId());
                    object.put("provinceId", info.getProvince());
                    object.put("cityId", info.getCity());
                    object.put("areaId", info.getArea());
                    object.put("adinfo", info.getStreet());
                    object.put("realname", info.getName());
                    object.put("mobile", info.getMobile());
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }
                Log.d("default address request", object.toString());
                map.put("json", object.toString());
                return map;
            }
        };
        mQueue.add(stringRequest);
    }
    public void checkOrder(Session session, String id, final NetCallBack2<List<Ticket>, Order> callback) {
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
            obj.put("id", id);
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        Map<String, String> map = new HashMap<>();
        map.put("json", obj.toString());
        Log.d("check order request", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "order/check.php",
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject arg0) {
                        Log.d("check order response", arg0.toString());
                        int succeed;
                        try {
                            succeed = arg0.getJSONObject("status").getInt("succeed");
                            if (succeed == 0) {
                                callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                                return;
                            }
                            List<Ticket> list = gson.fromJson(arg0.getJSONObject("data").getJSONArray("cards").toString(),
                                    new TypeToken<List<Ticket>>() {
                                    }.getType());
                            Order o = gson.fromJson(arg0.getJSONObject("data").getJSONObject("order").toString(), Order.class);
                            callback.onSuccess(list, o);
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    public void getNotices(Session session, int page, int count, final NetCallBack<List<Map<String, String>>> callback) {
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        JSONObject p = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            p.put("page", page);
            p.put("count", count);
            obj.put("session", s);
            obj.put("pagination", p);
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        final Map<String, String> map = new HashMap<>();
        map.put("json", obj.toString());
        Log.d("get notice request", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "account/pushlog.php",
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject arg0) {
                        Log.d("get notice response", arg0.toString());
                        int succeed;
                        try {
                            succeed = arg0.getJSONObject("status").getInt("succeed");
                            if (succeed == 0) {
                                callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                                return;
                            }
                            List<Map<String, String>> list = new ArrayList<>();
                            JSONArray array = arg0.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                Map<String, String> m = new HashMap<>();
                                m.put("content", o.getString("content"));
                                m.put("createtime", o.getString("createtime"));
                                list.add(m);
                            }
                            callback.onSuccess(list);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
    public void delNotices(Session session, final NetCallBack<String> callback) {
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        JSONObject p = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
            obj.put("act", "del");
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        final Map<String, String> map = new HashMap<>();
        map.put("json", obj.toString());
        Log.d("delnoticerequest", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "account/pushlog.php",
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject arg0) {
                        Log.d("delnoticeresponse", arg0.toString());
                        int succeed;
                        try {
                            succeed = arg0.getJSONObject("status").getInt("succeed");
                            if (succeed == 0) {
                                callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                                return;
                            }

                            callback.onSuccess("");
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    public void deleteOrder(Session session, String order_id, final NetCallBack<String> callback) {
        JSONObject obj = new JSONObject();
        JSONObject s = new JSONObject();
        try {
            s.put("sid", session.getSid());
            s.put("uid", session.getUid());
            obj.put("session", s);
            obj.put("id", order_id);
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onError(e1.getMessage());
        }
        final Map<String, String> map = new HashMap<>();
        map.put("json", obj.toString());
        Log.d("delete order request", obj.toString());
        NormalPostRequest request = new NormalPostRequest(domain + "order/delorder.php",
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject arg0) {
                        Log.d("delete order response", arg0.toString());
                        int succeed;
                        try {
                            succeed = arg0.getJSONObject("status").getInt("succeed");
                            if (succeed == 0) {
                                callback.onError(arg0.getJSONObject("status").getString("error_desc"));
                                return;
                            }
                            callback.onSuccess("");
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    /**
     * 获取版本信息，无返回值，通过回调拿到版本信息类
     * @param callBack
     */
    public void getVersion(final NetCallBack<VersionInfo> callBack) {

        Map map = new HashMap();
        final NormalPostRequest request = new NormalPostRequest(domain+"android.php", new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject status = response.getJSONObject("status");
                    if (status.getInt("succeed") == 1) {
                        VersionInfo data = gson.fromJson(response.getJSONObject("data").toString()
                                , VersionInfo.class);
                        callBack.onSuccess(data);
                    }
                } catch (JSONException e) {
                        callBack.onError("连接出错");
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //暂不做处理
                callBack.onError("连接出错");
            }
        }, map);
        mQueue.add(request);
    }
}
