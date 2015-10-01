package com.wuxibus.app.entity;

import java.io.Serializable;

/**
 * Created by zhongkee on 15/6/21.
 */
public class Stop implements Cloneable,Serializable {
//    line_id: "35",
//    line_code: "21874403",
//    stop_id: "55110526161800544050",
//    stop_name: "新区分公司",
//    stop_type_id: "6",
//    stop_type_name: "主站上客站",
//    stop_seq: "1",
//    longitude: "120.393970",
//    latitude: "31.523990",
//    longitude_baidu: "120.40519967071",
//    latitude_baidu: "31.528069653714"
//    "ad_title": "1元吃烧烤",
//            "ad_url": "http://www.wxbus.com.cn/view/shop-148.html"
    private String line_id;
    private String line_code;
    private String stop_id;
    private String stop_name;
    private String stop_type_id;
    private String stop_type_name;
    private String stop_seq;
    private String longitude;
    private String latitude;
    private String longitude_baidu;
    private String latitude_baidu;
    private String ad_title;
    private String ad_url;
    //有车时候4g信号图片
    private String pic;


    //add live info
    private boolean hasBus;//表示当前站点停靠站台
    private String actdatetime;
    private String busselfid;
    private String productid;
    private boolean isSelected;

    //nearby line
    private String distance;
    private String direction;
    private String stop_start;
    private String stop_end;
    private String time_start_end;






    public Stop(){

    }
    public Stop(String line_id,String line_code,String stop_id,String stop_name,String stop_type_id
                ,String stop_type_name,String stop_seq,String longitude,String latitude,String longitude_baidu,String latitude_baidu){
        this.line_id = line_id;
        this.line_code = line_code;
        this.stop_id = stop_id;
        this.stop_name = stop_name;
        this.stop_type_id = stop_type_id;
        this.stop_type_name = stop_type_name;
        this.stop_seq = stop_seq;
        this.longitude = longitude;
        this.latitude = latitude;
        this.longitude_baidu = longitude_baidu;
        this.latitude_baidu = latitude_baidu;
    }

    public String getAd_title() {
        return ad_title;
    }

    public void setAd_title(String ad_title) {
        this.ad_title = ad_title;
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }

    public String getLine_id() {
        return line_id;
    }

    public void setLine_id(String line_id) {
        this.line_id = line_id;
    }

    public String getLine_code() {
        return line_code;
    }

    public void setLine_code(String line_code) {
        this.line_code = line_code;
    }

    public String getStop_id() {
        return stop_id;
    }

    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }

    public String getStop_name() {
        return stop_name;
    }

    public void setStop_name(String stop_name) {
        this.stop_name = stop_name;
    }

    public String getStop_type_id() {
        return stop_type_id;
    }

    public void setStop_type_id(String stop_type_id) {
        this.stop_type_id = stop_type_id;
    }

    public String getStop_type_name() {
        return stop_type_name;
    }

    public void setStop_type_name(String stop_type_name) {
        this.stop_type_name = stop_type_name;
    }

    public String getStop_seq() {
        return stop_seq;
    }

    public void setStop_seq(String stop_seq) {
        this.stop_seq = stop_seq;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude_baidu() {
        return longitude_baidu;
    }

    public void setLongitude_baidu(String longitude_baidu) {
        this.longitude_baidu = longitude_baidu;
    }

    public String getLatitude_baidu() {
        return latitude_baidu;
    }

    public void setLatitude_baidu(String latitude_baidu) {
        this.latitude_baidu = latitude_baidu;
    }

    public String getActdatetime() {
        return actdatetime;
    }

    public void setActdatetime(String actdatetime) {
        this.actdatetime = actdatetime;
    }

    public String getBusselfid() {
        return busselfid;
    }

    public void setBusselfid(String busselfid) {
        this.busselfid = busselfid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public boolean getHasBus() {
        return hasBus;
    }

    public void setHasBus(boolean hasBus) {
        this.hasBus = hasBus;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getStop_start() {
        return stop_start;
    }

    public void setStop_start(String stop_start) {
        this.stop_start = stop_start;
    }

    public String getStop_end() {
        return stop_end;
    }

    public void setStop_end(String stop_end) {
        this.stop_end = stop_end;
    }

    public String getTime_start_end() {
        return time_start_end;
    }

    public void setTime_start_end(String time_start_end) {
        this.time_start_end = time_start_end;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    //复制一个副本，必须实现此方法
    public Stop clone(){
        try {
            return (Stop)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
