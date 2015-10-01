package com.wuxibus.app.volley;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.wuxibus.app.InitApplication;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lizhen 2014-4-11上午9:58:37 lizhen@touch-spring.com 自定义网络请求
 */
public abstract class BaseNoCacheRequest<T> extends Request<T> {
    private Listener<T> mListener;
    @SuppressWarnings("unused")
    private String postBody;
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE = String.format(
            "application/x-www-form-urlencoded; charset=%s", PROTOCOL_CHARSET);
    private Map<String, Object> postParams;
    private String url;

    public BaseNoCacheRequest(int method, String url, Listener<T> mListener,
                              ErrorListener listener) {
        super(method, url, listener);
        this.url = url;
        this.mListener = mListener;
        // 设置请求的不缓存
        setShouldCache(true);
    }

    public BaseNoCacheRequest(int method, Map<String, Object> params,
                              String url, Listener<T> mListener, ErrorListener listener) {
        this(method, url, mListener, listener);
        if (params == null) {
            postBody = null;
        } else {
            postParams = params;
        }
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            if (postParams != null) {
                StringBuffer body = new StringBuffer();
                for (Map.Entry<String, Object> m : postParams.entrySet()) {
                    body.append(m.getKey() + "=" + m.getValue() + "&");
                }
                if (!TextUtils.isEmpty(body.toString())) {
                    String str = body.substring(0, body.length() - 1)
                            .toString();
                    return str.getBytes(PROTOCOL_CHARSET);
                }
            }
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public String getCacheKey() {
        return super.getCacheKey();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            T result = parseNetworkResponseDelegate(jsonString);
            getCookie(response.headers);
            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.android.volley.Request#getHeaders() 设置请求头
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Charset", "UTF-8");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        setCookie(headers);
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    protected void deliverResponse(T result) {
        Log.i("Tag", "url========>" + url);
        if (result != null) {
            Log.i("Tag", "response========>" + result.toString());
            if(mListener != null){
                mListener.onResponse(result);
            }

        }
        // 将请求获取的数据缓存到文件
        VolleyFileManager.saveTextToFile(result.toString(),
                String.valueOf(url.hashCode()));
    }

    @Override
    public void deliverError(VolleyError error) {
        //先从文件缓存中获取
        String fileStr = VolleyFileManager.readTextFile(String.valueOf(url.hashCode()));
        if (fileStr != null) {
            mListener.onResponse(parseNetworkResponseDelegate(fileStr));
        } else {
            Log.i("Tag", "url========>" + url);
            Log.i("Tag", "error========>" + error.toString());
            super.deliverError(error);
        }
        super.deliverError(error);
    }

    /**
     * 设置cookieStr为全局变量
     *
     * @param heads
     * @author lizhen 2013-12-11上午9:16:54
     */
    private void getCookie(Map<String, String> heads) {
        /*
         * if (App.cookieStr != null) { return; }
		 */
        String cookie = heads.get("Set-Cookie");
        if (cookie != null) {
            InitApplication.cookieStr = cookie;
            InitApplication.appLog.i("服务器得到的Cookie===>"
                    + InitApplication.cookieStr);
        }
    }

    /**
     * 给heads加上cookie
     *
     * @param heads
     * @author lizhen 2013-12-11上午9:39:27
     */
    private void setCookie(Map<String, String> heads) {
        if (InitApplication.cookieStr == null) {
            return;
        }
        InitApplication.appLog
                .i("向请求中设置Cookie===>" + InitApplication.cookieStr);
        heads.put("Cookie", InitApplication.cookieStr);
    }

    protected abstract T parseNetworkResponseDelegate(String jsonString);
}
