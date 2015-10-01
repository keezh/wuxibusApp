package com.wuxibus.app.util;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.wuxibus.app.entity.GPS;

/**
 * Created by zhongkee on 15/6/25.
 */
public class DeviceTools {

    public static  String  getDeviceIMEI(Activity activity){
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Activity.TELEPHONY_SERVICE);
        return tm.getDeviceId();
        //return "fc0b1c7bee761ff8f10775e8992697fc6a1e857d94d357e0ef5d1facd0bb3400";
    }




    public static GPS getGPS(Activity activity){
        LocationManager locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                GPS.latitude = location.getLatitude();
                GPS.longitude = location.getLongitude();
            }
        }else{
            LocationListener locationListener = new LocationListener() {

                // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                // Provider被enable时触发此函数，比如GPS被打开
                @Override
                public void onProviderEnabled(String provider) {

                }

                // Provider被disable时触发此函数，比如GPS被关闭
                @Override
                public void onProviderDisabled(String provider) {

                }

                //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Log.e("Map", "Location changed : Lat: "
                                + location.getLatitude() + " Lng: "
                                + location.getLongitude());
                    }
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                GPS.latitude = location.getLatitude(); //经度
                GPS.longitude = location.getLongitude(); //纬度
            }
        }
        //返回一个实例即可，经纬度为静态变量，实例变量共享
        return new GPS();
    }


}
