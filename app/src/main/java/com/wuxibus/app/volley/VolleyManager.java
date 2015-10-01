package com.wuxibus.app.volley;

import android.animation.ObjectAnimator;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.wuxibus.app.InitApplication;
import com.wuxibus.app.constants.AllConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author lizhen 2014-4-11上午10:14:21 lizhen@touch-spring.com volley网络框架请求工具
 */
public class VolleyManager {
    /**
     * Volley请求队列
     */
    private static RequestQueue requestQueue;
    /**
     * Volley图片加载类
     */
    private static ImageLoader imageLoader;

    /**
     * @param
     * @author lizhen 2014-4-11上午11:03:59 lizhen@touch-spring.com
     * 初始化volley，最好再Application中调用
     */
    public static void init(Context application) {
        if (requestQueue == null) {
            if (!(application instanceof Application)) {
                throw new RuntimeException(
                        "请在Application中调用VolleyManager.init()");
            }
            // 初始化文件管理
            VolleyFileManager.init(application);
            requestQueue = VolleyRequestQueue.getCacheQueue();
            //requestQueue = VolleyRequestQueue.getDefaultQueue(application);
            imageLoader = new ImageLoader(requestQueue,
                    BitmapCache.getInstern());
        }
    }

    /**
     * @return
     * @author lizhen 2014-4-11下午2:18:25 lizhen@touch-spring.com 获取图片加载类
     */
    public static ImageLoader getImageLoader() {
        if (requestQueue == null) {
            throw new NullPointerException(
                    "requestQueue==null,请在Application中调用VolleyManager.init()");
        }
        return imageLoader;
    }

    public static void getJsonArray(String url, Map<String, String> params,
                               Listener<JSONArray> responseLintrner, ErrorListener errorLinenter) {
        if (requestQueue == null) {
            throw new NullPointerException(
                    "requestQueue==null,请在Application中调用VolleyManager.init()");
        }
        BaseRequest<JSONArray> baseRequest = new BaseRequest<JSONArray>(
                Method.POST, params, url, responseLintrner, errorLinenter) {
            @Override
            protected JSONArray parseNetworkResponseDelegate(String jsonString) {
                try{
                    return new JSONArray(jsonString);
                }catch (JSONException e){
                    Log.i("Tag","jsonString解析错误");
                    return  null;
                }

            }

//            @Override
//            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
//                return super.parseNetworkResponse(response);
//            }

//            @Override
//            protected JSONObject parseNetworkResponseDelegate(String jsonString) {
//                try {
//                    return new JSONObject(jsonString);
//                } catch (JSONException e) {
//                    Log.i("Tag", "jsonString解析错误");
//                    return null;
//                }
//            }
        };
        // baseRequest.setTag("valaisAPP");
        // 连接超时的时间设置
        baseRequest.setRetryPolicy(new DefaultRetryPolicy(AllConstants.NetConnectionTimeOut, 0, 0));
        // 开启缓存
        // baseRequest.setShouldCache(true);
        requestQueue.add(baseRequest);


        //requestQueue.start();
    }

    /**
     * @param url
     * @param params           可以为null
     * @param responseLintrner
     * @param errorLinenter
     * @author lizhen 2014-4-11下午1:18:47 lizhen@touch-spring.com 获取json数据
     */
    public static void getJson(String url, Map<String, String> params,
                               Listener<JSONObject> responseLintrner, ErrorListener errorLinenter) {
        if (requestQueue == null) {
            throw new NullPointerException(
                    "requestQueue==null,请在Application中调用VolleyManager.init()");
        }
        BaseRequest<JSONObject> baseRequest = new BaseRequest<JSONObject>(
                Method.POST, params, url, responseLintrner, errorLinenter) {

            @Override
            protected JSONObject parseNetworkResponseDelegate(String jsonString) {
                try {
                    return new JSONObject(jsonString);
                } catch (JSONException e) {
                    Log.i("Tag", "jsonString解析错误");
                    return null;
                }
            }
        };
        // baseRequest.setTag("valaisAPP");
        // 连接超时的时间设置
        baseRequest.setRetryPolicy(new DefaultRetryPolicy(AllConstants.NetConnectionTimeOut, 0, 0));
        // 开启缓存
        // baseRequest.setShouldCache(true);
        requestQueue.add(baseRequest);
        //requestQueue.start();
    }

    /**
     * @param url
     * @param params           可以为null
     * @param responseLintrner
     * @param errorLinenter
     * @author lizhen 2014-4-11下午1:18:47 lizhen@touch-spring.com 没有缓存的请求
     */
    public static void getNoCacheJson(String url, Map<String, Object> params,
                                      Listener<JSONObject> responseLintrner, ErrorListener errorLinenter) {
        if (requestQueue == null) {
            throw new NullPointerException(
                    "requestQueue==null,请在Application中调用VolleyManager.init()");
        }
        BaseNoCacheRequest<JSONObject> baseRequest = new BaseNoCacheRequest<JSONObject>(
                Method.POST, params, url, responseLintrner, errorLinenter) {

            @Override
            protected JSONObject parseNetworkResponseDelegate(String jsonString) {
                try {
                    return new JSONObject(jsonString);
                } catch (JSONException e) {
                    Log.i("Tag", "jsonString解析错误");
                    return null;
                }
            }
        };
        // baseRequest.setTag("valaisAPP");
        // 连接超时的时间设置
        baseRequest.setRetryPolicy(new DefaultRetryPolicy(AllConstants.NetConnectionTimeOut, 0, 0));
        // 开启缓存
        // baseRequest.setShouldCache(true);
        requestQueue.add(baseRequest);
        //requestQueue.start();
    }

    /**
     * @param url
     * @param params           可以为null
     * @param responseLintrner
     * @param errorLinenter
     * @author lizhen 2014-4-11下午1:19:07 lizhen@touch-spring.com 获取字符串数据
     */
    public static void getStr(String url, Map<String, String> params,
                              Listener<String> responseLintrner, ErrorListener errorLinenter) {
        if (requestQueue == null) {
            throw new NullPointerException(
                    "requestQueue==null,请在Application中调用VolleyManager.init()");
        }
        BaseRequest<String> baseRequest = new BaseRequest<String>(Method.POST,
                params, url, responseLintrner, errorLinenter) {

            @Override
            protected String parseNetworkResponseDelegate(String jsonString) {
                try {
                    return jsonString;
                } catch (Exception e) {
                    return null;
                }
            }
        };
        // baseRequest.setTag("valaisAPP");
        // 连接超时的时间设置
        baseRequest.setRetryPolicy(new DefaultRetryPolicy(AllConstants.NetConnectionTimeOut, 0, 0));
        // 开启缓存
        // baseRequest.setShouldCache(true);
        requestQueue.add(baseRequest);
       // requestQueue.start();  bug,不用加此，Android 5.0报错
    }

    /**
     * @param imageView
     * @param imgUrl
     * @param imgDefaul
     * @author lizhen 2014-4-15上午10:01:48 lizhen@touch-spring.com 加载图片
     */
    public static void loadImage(final ImageView imageView, String imgUrl,
                                 int imgDefaul) {
        //imageView.setScaleType(ScaleType.CENTER_CROP);
        imageView.setImageResource(imgDefaul);
        ImageListener listener = new ImageListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }

            @Override
            public void onResponse(ImageContainer arg0, boolean arg1) {
                Bitmap bmp = arg0.getBitmap();
                if (bmp != null) {
                    //imageView.setScaleType(ScaleType.CENTER_CROP);
                    new ObjectAnimator().ofFloat(imageView, "alpha", 0.3f, 1.0f).setDuration(350).start();
                    imageView.setImageBitmap(bmp);
                }
            }
        };
        imageLoader.get(imgUrl, listener, InitApplication.screenWidth,
                InitApplication.screenHeight);

    }

    /**
     * @param imageView
     * @param imgUrl
     * @param imgDefaul
     * @author lizhen 2014-4-15上午10:01:48 lizhen@touch-spring.com 加载图片
     */
    public static void loadImage(final ImageView imageView, String imgUrl,
                                 int imgDefaul, ImageListener listener) {
        imageView.setScaleType(ScaleType.FIT_CENTER);
        imageView.setImageResource(imgDefaul);
        imageLoader.get(imgUrl, listener, InitApplication.screenWidth,
                InitApplication.screenHeight);
    }

    /**
     * @param imageView
     * @param imgUrl
     * @param imgDefaul
     * @author lizhen 2014-4-15上午10:01:48 lizhen@touch-spring.com 加载按原图显示图片
     */
    public static void loadImageBig(final ImageView imageView, String imgUrl,
                                    int imgDefaul, ImageListener listener) {
        imageView.setScaleType(ScaleType.FIT_CENTER);
        imageView.setImageResource(imgDefaul);
        imageLoader.get(imgUrl, listener);
    }

}
