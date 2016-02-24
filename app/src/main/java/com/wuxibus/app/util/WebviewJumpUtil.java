package com.wuxibus.app.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.umeng.fb.FeedbackAgent;
import com.wuxibus.app.activity.InterchangeResultActivity;
import com.wuxibus.app.activity.LineRealActivity;
import com.wuxibus.app.activity.SearchLineActivity;
import com.wuxibus.app.activity.SearchLineResultActivity;
import com.wuxibus.app.activity.SearchStopActivity;
import com.wuxibus.app.activity.SearchStopResultActivity;
import com.wuxibus.app.activity.WebViewActivity;
import com.wuxibus.app.entity.GPS;
import com.wuxibus.app.entity.InterchangeSearch;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kee on 1/7/16.
 *wxbusapp://home/  : 首页

 wxbusapp://line/ 或者 wxbusapp://line/search : 线路搜索

 wxbusapp://line/detail/?line_name=&direction=&stop_seq= : 线路详情

 wxbusapp://stop/ 或者 wxbusapp://stop/search : 站台搜索

 wxbusapp://stop/detail?stop_name=&stop_id= : 站台详情

 wxbusapp://stop/nearby?lat=&lng=&place_name : 附近站台

 wxbusapp://webs/?url= : 打开内部浏览器

 wxbusapp://feedback/ : 反馈

 wxbusapp://transfer/schems?start_address=&start_coordiante=&end_address=&end_coordinate= : 直接到换乘结果

 wxbusapp://transfer/goto?end_address=&end_coordinate= : 到换乘结果页面，起始地为用户当前坐标

 wxbusapp://transfer/?start_address=&start_coordinate=&end_address=&end_coordinate= : 到换乘搜索的页面，会填好地址和坐标
 *
 */
public class WebviewJumpUtil {

    public static String Home = "wxbusapp://home/";
    public static String Line = "wxbusapp://line/";
    public static String LineSearch = "wxbusapp://line/search";
    public static String LineDetail = "wxbusapp://line/detail";
    public static String Stop = "wxbusapp://stop/";
    public static String StopSearch = "wxbusapp://stop/search";
    public static String StopDetail = "wxbusapp://stop/detail";
    public static String StopNearby = "wxbusapp://stop/nearby";
    public static String Webs = "wxbusapp://webs";
    public static String Feedback = "wxbusapp://feedback";
    public static String TransferSchems = "wxbusapp://transfer/schems";
    public static String TransferGoto = "wxbusapp://transfer/goto";
    public static String TransferStart = "wxbusapp://transfer/";


    /**
     * 该函数仅仅用于webview loadUrl中
     * @param url
     * @param activity
     * @return
     */
    public static boolean jumpTo(String url,Activity activity){

        try {
            String urlDecoder = URLDecoder.decode(url,"UTF-8");

            String urlPage = getUrlPage(urlDecoder);
            Map<String,String>params = getParams(urlDecoder);
            if(urlPage != null && !urlPage.equals("") && !urlPage.startsWith("wxbusapp://")){//当不是这个开头的url，就直接忽略
                return false;
            }

            if (urlPage.equals(Home)){
                //Intent intent = new Intent(activity,)

            }else if(urlPage.startsWith(LineSearch) || urlPage.equals(Line)){
                Intent intent = new Intent(activity, SearchLineActivity.class);
                activity.startActivity(intent);

            }else if(urlPage.startsWith(LineDetail)){
                Intent intent = new Intent(activity, SearchLineResultActivity.class);
                intent.putExtra("lineName",params.get("line_name"));
                intent.putExtra("direction",params.get("direction"));
                intent.putExtra("stop_seq",params.get("stop_seq"));
                activity.startActivity(intent);
            }else if(urlPage.startsWith(StopSearch) || urlPage.equals(Stop)){

                Intent intent = new Intent(activity, SearchStopActivity.class);
                activity.startActivity(intent);
            }else if(urlPage.startsWith(StopDetail)){
                Intent intent = new Intent(activity, SearchStopResultActivity.class);
                intent.putExtra("stop_name",params.get("stop_name"));
                intent.putExtra("stop_id",params.get("stop_id"));
                activity.startActivity(intent);
            }else if(urlPage.startsWith(StopNearby)){

            }else if(urlPage.startsWith(Webs)){
                String internUrl = params.get("url");
                Uri uri = Uri.parse(internUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                activity.startActivity(intent);

            }else if(urlPage.startsWith(Feedback)){
                FeedbackAgent agent = new FeedbackAgent(activity);
                agent.startFeedbackActivity();

            }else if(urlPage.startsWith(TransferSchems)){//
                if (params!= null){
                    String start_coordinate = params.get("start_coordinate");
                    String end_coordinate = params.get("end_coordinate");
                    String []points = end_coordinate.split(",");
                    String startPoints[] = start_coordinate.split(",");
                    if(points != null && points.length == 2 && start_coordinate == null){
                        Intent intent = new Intent(activity, InterchangeResultActivity.class);
                        InterchangeSearch.sourceInfo = new PoiInfo();
                        InterchangeSearch.sourceInfo.location = new LatLng(GPS.latitude,GPS.longitude);
                        InterchangeSearch.destinationInfo = new PoiInfo();
                        InterchangeSearch.destinationInfo.location = new LatLng(Double.parseDouble(points[0]),Double.parseDouble(points[1]));
                        activity.startActivity(intent);
                    }else if(startPoints != null){
                        Intent intent = new Intent(activity, InterchangeResultActivity.class);
                        InterchangeSearch.sourceInfo = new PoiInfo();
                        InterchangeSearch.sourceInfo.location = new LatLng(Double.parseDouble(startPoints[0]),Double.parseDouble(startPoints[1]));
                        InterchangeSearch.destinationInfo = new PoiInfo();
                        InterchangeSearch.destinationInfo.location = new LatLng(Double.parseDouble(points[0]),Double.parseDouble(points[1]));
                        activity.startActivity(intent);
                    }

                }

            }else if(urlPage.startsWith(TransferGoto) || urlPage.equals(TransferStart)){
                if (params!= null){
                    String start_coordinate = params.get("start_coordiante");//服务器错误
                    String end_coordinate = params.get("end_coordinate");
                    String []points = null ;
                    String startPoints[] = null;
                    if (end_coordinate != null)
                        points = end_coordinate.split(",");
                    if (start_coordinate != null)
                        startPoints = start_coordinate.split(",");
                    if(points != null && points.length == 2 && start_coordinate == null){
                        Intent intent = new Intent(activity, InterchangeResultActivity.class);
                        InterchangeSearch.sourceInfo = new PoiInfo();
                        InterchangeSearch.sourceInfo.location = new LatLng(GPS.latitude,GPS.longitude);
                        InterchangeSearch.destinationInfo = new PoiInfo();
                        InterchangeSearch.destinationInfo.location = new LatLng(Double.parseDouble(points[0]),Double.parseDouble(points[1]));
                        activity.startActivity(intent);
                    }else if(startPoints != null){
                        Intent intent = new Intent(activity, InterchangeResultActivity.class);
                        InterchangeSearch.sourceInfo = new PoiInfo();
                        InterchangeSearch.sourceInfo.location = new LatLng(Double.parseDouble(startPoints[0]),Double.parseDouble(startPoints[1]));
                        InterchangeSearch.destinationInfo = new PoiInfo();
                        InterchangeSearch.destinationInfo.location = new LatLng(Double.parseDouble(points[0]),Double.parseDouble(points[1]));
                        activity.startActivity(intent);
                    }

                }

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return true;

    }

    /**
     * 推送调整，逻辑函数
     * @param url
     * @param activity
     * @return
     */
    public static boolean jumpFromPush(String url,Activity activity){
        if(url == null && url.trim().equals("")){
           return false;
        }
        if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("https")){
            Intent intent = new Intent(activity, WebViewActivity.class);
            intent.putExtra("url",url);
            intent.putExtra("title","");
            activity.startActivity(intent);
        }else{
            jumpTo(url,activity);
        }

        return true;
    }


    /**
     * 解析出url请求的路径，包括页面
     * @param strURL url地址
     * @return url路径
     */
    public static String getUrlPage(String strURL)
    {
        String strPage=strURL;
        String[] arrSplit=null;
        strURL=strURL.trim().toLowerCase();
        arrSplit=strURL.split("[?]");
        if(strURL.length()>0)
        {
            if(arrSplit.length>1)
            {
                if(arrSplit[0]!=null)
                {
                    strPage=arrSplit[0];
                }
            }
        }
        return strPage;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL)
    {
        String strAllParam=null;
        String[] arrSplit=null;
        strURL=strURL.trim().toLowerCase();
        arrSplit=strURL.split("[?]");
        if(strURL.length()>1)
        {
            if(arrSplit.length>1)
            {
                if(arrSplit[1]!=null)
                {
                    strAllParam=arrSplit[1];
                }
            }
        }
        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> getParams(String URL)
    {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit=null;
        String strUrlParam=TruncateUrlPage(URL);
        if(strUrlParam==null)
        {
            return mapRequest;
        }
//每个键值为一组
        arrSplit=strUrlParam.split("[&]");
        for(String strSplit:arrSplit)
        {
            String[] arrSplitEqual=null;
            arrSplitEqual= strSplit.split("[=]");
//解析出键值
            if(arrSplitEqual.length>1)
            {
//正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            }
            else
            {
                if(arrSplitEqual[0]!="")
                {
//只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }


}
