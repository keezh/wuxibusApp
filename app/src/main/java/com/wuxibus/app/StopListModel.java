package com.wuxibus.app;

import com.wuxibus.app.entity.LiveStop;
import com.wuxibus.app.entity.Stop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/7/14.
 */
public class StopListModel {

    /**
     * 用户点击后的实时线路信息
     */
    public List<Stop> currentLiveStops = new ArrayList<Stop>();
    /* 默认该值只有，0，1，表示获取的两个方向的值*/

    public List<LiveStop> beforeLiveStops;

    /*
    *   当前车辆距离本站还有几站路
     */
    public int distance;



    public String click_stop_seq;

    private StopListModel(){

    }

    private static StopListModel instance;

     public static StopListModel newInstance() {

        if(instance == null){
            instance = new StopListModel();
        }
         return instance;
    }
}
