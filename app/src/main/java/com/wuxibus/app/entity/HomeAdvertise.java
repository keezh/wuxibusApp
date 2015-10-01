package com.wuxibus.app.entity;

/**
 * Created by zhongkee on 15/7/17.
 * "title": "aaa",
 "index_pic": "http://img.wxbus.com.cn/attachment/2015/07/1437037156.jpg",
 "url": "http://api.wxbus.com.cn/view/?m=info&id=152"
 */
public class HomeAdvertise {
    private String title;
    private String index_pic;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIndex_pic() {
        return index_pic;
    }

    public void setIndex_pic(String index_pic) {
        this.index_pic = index_pic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
