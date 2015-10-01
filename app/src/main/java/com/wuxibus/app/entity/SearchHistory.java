package com.wuxibus.app.entity;

/**
 * Created by zhongkee on 15/7/25.
 */
public class SearchHistory {
    private String lineName;
    private String title;
    private int type;
    private String startStop;
    private String endStop;

    public SearchHistory(String lineName,String title,int type,String startStop,String endStop){
        this.lineName = lineName;
        this.title = title;
        this.type = type;
        this.startStop = startStop;
        this.endStop = endStop;
    }


    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStartStop() {
        return startStop;
    }

    public void setStartStop(String startStop) {
        this.startStop = startStop;
    }

    public String getEndStop() {
        return endStop;
    }

    public void setEndStop(String endStop) {
        this.endStop = endStop;
    }
}
