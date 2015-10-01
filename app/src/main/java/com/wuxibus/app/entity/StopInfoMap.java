package com.wuxibus.app.entity;

/**
 * Created by zhongkee on 15/8/30.
 * "stop_id": "55110516160942742156",
 "stop_name": "运河饭店",
 "blongitude": "120.289327",
 "blatitude": "31.568132",
 "line_count": "8",
 "line_info": "1路,11路,41路,56路,131路,207路,211路,508路"
 */
public class StopInfoMap {
    private String stop_id;
    private String stop_name;
    private String blongitude;
    private String blatitude;
    private String line_count;
    private String line_info;

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

    public String getBlongitude() {
        return blongitude;
    }

    public void setBlongitude(String blongitude) {
        this.blongitude = blongitude;
    }

    public String getBlatitude() {
        return blatitude;
    }

    public void setBlatitude(String blatitude) {
        this.blatitude = blatitude;
    }

    public String getLine_count() {
        return line_count;
    }

    public void setLine_count(String line_count) {
        this.line_count = line_count;
    }

    public String getLine_info() {
        return line_info;
    }

    public void setLine_info(String line_info) {
        this.line_info = line_info;
    }
}
