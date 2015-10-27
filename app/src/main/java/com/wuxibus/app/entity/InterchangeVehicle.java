package com.wuxibus.app.entity;

/**
 * Created by zhongkee on 15/10/25.
 */
public class InterchangeVehicle {
    private String end_name;
    private String end_time;
    private String name;
    private String start_name;
    private String start_time;
    private int stop_num;
    private int type;


    public String getEnd_name() {
        return end_name;
    }

    public void setEnd_name(String end_name) {
        this.end_name = end_name;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_name() {
        return start_name;
    }

    public void setStart_name(String start_name) {
        this.start_name = start_name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public int getStop_num() {
        return stop_num;
    }

    public void setStop_num(int stop_num) {
        this.stop_num = stop_num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
