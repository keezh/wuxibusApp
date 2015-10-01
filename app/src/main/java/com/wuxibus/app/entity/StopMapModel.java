package com.wuxibus.app.entity;

import java.util.List;

/**
 * Created by zhongkee on 15/8/30.
 */
public class StopMapModel {
    private static StopMapModel instance;

    private List<StopInfoMap> list;

    private StopMapModel(){

    }
    public static StopMapModel getInstance(){
        if(instance == null){
            instance = new StopMapModel();
        }
        return instance;

    }

    public static void setInstance(StopMapModel instance) {
        StopMapModel.instance = instance;
    }

    public List<StopInfoMap> getList() {
        return list;
    }

    public void setList(List<StopInfoMap> list) {
        this.list = list;
    }
}
