package com.wuxibus.app.entity;

/**
 * Created by zhongkee on 15/10/25.
 *
 * "distance": 192,
 "duration": 233,
 "pois": [],
 "sname": "",
 "type": 5,
 "vehicle": null,
 "stepOriginLocation": {
 "lng": 120.36847373884,
 "lat": 31.568907881307
 },
 "stepDestinationLocation": {
 "lng": 120.36971340045,
 "lat": 31.56986162562
 },
 "stepInstruction": "步行192米",

 "vehicle": {
 "end_name": "观山路(菱湖大道)站",
 "end_time": "20:10",
 "end_uid": "b6598bade1e1fc02b13c8102",
 "name": "756路",
 "start_name": "长江北路(高浪路)站",
 "start_time": "06:40",
 "start_uid": "8d75003b2193cfcafe0eb0d8",
 "stop_num": 3,
 "total_price": 0,
 "type": 0,
 "uid": "d4750f1cd240c064210262ad",
 "zone_price": 0
 },
 *
 *
 *
 */
public class InterchangeStep implements Cloneable{
    private int distance;
    private int duration;
    private InterchangeStepLocation stepOriginLocation;
    private InterchangeStepLocation stepDestinationLocation;
    private String stepInstruction;
    private InterchangeVehicle vehicle;
    /**
     * type = 3
     * type = 5
     */
    private int type;

    public InterchangeStep(){

    }

    public InterchangeVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(InterchangeVehicle vehicle) {
        this.vehicle = vehicle;
    }



    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public InterchangeStepLocation getStepOriginLocation() {
        return stepOriginLocation;
    }

    public void setStepOriginLocation(InterchangeStepLocation stepOriginLocation) {
        this.stepOriginLocation = stepOriginLocation;
    }

    public InterchangeStepLocation getStepDestinationLocation() {
        return stepDestinationLocation;
    }

    public void setStepDestinationLocation(InterchangeStepLocation stepDestinationLocation) {
        this.stepDestinationLocation = stepDestinationLocation;
    }

    public String getStepInstruction() {
        return stepInstruction;
    }

    public void setStepInstruction(String stepInstruction) {
        this.stepInstruction = stepInstruction;
    }

    @Override
    public InterchangeStep clone() throws CloneNotSupportedException {
        return (InterchangeStep)super.clone();
    }
}
