package com.wuxibus.app.entity;

import com.wuxibus.app.adapter.InterchangeSearchAdapter;

/**
 * Created by zhongkee on 15/10/8.
 */
public class InterchangeSearchHistory {
    private int id;
    private String name;
    private String latitude;
    private String longitude;

    public InterchangeSearchHistory(){

    }

    public InterchangeSearchHistory(int id,String name,String latitude,String longitude){
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
