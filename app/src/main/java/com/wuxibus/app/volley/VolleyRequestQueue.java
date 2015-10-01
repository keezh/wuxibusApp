package com.wuxibus.app.volley;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.File;

public class VolleyRequestQueue{
	private static RequestQueue defaulQueue;
	private static RequestQueue cacheQueue;
	
	/**
	 * @author lizhen  2014-4-11上午10:54:31 lizhen@touch-spring.com
	 * 得到一个默认没有文件缓存的请求队列的实例
	 * @param context
	 * @return
	 */
	public static RequestQueue getDefaultQueue(Context context){
		if(defaulQueue==null){
			defaulQueue= Volley.newRequestQueue(context);
			defaulQueue.start();
		}
		return defaulQueue;
	}
	
	/**
	 * @author lizhen  2014-4-11上午10:54:08 lizhen@touch-spring.com
	 * 得到一个带有文件缓存的请求队列的实例
	 * @return
	 */
	public static RequestQueue getCacheQueue(){
		if(cacheQueue==null){
			cacheQueue=creatCacheQueue();
		}
		return cacheQueue;
	}
	
	/**
	 * @author lizhen  2014-4-11上午10:53:33 lizhen@touch-spring.com
	 * 实例化一个带有文件缓存的请求队列
	 * @return
	 */
	private static RequestQueue creatCacheQueue() {
		//得到图片缓存的目录
		File file = new File(VolleyFileManager.getVolleyDirectory());
		//设置图片缓存文件大小为40M
		DiskBasedCache basedCache = new DiskBasedCache(file,80 * 1024 * 1024);
		Network network;
		if (Build.VERSION.SDK_INT > 9) {
			// 系统是2.3及其以上版本
			network = new BasicNetwork(new HurlStack());
		} else {
			String userAgent = "volley/0";// UA
			// 系统是2.3极其一下版本
			network = new BasicNetwork(new HttpClientStack(
					AndroidHttpClient.newInstance(userAgent)));
		}
		cacheQueue = new RequestQueue(basedCache, network, 25);
		cacheQueue.start();
		return cacheQueue;
	}

}
