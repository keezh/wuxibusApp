package com.wuxibus.app.entity;

import java.util.List;

/**
 * 收藏的线路
 * Created by zhongkee on 15/7/5.
 */
public class FavoriteRoute {

    private String line_id;
    private String line_name;
    //private List<FavoriteStop> list;
    private String lineTitle;
    private String direction;
    private String stop_start;
    private String stop_end;
    private String start_end_time;
    private String stopName;

    public FavoriteRoute(){

    }

    public String getLine_id() {
        return line_id;
    }

    public void setLine_id(String line_id) {
        this.line_id = line_id;
    }

    public String getLine_name() {
        return line_name;
    }

    public void setLine_name(String line_name) {
        this.line_name = line_name;
    }

    public String getLineTitle() {
        return lineTitle;
    }

    public void setLineTitle(String lineTitle) {
        this.lineTitle = lineTitle;
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

    public String getStart_end_time() {
        return start_end_time;
    }

    public void setStart_end_time(String start_end_time) {
        this.start_end_time = start_end_time;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
}
