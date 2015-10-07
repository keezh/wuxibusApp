package com.wuxibus.app.entity;

import com.wuxibus.app.adapter.InterchangeSearchAdapter;

/**
 * Created by zhongkee on 15/10/8.
 */
public class InterchangeSearchHistory {
    private int id;
    private String latitude;
    private String longitude;

    public InterchangeSearchHistory(){

    }

    public InterchangeSearchHistory(int id,String latitude,String longitude){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
