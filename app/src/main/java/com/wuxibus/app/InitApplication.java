package com.wuxibus.app;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;


import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.wuxibus.app.activity.MyLocationListener;
import com.wuxibus.app.util.Log4j;
import com.wuxibus.app.volley.VolleyManager;


public class InitApplication extends Application {
    public static int screenWidth;
    public static int screenHeight;
    public static String filePath;
    public static String cookieStr;
    public static Log4j appLog;
    public static int mHeaderHeight ;
    public static int position;
    public static int downCount = 0;
    //public static List<AboutUsInfo> aboutUsList;
    public static String protext;



    @Override
    public void onCreate() {
        super.onCreate();
        appLog = Log4j.Log();
        // 开启Log调试信息
        Log4j.flag = true;
        // 计算屏幕的像素
        figureScreen(getApplicationContext());
        // 获取程序的缓存路径
        filePath = getApplicationContext().getCacheDir().getPath();
        // 初始化访问类
        VolleyManager.init(getApplicationContext());
        //mHeaderHeight = getApplicationContext().getResources().getDimensionPixelSize(R.dimen.move_top);
//百度地图初始化
        SDKInitializer.initialize(getApplicationContext());

        //leanCloud
        AVOSCloud.initialize(this, "22uyrx9gaw6c9onifkjpncy3qcjt8ogruvi42yh4r57tcqab",
                "isgqsuk3mlwz4dljc0pg3pkedlgrv3eaacwdollv17jagr9s");
        AVAnalytics.enableCrashReport(this, true);


    }




    public static void figureScreen(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }





}
