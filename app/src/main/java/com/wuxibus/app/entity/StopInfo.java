package com.wuxibus.app.entity;

/**
 * Created by zhongkee on 15/6/25.
 */
public class StopInfo {
    private int stop_limit;
    private String info;

    public StopInfo(){

    }

    public StopInfo(int stop_limit,String info){
        this.stop_limit = stop_limit;
        this.info = info;
    }

    public int getStop_limit() {
        return stop_limit;
    }

    public void setStop_limit(int stop_limit) {
        this.stop_limit = stop_limit;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
