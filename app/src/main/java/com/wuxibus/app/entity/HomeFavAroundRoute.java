package com.wuxibus.app.entity;

import android.widget.BaseAdapter;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * 首页收藏，附近线路实体类
 * Created by zhongkee on 15/7/27.
 */
public class HomeFavAroundRoute implements Response.Listener<JSONObject>{
    private String lineTitle;
    private String distance;
    private String lineId;
    private String lineName;
    private String direction;
    private String startStop;
    private String endStop;
    private String startEndTime;
    //收藏，附近两个类别
    private int type;
    //收藏的线路是否在附近列表中
    private boolean favIsHere;

    private String lineCode;
    private String stopSeq;
    private String stopName;



    //网络请求返回值
    public String realDistanceStops = "离我3站";

    public BaseAdapter adapter;

    public HomeFavAroundRoute(String lineTitle,String distance,String endStop,String lineCode,String stopSeq,int type){
        this.lineTitle = lineTitle;
        this.distance = distance;
        this.endStop = endStop;
        this.lineCode = lineCode;
        this.stopSeq = stopSeq;
        this.type = type;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public boolean isFavIsHere() {
        return favIsHere;
    }

    public void setFavIsHere(boolean favIsHere) {
        this.favIsHere = favIsHere;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getEndStop() {
        return endStop;
    }

    public void setEndStop(String endStop) {
        this.endStop = endStop;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLineTitle() {
        return lineTitle;
    }

    public void setLineTitle(String lineTitle) {
        this.lineTitle = lineTitle;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getStopSeq() {
        return stopSeq;
    }

    public void setStopSeq(String stopSeq) {
        this.stopSeq = stopSeq;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getStartStop() {
        return startStop;
    }

    public void setStartStop(String startStop) {
        this.startStop = startStop;
    }

    public String getStartEndTime() {
        return startEndTime;
    }

    public void setStartEndTime(String startEndTime) {
        this.startEndTime = startEndTime;
    }

    /**
     * {"stop_info":{"stop_limit":0,"info":"今日已经结束营运，谢谢使用。"},"list":[],"info":"今日已经结束营运，谢谢使用。"}
     * {"stop_info":{"stop_limit":0,"info":"最近一班车将于06:15:00从首站发出。"},"list":[],"info":"最近一班车将于06:15:00从首站发出。"}
     * @param jsonObject
     */
    @Override
    public void onResponse(JSONObject jsonObject) {
        try{
            String stops = jsonObject.getJSONObject("stop_info").getString("stop_limit");
            stops = "离我"+stops+"站";

            //int stopInt = Integer.parseInt(stops);
            if(jsonObject.getJSONObject("stop_info").getString("info") != null){
                if(jsonObject.getJSONObject("stop_info").getString("info").startsWith("今日已经结束营运")){
                    stops = "今日运营结束";
                }else if(jsonObject.getJSONObject("stop_info").getString("info").contains("最近一班车")){
                    stops = "尚未发车";
                }
            }


            this.realDistanceStops = stops;
            adapter.notifyDataSetChanged();

        }catch (Exception e){

        }


    }
}
