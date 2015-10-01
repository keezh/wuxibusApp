package com.wuxibus.app.entity;

/**
 * Created by zhongkee on 15/6/25.
 */
public class LiveStop {

    private String stop_name;
    private String actdatetime;
    private String stop_seq;
    private String busselfid;
    private String productid;
    private String lastBus;
    private String pic;

    public LiveStop(){

    }
    public LiveStop(String stop_name,String actdatetime,String stop_seq,String busselfid,
                    String productid,String lastBus){
        this.stop_name = stop_name;
        this.actdatetime = actdatetime;
        this.stop_seq = stop_seq;
        this.busselfid = busselfid;
        this.productid = productid;
        this.lastBus = lastBus;
    }

    public String getStop_name() {
        return stop_name;
    }

    public void setStop_name(String stop_name) {
        this.stop_name = stop_name;
    }

    public String getActdatetime() {
        return actdatetime;
    }

    public void setActdatetime(String actdatetime) {
        this.actdatetime = actdatetime;
    }

    public String getStop_seq() {
        return stop_seq;
    }

    public void setStop_seq(String stop_seq) {
        this.stop_seq = stop_seq;
    }

    public String getBusselfid() {
        return busselfid;
    }

    public void setBusselfid(String busselfid) {
        this.busselfid = busselfid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getLastBus() {
        return lastBus;
    }

    public void setLastBus(String lastBus) {
        this.lastBus = lastBus;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
