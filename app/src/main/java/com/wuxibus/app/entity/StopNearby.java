package com.wuxibus.app.entity;

import java.util.List;

/**
 * Created by zhongkee on 15/7/9.
 */
public class StopNearby {


    private String stop_name;
    private String removing;//removing
    private String line_count;
    private List<String> line_info;

    public String getStop_name() {
        return stop_name;
    }

    public void setStop_name(String stop_name) {
        this.stop_name = stop_name;
    }

    public String getRemoving() {
        return removing;
    }

    public void setRemoving(String removing) {
        this.removing = removing;
    }

    public String getLine_count() {
        return line_count;
    }

    public void setLine_count(String line_count) {
        this.line_count = line_count;
    }

    public List<String> getLine_info() {
        return line_info;
    }

    public void setLine_info(List<String> line_info) {
        this.line_info = line_info;
    }
}
