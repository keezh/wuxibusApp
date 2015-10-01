package com.wuxibus.app.entity;

import android.util.Log;
import android.widget.BaseAdapter;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhongkee on 15/7/7.
 */
public class AroundRoute implements Response.Listener<JSONObject>{
//    "stop_name": "运河饭店",
//            "removing": 230,
//            "line_count": 8,
//            "line_info": [
//    "line_id": "41",
//            "line_title": "41路",
//            "stop_list": [
//    {
//        "distance": 230,
//            "line_code": "22427117",
//            "direction": 1,
//            "stop_seq": "38",
//            "stop_name": "运河饭店",
//            "stop_start": "河埒口",
//            "stop_end": "坊前",
//            "time_start_end": "河埒口6:21-21:48"
//    },
//    {
//        "distance": 333,
//            "line_code": "22427117",
//            "direction": 0,
//            "stop_seq": "30",
//            "stop_name": "无锡公交",
//            "stop_start": "坊前",
//            "stop_end": "河埒口",
//            "time_start_end": "坊前5:30-21:00"
//    }
    private String line_id;
    private String line_title;
    //该数组有两个元素，两个相反的线路信息
    private List<Stop> stop_list;
    //附近线路中，用户选择方向，使用到该变量
    private int currentIndex = 0;

    public BaseAdapter adapter;
    private String distanceStopInfo;

    public AroundRoute(){

    }

    public String getLine_id() {
        return line_id;
    }

    public void setLine_id(String line_id) {
        this.line_id = line_id;
    }

    public String getLine_title() {
        return line_title;
    }

    public void setLine_title(String line_title) {
        this.line_title = line_title;
    }

    public List<Stop> getStop_list() {
        return stop_list;
    }

    public void setStop_list(List<Stop> stop_list) {
        this.stop_list = stop_list;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public String getDistanceStopInfo() {
        return distanceStopInfo;
    }

    public void setDistanceStopInfo(String distanceStopInfo) {
        this.distanceStopInfo = distanceStopInfo;
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        //response========>{"stop_info":{"stop_limit":0,"info":"今日已经结束营运，谢谢使用。"},"list":[],"info":"今日已经结束营运，谢谢使用。"}

        try {
//            String info = jsonObject.getString("info");
//            Log.i("kee",info);
//            if (info.equals("今日已经结束营运，谢谢使用。")) {
//                distanceStopInfo = "今日运营结束";
//            } else if (info.contains("最近一班车尚未发车")) {
//                distanceStopInfo = "尚未发车";
//            }else {//正常有车发出情况
//                String stops = jsonObject.getJSONObject("stop_info").getString("stop_limit");
//                String stopName = stop_list.get(currentIndex).getStop_name();
//                distanceStopInfo = "最近一辆车离"+stopName+"还有"+stops+"站";
//            }

            String stops = jsonObject.getJSONObject("stop_info").getString("stop_limit");
            distanceStopInfo = "离我"+stops+"站";

            //int stopInt = Integer.parseInt(stops);
            if(jsonObject.getJSONObject("stop_info").getString("info") != null){
                if(jsonObject.getJSONObject("stop_info").getString("info").startsWith("今日已经结束营运")){
                    distanceStopInfo = "今日运营结束";
                }else if(jsonObject.getJSONObject("stop_info").getString("info").contains("最近一班车")){
                    distanceStopInfo = "尚未发车";
                }
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
