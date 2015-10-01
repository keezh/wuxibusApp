package com.wuxibus.app.entity;

/**
 * Created by zhongkee on 15/7/17.
 * "title": "赖着不走：菲律宾摸黑偷偷修理南沙\"坐滩\"破船",
 "class_name": "本地新闻",
 "sm": "1999年，菲律宾海军“马德雷山”号登陆舰企图以非法“坐滩”的形式窃占中国仁爱礁。中方近年来多次要求菲方拖走该船，但菲方未履行自己的承诺。",
 "index_pic": "http://img.wxbus.com.cn/attachment/2015/07/1436950365.jpg",
 "url": "http://api.wxbus.com.cn/view/?m=info&id=187"
 */
public class NewsDetail {
    private String title;
    private String class_name;
    private String sm;
    private String index_pic;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getSm() {
        return sm;
    }

    public void setSm(String sm) {
        this.sm = sm;
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
