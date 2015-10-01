package com.wuxibus.app.entity;

import java.util.List;

/**
 * Created by zhongkee on 15/6/20.
 */
public class Route {
    private String line_id;
    private String line_title;
    private String line_code;
    private String line_name;
    private int direction;
    private String stop_start;
    private String stop_end;
    private String time_start_end;
    private List<Stop> stops;

    //
    private String stopName;

    public Route(){

    }

    public Route(String line_id,String line_code,String line_title,String line_name,
                 int direction,String stop_start,String stop_end,String time_start_end){
        this.line_id = line_id;
        this.line_code = line_code;
        this.line_title = line_title;
        this.line_name = line_name;
        this.direction = direction;
        this.stop_start = stop_start;
        this.stop_end = stop_end;
        this.time_start_end = time_start_end;
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

    public String getLine_code() {
        return line_code;
    }

    public void setLine_code(String line_code) {
        this.line_code = line_code;
    }

    public String getLine_name() {
        return line_name;
    }

    public void setLine_name(String line_name) {
        this.line_name = line_name;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
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

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    //    line_id: "35",
//    line_title: "35路",
//    line_code: "21874403",
//    line_name: "35路环形",
//    direction: 0,
//    stop_start: "新区分公司",
//    stop_end: "火车站",
//    time_start_end: "新区分公司5:30-23:00"

}
