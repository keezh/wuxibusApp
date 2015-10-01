package com.wuxibus.app.constants;

/**
 * Created by zhongkee on 15/6/20.
 */
public class AllConstants {

    /* 服务器url
    * 出现一个低级的bug，该ServerUrl必须加上最后的字符/,否则在VolleyManager框架请求中，无法返回数据
    * 其实这个/在服务器中也是一个资源，找不到请求了，就无法返回，被这个bug搞了半天，坑啊。。。
    * */

    public static int NetConnectionTimeOut = 15 * 1000;//10秒

    public static String charSet = "UTF-8";

    public static String AESKey = "1EXp6MhoWUNxPOIc";
    public static String IV = "0000000000000000";
    public static String ServerUrl = "http://api.wxbus.com.cn/api/";
    /* 线路搜索 */
    public static String RouteSearchUrl ="/?m=line_search&k=";
    //public static String

    public static String DeviceIMEI = "";

    public static String SqliteName = "line_search_history";

    public static int SEARCH_LINE_TYPE = 0;
    public static int SEARCH_STATIION_TYPE = 1;
    public static int SEARCH_PLACE_TYPE = 2;

    public static int FAV_FLAG = 0;
    public static int AROUND_FLAG = 1;

    public static String AdvertiseUrl = "get_start_pic";
//    public static String Latitude = "latitude_baidu";
    public static String Latitude = "latitude";
    public static String Longitude = "longitude";
    public static String LatitudeBaidu = "latitude_baidu";
    public static String LongitudeBaidu = "longitude_baidu";
    public static int HomeLineSize = 10;

    public static int Width;
    public static int Height;
}
