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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * @author lizhen 2014-4-11上午9:58:37 lizhen@touch-spring.com 自定义网络请求
 */
public abstract class BaseRequest<T> extends Request<T> {
	private Listener<T> mListener;
	@SuppressWarnings("unused")
	private String postBody;
	private static final String PROTOCOL_CHARSET = "utf-8";
	private static final String PROTOCOL_CONTENT_TYPE = String.format(
			"application/x-www-form-urlencoded; charset=%s", PROTOCOL_CHARSET);
	private Map<String, String> postParams;
	private String url;

	//add by kee
	private String urlParams = "";

	public BaseRequest(int method, String url, Listener<T> mListener,
			ErrorListener listener) {
		super(method, url, listener);
		this.url = url;
		this.mListener = mListener;
		// 设置请求的不缓存
		//modify by kee
		setShouldCache(false);
	}

	public BaseRequest(int method, Map<String, String> params, String url,
			Listener<T> mListener, ErrorListener listener) {
		this(method, url, mListener, listener);
		if (params == null) {
			postBody = null;
		} else {
			postParams = params;
			//add by kee
			initUrlParams(url,params);

		}
	}

	/**
	 * urlParms 生成策略师RESTFull风格如：http://xxx.yyy.com/name/password/type
	 * 参数的风格用 “/"
	 * @param url
	 * @param params
	 */
	public void initUrlParams(String url,Map<String,String>params){
		if(params != null) {
			Set<String> keys = params.keySet();
			Iterator<String> it = keys.iterator();
			boolean isFirst = true;
			while(it.hasNext()){
				String tempParam = it.next();
				if(isFirst){
					if(!url.endsWith("/")){
						url += "/";
					}
					urlParams = url+tempParam+"/"+params.get(tempParam);
					isFirst = false;
				}else {
					urlParams += "/"+tempParam+"/"+params.get(tempParam);
				}

			}
		}


	}

	public String getUrlParams() {
		return urlParams;
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		try {
			if (postParams != null) {
				StringBuffer body = new StringBuffer();
				for (Map.Entry<String, String> m : postParams.entrySet()) {
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
			//String jsonString = getRealString(response.data);
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
		//headers.put("Accept-Encoding", "gzip,deflate");
		setCookie(headers);
		return headers;
	}

	@Override
	public String getBodyContentType() {
		return PROTOCOL_CONTENT_TYPE;
	}

	protected void deliverResponse(T result) {
		Log.i("Tag", "url========>" + url);
		if(result != null){
			Log.i("Tag", "response========>" + result.toString());
			mListener.onResponse(result);
			// 将请求获取的数据缓存到文件
//			VolleyFileManager.saveTextToFile(result.toString(),
//					String.valueOf(url.hashCode()));
			VolleyFileManager.saveTextToFile(result.toString(),
					String.valueOf(urlParams.hashCode()));
			Log.i("Tag","缓存原始文件名"+urlParams);
		}

	}

	@Override
	public void deliverError(VolleyError error) {
		// 先从文件缓存中获取
		String fileStr = VolleyFileManager.readTextFile(String.valueOf(urlParams
				.hashCode()));
		if (fileStr != null) {
			mListener.onResponse(parseNetworkResponseDelegate(fileStr));
		} else {
			Log.i("Tag", "url========>" + urlParams);
			Log.i("Tag", "error========>" + error.toString());
			super.deliverError(error);
		}
	}

	/**
	 * 设置cookieStr为全局变量
	 * 
	 * @author lizhen 2013-12-11上午9:16:54
	 * @param heads
	 */
	private void getCookie(Map<String, String> heads) {
		if (InitApplication.cookieStr != null) {
			return;
		}
		String cookie = heads.get("Set-Cookie");
		InitApplication.appLog.i("服务器得到的Cookie===>" + heads.toString());
		if (cookie != null) {
			InitApplication.cookieStr = cookie;
			InitApplication.appLog.i("服务器得到的Cookie===>"
					+ InitApplication.cookieStr);
		}
	}

	/**
	 * 给heads加上cookie
	 * 
	 * @author lizhen 2013-12-11上午9:39:27
	 * @param heads
	 */
	private void setCookie(Map<String, String> heads) {
		if (InitApplication.cookieStr == null) {
			return;
		}
		InitApplication.appLog
				.i("向请求中设置Cookie===>" + InitApplication.cookieStr);
		heads.put("Cookie", InitApplication.cookieStr);
	}

	/**
	 * 以下两个方法为了处理返回的字节流进行了GZIP压缩，进行解压缩处理
	 * by kee
	 * @param data
	 * @return
	 */
	private int getShort(byte[] data)
	{
		return (int) ((data[0] << 8) | data[1] & 0xFF);
	}

	/**
	 * by kee
	 * @param data
	 * @return
	 */
	private String getRealString(byte[] data)
	{
		byte[] h = new byte[2];
		h[0] = (data)[0];
		h[1] = (data)[1];
		int head = getShort(h);
		boolean t = head == 0x1f8b;
		InputStream in;
		StringBuilder sb = new StringBuilder();
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			if (t)
			{
				in = new GZIPInputStream(bis);
			}
			else
			{
				in = bis;
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
			for (String line = r.readLine(); line != null; line = r.readLine())
			{
				sb.append(line);
			}
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return sb.toString();
	}

	protected abstract T parseNetworkResponseDelegate(String jsonString);
}
