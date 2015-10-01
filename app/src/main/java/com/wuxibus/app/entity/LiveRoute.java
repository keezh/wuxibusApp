package com.wuxibus.app.entity;

import java.util.List;

/**
 * Created by zhongkee on 15/6/25.
 */
public class LiveRoute {

    private String info;
    private List<LiveStop> list;
    private StopInfo stop_info;

    public LiveRoute(){

    }

    public LiveRoute(String info,List<LiveStop> list,StopInfo stop_info){

    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<LiveStop> getList() {
        return list;
    }

    public void setList(List<LiveStop> list) {
        this.list = list;
    }

    public StopInfo getStop_info() {
        return stop_info;
    }

    public void setStop_info(StopInfo stop_info) {
        this.stop_info = stop_info;
    }
}
