package com.wuxibus.app.entity;

/**
 * Created by zhongkee on 15/7/17.
 * "temp": "24",
 "weather_pic": "F01A",
 "weather": "小到中雨",
 "wind": "东南风3-4级",
 "temperature": "27 ~ 23℃"
 */
public class Weather {
    private String temp;
    private String weather_pic;
    private String weather;
    private String wind;
    private String temperature;
    private String weather_pic_ios;

    public String getWeather_pic_ios() {
        return weather_pic_ios;
    }

    public void setWeather_pic_ios(String weather_pic_ios) {
        this.weather_pic_ios = weather_pic_ios;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWeather_pic() {
        return weather_pic;
    }

    public void setWeather_pic(String weather_pic) {
        this.weather_pic = weather_pic;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
