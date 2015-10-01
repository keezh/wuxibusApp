package com.wuxibus.app.entity;

/**
 * Created by zhongkee on 15/8/6.
 */
public class MyItem {
    private int imgResId;
    private String title;

    public MyItem(int imgResId,String title){
        this.imgResId = imgResId;
        this.title = title;
    }

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
