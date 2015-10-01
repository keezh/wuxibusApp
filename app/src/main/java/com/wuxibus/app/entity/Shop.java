package com.wuxibus.app.entity;

import java.util.List;

/**
 * Created by zhongkee on 15/7/12.
 */
public class Shop {

    private int id;

    private String url;
    private String title;
    private String sjadd;
    private String index_pic;
    private List<String> flags;

    public Shop(){

    }

    public Shop(String title,String sjadd,String index_pic,List<String> flags){
        this.title = title;
        this.sjadd = sjadd;
        this.index_pic = index_pic;
        this.flags = flags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSjadd() {
        return sjadd;
    }

    public void setSjadd(String sjadd) {
        this.sjadd = sjadd;
    }

    public String getIndex_pic() {
        return index_pic;
    }

    public void setIndex_pic(String index_pic) {
        this.index_pic = index_pic;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }
}
