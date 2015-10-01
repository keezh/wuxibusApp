package com.wuxibus.app.entity;

/**
 * Created by zhongkee on 15/7/5.
 */
public class FavoriteStop {

//    direction: 0,
//    stop_start: "新区分公司",
//    stop_end: "火车站",
//    time_start_end: "新区分公司5:30-23:00"

    private int direction;
    private String stop_start;
    private String stop_end;
    private String time_start_end;

    public FavoriteStop(){

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
}
