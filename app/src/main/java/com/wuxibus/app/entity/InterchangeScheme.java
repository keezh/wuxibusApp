package com.wuxibus.app.entity;

import com.wuxibus.app.entity.InterchangeStep;

import java.util.List;

/**
 * Created by zhongkee on 15/10/25.
 */
public class InterchangeScheme {
    private int distance;
    private int duration;
    /**
     * 这个地方是一个二维数组，由于没有注意，一直导致使用JSON解析库出错，郁闷啊。。
     * baidu坑啊，，什么数据结构，
     */
    private List<List<InterchangeStep>> steps;
    private String tip_text;

    public int totalTime;
    public int totalStops;//总站数
    public int totalMeters;//总步行数

    public InterchangeScheme(){

    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<List<InterchangeStep>> getSteps() {
        return steps;
    }

    public void setSteps(List<List<InterchangeStep>> steps) {
        this.steps = steps;
    }

    public String getTip_text() {
        return tip_text;
    }

    public void setTip_text(String tip_text) {
        this.tip_text = tip_text;
    }
}
